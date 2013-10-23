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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @param <T>
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 * 
 */
public class Activatable<T> {
    private T object;

    private boolean active;

    public Activatable(T object) {
        this(object, true);
    }

    public Activatable(T object, boolean active) {
        this.object = object;
        this.active = active;
    }

    /**
     * @return isActive() ? getInternal() : null
     */
    public T get() {
        return isActive() ? getInternal() : null;
    }

    public T getInternal() {
        return object;
    }

    public boolean isActive() {
        return active;
    }

    public Activatable<T> setActive(boolean active) {
        this.active = active;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getInternal(), isActive());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Activatable) {
            Activatable<?> a = (Activatable) obj;
            return Objects.equal(isActive(), a.isActive()) && Objects.equal(getInternal(), a.getInternal());

        }
        return false;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(getClass()).add("object", getInternal()).add("active", isActive()).toString();
    }

    public static <K, V> Map<K, V> filter(Map<K, Activatable<V>> map) {
        if (map == null) {
            return Maps.newHashMap();
        }
        Map<K, V> filtered = Maps.newHashMapWithExpectedSize(map.size());
        for (K k : map.keySet()) {
            if (map.get(k) != null && map.get(k).get() != null) {
                filtered.put(k, map.get(k).get());
            }
        }
        return filtered;
    }

    public static <E> Set<E> filter(Set<Activatable<E>> set) {
        if (set == null) {
            return Sets.newHashSet();
        }
        Set<E> filtered = new HashSet<E>(set.size());
        for (Activatable<E> a : set) {
            if (a.isActive()) {
                filtered.add(a.get());
            }
        }
        return filtered;
    }

    public static <E> Set<E> unfiltered(Set<Activatable<E>> set) {
        if (set == null) {
            return Sets.newHashSet();
        }
        Set<E> unfiltered = new HashSet<E>(set.size());
        for (Activatable<E> a : set) {
            unfiltered.add(a.getInternal());
        }
        return unfiltered;
    }

    public static <K, V> Map<K, V> unfiltered(Map<K, Activatable<V>> map) {
        if (map == null) {
            return Maps.newHashMap();
        }
        Map<K, V> filtered = new HashMap<K, V>(map.size());
        for (K k : map.keySet()) {
            if (map.get(k) != null) {
                filtered.put(k, map.get(k).getInternal());
            }
        }
        return filtered;
    }

    public static <E> Set<Activatable<E>> from(Set<E> set) {
        Set<Activatable<E>> a = new HashSet<Activatable<E>>(set.size());
        for (E t : set) {
            a.add(from(t));
        }
        return a;
    }

    public static <T> Activatable<T> from(T t) {
        return from(t, true);
    }

    public static <T> Activatable<T> from(T t, boolean active) {
        return new Activatable<T>(t, active);
    }
}
