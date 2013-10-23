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
package org.n52.sos.request;

import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.util.StringHelper;

/**
 * SOS DescribeSensor request
 * 
 * @since 4.0.0
 */
public class DescribeSensorRequest extends AbstractServiceRequest {

    /**
     * Procedure identifier
     */
    private String procedure;

    /**
     * Output format
     */
    private String procedureDescriptionFormat;

    /**
     * Temporal filters
     */
    private Time validTime;

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sos.request.AbstractSosRequest#getOperationName()
     */
    @Override
    public String getOperationName() {
        return SosConstants.Operations.DescribeSensor.name();
    }

    /**
     * Get output format
     * 
     * @return output format
     */
    public String getProcedureDescriptionFormat() {
        return procedureDescriptionFormat;
    }

    /**
     * Set output format
     * 
     * @param procedureDescriptionFormat
     *            output format
     */
    public void setProcedureDescriptionFormat(String procedureDescriptionFormat) {
        this.procedureDescriptionFormat = procedureDescriptionFormat;
    }

    public boolean isSetProcedureDescriptionFormat() {
        return StringHelper.isNotEmpty(getProcedureDescriptionFormat());
    }

    /**
     * Get Procedure identifier
     * 
     * @return Procedure identifier
     */
    public String getProcedure() {
        return procedure;
    }

    /**
     * Set Procedure identifier
     * 
     * @param procedure
     *            Procedure identifier
     */
    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public boolean isSetProcedure() {
        return StringHelper.isNotEmpty(getProcedure());
    }

    /**
     * Get valid time
     * 
     * @return valid time
     */
    public Time getValidTime() {
        return validTime;
    }

    /**
     * Set valid time
     * 
     * @param validTime
     *            valid time
     */
    public void setValidTime(Time validTime) {
        this.validTime = validTime;
    }

    public boolean isSetValidTime() {
        return getValidTime() != null;
    }
}
