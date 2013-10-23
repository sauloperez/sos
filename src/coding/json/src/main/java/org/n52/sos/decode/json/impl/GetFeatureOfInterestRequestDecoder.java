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

import java.util.List;

import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.decode.json.AbstractSosRequestDecoder;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.GetFeatureOfInterestRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class GetFeatureOfInterestRequestDecoder extends AbstractSosRequestDecoder<GetFeatureOfInterestRequest> {
    private static final Logger log = LoggerFactory.getLogger(GetFeatureOfInterestRequestDecoder.class);

    public GetFeatureOfInterestRequestDecoder() {
        super(GetFeatureOfInterestRequest.class, SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                SosConstants.Operations.GetFeatureOfInterest);
    }

    @Override
    protected String getSchemaURI() {
        return SchemaConstants.Request.GET_FEATURE_OF_INTEREST;
    }

    @Override
    protected GetFeatureOfInterestRequest decodeRequest(JsonNode node) throws OwsExceptionReport {
        GetFeatureOfInterestRequest req = new GetFeatureOfInterestRequest();
        req.setFeatureIdentifiers(decodeFeatureOfInterests(node));
        req.setProcedures(decodeProcedures(node));
        req.setObservedProperties(decodeObservedProperties(node));
        req.setSpatialFilters(decodeSpatialFilters(node));
        req.setTemporalFilters(decodeTemporalFilters(node));
        return req;
    }

    private List<SpatialFilter> decodeSpatialFilters(JsonNode node) throws OwsExceptionReport {
        JsonNode path = node.path(JSONConstants.SPATIAL_FILTER);
        return decodeJsonToObjectList(path, SpatialFilter.class);
    }

    private List<TemporalFilter> decodeTemporalFilters(JsonNode node) throws OwsExceptionReport {
        JsonNode path = node.path(JSONConstants.TEMPORAL_FILTER);
        return decodeJsonToObjectList(path, TemporalFilter.class);
    }

    private List<String> decodeObservedProperties(JsonNode node) {
        JsonNode path = node.path(JSONConstants.OBSERVABLE_PROPERTY);
        return parseStringOrStringList(path);
    }

    private List<String> decodeProcedures(JsonNode node) {
        JsonNode path = node.path(JSONConstants.PROCEDURE);
        return parseStringOrStringList(path);
    }

    private List<String> decodeFeatureOfInterests(JsonNode node) {
        JsonNode path = node.path(JSONConstants.IDENTIFIER);
        return parseStringOrStringList(path);
    }
}
