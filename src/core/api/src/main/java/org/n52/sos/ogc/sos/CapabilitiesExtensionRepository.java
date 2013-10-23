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
package org.n52.sos.ogc.sos;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.operator.RequestOperatorKey;
import org.n52.sos.request.operator.RequestOperatorRepository;
import org.n52.sos.util.AbstractConfiguringServiceLoaderRepository;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Repository for {@link CapabilitiesExtension} implementations
 * 
 * @since 4.0.0
 * 
 */
public class CapabilitiesExtensionRepository extends
        AbstractConfiguringServiceLoaderRepository<CapabilitiesExtensionProvider> {
    private static final Logger LOG = LoggerFactory.getLogger(CapabilitiesExtensionRepository.class);

    private static CapabilitiesExtensionRepository instance;

    /**
     * Implemented {@link CapabilitiesExtensionProvider}
     */
    private final Map<CapabilitiesExtensionKey, List<CapabilitiesExtensionProvider>> providers = Maps.newHashMap();

    public static CapabilitiesExtensionRepository getInstance() {
        if (instance == null) {
            instance = new CapabilitiesExtensionRepository();
        }
        return instance;
    }

    /**
     * Load implemented Capabilities extension provider
     * 
     * @throws ConfigurationException
     *             If no Capabilities extension provider is implemented
     */
    private CapabilitiesExtensionRepository() throws ConfigurationException {
        super(CapabilitiesExtensionProvider.class, false);
        load(false);
    }

    /**
     * Load the implemented Capabilities extension provider and add them to a
     * map with operation name as key
     * 
     * @param implementations
     *            the loaded implementations
     */
    @Override
    protected void processConfiguredImplementations(Set<CapabilitiesExtensionProvider> implementations) {
        this.providers.clear();
        for (CapabilitiesExtensionProvider provider : implementations) {
            if (provider.hasRelatedOperation()) {
                if (checkIfRelatedOperationIsActivated(provider)) {
                    LOG.info("Registered CapabilitiesExtensionProvider for {}", provider.getCapabilitiesExtensionKey());
                    addCapabilitiesExtensionProvider(provider);
                }
            } else {
                LOG.info("Registered CapabilitiesExtensionProvider for {}", provider.getCapabilitiesExtensionKey());
                addCapabilitiesExtensionProvider(provider);
            }
        }
    }

    public List<CapabilitiesExtensionProvider> getCapabilitiesExtensionProvider(
            CapabilitiesExtensionKey serviceOperatorIdentifier) throws OwsExceptionReport {
        return getAllValidCapabilitiesExtensionProvider(providers.get(serviceOperatorIdentifier));
    }

    /**
     * Get the implemented {@link CapabilitiesExtensionProvider} for service and
     * version
     * 
     * @param service
     *            Specific service
     * @param version
     *            Specific version
     * 
     * @return the implemented Capabilities extension provider
     * 
     * @throws OwsExceptionReport
     */
    public List<CapabilitiesExtensionProvider> getCapabilitiesExtensionProvider(String service, String version)
            throws OwsExceptionReport {
        return getCapabilitiesExtensionProvider(new CapabilitiesExtensionKey(service, version));
    }

    /**
     * Get all valid {@link CapabilitiesExtensionProvider}
     * 
     * @param list
     *            Loaded CapabilitiesExtensionProvider
     * 
     * @return Valid CapabilitiesExtensionProvider
     */
    private List<CapabilitiesExtensionProvider> getAllValidCapabilitiesExtensionProvider(
            List<CapabilitiesExtensionProvider> list) {
        List<CapabilitiesExtensionProvider> valid = Lists.newLinkedList();
        if (CollectionHelper.isNotEmpty(list)) {
            for (CapabilitiesExtensionProvider provider : list) {
                if (provider.hasRelatedOperation()) {
                    if (checkIfRelatedOperationIsActivated(provider)) {
                        valid.add(provider);
                    }
                } else {
                    valid.add(provider);
                }
            }
        }
        return valid;
    }

    /**
     * Add a loaded {@link CapabilitiesExtensionProvider} to the local map
     * 
     * @param provider
     *            Loaded CapabilitiesExtensionProvider
     */
    private void addCapabilitiesExtensionProvider(CapabilitiesExtensionProvider provider) {
        List<CapabilitiesExtensionProvider> extensions = Lists.newLinkedList();
        extensions.add(provider);
        if (providers.containsKey(provider.getCapabilitiesExtensionKey())) {
            extensions.addAll(providers.get(provider.getCapabilitiesExtensionKey()));
        }
        this.providers.put(provider.getCapabilitiesExtensionKey(), extensions);
    }

    /**
     * Check if the related operation for the loaded
     * {@link CapabilitiesExtensionProvider} is active
     * 
     * @param cep
     *            CapabilitiesExtensionProvider to check
     * 
     * @return <code>true</code>, if related operation is active
     */
    private boolean checkIfRelatedOperationIsActivated(CapabilitiesExtensionProvider cep) {
        CapabilitiesExtensionKey cek = cep.getCapabilitiesExtensionKey();
        RequestOperatorKey rok = new RequestOperatorKey(cek.getService(), cek.getVersion(), cep.getRelatedOperation());
        return RequestOperatorRepository.getInstance().getActiveRequestOperatorKeys().contains(rok);
    }
}
