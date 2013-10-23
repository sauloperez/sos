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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.ObservationType;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Hibernate data access class for observation constellation
 * 
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class ObservationConstellationDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObservationConstellationDAO.class);

    /**
     * Get observation constellation objects for procedure and observable
     * property object and offering identifiers
     * 
     * @param procedure
     *            Procedure object
     * @param observableProperty
     *            Observable property object
     * @param offerings
     *            Offering identifiers
     * @param session
     *            Hibernate session
     * @return Observation constellation objects
     */
    @SuppressWarnings("unchecked")
    public List<ObservationConstellation> getObservationConstellation(Procedure procedure,
            ObservableProperty observableProperty, Collection<String> offerings, Session session) {
        Criteria criteria =
                session.createCriteria(ObservationConstellation.class)
                        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                        .add(Restrictions.eq(ObservationConstellation.PROCEDURE, procedure))
                        .add(Restrictions.eq(ObservationConstellation.OBSERVABLE_PROPERTY, observableProperty))
                        .createAlias(ObservationConstellation.OFFERING, "o")
                        .add(Restrictions.in("o." + Offering.IDENTIFIER, offerings))
                        .add(Restrictions.eq(ObservationConstellation.DELETED, false));
        LOGGER.debug("QUERY getObservationConstellation(procedure, observableProperty, offerings): {}",
                HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }


    @SuppressWarnings("unchecked")
    public List<ObservationConstellation> getObservationConstellationsForOfferings(Procedure procedure,
            ObservableProperty observableProperty, Collection<Offering> offerings, Session session) {
        return session.createCriteria(ObservationConstellation.class)
                        .add(Restrictions.eq(ObservationConstellation.DELETED, false))
                        .add(Restrictions.eq(ObservationConstellation.PROCEDURE, procedure))
                        .add(Restrictions.in(ObservationConstellation.OFFERING, offerings))
                        .add(Restrictions.eq(ObservationConstellation.OBSERVABLE_PROPERTY, observableProperty)).list();
    }

    public ObservationConstellation getFirstObservationConstellationForOfferings(
            Procedure p, ObservableProperty op, Collection<Offering> o, Session session) {
        final List<ObservationConstellation> oc
                = getObservationConstellationsForOfferings(p, op, o, session);
        return oc.isEmpty() ? null : oc.get(0);
    }


    @SuppressWarnings("unchecked")
    public List<ObservationConstellation> getObservationConstellations(String procedure, String observableProperty,
            Session session) {
        Criteria criteria =
                session.createCriteria(ObservationConstellation.class)
                        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                        .add(Restrictions.eq(ObservationConstellation.DELETED, false));
        criteria.createCriteria(ObservationConstellation.PROCEDURE).add(
                Restrictions.eq(Procedure.IDENTIFIER, procedure));
        criteria.createCriteria(ObservationConstellation.OBSERVABLE_PROPERTY).add(
                Restrictions.eq(ObservableProperty.IDENTIFIER, observableProperty));
        LOGGER.debug("QUERY getObservationConstellation(procedure, observableProperty): {}",
                HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Get all observation constellation objects
     * 
     * @param session
     *            Hibernate session
     * @return Observation constellation objects
     */
    @SuppressWarnings("unchecked")
    public List<ObservationConstellation> getObservationConstellations(Session session) {
        Criteria criteria =
                session.createCriteria(ObservationConstellation.class)
                        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                        .add(Restrictions.eq(ObservationConstellation.DELETED, false));
        LOGGER.debug("QUERY getObservationConstellations(): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();

    }

    /**
     * Insert or update and get observation constellation for procedure,
     * observable property and offering
     * 
     * @param procedure
     *            Procedure object
     * @param observableProperty
     *            Observable property object
     * @param offering
     *            Offering object
     * @param hiddenChild
     *            Is observation constellation hidden child
     * @param session
     *            Hibernate session
     * @return Observation constellation object
     */
    public ObservationConstellation checkOrInsertObservationConstellation(Procedure procedure,
            ObservableProperty observableProperty, Offering offering, boolean hiddenChild, Session session) {
        Criteria criteria =
                session.createCriteria(ObservationConstellation.class)
                        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                        .add(Restrictions.eq(ObservationConstellation.OFFERING, offering))
                        .add(Restrictions.eq(ObservationConstellation.OBSERVABLE_PROPERTY, observableProperty))
                        .add(Restrictions.eq(ObservationConstellation.PROCEDURE, procedure))
                        .add(Restrictions.eq(ObservationConstellation.HIDDEN_CHILD, hiddenChild));
        LOGGER.debug(
                "QUERY checkOrInsertObservationConstellation(procedure, observableProperty, offering, hiddenChild): {}",
                HibernateHelper.getSqlString(criteria));
        ObservationConstellation obsConst = (ObservationConstellation) criteria.uniqueResult();
        if (obsConst == null) {
            obsConst = new ObservationConstellation();
            obsConst.setObservableProperty(observableProperty);
            obsConst.setProcedure(procedure);
            obsConst.setOffering(offering);
            obsConst.setDeleted(false);
            obsConst.setHiddenChild(hiddenChild);
            session.save(obsConst);
            session.flush();
            session.refresh(obsConst);
        } else if (obsConst.getDeleted()) {
            obsConst.setDeleted(false);
            session.save(obsConst);
            session.flush();
            session.refresh(obsConst);
        }
        return obsConst;
    }

    /**
     * Check and Update and/or get observation constellation objects
     * 
     * @param sosObservationConstellation
     *            SOS observation constellation
     * @param offering
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @param parameterName
     *            Parameter name for exception
     * @return Observation constellation object
     * @throws OwsExceptionReport
     *             If the requested observation type is invalid
     */
    public ObservationConstellation checkObservationConstellation(
            OmObservationConstellation sosObservationConstellation, String offering, Session session,
            String parameterName) throws OwsExceptionReport {
        String observableProperty = sosObservationConstellation.getObservableProperty().getIdentifier();
        String procedure = sosObservationConstellation.getProcedure().getIdentifier();

        Criteria c =
                session.createCriteria(ObservationConstellation.class).setResultTransformer(
                        Criteria.DISTINCT_ROOT_ENTITY);

        c.createCriteria(ObservationConstellation.OFFERING).add(Restrictions.eq(Offering.IDENTIFIER, offering));

        c.createCriteria(ObservationConstellation.OBSERVABLE_PROPERTY).add(
                Restrictions.eq(ObservableProperty.IDENTIFIER, observableProperty));

        c.createCriteria(ObservationConstellation.PROCEDURE).add(Restrictions.eq(Procedure.IDENTIFIER, procedure));

        LOGGER.debug("QUERY checkObservationConstellation(sosObservationConstellation, offering): {}",
                HibernateHelper.getSqlString(c));
        @SuppressWarnings("unchecked")
        List<ObservationConstellation> hocs = c.list();

        if (!hocs.isEmpty()) {
            for (ObservationConstellation hoc : hocs) {
                if (hoc.getObservationType() == null
                        || (hoc.getObservationType() != null && (hoc.getObservationType().getObservationType()
                                .equals("NOT_DEFINED") || hoc.getObservationType().getObservationType().isEmpty()))) {
                    return updateObservationConstellation(hoc, sosObservationConstellation.getObservationType(),
                            session);
                } else {
                    if (hoc.getObservationType().getObservationType()
                            .equals(sosObservationConstellation.getObservationType())) {
                        return hoc;
                    } else {
                        throw new InvalidParameterValueException()
                                .at(parameterName)
                                .withMessage(
                                        "The requested observationType (%s) is invalid for procedure = %s, observedProperty = %s and offering = %s! The valid observationType is '%s'!",
                                        sosObservationConstellation.getObservationType(), procedure,
                                        observableProperty, sosObservationConstellation.getOfferings(),
                                        hoc.getObservationType().getObservationType());
                    }
                }
            }
        } else {
            throw new InvalidParameterValueException()
                    .at(Sos2Constants.InsertObservationParams.observation)
                    .withMessage(
                            "The requested observation constellation (procedure=%s, observedProperty=%s and offering=%s) is invalid!",
                            procedure, observableProperty, sosObservationConstellation.getOfferings());
        }
        return null;
    }

    /**
     * Update observation constellation with observation type
     * 
     * @param observationConstellation
     *            Observation constellation object
     * @param observationType
     *            Observation type
     * @param session
     *            Hibernate session
     * @return Observation constellation object
     */
    @SuppressWarnings("unchecked")
    public ObservationConstellation updateObservationConstellation(ObservationConstellation observationConstellation,
            String observationType, Session session) {
        ObservationType obsType = new ObservationTypeDAO().getObservationTypeObject(observationType, session);
        observationConstellation.setObservationType(obsType);
        session.saveOrUpdate(observationConstellation);

        // update hidden child observation constellations
        // TODO should hidden child observation constellations be restricted to
        // the parent observation type?
        Set<String> offerings =
                new HashSet<String>(Configurator.getInstance().getCache()
                        .getOfferingsForProcedure(observationConstellation.getProcedure().getIdentifier()));
        offerings.remove(observationConstellation.getOffering().getIdentifier());

        if (CollectionHelper.isNotEmpty(offerings)) {
            Criteria c =
                    session.createCriteria(ObservationConstellation.class)
                            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                            .add(Restrictions.eq(ObservationConstellation.OBSERVABLE_PROPERTY,
                                    observationConstellation.getObservableProperty()))
                            .add(Restrictions.eq(ObservationConstellation.PROCEDURE,
                                    observationConstellation.getProcedure()))
                            .add(Restrictions.eq(ObservationConstellation.HIDDEN_CHILD, true));
            c.createCriteria(ObservationConstellation.OFFERING).add(Restrictions.in(Offering.IDENTIFIER, offerings));
            LOGGER.debug("QUERY updateObservationConstellation(observationConstellation, observationType): {}",
                    HibernateHelper.getSqlString(c));
            List<ObservationConstellation> hiddenChildObsConsts = c.list();
            for (ObservationConstellation hiddenChildObsConst : hiddenChildObsConsts) {
                hiddenChildObsConst.setObservationType(obsType);
                session.saveOrUpdate(hiddenChildObsConst);
            }
        }

        return observationConstellation;
    }

    /**
     * Return the non-deleted observation constellations for an offering
     * 
     * @param offering
     *            Offering to fetch observation constellations for
     * @param session
     *            Session to use
     * @return Offering's observation constellations
     */
    @SuppressWarnings("unchecked")
    public List<ObservationConstellation> getObservationConstellationsForOffering(Offering offering, Session session) {
        if (HibernateHelper.isEntitySupported(ObservationConstellation.class, session)) {
            Criteria criteria =
                    session.createCriteria(ObservationConstellation.class)
                            .add(Restrictions.eq(ObservationConstellation.DELETED, false))
                            .add(Restrictions.eq(ObservationConstellation.OFFERING, offering));
            LOGGER.debug("QUERY getObservationConstellationsForOffering(offering): {}",
                    HibernateHelper.getSqlString(criteria));
            return criteria.list();
        }
        return Lists.newLinkedList();
    }
}
