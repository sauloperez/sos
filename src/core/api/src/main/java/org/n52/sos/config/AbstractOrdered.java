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
package org.n52.sos.config;

/**
 * Abstract, generic implementation of {@code Ordered}.
 * <p/>
 * 
 * @param <T>
 *            the type of the class extending this class
 *            <p/>
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public abstract class AbstractOrdered<T extends Ordered<T>> implements Ordered<T> {

    private float order;

    @Override
    public float getOrder() {
        return this.order;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T setOrder(float order) {
        this.order = order;
        return (T) this;
    }

    @Override
    public int compareTo(Ordered<?> t) {
        int compare = Float.compare(getOrder(), t.getOrder());
        if (compare == 0 && t instanceof AbstractOrdered) {
            AbstractOrdered<?> ao = (AbstractOrdered) t;
            if (getSuborder() == null) {
                return 1;
            } else if (ao.getSuborder() == null) {
                return -1;
            } else {
                return getSuborder().compareTo(ao.getSuborder());
            }
        }
        return compare;
    }

    protected abstract String getSuborder();
}
