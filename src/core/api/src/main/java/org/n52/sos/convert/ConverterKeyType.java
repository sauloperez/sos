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
package org.n52.sos.convert;

import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;

import com.google.common.base.Strings;

/**
 * @since 4.0.0
 * 
 */
public class ConverterKeyType implements Comparable<ConverterKeyType> {

    private String fromNamespace;

    private String toNamespace;

    public ConverterKeyType(String fromNamespace, String toNamespace) {
        this.fromNamespace = fromNamespace;
        this.toNamespace = toNamespace;
    }

    public String getFromNamespace() {
        return fromNamespace;
    }

    public String getToNamespace() {
        return toNamespace;
    }

    @Override
    public int compareTo(ConverterKeyType o) {
        if (o instanceof ConverterKeyType) {
            if (checkParameter(fromNamespace, o.getFromNamespace())
                    && checkParameter(toNamespace, o.getFromNamespace())) {
                return 0;
            }
            return 1;
        }
        return -1;
    }

    @Override
    public boolean equals(Object paramObject) {
        if (paramObject instanceof ConverterKeyType) {
            ConverterKeyType toCheck = (ConverterKeyType) paramObject;
            return (checkParameter(fromNamespace, toCheck.fromNamespace) && checkParameter(toNamespace,
                    toCheck.toNamespace));
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 7;
        hash = prime * hash + (this.fromNamespace != null ? this.fromNamespace.hashCode() : 0);
        hash = prime * hash + (this.toNamespace != null ? this.toNamespace.hashCode() : 0);
        return hash;
    }

    private boolean checkParameter(String localParameter, String parameterToCheck) {
        if (localParameter == null && parameterToCheck == null) {
            return true;
        }
        return localParameter != null && parameterToCheck != null && localParameter.equals(parameterToCheck);
    }

    @Override
    public String toString() {
        return String.format("%s[from=%s, to=%s]", getClass().getSimpleName(), fromNamespace, toNamespace);
    }

}
