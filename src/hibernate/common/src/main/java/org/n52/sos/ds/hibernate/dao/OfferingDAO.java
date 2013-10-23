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
package org.n52.sos.ds.hibernate.dao;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterestType;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Observation;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.ObservationInfo;
import org.n52.sos.ds.hibernate.entities.ObservationType;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.RelatedFeature;
import org.n52.sos.ds.hibernate.entities.TOffering;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Hibernate data access class for offering
 * 
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class OfferingDAO extends TimeCreator implements HibernateSqlQueryConstants {

    private static final String SQL_QUERY_GET_MIN_DATE_FOR_OFFERING = "getMinDate4Offering";

    private static final String SQL_QUERY_GET_MAX_DATE_FOR_OFFERING = "getMaxDate4Offering";

    private static final String SQL_QUERY_GET_MIN_RESULT_TIME_FOR_OFFERING = "getMinResultTime4Offering";

    private static final String SQL_QUERY_GET_MAX_RESULT_TIME_FOR_OFFERING = "getMaxResultTime4Offering";

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OfferingDAO.class);

    /**
     * Get transactional offering object for identifier
     * 
     * @param identifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return Transactional offering object
     */
    public TOffering getTOfferingForIdentifier(final String identifier, final Session session) {
        Criteria criteria =
                session.createCriteria(TOffering.class).add(Restrictions.eq(Offering.IDENTIFIER, identifier));
        LOGGER.debug("QUERY getTOfferingForIdentifier(): {}", HibernateHelper.getSqlString(criteria));
        return (TOffering) criteria.uniqueResult();
    }

    /**
     * Get all offering objects
     * 
     * @param session
     *            Hibernate session
     * @return Offering objects
     */
    @SuppressWarnings("unchecked")
    public List<Offering> getOfferingObjects(final Session session) {
        Criteria criteria = session.createCriteria(Offering.class);
        LOGGER.debug("QUERY getOfferingObjects(): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Get Offering object for identifier
     * 
     * @param identifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return Offering object
     */
    public Offering getOfferingForIdentifier(final String identifier, final Session session) {
        Criteria criteria =
                session.createCriteria(Offering.class).add(Restrictions.eq(Offering.IDENTIFIER, identifier));
        LOGGER.debug("QUERY getOfferingForIdentifier(identifier): {}", HibernateHelper.getSqlString(criteria));
        return (Offering) criteria.uniqueResult();
    }

    /**
     * Get Offering objects for identifiers
     * 
     * @param identifiers
     *            Offering identifiers
     * @param session
     *            Hibernate session
     * @return Offering objects
     */
    @SuppressWarnings("unchecked")
    public Collection<Offering> getOfferingsForIdentifiers(final Collection<String> identifiers, final Session session) {
        Criteria criteria =
                session.createCriteria(Offering.class).add(Restrictions.in(Offering.IDENTIFIER, identifiers));
        LOGGER.debug("QUERY getOfferingsForIdentifiers(identifiers): {}", HibernateHelper.getSqlString(criteria));
        return (List<Offering>) criteria.list();
    }

    /**
     * Get offering identifiers for procedure identifier
     * 
     * @param procedureIdentifier
     *            Procedure identifier
     * @param session
     *            Hibernate session
     * @return Offering identifiers
     */
    @SuppressWarnings("unchecked")
    public List<String> getOfferingIdentifiersForProcedure(final String procedureIdentifier, final Session session) {
        final boolean flag = HibernateHelper.isEntitySupported(ObservationConstellation.class, session);
        Criteria c = null;
        if (flag) {
            c = session.createCriteria(Offering.class);
            c.add(Subqueries.propertyIn(Offering.ID, getDetachedCriteriaProcedure(procedureIdentifier, session)));
            c.setProjection(Projections.distinct(Projections.property(Offering.IDENTIFIER)));
        } else {
            c = session.createCriteria(ObservationInfo.class).add(Restrictions.eq(Observation.DELETED, false));
            c.createCriteria(ObservationInfo.OFFERINGS).setProjection(
                    Projections.distinct(Projections.property(Offering.IDENTIFIER)));
            c.createCriteria(ObservationInfo.PROCEDURE)
                    .add(Restrictions.eq(Procedure.IDENTIFIER, procedureIdentifier));
        }
        LOGGER.debug(
                "QUERY getOfferingIdentifiersForProcedure(procedureIdentifier) using ObservationContellation entitiy ({}): {}",
                flag, HibernateHelper.getSqlString(c));
        return c.list();
    }

    /**
     * Get offering identifiers for observable property identifier
     * 
     * @param observablePropertyIdentifier
     *            Observable property identifier
     * @param session
     *            Hibernate session
     * @return Offering identifiers
     */
    @SuppressWarnings("unchecked")
    public Collection<String> getOfferingIdentifiersForObservableProperty(final String observablePropertyIdentifier,
            final Session session) {
        final boolean flag = HibernateHelper.isEntitySupported(ObservationConstellation.class, session);
        Criteria c = null;
        if (flag) {
            c = session.createCriteria(Offering.class);
            c.add(Subqueries.propertyIn(Offering.ID,
                    getDetachedCriteriaObservableProperty(observablePropertyIdentifier, session)));
            c.setProjection(Projections.distinct(Projections.property(Offering.IDENTIFIER)));
        } else {
            c = session.createCriteria(ObservationInfo.class).add(Restrictions.eq(Observation.DELETED, false));
            c.createCriteria(ObservationInfo.OFFERINGS).setProjection(
                    Projections.distinct(Projections.property(Offering.IDENTIFIER)));
            c.createCriteria(ObservationInfo.OBSERVABLE_PROPERTY).add(
                    Restrictions.eq(ObservableProperty.IDENTIFIER, observablePropertyIdentifier));
        }
        LOGGER.debug(
                "QUERY getOfferingIdentifiersForObservableProperty(observablePropertyIdentifier) using ObservationContellation entitiy ({}): {}",
                flag, HibernateHelper.getSqlString(c));
        return c.list();
    }

    /**
     * Get min time from observations for offering
     * 
     * @param offering
     *            Offering identifier
     * @param session
     *            Hibernate session Hibernate session
     * @return min time for offering
     */
    public DateTime getMinDate4Offering(final String offering, final Session session) {
        Object min = null;
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_MIN_DATE_FOR_OFFERING, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_MIN_DATE_FOR_OFFERING);
            namedQuery.setParameter(OFFERING, offering);
            LOGGER.debug("QUERY getMinDate4Offering(offering) with NamedQuery: {}",
                    SQL_QUERY_GET_MIN_DATE_FOR_OFFERING);
            min = namedQuery.uniqueResult();
        } else {
            final Criteria criteria =
                    session.createCriteria(ObservationInfo.class)
                            .setProjection(Projections.min(Observation.PHENOMENON_TIME_START))
                            .add(Restrictions.eq(Observation.DELETED, false));
            criteria.createCriteria(Observation.OFFERINGS).add(Restrictions.eq(Offering.IDENTIFIER, offering));
            LOGGER.debug("QUERY getMinDate4Offering(offering): {}", HibernateHelper.getSqlString(criteria));
            min = criteria.uniqueResult();
        }
        if (min != null) {
            return new DateTime(min, DateTimeZone.UTC);
        }
        return null;
    }

    /**
     * Get max time from observations for offering
     * 
     * @param offering
     *            Offering identifier
     * @param session
     *            Hibernate session Hibernate session
     * @return max time for offering
     */
    public DateTime getMaxDate4Offering(final String offering, final Session session) {
        Object maxStart = null;
        Object maxEnd = null;
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_MAX_DATE_FOR_OFFERING, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_MAX_DATE_FOR_OFFERING);
            namedQuery.setParameter(OFFERING, offering);
            LOGGER.debug("QUERY getMaxDate4Offering(offering) with NamedQuery: {}",
                    SQL_QUERY_GET_MAX_DATE_FOR_OFFERING);
            maxStart = namedQuery.uniqueResult();
            maxEnd = maxStart;
        } else {
            final Criteria cstart =
                    session.createCriteria(ObservationInfo.class).add(Restrictions.eq(Observation.DELETED, false))
                            .setProjection(Projections.max(Observation.PHENOMENON_TIME_START));
            cstart.createCriteria(Observation.OFFERINGS).add(Restrictions.eq(Offering.IDENTIFIER, offering));
            LOGGER.debug("QUERY getMaxDate4Offering(offering) start: {}", HibernateHelper.getSqlString(cstart));

            final Criteria cend =
                    session.createCriteria(ObservationInfo.class).add(Restrictions.eq(Observation.DELETED, false))
                            .setProjection(Projections.max(Observation.PHENOMENON_TIME_END));
            cend.createCriteria(Observation.OFFERINGS).add(Restrictions.eq(Offering.IDENTIFIER, offering));
            LOGGER.debug("QUERY getMaxDate4Offering(offering) end: {}", HibernateHelper.getSqlString(cend));
            if (HibernateHelper.getSqlString(cstart).equals(HibernateHelper.getSqlString(cend))) {
                maxStart = cstart.uniqueResult();
                maxEnd = maxStart;
                LOGGER.debug("Max time start and end query are identically, only one query is executed!");
            } else {
                maxStart = cstart.uniqueResult();
                maxEnd = cend.uniqueResult();
            }
        }
        if (maxStart == null && maxEnd == null) {
            return null;
        } else {
            final DateTime start = new DateTime(maxStart, DateTimeZone.UTC);
            if (maxEnd != null) {
                final DateTime end = new DateTime(maxEnd, DateTimeZone.UTC);
                if (end.isAfter(start)) {
                    return end;
                }
            }
            return start;
        }
    }

    /**
     * Get min result time from observations for offering
     * 
     * @param offering
     *            Offering identifier
     * @param session
     *            Hibernate session Hibernate session
     * 
     * @return min result time for offering
     */
    public DateTime getMinResultTime4Offering(final String offering, final Session session) {
        Object min = null;
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_MIN_RESULT_TIME_FOR_OFFERING, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_MIN_RESULT_TIME_FOR_OFFERING);
            namedQuery.setParameter(OFFERING, offering);
            LOGGER.debug("QUERY getMinResultTime4Offering(offering) with NamedQuery: {}",
                    SQL_QUERY_GET_MIN_RESULT_TIME_FOR_OFFERING);
            min = namedQuery.uniqueResult();
        } else {
            final Criteria criteria =
                    session.createCriteria(ObservationInfo.class).add(Restrictions.eq(Observation.DELETED, false))
                            .setProjection(Projections.min(Observation.RESULT_TIME));

            criteria.createCriteria(Observation.OFFERINGS).add(Restrictions.eq(Offering.IDENTIFIER, offering));
            LOGGER.debug("QUERY getMinResultTime4Offering(offering): {}", HibernateHelper.getSqlString(criteria));
            min = criteria.uniqueResult();
        }
        if (min != null) {
            return new DateTime(min, DateTimeZone.UTC);
        }
        return null;
    }

    /**
     * Get max result time from observations for offering
     * 
     * @param offering
     *            Offering identifier
     * @param session
     *            Hibernate session Hibernate session
     * 
     * @return max result time for offering
     */
    public DateTime getMaxResultTime4Offering(final String offering, final Session session) {
        Object maxStart = null;
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_MAX_RESULT_TIME_FOR_OFFERING, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_MAX_RESULT_TIME_FOR_OFFERING);
            namedQuery.setParameter(OFFERING, offering);
            LOGGER.debug("QUERY getMaxResultTime4Offering(offering) with NamedQuery: {}",
                    SQL_QUERY_GET_MAX_RESULT_TIME_FOR_OFFERING);
            maxStart = namedQuery.uniqueResult();
        } else {
            final Criteria c =
                    session.createCriteria(ObservationInfo.class).add(Restrictions.eq(Observation.DELETED, false))
                            .setProjection(Projections.max(Observation.RESULT_TIME));
            c.createCriteria(Observation.OFFERINGS).add(Restrictions.eq(Offering.IDENTIFIER, offering));
            LOGGER.debug("QUERY getMaxResultTime4Offering(offering): {}", HibernateHelper.getSqlString(c));
            maxStart = c.uniqueResult();
        }

        if (maxStart == null) {
            return null;
        } else {
            return new DateTime(maxStart, DateTimeZone.UTC);
        }
    }

    /**
     * Get temporal bounding box for each offering
     * 
     * @param session
     *            Hibernate session
     * @return a Map containing the temporal bounding box for each offering
     */
    public Map<String, TimePeriod> getTemporalBoundingBoxesForOfferings(final Session session) {
        if (session != null) {
            final Criteria criteria =
                    session.createCriteria(ObservationInfo.class).add(Restrictions.eq(Observation.DELETED, false));
            criteria.createAlias(Observation.OFFERINGS, "off");
            criteria.setProjection(Projections.projectionList()
                    .add(Projections.min(Observation.PHENOMENON_TIME_START))
                    .add(Projections.max(Observation.PHENOMENON_TIME_START))
                    .add(Projections.max(Observation.PHENOMENON_TIME_END))
                    .add(Projections.groupProperty("off." + Offering.IDENTIFIER)));
            LOGGER.debug("QUERY getTemporalBoundingBoxesForOfferings(): {}", HibernateHelper.getSqlString(criteria));
            final List<?> temporalBoundingBoxes = criteria.list();
            if (!temporalBoundingBoxes.isEmpty()) {
                final HashMap<String, TimePeriod> temporalBBoxMap =
                        new HashMap<String, TimePeriod>(temporalBoundingBoxes.size());
                for (final Object recordObj : temporalBoundingBoxes) {
                    if (recordObj instanceof Object[]) {
                        final Object[] record = (Object[]) recordObj;
                        final TimePeriod value =
                                createTimePeriod((Timestamp) record[0], (Timestamp) record[1], (Timestamp) record[2]);
                        temporalBBoxMap.put((String) record[3], value);
                    }
                }
                LOGGER.debug(temporalBoundingBoxes.toString());
                return temporalBBoxMap;
            }
        }
        return new HashMap<String, TimePeriod>(0);
    }

    /**
     * Insert or update and get offering
     * 
     * @param offeringIdentifier
     *            Offering identifier
     * @param offeringName
     *            Offering name
     * @param relatedFeatures
     *            Related feature objects
     * @param observationTypes
     *            Allowed observation type objects
     * @param featureOfInterestTypes
     *            Allowed featureOfInterest type objects
     * @param session
     *            Hibernate session
     * @return Offering object
     */
    public Offering getAndUpdateOrInsertNewOffering(final String offeringIdentifier, final String offeringName,
            final List<RelatedFeature> relatedFeatures, final List<ObservationType> observationTypes,
            final List<FeatureOfInterestType> featureOfInterestTypes, final Session session) {

        TOffering offering = getTOfferingForIdentifier(offeringIdentifier, session);
        if (offering == null) {
            offering = new TOffering();
            offering.setIdentifier(offeringIdentifier);
            if (offeringName != null) {
                offering.setName(offeringName);
            } else {
                offering.setName("Offering for the procedure " + offeringIdentifier);
            }
        }
        if (!relatedFeatures.isEmpty()) {
            offering.setRelatedFeatures(new HashSet<RelatedFeature>(relatedFeatures));
        } else {
            offering.setRelatedFeatures(new HashSet<RelatedFeature>(0));
        }
        if (!observationTypes.isEmpty()) {
            offering.setObservationTypes(new HashSet<ObservationType>(observationTypes));
        } else {
            offering.setObservationTypes(new HashSet<ObservationType>(0));
        }
        if (!featureOfInterestTypes.isEmpty()) {
            offering.setFeatureOfInterestTypes(new HashSet<FeatureOfInterestType>(featureOfInterestTypes));
        } else {
            offering.setFeatureOfInterestTypes(new HashSet<FeatureOfInterestType>(0));
        }
        session.saveOrUpdate(offering);
        session.flush();
        session.refresh(offering);
        return offering;
    }

    private DetachedCriteria getDetachedCriteriaObservableProperty(String observablePropertyIdentifier, Session session) {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(ObservationConstellation.class);
        detachedCriteria.add(Restrictions.eq(ObservationConstellation.DELETED, false));
        detachedCriteria.createCriteria(ObservationConstellation.OBSERVABLE_PROPERTY).add(
                Restrictions.eq(ObservableProperty.IDENTIFIER, observablePropertyIdentifier));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(ObservationConstellation.OFFERING)));
        return detachedCriteria;
    }

    private DetachedCriteria getDetachedCriteriaProcedure(String procedureIdentifier, Session session) {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(ObservationConstellation.class);
        detachedCriteria.add(Restrictions.eq(ObservationConstellation.DELETED, false));
        detachedCriteria.createCriteria(ObservationConstellation.PROCEDURE).add(
                Restrictions.eq(Procedure.IDENTIFIER, procedureIdentifier));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(ObservationConstellation.OFFERING)));
        return detachedCriteria;
    }

    public List<String> getAllowedFeatureOfInterestTypes(String identifier, Session session) {
        if (HibernateHelper.isEntitySupported(TOffering.class, session)) {
            Criteria criteria =
                    session.createCriteria(TOffering.class).add(Restrictions.eq(Offering.IDENTIFIER, identifier));
            LOGGER.debug("QUERY getAllowedFeatureOfInterestTypes(offering): {}", HibernateHelper.getSqlString(criteria));
            TOffering offering = (TOffering) criteria.uniqueResult();
            if (offering != null) {
                List<String> list = Lists.newArrayList();
                for (FeatureOfInterestType featureOfInterestType : offering.getFeatureOfInterestTypes()) {
                    list.add(featureOfInterestType.getFeatureOfInterestType());
                }
                return list;
            }
        }
        return Lists.newArrayList();
    }
}
