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

import net.opengis.sos.x20.InsertObservationResponseDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.binding.rest.requests.RequestHandler;
import org.n52.sos.binding.rest.requests.RestRequest;
import org.n52.sos.binding.rest.requests.RestResponse;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.InsertObservationRequest;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class ObservationsPostRequestHandler extends RequestHandler {
    
    @Override
    public RestResponse handleRequest(RestRequest req) throws OwsExceptionReport, XmlException
    {
        if (req != null && req instanceof ObservationsPostRequest) {
            InsertObservationRequest ioReq = ((ObservationsPostRequest) req).getInsertObservationRequest();
            
            // 2 handle core response
            XmlObject xb_InsertObservationResponse = executeSosRequest(ioReq);
            
            if (xb_InsertObservationResponse instanceof InsertObservationResponseDocument) {
                // 3 return response
                // no interesting content, just check the class to be sure that the insertion was successful
                // the restful response requires the link to the newly created observation
            	// FIXME we are always using only the first observation in the list without checking
                return new ObservationsPostResponse(
                        ioReq.getObservations().get(0).getIdentifier().getValue(),
                        ((ObservationsPostRequest) req).getXb_OMObservation());
            } 
        }
        throw logRequestTypeNotSupportedByThisHandlerAndCreateException(req,this.getClass().getName());
    }

}
