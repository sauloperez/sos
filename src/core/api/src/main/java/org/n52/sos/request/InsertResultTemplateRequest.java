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
package org.n52.sos.request;

import org.joda.time.DateTime;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosResultEncoding;
import org.n52.sos.ogc.sos.SosResultStructure;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.StringHelper;

import com.google.common.base.Strings;

/**
 * @since 4.0.0
 */
public class InsertResultTemplateRequest extends AbstractServiceRequest {

    private String identifier;

    private OmObservationConstellation observationTemplate;

    private SosResultStructure resultStructure;

    private SosResultEncoding resultEncoding;

    @Override
    public String getOperationName() {
        return Sos2Constants.Operations.InsertResultTemplate.name();
    }

    public String getIdentifier() {
        if (Strings.isNullOrEmpty(identifier)) {
            StringBuilder builder = new StringBuilder();
            builder.append(getObservationTemplate().toString());
            builder.append(new DateTime().getMillis());
            identifier = JavaHelper.generateID(builder.toString());
        }
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isSetIdentifier() {
        return StringHelper.isNotEmpty(getIdentifier());
    }

    public OmObservationConstellation getObservationTemplate() {
        return observationTemplate;
    }

    public void setObservationTemplate(OmObservationConstellation observationConstellation) {
        this.observationTemplate = observationConstellation;
    }

    public boolean isSetObservatioTenmplate() {
        return getObservationTemplate() != null && !getObservationTemplate().isEmpty();
    }

    public SosResultStructure getResultStructure() {
        return resultStructure;
    }

    public void setResultStructure(SosResultStructure resultStructure) {
        this.resultStructure = resultStructure;
    }

    public boolean isSetResultStructure() {
        return getResultStructure() != null && !getResultStructure().isEmpty();
    }

    public SosResultEncoding getResultEncoding() {
        return resultEncoding;
    }

    public void setResultEncoding(SosResultEncoding resultEncoding) {
        this.resultEncoding = resultEncoding;
    }

    public boolean isSetResultEncoding() {
        return getResultEncoding() != null && !getResultEncoding().isEmpty();
    }

}
