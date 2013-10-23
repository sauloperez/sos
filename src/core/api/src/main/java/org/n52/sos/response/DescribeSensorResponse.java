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
package org.n52.sos.response;

import java.util.List;

import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.util.CollectionHelper;

/**
 * @since 4.0.0
 * 
 */
public class DescribeSensorResponse extends AbstractServiceResponse {

    private String outputFormat;

    private List<SosProcedureDescription> procedureDescriptions;

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    @Deprecated
    public SosProcedureDescription getSensorDescription() {
        if (isSetProcedureDescriptions()) {
            return getProcedureDescriptions().get(0);
        }
        return null;
    }

    @Deprecated
    public void setSensorDescription(SosProcedureDescription procedureDescription) {
        addSensorDescription(procedureDescription);
    }

    @Override
    public String getOperationName() {
        return SosConstants.Operations.DescribeSensor.name();
    }

    public void setSensorDescriptions(List<SosProcedureDescription> procedureDescriptions) {
        if (isSetProcedureDescriptions()) {
            this.procedureDescriptions =
                    CollectionHelper.conjunctCollections(getProcedureDescriptions(), procedureDescriptions);
        } else {
            this.procedureDescriptions = procedureDescriptions;
        }
    }

    public boolean isSetProcedureDescriptions() {
        return CollectionHelper.isNotEmpty(getProcedureDescriptions());
    }

    public List<SosProcedureDescription> getProcedureDescriptions() {
        return this.procedureDescriptions;
    }

    public void addSensorDescription(SosProcedureDescription procedureDescription) {
        if (isSetProcedureDescriptions()) {
            getProcedureDescriptions().add(procedureDescription);
        } else {
            this.procedureDescriptions = CollectionHelper.list(procedureDescription);
        }
    }
}
