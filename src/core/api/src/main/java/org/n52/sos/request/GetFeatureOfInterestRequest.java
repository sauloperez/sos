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
import java.util.Map;

import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.util.CollectionHelper;

/**
 * Sos GetFeatureOfInterst request
 * 
 * @since 4.0.0
 */
public class GetFeatureOfInterestRequest extends AbstractServiceRequest {

    /**
     * FOI identifiers list
     */
    private List<String> featureIdentifiers;

    /**
     * FOI observedProperties list
     */
    private List<String> observedProperties;

    /**
     * FOI procedures list
     */
    private List<String> procedures;

    /**
     * FOI spatial filters list
     */
    private List<SpatialFilter> spatialFilters;

    /**
     * FOI temporal filters list
     */
    private List<TemporalFilter> temporalFilters;

    private Map<String, String> namespaces;

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sos.request.AbstractSosRequest#getOperationName()
     */
    @Override
    public String getOperationName() {
        return SosConstants.Operations.GetFeatureOfInterest.name();
    }

    /**
     * Get temporal filters
     * 
     * @return temporal filters
     */
    public List<TemporalFilter> getTemporalFilters() {
        return temporalFilters;
    }

    /**
     * Set temporal filters
     * 
     * @param temporalFilters
     *            temporal filters
     */
    public void setTemporalFilters(List<TemporalFilter> temporalFilters) {
        this.temporalFilters = temporalFilters;
    }

    /**
     * Get FOI identifiers
     * 
     * @return FOI identifiers
     */
    public List<String> getFeatureIdentifiers() {
        return featureIdentifiers;
    }

    /**
     * Set FOI identifiers
     * 
     * @param featureIDs
     *            FOI identifiers
     */
    public void setFeatureIdentifiers(List<String> featureIDs) {
        this.featureIdentifiers = featureIDs;
    }

    /**
     * Get FOI observedProperties
     * 
     * @return FOI observedProperties
     */
    public List<String> getObservedProperties() {
        return observedProperties;
    }

    /**
     * Set FOI observedProperties
     * 
     * @param observedProperties
     *            FOI observedProperties
     */
    public void setObservedProperties(List<String> observedProperties) {
        this.observedProperties = observedProperties;
    }

    /**
     * Get FOI procedures
     * 
     * @return FOI procedures
     */
    public List<String> getProcedures() {
        return procedures;
    }

    /**
     * Set FOI procedures
     * 
     * @param procedures
     *            FOI procedures
     */
    public void setProcedures(List<String> procedures) {
        this.procedures = procedures;
    }

    /**
     * Get spatial filters
     * 
     * @return spatial filters
     */
    public List<SpatialFilter> getSpatialFilters() {
        return spatialFilters;
    }

    /**
     * Set spatial filters
     * 
     * @param spatialFilters
     *            spatial filters
     */
    public void setSpatialFilters(List<SpatialFilter> spatialFilters) {
        this.spatialFilters = spatialFilters;
    }

    public void setNamespaces(Map<String, String> namespaces) {
        this.namespaces = namespaces;
    }

    public Map<String, String> getNamespaces() {
        return namespaces;
    }

    public boolean isSetFeatureOfInterestIdentifiers() {
        return CollectionHelper.isNotEmpty(getFeatureIdentifiers());
    }

    public boolean isSetTemporalFilters() {
        return CollectionHelper.isNotEmpty(getTemporalFilters());
    }

    public boolean isSetSpatialFilters() {
        return CollectionHelper.isNotEmpty(getSpatialFilters());
    }

    public boolean isSetObservableProperties() {
        return CollectionHelper.isNotEmpty(getObservedProperties());
    }

    public boolean isSetProcedures() {
        return CollectionHelper.isNotEmpty(getProcedures());
    }

    public boolean isSetNamespaces() {
        return CollectionHelper.isNotEmpty(namespaces);
    }

    public boolean containsOnlyFeatureParameter() {
        return !isSetObservableProperties() && !isSetProcedures() && !isSetTemporalFilters();
    }

}
