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
package org.n52.sos.ds.hibernate.cache.base;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.hibernate.Session;
import org.n52.sos.cache.WritableContentCache;
import org.n52.sos.ds.hibernate.ThreadLocalSessionFactory;
import org.n52.sos.ds.hibernate.cache.DatasourceCacheUpdateHelper;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.dao.ObservablePropertyDAO;
import org.n52.sos.ds.hibernate.dao.ObservationConstellationDAO;
import org.n52.sos.ds.hibernate.dao.ObservationDAO;
import org.n52.sos.ds.hibernate.dao.OfferingDAO;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.dao.SpatialFilteringProfileDAO;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterestType;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.ObservationType;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.RelatedFeature;
import org.n52.sos.ds.hibernate.entities.SpatialFilteringProfile;
import org.n52.sos.ds.hibernate.entities.TOffering;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.exception.ows.concrete.GenericThrowableWrapperException;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.CacheHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.RunnableAction;
import org.n52.sos.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
class OfferingCacheUpdateTask extends RunnableAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfferingCacheUpdateTask.class);

    private CountDownLatch countDownLatch;

    private ThreadLocalSessionFactory sessionFactory;

    private List<OwsExceptionReport> errors = Lists.newLinkedList();

    private OfferingDAO offeringDAO = new OfferingDAO();

    private ObservationConstellationDAO obsConstDAO = new ObservationConstellationDAO();
    
    private FeatureOfInterestDAO featureDAO = new FeatureOfInterestDAO();
    
    private String dsOfferingId;

    private Offering offering;

    private WritableContentCache cache;

    private List<ObservationConstellation> observationConstellations = Lists.newLinkedList();

    public OfferingCacheUpdateTask(CountDownLatch countDownLatch, ThreadLocalSessionFactory sessionFactory,
            WritableContentCache cache, String dsOfferingId, List<OwsExceptionReport> error) {
        this.countDownLatch = countDownLatch;
        this.sessionFactory = sessionFactory;
        this.cache = cache;
        this.dsOfferingId = dsOfferingId;
        this.errors = error;
    }

    protected CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    protected List<OwsExceptionReport> getErrors() {
        return errors;
    }

    protected Offering getOffering() {
        return offering;
    }

    protected WritableContentCache getCache() {
        return cache;
    }

    public ThreadLocalSessionFactory getSessionFactory() {
        return sessionFactory;
    }

    protected List<ObservationConstellation> getObservationConstellations() {
        return observationConstellations;
    }

    /**
     * Fetch the needed hibernate objects at start of processing. These object
     * cannot be passed in because they must be fetched with this task's session
     * (not the session of the task queuer).
     * 
     * @param session
     *            Session to use to load objects
     */
    protected void initHibernateObjects(Session session) {
        offering = offeringDAO.getOfferingForIdentifier(dsOfferingId, session);
        observationConstellations = obsConstDAO.getObservationConstellationsForOffering(offering, session);
    }

    protected void getOfferingInformationFromDbAndAddItToCacheMaps(Session session) throws OwsExceptionReport {
        initHibernateObjects(session);

        String offeringId = CacheHelper.addPrefixOrGetOfferingIdentifier(dsOfferingId);

        getCache().addOffering(offeringId);
        String offeringName = getOffering().getName();
        if (!StringHelper.isNotEmpty(offeringName)) {
            offeringName = offeringId;
            if (offeringName.startsWith("http")) {
                offeringName = offeringName.substring(offeringName.lastIndexOf('/') + 1, offeringName.length());
            } else if (offeringName.startsWith("urn")) {
                offeringName = offeringName.substring(offeringName.lastIndexOf(':') + 1, offeringName.length());
            }
            if (offeringName.contains("#")) {
                offeringName = offeringName.substring(offeringName.lastIndexOf('#') + 1, offeringName.length());
            }
        }
        getCache().setNameForOffering(offeringId, offeringName);
        // Procedures
        final Map<ProcedureFlag, Set<String>> procedureIdentifiers = getProcedureIdentifier(session);

        getCache().setProceduresForOffering(offeringId, procedureIdentifiers.get(ProcedureFlag.PARENT));
        getCache().setHiddenChildProceduresForOffering(offeringId,
                procedureIdentifiers.get(ProcedureFlag.HIDDEN_CHILD));
        // Observable properties
        getCache().setObservablePropertiesForOffering(offeringId, getObservablePropertyIdentifier(session));

        // Observation types
        getCache().setObservationTypesForOffering(offeringId, getObservationTypes(session));
        if (getOffering() instanceof TOffering) {
            // Related features
            getCache().setRelatedFeaturesForOffering(offeringId,
                    getRelatedFeatureIdentifiersFrom((TOffering) getOffering()));
            getCache().setAllowedObservationTypeForOffering(offeringId,
                    getObservationTypesFromObservationType(((TOffering) getOffering()).getObservationTypes()));
            // featureOfInterestTypes
            getCache().setAllowedFeatureOfInterestTypeForOffering(offeringId, 
                    getFeatureOfInterestTypesFromFeatureOfInterestType(((TOffering) getOffering()).getFeatureOfInterestTypes()));
        }
        
        // Features of Interest
        List<String> featureOfInterestIdentifiers =
                new FeatureOfInterestDAO().getFeatureOfInterestIdentifiersForOffering(dsOfferingId, session);
        getCache().setFeaturesOfInterestForOffering(offeringId,
                getValidFeaturesOfInterestFrom(featureOfInterestIdentifiers));
        getCache().setFeatureOfInterestTypesForOffering(offeringId, getFeatureOfInterestTypes(featureOfInterestIdentifiers, session));
        
        // Spatial Envelope
        getCache().setEnvelopeForOffering(offeringId, getEnvelopeForOffering(featureOfInterestIdentifiers, session));
        // Spatial Filtering Profile Spatial Envelope
        if (HibernateHelper.isEntitySupported(SpatialFilteringProfile.class, session)) {
            getCache().setSpatialFilteringProfileEnvelopeForOffering(offeringId,
                    getSpatialFilteringProfileEnvelopeForOffering(dsOfferingId, session));
        }

        // Temporal Envelope
        getCache().setMinPhenomenonTimeForOffering(offeringId, offeringDAO.getMinDate4Offering(dsOfferingId, session));
        getCache().setMaxPhenomenonTimeForOffering(offeringId, offeringDAO.getMaxDate4Offering(dsOfferingId, session));
        getCache().setMinResultTimeForOffering(offeringId,
                offeringDAO.getMinResultTime4Offering(dsOfferingId, session));
        getCache().setMaxResultTimeForOffering(offeringId,
                offeringDAO.getMaxResultTime4Offering(dsOfferingId, session));
    }

    protected Map<ProcedureFlag, Set<String>> getProcedureIdentifier(Session session) {
        Set<String> procedures = new HashSet<String>(0);
        Set<String> hiddenChilds = new HashSet<String>(0);
        if (CollectionHelper.isNotEmpty(getObservationConstellations())) {
            for (ObservationConstellation oc : getObservationConstellations()) {
                if (oc.isHiddenChild()) {
                    hiddenChilds.add(CacheHelper.addPrefixOrGetProcedureIdentifier(oc.getProcedure().getIdentifier()));
                } else {
                    procedures.add(CacheHelper.addPrefixOrGetProcedureIdentifier(oc.getProcedure().getIdentifier()));
                }
            }
        } else {
            List<String> list =
                    new ProcedureDAO().getProcedureIdentifiersForOffering(getOffering().getIdentifier(), session);
            if (list.size() > 1) {
                throw new RuntimeException(String.format(
                        "There are more than one procedures defined for the offering '%s'!", getOffering()
                                .getIdentifier()));
            }
            for (String procedureIdentifier : list) {
                procedures.add(CacheHelper.addPrefixOrGetProcedureIdentifier(procedureIdentifier));
            }
        }
        Map<ProcedureFlag, Set<String>> allProcedures = Maps.newEnumMap(ProcedureFlag.class);
        allProcedures.put(ProcedureFlag.PARENT, procedures);
        allProcedures.put(ProcedureFlag.HIDDEN_CHILD, hiddenChilds);
        return allProcedures;
    }

    protected Set<String> getRelatedFeatureIdentifiersFrom(TOffering hOffering) {
        Set<String> relatedFeatureList = new HashSet<String>(hOffering.getRelatedFeatures().size());
        for (RelatedFeature hRelatedFeature : hOffering.getRelatedFeatures()) {
            if (hRelatedFeature.getFeatureOfInterest() != null
                    && hRelatedFeature.getFeatureOfInterest().getIdentifier() != null) {
                relatedFeatureList.add(hRelatedFeature.getFeatureOfInterest().getIdentifier());
            }
        }
        return relatedFeatureList;
    }

    protected Collection<String> getValidFeaturesOfInterestFrom(List<String> featureOfInterestIdentifiers) {
        Set<String> features = new HashSet<String>(featureOfInterestIdentifiers.size());
        for (String featureIdentifier : featureOfInterestIdentifiers) {
            features.add(CacheHelper.addPrefixOrGetFeatureIdentifier(featureIdentifier));
        }
        return features;
    }

    protected Set<String> getObservablePropertyIdentifier(Session session) {
        if (CollectionHelper.isNotEmpty(getObservationConstellations())) {
            return DatasourceCacheUpdateHelper.getAllObservablePropertyIdentifiersFrom(getObservationConstellations());
        } else {
            Set<String> observableProperties = Sets.newHashSet();
            List<String> list =
                    new ObservablePropertyDAO().getObservablePropertyIdentifiersForOffering(getOffering()
                            .getIdentifier(), session);
            for (String observablePropertyIdentifier : list) {
                observableProperties.add(CacheHelper
                        .addPrefixOrGetObservablePropertyIdentifier(observablePropertyIdentifier));
            }
            return observableProperties;
        }
    }

    protected Set<String> getObservationTypes(Session session) {
        if (CollectionHelper.isNotEmpty(getObservationConstellations())) {
            Set<String> observationTypes = Sets.newHashSet();
            for (ObservationConstellation oc : getObservationConstellations()) {
                if (oc.getObservationType() != null && oc.getObservationType().getObservationType() != null) {
                    observationTypes.add(oc.getObservationType().getObservationType());
                }
            }
            return observationTypes;
        } else {
            return getObservationTypesFromObservations(session);
        }
    }

    private Set<String> getObservationTypesFromObservations(Session session) {
        ObservationDAO observationDAO = new ObservationDAO();
        Set<String> observationTypes = Sets.newHashSet();
        if (observationDAO.checkNumericObservationsFor(getOffering().getIdentifier(), session)) {
            observationTypes.add(OmConstants.OBS_TYPE_MEASUREMENT);
        } else if (observationDAO.checkCategoryObservationsFor(getOffering().getIdentifier(), session)) {
            observationTypes.add(OmConstants.OBS_TYPE_CATEGORY_OBSERVATION);
        } else if (observationDAO.checkCountObservationsFor(getOffering().getIdentifier(), session)) {
            observationTypes.add(OmConstants.OBS_TYPE_COUNT_OBSERVATION);
        } else if (observationDAO.checkTextObservationsFor(getOffering().getIdentifier(), session)) {
            observationTypes.add(OmConstants.OBS_TYPE_TEXT_OBSERVATION);
        } else if (observationDAO.checkBooleanObservationsFor(getOffering().getIdentifier(), session)) {
            observationTypes.add(OmConstants.OBS_TYPE_TRUTH_OBSERVATION);
        } else if (observationDAO.checkBlobObservationsFor(getOffering().getIdentifier(), session)) {
            observationTypes.add(OmConstants.OBS_TYPE_OBSERVATION);
        } else if (observationDAO.checkGeometryObservationsFor(getOffering().getIdentifier(), session)) {
            observationTypes.add(OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION);
        } else if (observationDAO.checkSweDataArrayObservationsFor(getOffering().getIdentifier(), session)) {
            observationTypes.add(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
        }
        return observationTypes;
    }

    protected Set<String> getFeatureOfInterestTypes(List<String> featureOfInterestIdentifiers, Session session) {
        if (CollectionHelper.isNotEmpty(featureOfInterestIdentifiers)) {
            List<FeatureOfInterest> featureOfInterestObjects = featureDAO.getFeatureOfInterestObject(featureOfInterestIdentifiers, session);
            if (CollectionHelper.isNotEmpty(featureOfInterestObjects)) {
                Set<String> featureTypes = Sets.newHashSet();
                for (FeatureOfInterest featureOfInterest : featureOfInterestObjects) {
                    if (!OGCConstants.UNKNOWN.equals(featureOfInterest.getFeatureOfInterestType().getFeatureOfInterestType())) {
                        featureTypes.add(featureOfInterest.getFeatureOfInterestType().getFeatureOfInterestType());
                    }
                }
                return featureTypes;
            }
        }
        return Sets.newHashSet();
    }

    protected SosEnvelope getEnvelopeForOffering(List<String> featureOfInterestIdentifiers, Session session)
            throws OwsExceptionReport {
        if (CollectionHelper.isNotEmpty(featureOfInterestIdentifiers)) {
            return Configurator.getInstance().getFeatureQueryHandler()
                    .getEnvelopeForFeatureIDs(featureOfInterestIdentifiers, session);
        }
        return null;
    }

    /**
     * Get SpatialFilteringProfile envelope if exist and supported
     * 
     * @param offeringID
     *            Offering identifier to get envelope for
     * @param session
     *            Hibernate sessio
     * @return SpatialFilteringProfile envelope for offering
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    protected SosEnvelope getSpatialFilteringProfileEnvelopeForOffering(String offeringID, Session session)
            throws OwsExceptionReport {
        return new SpatialFilteringProfileDAO().getEnvelopeForOfferingId(offeringID, session);
        // List<Long> observationIds = new
        // ObservationDAO().getObervationIds(offeringID, session);
        // if (CollectionHelper.isNotEmpty(observationIds)) {
        // return new
        // SpatialFilteringProfileDAO().getEnvelopeForOfferingId(offeringID,
        // session);
        // }
        // return null;
    }

    protected Set<String> getObservationTypesFromObservationType(Set<ObservationType> observationTypes) {
        Set<String> obsTypes = new HashSet<String>(observationTypes.size());
        for (ObservationType obsType : observationTypes) {
            obsTypes.add(obsType.getObservationType());
        }
        return obsTypes;
    }

    protected Collection<String> getFeatureOfInterestTypesFromFeatureOfInterestType(
            Set<FeatureOfInterestType> featureOfInterestTypes) {
        Set<String> featTypes = new HashSet<String>(featureOfInterestTypes.size());
        for (FeatureOfInterestType featType : featureOfInterestTypes) {
            featTypes.add(featType.getFeatureOfInterestType());
        }
        return featTypes;
    }

    private enum ProcedureFlag {
        PARENT, HIDDEN_CHILD;
    }

    @Override
    public void execute() {
        try {
            getOfferingInformationFromDbAndAddItToCacheMaps(getSessionFactory().getSession());
        } catch (OwsExceptionReport owse) {
            getErrors().add(owse);
        } catch (Exception e) {
            getErrors().add(
                    new GenericThrowableWrapperException(e)
                            .withMessage("Error while processing offering cache update task!"));
        } finally {
            LOGGER.debug("OfferingTask finished, latch.countDown().");
            getCountDownLatch().countDown();
        }
    }
}
