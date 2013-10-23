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
package org.n52.sos.decode;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.opengis.sos.x10.DescribeSensorDocument;
import net.opengis.sos.x10.DescribeSensorDocument.DescribeSensor;
import net.opengis.sos.x10.GetCapabilitiesDocument;
import net.opengis.sos.x10.GetCapabilitiesDocument.GetCapabilities;
import net.opengis.sos.x10.GetFeatureOfInterestDocument;
import net.opengis.sos.x10.GetFeatureOfInterestDocument.GetFeatureOfInterest;
import net.opengis.sos.x10.GetFeatureOfInterestDocument.GetFeatureOfInterest.Location;
import net.opengis.sos.x10.GetObservationByIdDocument;
import net.opengis.sos.x10.GetObservationByIdDocument.GetObservationById;
import net.opengis.sos.x10.GetObservationDocument;
import net.opengis.sos.x10.GetObservationDocument.GetObservation;
import net.opengis.sos.x10.GetObservationDocument.GetObservation.FeatureOfInterest;
import net.opengis.sos.x10.ResponseModeType.Enum;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.DescribeSensorRequest;
import org.n52.sos.request.GetCapabilitiesRequest;
import org.n52.sos.request.GetFeatureOfInterestRequest;
import org.n52.sos.request.GetObservationByIdRequest;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.service.AbstractServiceCommunicationObject;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.OMHelper;
import org.n52.sos.util.SosHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * @since 4.0.0
 * 
 */
public class SosDecoderv100 implements Decoder<AbstractServiceCommunicationObject, XmlObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SosDecoderv100.class);

    @SuppressWarnings("unchecked")
    private static final Set<DecoderKey> DECODER_KEYS = CollectionHelper.union(CodingHelper.decoderKeysForElements(
            Sos1Constants.NS_SOS, GetCapabilitiesDocument.class, DescribeSensorDocument.class,
            GetObservationDocument.class, GetFeatureOfInterestDocument.class, GetObservationByIdDocument.class),
            CodingHelper.xmlDecoderKeysForOperation(SosConstants.SOS, Sos1Constants.SERVICEVERSION,
                    SosConstants.Operations.GetCapabilities, SosConstants.Operations.GetObservation,
                    SosConstants.Operations.GetFeatureOfInterest, SosConstants.Operations.GetObservationById,
                    SosConstants.Operations.DescribeSensor));

    public SosDecoderv100() {
        LOGGER.debug("Decoder for the following namespaces initialized successfully: {}!",
                Joiner.on(", ").join(DECODER_KEYS));
    }

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.emptyMap();
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    public AbstractServiceCommunicationObject decode(XmlObject xmlObject) throws OwsExceptionReport {
        AbstractServiceCommunicationObject request = null;
        LOGGER.debug("REQUESTTYPE:" + xmlObject.getClass());

        /*
         * Add O&M 1.0.0 namespace to GetObservation document. XmlBeans removes
         * the namespace from the document because there are no om:... elements
         * in the document. But the validation fails if the <resultModel>
         * element is set with e.g. om:Measurement.
         */
        if (xmlObject instanceof GetObservationDocument) {
            XmlCursor cursor = xmlObject.newCursor();
            cursor.toFirstChild();
            cursor.insertNamespace(OmConstants.NS_OM_PREFIX, OmConstants.NS_OM);
            cursor.dispose();
        }
        // validate document
        XmlHelper.validateDocument(xmlObject);

        // getCapabilities request
        if (xmlObject instanceof GetCapabilitiesDocument) {
            GetCapabilitiesDocument getCapsDoc = (GetCapabilitiesDocument) xmlObject;
            request = parseGetCapabilities(getCapsDoc);
        }

        // DescribeSensor request (still SOS 1.0 NS_URI
        else if (xmlObject instanceof DescribeSensorDocument) {
            DescribeSensorDocument descSensorDoc = (DescribeSensorDocument) xmlObject;
            request = parseDescribeSensor(descSensorDoc);
        }

        // getObservation request
        else if (xmlObject instanceof GetObservationDocument) {
            GetObservationDocument getObsDoc = (GetObservationDocument) xmlObject;
            request = parseGetObservation(getObsDoc);
        }

        // getFeatureOfInterest request
        else if (xmlObject instanceof GetFeatureOfInterestDocument) {
            GetFeatureOfInterestDocument getFoiDoc = (GetFeatureOfInterestDocument) xmlObject;
            request = parseGetFeatureOfInterest(getFoiDoc);
        }

        // getObservationById request
        else if (xmlObject instanceof GetObservationByIdDocument) {
            GetObservationByIdDocument getObsByIdDoc = (GetObservationByIdDocument) xmlObject;
            request = parseGetObservationById(getObsByIdDoc);
        }

        else {
            throw new UnsupportedDecoderInputException(this, xmlObject);
        }
        return request;
    }

    /**
     * parses the XmlBean representing the getCapabilities request and creates a
     * SosGetCapabilities request
     * 
     * @param getCapsDoc
     *            XmlBean created from the incoming request stream
     * @return Returns SosGetCapabilitiesRequest representing the request
     * 
     * 
     * @throws OwsExceptionReport
     *             * If parsing the XmlBean failed
     */
    private AbstractServiceRequest parseGetCapabilities(GetCapabilitiesDocument getCapsDoc) throws OwsExceptionReport {
        GetCapabilitiesRequest request = new GetCapabilitiesRequest();

        GetCapabilities getCaps = getCapsDoc.getGetCapabilities();

        request.setService(getCaps.getService());

        if (getCaps.getAcceptFormats() != null && getCaps.getAcceptFormats().sizeOfOutputFormatArray() != 0) {
            request.setAcceptFormats(Arrays.asList(getCaps.getAcceptFormats().getOutputFormatArray()));
        }

        if (getCaps.getAcceptVersions() != null && getCaps.getAcceptVersions().sizeOfVersionArray() != 0) {
            request.setAcceptVersions(Arrays.asList(getCaps.getAcceptVersions().getVersionArray()));
        }

        if (getCaps.getSections() != null && getCaps.getSections().getSectionArray().length != 0) {
            request.setSections(Arrays.asList(getCaps.getSections().getSectionArray()));
        }

        return request;
    }

    /**
     * parses the XmlBean representing the describeSensor request and creates a
     * DescribeSensor request
     * 
     * @param descSensorDoc
     *            XmlBean created from the incoming request stream
     * @return Returns SosDescribeSensorRequest representing the request
     * 
     * 
     * @throws OwsExceptionReport
     *             * If parsing the XmlBean failed
     */
    private AbstractServiceCommunicationObject parseDescribeSensor(DescribeSensorDocument descSensorDoc) {

        DescribeSensorRequest request = new DescribeSensorRequest();
        DescribeSensor descSensor = descSensorDoc.getDescribeSensor();
        request.setService(descSensor.getService());
        request.setVersion(descSensor.getVersion());
        request.setProcedureDescriptionFormat(MediaType.parse(descSensor.getOutputFormat()).toString());
        request.setProcedure(descSensor.getProcedure());
        return request;
    }

    /**
     * parses the XmlBean representing the getObservation request and creates a
     * SoSGetObservation request
     * 
     * @param getObsDoc
     *            XmlBean created from the incoming request stream
     * @return Returns SosGetObservationRequest representing the request
     * 
     * 
     * @throws OwsExceptionReport
     *             * If parsing the XmlBean failed
     */
    private AbstractServiceRequest parseGetObservation(GetObservationDocument getObsDoc) throws OwsExceptionReport {
        GetObservationRequest getObsRequest = new GetObservationRequest();

        GetObservation getObs = getObsDoc.getGetObservation();

        getObsRequest.setService(getObs.getService());
        getObsRequest.setVersion(getObs.getVersion());
        getObsRequest.setOfferings(Arrays.asList(getObs.getOffering()));
        getObsRequest.setObservedProperties(Arrays.asList(getObs.getObservedPropertyArray()));
        getObsRequest.setProcedures(Arrays.asList(getObs.getProcedureArray()));
        getObsRequest.setTemporalFilters(parseTemporalFilters4GetObservation(getObs.getEventTimeArray()));
        getObsRequest.setSrid(SosHelper.parseSrsName(getObs.getSrsName()));

        getObsRequest.setSpatialFilter(parseSpatialFilter4GetObservation(getObs.getFeatureOfInterest()));

        getObsRequest.setFeatureIdentifiers(parseFeatureofInterestV100(getObs.getFeatureOfInterest()));
        // TODO result filter, not supported by this SOS yet?
        // return error message
        if (getObs.isSetResponseFormat()) {
            try {
                String responseFormat = URLDecoder.decode(getObs.getResponseFormat(), "UTF-8");
                getObsRequest.setResponseFormat(MediaType.parse(responseFormat).toString());
            } catch (UnsupportedEncodingException e) {
                throw new NoApplicableCodeException().causedBy(e).withMessage("Error while decoding response format!");
            }

        } else {
            getObsRequest.setResponseFormat(OmConstants.CONTENT_TYPE_OM.toString());
        }
        if (getObs.isSetResultModel()) {
            getObsRequest.setResultModel(OMHelper.getObservationTypeFor(getObs.getResultModel()));
        }

        return getObsRequest;
    }

    /**
     * parses the passes XmlBeans document and creates a SOS
     * getFeatureOfInterest request
     * 
     * @param getFoiDoc
     *            XmlBeans document representing the getFeatureOfInterest
     *            request
     * @return Returns SOS getFeatureOfInterest request
     * 
     * 
     * @throws OwsExceptionReport
     *             * if validation of the request failed
     */
    private AbstractServiceRequest parseGetFeatureOfInterest(GetFeatureOfInterestDocument getFoiDoc)
            throws OwsExceptionReport {

        GetFeatureOfInterestRequest getFoiRequest = new GetFeatureOfInterestRequest();
        GetFeatureOfInterest getFoi = getFoiDoc.getGetFeatureOfInterest();
        getFoiRequest.setService(getFoi.getService());
        getFoiRequest.setVersion(getFoi.getVersion());
        getFoiRequest.setFeatureIdentifiers(Arrays.asList(getFoi.getFeatureOfInterestIdArray()));
        getFoiRequest.setSpatialFilters(parseSpatialFilters4GetFeatureOfInterest(getFoi.getLocation()));

        return getFoiRequest;
    }

    private AbstractServiceRequest parseGetObservationById(GetObservationByIdDocument getObsByIdDoc)
            throws OwsExceptionReport {
        GetObservationByIdRequest getObsByIdRequest = new GetObservationByIdRequest();
        GetObservationById getObsById = getObsByIdDoc.getGetObservationById();
        getObsByIdRequest.setService(getObsById.getService());
        getObsByIdRequest.setVersion(getObsById.getVersion());
        getObsByIdRequest.setResponseFormat(getObsById.getResponseFormat());
        Enum responseMode = getObsById.getResponseMode();
        if (responseMode != null && responseMode.toString().equalsIgnoreCase(SosConstants.RESPONSE_MODE_INLINE)) {
            getObsByIdRequest.setResponseMode(SosConstants.RESPONSE_MODE_INLINE);
        }
        if (getObsById.isSetResultModel()) {
            getObsByIdRequest.setResultModel(OMHelper.getObservationTypeFor(getObsById.getResultModel()));
        }
        getObsByIdRequest.setObservationIdentifier(Arrays.asList(getObsById.getObservationId()));
        return getObsByIdRequest;
    }

    /**
     * Parses the spatial filter of a GetObservation request.
     * 
     * @param spatialFilter
     *            XmlBean representing the spatial filter parameter of the
     *            request
     * @return Returns SpatialFilter created from the passed foi request
     *         parameter
     * 
     * 
     * @throws OwsExceptionReport
     *             * if creation of the SpatialFilter failed
     */
    private SpatialFilter parseSpatialFilter4GetObservation(GetObservation.FeatureOfInterest spatialFilter)
            throws OwsExceptionReport {
        if (spatialFilter != null && spatialFilter.getSpatialOps() != null) {
            Object filter = CodingHelper.decodeXmlElement(spatialFilter.getSpatialOps());
            if (filter instanceof SpatialFilter) {
                return (SpatialFilter) filter;
            }
        }
        return null;
    }

    /**
     * Parses the spatial filters of a GetFeatureOfInterest request.
     * 
     * @param location
     *            XmlBean representing the spatial filter parameter of the
     *            request
     * @return Returns SpatialFilter created from the passed foi request
     *         parameter
     * 
     * 
     * @throws OwsExceptionReport
     *             * if creation of the SpatialFilter failed
     */
    private List<SpatialFilter> parseSpatialFilters4GetFeatureOfInterest(Location location) throws OwsExceptionReport {

        List<SpatialFilter> sosSpatialFilters = new LinkedList<SpatialFilter>();
        if (location != null && location.getSpatialOps() != null) {
            Object filter = CodingHelper.decodeXmlElement(location.getSpatialOps());
            if (filter instanceof SpatialFilter) {
                sosSpatialFilters.add((SpatialFilter) filter);
            }
        }
        return sosSpatialFilters;
    }

    /**
     * parses the Time of the requests and returns an array representing the
     * temporal filters
     * 
     * @param temporalFilters
     *            array of XmlObjects representing the Time element in the
     *            request
     * @return Returns array representing the temporal filters
     * 
     * 
     * @throws OwsExceptionReport
     *             * if parsing of the element failed
     */
    private List<TemporalFilter> parseTemporalFilters4GetObservation(GetObservation.EventTime[] temporalFilters)
            throws OwsExceptionReport {

        List<TemporalFilter> sosTemporalFilters = new LinkedList<TemporalFilter>();

        for (GetObservation.EventTime temporalFilter : temporalFilters) {
            Object filter = CodingHelper.decodeXmlElement(temporalFilter.getTemporalOps());
            if (filter instanceof TemporalFilter) {
                sosTemporalFilters.add((TemporalFilter) filter);
            }
        }
        return sosTemporalFilters;
    }

    private List<String> parseFeatureofInterestV100(FeatureOfInterest featureOfInterest) throws OwsExceptionReport {

        List<String> features = new LinkedList<String>();

        // TODO we need a featureDecoderV100 or in the GmlDecoderv311

        return features;
    }

}
