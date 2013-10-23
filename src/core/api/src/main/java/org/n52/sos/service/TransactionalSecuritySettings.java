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

import static org.n52.sos.config.SettingDefinitionProvider.ORDER_2;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionGroup;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.BooleanSettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;

import com.google.common.collect.Sets;

/**
 * @author Shane StClair <shane@axiomalaska.com>
 * 
 * @since 4.0.0
 */
public class TransactionalSecuritySettings implements SettingDefinitionProvider {

    public static final String TRANSACTIONAL_ACTIVE = "service.security.transactional.active";

    public static final String TRANSACTIONAL_ALLOWED_IPS = "service.transactionalAllowedIps";

    public static final String TRANSACTIONAL_TOKEN = "service.transactionalToken";

    public static final String ALLOWED_PROXIES = "service.transactionalAllowedProxies";

    public static final SettingDefinitionGroup TX_SEC_GROUP =
            new SettingDefinitionGroup()
                    .setTitle("Transactional Security")
                    .setDescription(
                            "Simple security settings to restrict access to transactional methods (InsertSensor, InsertObservation, etc.)."
                                    + " Users requiring more control over security should use "
                                    + "<a href=\"http://52north.org/communities/security/wss/2.2/\">http://52north.org/communities/security/wss/2.2/</a>.")
                    .setOrder(1);

    public static final BooleanSettingDefinition TRANSACTIONAL_SECURITY_ACTIVE_DEFINITION =
            new BooleanSettingDefinition()
                    .setGroup(TX_SEC_GROUP)
                    .setOrder(ORDER_0)
                    .setKey(TRANSACTIONAL_ACTIVE)
                    .setTitle("Transactional security active")
                    .setOptional(false)
                    .setDescription(
                            "Activate/Deactivate transactional security support. If true, allowed IPs or token should be defined! If IPs and token is defined, then they are checked with AND link!")
                    .setDefaultValue(false);

    public static final StringSettingDefinition TRANSACTIONAL_ALLOWED_IPS_DEFINITION = new StringSettingDefinition()
            .setGroup(TX_SEC_GROUP)
            .setOrder(ORDER_1)
            .setKey(TRANSACTIONAL_ALLOWED_IPS)
            .setTitle("Transactional Allowed IPs")
            .setOptional(true)
            .setDefaultValue("")
            .setDescription(
                    "Comma separated ranges of IPs that should be allowed to make transactional requests. " +
                    "Use CIDR notation or raw IP addresses (e.g. <code>127.0.0.1,192.168.0.0/16</code>). " +
                    "Subnet notation is also supported (e.g. <code>192.168.0.0/255.255.0.0</code>). Leading zeros are not allowed.");

    public static final StringSettingDefinition ALLOWED_PROXY_DEFINITITION =
            new StringSettingDefinition()
                    .setGroup(TX_SEC_GROUP)
                    .setOrder(ORDER_2)
                    .setKey(ALLOWED_PROXIES)
                    .setTitle("Allowed Proxy IPs")
                    .setOptional(true)
                    .setDefaultValue("127.0.0.1")
                    .setDescription("Comma seperated list of allowed proxy IP addresses. These will be used to authorize allowed transactional IP addresses behind proxy servers.");

    public static final StringSettingDefinition TRANSACTIONAL_TOKEN_DEFINITION =
            new StringSettingDefinition()
                    .setGroup(TX_SEC_GROUP)
                    .setOrder(ORDER_3)
                    .setKey(TRANSACTIONAL_TOKEN)
                    .setTitle("Transactional authorization token")
                    .setOptional(true)
                    .setDefaultValue("")
                    .setDescription(
                            "Authorization token to require for transactional requests. Specified in the HTTP Authorization header (Authorization: {token}).");

    private static final Set<SettingDefinition<?, ?>> DEFINITIONS = Sets.<SettingDefinition<?, ?>> newHashSet(
            TRANSACTIONAL_SECURITY_ACTIVE_DEFINITION,
            TRANSACTIONAL_ALLOWED_IPS_DEFINITION,
            TRANSACTIONAL_TOKEN_DEFINITION,
            ALLOWED_PROXY_DEFINITITION);

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return Collections.unmodifiableSet(DEFINITIONS);
    }
}
