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
package org.n52.sos.ogc.swe.simpleType;

import org.n52.sos.ogc.swe.SweConstants.SweDataComponentType;

/**
 * SOS internal representation of SWE simpleType quantity
 * 
 * @author Carsten Hollmann
 * @since 4.0.0
 */
public class SweQuantity extends SweAbstractUomType<Double> {

    /**
     * axis ID
     */
    private String axisID;

    /**
     * value
     */
    private Double value;

    /**
     * constructor
     */
    public SweQuantity() {
    }

    /**
     * Get axis ID
     * 
     * @return the axisID
     */
    public String getAxisID() {
        return axisID;
    }

    /**
     * set axis ID
     * 
     * @param axisID
     *            the axisID to set
     * @return This SweQuantity
     */
    public SweQuantity setAxisID(final String axisID) {
        this.axisID = axisID;
        return this;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public SweQuantity setValue(final Double value) {
        this.value = value;
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 97;
        int hash = 7;
        hash = prime * hash + super.hashCode();
        hash = prime * hash + (axisID != null ? axisID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SweQuantity other = (SweQuantity) obj;
        if ((getAxisID() == null) ? (other.getAxisID() != null) : !getAxisID().equals(other.getAxisID())) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public String getStringValue() {
        if (isSetValue()) {
            return Double.toString(value.intValue());
        }
        return null;
    }

    @Override
    public boolean isSetValue() {
        return value != null;
    }

    public boolean isSetAxisID() {
        return axisID != null && !axisID.isEmpty();
    }

    @Override
    public SweDataComponentType getDataComponentType() {
        return SweDataComponentType.Quantity;
    }

    @Override
    public SweQuantity setUom(String uom) {
        return (SweQuantity) super.setUom(uom);
    }

    @Override
    public SweQuantity setQuality(SweQuality quality) {
        return (SweQuantity) super.setQuality(quality);
    }
}
