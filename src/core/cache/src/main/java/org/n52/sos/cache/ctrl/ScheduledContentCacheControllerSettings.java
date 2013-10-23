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
package org.n52.sos.cache.ctrl;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.IntegerSettingDefinition;
import org.n52.sos.service.ServiceSettings;

/**
 * Settings for the {@link AbstractSchedulingContentCacheController}.
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class ScheduledContentCacheControllerSettings implements SettingDefinitionProvider {
    public static final String CAPABILITIES_CACHE_UPDATE_INTERVAL = "service.capabilitiesCacheUpdateInterval";

    public static final IntegerSettingDefinition CACHE_UPDATE_INTERVAL_DEFINITION = new IntegerSettingDefinition()
            .setGroup(ServiceSettings.GROUP)
            .setOrder(6)
            .setKey(CAPABILITIES_CACHE_UPDATE_INTERVAL)
            .setDefaultValue(120)
            .setMinimum(0)
            .setTitle("Content cache update interval")
            .setDescription(
                    "The update interval of the content cache in minutes. Set this to lower value if "
                            + "your database is externally modified frequently. Set to 0 to disable scheduled "
                            + "cache updates.");

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return Collections.<SettingDefinition<?, ?>> singleton(CACHE_UPDATE_INTERVAL_DEFINITION);
    }
}
