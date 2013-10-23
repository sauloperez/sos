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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Observation;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.ObservationInfo;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.ProcedureDescriptionFormat;
import org.n52.sos.ds.hibernate.entities.TProcedure;
import org.n52.sos.ds.hibernate.entities.ValidProcedureTime;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.QueryHelper;
import org.n52.sos.exception.ows.concrete.UnsupportedOperatorException;
import org.n52.sos.exception.ows.concrete.UnsupportedTimeException;
import org.n52.sos.exception.ows.concrete.UnsupportedValueReferenceException;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * Hibernate data access class for procedure
 * 
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class ProcedureDAO implements HibernateSqlQueryConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcedureDAO.class);

    private static final String SQL_QUERY_GET_PROCEDURES_FOR_FEATURE_OF_INTEREST = "getProceduresForFeatureOfInterest";

    private static final String SQL_QUERY_GET_MIN_DATE_FOR_PROCEDURE = "getMinDate4Procedure";

    private static final String SQL_QUERY_GET_MAX_DATE_FOR_PROCEDURE = "getMaxDate4Procedure";

    /**
     * Get all procedure objects
     * 
     * @param session
     *            Hibernate session
     * @return Procedure objects
     */
    @SuppressWarnings("unchecked")
    public List<Procedure> getProcedureObjects(final Session session) {
        Criteria criteria = session.createCriteria(Procedure.class);
        LOGGER.debug("QUERY getProcedureObjects(): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Get Procedure object for procedure identifier
     * 
     * @param identifier
     *            Procedure identifier
     * @param session
     *            Hibernate session
     * @return Procedure object
     */
    public Procedure getProcedureForIdentifier(final String identifier, final Session session) {
        Criteria criteria =
                session.createCriteria(Procedure.class).add(Restrictions.eq(Procedure.IDENTIFIER, identifier));
        if (HibernateHelper.isEntitySupported(TProcedure.class, session)) {
            criteria.createCriteria(TProcedure.VALID_PROCEDURE_TIME).add(
                    Restrictions.isNull(ValidProcedureTime.END_TIME));
        }
        LOGGER.debug("QUERY getProcedureForIdentifier(identifier): {}", HibernateHelper.getSqlString(criteria));
        return (Procedure) criteria.uniqueResult();
    }

    /**
     * Get Procedure object for procedure identifier
     * 
     * @param identifier
     *            Procedure identifier
     * @param session
     *            Hibernate session
     * @return Procedure object
     */
    public Procedure getProcedureForIdentifier(final String identifier, Time time, final Session session) {
        Criteria criteria =
                session.createCriteria(Procedure.class).add(Restrictions.eq(Procedure.IDENTIFIER, identifier));
        LOGGER.debug("QUERY getProcedureForIdentifier(identifier): {}", HibernateHelper.getSqlString(criteria));
        return (Procedure) criteria.uniqueResult();
    }

    /**
     * Get Procedure objects for procedure identifiers
     * 
     * @param identifiers
     *            Procedure identifiers
     * @param session
     *            Hibernate session
     * @return Procedure objects
     */
    @SuppressWarnings("unchecked")
    public List<Procedure> getProceduresForIdentifiers(final Collection<String> identifiers, final Session session) {
        if (identifiers == null || identifiers.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        Criteria criteria =
                session.createCriteria(Procedure.class).add(Restrictions.in(Procedure.IDENTIFIER, identifiers));
        LOGGER.debug("QUERY getProceduresForIdentifiers(identifiers): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Get procedure identifiers for FOI
     * 
     * @param session
     *            Hibernate session
     * @param feature
     *            FOI object
     * 
     * @return Related procedure identifiers
     */
    @SuppressWarnings("unchecked")
    public List<String> getProceduresForFeatureOfInterest(final Session session, final FeatureOfInterest feature) {
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_PROCEDURES_FOR_FEATURE_OF_INTEREST, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_PROCEDURES_FOR_FEATURE_OF_INTEREST);
            namedQuery.setParameter(FEATURE, feature.getIdentifier());
            LOGGER.debug("QUERY getProceduresForFeatureOfInterest(feature) with NamedQuery: {}",
                    SQL_QUERY_GET_PROCEDURES_FOR_FEATURE_OF_INTEREST);
            return namedQuery.list();
        } else {
            final Criteria c = session.createCriteria(ObservationInfo.class);
            c.add(Restrictions.eq(ObservationInfo.DELETED, false));
            c.createCriteria(ObservationInfo.FEATURE_OF_INTEREST).add(
                    Restrictions.eq(FeatureOfInterest.IDENTIFIER, feature.getIdentifier()));
            c.createCriteria(ObservationInfo.PROCEDURE).setProjection(
                    Projections.distinct(Projections.property(Procedure.IDENTIFIER)));
            LOGGER.debug("QUERY getProceduresForFeatureOfInterest(feature): {}", HibernateHelper.getSqlString(c));
            return (List<String>) c.list();
        }
    }

    /**
     * Get procedure identifiers for offering identifier
     * 
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return Procedure identifiers
     */
    @SuppressWarnings("unchecked")
    public List<String> getProcedureIdentifiersForOffering(final String offeringIdentifier, final Session session) {
        final boolean flag = HibernateHelper.isEntitySupported(ObservationConstellation.class, session);
        Criteria c = null;
        if (flag) {
            c = session.createCriteria(Procedure.class);
            c.add(Subqueries.propertyIn(Procedure.ID, getDetachedCriteriaOffering(offeringIdentifier, session)));
            c.setProjection(Projections.distinct(Projections.property(Procedure.IDENTIFIER)));
        } else {
            c = session.createCriteria(ObservationInfo.class).add(Restrictions.eq(Observation.DELETED, false));
            c.createCriteria(ObservationInfo.PROCEDURE).setProjection(
                    Projections.distinct(Projections.property(Procedure.IDENTIFIER)));
            c.createCriteria(ObservationInfo.OFFERINGS).add(Restrictions.eq(Offering.IDENTIFIER, offeringIdentifier));
        }
        LOGGER.debug(
                "QUERY getProcedureIdentifiersForOffering(offeringIdentifier) using ObservationContellation entitiy ({}): {}",
                flag, HibernateHelper.getSqlString(c));
        return c.list();
    }

    /**
     * Get procedure identifiers for observable property identifier
     * 
     * @param observablePropertyIdentifier
     *            Observable property identifier
     * @param session
     *            Hibernate session
     * @return Procedure identifiers
     */
    @SuppressWarnings("unchecked")
    public Collection<String> getProcedureIdentifiersForObservableProperty(final String observablePropertyIdentifier,
            final Session session) {
        final boolean flag = HibernateHelper.isEntitySupported(ObservationConstellation.class, session);
        Criteria c = null;
        if (flag) {
            c = session.createCriteria(Procedure.class);
            c.add(Subqueries.propertyIn(Procedure.ID,
                    getDetachedCriteriaObservableProperty(observablePropertyIdentifier, session)));
            c.setProjection(Projections.distinct(Projections.property(Procedure.IDENTIFIER)));
        } else {
            c = session.createCriteria(ObservationInfo.class).add(Restrictions.eq(Observation.DELETED, false));
            c.createCriteria(ObservationInfo.PROCEDURE).setProjection(
                    Projections.distinct(Projections.property(Procedure.IDENTIFIER)));
            c.createCriteria(ObservationInfo.OBSERVABLE_PROPERTY).add(
                    Restrictions.eq(ObservableProperty.IDENTIFIER, observablePropertyIdentifier));
        }
        LOGGER.debug(
                "QUERY getProcedureIdentifiersForObservableProperty(observablePropertyIdentifier) using ObservationContellation entitiy ({}): {}",
                flag, HibernateHelper.getSqlString(c));
        return c.list();
    }

    /**
     * Get transactional procedure object for procedure identifier
     * 
     * @param identifier
     *            Procedure identifier
     * @param session
     *            Hibernate session
     * @return Transactional procedure object
     */
    public TProcedure getTProcedureForIdentifier(final String identifier, final Session session) {
        Criteria criteria =
                session.createCriteria(TProcedure.class).add(Restrictions.eq(Procedure.IDENTIFIER, identifier));
        LOGGER.debug("QUERY getTProcedureForIdentifier(identifier): {}", HibernateHelper.getSqlString(criteria));
        return (TProcedure) criteria.uniqueResult();
    }

    /**
     * Get transactional procedure object for procedure identifier and
     * procedureDescriptionFormat
     * 
     * @param identifier
     *            Procedure identifier
     * @param procedureDescriptionFormat
     *            ProcedureDescriptionFormat identifier
     * @param session
     *            Hibernate session
     * @return Transactional procedure object
     * @throws UnsupportedOperatorException
     * @throws UnsupportedValueReferenceException
     * @throws UnsupportedTimeException
     */
    public TProcedure getTProcedureForIdentifier(final String identifier, String procedureDescriptionFormat,
            Time validTime, final Session session) throws UnsupportedTimeException,
            UnsupportedValueReferenceException, UnsupportedOperatorException {
        Criteria criteria =
                session.createCriteria(TProcedure.class).add(Restrictions.eq(Procedure.IDENTIFIER, identifier));
        Criteria createValidProcedureTime = criteria.createCriteria(TProcedure.VALID_PROCEDURE_TIME);
        Criterion validTimeCriterion = QueryHelper.getValidTimeCriterion(validTime);
        if (validTime == null || validTimeCriterion == null) {
            createValidProcedureTime.add(Restrictions.isNull(ValidProcedureTime.END_TIME));
        } else {
            createValidProcedureTime.add(validTimeCriterion);
        }
        createValidProcedureTime.createCriteria(ValidProcedureTime.PROCEDURE_DESCRIPTION_FORMAT).add(
                Restrictions.eq(ProcedureDescriptionFormat.PROCEDURE_DESCRIPTION_FORMAT, procedureDescriptionFormat));
        LOGGER.debug("QUERY getTProcedureForIdentifier(identifier): {}", HibernateHelper.getSqlString(criteria));
        return (TProcedure) criteria.uniqueResult();
    }

    /**
     * Get transactional procedure object for procedure identifier and
     * procedureDescriptionFormats
     * 
     * @param identifier
     *            Procedure identifier
     * @param procedureDescriptionFormats
     *            ProcedureDescriptionFormat identifiers
     * @param session
     *            Hibernate session
     * @return Transactional procedure object
     */
    public TProcedure getTProcedureForIdentifier(final String identifier, Set<String> procedureDescriptionFormats,
            final Session session) {
        Criteria criteria =
                session.createCriteria(TProcedure.class).add(Restrictions.eq(Procedure.IDENTIFIER, identifier));
        criteria.createCriteria(TProcedure.VALID_PROCEDURE_TIME).add(
                Restrictions.in(ValidProcedureTime.PROCEDURE_DESCRIPTION_FORMAT, procedureDescriptionFormats));
        LOGGER.debug("QUERY getTProcedureForIdentifier(identifier): {}", HibernateHelper.getSqlString(criteria));
        return (TProcedure) criteria.uniqueResult();
    }

    // protected Criterion getTemporalFilterCriterion(final TemporalFilter
    // filter) throws OwsExceptionReport {
    // return TemporalRestrictions.filter(filters);
    // }

    public TProcedure getTProcedureForIdentifier(String identifier, Set<String> possibleProcedureDescriptionFormats,
            Time validTime, Session session) throws UnsupportedTimeException, UnsupportedValueReferenceException,
            UnsupportedOperatorException {
        Criteria criteria =
                session.createCriteria(TProcedure.class).add(Restrictions.eq(Procedure.IDENTIFIER, identifier));
        Criteria createValidProcedureTime = criteria.createCriteria(TProcedure.VALID_PROCEDURE_TIME);
        Criterion validTimeCriterion = QueryHelper.getValidTimeCriterion(validTime);
        if (validTime == null || validTimeCriterion == null) {
            createValidProcedureTime.add(Restrictions.isNull(ValidProcedureTime.END_TIME));
        } else {
            createValidProcedureTime.add(validTimeCriterion);
        }
        createValidProcedureTime.createCriteria(ValidProcedureTime.PROCEDURE_DESCRIPTION_FORMAT).add(
                Restrictions.in(ProcedureDescriptionFormat.PROCEDURE_DESCRIPTION_FORMAT,
                        possibleProcedureDescriptionFormats));
        LOGGER.debug(
                "QUERY getTProcedureForIdentifier(identifier, possibleProcedureDescriptionFormats, validTime): {}",
                HibernateHelper.getSqlString(criteria));
        return (TProcedure) criteria.uniqueResult();
    }

    /**
     * Get min time from observations for procedure
     * 
     * @param procedure
     *            Procedure identifier
     * @param session
     *            Hibernate session
     * @return min time for procedure
     */
    public DateTime getMinDate4Procedure(final String procedure, final Session session) {
        Object min = null;
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_MIN_DATE_FOR_PROCEDURE, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_MIN_DATE_FOR_PROCEDURE);
            namedQuery.setParameter(PROCEDURE, procedure);
            LOGGER.debug("QUERY getMinDate4Procedure(procedure) with NamedQuery: {}",
                    SQL_QUERY_GET_MIN_DATE_FOR_PROCEDURE);
            min = namedQuery.uniqueResult();
        } else {
            final Criteria criteria =
                    session.createCriteria(ObservationInfo.class)
                            .setProjection(Projections.min(Observation.PHENOMENON_TIME_START))
                            .add(Restrictions.eq(Observation.DELETED, false));
            criteria.createCriteria(Observation.PROCEDURE).add(Restrictions.eq(Procedure.IDENTIFIER, procedure));
            LOGGER.debug("QUERY getMinDate4Procedure(procedure): {}", HibernateHelper.getSqlString(criteria));
            min = criteria.uniqueResult();
        }
        if (min != null) {
            return new DateTime(min, DateTimeZone.UTC);
        }
        return null;
    }

    /**
     * Get max time from observations for procedure
     * 
     * @param procedure
     *            Procedure identifier
     * @param session
     *            Hibernate session
     * @return max time for procedure
     */
    public DateTime getMaxDate4Procedure(final String procedure, final Session session) {
        Object maxStart = null;
        Object maxEnd = null;
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_MAX_DATE_FOR_PROCEDURE, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_MAX_DATE_FOR_PROCEDURE);
            namedQuery.setParameter(PROCEDURE, procedure);
            LOGGER.debug("QUERY getMaxDate4Procedure(procedure) with NamedQuery: {}",
                    SQL_QUERY_GET_MAX_DATE_FOR_PROCEDURE);
            maxStart = namedQuery.uniqueResult();
            maxEnd = maxStart;

        } else {
            final Criteria cstart =
                    session.createCriteria(ObservationInfo.class).add(Restrictions.eq(Observation.DELETED, false))
                            .setProjection(Projections.max(Observation.PHENOMENON_TIME_START));
            cstart.createCriteria(Observation.PROCEDURE).add(Restrictions.eq(Procedure.IDENTIFIER, procedure));
            LOGGER.debug("QUERY getMaxDate4Procedure(procedure) start: {}", HibernateHelper.getSqlString(cstart));

            final Criteria cend =
                    session.createCriteria(ObservationInfo.class).add(Restrictions.eq(Observation.DELETED, false))
                            .setProjection(Projections.max(Observation.PHENOMENON_TIME_END));
            cend.createCriteria(Observation.PROCEDURE).add(Restrictions.eq(Procedure.IDENTIFIER, procedure));
            LOGGER.debug("QUERY getMaxDate4Procedure(procedure) end: {}", HibernateHelper.getSqlString(cend));

            if (HibernateHelper.getSqlString(cstart).endsWith(HibernateHelper.getSqlString(cend))) {
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
     * Insert and get procedure object
     * 
     * @param identifier
     *            Procedure identifier
     * @param procedureDecriptionFormat
     *            Procedure description format object
     * @param parentProcedures
     *            Parent procedure identifiers
     * @param session
     *            Hibernate session
     * @return Procedure object
     */
    public Procedure getOrInsertProcedure(final String identifier,
            final ProcedureDescriptionFormat procedureDecriptionFormat, final Collection<String> parentProcedures,
            final Session session) {
        Procedure procedure = getProcedureForIdentifier(identifier, session);
        if (procedure == null) {
            final TProcedure tProcedure = new TProcedure();
            tProcedure.setProcedureDescriptionFormat(procedureDecriptionFormat);
            tProcedure.setIdentifier(identifier);
            if (CollectionHelper.isNotEmpty(parentProcedures)) {
                tProcedure.setParents(Sets.newHashSet(getProceduresForIdentifiers(parentProcedures, session)));
            }
            procedure = tProcedure;
        }
        procedure.setDeleted(false);
        session.saveOrUpdate(procedure);
        session.flush();
        session.refresh(procedure);
        return procedure;
    }

    private DetachedCriteria getDetachedCriteriaObservableProperty(String observablePropertyIdentifier, Session session) {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(ObservationConstellation.class);
        detachedCriteria.add(Restrictions.eq(ObservationConstellation.DELETED, false));
        detachedCriteria.createCriteria(ObservationConstellation.OBSERVABLE_PROPERTY).add(
                Restrictions.eq(ObservableProperty.IDENTIFIER, observablePropertyIdentifier));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(ObservationConstellation.PROCEDURE)));
        return detachedCriteria;
    }

    private DetachedCriteria getDetachedCriteriaOffering(String offeringIdentifier, Session session) {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(ObservationConstellation.class);
        detachedCriteria.add(Restrictions.eq(ObservationConstellation.DELETED, false));
        detachedCriteria.createCriteria(ObservationConstellation.OFFERING).add(
                Restrictions.eq(Offering.IDENTIFIER, offeringIdentifier));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(ObservationConstellation.PROCEDURE)));
        return detachedCriteria;
    }

}
