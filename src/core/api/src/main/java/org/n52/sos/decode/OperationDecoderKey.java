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
package org.n52.sos.decode;

import org.n52.sos.coding.OperationKey;
import org.n52.sos.util.http.MediaType;

import com.google.common.base.Objects;

/**
 * @since 4.0.0
 * 
 */
public class OperationDecoderKey extends OperationKey implements DecoderKey {
    private final MediaType contentType;

    public OperationDecoderKey(String service, String version, String operation, MediaType contentType) {
        super(service, version, operation);
        this.contentType = contentType;
    }

    public OperationDecoderKey(String service, String version, Enum<?> operation, MediaType contentType) {
        super(service, version, operation);
        this.contentType = contentType;
    }

    public OperationDecoderKey(OperationKey key, MediaType contentType) {
        super(key);
        this.contentType = contentType;
    }

    public MediaType getContentType() {
        return contentType;
    }

    @Override
    public String toString() {
        return String.format("%s[service=%s, version=%s, operation=%s, contentType=%s]", getClass().getSimpleName(),
                getService(), getVersion(), getOperation(), getContentType());
    }

    @Override
    public int getSimilarity(DecoderKey key) {
        return equals(key) ? 0 : -1;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), getContentType());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && getClass() == obj.getClass()) {
            final OperationDecoderKey o = (OperationDecoderKey) obj;
            return Objects.equal(getService(), o.getService()) && Objects.equal(getVersion(), o.getVersion())
                    && Objects.equal(getOperation(), o.getOperation()) && getContentType() != null
                    && getContentType().isCompatible(o.getContentType());
        }
        return false;
    }
}
