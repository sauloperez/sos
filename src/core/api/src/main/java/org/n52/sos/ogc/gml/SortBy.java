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

import org.n52.sos.ogc.gml.GmlConstants.SortingOrder;

/**
 * class represents the gml:sortByType
 * 
 * @since 4.0.0
 */
public class SortBy {

    /** name of the property, by which should be sorted */
    private String property;

    /**
     * order of the sorting (currently only ascending (ASC) or descending (DESC)
     */
    private SortingOrder order;

    /**
     * constructor
     * 
     * @param propertyp
     *            name of property, by which should be sorted
     * @param orderp
     *            sorting order (currently only ascending ('ASC') or descending
     *            ('DESC')
     */
    public SortBy(String propertyp, SortingOrder orderp) {
        this.property = propertyp;
        this.order = orderp;
    }

    /**
     * default constructor
     */
    public SortBy() {
    }

    /**
     * 
     * @return Returns String representation with values of this object
     */
    public String toString() {
        return String.format("Sort by [property=%s, order=%]", getProperty(), getOrder());
    }

    /**
     * Get order
     * 
     * @return the order
     */
    public SortingOrder getOrder() {
        return order;
    }

    /**
     * Set ordering
     * 
     * @param order
     *            the order to set
     */
    public void setOrder(SortingOrder order) {
        this.order = order;
    }

    /**
     * Get property
     * 
     * @return the property
     */
    public String getProperty() {
        return property;
    }

    /**
     * Set property
     * 
     * @param property
     *            the property to set
     */
    public void setProperty(String property) {
        this.property = property;
    }
}
