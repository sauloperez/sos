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
package org.n52.sos.ext.deleteobservation;

import static org.hibernate.criterion.Restrictions.eq;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.joda.time.DateTime;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.dao.ObservationDAO;
import org.n52.sos.ds.hibernate.dao.OfferingDAO;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.Observation;
import org.n52.sos.ds.hibernate.entities.ObservationInfo;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Updates the cache after a Observation was deleted. Uses the deleted
 * observation to determine which cache relations have to be updated.
 * <p/>
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 1.0.0
 */
public class HibernateDeleteObservationCacheFeederDAO extends DeleteObservationCacheFeederDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateDeleteObservationCacheFeederDAO.class);

    private final HibernateSessionHolder sessionHolder = new HibernateSessionHolder();

    private Session session;

    private final ObservationDAO observationDAO = new ObservationDAO();

    private final OfferingDAO offeringDAO = new OfferingDAO();

    private final ProcedureDAO procedureDAO = new ProcedureDAO();

    @Override
    protected boolean isLastForProcedure(String feature, String procedure) {
        Criteria oc = getConnection().createCriteria(ObservationInfo.class).add(eq(Observation.DELETED, false));
        oc.createCriteria(Observation.FEATURE_OF_INTEREST).add(eq(FeatureOfInterest.IDENTIFIER, feature));
        oc.createCriteria(Observation.PROCEDURE).add(eq(Procedure.IDENTIFIER, procedure));
        return isEmpty(oc);
    }

    @Override
    protected boolean isLastForOffering(String feature, String offering) {
        Criteria oc = getConnection().createCriteria(ObservationInfo.class).add(eq(Observation.DELETED, false));
        oc.createCriteria(Observation.FEATURE_OF_INTEREST).add(eq(FeatureOfInterest.IDENTIFIER, feature));
        oc.createCriteria(Observation.OFFERINGS).add(eq(Offering.IDENTIFIER, offering));
        return isEmpty(oc);
    }

    /**
     * Checks if the specified query has no results.
     * 
     * @param q
     *            the query
     * 
     * @return if it has no results
     */
    protected boolean isEmpty(Criteria q) {
        Criteria criteria = q.setProjection(Projections.rowCount());
        LOGGER.debug("QUERY isEmpty(criteria): {}", HibernateHelper.getSqlString(criteria));
        return ((Number) criteria.uniqueResult()).longValue() == 0L;
    }

    @Override
    protected DateTime getMaxResultTime() {
        return observationDAO.getMaxResultTime(getConnection());
    }

    @Override
    protected DateTime getMinResultTime() {
        return observationDAO.getMinResultTime(getConnection());
    }

    @Override
    protected DateTime getMaxPhenomenonTime() {
        return observationDAO.getMaxPhenomenonTime(getConnection());
    }

    @Override
    protected DateTime getMinPhenomenonTime() {
        return observationDAO.getMinPhenomenonTime(getConnection());
    }

    @Override
    protected DateTime getMaxDateForOffering(final String offering) {
        return offeringDAO.getMaxDate4Offering(offering, getConnection());
    }

    @Override
    protected DateTime getMaxDateForProcedure(final String procedure) {
        return procedureDAO.getMaxDate4Procedure(procedure, getConnection());
    }

    @Override
    protected DateTime getMinResultTimeForOffering(final String offering) {
        return offeringDAO.getMinResultTime4Offering(offering, getConnection());
    }

    @Override
    protected DateTime getMaxResultTimeForOffering(final String offering) {
        return offeringDAO.getMaxResultTime4Offering(offering, getConnection());
    }

    @Override
    protected DateTime getMinDateForOffering(final String offering) {
        return offeringDAO.getMinDate4Offering(offering, getConnection());
    }

    @Override
    protected DateTime getMinDateForProcedure(final String procedure) {
        return procedureDAO.getMinDate4Procedure(procedure, getConnection());
    }

    @Override
    protected Session getConnection() {
        return session;
    }

    @Override
    protected void prepare() throws OwsExceptionReport {
        this.session = this.sessionHolder.getSession();
    }

    @Override
    protected void cleanup() {
        this.sessionHolder.returnSession(session);
    }
}
