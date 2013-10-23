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
import java.util.List;
import java.util.Set;

import org.n52.sos.ds.AbstractGetFeatureOfInterestDAO;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.ConformanceClasses;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.GetFeatureOfInterestRequest;
import org.n52.sos.response.GetFeatureOfInterestResponse;
import org.n52.sos.wsdl.WSDLConstants;
import org.n52.sos.wsdl.WSDLOperation;

/**
 * @since 4.0.0
 * 
 */
public class SosGetFeatureOfInterestOperatorV20
        extends
        AbstractV2RequestOperator<AbstractGetFeatureOfInterestDAO, GetFeatureOfInterestRequest, GetFeatureOfInterestResponse> {

    private static final Set<String> CONFORMANCE_CLASSES = Collections
            .singleton(ConformanceClasses.SOS_V2_FEATURE_OF_INTEREST_RETRIEVAL);

    private static final String OPERATION_NAME = SosConstants.Operations.GetFeatureOfInterest.name();

    public SosGetFeatureOfInterestOperatorV20() {
        super(OPERATION_NAME, GetFeatureOfInterestRequest.class);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
    }

    @Override
    public GetFeatureOfInterestResponse receive(GetFeatureOfInterestRequest request) throws OwsExceptionReport {
        return getDao().getFeatureOfInterest(request);
    }

    @Override
    protected void checkParameters(GetFeatureOfInterestRequest sosRequest) throws OwsExceptionReport {
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
                    Sos2Constants.GetFeatureOfInterestParams.observedProperty.name());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkProcedureIDs(sosRequest.getProcedures(), Sos2Constants.GetFeatureOfInterestParams.procedure.name());
            if (sosRequest.isSetProcedures()) {
                sosRequest.setProcedures(addChildProcedures(sosRequest.getProcedures()));
            }
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkFeatureOfInterestAndRelatedFeatureIdentifier(sosRequest.getFeatureIdentifiers(),
                    Sos2Constants.GetFeatureOfInterestParams.featureOfInterest.name());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkSpatialFilters(sosRequest.getSpatialFilters(),
                    Sos2Constants.GetFeatureOfInterestParams.spatialFilter.name());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }

        exceptions.throwIfNotEmpty();
    }

    private void checkFeatureOfInterestAndRelatedFeatureIdentifier(List<String> featureIdentifiers,
            String parameterName) throws OwsExceptionReport {
        if (featureIdentifiers != null) {
            CompositeOwsException exceptions = new CompositeOwsException();
            for (String featureOfInterest : featureIdentifiers) {
                try {
                    if (!getCache().hasRelatedFeature(featureOfInterest)) {
                        checkFeatureOfInterestIdentifier(featureOfInterest, parameterName);
                    }
                } catch (OwsExceptionReport e) {
                    exceptions.add(e);
                }
            }
            exceptions.throwIfNotEmpty();
        }
    }

    @Override
    public WSDLOperation getSosOperationDefinition() {
        return WSDLConstants.Operations.GET_FEATURE_OF_INTEREST;
    }
}
