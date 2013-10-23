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

import java.util.Collections;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class CompositeSimilar<T extends Similar<T>> implements Similar<T> {
    private final Set<T> similars;

    public CompositeSimilar(Iterable<T> similars) {
        this.similars = Sets.newHashSet(similars);
    }

    protected Set<T> getSimilars() {
        return Collections.unmodifiableSet(this.similars);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == getClass()) {
            @SuppressWarnings(value = "unchecked")
            CompositeSimilar<T> key = (CompositeSimilar) obj;
            return key.matches(getSimilars()) && matches(key.getSimilars());
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", getClass().getSimpleName(), Joiner.on(", ").join(similars));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(similars.toArray());
    }

    public boolean matches(Set<T> toTest) {
        return toTest == null ? similars.isEmpty() : toTest.containsAll(similars);
    }

    @Override
    public int getSimilarity(T key) {
        if (key != null && key.getClass() == getClass()) {
            CompositeSimilar<T> cek = (CompositeSimilar<T>) key;
            if (cek.getSimilars().size() != similars.size()) {
                return -1;
            }
            int similarity = 0;
            for (T k1 : similars) {
                int s = -1;
                for (T k2 : cek.getSimilars()) {
                    int ks = k1.getSimilarity(k2);
                    s = (s < 0) ? ks : Math.min(s, ks);
                    if (s == 0) {
                        break;
                    }
                }
                if (s < 0) {
                    return -1;
                } else {
                    similarity += s;
                }
            }
            return similarity;
        }
        return -1;
    }
}
