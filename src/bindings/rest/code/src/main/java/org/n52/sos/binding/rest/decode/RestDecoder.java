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
package org.n52.sos.binding.rest.decode;

import static org.n52.sos.util.CodingHelper.decoderKeysForElements;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.n52.sos.binding.rest.Constants;
import org.n52.sos.binding.rest.requests.RestRequest;
import org.n52.sos.binding.rest.resources.ServiceEndpointDecoder;
import org.n52.sos.binding.rest.resources.capabilities.CapabilitiesDecoder;
import org.n52.sos.binding.rest.resources.features.FeaturesDecoder;
import org.n52.sos.binding.rest.resources.observations.ObservationsDecoder;
import org.n52.sos.binding.rest.resources.offerings.OfferingsDecoder;
import org.n52.sos.binding.rest.resources.sensors.SensorsDecoder;
import org.n52.sos.decode.Decoder;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.exception.HTTPException;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.OperationNotSupportedException;
import org.n52.sos.exception.ows.concrete.ContentTypeNotSupportedException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.http.HTTPHeaders;
import org.n52.sos.util.http.HTTPStatus;
import org.n52.sos.util.http.HTTPUtils;
import org.n52.sos.util.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 *
 */
public class RestDecoder implements Decoder<RestRequest, HttpServletRequest> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RestDecoder.class);

    @SuppressWarnings("unchecked")
	private final Set<DecoderKey> DECODER_KEYS = decoderKeysForElements(
            Constants.getInstance().getEncodingNamespace(), HttpServletRequest.class);
//            union(
//    		decoderKeysForElements(Constants.getInstance().getEncodingNamespace(), HttpServletRequest.class),
//    		CodingHelper.xmlDecoderKeysForOperation(SOS, Sos2Constants.SERVICEVERSION,
//    				Sos2Constants.Operations.DeleteSensor,
//    				Sos2Constants.Operations.InsertSensor,
//    				Sos2Constants.Operations.UpdateSensorDescription,
//    				SosConstants.Operations.DescribeSensor,
//    				SosConstants.Operations.GetCapabilities,
//    				SosConstants.Operations.GetFeatureOfInterest,
//    				SosConstants.Operations.GetObservation,
//    				SosConstants.Operations.GetObservationById,
//    				DeleteObservationConstants.Operations.DeleteObservation));

    /**
     * constructor called by the service loader of the SOS instance
     */
    public RestDecoder() {
    	LOGGER.info("Decoder for the following keys initialized successfully: {}!",
                    Joiner.on(", ").join(DECODER_KEYS));
    }
    
    @Override
    public RestRequest decode(final HttpServletRequest httpRequest)
            throws OwsExceptionReport{
        
        // check requested content type
        if (!isAcceptHeaderOk(httpRequest))
        {
            throw new ContentTypeNotSupportedException(
            		httpRequest.getContentType(), 
            		bindingConstants().getContentTypeDefault().toString());
        }

        // get decoder for method
        final ResourceDecoder decoder = getDecoderForResource(getResourceTypeFromPathInfoWithWorkingUrl(httpRequest.getPathInfo()));
        LOGGER.debug("Decoder found {}", decoder.getClass().getName());

        return decoder.decodeRestRequest(httpRequest);
    }

    private boolean isAcceptHeaderOk(final HttpServletRequest httpRequest) throws OwsExceptionReport {
        List<MediaType> request;
        try {
            request = HTTPUtils.getAcceptHeader(httpRequest);
        } catch (HTTPException e) {
            throw new InvalidParameterValueException().causedBy(e)
                    .at(HTTPHeaders.ACCEPT)
                    .withMessage("Invalid Accept Header: %s", httpRequest
                    .getHeader(HTTPHeaders.ACCEPT))
                    .setStatus(HTTPStatus.BAD_REQUEST);
        }
        for (MediaType mt : request) {
            if (mt.isCompatible(bindingConstants().getContentTypeDefault())) {
                return true;
            }
        }
        return false;
    }
    
    protected String getResourceTypeFromPathInfoWithWorkingUrl(String pathInfo)
    {
        /*
         * http:// workaround - Tomcat servlet container removes one "/",
         * if HttpRequest.getPathInfo() contains a second "http://"
         */
        if (pathInfo != null) {
            pathInfo = pathInfo.replaceAll("http:/", "http://");
            // use part from second slash "/" till end
            final int indexOfPotentialSecondSlash = pathInfo.indexOf("/", 1);
            
            if (indexOfPotentialSecondSlash > 1) {
                return pathInfo.substring(indexOfPotentialSecondSlash + 1);
            } else {
                return pathInfo.substring(1);
            }
        }
        return pathInfo;
    }

    private ResourceDecoder getDecoderForResource(
            final String httpRequestPathInfo) throws OwsExceptionReport {
        if (isSensorsRequest(httpRequestPathInfo)) {
            return new SensorsDecoder();
        } else if (isObservationsRequest(httpRequestPathInfo)) {
            return new ObservationsDecoder();
        } else if (isCapabilitiesRequest(httpRequestPathInfo)) {
            return new CapabilitiesDecoder();
        } else if (isOfferingsRequest(httpRequestPathInfo)) {
            return new OfferingsDecoder();
        } else if (isFeaturesRequest(httpRequestPathInfo)) {
            return new FeaturesDecoder();
        } else if (isServiceDefaultEndpoint(httpRequestPathInfo)) {
            return new ServiceEndpointDecoder();
        }
        final String exceptionText = String
                .format("Requested resource type \"%s\" is not supported by this decoder \"%s\"!",
                        httpRequestPathInfo,
                        this.getClass().getName());
        LOGGER.debug(exceptionText);
        throw new OperationNotSupportedException(httpRequestPathInfo);
    }

    private boolean isServiceDefaultEndpoint(final String pathInfo) {
        return ((pathInfo != null) && pathInfo.isEmpty()) || ("/" + pathInfo)
                .startsWith(Constants.getInstance().getUrlPattern());
    }

    private boolean isOfferingsRequest(final String pathInfo) {
        return (pathInfo != null) && pathInfo.startsWith(bindingConstants()
                .getResourceOfferings());
    }

    private boolean isFeaturesRequest(final String pathInfo) {
        return (pathInfo != null) && pathInfo.startsWith(bindingConstants()
                .getResourceFeatures());
    }

    private boolean isCapabilitiesRequest(final String pathInfo) {
        return (pathInfo != null) && pathInfo.startsWith(bindingConstants()
                .getResourceCapabilities());
    }

    private boolean isObservationsRequest(final String pathInfo) {
        return (pathInfo != null) && pathInfo.startsWith(bindingConstants()
                .getResourceObservations());
    }

    private boolean isSensorsRequest(final String pathInfo) {
        return (pathInfo != null) && pathInfo.startsWith(bindingConstants()
                .getResourceSensors());
    }

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.emptyMap();
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    private Constants bindingConstants() {
        return Constants.getInstance();
    }

}
