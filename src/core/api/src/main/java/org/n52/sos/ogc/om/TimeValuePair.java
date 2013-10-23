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
package org.n52.sos.ogc.om;

import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.om.values.Value;

/**
 * Class representing a time value pair
 * 
 * @since 4.0.0
 * 
 */
public class TimeValuePair implements Comparable<TimeValuePair> {

    /**
     * Time value pair time
     */
    private Time time;

    /**
     * Time value pair value
     */
    private Value<?> value;

    /**
     * Constructor
     * 
     * @param time
     *            Time value pair time
     * @param value
     *            Time value pair value
     */
    public TimeValuePair(Time time, Value<?> value) {
        this.time = time;
        this.value = value;
    }

    /**
     * Get time value pair time
     * 
     * @return Time value pair time
     */
    public Time getTime() {
        return time;
    }

    /**
     * Get time value pair value
     * 
     * @return Time value pair value
     */
    public Value<?> getValue() {
        return value;
    }

    /**
     * Set time value pair time
     * 
     * @param time
     *            Time value pair time to set
     */
    public void setTime(Time time) {
        this.time = time;
    }

    /**
     * Set time value pair value
     * 
     * @param value
     *            Time value pair value to set
     */
    public void setValue(Value<?> value) {
        this.value = value;
    }

    @Override
    public int compareTo(TimeValuePair o) {
        return time.compareTo(o.time);
    }

}
