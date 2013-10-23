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

import org.n52.sos.util.http.MediaType;

import com.google.common.base.Objects;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class ExceptionEncoderKey implements EncoderKey {
    private final MediaType mediaType;

    public ExceptionEncoderKey(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    @Override
    public int getSimilarity(EncoderKey key) {
        return equals(key) ? 0 : -1;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getMediaType());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && getClass().equals(obj.getClass())) {
            ExceptionEncoderKey key = (ExceptionEncoderKey) obj;
            return key.getMediaType().equals(getMediaType());
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("ExceptionEncoderKey[mediaType=%s]", getMediaType());
    }
}
