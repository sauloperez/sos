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

import net.opengis.sosdo.x10.DeleteObservationResponseDocument;
import net.opengis.sosdo.x10.DeleteObservationResponseType;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.binding.rest.requests.RequestHandler;
import org.n52.sos.binding.rest.requests.RestRequest;
import org.n52.sos.binding.rest.requests.RestResponse;
import org.n52.sos.ext.deleteobservation.DeleteObservationRequest;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class ObservationsDeleteRequestHandler extends RequestHandler {

    @Override
    public RestResponse handleRequest(RestRequest req) throws OwsExceptionReport, XmlException, IOException
    {
        if (req instanceof ObservationsDeleteRequest) {
            DeleteObservationRequest doReq = ((ObservationsDeleteRequest) req).getDeleteObservationRequest();
            XmlObject xb_deleteObservationResponse = executeSosRequest(doReq);
            if (xb_deleteObservationResponse instanceof DeleteObservationResponseDocument) {
                DeleteObservationResponseType xb_delObsResponse = ((DeleteObservationResponseDocument) xb_deleteObservationResponse).getDeleteObservationResponse();
                if (xb_delObsResponse.getDeletedObservation().equalsIgnoreCase(doReq.getObservationIdentifier())) {
                    return new ObservationsDeleteRespone(xb_delObsResponse.getDeletedObservation());
                }
            }
        }
        throw logRequestTypeNotSupportedByThisHandlerAndCreateException(req,this.getClass().getName());
    }

}
