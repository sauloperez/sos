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

import static org.n52.sos.coding.json.JSONConstants.FEATURE_OF_INTEREST;
import static org.n52.sos.coding.json.JSONConstants.OBSERVED_PROPERTY;
import static org.n52.sos.coding.json.JSONConstants.OFFERING;
import static org.n52.sos.coding.json.JSONConstants.PROCEDURE;
import static org.n52.sos.coding.json.JSONConstants.RESPONSE_FORMAT;
import static org.n52.sos.coding.json.JSONConstants.RESPONSE_MODE;
import static org.n52.sos.coding.json.JSONConstants.RESULT_FILTER;
import static org.n52.sos.coding.json.JSONConstants.RESULT_MODEL;
import static org.n52.sos.coding.json.JSONConstants.SPATIAL_FILTER;
import static org.n52.sos.coding.json.JSONConstants.TEMPORAL_FILTER;

import java.util.List;

import org.n52.sos.coding.json.JSONUtils;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.decode.json.AbstractSosRequestDecoder;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.GetObservationRequest;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class GetObservationRequestDecoder extends AbstractSosRequestDecoder<GetObservationRequest> {

    public GetObservationRequestDecoder() {
        super(GetObservationRequest.class, SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                SosConstants.Operations.GetObservation);
    }

    @Override
    public String getSchemaURI() {
        return SchemaConstants.Request.GET_OBSERVATION;
    }

    @Override
    public GetObservationRequest decodeRequest(JsonNode node) throws OwsExceptionReport {
        GetObservationRequest r = new GetObservationRequest();
        r.setFeatureIdentifiers(parseStringOrStringList(node.path(FEATURE_OF_INTEREST)));
        r.setObservedProperties(parseStringOrStringList(node.path(OBSERVED_PROPERTY)));
        r.setOfferings(parseStringOrStringList(node.path(OFFERING)));
        r.setProcedures(parseStringOrStringList(node.path(PROCEDURE)));
        r.setResponseFormat(node.path(RESPONSE_FORMAT).textValue());
        r.setResponseMode(node.path(RESPONSE_MODE).textValue());
        r.setResultModel(node.path(RESULT_MODEL).textValue());
        r.setResult(parseComparisonFilter(node.path(RESULT_FILTER)));
        r.setSpatialFilter(parseSpatialFilter(node.path(SPATIAL_FILTER)));
        r.setTemporalFilters(parseTemporalFilters(node.path(TEMPORAL_FILTER)));
        // TODO whats that for?
        r.setRequestString(JSONUtils.print(node));
        return r;
    }

    private List<TemporalFilter> parseTemporalFilters(JsonNode node) throws OwsExceptionReport {
        return decodeJsonToObjectList(node, TemporalFilter.class);
    }

    private SpatialFilter parseSpatialFilter(JsonNode node) throws OwsExceptionReport {
        return decodeJsonToObject(node, SpatialFilter.class);
    }
}
