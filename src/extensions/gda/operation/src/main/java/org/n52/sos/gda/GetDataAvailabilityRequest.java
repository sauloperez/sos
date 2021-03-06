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
package org.n52.sos.gda;

import java.util.LinkedList;
import java.util.List;

import org.n52.sos.request.AbstractServiceRequest;

/**
 * A request to obtain the {@code DataAvailabilites} of the SOS.
 * 
 * @author Christian Autermann
 * 
 * @since 4.0.0
 */
public class GetDataAvailabilityRequest extends AbstractServiceRequest {

    private final List<String> procedures = new LinkedList<String>();

    private final List<String> observedProperties = new LinkedList<String>();

    private final List<String> featuresOfInterest = new LinkedList<String>();

    @Override
    public String getOperationName() {
        return GetDataAvailabilityConstants.OPERATION_NAME;
    }

    /**
     * @return the requested {@code procedures}.
     */
    public List<String> getProcedures() {
        return procedures;
    }

    /**
     * @return the requested {@code observedProperties}.
     */
    public List<String> getObservedProperties() {
        return observedProperties;
    }

    /**
     * @return the requested {@code featuresOfInterest}.
     */
    public List<String> getFeaturesOfInterest() {
        return featuresOfInterest;
    }

    /**
     * Add a {@code procedure} to the request.
     * 
     * @param procedure
     *            the {@code procedure}
     */
    public void addProcedure(String procedure) {
        this.procedures.add(procedure);
    }

    /**
     * Add a {@code observedProperty} to the request.
     * 
     * @param observedProperty
     *            the {@code observedProperty}
     */
    public void addObservedProperty(String observedProperty) {
        this.observedProperties.add(observedProperty);
    }

    /**
     * Add a {@code featureOfInterest} to the request.
     * 
     * @param featureOfInterest
     *            the {@code featureOfInterest}
     */
    public void addFeatureOfInterest(String featureOfInterest) {
        this.featuresOfInterest.add(featureOfInterest);
    }

    public boolean isSetProcedures() {
        return procedures != null && !procedures.isEmpty();
    }

    public boolean isSetObservedProperties() {
        return observedProperties != null && !observedProperties.isEmpty();
    }

    public boolean isSetFeaturesOfInterest() {
        return featuresOfInterest != null && !featuresOfInterest.isEmpty();
    }
}
