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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import net.opengis.om.x20.OMObservationType;
import net.opengis.sos.x20.GetObservationResponseType.ObservationData;
import net.opengis.sosREST.x10.ObservationCollectionDocument;
import net.opengis.sosREST.x10.ObservationCollectionType;
import net.opengis.sosREST.x10.ObservationDocument;
import net.opengis.sosREST.x10.ObservationType;

import org.n52.sos.binding.rest.requests.RestResponse;
import org.n52.sos.exception.ows.concrete.NoEncoderForResponseException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.response.ServiceResponse;
import org.n52.sos.util.SosHelper;
import org.n52.sos.util.http.HTTPStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class ObservationsGetEncoder extends AObservationsEncoder {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ObservationsGetEncoder.class);
    
    @Override
    public ServiceResponse encodeRestResponse(RestResponse objectToEncode) throws OwsExceptionReport
    {
        if (objectToEncode != null) {
            if (objectToEncode instanceof ObservationsGetByIdResponse) {
                
                return encodeObservationsGetById(objectToEncode);
                
            } else if (objectToEncode instanceof ObservationsSearchResponse) {
                
                return encodeObservationsSearch(objectToEncode);
                
            }
            String exceptionText = String.format("No encoder implementation is available for RestBinding and namespace \"%s\" to encode Type \"%s\"!",
                    bindingConstants.getEncodingNamespace(),
                    (objectToEncode!=null?objectToEncode.getClass().getName():"null"));
            LOGGER.debug(exceptionText);
            throw new NoEncoderForResponseException().withMessage(exceptionText);
        }
        return null;
    }

    private ServiceResponse encodeObservationsSearch(RestResponse objectToEncode) throws OwsExceptionReport
    {
        ObservationsSearchResponse observationsSearchResponse = (ObservationsSearchResponse) objectToEncode;
        ServiceResponse response;
        if (observationsSearchResponse.getObservationDataArrayXB() == null) {
            response = createNoContentResponse(bindingConstants.getResourceObservations(),true,false);
        } else {
            ObservationCollectionDocument xb_ObservationCollectionDoc = ObservationCollectionDocument.Factory.newInstance();
            ObservationCollectionType xb_ObservationCollection = xb_ObservationCollectionDoc.addNewObservationCollection();
            ArrayList<ObservationType> xb_observationList = new ArrayList<ObservationType>();

            collectAndUpdateOMObservationFromSOSCore(observationsSearchResponse, xb_observationList);

            ObservationType[] xb_obsTypeArray = xb_observationList.toArray(new ObservationType[xb_observationList.size()]);
            xb_ObservationCollection.setObservationArray(xb_obsTypeArray);

            // rel:self
            setValuesOfLinkToDynamicResource(xb_ObservationCollection.addNewLink(),
                    observationsSearchResponse.getResourceIdentifier(),
                    bindingConstants.getResourceRelationSelf(),
                    bindingConstants.getResourceObservations());

            response = createServiceResponseFromXBDocument(
                    xb_ObservationCollectionDoc,
                    bindingConstants.getResourceObservations(),
                    HTTPStatus.OK, true, false);
        }
        return response;
    }

    private ServiceResponse encodeObservationsGetById(RestResponse restResponse) throws OwsExceptionReport
    {
        ObservationsGetByIdResponse observationsGetResponse = (ObservationsGetByIdResponse) restResponse;
        ObservationDocument xb_ObservationRestDoc = createRestObservationDocumentFrom( observationsGetResponse.getObservationXB() );
        
        return createServiceResponseFromXBDocument(
                xb_ObservationRestDoc,
                bindingConstants.getResourceObservations(),
                HTTPStatus.OK, false, false);
    }

    private void collectAndUpdateOMObservationFromSOSCore(ObservationsSearchResponse observationsSearchResponse,
            ArrayList<ObservationType> xb_observationList) throws OwsExceptionReport
    {
        Map<String,String> inDocumentReferenceToFeatureId = new HashMap<String, String>();
        for (ObservationData xb_obsData : observationsSearchResponse.getObservationDataArrayXB())
        {
            SosHelper.checkFreeMemory();
            OMObservationType xb_observation = xb_obsData.getOMObservation();
            ObservationType xb_restObservation = createRestObservationFromOMObservation(ObservationType.Factory.newInstance(),
                    xb_observation, inDocumentReferenceToFeatureId);
            xb_observationList.add(xb_restObservation);
        }
    }

}
