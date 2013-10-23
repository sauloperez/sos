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

import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.om.values.Value;

/**
 * Class representing a O&M conform NamedValue
 * 
 * @since 4.0.0
 * 
 * @param <T>
 *            value type
 */
public class NamedValue<T> {

    /**
     * Value name
     */
    private ReferenceType name;

    /**
     * Value
     */
    private Value<T> value;

    /**
     * Get value name
     * 
     * @return Value name
     */
    public ReferenceType getName() {
        return name;
    }

    /**
     * Set value name
     * 
     * @param name
     *            Value name to set
     */
    public void setName(ReferenceType name) {
        this.name = name;
    }

    /**
     * Get value
     * 
     * @return Value
     */
    public Value<T> getValue() {
        return value;
    }

    /**
     * Set value
     * 
     * @param value
     *            Value to set
     */
    public void setValue(Value<T> value) {
        this.value = value;
    }

    /**
     * Check whether value name is set
     * 
     * @return <code>true</code>, if value name is set
     */
    public boolean isSetName() {
        return name != null && name.isSetHref();
    }

    /**
     * Check whether value is set
     * 
     * @return <code>true</code>, if value is set
     */
    public boolean isSetValue() {
        return value != null && value.isSetValue();
    }
}
