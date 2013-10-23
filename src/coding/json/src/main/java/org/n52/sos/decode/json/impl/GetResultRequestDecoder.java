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
import org.n52.sos.request.GetResultRequest;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class GetResultRequestDecoder extends AbstractSosRequestDecoder<GetResultRequest> {

    public GetResultRequestDecoder() {
        super(GetResultRequest.class, SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                SosConstants.Operations.GetResult);
    }

    @Override
    protected String getSchemaURI() {
        return SchemaConstants.Request.GET_RESULT;
    }

    @Override
    protected GetResultRequest decodeRequest(JsonNode node) throws OwsExceptionReport {
        GetResultRequest req = new GetResultRequest();
        req.setFeatureIdentifiers(parseFeatureIdentifiers(node));
        req.setObservationTemplateIdentifier(parseObservationTemplateIdentifier(node));
        req.setObservedProperty(parseObservedProperty(node));
        req.setOffering(parseOffering(node));
        req.setSpatialFilter(parseSpatialFilter(node));
        req.setTemporalFilter(parseTemporalFilters(node));
        return req;
    }

    private List<String> parseFeatureIdentifiers(JsonNode node) {
        return parseStringOrStringList(node.path(JSONConstants.FEATURE_OF_INTEREST));
    }

    private String parseObservationTemplateIdentifier(JsonNode node) {
        return node.path(JSONConstants.OBSERVATION_TEMPLATE).textValue();
    }

    private String parseObservedProperty(JsonNode node) {
        return node.path(JSONConstants.OBSERVED_PROPERTY).textValue();
    }

    private String parseOffering(JsonNode node) {
        return node.path(JSONConstants.OFFERING).textValue();
    }

    private SpatialFilter parseSpatialFilter(JsonNode node) throws OwsExceptionReport {
        return decodeJsonToObject(node.path(JSONConstants.SPATIAL_FILTER), SpatialFilter.class);
    }

    private List<TemporalFilter> parseTemporalFilters(JsonNode node) throws OwsExceptionReport {
        return decodeJsonToObjectList(node.path(JSONConstants.TEMPORAL_FILTER), TemporalFilter.class);
    }

}
