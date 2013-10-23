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

/**
 * Nil template value for observation
 * @since 4.0.0
 * 
 */

public class NilTemplateValue implements Value<String> {

    /**
     * serial number
     */
    private static final long serialVersionUID = -3751934124688213692L;

    /**
     * Unit of measure
     */
    private String unit;

    @Override
    public void setValue(String value) {
    }

    @Override
    public String getValue() {
        return "template";
    }

    @Override
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return String.format("NilTemplateValue [value=%s, unit=%s]", getValue(), getUnit());
    }

    @Override
    public boolean isSetValue() {
        return true;
    }

    @Override
    public boolean isSetUnit() {
        return false;
    }

}
