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

import static org.n52.sos.util.DateTimeHelper.parseIsoString2DateTime;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.decode.Decoder;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.decode.JsonDecoderKey;
import org.n52.sos.exception.ows.concrete.DateTimeParseException;
import org.n52.sos.exception.ows.concrete.NoDecoderForKeyException;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.Time.TimeIndeterminateValue;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Lists;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public abstract class JSONDecoder<T> implements Decoder<T, JsonNode> {
    private final Set<DecoderKey> decoderKeys;

    public JSONDecoder(Class<T> type) {
        this(Collections.<DecoderKey> singleton(new JsonDecoderKey(type)));
    }

    public JSONDecoder(Set<DecoderKey> keys) {
        this.decoderKeys = keys;
    }

    private <T> Decoder<T, JsonNode> getDecoder(Class<T> type) throws NoDecoderForKeyException {
        JsonDecoderKey key = new JsonDecoderKey(type);
        Decoder<T, JsonNode> decoder = CodingRepository.getInstance().getDecoder(key);
        if (decoder == null) {
            throw new NoDecoderForKeyException(key);
        }
        return decoder;
    }

    protected <T> T decodeJsonToObject(JsonNode json, Class<T> type) throws OwsExceptionReport {
        if (json == null || json.isNull() || json.isMissingNode()) {
            return null;
        }
        return getDecoder(type).decode(json);
    }

    protected <T> List<T> decodeJsonToObjectList(JsonNode node, Class<T> type) throws OwsExceptionReport {
        Decoder<T, JsonNode> decoder = getDecoder(type);
        if (node.isArray()) {
            List<T> filters = Lists.newArrayListWithExpectedSize(node.size());
            for (JsonNode n : node) {
                if (n.isObject()) {
                    filters.add(decoder.decode(n));
                }
            }
            return filters;
        } else if (node.isObject()) {
            return Collections.singletonList(decoder.decode(node));
        } else {
            return null;
        }
    }

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(decoderKeys);
    }

    @Override
    public T decode(JsonNode objectToDecode) throws OwsExceptionReport {
        return decodeJSON(objectToDecode, true);
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.emptyMap();
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    protected TimeInstant parseTimeInstant(JsonNode node) throws DateTimeParseException {
        if (node.isTextual()) {
            TimeInstant timeInstant = new TimeInstant();
            String time = node.textValue();
            if (TimeIndeterminateValue.template.equals(TimeIndeterminateValue.getEnumForString(time))) {
                timeInstant.setIndeterminateValue(TimeIndeterminateValue.template);
            } else {
                DateTime dateTime = parseDateTime(time);
                timeInstant.setValue(dateTime);
            }
            return timeInstant;
        } else {
            return null;
        }
    }

    protected TimePeriod parseTimePeriod(JsonNode node) throws DateTimeParseException {
        if (node.isArray()) {
            ArrayNode array = (ArrayNode) node;
            String startTime = array.get(0).textValue();
            String endTime = array.get(1).textValue();
            DateTime start = parseDateTime(startTime);
            DateTime end = parseDateTime(endTime);
            return new TimePeriod(start, end);
        } else {
            return null;
        }
    }

    protected DateTime parseDateTime(String time) throws DateTimeParseException {
        return parseIsoString2DateTime(time);
    }

    protected Time parseTime(JsonNode node) throws DateTimeParseException {
        if (node.isArray()) {
            return parseTimePeriod(node);
        } else if (node.isTextual()) {
            return parseTimeInstant(node);
        } else {
            return null;
        }
    }

    protected CodeWithAuthority parseCodeWithAuthority(JsonNode node) {
        if (node.isObject()) {
            String value = node.path(JSONConstants.VALUE).textValue();
            String codespace = node.path(JSONConstants.CODESPACE).textValue();
            if (codespace == null || codespace.isEmpty()) {
                codespace = OGCConstants.UNKNOWN;
            }
            return new CodeWithAuthority(value, codespace);
        } else if (node.isTextual()) {
            return new CodeWithAuthority(node.textValue(), OGCConstants.UNKNOWN);
        } else {
            return null;
        }
    }

    protected CodeType parseCodeType(JsonNode node) {
        if (node.isObject()) {
            String value = node.path(JSONConstants.VALUE).textValue();
            String codespace = node.path(JSONConstants.CODESPACE).textValue();
            if (codespace == null || codespace.isEmpty()) {
                codespace = OGCConstants.UNKNOWN;
            }
            return new CodeType(value).setCodeSpace(codespace);
        } else if (node.isTextual()) {
            return new CodeType(node.textValue()).setCodeSpace(OGCConstants.UNKNOWN);
        } else {
            return null;
        }
    }

    public abstract T decodeJSON(JsonNode node, boolean validate) throws OwsExceptionReport;
}
