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
package org.n52.sos.ogc.filter;

import org.n52.sos.ogc.filter.FilterConstants.TimeOperator;
import org.n52.sos.ogc.gml.time.Time;

/**
 * Temporal filter class
 * 
 * @since 4.0.0
 */
public class TemporalFilter extends Filter<TimeOperator> {

    /**
     * Temporal filter operator
     */
    private TimeOperator operator;

    /**
     * Temporal filter time value
     */
    private Time time;

    /**
     * constructor
     */
    public TemporalFilter() {
        super();
    }

    /**
     * constructor
     * 
     * @param operatorp
     *            Temporal filter operator
     * @param timep
     *            Filter time
     * @param valueReferencep
     *            value reference
     */
    public TemporalFilter(TimeOperator operatorp, Time timep, String valueReferencep) {
        super(valueReferencep);
        this.operator = operatorp;
        this.time = timep;
    }

    /**
     * constructor
     * 
     * @param operatorNamep
     *            Temporal filter operator name
     * @param timep
     *            Filter time
     * @param valueReferencep
     *            value reference
     */
    public TemporalFilter(String operatorNamep, Time timep, String valueReferencep) {
        super(valueReferencep);
        this.operator = TimeOperator.valueOf(operatorNamep);
        this.time = timep;
    }

    @Override
    public TimeOperator getOperator() {
        return operator;
    }

    @Override
    public TemporalFilter setOperator(TimeOperator operator) {
        this.operator = operator;
        return this;
    }

    /**
     * Get filter time
     * 
     * @return filter time
     */
    public Time getTime() {
        return time;
    }

    /**
     * Set filter time
     * 
     * @param time
     *            filter time
     * @return This filter
     */
    public TemporalFilter setTime(Time time) {
        this.time = time;
        return this;
    }

    @Override
    public String toString() {
        return "Temporal filter: " + operator + time.toString();
    }

}
