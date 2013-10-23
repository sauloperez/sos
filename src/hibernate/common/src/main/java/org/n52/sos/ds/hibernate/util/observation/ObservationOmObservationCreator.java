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
package org.n52.sos.ds.hibernate.util.observation;

import org.n52.sos.ds.hibernate.util.procedure.HibernateProcedureConverter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.ds.hibernate.dao.ObservationConstellationDAO;
import org.n52.sos.ds.hibernate.entities.BlobObservation;
import org.n52.sos.ds.hibernate.entities.BooleanObservation;
import org.n52.sos.ds.hibernate.entities.CategoryObservation;
import org.n52.sos.ds.hibernate.entities.CountObservation;
import org.n52.sos.ds.hibernate.entities.GeometryObservation;
import org.n52.sos.ds.hibernate.entities.NumericObservation;
import org.n52.sos.ds.hibernate.entities.Observation;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.SpatialFilteringProfile;
import org.n52.sos.ds.hibernate.entities.SweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.TextObservation;
import org.n52.sos.exception.CodedException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.AbstractPhenomenon;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.om.values.UnknownValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.sos.SosProcedureDescriptionUnknowType;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.JTSHelper;
import org.n52.sos.util.SosHelper;
import org.n52.sos.util.StringHelper;
import org.n52.sos.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Geometry;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public class ObservationOmObservationCreator extends AbstractOmObservationCreator {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ObservationOmObservationCreator.class);
    private final Collection<Observation> observations;
    private final Map<Long, SpatialFilteringProfile> spatialFilteringProfile;
    private final String resultModel;
    private final Map<String, AbstractFeature> features = Maps.newHashMap();
    private final Map<String, AbstractPhenomenon> observedProperties = Maps.newHashMap();
    private final Map<String, SosProcedureDescription> procedures = Maps.newHashMap();
    private final Set<OmObservationConstellation> observationConstellations = Sets.newHashSet();
    private final HibernateProcedureConverter procedureConverter;
    private final FeatureQueryHandler featureQueryHandler;
    private final boolean encodeProcedureInObservation;
    private final boolean strictSpatialFilteringProfile;
    private List<OmObservation> observationCollection;
    private final int default3DEPSG;
    private final int defaultEPSG;
    
    public ObservationOmObservationCreator(Collection<Observation> observations,
                                           Map<Long, SpatialFilteringProfile> spatialFilteringProfile,
                                           String version, String resultModel,
                                           Session session) {
        super(version, session);
        this.resultModel = resultModel;
        if (observations == null) {
            this.observations = Collections.emptyList();
        } else {
            this.observations = observations;
        }
        if (spatialFilteringProfile == null) {
            this.spatialFilteringProfile = Collections.emptyMap();
        } else {
            this.spatialFilteringProfile = spatialFilteringProfile;
        }
        this.procedureConverter = new HibernateProcedureConverter();
        this.featureQueryHandler = getFeatureQueryHandler();
        this.strictSpatialFilteringProfile = ServiceConfiguration.getInstance().isStrictSpatialFilteringProfile();
        this.encodeProcedureInObservation = getActiveProfile().isEncodeProcedureInObservation();
        this.defaultEPSG = GeometryHandler.getInstance().getDefaultEPSG();
        this.default3DEPSG = GeometryHandler.getInstance().getDefault3DEPSG();
    }

    private int getDefaultEPSG() {
        return defaultEPSG;
    }

    private int getDefault3DEPSG() {
        return default3DEPSG;
    }


    private Collection<Observation> getObservations() {
        return observations;
    }

    private Map<Long, SpatialFilteringProfile> getSpatialFilteringProfile() {
        return spatialFilteringProfile;
    }

    private String getResultModel() {
        return resultModel;
    }

    private SosProcedureDescription getProcedure(String procedureId) {
        return procedures.get(procedureId);
    }

    private AbstractPhenomenon getObservedProperty(String phenomenonId) {
        return observedProperties.get(phenomenonId);
    }

    private AbstractFeature getFeature(String featureId) {
        return features.get(featureId);
    }

    @Override
    public List<OmObservation> create() throws OwsExceptionReport, ConverterException {
        if (getObservations() == null) {
            return Collections.emptyList();
        } else if (this.observationCollection == null) {
            this.observationCollection = Lists.newLinkedList();
            // now iterate over resultset and create Measurement for each row
            for (Observation hObservation : getObservations()) {
                // check remaining heap size and throw exception if minimum is
                // reached
                SosHelper.checkFreeMemory();

                String procedureId = createProcedure(hObservation);
                String featureId = createFeatureOfInterest(hObservation);
                String phenomenonId = createPhenomenon(hObservation);
                // TODO: add offering ids to response if needed later.
                // String offeringID =
                // hoc.getOffering().getIdentifier();
                // String mimeType = SosConstants.PARAMETER_NOT_SET;

                createValue(hObservation, phenomenonId, procedureId, featureId);
            }
        }
        return this.observationCollection;
    }

    private void checkOrSetObservablePropertyUnit(AbstractPhenomenon phen,
                                                  String unit) {
        if (phen instanceof OmObservableProperty) {
            final OmObservableProperty obsProp = (OmObservableProperty) phen;
            if (obsProp.getUnit() == null && unit != null) {
                obsProp.setUnit(unit);
            }
        }
    }

    /**
     * Get observation value from all value tables for an Observation object
     *
     * @param hObservation
     *                     Observation object
     *
     * @return Observation value
     *
     * @throws OwsExceptionReport
     * @throws CodedException
     */
    private Value<?> getValueFromObservation(final Observation hObservation)
            throws CodedException,
                   OwsExceptionReport {
        if (hObservation instanceof NumericObservation) {
            return new QuantityValue(((NumericObservation) hObservation).getValue());
        } else if (hObservation instanceof BooleanObservation) {
            return new org.n52.sos.ogc.om.values.BooleanValue(Boolean.valueOf(((BooleanObservation) hObservation).getValue()));
        } else if (hObservation instanceof CategoryObservation) {
            return new org.n52.sos.ogc.om.values.CategoryValue(((CategoryObservation) hObservation).getValue());
        } else if (hObservation instanceof CountObservation) {
            return new org.n52.sos.ogc.om.values.CountValue(Integer.valueOf(((CountObservation) hObservation).getValue()));
        } else if (hObservation instanceof TextObservation) {
            return new org.n52.sos.ogc.om.values.TextValue(((TextObservation) hObservation).getValue().toString());
        } else if (hObservation instanceof GeometryObservation) {
            return new org.n52.sos.ogc.om.values.GeometryValue(((GeometryObservation) hObservation).getValue());
        } else if (hObservation instanceof BlobObservation) {
            return new UnknownValue(((BlobObservation) hObservation).getValue());
        } else if (hObservation instanceof SweDataArrayObservation) {
            SweDataArrayValue sweDataArrayValue = new SweDataArrayValue();
            final XmlObject xml = XmlHelper.parseXmlString(((SweDataArrayObservation) hObservation).getValue());
            sweDataArrayValue.setValue((SweDataArray) CodingHelper.decodeXmlElement(xml));
            return sweDataArrayValue;
        }
        return null;
    }

    private NamedValue<?> getSpatialFilteringProfileParameter(
            SpatialFilteringProfile spatialFilteringProfile)
            throws OwsExceptionReport {
        final NamedValue<Geometry> namedValue = new NamedValue<Geometry>();
        final ReferenceType referenceType =
                new ReferenceType(spatialFilteringProfile.getDefinition());
        if (spatialFilteringProfile.isSetTitle()) {
            referenceType.setTitle(spatialFilteringProfile.getTitle());
        }
        namedValue.setName(referenceType);
        Geometry geometry;
        if (spatialFilteringProfile.isSetLongLat()) {
            final int epsg;
            if (spatialFilteringProfile.isSetSrid()) {
                epsg = spatialFilteringProfile.getSrid();
            } else {
                epsg = getDefaultEPSG();
            }
            JTSHelper.getGeometryFactoryForSRID(epsg);
            final String wktString = GeometryHandler.getInstance().getWktString(
                    spatialFilteringProfile.getLongitude(),
                    spatialFilteringProfile.getLatitude());

            geometry = JTSHelper.createGeometryFromWKT(wktString, epsg);
            if (spatialFilteringProfile.isSetAltitude()) {
                geometry.getCoordinate().z = GeometryHandler.getInstance().getValueAsDouble(spatialFilteringProfile.getAltitude());
                if (geometry.getSRID() == getDefaultEPSG()) {
                    geometry.setSRID(getDefault3DEPSG());
                }
            }
        } else {
            geometry = spatialFilteringProfile.getGeom();
        }
        namedValue.setValue(new GeometryValue(GeometryHandler.getInstance()
                .switchCoordinateAxisOrderIfNeeded(geometry)));
        return namedValue;
    }

    private NamedValue<?> getSpatialFilteringProfileParameterForConstellation(
            OmObservationConstellation omObservationConstellation) {
        final NamedValue<Geometry> namedValue = new NamedValue<Geometry>();
        namedValue
                .setName(new ReferenceType(Sos2Constants.HREF_PARAMETER_SPATIAL_FILTERING_PROFILE));
        if (omObservationConstellation.getFeatureOfInterest() instanceof SamplingFeature &&
            ((SamplingFeature) omObservationConstellation.getFeatureOfInterest()).isSetGeometry()) {
            namedValue.setValue(new GeometryValue(((SamplingFeature) omObservationConstellation.getFeatureOfInterest()).getGeometry()));
        } else {
            final String pId = omObservationConstellation.getProcedure().getIdentifier();
            SosEnvelope offeringEnvelope = getMergedBBox(getSosOfferingsForProcedure(pId));
            namedValue.setValue(new GeometryValue(JTSHelper
                    .getGeometryFactoryForSRID(
                    getDefaultEPSG()).createPoint(
                    offeringEnvelope.getEnvelope().centre())));
        }
        return namedValue;
    }

    /**
     * Merge offering envelopes
     *
     * @param offeringsForProcedure
     *                              SOS offerings
     *
     * @return merged envelopes
     */
    private SosEnvelope getMergedBBox(
            final Collection<SosOffering> offeringsForProcedure) {
        SosEnvelope mergedEnvelope = null;
        for (final SosOffering sosOffering : offeringsForProcedure) {
            final SosEnvelope offeringEnvelope =
                    getCache().getEnvelopeForOffering(sosOffering
                    .getOfferingIdentifier());
            if (offeringEnvelope != null && offeringEnvelope.isSetEnvelope()) {
                if (mergedEnvelope == null) {
                    mergedEnvelope = offeringEnvelope;
                } else {
                    mergedEnvelope.expandToInclude(offeringEnvelope
                            .getEnvelope());
                }
            }
        }
        return mergedEnvelope;
    }

    private Collection<SosOffering> getSosOfferingsForProcedure(
            final String procedureIdentifier) {
        final Collection<String> offeringIds = getCache()
                .getOfferingsForProcedure(procedureIdentifier);
        final Collection<SosOffering> offerings = Lists.newLinkedList();
        for (final String offeringIdentifier : offeringIds) {
            final String offeringName = getCache()
                    .getNameForOffering(offeringIdentifier);
            offerings.add(new SosOffering(offeringIdentifier, offeringName));
        }
        return offerings;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private OmObservation createNewObservation(OmObservationConstellation oc, Observation ho, Value<?> value) {
        final OmObservation o = new OmObservation();
        o.setObservationID(Long.toString(ho.getObservationId()));
        if (ho.isSetIdentifier() && !ho.getIdentifier().startsWith(SosConstants.GENERATED_IDENTIFIER_PREFIX)) {
            final CodeWithAuthority identifier = new CodeWithAuthority(ho.getIdentifier());
            if (ho.isSetCodespace()) {
                identifier.setCodeSpace(ho.getCodespace().getCodespace());
            }
            o.setIdentifier(identifier);
        }
        o.setNoDataValue(getActiveProfile().getResponseNoDataPlaceholder());
        o.setTokenSeparator(getTokenSeparator());
        o.setTupleSeparator(getTupleSeparator());
        o.setObservationConstellation(oc);
        o.setResultTime(new TimeInstant(new DateTime(ho.getResultTime(), DateTimeZone.UTC)));
        o.setValue(new SingleObservationValue(getPhenomenonTime(ho), value));
        return o;
    }

    private Time getPhenomenonTime(final Observation hObservation) {
        // create time element
        final DateTime phenStartTime = new DateTime(hObservation
                .getPhenomenonTimeStart(), DateTimeZone.UTC);
        DateTime phenEndTime;
        if (hObservation.getPhenomenonTimeEnd() != null) {
            phenEndTime =
                    new DateTime(hObservation.getPhenomenonTimeEnd(), DateTimeZone.UTC);
        } else {
            phenEndTime = phenStartTime;
        }
        Time phenomenonTime;
        if (phenStartTime.equals(phenEndTime)) {
            phenomenonTime = new TimeInstant(phenStartTime);
        } else {
            phenomenonTime = new TimePeriod(phenStartTime, phenEndTime);
        }
        return phenomenonTime;
    }

    private String createPhenomenon(final Observation hObservation) {
        LOGGER.trace("Creating Phenomenon...");
        final String phenID = hObservation.getObservableProperty().getIdentifier();
        if (!observedProperties.containsKey(phenID)) {
            final String description = hObservation.getObservableProperty().getDescription();
            observedProperties.put(phenID, new OmObservableProperty(phenID, description, null, null));
        }
        LOGGER.trace("Creating Phenomenon done.");
        return phenID;
    }

    private String createProcedure(final Observation hObservation) throws OwsExceptionReport,
                                                                          ConverterException {
        // TODO sfp full description
        LOGGER.trace("Creating Procedure...");
        final String procedureId = hObservation.getProcedure().getIdentifier();
        if (!procedures.containsKey(procedureId)) {
            final Procedure hProcedure = hObservation.getProcedure();
            final String pdf = hProcedure .getProcedureDescriptionFormat() .getProcedureDescriptionFormat();
            final SosProcedureDescription procedure;
            if (encodeProcedureInObservation) {
                procedure = procedureConverter.createSosProcedureDescription(
                        hProcedure, pdf, getVersion(), getSession());
            } else {
                procedure = new SosProcedureDescriptionUnknowType(procedureId, pdf, null);
            }
            procedures.put(procedureId, procedure);
        }
        LOGGER.trace("Creating Procedure done.");
        return procedureId;
    }

    private String createFeatureOfInterest(final Observation hObservation)
            throws OwsExceptionReport {
        LOGGER.trace("Creating Feature...");
        final String foiID = hObservation
                .getFeatureOfInterest().getIdentifier();
        if (!features.containsKey(foiID)) {
            final AbstractFeature featureByID =
                    featureQueryHandler
                    .getFeatureByID(foiID, getSession(), getVersion(), -1);
            features.put(foiID, featureByID);
        }
        LOGGER.trace("Creating Feature done.");
        return foiID;
    }

    private void createValue(Observation hObservation, String phenomenonId,
                             String procedureId, String featureId) throws OwsExceptionReport {
        LOGGER.trace("Creating Value...");
        final Value<?> value = getValueFromObservation(hObservation);
        if (value != null) {
            if (hObservation.getUnit() != null) {
                value.setUnit(hObservation.getUnit().getUnit());
            }
            checkOrSetObservablePropertyUnit(getObservedProperty(phenomenonId), value.getUnit());
            OmObservationConstellation obsConst = createObservationConstellation(hObservation, procedureId, phenomenonId, featureId);
            final OmObservation sosObservation =
                    createNewObservation(obsConst, hObservation, value);
            createSpatialFilteringProfileParameter(hObservation, sosObservation, obsConst);
            observationCollection.add(sosObservation);
            getSession().evict(hObservation);
            // TODO check for ScrollableResult vs
            // setFetchSize/setMaxResult
            // + setFirstResult
        }
        LOGGER.trace("Creating Value done.");
    }

    private OmObservationConstellation createObservationConstellation(Observation hObservation,
                                               String procedureId,
                                               String phenomenonId,
                                               String featureId) {
        OmObservationConstellation obsConst = new OmObservationConstellation(
                getProcedure(procedureId),
                getObservedProperty(phenomenonId),
                getFeature(featureId));

        /* sfp the offerings to find the templates */
        if (obsConst.getOfferings() == null) {
            final Set<String> offerings = Sets.newHashSet(getCache().getOfferingsForObservableProperty(obsConst.getObservableProperty().getIdentifier()));
            offerings.retainAll(getCache().getOfferingsForProcedure(obsConst.getProcedure().getIdentifier()));
            obsConst.setOfferings(offerings);
        }
        if (!observationConstellations.contains(obsConst)) {
            if (StringHelper.isNotEmpty(getResultModel())) {
                obsConst.setObservationType(getResultModel());
            }
            final ObservationConstellationDAO dao = new ObservationConstellationDAO();
            final ObservationConstellation hoc =
                    dao.getFirstObservationConstellationForOfferings(
                    hObservation.getProcedure(),
                    hObservation.getObservableProperty(),
                    hObservation.getOfferings(), getSession());
            if (hoc != null && hoc.getObservationType() != null) {
                obsConst.setObservationType(hoc .getObservationType().getObservationType());
            }
            observationConstellations.add(obsConst);
        }
        return obsConst;
    }

    private void createSpatialFilteringProfileParameter(Observation hObservation,
                                                        OmObservation sosObservation,
                                                        OmObservationConstellation oc)
            throws OwsExceptionReport {
        final long oId = hObservation.getObservationId();
        if (getSpatialFilteringProfile().containsKey(oId)) {
            SpatialFilteringProfile sfp = getSpatialFilteringProfile().get(oId);
            sosObservation.addParameter(getSpatialFilteringProfileParameter(sfp));
        } else if (strictSpatialFilteringProfile) {
            sosObservation.addParameter(getSpatialFilteringProfileParameterForConstellation(oc));
        }
    }
}
