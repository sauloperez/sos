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

import java.util.List;

import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosInsertionMetadata;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.swes.SwesFeatureRelationship;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;

import com.google.common.collect.Lists;

/**
 * @since 4.0.0
 * 
 */
public class InsertSensorRequest extends AbstractServiceRequest {

    private String procedureDescriptionFormat;

    /** observableProperty parameter */
    private List<String> observableProperty;

    private List<SwesFeatureRelationship> relatedFeatures;

    /** SOS SensorML description */
    private SosProcedureDescription procedureDescription;

    private String assignedProcedureIdentifier;

    private List<SosOffering> assignedOfferings = Lists.newLinkedList();

    /** metadata parameter */
    private SosInsertionMetadata metadata;

    /**
     * default constructor
     */
    public InsertSensorRequest() {
        super();
    }

    @Override
    public String getOperationName() {
        return Sos2Constants.Operations.InsertSensor.name();
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

    /**
     * Get the observableProperty contained in request.
     * 
     * @return the observableProperty
     */
    public List<String> getObservableProperty() {
        return observableProperty;
    }

    /**
     * Set the observableProperty contained in request.
     * 
     * @param observableProperty
     *            the observableProperty to set
     */
    public void setObservableProperty(List<String> observableProperty) {
        this.observableProperty = observableProperty;
    }

    public boolean isSetObservableProperty() {
        return CollectionHelper.isNotEmpty(getObservableProperty());
    }

    /**
     * Get the sensor description contained in request.
     * 
     * @return the sosSensorML
     */
    public SosProcedureDescription getProcedureDescription() {
        return procedureDescription;
    }

    /**
     * Set the sensor description contained in request.
     * 
     * @param procedureDescription
     *            the procedureDescription to set
     */
    public void setProcedureDescription(SosProcedureDescription procedureDescription) {
        this.procedureDescription = procedureDescription;
    }

    public boolean isSetProcedureDescription() {
        return procedureDescription != null;
    }

    /**
     * Get the metadata contained in request.
     * 
     * @return the metadata
     */
    public SosInsertionMetadata getMetadata() {
        return metadata;
    }

    /**
     * Set the metadata contained in request.
     * 
     * @param metadata
     *            the metadata to set
     */
    public void setMetadata(SosInsertionMetadata metadata) {
        this.metadata = metadata;
    }

    public boolean isSetMetadata() {
        return getMetadata() != null;
    }

    public List<SwesFeatureRelationship> getRelatedFeatures() {
        return relatedFeatures;
    }

    public void setRelatedFeature(List<SwesFeatureRelationship> relatedFeatures) {
        this.relatedFeatures = relatedFeatures;
    }

    public boolean isSetRelatedFeatures() {
        return CollectionHelper.isNotEmpty(getRelatedFeatures());
    }

    public String getAssignedProcedureIdentifier() {
        return assignedProcedureIdentifier;
    }

    public void setAssignedProcedureIdentifier(String assignedProcedureID) {
        this.assignedProcedureIdentifier = assignedProcedureID;
    }

    public boolean isSetAssignedProcedureIdentifier() {
        return StringHelper.isNotEmpty(getAssignedProcedureIdentifier());
    }

    public List<SosOffering> getAssignedOfferings() {
        return assignedOfferings;
    }

    public void setAssignedOfferings(List<SosOffering> assignedOfferings) {
        this.assignedOfferings.addAll(assignedOfferings);
    }

    public SosOffering getFirstAssignedOffering() {
        if (isSetAssignedOfferings()) {
            return assignedOfferings.get(0);
        }
        return null;
    }

    public boolean isSetAssignedOfferings() {
        return CollectionHelper.isNotEmpty(getAssignedOfferings());
    }
}
