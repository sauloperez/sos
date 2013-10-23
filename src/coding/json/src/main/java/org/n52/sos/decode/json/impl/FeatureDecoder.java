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

import static org.n52.sos.coding.json.JSONConstants.GEOMETRY;
import static org.n52.sos.coding.json.JSONConstants.IDENTIFIER;
import static org.n52.sos.coding.json.JSONConstants.NAME;
import static org.n52.sos.coding.json.JSONConstants.SAMPLED_FEATURE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.n52.sos.coding.json.JSONValidator;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.decode.json.JSONDecoder;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.om.features.FeatureCollection;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.fasterxml.jackson.databind.JsonNode;
import com.vividsolutions.jts.geom.Geometry;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class FeatureDecoder extends JSONDecoder<AbstractFeature> {
    private final JSONDecoder<Geometry> geometryDecoder = new GeoJSONDecoder();

    public FeatureDecoder() {
        super(AbstractFeature.class);
    }

    @Override
    public AbstractFeature decodeJSON(JsonNode node, boolean validate) throws OwsExceptionReport {
        if (validate) {
            JSONValidator.getInstance().validateAndThrow(node, SchemaConstants.Common.FEATURE_OF_INTEREST);
        }
        return decodeJSON(node);
    }

    protected AbstractFeature decodeJSON(JsonNode node) throws OwsExceptionReport {
        if (node.isArray()) {
            return parseFeatureCollection(node);
        } else {
            return parseSamplingFeature(node);
        }
    }

    protected SamplingFeature parseSamplingFeature(JsonNode node) throws OwsExceptionReport {
        if (node.isTextual()) {
            return new SamplingFeature(parseCodeWithAuthority(node));
        } else if (node.isObject()) {
            SamplingFeature foi = new SamplingFeature(parseCodeWithAuthority(node.path(IDENTIFIER)));
            foi.setGeometry(parseGeometry(node));
            foi.setSampledFeatures(parseSampledFeatures(node));
            foi.setName(parseNames(node));
            return foi;
        } else {
            return null;
        }
    }

    private FeatureCollection parseFeatureCollection(JsonNode node) throws OwsExceptionReport {
        if (node.isArray()) {
            FeatureCollection collection = new FeatureCollection();
            for (JsonNode n : node) {
                collection.addMember(parseSamplingFeature(n));
            }
            return collection;
        } else {
            return null;
        }
    }

    protected List<AbstractFeature> parseSampledFeatures(JsonNode node) throws OwsExceptionReport {
        final JsonNode sfnode = node.path(SAMPLED_FEATURE);
        if (sfnode.isArray()) {
            ArrayList<AbstractFeature> features = new ArrayList<AbstractFeature>(sfnode.size());
            for (JsonNode n : sfnode) {
                features.add(parseSamplingFeature(n));
            }
            return features;
        } else {
            final SamplingFeature sff = parseSamplingFeature(sfnode);
            if (sff == null) {
                return Collections.emptyList();
            } else {
                return Collections.<AbstractFeature> singletonList(sff);
            }
        }
    }

    protected Geometry parseGeometry(JsonNode node) throws OwsExceptionReport {
        return geometryDecoder.decodeJSON(node.path(GEOMETRY), false);
    }

    private List<CodeType> parseNames(JsonNode node) {
        final JsonNode name = node.path(NAME);
        if (name.isArray()) {
            ArrayList<CodeType> codeTypes = new ArrayList<CodeType>(name.size());
            for (JsonNode n : name) {
                codeTypes.add(parseCodeType(n));
            }
            return codeTypes;
        } else {
            return Collections.singletonList(parseCodeType(name));
        }
    }
}
