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

import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.encode.json.JSONEncoder;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.om.features.FeatureCollection;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;

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
public class FeatureOfInterestEncoder extends JSONEncoder<AbstractFeature> {
    public FeatureOfInterestEncoder() {
        super(AbstractFeature.class);
    }

    @Override
    public JsonNode encodeJSON(AbstractFeature t) throws OwsExceptionReport {
        if (t instanceof FeatureCollection) {
            return encodeFeatureCollection(t);
        } else if (t instanceof SamplingFeature) {
            return encodeSamplingFeature(t);
        } else {
            throw new UnsupportedEncoderInputException(this, t);
        }
    }

    private JsonNode encodeSamplingFeature(AbstractFeature t) throws OwsExceptionReport {
        SamplingFeature sf = (SamplingFeature) t;
        if (sf.isSetUrl()) {
            return nodeFactory().textNode(sf.getUrl());
        } else if (!sf.isSetGeometry()) {
            return nodeFactory().textNode(sf.getIdentifier().getValue());
        } else {
            ObjectNode json = nodeFactory().objectNode();
            encodeIdentifier(sf, json);
            encodeNames(sf, json);
            encodeSampledFeatures(sf, json);
            encodeGeometry(sf, json);
            return json;
        }
    }

    private JsonNode encodeFeatureCollection(AbstractFeature t) throws OwsExceptionReport {
        FeatureCollection featureCollection = (FeatureCollection) t;
        ArrayNode a = nodeFactory().arrayNode();
        for (AbstractFeature af : featureCollection) {
            a.add(encodeObjectToJson(af));
        }
        return a;
    }

    private void encodeIdentifier(SamplingFeature sf, ObjectNode json) {
        if (sf.isSetIdentifier()) {
            json.put(JSONConstants.IDENTIFIER, encodeCodeWithAuthority(sf.getIdentifier()));
        }

    }

    private void encodeNames(SamplingFeature samplingFeature, ObjectNode json) {
        if (samplingFeature.isSetNames()) {
            if (samplingFeature.getName().size() == 1) {
                json.put(JSONConstants.SAMPLED_FEATURE, encodeCodeType(samplingFeature.getName().iterator().next()));
            } else {
                ArrayNode names = json.putArray(JSONConstants.SAMPLED_FEATURE);
                for (CodeType name : samplingFeature.getName()) {
                    names.add(encodeCodeType(name));
                }
            }
        }
    }

    private void encodeSampledFeatures(SamplingFeature sf, ObjectNode json) throws OwsExceptionReport {
        if (sf.isSetSampledFeatures()) {
            if (sf.getSampledFeatures().size() == 1) {
                json.put(JSONConstants.SAMPLED_FEATURE, encodeObjectToJson(sf.getSampledFeatures().iterator().next()));
            } else {
                ArrayNode sampledFeatures = json.putArray(JSONConstants.SAMPLED_FEATURE);
                for (AbstractFeature sampledFeature : sf.getSampledFeatures()) {
                    sampledFeatures.add(encodeObjectToJson(sampledFeature));
                }
            }
        }
    }

    private void encodeGeometry(SamplingFeature sf, ObjectNode json) throws OwsExceptionReport {
        if (sf.isSetGeometry()) {
            json.put(JSONConstants.GEOMETRY, encodeObjectToJson(sf.getGeometry()));
        }
    }
}
