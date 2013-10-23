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
package org.n52.sos.util;

import java.util.Comparator;

/**
 * TODO JavaDoc
 * 
 * @param <T>
 *            the type
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class SimilarityComparator<T extends Similar<T>> implements Comparator<T> {
    private final T ref;

    public SimilarityComparator(T ref) {
        this.ref = ref;
    }

    @Override
    public int compare(T o1, T o2) {
        if (o1 == o2) {
            return 0;
        }
        // FIXME this conflicts with the contract of compare
        if (o1 == null || o2 == null) {
            return -1;
        }
        int s1 = o1.getSimilarity(ref);
        int s2 = o2.getSimilarity(ref);
        if (s1 == s2) {
            if (o1.getClass().isAssignableFrom(o2.getClass())) {
                return 1;
            } else if (o2.getClass().isAssignableFrom(o1.getClass())) {
                return -1;
            }
        }
        if (s1 == 0) {
            return -1;
        }
        if (s2 == 0) {
            return 1;
        }
        if (s1 < 0) {
            return s2 < 0 ? 0 : 1;
        } else if (s2 < 0 || s1 < s2) {
            return -1;
        } else {
            return 1;
        }
    }
}
