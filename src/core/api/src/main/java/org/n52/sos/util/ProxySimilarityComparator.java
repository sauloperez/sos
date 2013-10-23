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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * TODO JavaDoc
 * 
 * @param <T>
 *            the type to compare
 * @param <K>
 *            the similarity type of {@code T}
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public abstract class ProxySimilarityComparator<T, K extends Similar<K>> implements Comparator<T> {
    private final SimilarityComparator<K> comp;

    public ProxySimilarityComparator(K ref) {
        this.comp = new SimilarityComparator<K>(ref);
    }

    @Override
    public int compare(T o1, T o2) {
        int compResult = comp.compare(Collections.min(getSimilars(o1), comp), Collections.min(getSimilars(o2), comp));
        // check inheritance hierarchy if key matches are equal
        if (compResult == 0) {
            if (o1.getClass().isAssignableFrom(o2.getClass())) {
                return 1;
            } else if (o2.getClass().isAssignableFrom(o1.getClass())) {
                return -1;
            }
        }
        return compResult;
    }

    protected abstract Collection<K> getSimilars(T t);
}
