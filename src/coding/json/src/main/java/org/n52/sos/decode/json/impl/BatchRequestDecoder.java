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

import org.n52.sos.coding.CodingRepository;
import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.decode.Decoder;
import org.n52.sos.decode.OperationDecoderKey;
import org.n52.sos.decode.json.AbstractSosRequestDecoder;
import org.n52.sos.exception.ows.concrete.NoDecoderForKeyException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.BatchRequest;
import org.n52.sos.util.BatchConstants;
import org.n52.sos.util.http.MediaTypes;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class BatchRequestDecoder extends AbstractSosRequestDecoder<BatchRequest> {
    public BatchRequestDecoder() {
        super(BatchRequest.class, SosConstants.SOS, Sos2Constants.SERVICEVERSION, BatchConstants.OPERATION_NAME);
    }

    @Override
    protected String getSchemaURI() {
        return SchemaConstants.Request.BULK_REQUEST;
    }

    @Override
    protected BatchRequest decodeRequest(JsonNode node) throws OwsExceptionReport {
        BatchRequest request = new BatchRequest();
        if (node.path(JSONConstants.STOP_AT_FAILURE).isBoolean()) {
            request.setStopAtFailure(node.path(JSONConstants.STOP_AT_FAILURE).booleanValue());
        }
        for (JsonNode n : node.path(JSONConstants.REQUESTS)) {
            request.add(getDecoder(n).decode(n));
        }
        return request;
    }

    private Decoder<AbstractServiceRequest, JsonNode> getDecoder(JsonNode n) throws OwsExceptionReport {
        OperationDecoderKey k =
                new OperationDecoderKey(n.path(JSONConstants.SERVICE).textValue(), n.path(JSONConstants.VERSION)
                        .textValue(), n.path(JSONConstants.REQUEST).textValue(), MediaTypes.APPLICATION_JSON);
        Decoder<AbstractServiceRequest, JsonNode> decoder = CodingRepository.getInstance().getDecoder(k);
        if (decoder == null) {
            // TODO other exception?
            throw new NoDecoderForKeyException(k);
        }
        return decoder;
    }
}
