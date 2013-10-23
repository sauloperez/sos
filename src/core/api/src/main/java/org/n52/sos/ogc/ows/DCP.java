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

package org.n52.sos.ogc.ows;

import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.base.Objects;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class DCP {
    private final String url;

    private final SortedSet<Constraint> constraints;

    public DCP(String url) {
        this(url, (Set<Constraint>) null);
    }

    public DCP(String url, Set<Constraint> constraints) {
        this.url = url;
        if (constraints == null) {
            this.constraints = new TreeSet<Constraint>();
        } else {
            this.constraints = new TreeSet<Constraint>(constraints);
        }
    }

    public DCP(String url, Constraint constraint) {
        this(url, Collections.singleton(constraint));
    }

    public String getUrl() {
        return url;
    }

    public Set<Constraint> getConstraints() {
        return Collections.unmodifiableSet(constraints);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUrl(), getConstraints());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DCP) {
            DCP o = (DCP) obj;
            return Objects.equal(getUrl(), o.getUrl()) && Objects.equal(getConstraints(), o.getConstraints());
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("DCP[]", getUrl(), getConstraints());
    }

}
