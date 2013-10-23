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
package org.n52.sos.w3c;

import org.n52.sos.util.Comparables;

import com.google.common.base.Objects;

/**
 * Class represents a XML schema location with namespace and schema fileURL.
 * 
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class SchemaLocation implements Comparable<SchemaLocation> {
    private final String namespace;

    private final String schemaFileUrl;

    private final String schemaLocationString;

    /**
     * Constructor
     * 
     * @param namespace
     *            Namespace
     * @param schemaFileUrl
     *            Schema file URL
     */
    public SchemaLocation(String namespace, String schemaFileUrl) {
        this.namespace = namespace;
        this.schemaFileUrl = schemaFileUrl;
        this.schemaLocationString = new StringBuilder().append(namespace).append(' ').append(schemaFileUrl).toString();
    }

    /**
     * Get namespace of schema location
     * 
     * @return namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Get schema file URL
     * 
     * @return schema file URL
     */
    public String getSchemaFileUrl() {
        return schemaFileUrl;
    }

    /**
     * @return Schema location string
     */
    public String getSchemaLocationString() {
        return schemaLocationString;
    }

    @Override
    public int compareTo(SchemaLocation o) {
        return Comparables.chain(o).compare(getNamespace(), o.getNamespace())
                .compare(getSchemaFileUrl(), o.getSchemaFileUrl()).result();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getNamespace(), getSchemaFileUrl());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == getClass()) {
            SchemaLocation other = (SchemaLocation) obj;
            return Objects.equal(getNamespace(), other.getNamespace())
                    && Objects.equal(getSchemaFileUrl(), other.getSchemaFileUrl());
        }
        return false;
    }
}