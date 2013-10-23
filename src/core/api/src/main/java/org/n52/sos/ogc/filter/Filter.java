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

import org.n52.sos.util.StringHelper;

/**
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.0
 * 
 * @param <T>
 *            operator type, e.g.
 *            {@link org.n52.sos.ogc.filter.FilterConstants.TimeOperator},
 *            {@link org.n52.sos.ogc.filter.FilterConstants.SpatialOperator}
 */
public abstract class Filter<T> {

    /**
     * Value reference
     */
    private String valueReference;

    /**
     * constructor
     */
    public Filter() {
    }

    /**
     * @param valueReference
     */
    public Filter(String valueReference) {
        super();
        this.valueReference = valueReference;
    }

    /**
     * Get value reference
     * 
     * @return value reference
     */
    public String getValueReference() {
        return valueReference;
    }

    /**
     * Set value reference
     * 
     * @param valueReference
     *            value reference
     * @return This filter
     */
    public Filter<T> setValueReference(String valueReference) {
        this.valueReference = valueReference;
        return this;
    }

    /**
     * Check if valueReference is set
     * 
     * @return <code>true</code>, if valueReference is set
     */
    public boolean hasValueReference() {
        return StringHelper.isNotEmpty(getValueReference());
    }

    /**
     * Get filter operator
     * 
     * @return filter operator
     */
    public abstract T getOperator();

    /**
     * Set filter operator
     * 
     * @param operator
     *            filter operator
     */
    public abstract Filter<T> setOperator(T operator);

    /**
     * Check if operator is set
     * 
     * @return <code>true</code>, if operator is set
     */
    public boolean isSetOperator() {
        return getOperator() != null;
    }
}
