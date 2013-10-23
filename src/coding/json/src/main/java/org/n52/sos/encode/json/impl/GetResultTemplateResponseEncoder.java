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
package org.n52.sos.encode.json.impl;

import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.encode.json.AbstractSosResponseEncoder;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.encoding.SweAbstractEncoding;
import org.n52.sos.ogc.swe.encoding.SweTextEncoding;
import org.n52.sos.response.GetResultTemplateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class GetResultTemplateResponseEncoder extends AbstractSosResponseEncoder<GetResultTemplateResponse> {
    private static final Logger LOG = LoggerFactory.getLogger(GetResultTemplateResponseEncoder.class);

    public GetResultTemplateResponseEncoder() {
        super(GetResultTemplateResponse.class, Sos2Constants.Operations.GetResultTemplate);
    }

    @Override
    protected void encodeResponse(ObjectNode json, GetResultTemplateResponse t) throws OwsExceptionReport {
        encodeResultEncoding(t, json);
        encodeResultStructure(t, json);
    }

    private void encodeResultStructure(GetResultTemplateResponse t, ObjectNode json) throws OwsExceptionReport {
        ObjectNode jrs = json.putObject(JSONConstants.RESULT_STRUCTURE);
        SweAbstractDataComponent structure = t.getResultStructure().getResultStructure();
        if (structure instanceof SweDataRecord) {
            encodeSweDataRecord(structure, jrs);
        } else {
            LOG.warn("Unsupported structure: {}", structure == null ? null : structure.getClass());
        }
    }

    private void encodeResultEncoding(GetResultTemplateResponse t, ObjectNode json) throws OwsExceptionReport {
        ObjectNode jre = json.putObject(JSONConstants.RESULT_ENCODING);
        SweAbstractEncoding encoding = t.getResultEncoding().getEncoding();
        if (encoding instanceof SweTextEncoding) {
            encodeSweTextEncoding(encoding, jre);
        } else {
            LOG.warn("Unsupported encoding: {}", encoding == null ? null : encoding.getClass());
        }
    }

    private void encodeSweTextEncoding(SweAbstractEncoding encoding, ObjectNode node) {
        SweTextEncoding sweTextEncoding = (SweTextEncoding) encoding;
        String ts = sweTextEncoding.getTokenSeparator();
        if (ts != null && !ts.isEmpty()) {
            node.put(JSONConstants.TOKEN_SEPARATOR, ts);
        }
        String bs = sweTextEncoding.getBlockSeparator();
        if (bs != null && !bs.isEmpty()) {
            node.put(JSONConstants.BLOCK_SEPARATOR, bs);
        }
        String ds = sweTextEncoding.getDecimalSeparator();
        if (ds != null && !ds.isEmpty()) {
            node.put(JSONConstants.DECIMAL_SEPARATOR, ds);
        }
    }

    private void encodeSweDataRecord(SweAbstractDataComponent structure, ObjectNode node) throws OwsExceptionReport {
        SweDataRecord sweDataRecord = (SweDataRecord) structure;
        ArrayNode fields = node.putArray(JSONConstants.FIELDS);
        for (SweField field : sweDataRecord.getFields()) {
            fields.add(encodeObjectToJson(field));
        }
    }
}
