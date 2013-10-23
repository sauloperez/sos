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
package org.n52.sos.decode.json;

import static org.n52.sos.coding.json.JSONConstants.SERVICE;
import static org.n52.sos.coding.json.JSONConstants.VERSION;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.n52.sos.coding.json.JSONValidator;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.decode.JsonDecoderKey;
import org.n52.sos.decode.OperationDecoderKey;
import org.n52.sos.ogc.filter.ComparisonFilter;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swes.SwesExtensions;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.util.http.MediaTypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 * 
 * @param <T>
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public abstract class AbstractSosRequestDecoder<T extends AbstractServiceRequest> extends JSONDecoder<T> {

    public AbstractSosRequestDecoder(Class<T> type, String service, String version, Enum<?> operation) {
        this(type, service, version, operation.name());
    }

    public AbstractSosRequestDecoder(Class<T> type, String service, String version, String operation) {
        super(Sets.newHashSet(new JsonDecoderKey(type), new OperationDecoderKey(service, version, operation,
                MediaTypes.APPLICATION_JSON)));
    }

    public AbstractSosRequestDecoder(Set<DecoderKey> keys) {
        super(keys);
    }

    public AbstractSosRequestDecoder(Class<T> type, String service, Enum<?> operation) {
        this(type, service, null, operation.name());
    }

    public AbstractSosRequestDecoder(Class<T> type, String service, String operation) {
        this(type, service, null, operation);
    }

    @Override
    public T decodeJSON(JsonNode node, boolean validate) throws OwsExceptionReport {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        if (validate) {
            JSONValidator.getInstance().validateAndThrow(node, getSchemaURI());
        }
        T t = decodeRequest(node);
        t.setService(node.path(SERVICE).textValue());
        t.setVersion(node.path(VERSION).textValue());
        t.setExtensions(parseExtensions(node));
        return t;

    }

    protected SwesExtensions parseExtensions(JsonNode node) {
        // TODO extension parsing
        return null;
    }

    protected List<String> parseStringOrStringList(JsonNode node) {
        if (node.isArray()) {
            List<String> offerings = Lists.newArrayListWithExpectedSize(node.size());
            for (JsonNode n : node) {
                if (n.isTextual()) {
                    offerings.add(n.textValue());
                }
            }
            return offerings;
        } else if (node.isTextual()) {
            return Collections.singletonList(node.textValue());
        } else {
            return null;
        }
    }

    protected ComparisonFilter parseComparisonFilter(JsonNode node) {
        // TODO
        return null;
    }

    protected abstract String getSchemaURI();

    protected abstract T decodeRequest(JsonNode node) throws OwsExceptionReport;
}
