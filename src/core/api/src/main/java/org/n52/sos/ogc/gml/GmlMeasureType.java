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
package org.n52.sos.ogc.gml;

import java.math.BigDecimal;

import org.n52.sos.util.StringHelper;

/**
 * Class represents a GML conform MeasureType element
 * 
 * @since 4.0.0
 * 
 */
public class GmlMeasureType {

    /**
     * Measured value
     */
    private BigDecimal value;

    /**
     * Unit of measure
     */
    private String unit;

    /**
     * constructor
     * 
     * @param value
     *            Measured value
     */
    public GmlMeasureType(BigDecimal value) {
        this(value, null);
    }

    /**
     * constructor
     * 
     * @param value
     *            Measured value
     * @param unit
     *            Unit of measure
     */
    public GmlMeasureType(BigDecimal value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    /**
     * @param value
     *            Measured value to set
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    /**
     * @return Measured value
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Set unit of measure
     * 
     * @param unit
     *            Unit of measure to set
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Get unit of measure
     * 
     * @return Unit of measure
     */
    public String getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return String.format("GmlMeasureType [value=%s, unit=%s]", getValue(), getUnit());
    }

    /**
     * Check whether measured value is set
     * 
     * @return <code>true</code>, if measured value is set
     */
    public boolean isSetValue() {
        return value != null;
    }

    /**
     * Check whether unit of measure is set
     * 
     * @return <code>true</code>, if unit of measure is set
     */
    public boolean isSetUnit() {
        return StringHelper.isNotEmpty(getUnit());
    }

}
