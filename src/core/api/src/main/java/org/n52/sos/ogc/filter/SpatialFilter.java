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
package org.n52.sos.ogc.filter;

import org.n52.sos.ogc.filter.FilterConstants.SpatialOperator;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Spatial filter class
 * 
 * @since 4.0.0
 * 
 */
public class SpatialFilter extends Filter<SpatialOperator> {

    /**
     * Spatial filter operator
     */
    private SpatialOperator operator;

    /**
     * Filter geometry
     */
    private Geometry geometry;

    /**
     * default constructor
     */
    public SpatialFilter() {
        super();
    }

    /**
     * constructor
     * 
     * @param operatorp
     *            Spatial operator
     * @param geomWKTp
     *            Filter geometry
     * @param valueReferencep
     *            Filter valueReference
     */
    public SpatialFilter(SpatialOperator operatorp, Geometry geomWKTp, String valueReferencep) {
        super(valueReferencep);
        this.operator = operatorp;
        this.geometry = geomWKTp;
    }

    @Override
    public SpatialOperator getOperator() {
        return operator;
    }

    @Override
    public SpatialFilter setOperator(SpatialOperator operator) {
        this.operator = operator;
        return this;
    }

    /**
     * Get SRID
     * 
     * @return SRID
     */
    public int getSrid() {
        return geometry.getSRID();
    }

    /**
     * Get filter geometry
     * 
     * @return filter geometry
     */
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * Set filter geometry
     * 
     * @param geometry
     *            filter geometry
     * @return This filter
     */
    public SpatialFilter setGeometry(Geometry geometry) {
        this.geometry = geometry;
       return this;
    }

    @Override
    public String toString() {
        return "Spatial filter: " + operator + " " + geometry;
    }

}
