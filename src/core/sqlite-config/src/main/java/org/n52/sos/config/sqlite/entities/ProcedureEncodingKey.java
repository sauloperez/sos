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
package org.n52.sos.config.sqlite.entities;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.n52.sos.encode.ProcedureDescriptionFormatKey;

/**
 * TODO JavaDoc
 *
 * @since 4.0.0
 */
@Embeddable
public class ProcedureEncodingKey implements Serializable {
    private static final long serialVersionUID = -7906820756389374560L;
    private String service;
    private String version;
    private String procedureDescriptionFormat;

    public ProcedureEncodingKey(String service, String version, String procedureDescriptionFormat) {
        this.service = service;
        this.version = version;
        this.procedureDescriptionFormat = procedureDescriptionFormat;
    }

    public ProcedureEncodingKey(ProcedureDescriptionFormatKey key) {
        this(key.getService(), key.getVersion(), key.getProcedureDescriptionFormat());
    }

    public ProcedureEncodingKey() {
        this(null, null, null);
    }

    public String getService() {
        return service;
    }

    public ProcedureEncodingKey setService(String service) {
        this.service = service;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public ProcedureEncodingKey setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getEncoding() {
        return procedureDescriptionFormat;
    }

    public ProcedureEncodingKey setProcedureDescriptionFormat(String procedureDescriptionFormat) {
        this.procedureDescriptionFormat = procedureDescriptionFormat;
        return this;
    }
}
