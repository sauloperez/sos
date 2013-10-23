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

import java.util.Map;
import java.util.Set;

import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.util.AbstractConfiguringServiceLoaderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * Repository for {@link OwsExtendedCapabilities}. Loads all implemented
 * {@link OwsExtendedCapabilities}.
 * 
 * @since 4.0.0
 * 
 */
public class OwsExtendedCapabilitiesRepository extends
        AbstractConfiguringServiceLoaderRepository<OwsExtendedCapabilities> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwsExtendedCapabilitiesRepository.class);

    private static OwsExtendedCapabilitiesRepository instance;

    private final Map<String, OwsExtendedCapabilities> extendedCapabilities = Maps.newHashMap();

    /**
     * For singleton use
     * 
     * @return The single instance
     */
    public static OwsExtendedCapabilitiesRepository getInstance() {
        if (instance == null) {
            instance = new OwsExtendedCapabilitiesRepository();
        }
        return instance;
    }

    /**
     * Load implemented {@link OwsExtendedCapabilities}
     * 
     * @throws ConfigurationException
     *             If no Capabilities extension providerr is implemented
     */
    private OwsExtendedCapabilitiesRepository() throws ConfigurationException {
        super(OwsExtendedCapabilities.class, false);
        load(false);
    }

    @Override
    protected void processConfiguredImplementations(Set<OwsExtendedCapabilities> implementations)
            throws ConfigurationException {
        this.extendedCapabilities.clear();
        for (OwsExtendedCapabilities owsExtendedCapabilities : implementations) {
            if (hasExtendedCapabilitiesFor(owsExtendedCapabilities.getService())) {
                LOGGER.warn(
                        "The OwsExtendedCapabilitiesRepository still contains an OwsExtendedCapabilities implementation for service '{}'",
                        owsExtendedCapabilities.getService());
            } else {
                this.extendedCapabilities.put(owsExtendedCapabilities.getService(), owsExtendedCapabilities);
            }
        }
    }

    /**
     * Get map with all loaded OwsExtendedCapabilities implementations
     * 
     * @return Map with loaded OwsExtendedCapabilities implementations for
     *         services
     */
    public Map<String, OwsExtendedCapabilities> getExtendedCapabilities() {
        return extendedCapabilities;
    }

    /**
     * Get the loaded OwsExtendedCapabilities implementation for the specific
     * service
     * 
     * @param service
     *            The related service
     * @return loaded OwsExtendedCapabilities implementation
     */
    public OwsExtendedCapabilities getExtendedCapabilities(String service) {
        return getExtendedCapabilities().get(service);
    }

    /**
     * Check if a loaded OwsExtendedCapabilities implementation is loaded for
     * the specific service
     * 
     * @param service
     *            The related service to check for
     * @return <code>true</code>, if a OwsExtendedCapabilities implementation is
     *         loaded for the specific service
     */
    public boolean hasExtendedCapabilitiesFor(String service) {
        return getExtendedCapabilities().containsKey(service);
    }

}
