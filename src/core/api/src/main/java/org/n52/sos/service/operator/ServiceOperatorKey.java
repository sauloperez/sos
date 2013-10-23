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
package org.n52.sos.service.operator;

import org.n52.sos.util.Comparables;

import com.google.common.base.Objects;

/**
 * @since 4.0.0
 * 
 */
public class ServiceOperatorKey implements Comparable<ServiceOperatorKey> {
    private final String service;

    private final String version;

    public ServiceOperatorKey(String service, String version) {
        this.service = service;
        this.version = version;
    }

    public String getService() {
        return service;
    }

    public boolean hasService() {
        return getService() != null;
    }

    public String getVersion() {
        return version;
    }

    public boolean hasVersion() {
        return getVersion() != null;
    }

    @Override
    public int compareTo(ServiceOperatorKey other) {
        return Comparables.chain(other).compare(getService(), other.getService())
                .compare(getVersion(), other.getVersion()).result();
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o.getClass() == getClass()) {
            ServiceOperatorKey other = (ServiceOperatorKey) o;
            return Objects.equal(getService(), other.getService()) && Objects.equal(getVersion(), other.getVersion());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getService(), getVersion());
    }

    @Override
    public String toString() {
        return String.format("ServiceOperatorKeyType[service=%s, version=%s]", getService(), getVersion());
    }
}
