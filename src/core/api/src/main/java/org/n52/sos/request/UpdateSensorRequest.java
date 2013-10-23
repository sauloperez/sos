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

import java.util.ArrayList;
import java.util.List;

import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;

/**
 * class represents a updateSensor request
 * 
 * @since 4.0.0
 */
public class UpdateSensorRequest extends AbstractServiceRequest {

    private String procedureIdentifier;

    private String procedureDescriptionFormat;

    /** SOS SensorML description */
    private List<SosProcedureDescription> procedureDescriptions;

    /**
     * default constructor
     */
    public UpdateSensorRequest() {
        super();
    }

    @Override
    public String getOperationName() {
        return Sos2Constants.Operations.UpdateSensorDescription.name();
    }

    /**
     * @return the procedureIdentifier
     */
    public String getProcedureIdentifier() {
        return procedureIdentifier;
    }

    /**
     * @param procedureIdentifier
     *            the procedureIdentifier to set
     */
    public void setProcedureIdentifier(String procedureIdentifier) {
        this.procedureIdentifier = procedureIdentifier;
    }

    public boolean isSetProcedureIdentifier() {
        return StringHelper.isNotEmpty(getProcedureIdentifier());
    }

    public String getProcedureDescriptionFormat() {
        return procedureDescriptionFormat;
    }

    public void setProcedureDescriptionFormat(String procedureDescriptionFormat) {
        this.procedureDescriptionFormat = procedureDescriptionFormat;
    }

    public boolean isSetProcedureDescriptionFormat() {
        return StringHelper.isNotEmpty(getProcedureDescriptionFormat());
    }

    public List<SosProcedureDescription> getProcedureDescriptions() {
        return procedureDescriptions;
    }

    public void setProcedureDescriptions(List<SosProcedureDescription> procedureDescriptions) {
        this.procedureDescriptions = procedureDescriptions;
    }

    public void addProcedureDescriptionString(SosProcedureDescription procedureDescription) {
        if (procedureDescriptions == null) {
            procedureDescriptions = new ArrayList<SosProcedureDescription>();
        }
        procedureDescriptions.add(procedureDescription);
    }

    public boolean isSetProcedureDescriptions() {
        return CollectionHelper.isNotEmpty(getProcedureDescriptions());
    }
}
