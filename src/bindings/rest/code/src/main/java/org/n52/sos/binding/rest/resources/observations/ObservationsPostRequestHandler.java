/**
 * Copyright (C) 2013
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.sos.binding.rest.resources.observations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.opengis.sos.x20.InsertObservationResponseDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.binding.rest.requests.RequestHandler;
import org.n52.sos.binding.rest.requests.RestRequest;
import org.n52.sos.binding.rest.requests.RestResponse;
import org.n52.sos.binding.rest.resources.features.FeatureByIdRequest;
import org.n52.sos.binding.rest.resources.features.FeatureByIdResponse;
import org.n52.sos.binding.rest.resources.features.FeaturesDecoder;
import org.n52.sos.binding.rest.resources.features.FeaturesRequestHandler;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.GetFeatureOfInterestRequest;
import org.n52.sos.request.InsertObservationRequest;
import org.n52.sos.response.GetFeatureOfInterestResponse;

import com.google.gson.Gson;
import com.redch.AMQPService;
import com.redch.AMQPServiceImpl;
import com.redch.Sample;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class ObservationsPostRequestHandler extends RequestHandler {
    
    @Override
    public RestResponse handleRequest(RestRequest req) throws OwsExceptionReport, XmlException
    {
        if (req != null && req instanceof ObservationsPostRequest) {
            InsertObservationRequest ioReq = ((ObservationsPostRequest) req).getInsertObservationRequest();
            
            // 2 handle core response
            XmlObject xb_InsertObservationResponse = executeSosRequest(ioReq);
            
            publish(ioReq);
            
            if (xb_InsertObservationResponse instanceof InsertObservationResponseDocument) {
                // 3 return response
                // no interesting content, just check the class to be sure that the insertion was successful
                // the restful response requires the link to the newly created observation
            	// FIXME we are always using only the first observation in the list without checking
                return new ObservationsPostResponse(
                        ioReq.getObservations().get(0).getIdentifier().getValue(),
                        ((ObservationsPostRequest) req).getXb_OMObservation());
            } 
        }
        throw logRequestTypeNotSupportedByThisHandlerAndCreateException(req,this.getClass().getName());
    }

	private void publish(InsertObservationRequest ioReq) {
		OmObservation observation = ioReq.getObservations().get(0);
		
		Sample sample = new Sample();
		sample.setId(observation.getIdentifier().getValue());
		sample.setValue(observation.getValue().getValue().getValue());
		sample.setSensorId(observation.getObservationConstellation().getProcedure().getIdentifier());
		sample.setResultTime(observation.getResultTime().getValue().toString());
		
		// Send a FeatureByIdRequest to get the Feature of Interest position (gml:pos xml node)
		String foiId = observation.getObservationConstellation().getFeatureOfInterest().getIdentifier().getValue();
		
		List<String> featureIDs = new ArrayList<String>(1);
        featureIDs.add(foiId);
        
		GetFeatureOfInterestRequest foiReq = new GetFeatureOfInterestRequest();
		foiReq.setFeatureIdentifiers(featureIDs);
		foiReq.setService("SOS");
		foiReq.setVersion("2.0.0");
		
		// Retrieve the Feature of Interest geometry and store its coordinate
		try {
			FeatureByIdRequest featureByIdReq = new FeatureByIdRequest(foiReq, null);
			FeaturesRequestHandler handler = new FeaturesRequestHandler();
			FeatureByIdResponse resp = (FeatureByIdResponse) handler.handleRequest((RestRequest) featureByIdReq);
			
			String coord = getGmlPoint(resp.getAbstractFeature().xmlText());
			sample.setCoord(coordToArray(coord));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Json encoding and delivery
		Gson gson = new Gson();
		String jsonMsg = gson.toJson(sample);
		
		publishToQueue(jsonMsg, "samples");
	}

	private float[] coordToArray(String coord) {
		String[] s = coord.split(" ");
		float[] array = { Float.parseFloat(s[0]), Float.parseFloat(s[1]) };
		return array;
	}

	private String getGmlPoint(String xmlText) {
		String gmlPosPattern = "<gml:pos.*>(.+?)</gml:pos>";
		Pattern pattern = Pattern.compile(gmlPosPattern);
		Matcher matcher = pattern.matcher(xmlText);
		matcher.find();
		return matcher.group(1);
	}

	private void publishToQueue(String msg, String exchangeName) {
		try {
			AMQPService amqpService = new AMQPServiceImpl(exchangeName);
			amqpService.publish(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
