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
package org.n52.sos.ogc.om;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.sos.SosProcedureDescription;

import com.google.common.base.Objects;

/**
 * @since 4.0.0
 */
public class OmObservationConstellation implements Serializable, Cloneable {
    private static final long serialVersionUID = 8758412729768944974L;

    /** Identifier of the procedure by which the observation is made */
    private SosProcedureDescription procedure;

    /** Identifier of the observableProperty to which the observation accords to */
    private AbstractPhenomenon observableProperty;

    /** Identifiers of the offerings to which this observation belongs */
    private Set<String> offerings;

    /** Identifier of the featureOfInterest to which this observation belongs */
    private AbstractFeature featureOfInterest;

    /** type of the observation */
    private String observationType;

    // private SosResultTemplate sosResultTemplate;

    /**
     * default constructor
     */
    public OmObservationConstellation() {
        super();
    }

    /**
     * constructor
     * 
     * @param procedure
     *            Procedure by which the observation is made
     * @param observableProperty
     *            observableProperty to which the observation accords to
     * @param featureOfInterest
     *            featureOfInterest to which this observation belongs
     */
    public OmObservationConstellation(SosProcedureDescription procedure, AbstractPhenomenon observableProperty,
            AbstractFeature featureOfInterest) {
        super();
        this.procedure = procedure;
        this.observableProperty = observableProperty;
        this.featureOfInterest = featureOfInterest;
    }

    /**
     * constructor
     * 
     * @param procedure
     *            Procedure by which the observation is made
     * @param observableProperty
     *            observableProperty to which the observation accords to
     * @param offerings
     *            offering to which this observation belongs
     * @param featureOfInterest
     *            featureOfInterest to which this observation belongs
     * @param observationType
     *            Observation type
     */
    public OmObservationConstellation(SosProcedureDescription procedure, AbstractPhenomenon observableProperty,
            Set<String> offerings, AbstractFeature featureOfInterest, String observationType) {
        super();
        this.procedure = procedure;
        this.observableProperty = observableProperty;
        this.offerings = offerings;
        this.featureOfInterest = featureOfInterest;
        this.observationType = observationType;
    }

    /**
     * Get the procedure
     * 
     * @return the procedure
     */
    public SosProcedureDescription getProcedure() {
        return procedure;
    }

    /**
     * Set the procedure
     * 
     * @param procedure
     *            the procedure to set
     */
    public void setProcedure(SosProcedureDescription procedure) {
        this.procedure = procedure;
    }

    /**
     * Get observableProperty
     * 
     * @return the observableProperty
     */
    public AbstractPhenomenon getObservableProperty() {
        return observableProperty;
    }

    /**
     * Set observableProperty
     * 
     * @param observableProperty
     *            the observableProperty to set
     */
    public void setObservableProperty(AbstractPhenomenon observableProperty) {
        this.observableProperty = observableProperty;
    }

    /**
     * Get offering
     * 
     * @return the offering
     */
    public Set<String> getOfferings() {
        return offerings;
    }

    /**
     * Set offering
     * 
     * @param offerings
     *            the offering to set
     */
    public void setOfferings(Set<String> offerings) {
        this.offerings = offerings;
    }

    public void setOfferings(List<String> offerings) {
        if (this.offerings == null) {
            this.offerings = new HashSet<String>(0);
        }
        this.offerings.addAll(offerings);
    }

    public void addOffering(String offering) {
        if (offerings == null) {
            offerings = new HashSet<String>(0);
        }
        offerings.add(offering);
    }

    /**
     * Get featureOfInterest
     * 
     * @return the featureOfInterest
     */
    public AbstractFeature getFeatureOfInterest() {
        return featureOfInterest;
    }

    /**
     * Set featureOfInterest
     * 
     * @param featureOfInterest
     *            the featureOfInterest to set
     */
    public void setFeatureOfInterest(AbstractFeature featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
    }

    /**
     * Get observation type
     * 
     * @return the observationType
     */
    public String getObservationType() {
        return observationType;
    }

    /**
     * Set observation type
     * 
     * @param observationType
     *            the observationType to set
     */
    public void setObservationType(String observationType) {
        this.observationType = observationType;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof OmObservationConstellation) {
            return hashCode() == o.hashCode();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.procedure, this.observableProperty,
                                this.offerings, this.featureOfInterest);
    }

    /**
     * Check if constellations are equal excluding observableProperty
     * 
     * @param toCheckObsConst
     *            Observation constellation to chek
     * @return true if equals
     */
    public boolean equalsExcludingObsProp(OmObservationConstellation toCheckObsConst) {
        return (procedure.equals(toCheckObsConst.getProcedure())
                && featureOfInterest.equals(toCheckObsConst.getFeatureOfInterest())
                && observationType.equals(toCheckObsConst.getObservationType()) && checkObservationTypeForMerging());

    }

    private boolean checkObservationTypeForMerging() {
        return (!observationType.equals(OmConstants.OBS_TYPE_MEASUREMENT)
                && !observationType.equals(OmConstants.OBS_TYPE_CATEGORY_OBSERVATION) && !observationType
                    .equals(OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION));
    }

    public boolean isSetObservationType() {
        return observationType != null && !observationType.isEmpty();
    }

    public boolean isSetOfferings() {
        return offerings != null && !offerings.isEmpty();
    }

    @Override
    public OmObservationConstellation clone() throws CloneNotSupportedException {
        OmObservationConstellation clone = new OmObservationConstellation();
        clone.setFeatureOfInterest(this.getFeatureOfInterest());
        clone.setObservableProperty(this.getObservableProperty());
        clone.setObservationType(this.getObservationType());
        clone.setOfferings(new HashSet<String>(this.getOfferings()));
        clone.setProcedure(this.getProcedure());
        return clone;
    }

    public boolean isEmpty() {
        return !isSetOfferings() && !isSetProcedure() && !isSetObservableProperty() && isSetFeatureOfInterest();
    }

    private boolean isSetFeatureOfInterest() {
        return getProcedure() != null && getProcedure().isSetIdentifier();
    }

    private boolean isSetObservableProperty() {
        return getObservableProperty() != null && getObservableProperty().isSetIdentifier();
    }

    private boolean isSetProcedure() {
        return getProcedure() != null && getProcedure().isSetIdentifier();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("OmObservationConstellation [");
        builder.append("procedure=").append(getProcedure().getIdentifier());
        builder.append(", observableProperty=").append(getObservableProperty().getIdentifier());
        builder.append(", featureOfInterest=").append(getFeatureOfInterest().getIdentifier());
        if (isSetOfferings()) {
            builder.append(", offerings=[");
            boolean first = true;
            for (String offering : getOfferings()) {
                if (!first) {
                    builder.append(", ");
                }
                first = false;
                builder.append(offering);
            }
            builder.append("]");
        }
        if (isSetObservationType()) {
            builder.append(", observationType=").append(getObservationType());
        }
        builder.append("]");
        return builder.toString();
    }
}
