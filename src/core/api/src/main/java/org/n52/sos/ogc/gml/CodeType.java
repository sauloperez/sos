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

import org.n52.sos.util.Constants;
import org.n52.sos.util.StringHelper;

/**
 * Class represents a GML conform CodeType element
 * 
 * @since 4.0.0
 * 
 */
public class CodeType {

    /**
     * Value/identifier
     */
    private String value;

    /**
     * Code space
     */
    private String codeSpace = Constants.EMPTY_STRING;

    /**
     * constructor
     * 
     * @param value
     *            Value/identifier
     */
    public CodeType(final String value) {
        this.value = value;
    }

    /**
     * Get value
     * 
     * @return Value to set
     */
    public String getValue() {
        return value;
    }

    /**
     * Get code space
     * 
     * @return Code space
     */
    public String getCodeSpace() {
        return codeSpace;
    }

    /**
     * Set value and return this CodeType object
     * 
     * @param value
     *            Value to set
     * @return This CodeType object
     */
    public CodeType setValue(final String value) {
        this.value = value;
        return this;
    }

    /**
     * Set code space and return this CodeType object
     * 
     * @param codeSpace
     *            Code space to set
     * @return This CodeType object
     */
    public CodeType setCodeSpace(final String codeSpace) {
        this.codeSpace = codeSpace;
        return this;
    }

    /**
     * Check whether value is set
     * 
     * @return <code>true</code>, if value is set
     */
    public boolean isSetValue() {
        return StringHelper.isNotEmpty(getValue());
    }

    /**
     * Check whether code space is set
     * 
     * @return <code>true</code>, if code space is set
     */
    public boolean isSetCodeSpace() {
        return StringHelper.isNotEmpty(getCodeSpace());
    }

    @Override
    public String toString() {
        return String.format("CodeType [value=%s, codeSpace=%s]", getValue(), getCodeSpace());
    }
}
