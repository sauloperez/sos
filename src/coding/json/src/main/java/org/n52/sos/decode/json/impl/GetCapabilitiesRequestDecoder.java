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

import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.decode.JsonDecoderKey;
import org.n52.sos.decode.OperationDecoderKey;
import org.n52.sos.decode.json.AbstractSosRequestDecoder;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.Operations;
import org.n52.sos.request.GetCapabilitiesRequest;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.util.http.MediaTypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class GetCapabilitiesRequestDecoder extends AbstractSosRequestDecoder<GetCapabilitiesRequest> {
    private static final Operations OP = SosConstants.Operations.GetCapabilities;

    private static final MediaType MT = MediaTypes.APPLICATION_JSON;

    private static final String V2 = Sos2Constants.SERVICEVERSION;

    private static final String V1 = Sos1Constants.SERVICEVERSION;

    private static final String SOS = SosConstants.SOS;

    public GetCapabilitiesRequestDecoder() {
        super(Sets.<DecoderKey> newHashSet(new JsonDecoderKey(GetCapabilitiesRequest.class), new OperationDecoderKey(
                SOS, null, OP, MT), new OperationDecoderKey(SOS, V2, OP, MT),
                new OperationDecoderKey(null, V2, OP, MT), new OperationDecoderKey(null, null, OP, MT),
                new OperationDecoderKey(SOS, V1, OP, MT), new OperationDecoderKey(null, V1, OP, MT)));
    }

    @Override
    protected String getSchemaURI() {
        return SchemaConstants.Request.GET_CAPABILITIES;
    }

    @Override
    protected GetCapabilitiesRequest decodeRequest(JsonNode node) throws OwsExceptionReport {
        GetCapabilitiesRequest req = new GetCapabilitiesRequest();
        req.setAcceptFormats(parseStringOrStringList(node.path(JSONConstants.ACCEPT_FORMATS)));
        req.setAcceptVersions(parseStringOrStringList(node.path(JSONConstants.ACCEPT_VERSIONS)));
        req.setSections(parseStringOrStringList(node.path(JSONConstants.SECTIONS)));
        req.setUpdateSequence(node.path(JSONConstants.UPDATE_SEQUENCE).textValue());
        return req;
    }

}
