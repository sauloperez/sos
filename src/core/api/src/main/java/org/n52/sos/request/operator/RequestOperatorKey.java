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
package org.n52.sos.request.operator;

import org.n52.sos.service.operator.ServiceOperatorKey;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;

/**
 * @since 4.0.0
 * 
 */
public class RequestOperatorKey implements Comparable<RequestOperatorKey> {
    private final ServiceOperatorKey sok;

    private final String operationName;

    public RequestOperatorKey(ServiceOperatorKey sok, String operationName) {
        this.sok = sok;
        this.operationName = operationName;
    }

    public RequestOperatorKey(String service, String version, String operationName) {
        this(new ServiceOperatorKey(service, version), operationName);
    }

    public ServiceOperatorKey getServiceOperatorKey() {
        return sok;
    }

    public String getService() {
        return sok == null ? null : sok.getService();
    }

    public String getVersion() {
        return sok == null ? null : sok.getVersion();
    }

    public String getOperationName() {
        return operationName;
    }

    @Override
    public int compareTo(RequestOperatorKey o) {
        Preconditions.checkNotNull(o);
        return ComparisonChain.start().compare(getServiceOperatorKey(), o.getServiceOperatorKey())
                .compare(getOperationName(), o.getOperationName()).result();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == getClass()) {
            RequestOperatorKey o = (RequestOperatorKey) obj;
            return Objects.equal(getServiceOperatorKey(), o.getServiceOperatorKey())
                    && Objects.equal(getOperationName(), o.getOperationName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getServiceOperatorKey(), getOperationName());
    }

    @Override
    public String toString() {
        return String.format("%s[serviceOperatorKeyType=%s, operationName=%s]", getClass().getSimpleName(),
                getServiceOperatorKey(), getOperationName());
    }
}
