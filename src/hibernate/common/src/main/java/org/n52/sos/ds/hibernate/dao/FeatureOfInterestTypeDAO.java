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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterestType;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ogc.OGCConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hibernate data access class for featureofInterest types
 * 
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class FeatureOfInterestTypeDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureOfInterestTypeDAO.class);

    /**
     * Get all featureOfInterest types
     * 
     * @param session
     *            Hibernate session
     * @return All featureOfInterest types
     */
    @SuppressWarnings("unchecked")
    public List<String> getFeatureOfInterestTypes(final Session session) {
        Criteria criteria =
                session.createCriteria(FeatureOfInterestType.class)
                        .add(Restrictions.ne(FeatureOfInterestType.FEATURE_OF_INTEREST_TYPE, OGCConstants.UNKNOWN))
                        .setProjection(
                                Projections.distinct(Projections
                                        .property(FeatureOfInterestType.FEATURE_OF_INTEREST_TYPE)));

        LOGGER.debug("QUERY getFeatureOfInterestTypes(): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Get featureOfInterest type object for featureOfInterest type
     * 
     * @param featureOfInterestType
     *            FeatureOfInterest type
     * @param session
     *            Hibernate session
     * @return FeatureOfInterest type object
     */
    public FeatureOfInterestType getFeatureOfInterestTypeObject(final String featureOfInterestType,
            final Session session) {
        Criteria criteria =
                session.createCriteria(FeatureOfInterestType.class).add(
                        Restrictions.eq(FeatureOfInterestType.FEATURE_OF_INTEREST_TYPE, featureOfInterestType));
        LOGGER.debug("QUERY getFeatureOfInterestTypeObject(featureOfInterestType): {}",
                HibernateHelper.getSqlString(criteria));
        return (FeatureOfInterestType) criteria.uniqueResult();
    }

    /**
     * Get featureOfInterest type objects for featureOfInterest types
     * 
     * @param featureOfInterestType
     *            FeatureOfInterest types
     * @param session
     *            Hibernate session
     * @return FeatureOfInterest type objects
     */
    @SuppressWarnings("unchecked")
    public List<FeatureOfInterestType> getFeatureOfInterestTypeObjects(final List<String> featureOfInterestType,
            final Session session) {
        Criteria criteria =
                session.createCriteria(FeatureOfInterestType.class).add(
                        Restrictions.in(FeatureOfInterestType.FEATURE_OF_INTEREST_TYPE, featureOfInterestType));
        LOGGER.debug("QUERY getFeatureOfInterestTypeObjects(featureOfInterestTypes): {}",
                HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Get featureOfInterest type objects for featureOfInterest identifiers
     * 
     * @param featureOfInterestIdentifiers
     *            FeatureOfInterest identifiers
     * @param session
     *            Hibernate session
     * @return FeatureOfInterest type objects
     */
    @SuppressWarnings("unchecked")
    public List<String> getFeatureOfInterestTypesForFeatureOfInterest(
            final Collection<String> featureOfInterestIdentifiers, final Session session) {
        Criteria criteria =
                session.createCriteria(FeatureOfInterest.class).add(
                        Restrictions.in(FeatureOfInterest.IDENTIFIER, featureOfInterestIdentifiers));
        criteria.createCriteria(FeatureOfInterest.FEATURE_OF_INTEREST_TYPE).setProjection(
                Projections.distinct(Projections.property(FeatureOfInterestType.FEATURE_OF_INTEREST_TYPE)));
        LOGGER.debug("QUERY getFeatureOfInterestTypesForFeatureOfInterest(featureOfInterestIdentifiers): {}",
                HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Insert and/or get featureOfInterest type object for featureOfInterest
     * type
     * 
     * @param featureType
     *            FeatureOfInterest type
     * @param session
     *            Hibernate session
     * @return FeatureOfInterest type object
     */
    public FeatureOfInterestType getOrInsertFeatureOfInterestType(final String featureType, final Session session) {
        FeatureOfInterestType featureOfInterestType = getFeatureOfInterestTypeObject(featureType, session);
        if (featureOfInterestType == null) {
            featureOfInterestType = new FeatureOfInterestType();
            featureOfInterestType.setFeatureOfInterestType(featureType);
            session.save(featureOfInterestType);
            session.flush();
        }
        return featureOfInterestType;
    }

    /**
     * Insert and/or get featureOfInterest type objects for featureOfInterest
     * types
     * 
     * @param featureOfInterestTypes
     *            FeatureOfInterest types
     * @param session
     *            Hibernate session
     * @return FeatureOfInterest type objects
     */
    public List<FeatureOfInterestType> getOrInsertFeatureOfInterestTypes(final Set<String> featureOfInterestTypes,
            final Session session) {
        final List<FeatureOfInterestType> featureTypes = new LinkedList<FeatureOfInterestType>();
        for (final String featureType : featureOfInterestTypes) {
            featureTypes.add(getOrInsertFeatureOfInterestType(featureType, session));
        }
        return featureTypes;
    }

}
