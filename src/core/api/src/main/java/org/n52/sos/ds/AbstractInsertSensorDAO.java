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
package org.n52.sos.ds;

import org.n52.sos.coding.CodingRepository;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsOperation;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.request.InsertSensorRequest;
import org.n52.sos.response.InsertSensorResponse;

/**
 * @since 4.0.0
 * 
 */
public abstract class AbstractInsertSensorDAO extends AbstractOperationDAO {

    public AbstractInsertSensorDAO(String service) {
        super(service, Sos2Constants.Operations.InsertSensor.name());
    }

    @Override
    protected void setOperationsMetadata(OwsOperation opsMeta, String service, String version)
            throws OwsExceptionReport {
        if (version.equals(Sos1Constants.SERVICEVERSION)) {
            opsMeta.addAnyParameterValue(Sos1Constants.RegisterSensorParams.SensorDescription);
            opsMeta.addAnyParameterValue(Sos1Constants.RegisterSensorParams.ObservationTemplate);
        } else {
            opsMeta.addAnyParameterValue(Sos2Constants.InsertSensorParams.procedureDescription);
            opsMeta.addPossibleValuesParameter(Sos2Constants.InsertSensorParams.procedureDescriptionFormat,
                    CodingRepository.getInstance().getSupportedProcedureDescriptionFormats(service, version));
            opsMeta.addAnyParameterValue(Sos2Constants.InsertSensorParams.observableProperty);
            opsMeta.addAnyParameterValue(Sos2Constants.InsertSensorParams.metadata);
            opsMeta.addDataTypeParameter(Sos2Constants.InsertSensorParams.metadata,
                    Sos2Constants.SCHEMA_LOCATION_URL_SOS_INSERTION_CAPABILITIES);
        }
    }

    public abstract InsertSensorResponse insertSensor(InsertSensorRequest request) throws OwsExceptionReport;
}
