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

import java.io.IOException;

import net.opengis.swes.x20.UpdateSensorDescriptionResponseDocument;
import net.opengis.swes.x20.UpdateSensorDescriptionResponseType;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.binding.rest.requests.RestRequest;
import org.n52.sos.binding.rest.requests.RestResponse;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class SensorsPutRequestHandler extends SensorsRequestHandler {

    @Override
    public RestResponse handleRequest(RestRequest request) throws OwsExceptionReport, XmlException, IOException
    {
        if (request != null && request instanceof SensorsPutRequest) {
            // submit request to core
            SensorsPutRequest putRequest = (SensorsPutRequest)request;
            XmlObject xb_ServiceResponse = executeSosRequest(putRequest.getUpdateSensorRequest());
            
            if(xb_ServiceResponse instanceof UpdateSensorDescriptionResponseDocument) {
                UpdateSensorDescriptionResponseDocument xb_InsertSensorResponseDoc = (UpdateSensorDescriptionResponseDocument) xb_ServiceResponse;
                UpdateSensorDescriptionResponseType xb_InsertSensorResponse = xb_InsertSensorResponseDoc.getUpdateSensorDescriptionResponse();
                String procedureId = xb_InsertSensorResponse.getUpdatedProcedure();
                
                return new SensorsPutResponse(procedureId,putRequest.getXb_smlSystem());
            }
        }
        throw logRequestTypeNotSupportedByThisHandlerAndCreateException(request, getClass().getName());
    }

}
