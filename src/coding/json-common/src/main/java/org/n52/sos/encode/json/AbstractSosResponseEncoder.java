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
package org.n52.sos.encode.json;

import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.coding.json.JSONUtils;
import org.n52.sos.encode.OperationEncoderKey;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.util.http.MediaTypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public abstract class AbstractSosResponseEncoder<T extends AbstractServiceResponse> extends JSONEncoder<T> {
    public AbstractSosResponseEncoder(Class<T> type, String operation) {
        super(type, new OperationEncoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION, operation,
                MediaTypes.APPLICATION_JSON));
    }

    public AbstractSosResponseEncoder(Class<T> type, Enum<?> operation) {
        this(type, operation.name());
    }

    @Override
    public JsonNode encodeJSON(T t) throws OwsExceptionReport {
        ObjectNode n = JSONUtils.nodeFactory().objectNode();
        n.put(JSONConstants.REQUEST, t.getOperationName());
        n.put(JSONConstants.VERSION, t.getVersion());
        n.put(JSONConstants.SERVICE, t.getService());
        encodeResponse(n, t);
        return n;
    }

    protected abstract void encodeResponse(ObjectNode json, T t) throws OwsExceptionReport;
}
