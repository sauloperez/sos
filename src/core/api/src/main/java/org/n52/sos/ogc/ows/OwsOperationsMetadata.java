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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.n52.sos.util.CollectionHelper;

/**
 * @since 4.0.0
 * 
 */
public class OwsOperationsMetadata {
    private SortedSet<OwsOperation> operations;

    private SortedMap<String, List<OwsParameterValue>> commonValues;

    private OwsExtendedCapabilities extendedCapabilities;

    public SortedSet<OwsOperation> getOperations() {
        return Collections.unmodifiableSortedSet(operations);
    }

    public void setOperations(Collection<OwsOperation> operations) {
        this.operations = operations == null ? null : new TreeSet<OwsOperation>(operations);
    }

    public SortedMap<String, List<OwsParameterValue>> getCommonValues() {
        return Collections.unmodifiableSortedMap(commonValues);
    }

    public void addOperation(OwsOperation operation) {
        if (operations == null) {
            operations = new TreeSet<OwsOperation>();
        }
        operations.add(operation);
    }

    public void addCommonValue(String parameterName, OwsParameterValue value) {
        if (commonValues == null) {
            commonValues = new TreeMap<String, List<OwsParameterValue>>();
        }
        List<OwsParameterValue> values = commonValues.get(parameterName);
        if (values == null) {
            values = new LinkedList<OwsParameterValue>();
            commonValues.put(parameterName, values);
        }
        values.add(value);
    }

    public boolean isSetCommonValues() {
        return !CollectionHelper.isEmpty(getCommonValues());
    }

    public boolean isSetOperations() {
        return !CollectionHelper.isEmpty(getOperations());
    }

    public boolean isEmpty() {
        return CollectionHelper.isEmpty(getOperations());
    }

    /**
     * @return the extendedCapabilities
     */
    public OwsExtendedCapabilities getExtendedCapabilities() {
        return extendedCapabilities;
    }

    /**
     * @param extendedCapabilities
     *            the extendedCapabilities to set
     */
    public void setExtendedCapabilities(OwsExtendedCapabilities extendedCapabilities) {
        this.extendedCapabilities = extendedCapabilities;
    }

    public boolean isSetExtendedCapabilities() {
        return getExtendedCapabilities() != null;
    }
}
