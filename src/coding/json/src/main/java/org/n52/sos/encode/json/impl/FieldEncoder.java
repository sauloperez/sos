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
package org.n52.sos.encode.json.impl;

import static org.n52.sos.util.DateTimeHelper.formatDateTime2IsoString;

import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.encode.json.JSONEncoder;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
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
import org.n52.sos.util.DateTimeHelper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class FieldEncoder extends JSONEncoder<SweField> {
    public FieldEncoder() {
        super(SweField.class);
    }

    @Override
    public JsonNode encodeJSON(SweField field) throws OwsExceptionReport {
        switch (field.getElement().getDataComponentType()) {
        case Count:
            return encodeSweCountField(field);
        case Boolean:
            return encodeSweBooleanField(field);
        case CountRange:
            return encodeSweCountRangeField(field);
        case ObservableProperty:
            return encodeSweObservableProperyField(field);
        case Text:
            return encodeSweTextField(field);
        case Quantity:
            return encodeSweQuantityField(field);
        case QuantityRange:
            return encodeSweQuantityRangeField(field);
        case Time:
            return encodeSweTimeField(field);
        case TimeRange:
            return encodeSweTimeRangeField(field);
        case Category:
            return encodeSweCategoryField(field);
        default:
            throw new UnsupportedEncoderInputException(this, field);
        }
    }

    private ObjectNode createField(SweField field) {
        ObjectNode jfield = nodeFactory().objectNode();
        jfield.put(JSONConstants.NAME, field.getName());
        SweAbstractDataComponent element = field.getElement();
        if (element.isSetDefinition()) {
            jfield.put(JSONConstants.DEFINITION, element.getDefinition());
        }
        if (element.isSetDescription()) {
            jfield.put(JSONConstants.DESCRIPTION, element.getDescription());
        }
        if (element.isSetIdentifier()) {
            jfield.put(JSONConstants.IDENTIFIER, element.getIdentifier());
        }
        if (element.isSetLabel()) {
            jfield.put(JSONConstants.LABEL, element.getLabel());
        }
        return jfield;
    }

    private ObjectNode encodeSweCountField(SweField field) {
        ObjectNode jfield = createField(field);
        jfield.put(JSONConstants.TYPE, JSONConstants.COUNT_TYPE);
        SweCount sweCount = (SweCount) field.getElement();
        if (sweCount.isSetValue()) {
            jfield.put(JSONConstants.VALUE, sweCount.getValue());
        }
        return jfield;
    }

    private ObjectNode encodeSweBooleanField(SweField field) {
        ObjectNode jfield = createField(field);
        jfield.put(JSONConstants.TYPE, JSONConstants.BOOLEAN_TYPE);
        SweBoolean sweBoolean = (SweBoolean) field.getElement();
        if (sweBoolean.isSetValue()) {
            jfield.put(JSONConstants.VALUE, sweBoolean.getValue());
        }
        return jfield;
    }

    private ObjectNode encodeSweCountRangeField(SweField field) {
        ObjectNode jfield = createField(field);
        jfield.put(JSONConstants.TYPE, JSONConstants.COUNT_RANGE_TYPE);
        SweCountRange sweCountRange = (SweCountRange) field.getElement();
        if (sweCountRange.isSetValue()) {
            ArrayNode av = jfield.putArray(JSONConstants.VALUE);
            av.add(sweCountRange.getValue().getRangeStart());
            av.add(sweCountRange.getValue().getRangeEnd());
        }
        return jfield;
    }

    private ObjectNode encodeSweObservableProperyField(SweField field) {
        ObjectNode jfield = createField(field);
        jfield.put(JSONConstants.TYPE, JSONConstants.OBSERVABLE_PROPERTY_TYPE);
        SweObservableProperty sweObservableProperty = (SweObservableProperty) field.getElement();
        if (sweObservableProperty.isSetValue()) {
            jfield.put(JSONConstants.VALUE, sweObservableProperty.getValue());
        }
        return jfield;
    }

    private ObjectNode encodeSweTextField(SweField field) {
        ObjectNode jfield = createField(field);
        jfield.put(JSONConstants.TYPE, JSONConstants.TEXT_TYPE);
        SweText sweText = (SweText) field.getElement();
        if (sweText.isSetValue()) {
            jfield.put(JSONConstants.VALUE, sweText.getValue());
        }
        return jfield;
    }

    private ObjectNode encodeSweQuantityField(SweField field) {
        ObjectNode jfield = createField(field);
        jfield.put(JSONConstants.TYPE, JSONConstants.QUANTITY_TYPE);
        SweQuantity sweQuantity = (SweQuantity) field.getElement();
        if (sweQuantity.isSetValue()) {
            jfield.put(JSONConstants.VALUE, sweQuantity.getValue());
        }
        jfield.put(JSONConstants.UOM, sweQuantity.getUom());
        return jfield;
    }

    private ObjectNode encodeSweQuantityRangeField(SweField field) {
        ObjectNode jfield = createField(field);
        jfield.put(JSONConstants.TYPE, JSONConstants.QUANTITY_RANGE_TYPE);
        SweQuantityRange sweQuantityRange = (SweQuantityRange) field.getElement();
        jfield.put(JSONConstants.UOM, sweQuantityRange.getUom());
        if (sweQuantityRange.isSetValue()) {
            ArrayNode av = jfield.putArray(JSONConstants.VALUE);
            av.add(sweQuantityRange.getValue().getRangeStart());
            av.add(sweQuantityRange.getValue().getRangeEnd());
        }
        return jfield;
    }

    private ObjectNode encodeSweTimeField(SweField field) {
        ObjectNode jfield = createField(field);
        jfield.put(JSONConstants.TYPE, JSONConstants.TIME_TYPE);
        SweTime sweTime = (SweTime) field.getElement();
        jfield.put(JSONConstants.UOM, sweTime.getUom());
        if (sweTime.isSetValue()) {
            jfield.put(JSONConstants.VALUE, formatDateTime2IsoString(sweTime.getValue()));
        }
        return jfield;
    }

    private ObjectNode encodeSweTimeRangeField(SweField field) {
        ObjectNode jfield = createField(field);
        jfield.put(JSONConstants.TYPE, JSONConstants.TIME_RANGE_TYPE);
        SweTimeRange sweTimeRange = (SweTimeRange) field.getElement();
        jfield.put(JSONConstants.UOM, sweTimeRange.getUom());
        if (sweTimeRange.isSetValue()) {
            ArrayNode av = jfield.putArray(JSONConstants.VALUE);
            av.add(DateTimeHelper.formatDateTime2IsoString(sweTimeRange.getValue().getRangeStart()));
            av.add(DateTimeHelper.formatDateTime2IsoString(sweTimeRange.getValue().getRangeEnd()));
        }
        return jfield;
    }

    private ObjectNode encodeSweCategoryField(SweField field) {
        ObjectNode jfield = createField(field);
        jfield.put(JSONConstants.TYPE, JSONConstants.CATEGORY_TYPE);
        SweCategory sweCategory = (SweCategory) field.getElement();
        jfield.put(JSONConstants.CODESPACE, sweCategory.getCodeSpace());
        if (sweCategory.isSetValue()) {
            jfield.put(JSONConstants.VALUE, sweCategory.getValue());
        }
        return jfield;
    }
}
