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
package org.n52.sos.binding.rest.resources.sensors;


import net.opengis.sosREST.x10.ResourceCollectionType;
import net.opengis.sosREST.x10.SensorCollectionDocument;
import net.opengis.sosREST.x10.SensorDocument;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.binding.rest.requests.RestResponse;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.response.ServiceResponse;
import org.n52.sos.util.http.HTTPStatus;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class SensorsGetEncoder extends ASensorsEncoder {

    public ServiceResponse encodeRestResponse(RestResponse restResponse) throws OwsExceptionReport
    {
        if (restResponse != null)
        {
            if(restResponse instanceof GetSensorByIdResponse)
            {
                return encodeGetSensorByIdRequest((GetSensorByIdResponse)restResponse); 
            } 
            else if (restResponse instanceof SensorsGetResponse)
            {
                return encodeGetSensorsResponse((SensorsGetResponse)restResponse);
            }
        } 
        throw createResponseNotSupportedException(
                GetSensorByIdResponse.class.getName().concat(" or ").concat(SensorsGetResponse.class.getName()),
                restResponse);
    }

    private ServiceResponse encodeGetSensorsResponse(SensorsGetResponse restResponse) throws OwsExceptionReport
    {
        SensorCollectionDocument xb_SensorsDoc = SensorCollectionDocument.Factory.newInstance();
        ResourceCollectionType xb_Sensors = xb_SensorsDoc.addNewSensorCollection();
        
        // 0 add self link
        setValuesOfLinkToGlobalResource(xb_Sensors.addNewLink(),
                bindingConstants.getResourceRelationSelf(),
                bindingConstants.getResourceSensors());
        
        // 1 add sensor links
        for (String sensorId : restResponse.getSensorIds()) {
            setValuesOfLinkToUniqueResource(xb_Sensors.addNewLink(),
                    sensorId,
                    bindingConstants.getResourceRelationSensorGet(),
                    bindingConstants.getResourceSensors());
        }
        
        return createSensorResponseWithStatusOk(xb_SensorsDoc,true,true);
    }

    private ServiceResponse encodeGetSensorByIdRequest(GetSensorByIdResponse sensorsGetResponse) throws OwsExceptionReport
    {
        SensorDocument xb_SensorRestDoc = SensorDocument.Factory.newInstance();
        createRestDefaultRestSensor(sensorsGetResponse, xb_SensorRestDoc);
        return createSensorResponseWithStatusOk(xb_SensorRestDoc,false,false);
    }

    private ServiceResponse createSensorResponseWithStatusOk(XmlObject xb_SensorsDoc, boolean isResourceCollection, boolean isGlobalResource) throws OwsExceptionReport
    {
        return createServiceResponseFromXBDocument(
                xb_SensorsDoc,
                bindingConstants.getResourceSensors(),
                HTTPStatus.OK,
                isResourceCollection,isGlobalResource);
    }

}
