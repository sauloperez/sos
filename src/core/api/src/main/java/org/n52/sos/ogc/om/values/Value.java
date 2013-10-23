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

import java.io.Serializable;

/**
 * Interface for measurement value representation for observation
 * 
 * @since 4.0.0
 * 
 * @param <T>
 *            specific value type
 */
public interface Value<T> extends Serializable {

    /**
     * Set the measurment value
     * 
     * @param value
     *            Value to set
     */
    void setValue(T value);

    /**
     * Get the measurement value
     * 
     * @return Measurement value
     */
    T getValue();

    /**
     * Set the unit of measure
     * 
     * @param unit
     *            Unit of measure
     */
    void setUnit(String unit);

    /**
     * Get the unit of measure
     * 
     * @return Unit of measure
     */
    String getUnit();

    /**
     * Check whether the value is set
     * 
     * @return <code>true</code>, if value is set
     */
    boolean isSetValue();

    /**
     * Check whether the unit of measure is set
     * 
     * @return <code>true</code>, if unit of measure is set
     */
    boolean isSetUnit();

}
