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
package org.n52.sos.decode.json.impl;

import java.util.List;

import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.decode.json.AbstractSosRequestDecoder;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.GetObservationByIdRequest;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class GetObservationByIdRequestDecoder extends AbstractSosRequestDecoder<GetObservationByIdRequest> {
    public GetObservationByIdRequestDecoder() {
        super(GetObservationByIdRequest.class, SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                SosConstants.Operations.GetObservationById);
    }

    @Override
    protected String getSchemaURI() {
        return SchemaConstants.Request.GET_OBSERVATION_BY_ID;
    }

    @Override
    protected GetObservationByIdRequest decodeRequest(JsonNode node) throws OwsExceptionReport {
        GetObservationByIdRequest req = new GetObservationByIdRequest();
        req.setObservationIdentifier(parseObservationIdentifiers(node));
        return req;
    }

    private List<String> parseObservationIdentifiers(JsonNode node) {
        return parseStringOrStringList(node.path(JSONConstants.OBSERVATION));
    }
}
