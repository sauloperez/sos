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

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.n52.sos.coding.CodingRepository;
import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.coding.json.JSONUtils;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.NoEncoderForKeyException;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.util.http.MediaTypes;
import org.n52.sos.w3c.SchemaLocation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * TODO JavaDoc
 * 
 * @param <T>
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public abstract class JSONEncoder<T> implements Encoder<JsonNode, T> {
    public static final String CONTENT_TYPE = "application/json";

    private final Set<EncoderKey> encoderKeys;

    public JSONEncoder(Class<T> type, EncoderKey... additionalKeys) {
        Builder<EncoderKey> set = ImmutableSet.builder();
        set.add(new JSONEncoderKey(type));
        set.addAll(Arrays.asList(additionalKeys));
        this.encoderKeys = set.build();
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(encoderKeys);
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.emptyMap();
    }

    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        /* noop */
    }

    @Override
    public MediaType getContentType() {
        return MediaTypes.APPLICATION_JSON;
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Collections.emptySet();
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    public JsonNode encode(T objectToEncode, Map<HelperValues, String> v) throws OwsExceptionReport {
        return encode(objectToEncode);
    }

    @Override
    public JsonNode encode(T objectToEncode) throws OwsExceptionReport {
        try {
            return encodeJSON(objectToEncode);
        } catch (JSONEncodingException ex) {
            throw new NoApplicableCodeException().causedBy(ex);
        }
    }

    public abstract JsonNode encodeJSON(T t) throws OwsExceptionReport;

    protected JsonNode encodeObjectToJson(Object o) throws OwsExceptionReport {
        if (o == null) {
            return nodeFactory().nullNode();
        }
        JSONEncoderKey key = new JSONEncoderKey(o.getClass());
        Encoder<JsonNode, Object> encoder = CodingRepository.getInstance().getEncoder(key);
        if (encoder == null) {
            throw new NoEncoderForKeyException(key);
        }
        return encoder.encode(o);
    }

    protected JsonNodeFactory nodeFactory() {
        return JSONUtils.nodeFactory();
    }

    protected JsonNode encodeCodeType(CodeType codeType) {
        if (codeType.isSetCodeSpace()) {
            return nodeFactory().objectNode().put(JSONConstants.CODESPACE, codeType.getCodeSpace())
                    .put(JSONConstants.VALUE, codeType.getValue());
        } else {
            return nodeFactory().textNode(codeType.getValue());
        }
    }

    protected JsonNode encodeCodeWithAuthority(CodeWithAuthority cwa) {
        if (cwa.isSetCodeSpace()) {
            return nodeFactory().objectNode().put(JSONConstants.CODESPACE, cwa.getCodeSpace())
                    .put(JSONConstants.VALUE, cwa.getValue());
        } else {
            return nodeFactory().textNode(cwa.getValue());
        }
    }
}
