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

import static org.n52.sos.util.DateTimeHelper.formatDateTime2IsoString;
import static org.n52.sos.util.DateTimeHelper.formatDateTime2String;

import org.n52.sos.encode.json.JSONEncoder;
import org.n52.sos.exception.ows.concrete.DateTimeFormatException;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.gml.time.TimePosition;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class TimeEncoder extends JSONEncoder<Time> {
    public TimeEncoder() {
        super(Time.class);
    }

    private String encodeTimePosition(TimePosition timePosition) throws DateTimeFormatException {
        if (timePosition.isSetIndeterminateValue()) {
            return timePosition.getIndeterminateValue().name();
        } else if (timePosition.isSetTimeFormat()) {
            return formatDateTime2String(timePosition.getTime(), timePosition.getTimeFormat());
        } else if (timePosition.isSetTime()) {
            return formatDateTime2IsoString(timePosition.getTime());
        } else {
            return null;
        }
    }

    @Override
    public JsonNode encodeJSON(Time time) throws OwsExceptionReport {
        if (time instanceof TimeInstant) {
            TimeInstant ti = (TimeInstant) time;
            return nodeFactory().textNode(encodeTimePosition(ti.getTimePosition()));
        }
        if (time instanceof TimePeriod) {
            TimePeriod tp = (TimePeriod) time;
            ArrayNode a = nodeFactory().arrayNode();
            a.add(encodeTimePosition(tp.getStartTimePosition()));
            a.add(encodeTimePosition(tp.getEndTimePosition()));
            return a;
        } else {
            return null;
        }
    }
}
