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

import net.opengis.swes.x20.UpdateSensorDescriptionResponseDocument;
import net.opengis.swes.x20.UpdateSensorDescriptionResponseType;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.swes.SwesConstants;
import org.n52.sos.response.UpdateSensorResponse;
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
public class UpdateSensorResponseEncoder extends AbstractSwesResponseEncoder<UpdateSensorResponse> {
    public UpdateSensorResponseEncoder() {
        super(Sos2Constants.Operations.UpdateSensorDescription.name(), UpdateSensorResponse.class);
    }

    @Override
    protected XmlObject create(UpdateSensorResponse response) throws OwsExceptionReport {
        final UpdateSensorDescriptionResponseDocument document =
                UpdateSensorDescriptionResponseDocument.Factory.newInstance(XmlOptionsHelper.getInstance()
                        .getXmlOptions());
        final UpdateSensorDescriptionResponseType usdr = document.addNewUpdateSensorDescriptionResponse();
        usdr.setUpdatedProcedure(response.getUpdatedProcedure());
        return document;
    }

    @Override
    protected Set<SchemaLocation> getConcreteSchemaLocations() {
        return Sets.newHashSet(SwesConstants.SWES_20_UPDATE_SENSOR_DESCRIPTION_SCHEMA_LOCATION);
    }
}
