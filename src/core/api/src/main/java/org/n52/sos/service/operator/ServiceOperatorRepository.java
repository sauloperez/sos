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
package org.n52.sos.service.operator;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.operator.RequestOperatorRepository;
import org.n52.sos.util.AbstractConfiguringServiceLoaderRepository;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.MultiMaps;
import org.n52.sos.util.SetMultiMap;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class ServiceOperatorRepository extends AbstractConfiguringServiceLoaderRepository<ServiceOperator> {
    private static ServiceOperatorRepository instance;

    /**
     * Implemented ServiceOperator
     */
    private final Map<ServiceOperatorKey, ServiceOperator> serviceOperators = Maps.newHashMap();

    /** supported SOS versions */
    private final SetMultiMap<String, String> supportedVersions = MultiMaps.newSetMultiMap();

    /** supported services */
    private final Set<String> supportedServices = Sets.newHashSet();

    /**
     * Load implemented request listener
     * 
     * @throws ConfigurationException
     *             If no request listener is implemented
     */
    private ServiceOperatorRepository() throws ConfigurationException {
        super(ServiceOperator.class, false);
        load(false);
    }

    public static ServiceOperatorRepository getInstance() {
        if (instance == null) {
            instance = new ServiceOperatorRepository();
        }
        return instance;
    }

    /**
     * Load the implemented request listener and add them to a map with
     * operation name as key
     * 
     * @param implementations
     *            the loaded implementations
     * 
     * @throws ConfigurationException
     *             If no request listener is implemented
     */
    @Override
    protected void processConfiguredImplementations(Set<ServiceOperator> implementations)
            throws ConfigurationException {
        this.serviceOperators.clear();
        this.supportedServices.clear();
        this.supportedVersions.clear();
        for (ServiceOperator so : implementations) {
            this.serviceOperators.put(so.getServiceOperatorKey(), so);
            this.supportedVersions.add(so.getServiceOperatorKey().getService(), so.getServiceOperatorKey()
                    .getVersion());
            this.supportedServices.add(so.getServiceOperatorKey().getService());
        }
    }

    /**
     * Update/reload the implemented request listener
     * 
     * @throws ConfigurationException
     *             If no request listener is implemented
     */
    @Override
    public void update() throws ConfigurationException {
        RequestOperatorRepository.getInstance().update();
        super.update();
    }

    /**
     * @return the implemented request listener
     */
    public Map<ServiceOperatorKey, ServiceOperator> getServiceOperators() {
        return Collections.unmodifiableMap(serviceOperators);
    }

    public Set<ServiceOperatorKey> getServiceOperatorKeyTypes() {
        return getServiceOperators().keySet();
    }

    public ServiceOperator getServiceOperator(ServiceOperatorKey sok) {
        return serviceOperators.get(sok);
    }

    /**
     * @param service
     *            the service
     * @param version
     *            the version
     * @return the implemented request listener
     * 
     * 
     * @throws OwsExceptionReport
     */
    public ServiceOperator getServiceOperator(String service, String version) throws OwsExceptionReport {
        return getServiceOperator(new ServiceOperatorKey(service, version));
    }

    /**
     * @return the supportedVersions
     * 
     * @deprecated use getSupporteVersions(String service)
     */
    @Deprecated
    public Set<String> getSupportedVersions() {
        return getAllSupportedVersions();

    }

    public Set<String> getAllSupportedVersions() {
        return CollectionHelper.union(this.supportedVersions.values());
    }

    /**
     * @param service
     *            the service
     * @return the supportedVersions
     * 
     */
    public Set<String> getSupportedVersions(String service) {
        if (isServiceSupported(service)) {
            return Collections.unmodifiableSet(supportedVersions.get(service));
        }
        return Sets.newHashSet();
    }

    /**
     * @param version
     *            the version
     * @return the supportedVersions
     * 
     * @deprecated use isVersionSupported(String service, String version)
     */
    @Deprecated
    public boolean isVersionSupported(String version) {
        return getAllSupportedVersions().contains(version);
    }

    /**
     * @param service
     *            the service
     * @param version
     *            the version
     * @return the supportedVersions
     * 
     */
    public boolean isVersionSupported(String service, String version) {
        return isServiceSupported(service) && supportedVersions.get(service).contains(version);
    }

    /**
     * @return the supportedVersions
     */
    public Set<String> getSupportedServices() {
        return Collections.unmodifiableSet(this.supportedServices);
    }

    public boolean isServiceSupported(String service) {
        return this.supportedServices.contains(service);
    }

}
