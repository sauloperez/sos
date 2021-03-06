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
package org.n52.sos.ext.deleteobservation;

import static org.n52.sos.ext.deleteobservation.DeleteObservationConstants.CONFORMANCE_CLASSES;
import static org.n52.sos.ogc.sos.SosConstants.SOS;

import java.util.Set;

import org.n52.sos.event.SosEventBus;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.request.operator.AbstractTransactionalRequestOperator;
import org.n52.sos.request.operator.RequestOperator;
import org.n52.sos.service.Configurator;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 1.0.0
 */
public class DeleteObservationRequestOperator
        extends
        AbstractTransactionalRequestOperator<DeleteObservationAbstractDAO, DeleteObservationRequest, DeleteObservationResponse>
        implements RequestOperator {

    public DeleteObservationRequestOperator() {
        super(SOS, Sos2Constants.SERVICEVERSION, DeleteObservationConstants.Operations.DeleteObservation.name(),
                DeleteObservationRequest.class);
    }

    @Override
    public DeleteObservationResponse receive(DeleteObservationRequest request) throws OwsExceptionReport {
        DeleteObservationResponse response = getDao().deleteObservation(request);
        SosEventBus.fire(new DeleteObservationEvent(request, response));
        return response;
    }

    protected Configurator getConfigurator() {
        return Configurator.getInstance();
    }

    @Override
    protected void checkParameters(DeleteObservationRequest sosRequest) throws OwsExceptionReport {
        CompositeOwsException exceptions = new CompositeOwsException();
        try {
            checkServiceParameter(sosRequest.getService());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkSingleVersionParameter(sosRequest);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkObservationIdentifier(sosRequest.getObservationIdentifier());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        exceptions.throwIfNotEmpty();
    }

    private void checkObservationIdentifier(String observationIdentifier) throws OwsExceptionReport {
        if (observationIdentifier == null || observationIdentifier.isEmpty()) {
            throw new MissingObservationParameterException();
        } else if (!getConfigurator().getCache().hasObservationIdentifier(observationIdentifier)) {
            throw new InvalidObservationParameterException(observationIdentifier);
        }
    }

    @Override
    public Set<String> getConformanceClasses() {
        return CONFORMANCE_CLASSES;
    }

}
