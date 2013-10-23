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
import org.n52.sos.coding.json.JSONValidator;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.decode.json.JSONDecoder;
import org.n52.sos.exception.ows.concrete.DateTimeParseException;
import org.n52.sos.ogc.filter.FilterConstants.TimeOperator;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class TemporalFilterDecoder extends JSONDecoder<TemporalFilter> {
    public TemporalFilterDecoder() {
        super(TemporalFilter.class);
    }

    @Override
    public TemporalFilter decodeJSON(JsonNode node, boolean validate) throws OwsExceptionReport {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        if (validate) {
            JSONValidator.getInstance().validateAndThrow(node, SchemaConstants.Common.TEMPORAL_FILTER);
        }
        if (node.isObject()) {
            return parseTemporalFilter(node);
        } else {
            return null;
        }
    }

    protected TemporalFilter parseTemporalFilter(JsonNode node) throws DateTimeParseException {
        if (node.isObject()) {
            final String oName = node.fields().next().getKey();
            final TOp o = TOp.valueOf(oName);
            return new TemporalFilter(o.getOp(), parseTime(node.path(oName).path(JSONConstants.VALUE)), node
                    .path(oName).path(JSONConstants.REF).textValue());
        } else {
            return null;
        }
    }

    private enum TOp {
        before(TimeOperator.TM_Before), after(TimeOperator.TM_After), begins(TimeOperator.TM_Begins), ends(
                TimeOperator.TM_Ends), endedBy(TimeOperator.TM_EndedBy), begunBy(TimeOperator.TM_BegunBy), during(
                TimeOperator.TM_During), equals(TimeOperator.TM_Equals), contains(TimeOperator.TM_Contains), overlaps(
                TimeOperator.TM_Overlaps), meets(TimeOperator.TM_Meets), metBy(TimeOperator.TM_MetBy), overlappedBy(
                TimeOperator.TM_OverlappedBy);
        private TimeOperator op;

        private TOp(TimeOperator op) {
            this.op = op;
        }

        public TimeOperator getOp() {
            return op;
        }
    }
}
