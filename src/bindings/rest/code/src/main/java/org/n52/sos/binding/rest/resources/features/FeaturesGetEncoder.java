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
package org.n52.sos.binding.rest.resources.features;


import net.opengis.sosREST.x10.FeatureCollectionDocument;
import net.opengis.sosREST.x10.FeatureDocument;
import net.opengis.sosREST.x10.FeatureType;
import net.opengis.sosREST.x10.ResourceCollectionType;

import org.n52.sos.binding.rest.encode.ResourceEncoder;
import org.n52.sos.binding.rest.requests.RestResponse;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.response.ServiceResponse;
import org.n52.sos.util.http.HTTPStatus;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class FeaturesGetEncoder extends ResourceEncoder {
    
    @Override
    public ServiceResponse encodeRestResponse(RestResponse objectToEncode) throws OwsExceptionReport
    {
        if (objectToEncode != null) {
            
            if (objectToEncode instanceof FeatureByIdResponse) {
                return encodeFeatureByIdResponse((FeatureByIdResponse)objectToEncode);
                
            } else if (objectToEncode instanceof FeaturesResponse) {
                return encodeFeaturesResponse((FeaturesResponse)objectToEncode);
            }
            
        }
        return null;
    }

    private ServiceResponse encodeFeaturesResponse(FeaturesResponse featuresResponse) throws OwsExceptionReport
    {
        String[] featureIds = featuresResponse.getFeatureIds();
        // add feature links
        if (featureIds != null && featureIds.length > 0) {
            FeatureCollectionDocument xb_FeatureCollectionDoc = FeatureCollectionDocument.Factory.newInstance();
            ResourceCollectionType xb_FeatureCollection = xb_FeatureCollectionDoc.addNewFeatureCollection();
            
            for (String featureId : featureIds) {
                addFeatureLink(xb_FeatureCollection, featureId);
            }
            // add self link
            if (featuresResponse instanceof FeaturesSearchResponse) {
                // Case A: search -> link with query string
                setValuesOfLinkToDynamicResource(xb_FeatureCollection.addNewLink(),
                        ((FeaturesSearchResponse)featuresResponse).getQueryString(),
                        bindingConstants.getResourceRelationSelf(),
                        bindingConstants.getResourceFeatures());
            } else {
                // Case B: global resource
                setValuesOfLinkToGlobalResource(xb_FeatureCollection.addNewLink(),
                        bindingConstants.getResourceRelationSelf(),
                        bindingConstants.getResourceFeatures());
            }
            return createServiceResponseFromXBDocument(
                    xb_FeatureCollectionDoc,
                    bindingConstants.getResourceFeatures(),
                    HTTPStatus.OK, true, true);
        }
        else
        {
            return createNoContentResponse(bindingConstants.getResourceFeatures(),true,true);
        }
    }

    private void addFeatureLink(ResourceCollectionType xb_FeatureCollection,
            String featureId)
    {
        setValuesOfLinkToUniqueResource(xb_FeatureCollection.addNewLink(),
                featureId,
                bindingConstants.getResourceRelationFeatureGet(),
                bindingConstants.getResourceFeatures());
    }

    private ServiceResponse encodeFeatureByIdResponse(FeatureByIdResponse featureByIdResponse) throws OwsExceptionReport
    {
        FeatureDocument xb_feature = FeatureDocument.Factory.newInstance();
        FeatureType xb_RestFeature = xb_feature.addNewFeature();
        
        // add Feature from DeleteObservationResponse
        xb_RestFeature.set(featureByIdResponse.getAbstractFeature());
        
        // add selflink
        setValuesOfLinkToUniqueResource(xb_RestFeature.addNewLink(),
                featureByIdResponse.getFeatureResourceIdentifier(),
                bindingConstants.getResourceRelationSelf(),
                bindingConstants.getResourceFeatures());
        
        return createServiceResponseFromXBDocument(
                xb_feature, bindingConstants.getResourceFeatures(),
                HTTPStatus.OK, false, false);
    }

}
