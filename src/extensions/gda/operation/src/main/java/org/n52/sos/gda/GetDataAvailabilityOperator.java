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
package org.n52.sos.gda;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.n52.sos.gda.GetDataAvailabilityConstants.GetDataAvailabilityParams;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.operator.AbstractRequestOperator;
import org.n52.sos.request.operator.WSDLAwareRequestOperator;
import org.n52.sos.wsdl.WSDLOperation;

/**
 * {@code IRequestOperator} to handle {@link GetDataAvailabilityRequest}s.
 * 
 * @author Christian Autermann
 * 
 * @since 4.0.0
 */
public class GetDataAvailabilityOperator
        extends
        AbstractRequestOperator<AbstractGetDataAvailabilityDAO, GetDataAvailabilityRequest, GetDataAvailabilityResponse>
        implements WSDLAwareRequestOperator {

    private static final Set<String> CONFORMANCE_CLASSES = Collections
            .singleton(GetDataAvailabilityConstants.CONFORMANCE_CLASS);

    /**
     * Constructs a new {@code GetDataAvailabilityOperator}.
     */
    public GetDataAvailabilityOperator() {
        super(SosConstants.SOS, Sos2Constants.SERVICEVERSION, GetDataAvailabilityConstants.OPERATION_NAME,
                GetDataAvailabilityRequest.class);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
    }

    @Override
    public GetDataAvailabilityResponse receive(GetDataAvailabilityRequest sosRequest) throws OwsExceptionReport {
        return getDao().getDataAvailability(sosRequest);
    }

    @Override
    protected void checkParameters(GetDataAvailabilityRequest sosRequest) throws OwsExceptionReport {
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
            checkObservedProperties(sosRequest.getObservedProperties(),
                    GetDataAvailabilityParams.observedProperty.name());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkProcedureIDs(sosRequest.getProcedures(), GetDataAvailabilityParams.procedure.name());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkFeatureOfInterestIdentifiers(sosRequest.getFeaturesOfInterest(),
                    GetDataAvailabilityParams.featureOfInterest.name());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        exceptions.throwIfNotEmpty();
    }

    @Override
    public WSDLOperation getSosOperationDefinition() {
//       TODO no schema available
//        return GetDataAvailabilityConstants.WSDL_OPERATION;
        return null;
    }

    @Override
    public Map<String, String> getAdditionalSchemaImports() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, String> getAdditionalPrefixes() {
        return Collections.emptyMap();
    }
}
