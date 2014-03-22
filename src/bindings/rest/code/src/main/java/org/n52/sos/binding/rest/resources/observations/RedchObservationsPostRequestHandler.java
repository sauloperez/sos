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
import java.math.BigDecimal;
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
import com.redch.Action;
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
			// Build sample from an O&M observation
			OmObservation observation = ioReq.getObservations().get(0);
			Sample sample = buildSample(observation);
			
			LOGGER.debug("Sample Built. Sensor Id: {}, Observation Id: {}",
						 sample.getSensorId(), sample.getId());
			
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
			LOGGER.debug("The observation could not be published to the queue. " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private String getCoordFromFoI(OmObservation observation) throws Exception {
		// Send a FeatureByIdRequest to get the Feature of Interest position (gml:pos xml node)
		String foiId = observation.getObservationConstellation().getFeatureOfInterest().getIdentifier().getValue();
		
		List<String> featureIDs = new ArrayList<String>(1);
	    featureIDs.add(foiId);
	    
		GetFeatureOfInterestRequest foiReq = new GetFeatureOfInterestRequest();
		foiReq.setFeatureIdentifiers(featureIDs);
		foiReq.setService("SOS");
		foiReq.setVersion("2.0.0");
		
		// Retrieve the Feature of Interest geometry and get its coordinate
		try {
			FeaturesRequestHandler handler = new FeaturesRequestHandler();
			FeatureByIdRequest featureByIdReq = new FeatureByIdRequest(foiReq, null);
			FeatureByIdResponse resp = (FeatureByIdResponse) handler.handleRequest((RestRequest) featureByIdReq);
			
			return RedchObservationsHelpers.getGmlPoint(resp.getAbstractFeature().xmlText());
			
		} catch (Exception e) {
			LOGGER.debug("Coordinates retrieval from Feature of Interest geometry failed");
			throw e;
		}
	}
	
	private Sample buildSample(OmObservation observation) throws Exception {
		String obsId = observation.getIdentifier().getValue();
		String sensorId = observation.getObservationConstellation().getProcedure().getIdentifier();
		
		if (obsId == "" || obsId == null) {
			throw new Exception("Sample observation Id can't be empty");
		}
		
		if (sensorId == "" || sensorId == null) {
			throw new Exception("Sample sensor Id can't be empty");
		}
		
		Sample sample = new Sample();
		sample.setId(obsId);
		sample.setValue(observation.getValue().getValue().getValue());
		sample.setSensorId(sensorId);
		sample.setResultTime(observation.getResultTime().getValue().toString());
		
		
		sample.setAction(Action.DELETE);
		BigDecimal value = (BigDecimal) sample.getValue();
		if (value.compareTo(BigDecimal.ZERO) > 0) {
			sample.setAction(Action.ADD);
		}
		
		String coord = getCoordFromFoI(observation);
		sample.setCoord(RedchObservationsHelpers.coordToArray(coord));
		
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
