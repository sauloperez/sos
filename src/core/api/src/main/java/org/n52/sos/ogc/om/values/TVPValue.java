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
package org.n52.sos.ogc.om.values;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.TimeValuePair;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;

/**
 * Multi value representing a time value pairs for observations
 * 
 * @since 4.0.0
 * 
 */
public class TVPValue implements MultiValue<List<TimeValuePair>> {

    /**
     * serial number
     */
    private static final long serialVersionUID = -5156098026027119423L;

    /**
     * Mesurement values
     */
    private List<TimeValuePair> value = new ArrayList<TimeValuePair>(0);

    /**
     * Unit of measure
     */
    private String unit;

    @Override
    public void setValue(List<TimeValuePair> value) {
        this.value = value;
    }

    @Override
    public List<TimeValuePair> getValue() {
        Collections.sort(value);
        return value;
    }

    /**
     * Add time value pair value
     * 
     * @param value
     *            Time value pair value to add
     */
    public void addValue(TimeValuePair value) {
        this.value.add(value);
    }

    /**
     * Add time value pair values
     * 
     * @param values
     *            Time value pair values to add
     */
    public void addValues(List<TimeValuePair> values) {
        this.value.addAll(values);
    }

    @Override
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String getUnit() {
        return this.unit;
    }

    @Override
    public Time getPhenomenonTime() {
        TimePeriod timePeriod = new TimePeriod();
        if (isSetValue()) {
            for (TimeValuePair timeValuePair : getValue()) {
                timePeriod.extendToContain(timeValuePair.getTime());
            }
        }
        return timePeriod;
    }

    @Override
    public boolean isSetValue() {
        return CollectionHelper.isNotEmpty(getValue());
    }

    @Override
    public boolean isSetUnit() {
        return StringHelper.isNotEmpty(getUnit());
    }
}
