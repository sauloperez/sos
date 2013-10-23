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

import static org.n52.sos.coding.json.JSONConstants.REF;
import static org.n52.sos.coding.json.JSONConstants.VALUE;

import org.n52.sos.coding.json.JSONValidator;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.decode.json.JSONDecoder;
import org.n52.sos.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.sos.ogc.filter.SpatialFilter;
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
public class SpatialFilterDecoder extends JSONDecoder<SpatialFilter> {
    public SpatialFilterDecoder() {
        super(SpatialFilter.class);
    }

    @Override
    public SpatialFilter decodeJSON(JsonNode node, boolean validate) throws OwsExceptionReport {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        if (validate) {
            JSONValidator.getInstance().validateAndThrow(node, SchemaConstants.Common.SPATIAL_FILTER);
        }
        if (node.isObject()) {
            final String oName = node.fields().next().getKey();
            final SOp o = SOp.valueOf(oName);
            JsonNode value = node.path(oName).path(VALUE);
            JsonNode ref = node.path(oName).path(REF);
            return new SpatialFilter(o.getOp(), decodeGeometry(value), ref.textValue());
        } else {
            return null;
        }
    }

    private Geometry decodeGeometry(JsonNode value) throws OwsExceptionReport {
        return decodeJsonToObject(value, Geometry.class);
    }

    private enum SOp {
        equals(SpatialOperator.Equals), disjount(SpatialOperator.Disjoint), touches(SpatialOperator.Touches), within(
                SpatialOperator.Within), overlaps(SpatialOperator.Overlaps), crosses(SpatialOperator.Crosses), intersects(
                SpatialOperator.Intersects), contains(SpatialOperator.Contains), dWithin(SpatialOperator.DWithin), beyond(
                SpatialOperator.Beyond), bbox(SpatialOperator.BBOX);
        private SpatialOperator op;

        private SOp(SpatialOperator op) {
            this.op = op;
        }

        public SpatialOperator getOp() {
            return op;
        }
    }
}
