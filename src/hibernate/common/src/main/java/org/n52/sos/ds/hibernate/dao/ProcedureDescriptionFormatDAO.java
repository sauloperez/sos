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

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.entities.ProcedureDescriptionFormat;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hibernate data access class for procedure description format
 * 
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class ProcedureDescriptionFormatDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcedureDescriptionFormatDAO.class);

    /**
     * Get procedure description format object
     * 
     * @param procedureDescriptionFormat
     *            Procedure description format
     * @param session
     *            Hibernate session
     * @return Procedure description format object
     */
    public ProcedureDescriptionFormat getProcedureDescriptionFormatObject(String procedureDescriptionFormat,
            Session session) {
        Criteria criteria =
                session.createCriteria(ProcedureDescriptionFormat.class).add(
                        Restrictions.eq(ProcedureDescriptionFormat.PROCEDURE_DESCRIPTION_FORMAT,
                                procedureDescriptionFormat));
        LOGGER.debug("QUERY getProcedureDescriptionFormatObject(procedureDescriptionFormat): {}",
                HibernateHelper.getSqlString(criteria));
        return (ProcedureDescriptionFormat) criteria.uniqueResult();
    }

    /**
     * Insert and get procedure description format
     * 
     * @param procedureDescriptionFormat
     *            Procedure description format
     * @param session
     *            Hibernate session
     * @return Procedure description format object
     */
    public ProcedureDescriptionFormat getOrInsertProcedureDescriptionFormat(String procedureDescriptionFormat,
            Session session) {
        ProcedureDescriptionFormat hProcedureDescriptionFormat =
                getProcedureDescriptionFormatObject(procedureDescriptionFormat, session);
        if (hProcedureDescriptionFormat == null) {
            hProcedureDescriptionFormat = new ProcedureDescriptionFormat();
            hProcedureDescriptionFormat.setProcedureDescriptionFormat(procedureDescriptionFormat);
            session.save(hProcedureDescriptionFormat);
            session.flush();
        }
        return hProcedureDescriptionFormat;
    }
}
