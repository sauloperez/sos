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


import net.opengis.sosREST.x10.SensorDocument;

import org.n52.sos.binding.rest.requests.RestResponse;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.response.ServiceResponse;
import org.n52.sos.util.http.HTTPStatus;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class SensorsPostEncoder extends ASensorsEncoder {
    
    @Override
    public ServiceResponse encodeRestResponse(RestResponse restResponse) throws OwsExceptionReport
    {
        if (restResponse != null && restResponse instanceof SensorsPostResponse) {
            SensorsPostResponse sensorsPostResponse = (SensorsPostResponse) restResponse;
            SensorDocument xb_SensorRestDoc = SensorDocument.Factory.newInstance();
            createRestDefaultRestSensor(sensorsPostResponse, xb_SensorRestDoc);

            ServiceResponse response = createServiceResponseFromXBDocument(
                    xb_SensorRestDoc,
                    bindingConstants.getResourceSensors(),
                    HTTPStatus.CREATED,
                    false, true);
            
            addLocationHeader(response, sensorsPostResponse.getProcedureIdentifier(), bindingConstants.getResourceSensors());
            return response;
        }
        throw createResponseNotSupportedException(SensorsPostResponse.class.getName(),restResponse);
    }

}
