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
import org.n52.sos.encode.json.AbstractSosResponseEncoder;
import org.n52.sos.gda.GetDataAvailabilityResponse.DataAvailability;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class GetDataAvailabilityJsonEncoder extends AbstractSosResponseEncoder<GetDataAvailabilityResponse> {
    public GetDataAvailabilityJsonEncoder() {
        super(GetDataAvailabilityResponse.class, GetDataAvailabilityConstants.OPERATION_NAME);
    }

    @Override
    protected void encodeResponse(ObjectNode json, GetDataAvailabilityResponse t) throws OwsExceptionReport {
        ArrayNode a = json.putArray(GetDataAvailabilityConstants.DATA_AVAILABILITY);
        for (DataAvailability da : t.getDataAvailabilities()) {
            a.addObject().put(JSONConstants.FEATURE_OF_INTEREST, da.getFeatureOfInterest().getHref())
                    .put(JSONConstants.PROCEDURE, da.getProcedure().getHref())
                    .put(JSONConstants.OBSERVED_PROPERTY, da.getObservedProperty().getHref())
                    .put(JSONConstants.PHENOMENON_TIME, encodeObjectToJson(da.getPhenomenonTime()));

        }
    }
}
