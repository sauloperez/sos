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

import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.decode.json.AbstractSosRequestDecoder;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.DescribeSensorRequest;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class DescribeSensorRequestDecoder extends AbstractSosRequestDecoder<DescribeSensorRequest> {
    public DescribeSensorRequestDecoder() {
        super(DescribeSensorRequest.class, SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                SosConstants.Operations.DescribeSensor);
    }

    @Override
    protected String getSchemaURI() {
        return SchemaConstants.Request.DESCRIBE_SENSOR;
    }

    @Override
    protected DescribeSensorRequest decodeRequest(JsonNode node) throws OwsExceptionReport {
        DescribeSensorRequest req = new DescribeSensorRequest();
        req.setProcedure(node.path(JSONConstants.PROCEDURE).textValue());
        req.setProcedureDescriptionFormat(node.path(JSONConstants.PROCEDURE_DESCRIPTION_FORMAT).textValue());
        if (node.has(JSONConstants.VALID_TIME)) {
            req.setValidTime(decodeJsonToObject(node.path(JSONConstants.VALID_TIME), Time.class));
        }
        return req;
    }
}
