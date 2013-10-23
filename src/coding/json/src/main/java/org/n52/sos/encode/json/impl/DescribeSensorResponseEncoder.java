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

import java.util.List;

import org.apache.xmlbeans.XmlOptions;
import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.encode.json.AbstractSosResponseEncoder;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.response.DescribeSensorResponse;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlOptionsHelper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class DescribeSensorResponseEncoder extends AbstractSosResponseEncoder<DescribeSensorResponse> {
    public DescribeSensorResponseEncoder() {
        super(DescribeSensorResponse.class, SosConstants.Operations.DescribeSensor);
    }

    @Override
    protected void encodeResponse(ObjectNode json, DescribeSensorResponse t) throws OwsExceptionReport {
        json.put(JSONConstants.PROCEDURE_DESCRIPTION_FORMAT, t.getOutputFormat());
        json.put(JSONConstants.PROCEDURE_DESCRIPTION,
                encodeDescriptions(t.getProcedureDescriptions(), t.getOutputFormat()));

    }

    private String toString(SosProcedureDescription desc, String format) throws OwsExceptionReport {
        XmlOptions options = XmlOptionsHelper.getInstance().getXmlOptions();
        return CodingHelper.encodeObjectToXml(format, desc).xmlText(options);
    }

    private JsonNode encodeDescription(SosProcedureDescription desc, String format) throws OwsExceptionReport {
        String xml = toString(desc, format);
        if (desc.isSetValidTime()) {
            ObjectNode j = nodeFactory().objectNode();
            j.put(JSONConstants.VALID_TIME, encodeObjectToJson(desc.getValidTime()));
            j.put(JSONConstants.DESCRIPTION, xml);
            return j;
        } else {
            return nodeFactory().textNode(xml);
        }
    }

    private JsonNode encodeDescriptions(List<SosProcedureDescription> descs, String format) throws OwsExceptionReport {
        if (descs.size() == 1) {
            return encodeDescription(descs.get(0), format);
        } else {
            ArrayNode a = nodeFactory().arrayNode();
            for (SosProcedureDescription desc : descs) {
                a.add(encodeDescription(desc, format));
            }
            return a;
        }
    }
}
