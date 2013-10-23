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
package org.n52.sos.ogc.gml.time;

import org.joda.time.DateTime;
import org.n52.sos.ogc.gml.time.Time.TimeFormat;
import org.n52.sos.ogc.gml.time.Time.TimeIndeterminateValue;
import org.n52.sos.util.Constants;

/**
 * Representation class for GML TimePosition. Used by TimeInstant and TimePeriod
 * during encoding to reduce duplicate code.
 * 
 * @since 4.0.0
 * 
 */
public class TimePosition {

    /**
     * Date time of time position
     */
    private DateTime time;

    /**
     * Indeterminate value of time position
     */
    private TimeIndeterminateValue indeterminateValue;

    /**
     * Time format
     */
    private TimeFormat timeFormat;

    /**
     * constructor
     * 
     * @param time
     *            Time postion time
     */
    public TimePosition(DateTime time) {
        super();
    }

    /**
     * constructor
     * 
     * @param indeterminateValue
     *            Indeterminate value of time position
     */
    public TimePosition(TimeIndeterminateValue indeterminateValue) {
        super();
        this.indeterminateValue = indeterminateValue;
    }

    /**
     * constructor
     * 
     * @param time
     *            Time position time
     * @param timeFormat
     *            Time format
     */
    public TimePosition(DateTime time, TimeFormat timeFormat) {
        super();
        this.time = time;
        this.setTimeFormat(timeFormat);
    }

    /**
     * Get time position time
     * 
     * @return the time Time position time
     */
    public DateTime getTime() {
        return time;
    }

    /**
     * Set time position time
     * 
     * @param time
     *            the time to set
     */
    public void setTime(DateTime time) {
        this.time = time;
    }

    /**
     * Get time position indeterminate value
     * 
     * @return the indeterminateValue time position indeterminate value
     */
    public TimeIndeterminateValue getIndeterminateValue() {
        return indeterminateValue;
    }

    /**
     * Get time position time format
     * 
     * @return the timeFormat Time position time format
     */
    public TimeFormat getTimeFormat() {
        return timeFormat;
    }

    /**
     * Set time position time format
     * 
     * @param timeFormat
     *            the timeFormat to set
     */
    public void setTimeFormat(TimeFormat timeFormat) {
        this.timeFormat = timeFormat;
    }

    /**
     * Set time position indeterminat value
     * 
     * @param indeterminateValue
     *            the indeterminateValue to set
     */
    public void setIndeterminateValue(TimeIndeterminateValue indeterminateValue) {
        this.indeterminateValue = indeterminateValue;
    }

    /**
     * Check if time value is set
     * 
     * @return <tt>true</tt>, if time is set
     */
    public boolean isSetTime() {
        return getTime() != null;
    }

    /**
     * Check if indeterminateValue is set
     * 
     * @return <tt>true</tt>, if indeterminateValue is set
     */
    public boolean isSetIndeterminateValue() {
        return getIndeterminateValue() != null;
    }

    /**
     * Check if time format is set
     * 
     * @return <tt>true</tt>, if time format is set
     */
    public boolean isSetTimeFormat() {
        return getTimeFormat() != null;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Time position: ");
        if (isSetTime()) {
            result.append(getTime().toString()).append(Constants.COMMA_STRING);
        }
        result.append(getIndeterminateValue());
        return result.toString();
    }
}
