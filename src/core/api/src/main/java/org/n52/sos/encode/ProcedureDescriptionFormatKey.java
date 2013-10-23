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
package org.n52.sos.encode;

import org.n52.sos.service.operator.ServiceOperatorKey;

import com.google.common.base.Objects;

/**
 * @since 4.0.0
 * 
 */
public class ProcedureDescriptionFormatKey {
    private ServiceOperatorKey serviceOperatorKeyType;

    private String procedureDescriptionFormat;

    public ProcedureDescriptionFormatKey(ServiceOperatorKey serviceOperatorKeyType, String responseFormat) {
        this.serviceOperatorKeyType = serviceOperatorKeyType;
        this.procedureDescriptionFormat = responseFormat;
    }

    public ProcedureDescriptionFormatKey() {
        this(null, null);
    }

    public ServiceOperatorKey getServiceOperatorKeyType() {
        return serviceOperatorKeyType;
    }

    public void setServiceOperatorKeyType(ServiceOperatorKey serviceOperatorKeyType) {
        this.serviceOperatorKeyType = serviceOperatorKeyType;
    }

    public String getProcedureDescriptionFormat() {
        return procedureDescriptionFormat;
    }

    public void setProcedureDescriptionFormat(String responseFormat) {
        this.procedureDescriptionFormat = responseFormat;
    }

    public String getService() {
        return getServiceOperatorKeyType() != null ? getServiceOperatorKeyType().getService() : null;
    }

    public String getVersion() {
        return getServiceOperatorKeyType() != null ? getServiceOperatorKeyType().getVersion() : null;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getServiceOperatorKeyType(), getProcedureDescriptionFormat());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProcedureDescriptionFormatKey) {
            ProcedureDescriptionFormatKey o = (ProcedureDescriptionFormatKey) obj;
            return Objects.equal(getServiceOperatorKeyType(), o.getServiceOperatorKeyType())
                    && Objects.equal(getProcedureDescriptionFormat(), o.getProcedureDescriptionFormat());
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s[serviceOperatorKeyType=%s, procedureDescriptionFormat=%s]", getClass()
                .getSimpleName(), getServiceOperatorKeyType(), getProcedureDescriptionFormat());
    }
}
