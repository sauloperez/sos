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

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.decode.Decoder;
import org.n52.sos.decode.XmlNamespaceDecoderKey;
import org.n52.sos.decode.json.AbstractSosRequestDecoder;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.request.UpdateSensorRequest;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class UpdateSensorRequestDecoder extends AbstractSosRequestDecoder<UpdateSensorRequest> {
    public UpdateSensorRequestDecoder() {
        super(UpdateSensorRequest.class, SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                Sos2Constants.Operations.UpdateSensorDescription);
    }

    @Override
    protected String getSchemaURI() {
        return SchemaConstants.Request.UPDATE_SENSOR_DESCRIPTION;
    }

    @Override
    protected UpdateSensorRequest decodeRequest(JsonNode node) throws OwsExceptionReport {
        UpdateSensorRequest req = new UpdateSensorRequest();
        req.setProcedureIdentifier(node.path(JSONConstants.PROCEDURE).textValue());
        String pdf = node.path(JSONConstants.PROCEDURE_DESCRIPTION_FORMAT).textValue();
        req.setProcedureDescriptionFormat(pdf);
        if (node.path(JSONConstants.PROCEDURE_DESCRIPTION).isArray()) {
            for (JsonNode n : node.path(JSONConstants.PROCEDURE_DESCRIPTION)) {
                req.addProcedureDescriptionString(decodeProcedureDescription(n, pdf));
            }
        } else {
            req.addProcedureDescriptionString(decodeProcedureDescription(node, pdf));
        }
        return req;
    }

    private SosProcedureDescription decodeProcedureDescription(JsonNode node, String pdf) throws OwsExceptionReport {
        if (node.isTextual()) {
            return parseProcedureDesciption(node.textValue(), pdf);
        } else {
            SosProcedureDescription pd =
                    parseProcedureDesciption(node.path(JSONConstants.DESCRIPTION).textValue(), pdf);
            Time validTime = decodeJsonToObject(node.path(JSONConstants.VALID_TIME), Time.class);
            pd.setValidTime(validTime);
            return pd;
        }
    }

    private SosProcedureDescription parseProcedureDesciption(String xml, String pdf) throws OwsExceptionReport {
        try {
            final XmlObject xb = XmlObject.Factory.parse(xml);
            Decoder<?, XmlObject> decoder =
                    CodingRepository.getInstance().getDecoder(new XmlNamespaceDecoderKey(pdf, xb.getClass()));
            if (decoder == null) {
                throw new InvalidParameterValueException().at(JSONConstants.PROCEDURE_DESCRIPTION_FORMAT).withMessage(
                        "The requested %s is not supported!", JSONConstants.PROCEDURE_DESCRIPTION_FORMAT);
            }
            return (SosProcedureDescription) decoder.decode(xb);
        } catch (final XmlException xmle) {
            throw new NoApplicableCodeException().causedBy(xmle).withMessage(
                    "Error while parsing procedure description of InsertSensor request!");
        }
    }
}
