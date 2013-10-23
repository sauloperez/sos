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

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public abstract class SweAbstractUomType<T> extends SweAbstractSimpleType<T> {

    /**
     * unit of measurement
     */
    private String uom;

    /**
     * Get unit of measurement
     * 
     * @return the uom
     */
    public String getUom() {
        return uom;
    }

    /**
     * Set unit of measurement
     * 
     * @param uom
     *            the uom to set
     * @return This SweAbstractUomType
     */
    public SweAbstractUomType<T> setUom(final String uom) {
        this.uom = uom;
        return this;
    }

    /**
     * 
     * @return <tt>true</tt>, if the uom is set and not an empty string,<br>
     *         <tt>false</tt>, else.
     */
    public boolean isSetUom() {
        return uom != null && !uom.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("%s [simpleType=%s, value=%s, uom=%s, quality=%s]", getClass().getSimpleName(),
                getDataComponentType(), getValue(), getUom(), getQuality());
    }
}
