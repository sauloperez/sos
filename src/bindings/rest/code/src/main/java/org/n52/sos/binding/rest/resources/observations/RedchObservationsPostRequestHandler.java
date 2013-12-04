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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.xmlbeans.XmlException;
import org.n52.sos.binding.rest.requests.RestRequest;
import org.n52.sos.binding.rest.requests.RestResponse;
import org.n52.sos.binding.rest.resources.features.FeatureByIdRequest;
import org.n52.sos.binding.rest.resources.features.FeatureByIdResponse;
import org.n52.sos.binding.rest.resources.features.FeaturesRequestHandler;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.GetFeatureOfInterestRequest;
import org.n52.sos.request.InsertObservationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.redch.AMQPService;
import com.redch.AMQPServiceImpl;
import com.redch.Sample;
import com.redch.exception.AMQPServiceException;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class RedchObservationsPostRequestHandler extends ObservationsPostRequestHandler {
    
	private static final Logger LOGGER = LoggerFactory.getLogger(RedchObservationsPostRequestHandler.class);

	private static final String PROPERTIES_FILE = "redch.properties";
    private static final String HOST = "redch.amqp.host";
    private static final String EXCHANGE = "redch.amqp.exchange";
    private static final String USER_HOME = "user.home";
    
    private Map<?, ?> properties = new Properties();

	@Override
    public RestResponse handleRequest(RestRequest req) throws OwsExceptionReport, XmlException
    {
    	RestResponse response = super.handleRequest(req);
    	InsertObservationRequest ioReq = ((ObservationsPostRequest) req).getInsertObservationRequest();
    	
    	try {
    		// Read all properties for further use
    		// Note that the properties file must be stored within user's home directory
    		String dir = System.getProperty(USER_HOME);
    		File propsFile = new File(dir, PROPERTIES_FILE);
			properties = RedchObservationsHelpers.propertiesToMap(propsFile);
			
			publish(ioReq);
		} catch (IOException e) {
			LOGGER.debug("AMQP properties retrieval failed");
			e.printStackTrace();
		}
    	
    	return response;
    }

	private void publish(InsertObservationRequest ioReq) {
		try {
			LOGGER.debug("Start handling of queue publication. Sensor Id: {}", 
					ioReq.getAssignedSensorId());
			
			OmObservation observation = ioReq.getObservations().get(0);
			Sample sample = buildSample(observation);
			
			LOGGER.debug("Sample built from Observation. Observation Id: {}",
					observation.getIdentifier().getValue());
			
			// Json encoding
			Gson gson = new Gson();
			String jsonMsg = gson.toJson(sample);
			
			LOGGER.debug("JSON message encoded");
			
			// Queue publication
			String host = (String) properties.get(HOST);
			String exchange = (String) properties.get(EXCHANGE);
			publishToQueue(host, exchange, jsonMsg);
			
			LOGGER.debug("Message published to queue. Host: {}, Exchange: {}", host, exchange);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Sample buildSample(OmObservation observation) throws Exception {
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
			
			String coord = RedchObservationsHelpers.getGmlPoint(resp.getAbstractFeature().xmlText());
			sample.setCoord(RedchObservationsHelpers.coordToArray(coord));
			
		} catch (Exception e) {
			LOGGER.debug("Coordinates retrieval from Feature of Interest geometry failed");
			throw e;
		}
		return sample;
	}

	private void publishToQueue(String host, String exchangeName, String msg) throws IOException {
		try {
			AMQPService amqpService = new AMQPServiceImpl(host, exchangeName);
			amqpService.publish(msg);
		} catch(IOException e) {
			LOGGER.debug("AMQP properties retrieval failed");
			throw e;
		} catch (AMQPServiceException e) {
			e.printStackTrace();
		}
	}

}
