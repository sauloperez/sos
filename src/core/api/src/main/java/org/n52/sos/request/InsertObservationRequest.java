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

import java.util.LinkedList;
import java.util.List;

import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;

/**
 * SOS InsertObservation request
 * 
 * @since 4.0.0
 */
public class InsertObservationRequest extends AbstractServiceRequest {

    /**
     * Assigned sensor id
     */
    private String assignedSensorId;

    private List<String> offerings;

    /**
     * SOS observation collection with observations to insert
     */
    private List<OmObservation> observations;

    public InsertObservationRequest() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sos.request.AbstractSosRequest#getOperationName()
     */
    @Override
    public String getOperationName() {
        return SosConstants.Operations.InsertObservation.name();
    }

    /**
     * Get assigned sensor id
     * 
     * @return assigned sensor id
     */
    public String getAssignedSensorId() {
        return assignedSensorId;
    }

    /**
     * Set assigned sensor id
     * 
     * @param assignedSensorId
     *            assigned sensor id
     */
    public void setAssignedSensorId(String assignedSensorId) {
        this.assignedSensorId = assignedSensorId;
    }

    public boolean isSetAssignedSensorId() {
        return StringHelper.isNotEmpty(getAssignedSensorId());
    }

    /**
     * Get observations to insert
     * 
     * @return observations to insert
     */
    public List<OmObservation> getObservations() {
        return observations;
    }

    /**
     * Set observations to insert
     * 
     * @param observation
     *            observations to insert
     */
    public void setObservation(List<OmObservation> observation) {
        this.observations = observation;
    }

    public void addObservation(OmObservation observation) {
        if (observations == null) {
            observations = new LinkedList<OmObservation>();
        }
        observations.add(observation);
    }

    public boolean isSetObservation() {
        return CollectionHelper.isNotEmpty(getObservations());
    }

    public void setOfferings(List<String> offerings) {
        this.offerings = offerings;
    }

    public List<String> getOfferings() {
        return offerings;
    }

    public boolean isSetOfferings() {
        return CollectionHelper.isNotEmpty(getOfferings());
    }
}
