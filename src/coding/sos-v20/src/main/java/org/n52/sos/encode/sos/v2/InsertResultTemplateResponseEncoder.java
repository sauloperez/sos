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
package org.n52.sos.encode.sos.v2;

import java.util.Set;

import net.opengis.sos.x20.InsertResultTemplateResponseDocument;
import net.opengis.sos.x20.InsertResultTemplateResponseType;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.response.InsertResultTemplateResponse;
import org.n52.sos.w3c.SchemaLocation;

import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class InsertResultTemplateResponseEncoder extends AbstractSosResponseEncoder<InsertResultTemplateResponse> {
    public InsertResultTemplateResponseEncoder() {
        super(Sos2Constants.Operations.InsertResultTemplate.name(), InsertResultTemplateResponse.class);
    }

    @Override
    protected XmlObject create(InsertResultTemplateResponse response) throws OwsExceptionReport {
        InsertResultTemplateResponseDocument doc =
                InsertResultTemplateResponseDocument.Factory.newInstance(getXmlOptions());
        InsertResultTemplateResponseType irtr = doc.addNewInsertResultTemplateResponse();
        irtr.setAcceptedTemplate(response.getAcceptedTemplate());
        return doc;
    }

    @Override
    protected Set<SchemaLocation> getConcreteSchemaLocations() {
        return Sets.newHashSet(Sos2Constants.SOS_INSERT_RESULT_TEMPLATE_SCHEMA_LOCATION);
    }
}
