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
package org.n52.sos.encode.json;

import org.n52.sos.encode.EncoderKey;
import org.n52.sos.util.ClassHelper;

import com.google.common.base.Objects;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class JSONEncoderKey implements EncoderKey {
    private final Class<?> type;

    public JSONEncoderKey(Class<?> type) {
        this.type = type;
    }

    @Override
    public int getSimilarity(EncoderKey key) {
        if (key instanceof JSONEncoderKey) {
            JSONEncoderKey jsonKey = (JSONEncoderKey) key;
            return ClassHelper.getSimiliarity(getType() != null ? getType() : Object.class,
                    jsonKey.getType() != null ? jsonKey.getType() : Object.class);
        }
        return -1;
    }

    public Class<?> getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getType());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == getClass()) {
            JSONEncoderKey key = (JSONEncoderKey) obj;
            return Objects.equal(getType(), key.getType());
        }
        return false;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(getClass()).add("type", getType()).toString();
    }

}
