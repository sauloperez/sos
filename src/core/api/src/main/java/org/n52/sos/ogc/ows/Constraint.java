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
import java.util.List;

import com.google.common.base.Objects;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class Constraint implements Comparable<Constraint> {
    private final String name;

    private final List<OwsParameterValue> values;

    public Constraint(String name, List<OwsParameterValue> values) {
        if (name == null) {
            throw new NullPointerException();
        }
        if (values == null) {
            throw new NullPointerException();
        }
        this.name = name;
        this.values = values;
    }

    public Constraint(String name, OwsParameterValue value) {
        this(name, Collections.singletonList(value));
    }

    public String getName() {
        return name;
    }

    public List<OwsParameterValue> getValues() {
        return Collections.unmodifiableList(values);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName(), getValues());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Constraint) {
            Constraint c = (Constraint) obj;
            return Objects.equal(getName(), c.getName()) && Objects.equal(getValues(), c.getValues());
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Constraint[name=%s, values=%s]", getName(), getValues());
    }

    @Override
    public int compareTo(Constraint o) {
        return getName().compareTo(o.getName());
    }
}
