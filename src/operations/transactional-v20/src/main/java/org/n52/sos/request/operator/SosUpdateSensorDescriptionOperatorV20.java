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
package org.n52.sos.request.operator;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.ds.AbstractUpdateSensorDescriptionDAO;
import org.n52.sos.event.SosEventBus;
import org.n52.sos.event.events.SensorModification;
import org.n52.sos.exception.ows.OptionNotSupportedException;
import org.n52.sos.exception.ows.concrete.InvalidProcedureParameterException;
import org.n52.sos.exception.ows.concrete.MissingProcedureParameterException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.ConformanceClasses;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.request.UpdateSensorRequest;
import org.n52.sos.response.UpdateSensorResponse;
import org.n52.sos.service.Configurator;
import org.n52.sos.wsdl.WSDLConstants;
import org.n52.sos.wsdl.WSDLOperation;

/**
 * @since 4.0.0
 *
 */
public class SosUpdateSensorDescriptionOperatorV20 extends 
        AbstractV2TransactionalRequestOperator<AbstractUpdateSensorDescriptionDAO, UpdateSensorRequest, UpdateSensorResponse>{
    private static final Set<String> CONFORMANCE_CLASSES = Collections.singleton(ConformanceClasses.SOS_V2_UPDATE_SENSOR_DESCRIPTION);

    public SosUpdateSensorDescriptionOperatorV20() {
        super(Sos2Constants.Operations.UpdateSensorDescription.name(), UpdateSensorRequest.class);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
    }

    @Override
    public UpdateSensorResponse receive(UpdateSensorRequest request) throws OwsExceptionReport {
        UpdateSensorResponse response = getDao().updateSensorDescription(request);
        SosEventBus.fire(new SensorModification(request, response));
        return response;
    }

    @Override
    protected void checkParameters(UpdateSensorRequest request) throws OwsExceptionReport {
        CompositeOwsException exceptions = new CompositeOwsException();
        try {
            checkServiceParameter(request.getService());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkSingleVersionParameter(request);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkProcedureIdentifier(request.getProcedureIdentifier());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            for (SosProcedureDescription sosProcedureDescription : request.getProcedureDescriptions()) {
                if (sosProcedureDescription.isSetValidTime()) {
                    throw new OptionNotSupportedException().at(Sos2Constants.UpdateSensorDescriptionParams.validTime)
                            .withMessage("The optional parameter '%s' is not supported!",
                                    Sos2Constants.UpdateSensorDescriptionParams.validTime.name());
                }
            }
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        exceptions.throwIfNotEmpty();
    }

    private void checkProcedureIdentifier(String procedureIdentifier) throws OwsExceptionReport {
        if (procedureIdentifier != null && !procedureIdentifier.isEmpty()) {
            if (!Configurator.getInstance().getCache().getProcedures().contains(procedureIdentifier)) {
                throw new InvalidProcedureParameterException(procedureIdentifier);
            }
        } else {
            throw new MissingProcedureParameterException();
        }
    }

    @Override
    public WSDLOperation getSosOperationDefinition() {
        return WSDLConstants.Operations.UPDATE_SENSOR_DESCRIPTION;
    }
}
