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

import net.opengis.om.x20.OMObservationType;
import net.opengis.sos.x20.GetObservationByIdResponseDocument;
import net.opengis.sos.x20.GetObservationByIdResponseType.Observation;
import net.opengis.sos.x20.GetObservationResponseDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.binding.rest.requests.RequestHandler;
import org.n52.sos.binding.rest.requests.ResourceNotFoundResponse;
import org.n52.sos.binding.rest.requests.RestRequest;
import org.n52.sos.binding.rest.requests.RestResponse;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class ObservationsGetRequestHandler extends RequestHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObservationsGetRequestHandler.class);

    @Override
    public RestResponse handleRequest(RestRequest observationsHttpGetRequest) throws OwsExceptionReport, XmlException, IOException
    {
        if (observationsHttpGetRequest != null) {

            if (observationsHttpGetRequest instanceof ObservationsGetRequest) {
                // Case A: with ID
                return handleObservationsGetRequest((ObservationsGetRequest)observationsHttpGetRequest);

            } else if (observationsHttpGetRequest instanceof ObservationsSearchRequest) {
                // Case B: search observations
                return handleObservationsSearchRequest((ObservationsSearchRequest)observationsHttpGetRequest);
            } /*else if (observationsHttpGetRequest instanceof ObservationsFeedRequest) {
                // Case C: Atom Feed
                return handleObservationsFeedRequest((ObservationsFeedRequest)observationsHttpGetRequest);
            }*/
            
        }
        throw logRequestTypeNotSupportedByThisHandlerAndCreateException(observationsHttpGetRequest,this.getClass().getName());
    }

    private RestResponse handleObservationsGetRequest(ObservationsGetRequest req) throws OwsExceptionReport, XmlException, IOException
    {
        String procedureId = null;
        OMObservationType xb_observation = null;

        // 0 submit GetObservationById (if response is an OWSException report -> cancel whole process and throw it)
        XmlObject xb_getObservationByIdResponse = executeSosRequest(req.getGetObservationByIdRequest());

        if (xb_getObservationByIdResponse instanceof GetObservationByIdResponseDocument) {
            // 1 Get Offering from Capabilities
            GetObservationByIdResponseDocument xb_ByIdResponseDocument = (GetObservationByIdResponseDocument) xb_getObservationByIdResponse;
            Observation[] xb_observations = xb_ByIdResponseDocument.getGetObservationByIdResponse().getObservationArray();

            // 1.1 Get Procedure Id from GetObservationByIdResponse
            if (xb_observations.length > 0){ // TODO should be one
                if (xb_observations[0].getOMObservation() != null){

                    xb_observation = xb_observations[0].getOMObservation();

                    if (xb_observation.getProcedure() != null &&
                            xb_observation.getProcedure().isSetHref()) {
                        procedureId = xb_observations[0].getOMObservation().getProcedure().getHref();
                    }
                }
            }
            
            LOGGER.debug("xb_observation == null? {}; procedureId? {}",xb_observation==null,procedureId);
            
            if (xb_observation == null)
            {
                return new ResourceNotFoundResponse(bindingConstants.getResourceObservations(),
                        req.getGetObservationByIdRequest().getObservationIdentifier().get(0)); // TODO NPE handling?
            }

            // 2 collect results
            return new ObservationsGetByIdResponse( xb_observation );

        } else {
            String exceptionText = String.format("Processing of SOS core operation \"GetObservationById\" response failed. Type of could not be handled: \"%s\"",
                    xb_getObservationByIdResponse.getClass().getName());
            LOGGER.debug(exceptionText);
            throw new NoApplicableCodeException().withMessage(exceptionText);
        }
    }

    private RestResponse handleObservationsSearchRequest(ObservationsSearchRequest req) throws OwsExceptionReport, XmlException
    {
        // 0 submit request to core
        XmlObject xb_getObservationResponse = executeSosRequest(req.getGetObservationRequest());
        if (xb_getObservationResponse instanceof GetObservationResponseDocument) {
            GetObservationResponseDocument xb_getObservationResponseDoc = (GetObservationResponseDocument) xb_getObservationResponse;
            
            ObservationsSearchResponse response;
            
            if (xb_getObservationResponseDoc.getGetObservationResponse().getObservationDataArray() != null &&
                    xb_getObservationResponseDoc.getGetObservationResponse().getObservationDataArray().length > 0){

                response = new ObservationsSearchResponse(xb_getObservationResponseDoc.getGetObservationResponse().getObservationDataArray(),
                        req.getQueryString());

            } else {
                response = new ObservationsSearchResponse(null, req.getQueryString());
            }
            
            return response;
        }
        throw createHandlingOfSosCoreResponseFailedException(xb_getObservationResponse,
                GetObservationResponseDocument.class.getName());
    }

    private OwsExceptionReport createHandlingOfSosCoreResponseFailedException(XmlObject xb_getObservationResponse,
            String nameOfExpectedType)
    {
        return new NoApplicableCodeException().withMessage("Handling of internal response failed. Expected '%s' and received '%s'.",
                nameOfExpectedType,
                xb_getObservationResponse.getClass().getName());
    }

}
