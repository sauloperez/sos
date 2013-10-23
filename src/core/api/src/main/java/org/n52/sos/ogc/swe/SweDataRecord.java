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
package org.n52.sos.ogc.swe;

import org.n52.sos.ogc.swe.SweConstants.SweDataComponentType;

/**
 * SOS internal representation of SWE dataRecord
 * 
 * @since 4.0.0
 */
public class SweDataRecord extends SweAbstractDataRecord {

    @Override
    public SweDataComponentType getDataComponentType() {
        return SweDataComponentType.DataRecord;
    }

    @Override
    public SweDataRecord addField(final SweField field) {
        return (SweDataRecord) super.addField(field);
    }

    @Override
    public int hashCode() {
        final int prime = 42;
        int hash = 7;
        hash = prime * hash + super.hashCode();
        hash = prime * hash + (getDataComponentType() != null ? getDataComponentType().hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return String.format("SweDataRecord [fields=%s, definition=%s, label=%s, identifier=%s, xml=%s]", getFields(),
                getDefinition(), getLabel(), getIdentifier(), getXml());
    }
}
