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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Observation;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.ObservationInfo;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hibernate data access class for observable properties
 * 
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class ObservablePropertyDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObservablePropertyDAO.class);

    /**
     * Get observable property objects for observable property identifiers
     * 
     * @param identifiers
     *            Observable property identifiers
     * @param session
     *            Hibernate session
     * @return Observable property objects
     */
    @SuppressWarnings("unchecked")
    public List<ObservableProperty> getObservableProperties(final List<String> identifiers, final Session session) {
        Criteria criteria =
                session.createCriteria(ObservableProperty.class).add(
                        Restrictions.in(ObservableProperty.IDENTIFIER, identifiers));
        LOGGER.debug("QUERY getObservableProperties(identifiers): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Get observable property identifiers for offering identifier
     * 
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return Observable property identifiers
     */
    @SuppressWarnings("unchecked")
    public List<String> getObservablePropertyIdentifiersForOffering(final String offeringIdentifier,
            final Session session) {
        final boolean flag = HibernateHelper.isEntitySupported(ObservationConstellation.class, session);
        Criteria c = null;
        if (flag) {
            c =
                    session.createCriteria(ObservationConstellation.class).add(
                            Restrictions.eq(ObservationConstellation.DELETED, false));
            c.createCriteria(ObservationConstellation.OBSERVABLE_PROPERTY).setProjection(
                    Projections.distinct(Projections.property(ObservableProperty.IDENTIFIER)));
            c.createCriteria(ObservationConstellation.OFFERING).add(
                    Restrictions.eq(Offering.IDENTIFIER, offeringIdentifier));
        } else {
            c = session.createCriteria(ObservationInfo.class).add(Restrictions.eq(Observation.DELETED, false));
            c.createCriteria(ObservationInfo.OBSERVABLE_PROPERTY).setProjection(
                    Projections.distinct(Projections.property(ObservableProperty.IDENTIFIER)));
            c.createCriteria(ObservationInfo.OFFERINGS).add(Restrictions.eq(Offering.IDENTIFIER, offeringIdentifier));
        }
        LOGGER.debug(
                "QUERY getObservablePropertyIdentifiersForOffering(offeringIdentifier) using ObservationContellation entitiy ({}): {}",
                flag, HibernateHelper.getSqlString(c));
        return c.list();
    }

    /**
     * Get observable property identifiers for procedure identifier
     * 
     * @param procedureIdentifier
     *            Procedure identifier
     * @param session
     *            Hibernate session
     * @return Observable property identifiers
     */
    @SuppressWarnings("unchecked")
    public List<String> getObservablePropertyIdentifiersForProcedure(final String procedureIdentifier,
            final Session session) {
        final boolean flag = HibernateHelper.isEntitySupported(ObservationConstellation.class, session);
        Criteria c = null;
        if (flag) {
            c =
                    session.createCriteria(ObservableProperty.class).add(
                            Subqueries.propertyIn(ObservableProperty.ID,
                                    getDetachedCriteriaObservableProperty(procedureIdentifier, session)));
            c.setProjection(Projections.distinct(Projections.property(ObservableProperty.IDENTIFIER)));
        } else {
            c = session.createCriteria(ObservationInfo.class).add(Restrictions.eq(Observation.DELETED, false));
            c.createCriteria(ObservationInfo.OBSERVABLE_PROPERTY).setProjection(
                    Projections.distinct(Projections.property(ObservableProperty.IDENTIFIER)));
            c.createCriteria(ObservationInfo.PROCEDURE)
                    .add(Restrictions.eq(Procedure.IDENTIFIER, procedureIdentifier));

        }
        LOGGER.debug(
                "QUERY getObservablePropertyIdentifiersForProcedure(procedureIdentifier) using ObservationContellation entitiy ({}): {}",
                flag, HibernateHelper.getSqlString(c));
        return c.list();
    }

    /**
     * Get observable property by identifier
     * 
     * @param identifier
     *            The observable property's identifier
     * @param session
     *            Hibernate session
     * @return Observable property object
     */
    public ObservableProperty getObservablePropertyForIdentifier(final String identifier, final Session session) {
        Criteria criteria = session.createCriteria(ObservableProperty.class).add(Restrictions.idEq(identifier));
        LOGGER.debug("QUERY getObservablePropertyForIdentifier(identifier): {}",
                HibernateHelper.getSqlString(criteria));
        return (ObservableProperty) criteria.uniqueResult();
    }

    /**
     * Get observable properties by identifiers
     * 
     * @param identifiers
     *            The observable property identifiers
     * @param session
     *            Hibernate session
     * @return Observable property objects
     */
    @SuppressWarnings("unchecked")
    public List<ObservableProperty> getObservablePropertiesForIdentifiers(final Collection<String> identifiers,
            final Session session) {
        Criteria criteria =
                session.createCriteria(ObservableProperty.class).add(
                        Restrictions.in(ObservableProperty.IDENTIFIER, identifiers));
        LOGGER.debug("QUERY getObservablePropertiesForIdentifiers(identifiers): {}",
                HibernateHelper.getSqlString(criteria));
        return (List<ObservableProperty>)criteria.list();
    }

    /**
     * Get all observable property objects
     * 
     * @param session
     *            Hibernate session
     * @return Observable property objects
     */
    @SuppressWarnings("unchecked")
    public List<ObservableProperty> getObservablePropertyObjects(final Session session) {
        Criteria criteria = session.createCriteria(ObservableProperty.class);
        LOGGER.debug("QUERY getObservablePropertyObjects(): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Insert and/or get observable property objects for SOS observable
     * properties
     * 
     * @param observableProperty
     *            SOS observable properties
     * @param session
     *            Hibernate session
     * @return Observable property objects
     */
    public List<ObservableProperty> getOrInsertObservableProperty(final List<OmObservableProperty> observableProperty,
            final Session session) {
        final List<String> identifiers = new ArrayList<String>(observableProperty.size());
        for (final OmObservableProperty sosObservableProperty : observableProperty) {
            identifiers.add(sosObservableProperty.getIdentifier());
        }
        final List<ObservableProperty> obsProps = getObservableProperties(identifiers, session);
        for (final OmObservableProperty sosObsProp : observableProperty) {
            boolean exists = false;
            for (final ObservableProperty obsProp : obsProps) {
                if (obsProp.getIdentifier().equals(sosObsProp.getIdentifier())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                final ObservableProperty obsProp = new ObservableProperty();
                obsProp.setIdentifier(sosObsProp.getIdentifier());
                obsProp.setDescription(sosObsProp.getDescription());
                session.save(obsProp);
                session.flush();
                session.refresh(obsProp);
                obsProps.add(obsProp);
            }
        }
        return obsProps;
    }

    private DetachedCriteria getDetachedCriteriaObservableProperty(String procedureIdentifier, Session session) {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(ObservationConstellation.class);
        detachedCriteria.add(Restrictions.eq(ObservationConstellation.DELETED, false));
        detachedCriteria.createCriteria(ObservationConstellation.PROCEDURE).add(
                Restrictions.eq(Procedure.IDENTIFIER, procedureIdentifier));
        detachedCriteria.setProjection(Projections.distinct(Projections
                .property(ObservationConstellation.OBSERVABLE_PROPERTY)));
        return detachedCriteria;
    }

}
