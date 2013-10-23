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
package org.n52.sos.service;

import static org.n52.sos.service.TransactionalSecuritySettings.ALLOWED_PROXIES;
import static org.n52.sos.service.TransactionalSecuritySettings.TRANSACTIONAL_ACTIVE;
import static org.n52.sos.service.TransactionalSecuritySettings.TRANSACTIONAL_ALLOWED_IPS;
import static org.n52.sos.service.TransactionalSecuritySettings.TRANSACTIONAL_TOKEN;

import org.n52.sos.config.SettingsManager;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.net.IPAddress;
import org.n52.sos.util.net.IPAddressRange;
import org.n52.sos.util.StringHelper;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * @author Shane StClair <shane@axiomalaska.com>
 * 
 * @since 4.0.0
 */
@Configurable
public class TransactionalSecurityConfiguration {
    private static TransactionalSecurityConfiguration instance;

    private boolean transactionalActive;

    /**
     * List of CIDR encoded or raw IP ranges allowed to make transactional
     * requests
     */
    private ImmutableSet<IPAddressRange> transactionalAllowedIps = ImmutableSet.of();

    /**
     * Authorization token required for transactional requests
     */
    private String transactionalToken;

    private ImmutableSet<IPAddress> allowedProxies =  ImmutableSet.of();

    /**
     * @return Returns a singleton instance of the
     *         TransactionalSecurityConfiguration.
     */
    public static TransactionalSecurityConfiguration getInstance() {
        if (instance == null) {
            instance = new TransactionalSecurityConfiguration();
            SettingsManager.getInstance().configure(instance);
        }
        return instance;
    }

    /**
     * private constructor for singleton
     */
    private TransactionalSecurityConfiguration() {
    }

    /**
     * @return the transactionalActive
     */
    public boolean isTransactionalActive() {
        return transactionalActive;
    }

    /**
     * @param transactionalActive
     *            the transactionalActive to set
     */
    @Setting(TRANSACTIONAL_ACTIVE)
    public void setTransactionalActive(boolean transactionalActive) {
        this.transactionalActive = transactionalActive;
    }

    /**
     * @return List of CIDR encoded or raw IP ranges allowed to make
     *         transactional requests
     */
    public ImmutableSet<IPAddressRange> getAllowedAddresses() {
        return transactionalAllowedIps;
    }

    @Setting(TRANSACTIONAL_ALLOWED_IPS)
    public void setTransactionalAllowedIps(String txAllowedIps) throws ConfigurationException {
        if (StringHelper.isNotEmpty(txAllowedIps)) {
            Builder<IPAddressRange> builder = ImmutableSet.builder();
            for (String splitted : txAllowedIps.split(",")) {
                String trimmed = splitted.trim();
                String cidrAddress = trimmed.contains("/") ? trimmed : trimmed + "/32";
                try {
                    builder.add(new IPAddressRange(cidrAddress));
                } catch (IllegalArgumentException e) {
                    throw new ConfigurationException(
                            "Transactional allowed address is not a valid CIDR range or IP address", e);
                }
            }
            this.transactionalAllowedIps = builder.build();
        } else {
            this.transactionalAllowedIps = ImmutableSet.of();
        }
    }

    @Setting(ALLOWED_PROXIES)
    public void setAllowedProxies(String proxies) {
        if (StringHelper.isNotEmpty(proxies)) {
            Builder<IPAddress> builder = ImmutableSet.builder();;
            for (String splitted : proxies.split(",")) {
                try {
                    builder.add(new IPAddress(splitted.trim()));
                } catch (IllegalArgumentException e) {
                    throw new ConfigurationException(
                            "Allowed proxy address is not a valid IP address", e);
                }
            }
            this.allowedProxies = builder.build();
        } else {
            this.allowedProxies = ImmutableSet.of(new IPAddress("127.0.0.1"));
        }
    }

    public ImmutableSet<IPAddress> getAllowedProxies() {
        return this.allowedProxies;
    }

    /**
     * @return Authorization token for transactional requests
     */
    public String getTransactionalToken() {
        return transactionalToken;
    }

    @Setting(TRANSACTIONAL_TOKEN)
    public void setTransactionalToken(String txToken) {
        transactionalToken = txToken;
    }

    /**
     * @return true if allowed IPs or token is defined
     */
    public boolean isSetTransactionalSecurityActive() {
        return transactionalActive;
    }

    /**
     * @return true if allowed IPs defined
     */
    public boolean isSetTransactionalAllowedIps() {
        return CollectionHelper.isNotEmpty(getAllowedAddresses());
    }

    /**
     * @return true if token is defined
     */
    public boolean isSetTransactionalToken() {
        return StringHelper.isNotEmpty(getTransactionalToken());
    }

}
