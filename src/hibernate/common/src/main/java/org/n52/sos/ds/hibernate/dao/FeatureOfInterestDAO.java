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

import static org.n52.sos.util.http.HTTPStatus.BAD_REQUEST;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterestType;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.ObservationInfo;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.RelatedFeature;
import org.n52.sos.ds.hibernate.entities.TFeatureOfInterest;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.http.HTTPStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hibernate data access class for featureOfInterest
 * 
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class FeatureOfInterestDAO implements HibernateSqlQueryConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureOfInterestDAO.class);

    private static final String SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OFFERING =
            "getFeatureOfInterestIdentifiersForOffering";

    private static final String SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OBSERVATION_CONSTELLATION =
            "getFeatureOfInterestIdentifiersForObservationConstellation";

    /**
     * Get featureOfInterest object for identifier
     * 
     * @param identifier
     *            FeatureOfInterest identifier
     * @param session
     *            Hibernate session Hibernate session
     * @return FeatureOfInterest entity
     */
    public FeatureOfInterest getFeatureOfInterest(final String identifier, final Session session) {
        Criteria criteria =
                session.createCriteria(FeatureOfInterest.class).add(
                        Restrictions.eq(FeatureOfInterest.IDENTIFIER, identifier));
        LOGGER.debug("QUERY getFeatureOfInterest(identifier): {}", HibernateHelper.getSqlString(criteria));
        return (FeatureOfInterest) criteria.uniqueResult();
    }

    /**
     * Get featureOfInterest identifiers for observation constellation
     * 
     * @param observationConstellation
     *            Observation constellation
     * @param session
     *            Hibernate session Hibernate session
     * @return FeatureOfInterest identifiers for observation constellation
     */
    @SuppressWarnings("unchecked")
    public List<String> getFeatureOfInterestIdentifiersForObservationConstellation(
            final ObservationConstellation observationConstellation, final Session session) {
        if (HibernateHelper.isNamedQuerySupported(
                SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OBSERVATION_CONSTELLATION, session)) {
            Query namedQuery =
                    session.getNamedQuery(SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OBSERVATION_CONSTELLATION);
            namedQuery.setParameter(PROCEDURE, observationConstellation.getProcedure().getIdentifier());
            namedQuery.setParameter(OBSERVABLE_PROPERTY, observationConstellation.getObservableProperty()
                    .getIdentifier());
            namedQuery.setParameter(OFFERING, observationConstellation.getOffering().getIdentifier());
            LOGGER.debug(
                    "QUERY getFeatureOfInterestIdentifiersForObservationConstellation(observationConstellation) with NamedQuery: {}",
                    SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OBSERVATION_CONSTELLATION);
            return namedQuery.list();
        } else {
            final Criteria criteria =
                    session.createCriteria(ObservationInfo.class)
                            .add(Restrictions.eq(ObservationInfo.DELETED, false))
                            .add(Restrictions.eq(ObservationInfo.PROCEDURE, observationConstellation.getProcedure()))
                            .add(Restrictions.eq(ObservationInfo.OBSERVABLE_PROPERTY,
                                    observationConstellation.getObservableProperty()));
            criteria.createCriteria(ObservationInfo.OFFERINGS).add(
                    Restrictions.eq(Offering.ID, observationConstellation.getOffering().getOfferingId()));
            criteria.createCriteria(ObservationInfo.FEATURE_OF_INTEREST).setProjection(
                    Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));
            LOGGER.debug(
                    "QUERY getFeatureOfInterestIdentifiersForObservationConstellation(observationConstellation): {}",
                    HibernateHelper.getSqlString(criteria));
            return criteria.list();
        }
    }

    /**
     * Get featureOfInterest identifiers for an offering identifier
     * 
     * @param offeringIdentifiers
     *            Offering identifier
     * @param session
     *            Hibernate session Hibernate session
     * @return FeatureOfInterest identifiers for offering
     */
    @SuppressWarnings("unchecked")
    public List<String> getFeatureOfInterestIdentifiersForOffering(final String offeringIdentifiers,
            final Session session) {
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OFFERING, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OFFERING);
            namedQuery.setParameter(OFFERING, offeringIdentifiers);
            LOGGER.debug("QUERY getFeatureOfInterestIdentifiersForOffering(offeringIdentifiers) with NamedQuery: {}",
                    SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OFFERING);
            return namedQuery.list();
        } else {
            final Criteria c =
                    session.createCriteria(ObservationInfo.class).add(Restrictions.eq(ObservationInfo.DELETED, false));
            c.createCriteria(ObservationInfo.FEATURE_OF_INTEREST).setProjection(
                    Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));
            c.createCriteria(ObservationInfo.OFFERINGS).add(Restrictions.eq(Offering.IDENTIFIER, offeringIdentifiers));
            LOGGER.debug("QUERY getFeatureOfInterestIdentifiersForOffering(offeringIdentifiers): {}",
                    HibernateHelper.getSqlString(c));
            return c.list();
        }
    }

    /**
     * Get featureOfInterest objects for featureOfInterest identifiers
     * 
     * @param identifiers
     *            FeatureOfInterest identifiers
     * @param session
     *            Hibernate session
     * @return FeatureOfInterest objects
     */
    @SuppressWarnings("unchecked")
    public List<FeatureOfInterest> getFeatureOfInterestObject(final Collection<String> identifiers,
            final Session session) {
        if (identifiers != null && !identifiers.isEmpty()) {
            Criteria criteria =
                    session.createCriteria(FeatureOfInterest.class).add(
                            Restrictions.in(FeatureOfInterest.IDENTIFIER, identifiers));
            LOGGER.debug("QUERY getFeatureOfInterestObject(identifiers): {}", HibernateHelper.getSqlString(criteria));
            return criteria.list();
        }
        return Collections.emptyList();
    }

    /**
     * Get all featureOfInterest objects
     * 
     * @param session
     *            Hibernate session
     * @return FeatureOfInterest objects
     */
    @SuppressWarnings("unchecked")
    public List<FeatureOfInterest> getFeatureOfInterestObjects(final Session session) {
        Criteria criteria = session.createCriteria(FeatureOfInterest.class);
        LOGGER.debug("QUERY getResultTemplateObject(identifier): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Insert and/or get featureOfInterest object for identifier
     * 
     * @param identifier
     *            FeatureOfInterest identifier
     * @param url
     *            FeatureOfInterest URL, if defined as link
     * @param session
     *            Hibernate session
     * @return FeatureOfInterest object
     */
    public FeatureOfInterest getOrInsertFeatureOfInterest(final String identifier, final String url,
            final Session session) {
        FeatureOfInterest feature = getFeatureOfInterest(identifier, session);
        if (feature == null) {
            feature = new TFeatureOfInterest();
            feature.setIdentifier(identifier);
            if (url != null && !url.isEmpty()) {
                feature.setUrl(url);
            }
            final FeatureOfInterestType featureOfInterestType =
                    new FeatureOfInterestTypeDAO().getOrInsertFeatureOfInterestType(OGCConstants.UNKNOWN, session);
            feature.setFeatureOfInterestType(featureOfInterestType);
            session.save(feature);
            session.flush();
        } else if (feature.getUrl() != null && !feature.getUrl().isEmpty() && url != null && !url.isEmpty()) {
            feature.setUrl(url);
            session.saveOrUpdate(feature);
            session.flush();
        }
        return feature;
    }

    /**
     * Insert featureOfInterest relationship
     * 
     * @param parentFeature
     *            Parent featureOfInterest
     * @param childFeature
     *            Child featureOfInterest
     * @param session
     *            Hibernate session
     */
    public void insertFeatureOfInterestRelationShip(final TFeatureOfInterest parentFeature,
            final FeatureOfInterest childFeature, final Session session) {
        parentFeature.getChilds().add(childFeature);
        session.saveOrUpdate(parentFeature);
        session.flush();
    }

    /**
     * Insert featureOfInterest/related feature relations if relatedFeatures
     * exists for offering.
     * 
     * @param featureOfInterest
     *            FeatureOfInerest
     * @param offering
     *            Offering
     * @param session
     *            Hibernate session
     */
    public void checkOrInsertFeatureOfInterestRelatedFeatureRelation(final FeatureOfInterest featureOfInterest,
            final Offering offering, final Session session) {
        final List<RelatedFeature> relatedFeatures =
                new RelatedFeatureDAO().getRelatedFeatureForOffering(offering.getIdentifier(), session);
        if (CollectionHelper.isNotEmpty(relatedFeatures)) {
            for (final RelatedFeature relatedFeature : relatedFeatures) {
                insertFeatureOfInterestRelationShip((TFeatureOfInterest) relatedFeature.getFeatureOfInterest(),
                        featureOfInterest, session);
            }
        }
    }

    /**
     * Insert featureOfInterest if it is supported
     * 
     * @param featureOfInterest
     *            SOS featureOfInterest to insert
     * @param session
     *            Hibernate session
     * @return FeatureOfInterest object
     * @throws NoApplicableCodeException
     *             If SOS feature type is not supported (with status
     *             {@link HTTPStatus}.BAD_REQUEST
     */
    public FeatureOfInterest checkOrInsertFeatureOfInterest(final AbstractFeature featureOfInterest,
            final Session session) throws OwsExceptionReport {
        if (featureOfInterest instanceof SamplingFeature) {
            final String featureIdentifier =
                    Configurator.getInstance().getFeatureQueryHandler()
                            .insertFeature((SamplingFeature) featureOfInterest, session);
            return getOrInsertFeatureOfInterest(featureIdentifier, ((SamplingFeature) featureOfInterest).getUrl(),
                    session);
        } else {
            throw new NoApplicableCodeException().withMessage("The used feature type '%s' is not supported.",
                    featureOfInterest != null ? featureOfInterest.getClass().getName() : featureOfInterest).setStatus(
                    BAD_REQUEST);
        }
    }

}
