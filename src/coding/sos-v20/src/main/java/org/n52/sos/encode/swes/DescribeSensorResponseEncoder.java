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
package org.n52.sos.encode.swes;

import java.util.Set;

import net.opengis.swes.x20.DescribeSensorResponseDocument;
import net.opengis.swes.x20.DescribeSensorResponseType;
import net.opengis.swes.x20.SensorDescriptionType;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.swes.SwesConstants;
import org.n52.sos.response.DescribeSensorResponse;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.GmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.w3c.SchemaLocation;

import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class DescribeSensorResponseEncoder extends AbstractSwesResponseEncoder<DescribeSensorResponse> {
    public DescribeSensorResponseEncoder() {
        super(SosConstants.Operations.DescribeSensor.name(), DescribeSensorResponse.class);
    }

    @Override
    protected XmlObject create(DescribeSensorResponse response) throws OwsExceptionReport {
        DescribeSensorResponseDocument doc =
                DescribeSensorResponseDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        DescribeSensorResponseType dsr = doc.addNewDescribeSensorResponse();
        dsr.setProcedureDescriptionFormat(response.getOutputFormat());
        for (SosProcedureDescription sosProcedureDescription : response.getProcedureDescriptions()) {
            SensorDescriptionType sensorDescription = dsr.addNewDescription().addNewSensorDescription();
            XmlObject xmlObject = CodingHelper.encodeObjectToXml(response.getOutputFormat(), sosProcedureDescription);
            sensorDescription.addNewData().set(xmlObject);
            if (sosProcedureDescription.isSetValidTime()) {
                XmlObject xmlObjectValidtime =
                        CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, sosProcedureDescription.getValidTime());
                XmlObject substitution =
                        sensorDescription
                                .addNewValidTime()
                                .addNewAbstractTimeGeometricPrimitive()
                                .substitute(GmlHelper.getGml321QnameForITime(sosProcedureDescription.getValidTime()),
                                        xmlObjectValidtime.schemaType());
                substitution.set(xmlObjectValidtime);
            }
        }
        return doc;
    }

    @Override
    public Set<SchemaLocation> getConcreteSchemaLocations() {
        return Sets.newHashSet(SwesConstants.SWES_20_DESCRIBE_SENSOR_SCHEMA_LOCATION);
    }
}
