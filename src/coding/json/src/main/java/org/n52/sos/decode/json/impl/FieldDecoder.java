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

import org.joda.time.DateTime;
import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.coding.json.JSONValidator;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.decode.json.JSONDecoder;
import org.n52.sos.exception.ows.concrete.DateTimeParseException;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.RangeValue;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swe.simpleType.SweCategory;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweCountRange;
import org.n52.sos.ogc.swe.simpleType.SweObservableProperty;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweQuantityRange;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.ogc.swe.simpleType.SweTime;
import org.n52.sos.ogc.swe.simpleType.SweTimeRange;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class FieldDecoder extends JSONDecoder<SweField> {

    public FieldDecoder() {
        super(SweField.class);
    }

    @Override
    public SweField decodeJSON(JsonNode node, boolean validate) throws OwsExceptionReport {
        if (validate) {
            JSONValidator.getInstance().validateAndThrow(node, SchemaConstants.Common.FIELD);
        }
        return decodeJSON(node);
    }

    public SweField decodeJSON(JsonNode node) throws DateTimeParseException, UnsupportedDecoderInputException {
        final String type = node.path(JSONConstants.TYPE).textValue();
        final SweAbstractDataComponent element;

        if (type.equals(JSONConstants.BOOLEAN_TYPE)) {
            element = decodeBoolean(node);
        } else if (type.equals(JSONConstants.COUNT_TYPE)) {
            element = decodeCount(node);
        } else if (type.equals(JSONConstants.COUNT_RANGE_TYPE)) {
            element = decodeCountRange(node);
        } else if (type.equals(JSONConstants.OBSERVABLE_PROPERTY_TYPE)) {
            element = decodeObservableProperty(node);
        } else if (type.equals(JSONConstants.QUALITY_TYPE)) {
            element = decodeQuality(node);
        } else if (type.equals(JSONConstants.TEXT_TYPE)) {
            element = decodeText(node);
        } else if (type.equals(JSONConstants.QUANTITY_TYPE)) {
            element = decodeQuantity(node);
        } else if (type.equals(JSONConstants.QUANTITY_RANGE_TYPE)) {
            element = decodeQuantityRange(node);
        } else if (type.equals(JSONConstants.TIME_TYPE)) {
            element = decodeTime(node);
        } else if (type.equals(JSONConstants.TIME_RANGE_TYPE)) {
            element = decodeTimeRange(node);
        } else if (type.equals(JSONConstants.CATEGORY_TYPE)) {
            element = decodeCategory(node);
        } else {
            throw new UnsupportedDecoderInputException(this, node);
        }
        final String name = node.path(JSONConstants.NAME).textValue();
        element.setDescription(node.path(JSONConstants.DESCRIPTION).textValue());
        element.setIdentifier(node.path(JSONConstants.IDENTIFIER).textValue());
        element.setDefinition(node.path(JSONConstants.DEFINITION).textValue());
        element.setLabel(node.path(JSONConstants.LABEL).textValue());
        return new SweField(name, element);
    }

    protected SweAbstractDataComponent decodeBoolean(JsonNode node) {
        SweBoolean swe = new SweBoolean();
        if (node.hasNonNull(JSONConstants.VALUE)) {
            swe.setValue(node.path(JSONConstants.VALUE).booleanValue());
        }
        return swe;
    }

    protected SweAbstractDataComponent decodeCount(JsonNode node) {
        SweCount swe = new SweCount();
        if (node.hasNonNull(JSONConstants.VALUE)) {
            swe.setValue(node.path(JSONConstants.VALUE).intValue());
        }
        return swe;
    }

    protected SweAbstractDataComponent decodeCountRange(JsonNode node) {
        SweCountRange swe = new SweCountRange();
        if (node.hasNonNull(JSONConstants.VALUE)) {
            int start = node.path(JSONConstants.VALUE).path(0).intValue();
            int end = node.path(JSONConstants.VALUE).path(1).intValue();
            swe.setValue(new RangeValue<Integer>(start, end));
        }
        return swe;
    }

    protected SweAbstractDataComponent decodeQuantity(JsonNode node) {
        SweQuantity swe = new SweQuantity();
        if (node.hasNonNull(JSONConstants.VALUE)) {
            swe.setValue(node.path(JSONConstants.VALUE).doubleValue());
        }
        return swe.setUom(node.path(JSONConstants.UOM).textValue());
    }

    protected SweAbstractDataComponent decodeText(JsonNode node) {
        return new SweText().setValue(node.path(JSONConstants.VALUE).textValue());
    }

    protected SweAbstractDataComponent decodeQuality(JsonNode node) throws UnsupportedDecoderInputException {
        // TODO quality
        throw new UnsupportedDecoderInputException(this, node);
    }

    protected SweAbstractDataComponent decodeObservableProperty(JsonNode node) {
        SweObservableProperty swe = new SweObservableProperty();
        return swe.setValue(node.path(JSONConstants.VALUE).textValue());
    }

    protected SweAbstractDataComponent decodeTime(JsonNode node) throws DateTimeParseException {
        SweTime swe = new SweTime();
        if (node.hasNonNull(JSONConstants.VALUE)) {
            String value = node.path(JSONConstants.VALUE).textValue();
            swe.setValue(parseDateTime(value));
        }
        return swe.setUom(node.path(JSONConstants.UOM).textValue());
    }

    protected SweAbstractDataComponent decodeCategory(JsonNode node) {
        String value = node.path(JSONConstants.VALUE).textValue();
        String codespace = node.path(JSONConstants.CODESPACE).textValue();
        return new SweCategory().setValue(value).setCodeSpace(codespace);
    }

    protected SweAbstractDataComponent decodeQuantityRange(JsonNode node) {
        SweQuantityRange swe = new SweQuantityRange();
        if (node.hasNonNull(JSONConstants.VALUE)) {
            double start = node.path(JSONConstants.VALUE).path(0).doubleValue();
            double end = node.path(JSONConstants.VALUE).path(1).doubleValue();
            swe.setValue(new RangeValue<Double>(start, end));
        }
        return swe.setUom(node.path(JSONConstants.UOM).textValue());
    }

    protected SweAbstractDataComponent decodeTimeRange(JsonNode node) throws DateTimeParseException {
        SweTimeRange swe = new SweTimeRange();
        if (node.hasNonNull(JSONConstants.VALUE)) {
            String start = node.path(JSONConstants.VALUE).path(0).textValue();
            String end = node.path(JSONConstants.VALUE).path(1).textValue();
            swe.setValue(new RangeValue<DateTime>(parseDateTime(start), parseDateTime(end)));
        }
        return swe.setUom(node.path(JSONConstants.UOM).textValue());
    }
}
