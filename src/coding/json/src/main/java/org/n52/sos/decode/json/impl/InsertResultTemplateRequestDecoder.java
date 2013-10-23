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
package org.n52.sos.decode.json.impl;

import static org.n52.sos.coding.json.JSONConstants.BLOCK_SEPARATOR;
import static org.n52.sos.coding.json.JSONConstants.DECIMAL_SEPARATOR;
import static org.n52.sos.coding.json.JSONConstants.IDENTIFIER;
import static org.n52.sos.coding.json.JSONConstants.OBSERVATION_TEMPLATE;
import static org.n52.sos.coding.json.JSONConstants.OFFERING;
import static org.n52.sos.coding.json.JSONConstants.RESULT_ENCODING;
import static org.n52.sos.coding.json.JSONConstants.RESULT_STRUCTURE;
import static org.n52.sos.coding.json.JSONConstants.TOKEN_SEPARATOR;

import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.decode.json.AbstractSosRequestDecoder;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosResultEncoding;
import org.n52.sos.ogc.sos.SosResultStructure;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.encoding.SweTextEncoding;
import org.n52.sos.request.InsertResultTemplateRequest;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @since 4.0.0
 * 
 */
public class InsertResultTemplateRequestDecoder extends AbstractSosRequestDecoder<InsertResultTemplateRequest> {
    private final ObservationDecoder observationDecoder = new ObservationDecoder();

    public InsertResultTemplateRequestDecoder() {
        super(InsertResultTemplateRequest.class, SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                Sos2Constants.Operations.InsertResultTemplate);
    }

    @Override
    public String getSchemaURI() {
        return SchemaConstants.Request.INSERT_RESULT_TEMPLATE;
    }

    @Override
    public InsertResultTemplateRequest decodeRequest(JsonNode node) throws OwsExceptionReport {
        InsertResultTemplateRequest irtr = new InsertResultTemplateRequest();
        if (!node.path(IDENTIFIER).isMissingNode()) {
            irtr.setIdentifier(node.path(IDENTIFIER).textValue());
        }
        irtr.setObservationTemplate(parseObservationTemplate(node));
        irtr.setResultStructure(parseResultStructure(node.path(RESULT_STRUCTURE)));
        irtr.setResultEncoding(parseResultEncoding(node.path(RESULT_ENCODING)));
        return irtr;
    }

    private OmObservationConstellation parseObservationTemplate(JsonNode node) throws OwsExceptionReport {
        final OmObservationConstellation oc =
                observationDecoder.parseObservationConstellation(node.path(OBSERVATION_TEMPLATE));
        oc.addOffering(node.path(OFFERING).textValue());
        return oc;
    }

    private SosResultStructure parseResultStructure(JsonNode node) throws OwsExceptionReport {
        SweDataRecord dataRecord = parseFields(node.path(JSONConstants.FIELDS));
        return new SosResultStructure().setResultStructure(dataRecord);
    }

    private SosResultEncoding parseResultEncoding(JsonNode node) {
        SweTextEncoding textEncoding = new SweTextEncoding();
        textEncoding.setTokenSeparator(node.path(TOKEN_SEPARATOR).textValue());
        textEncoding.setBlockSeparator(node.path(BLOCK_SEPARATOR).textValue());
        if (!node.path(DECIMAL_SEPARATOR).isMissingNode()) {
            textEncoding.setDecimalSeparator(node.path(DECIMAL_SEPARATOR).textValue());
        }
        return new SosResultEncoding().setEncoding(textEncoding);
    }

    protected SweDataRecord parseFields(JsonNode node) throws OwsExceptionReport {
        SweDataRecord dataRecord = new SweDataRecord();
        for (JsonNode field : node) {
            dataRecord.addField(decodeJsonToObject(field, SweField.class));
        }
        return dataRecord;
    }
}
