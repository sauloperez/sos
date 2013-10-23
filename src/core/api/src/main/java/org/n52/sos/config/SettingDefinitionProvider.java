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
package org.n52.sos.config;

import java.util.ServiceLoader;
import java.util.Set;

import org.n52.sos.config.annotation.Configurable;

/**
 * Interface to declare dependencies to specific settings. This class should not
 * be implemented by classes that are loaded by the Service (e.g
 * {@link org.n52.sos.binding.Binding}s), as the will be instantiated before the
 * Configurator is present. {@code SettingDefinitionProvider} will be loaded
 * with the {@link ServiceLoader} interface. The setting will be injected in the
 * classes loaded by the service, that are annotated with the
 * <code>&#064;Configurable</code> annotation.
 * <p/>
 * 
 * @see Configurable
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public interface SettingDefinitionProvider {

    float ORDER_0 = 0;

    float ORDER_1 = 1;

    float ORDER_2 = 2;

    float ORDER_3 = 3;

    float ORDER_4 = 4;

    float ORDER_5 = 5;

    float ORDER_6 = 6;

    float ORDER_7 = 7;

    float ORDER_8 = 8;

    float ORDER_9 = 9;

    float ORDER_10 = 10;

    float ORDER_11 = 11;

    float ORDER_12 = 12;

    float ORDER_13 = 13;

    float ORDER_14 = 14;

    float ORDER_15 = 15;

    float ORDER_16 = 16;

    float ORDER_17 = 17;

    float ORDER_18 = 18;

    float ORDER_19 = 19;

    /**
     * @return the declared setting definitons of this provider
     */
    Set<SettingDefinition<?, ?>> getSettingDefinitions();
}
