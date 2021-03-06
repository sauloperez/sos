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
package org.n52.sos.binding.rest.resources.capabilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.opengis.sos.x20.CapabilitiesDocument;
import net.opengis.sos.x20.CapabilitiesType;
import net.opengis.sos.x20.ObservationOfferingDocument;
import net.opengis.sos.x20.ObservationOfferingType;
import net.opengis.swes.x20.AbstractContentsType.Offering;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.binding.rest.requests.RequestHandler;
import org.n52.sos.binding.rest.requests.RestRequest;
import org.n52.sos.binding.rest.requests.RestResponse;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class CapabilitiesRequestHandler extends RequestHandler {

    @Override
    public RestResponse handleRequest(RestRequest request) throws OwsExceptionReport, XmlException, IOException
    {
        if (request instanceof CapabilitiesRequest) {
            List<String> offeringIdentifiers = null;
            CapabilitiesType xb_sosCapabilities = null;

            // 0 submit GetCapabilities DeleteObservationRequest
            XmlObject xb_getCapabilitiesResponse = executeSosRequest(((CapabilitiesRequest) request).getGetCapabilitiesRequest());
            
            // 1 get offerings
            if (xb_getCapabilitiesResponse instanceof CapabilitiesDocument) {
                CapabilitiesDocument xb_capaCapabilitiesDocument = (CapabilitiesDocument) xb_getCapabilitiesResponse;
                xb_sosCapabilities = xb_capaCapabilitiesDocument.getCapabilities();

                if(xb_sosCapabilities.isSetContents()) {

                    // 1.1 save offering identifier
                    Offering[] xb_offerings = xb_sosCapabilities.getContents().getContents().getOfferingArray();
                    offeringIdentifiers = new ArrayList<String>(xb_offerings.length);
                    for (Offering xb_offering : xb_offerings) {
                        ObservationOfferingType xb_observationOffering = ObservationOfferingDocument.Factory.parse(xb_offering.newInputStream()).getObservationOffering();
                        if (xb_observationOffering.isSetIdentifier()) {
                            offeringIdentifiers.add(xb_observationOffering.getIdentifier());
                        }
                    }
                    // 2 remove offerings from capabilities
                    XmlCursor c = xb_sosCapabilities.newCursor();
                    c.toLastChild();
                    c.removeXml();
                    c.dispose();
                }
            }

            // 3 create result
            return new CapabilitiesGetResponse(offeringIdentifiers,xb_sosCapabilities);

        }
        throw logRequestTypeNotSupportedByThisHandlerAndCreateException(request,this.getClass().getName());
    }
}
