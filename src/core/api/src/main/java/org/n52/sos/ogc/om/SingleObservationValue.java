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

import java.util.List;

import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.om.quality.SosQuality;
import org.n52.sos.ogc.om.values.Value;

/**
 * Class representing a single value observation value
 * 
 * @since 4.0.0
 * 
 * @param <T>
 *            value type
 */
public class SingleObservationValue<T> implements ObservationValue<Value<T>> {
    /**
     * serial number
     */
    private static final long serialVersionUID = -8162038672393523937L;

    /**
     * Phenomenon time
     */
    private Time phenomenonTime;

    /**
     * Measurement value
     */
    private Value<T> value;

    /**
     * Measurment quality
     */
    private List<SosQuality> qualityList;

    /**
     * constructor
     */
    public SingleObservationValue() {
    }

    /**
     * constructor
     * 
     * @param value
     *            Measurement value
     */
    public SingleObservationValue(Value<T> value) {
        this.value = value;
    }

    /**
     * constructor
     * 
     * @param phenomenonTime
     *            Phenomenon time
     * @param value
     *            Measurement value
     * @param qualityList
     *            Measurment quality
     */
    public SingleObservationValue(Time phenomenonTime, Value<T> value, List<SosQuality> qualityList) {
        this.phenomenonTime = phenomenonTime;
        this.value = value;
        this.qualityList = qualityList;
    }

    /**
     * constructor
     * 
     * @param phenomenonTime
     *            Phenomenon time
     * @param value
     *            Measurement value
     */
    public SingleObservationValue(Time phenomenonTime, Value<T> value) {
        this.phenomenonTime = phenomenonTime;
        this.value = value;
    }

    @Override
    public Time getPhenomenonTime() {
        return phenomenonTime;
    }

    @Override
    public void setPhenomenonTime(Time phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }

    @Override
    public Value<T> getValue() {
        return value;
    }

    @Override
    public void setValue(Value<T> value) {
        this.value = value;
    }

    /**
     * Set measurement quality
     * 
     * @param qualityList
     *            Measurement quality to set
     */
    public void setQualityList(List<SosQuality> qualityList) {
        this.qualityList = qualityList;
    }

    /**
     * Get measurement quality
     * 
     * @return Measurement quality
     */
    public List<SosQuality> getQualityList() {
        return qualityList;
    }
}
