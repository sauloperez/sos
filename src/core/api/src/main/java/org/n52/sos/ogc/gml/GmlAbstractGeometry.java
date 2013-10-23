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

import com.vividsolutions.jts.geom.Geometry;

/**
 * A class that represents a gml:AbstractGeometry (PointType, ...).
 * 
 * @since 4.0.0
 * 
 */
public class GmlAbstractGeometry extends AbstractFeature {

    /**
     * serial number
     */
    private static final long serialVersionUID = -2997129475905560236L;

    /**
     * Geometry
     */
    private Geometry geometry;

    /**
     * constructor
     */
    public GmlAbstractGeometry() {
    }

    /**
     * constructor
     * 
     * @param id
     *            GML id
     */
    public GmlAbstractGeometry(String id) {
        setGmlId(id);
    }

    /**
     * Get geometry
     * 
     * @return the geometry
     */
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * set geometry
     * 
     * @param geometry
     *            the geometry to set
     */
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    /**
     * Is geometry set
     * 
     * @return true if geometry is set
     */
    public boolean isSetGeometry() {
        return this.geometry != null;
    }
}
