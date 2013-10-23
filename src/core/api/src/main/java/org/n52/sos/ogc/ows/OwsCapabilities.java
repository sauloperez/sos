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
package org.n52.sos.ogc.ows;

import org.n52.sos.util.StringHelper;

/**
 * @since 4.0.0
 * 
 */
public abstract class OwsCapabilities {

    private String service;

    private String version;

    private String updateSequence;

    /**
     * Service identification, loaded from file.
     */
    private SosServiceIdentification serviceIdentification;

    /**
     * Service provider, loaded from file.
     */
    private SosServiceProvider serviceProvider;

    /**
     * Operations meta data for all supported operations.
     */
    private OwsOperationsMetadata operationsMetadata;

    public OwsCapabilities(String service, String version) {
        this.version = version;
    }

    /**
     * @return the service
     */
    public String getService() {
        return this.service;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version
     *            the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isSetVersion() {
        return StringHelper.isNotEmpty(getVersion());
    }

    /**
     * @return the updateSequence
     */
    public String getUpdateSequence() {
        return updateSequence;
    }

    /**
     * @param updateSequence
     *            the updateSequence to set
     */
    public void setUpdateSequence(String updateSequence) {
        this.updateSequence = updateSequence;
    }

    public boolean isSetUpdateSequence() {
        return StringHelper.isNotEmpty(getUpdateSequence());
    }

    /**
     * Set service identification
     * 
     * @param serviceIdentification
     *            service identification
     */
    public void setServiceIdentification(SosServiceIdentification serviceIdentification) {
        this.serviceIdentification = serviceIdentification;

    }

    /**
     * Get service identification
     * 
     * @return service identification
     */
    public SosServiceIdentification getServiceIdentification() {
        return serviceIdentification;
    }

    public boolean isSetServiceIdentification() {
        return getServiceIdentification() != null;
    }

    /**
     * Set service provider
     * 
     * @param serviceProvider
     *            service provider
     */
    public void setServiceProvider(SosServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;

    }

    /**
     * Get service provider
     * 
     * @return service provider
     */
    public SosServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public boolean isSetServiceProvider() {
        return getServiceProvider() != null;
    }

    /**
     * Get operations metadata
     * 
     * @return operations metadata
     */
    public OwsOperationsMetadata getOperationsMetadata() {
        return operationsMetadata;
    }

    /**
     * Set operations metadata
     * 
     * @param operationsMetadata
     *            operations metadata
     */
    public void setOperationsMetadata(OwsOperationsMetadata operationsMetadata) {
        this.operationsMetadata = operationsMetadata;
    }

    public boolean isSetOperationsMetadata() {
        return getOperationsMetadata() != null && !getOperationsMetadata().isEmpty();
    }
}
