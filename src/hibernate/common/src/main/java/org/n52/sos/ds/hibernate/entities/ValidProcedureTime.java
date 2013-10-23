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
package org.n52.sos.ds.hibernate.entities;

import java.io.Serializable;
import java.util.Date;

import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasProcedure;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasProcedureDescriptionFormat;

/**
 * @since 4.0.0
 * 
 */
public class ValidProcedureTime extends DescriptionXmlEntity implements Serializable, HasProcedure,
        HasProcedureDescriptionFormat {

    private static final long serialVersionUID = -3658568714438752174L;

    public static final String ID = "validProcedureTimeId";

    public static final String START_TIME = "startTime";

    public static final String END_TIME = "endTime";

    private long validProcedureTimeId;

    private Procedure procedure;

    private ProcedureDescriptionFormat procedureDescriptionFormat;

    private Date startTime;

    private Date endTime;

    public long getValidProcedureTimeId() {
        return this.validProcedureTimeId;
    }

    public void setValidProcedureTimeId(long validProcedureTimeId) {
        this.validProcedureTimeId = validProcedureTimeId;
    }

    @Override
    public Procedure getProcedure() {
        return this.procedure;
    }

    @Override
    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    @Override
    public ProcedureDescriptionFormat getProcedureDescriptionFormat() {
        return this.procedureDescriptionFormat;
    }

    @Override
    public ValidProcedureTime setProcedureDescriptionFormat(ProcedureDescriptionFormat procedureDescriptionFormat) {
        this.procedureDescriptionFormat = procedureDescriptionFormat;
        return this;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
