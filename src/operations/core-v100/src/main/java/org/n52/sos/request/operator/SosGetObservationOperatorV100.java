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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.ds.AbstractGetObservationDAO;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.ObservationEncoder;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.concrete.InvalidObservedPropertyParameterException;
import org.n52.sos.exception.ows.concrete.InvalidOfferingParameterException;
import org.n52.sos.exception.ows.concrete.InvalidResponseFormatParameterException;
import org.n52.sos.exception.ows.concrete.MissingObservedPropertyParameterException;
import org.n52.sos.exception.ows.concrete.MissingOfferingParameterException;
import org.n52.sos.exception.ows.concrete.MissingResponseFormatParameterException;
import org.n52.sos.exception.sos.ResponseExceedsSizeLimitException;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.OMHelper;
import org.n52.sos.util.SosHelper;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * class and forwards requests to the GetObservationDAO; after query of
 * Database, class encodes the ObservationResponse (thru using the IOMEncoder)
 * 
 * @since 4.0.0
 */
public class SosGetObservationOperatorV100 extends
        AbstractV1RequestOperator<AbstractGetObservationDAO, GetObservationRequest, GetObservationResponse> {

    private static final String OPERATION_NAME = SosConstants.Operations.GetObservation.name();

    private static final Set<String> CONFORMANCE_CLASSES = Collections
            .singleton("http://www.opengis.net/spec/SOS/1.0/conf/core");

    public SosGetObservationOperatorV100() {
        super(OPERATION_NAME, GetObservationRequest.class);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
    }

    @Override
    public GetObservationResponse receive(GetObservationRequest sosRequest) throws OwsExceptionReport {
        String responseFormat = sosRequest.getResponseFormat();
        GetObservationResponse response = getDao().getObservation(sosRequest);
        if (sosRequest.isSetResponseFormat()) {
            response.setResponseFormat(responseFormat);
            response.setContentType(checkResponseFormatForMediaType(sosRequest.getResponseFormat()));
        }
        return response;
    }

    @Override
    protected void checkParameters(GetObservationRequest sosRequest) throws OwsExceptionReport {
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

        // check if parameters are set, if not throw ResponseExceedsSizeLimit
        // exception
        checkQueryParametersIfAllEmpty(sosRequest);

        try {
            checkResponseFormat(sosRequest);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }

        try {
            sosRequest.setOfferings(checkOfferingId(sosRequest.getOfferings()));
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkObservedProperties(sosRequest.getObservedProperties());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkProcedureIDs(sosRequest.getProcedures(), SosConstants.GetObservationParams.procedure.name());
            // add child procedures to request
            if (sosRequest.isSetProcedure()) {
                sosRequest.setProcedures(addChildProcedures(sosRequest.getProcedures()));
            }
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        // TODO check foi param is ID
        try {
            checkFeatureOfInterestIdentifiers(sosRequest.getFeatureIdentifiers(),
                    SosConstants.GetObservationParams.featureOfInterest.name());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        // TODO spatial filter BBOX?
        try {
            checkSpatialFilter(sosRequest.getSpatialFilter(),
                    SosConstants.GetObservationParams.featureOfInterest.name());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        // TODO check for SOS 1.0.0 EventTime
        try {
            if (sosRequest.getTemporalFilters() != null && !sosRequest.getTemporalFilters().isEmpty()) {
                checkTemporalFilter(sosRequest.getTemporalFilters(),
                        Sos2Constants.GetObservationParams.temporalFilter.name());
            }
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }

        if (sosRequest.isSetResultModel()) {
            for (String offering : sosRequest.getOfferings()) {
                Collection<QName> qNamesForResultModel =
                        OMHelper.getQNamesForResultModel(getCache().getObservationTypesForOffering(offering));
                if (!qNamesForResultModel.contains(sosRequest.getResultModel())) {
                    exceptions.add(new InvalidParameterValueException().at(
                            Sos1Constants.GetObservationParams.resultModel).withMessage(
                            "The value '%s' is invalid for the requested offering!",
                            OMHelper.getEncodedResultModelFor(sosRequest.getResultModel())));
                }
            }
        }

        exceptions.throwIfNotEmpty();
    }

    /**
     * checks if mandatory parameter observed property is correct
     * 
     * @param observedProperties
     *            List containing the observed properties of the request
     * 
     * @throws OwsExceptionReport
     *             if the parameter does not containing any matching
     *             observedProperty for the requested offering
     */
    private void checkObservedProperties(List<String> observedProperties) throws OwsExceptionReport {
        if (observedProperties != null) {
            CompositeOwsException exceptions = new CompositeOwsException();

            if (observedProperties.isEmpty()) {
                throw new MissingObservedPropertyParameterException();
            }
            Collection<String> validObservedProperties =
                    Configurator.getInstance().getCache().getObservableProperties();
            for (String obsProp : observedProperties) {
                if (obsProp.isEmpty()) {
                    throw new MissingObservedPropertyParameterException();
                } else {
                    if (!validObservedProperties.contains(obsProp)) {
                        throw new InvalidObservedPropertyParameterException(obsProp);
                    }
                }
            }
            exceptions.throwIfNotEmpty();
        }
    }

    /**
     * checks if the passed offeringId is supported
     * 
     * @param offeringIds
     *            the offeringIds to be checked
     * 
     * @throws OwsExceptionReport
     *             if the passed offeringId is not supported
     */
    // one / mandatory
    private List<String> checkOfferingId(List<String> offeringIds) throws OwsExceptionReport {
        List<String> validOfferings = Lists.newLinkedList();
        if (offeringIds != null) {

            Set<String> offerings = Configurator.getInstance().getCache().getOfferings();
            Map<String, String> ncOfferings = SosHelper.getNcNameResolvedOfferings(offerings);
            CompositeOwsException exceptions = new CompositeOwsException();

            if (offeringIds.size() != 1) {
                throw new MissingOfferingParameterException();
            }

            for (String offeringId : offeringIds) {
                if (Strings.isNullOrEmpty(offeringId)) {
                    throw new MissingOfferingParameterException();
                }
                if (offerings.contains(offeringId)) {
                    validOfferings.add(offeringId);
                } else if (ncOfferings.containsKey(offeringId)) {
                    validOfferings.add(ncOfferings.get(offeringId));
                } else {
                    throw new InvalidOfferingParameterException(offeringId);
                }
            }
            exceptions.throwIfNotEmpty();
        }
        return validOfferings;
    }

    // TODO check for SOS 1.0.0
    private boolean checkForObservationAndMeasurementV20Type(String responseFormat) throws OwsExceptionReport {
        Encoder<XmlObject, OmObservation> encoder = CodingHelper.getEncoder(responseFormat, new OmObservation());
        if (encoder instanceof ObservationEncoder) {
            return ((ObservationEncoder) encoder).isObservationAndMeasurmentV20Type();
        }
        return false;
    }

    // TODO check for SOS 1.0.0 one / mandatory
    private boolean checkResponseFormat(GetObservationRequest request) throws OwsExceptionReport {
        boolean zipCompression = false;
        if (request.getResponseFormat() == null) {
            request.setResponseFormat(OmConstants.CONTENT_TYPE_OM.toString());
        } else if (request.getResponseFormat() != null && request.getResponseFormat().isEmpty()) {
            throw new MissingResponseFormatParameterException();
        } else {
            Collection<String> supportedResponseFormats =
                    CodingRepository.getInstance().getSupportedResponseFormats(request.getService(),
                            request.getVersion());
            if (!supportedResponseFormats.contains(request.getResponseFormat())) {
                throw new InvalidResponseFormatParameterException(request.getResponseFormat());
            }
        }
        return zipCompression;
    }

    private void checkQueryParametersIfAllEmpty(GetObservationRequest request) throws OwsExceptionReport {
        if (!request.isSetOffering() && !request.isSetObservableProperty() && !request.isSetProcedure()
                && !request.isSetFeatureOfInterest() && !request.isSetTemporalFilter()
                && !request.isSetSpatialFilter()) {
            throw new ResponseExceedsSizeLimitException()
                    .withMessage("The response exceeds the size limit! Please define some filtering parameters.");
        }

    }
}
