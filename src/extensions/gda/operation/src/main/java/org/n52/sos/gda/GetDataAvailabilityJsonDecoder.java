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
package org.n52.sos.gda;

import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.decode.json.AbstractSosRequestDecoder;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class GetDataAvailabilityJsonDecoder extends AbstractSosRequestDecoder<GetDataAvailabilityRequest> {
    public GetDataAvailabilityJsonDecoder() {
        super(GetDataAvailabilityRequest.class, SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                GetDataAvailabilityConstants.OPERATION_NAME);
    }

    @Override
    protected String getSchemaURI() {
        return SchemaConstants.Request.GET_DATA_AVAILABILITY;
    }

    @Override
    protected GetDataAvailabilityRequest decodeRequest(JsonNode node) throws OwsExceptionReport {
        GetDataAvailabilityRequest req = new GetDataAvailabilityRequest();
        if (node.has(JSONConstants.FEATURE_OF_INTEREST)) {
            for (String feature : parseStringOrStringList(node.path(JSONConstants.FEATURE_OF_INTEREST))) {
                req.addFeatureOfInterest(feature);
            }
        }
        if (node.has(JSONConstants.PROCEDURE)) {
            for (String procedure : parseStringOrStringList(node.path(JSONConstants.PROCEDURE))) {
                req.addProcedure(procedure);
            }
        }
        if (node.has(JSONConstants.OBSERVED_PROPERTY)) {
            for (String property : parseStringOrStringList(node.path(JSONConstants.OBSERVED_PROPERTY))) {
                req.addObservedProperty(property);
            }
        }
        return req;
    }
}
