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
package org.n52.sos.binding.rest.encode;

import static java.util.Collections.emptySet;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.n52.sos.binding.rest.Constants;
import org.n52.sos.binding.rest.requests.ResourceNotFoundResponse;
import org.n52.sos.binding.rest.requests.RestResponse;
import org.n52.sos.binding.rest.resources.OptionsRestEncoder;
import org.n52.sos.binding.rest.resources.OptionsRestResponse;
import org.n52.sos.binding.rest.resources.ServiceEndpointEncoder;
import org.n52.sos.binding.rest.resources.ServiceEndpointResponse;
import org.n52.sos.binding.rest.resources.capabilities.CapabilitiesGetEncoder;
import org.n52.sos.binding.rest.resources.capabilities.CapabilitiesGetResponse;
import org.n52.sos.binding.rest.resources.features.FeatureByIdResponse;
import org.n52.sos.binding.rest.resources.features.FeaturesGetEncoder;
import org.n52.sos.binding.rest.resources.features.FeaturesResponse;
import org.n52.sos.binding.rest.resources.observations.ObservationsDeleteEncoder;
import org.n52.sos.binding.rest.resources.observations.ObservationsDeleteRespone;
import org.n52.sos.binding.rest.resources.observations.ObservationsGetByIdResponse;
import org.n52.sos.binding.rest.resources.observations.ObservationsGetEncoder;
import org.n52.sos.binding.rest.resources.observations.ObservationsPostEncoder;
import org.n52.sos.binding.rest.resources.observations.ObservationsPostResponse;
import org.n52.sos.binding.rest.resources.observations.ObservationsSearchResponse;
import org.n52.sos.binding.rest.resources.offerings.OfferingByIdResponse;
import org.n52.sos.binding.rest.resources.offerings.OfferingsGetEncoder;
import org.n52.sos.binding.rest.resources.offerings.OfferingsResponse;
import org.n52.sos.binding.rest.resources.sensors.GetSensorByIdResponse;
import org.n52.sos.binding.rest.resources.sensors.SensorsGetEncoder;
import org.n52.sos.binding.rest.resources.sensors.SensorsGetResponse;
import org.n52.sos.binding.rest.resources.sensors.SensorsPostEncoder;
import org.n52.sos.binding.rest.resources.sensors.SensorsPostResponse;
import org.n52.sos.binding.rest.resources.sensors.SensorsPutEncoder;
import org.n52.sos.binding.rest.resources.sensors.SensorsPutResponse;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.exception.ows.concrete.NoEncoderForResponseException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.response.ServiceResponse;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class RestEncoder implements Encoder<ServiceResponse, RestResponse> {
    
    protected Constants bindingConstants = Constants.getInstance();
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RestEncoder.class);
    
    private final Set<EncoderKey> ENCODER_KEYS = CodingHelper.encoderKeysForElements(bindingConstants.getEncodingNamespace(),
    		RestResponse.class);

    public RestEncoder() {
    	LOGGER.info("Encoder for the following keys initialized successfully: {}!", Joiner.on(", ").join(ENCODER_KEYS));
    }
    
    @Override
    public ServiceResponse encode(final RestResponse restResponse)
            throws OwsExceptionReport{

        // 0 variables
        ServiceResponse encodedResponse = null;

        // 1 get decoder for response
        final ResourceEncoder encoder = getRestEncoderForBindingResponse(restResponse);
        LOGGER.debug("RestEncoder found for RestResponse {}: {}", restResponse.getClass().getName(), encoder.getClass().getName());
        // 2 decode
        encodedResponse = encoder.encodeRestResponse(restResponse);

        // 3 return the results
        return encodedResponse;
    }
    
    private ResourceEncoder getRestEncoderForBindingResponse(final RestResponse restResponse) throws OwsExceptionReport
    {
        if (restResponse != null) {
            if (isSensorsGetResponse(restResponse))
            {
                return new SensorsGetEncoder();
            }
            else if (isObservationsGetResponse(restResponse)) 
            {
                return new ObservationsGetEncoder();
            }
            else if (restResponse instanceof CapabilitiesGetResponse) 
            {
                return new CapabilitiesGetEncoder();
            } 
            else if (restResponse instanceof ObservationsPostResponse) 
            {
                return new ObservationsPostEncoder();
            } 
            else if (restResponse instanceof SensorsPostResponse)
            {
                return new SensorsPostEncoder();
            } 
            else if (restResponse instanceof SensorsPutResponse) 
            {
                return new SensorsPutEncoder();
            } 
            else if (isOfferingsGetResponse(restResponse))
            {
                return new OfferingsGetEncoder();
            } 
            else if (isFeatureResponse(restResponse))
            {
                return new FeaturesGetEncoder();
            } 
            else if (restResponse instanceof ObservationsDeleteRespone)
            {
                return new ObservationsDeleteEncoder();
            }
            else if (restResponse instanceof OptionsRestResponse)
            {
                return new OptionsRestEncoder();
            }
            else if (restResponse instanceof ResourceNotFoundResponse)
            {
                return new GenericRestEncoder();
            }
            else if (restResponse instanceof ServiceEndpointResponse)
            {
                return new ServiceEndpointEncoder();
            }
        }
        final String exceptionText = String.format("No encoder is available for response type '%s' by this encoder '%s'!",
                restResponse!=null?restResponse.getClass().getName():"null",
                        this.getClass().getName());
        LOGGER.debug(exceptionText);
        throw new NoEncoderForResponseException().withMessage(exceptionText);
    }

    protected boolean isFeatureResponse(final RestResponse restResponse)
    {
        return restResponse instanceof FeatureByIdResponse || restResponse instanceof FeaturesResponse;
    }

    private boolean isObservationsGetResponse(final RestResponse restResponse)
    {
        return restResponse instanceof ObservationsGetByIdResponse || restResponse instanceof ObservationsSearchResponse;
    }

    private boolean isSensorsGetResponse(final RestResponse restResponse)
    {
        return restResponse instanceof GetSensorByIdResponse || restResponse instanceof SensorsGetResponse;
    }

    private boolean isOfferingsGetResponse(final RestResponse restResponse)
    {
        return restResponse instanceof OfferingsResponse || restResponse instanceof OfferingByIdResponse;
    }

    @Override
	public ServiceResponse encode(final RestResponse objectToEncode, final Map<HelperValues, String> iGNOREDadditionalValues) throws OwsExceptionReport
    {
        return encode(objectToEncode);
    }
    
    @Override
    public Set<String> getConformanceClasses() {
        return emptySet();
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS) ;
    }
    
    @Override
	public void addNamespacePrefixToMap(final Map<String, String> nameSpacePrefixMap){
    	if (nameSpacePrefixMap != null)
    	{
    		nameSpacePrefixMap.put(bindingConstants.getEncodingNamespace(), bindingConstants.getEncodingPrefix());
    	}
    }
    
    @Override
	public Map<SupportedTypeKey, Set<String>> getSupportedTypes() { 
    	return Collections.emptyMap();
    }
    
    @Override
	public MediaType getContentType()
    {
        return Constants.getInstance().getContentTypeDefault();
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(
        		new SchemaLocation(
        				bindingConstants.getEncodingNamespace(),
        				bindingConstants.getEncodingSchemaUrl().toString()));
    }
    
}
