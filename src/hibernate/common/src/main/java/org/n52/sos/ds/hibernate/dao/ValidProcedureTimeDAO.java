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

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hibernate data access class for valid procedure time
 * 
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class ValidProcedureTimeDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidProcedureTimeDAO.class);

    /**
     * Insert valid procedure time for procedrue
     * 
     * @param procedure
     *            Procedure object
     * @param xmlDescription
     *            Procedure XML description
     * @param validStartTime
     *            Valid start time
     * @param session
     *            Hibernate session
     */
    public void insertValidProcedureTime(Procedure procedure, ProcedureDescriptionFormat procedureDescriptionFormat,
            String xmlDescription, DateTime validStartTime, Session session) {
        ValidProcedureTime vpd = new ValidProcedureTime();
        vpd.setProcedure(procedure);
        vpd.setProcedureDescriptionFormat(procedureDescriptionFormat);
        vpd.setDescriptionXml(xmlDescription);
        vpd.setStartTime(validStartTime.toDate());
        session.save(vpd);
        session.flush();
    }

    /**
     * Update valid procedure time object
     * 
     * @param validProcedureTime
     *            Valid procedure time object
     * @param session
     *            Hibernate session
     */
    public void updateValidProcedureTime(ValidProcedureTime validProcedureTime, Session session) {
        session.saveOrUpdate(validProcedureTime);
    }

    /**
     * Set valid end time to valid procedure time object for procedure
     * identifier
     * 
     * @param procedureIdentifier
     *            Procedure identifier
     * @param session
     *            Hibernate session
     * @throws UnsupportedOperatorException
     * @throws UnsupportedValueReferenceException
     * @throws UnsupportedTimeException
     */
    public void setValidProcedureDescriptionEndTime(String procedureIdentifier, String procedureDescriptionFormat,
            Session session) throws UnsupportedTimeException, UnsupportedValueReferenceException,
            UnsupportedOperatorException {
        TProcedure procedure =
                new ProcedureDAO().getTProcedureForIdentifier(procedureIdentifier, procedureDescriptionFormat, null,
                        session);
        Set<ValidProcedureTime> validProcedureTimes = procedure.getValidProcedureTimes();
        for (ValidProcedureTime validProcedureTime : validProcedureTimes) {
            if (validProcedureTime.getEndTime() == null) {
                validProcedureTime.setEndTime(new DateTime(DateTimeZone.UTC).toDate());
            }
        }
    }

    /**
     * Set valid end time to valid procedure time object for procedure
     * identifier
     * 
     * @param procedureIdentifier
     *            Procedure identifier
     * @param session
     *            Hibernate session
     */
    public void setValidProcedureDescriptionEndTime(String procedureIdentifier, Session session) {
        TProcedure procedure = new ProcedureDAO().getTProcedureForIdentifier(procedureIdentifier, session);
        Set<ValidProcedureTime> validProcedureTimes = procedure.getValidProcedureTimes();
        for (ValidProcedureTime validProcedureTime : validProcedureTimes) {
            if (validProcedureTime.getEndTime() == null) {
                validProcedureTime.setEndTime(new DateTime(DateTimeZone.UTC).toDate());
            }
        }
    }

    /**
     * Get ValidProcedureTimes for requested parameters
     * 
     * @param procedure
     *            Requested Procedure
     * @param procedureDescriptionFormat
     *            Requested procedureDescriptionFormat
     * @param validTime
     *            Requested validTime (optional)
     * @param session
     *            Hibernate session
     * @return List with ValidProcedureTime objects
     * @throws UnsupportedTimeException
     *             If validTime time value is invalid
     * @throws UnsupportedValueReferenceException
     *             If valueReference is not supported
     * @throws UnsupportedOperatorException
     *             If temporal operator is not supported
     */
    @SuppressWarnings("unchecked")
    public List<ValidProcedureTime> getValidProcedureTimes(Procedure procedure, String procedureDescriptionFormat,
            Time validTime, Session session) throws UnsupportedTimeException, UnsupportedValueReferenceException,
            UnsupportedOperatorException {
        Criteria criteria = session.createCriteria(ValidProcedureTime.class);
        criteria.add(Restrictions.eq(ValidProcedureTime.PROCEDURE, procedure));
        criteria.createCriteria(ValidProcedureTime.PROCEDURE_DESCRIPTION_FORMAT).add(
                Restrictions.eq(ProcedureDescriptionFormat.PROCEDURE_DESCRIPTION_FORMAT, procedureDescriptionFormat));

        Criterion validTimeCriterion = QueryHelper.getValidTimeCriterion(validTime);
        // if validTime == null or validTimeCriterion == null, query latest
        // valid procedure description
        if (validTime == null || validTimeCriterion == null) {
            criteria.add(Restrictions.isNull(ValidProcedureTime.END_TIME));
        } else {
            criteria.add(validTimeCriterion);
        }
        LOGGER.debug("QUERY getValidProcedureTimes(procedure,procedureDescriptionFormat, validTime): {}",
                HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<ValidProcedureTime> getValidProcedureTimes(TProcedure procedure,
            Set<String> possibleProcedureDescriptionFormats, Time validTime, Session session)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        Criteria criteria = session.createCriteria(ValidProcedureTime.class);
        criteria.add(Restrictions.eq(ValidProcedureTime.PROCEDURE, procedure));
        criteria.createCriteria(ValidProcedureTime.PROCEDURE_DESCRIPTION_FORMAT).add(
                Restrictions.in(ProcedureDescriptionFormat.PROCEDURE_DESCRIPTION_FORMAT,
                        possibleProcedureDescriptionFormats));

        Criterion validTimeCriterion = QueryHelper.getValidTimeCriterion(validTime);
        // if validTime == null or validTimeCriterion == null, query latest
        // valid procedure description
        if (validTime == null || validTimeCriterion == null) {
            criteria.add(Restrictions.isNull(ValidProcedureTime.END_TIME));
        } else {
            criteria.add(validTimeCriterion);
        }
        LOGGER.debug("QUERY getValidProcedureTimes(procedure, possibleProcedureDescriptionFormats, validTime): {}",
                HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }
}
