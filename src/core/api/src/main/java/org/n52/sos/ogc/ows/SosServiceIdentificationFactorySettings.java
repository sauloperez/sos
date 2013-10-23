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

import java.util.Collections;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionGroup;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.FileSettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;

import com.google.common.collect.Sets;

/**
 * Setting definitions for the OWS Service Identification.
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class SosServiceIdentificationFactorySettings implements SettingDefinitionProvider {
    public static final String SERVICE_TYPE = "serviceIdentification.serviceType";

    public static final String SERVICE_TYPE_CODE_SPACE = "serviceIdentification.serviceTypeCodeSpace";

    public static final String ACCESS_CONSTRAINTS = "serviceIdentification.accessConstraints";

    public static final String FILE = "serviceIdentification.file";

    public static final String TITLE = "serviceIdentification.title";

    public static final String KEYWORDS = "serviceIdentification.keywords";

    public static final String ABSTRACT = "serviceIdentification.abstract";

    public static final String FEES = "serviceIdentification.fees";

    public static final SettingDefinitionGroup GROUP = new SettingDefinitionGroup().setTitle("Service Identification")
            .setOrder(1);

    public static final StringSettingDefinition TITLE_DEFINITION = new StringSettingDefinition().setGroup(GROUP)
            .setOrder(ORDER_0).setKey(TITLE).setTitle("Title").setDescription("SOS Service Title.")
            .setDefaultValue("52N SOS");

    public static final StringSettingDefinition KEYWORDS_DEFINITION = new StringSettingDefinition().setGroup(GROUP)
            .setOrder(ORDER_1).setKey(KEYWORDS).setTitle("Keywords")
            .setDescription("Comma separated SOS service keywords.").setOptional(true);

    public static final StringSettingDefinition ABSTRACT_DEFINITION = new StringSettingDefinition().setGroup(GROUP)
            .setOrder(ORDER_2).setKey(ABSTRACT).setTitle("SOS Abstract").setDescription("SOS service abstract.")
            .setDefaultValue("52North Sensor Observation Service - Data Access for the Sensor Web");

    public static final StringSettingDefinition ACCESS_CONSTRAINTS_DEFINITION = new StringSettingDefinition()
            .setGroup(GROUP).setOrder(ORDER_3).setKey(ACCESS_CONSTRAINTS).setTitle("Access Constraints")
            .setDescription("Service access constraints.").setDefaultValue("NONE");

    public static final StringSettingDefinition FEES_DEFINITION = new StringSettingDefinition().setGroup(GROUP)
            .setOrder(ORDER_4).setKey(FEES).setTitle("Fees").setDescription("SOS Service Fees.")
            .setDefaultValue("NONE");

    public static final StringSettingDefinition SERVICE_TYPE_DEFINITION = new StringSettingDefinition()
            .setGroup(GROUP).setOrder(ORDER_5).setKey(SERVICE_TYPE).setTitle("Service Type")
            .setDescription("SOS Service Type.").setDefaultValue("OGC:SOS");

    public static final StringSettingDefinition SERVICE_TYPE_CODE_SPACE_DEFINITION = new StringSettingDefinition()
            .setGroup(GROUP).setOrder(ORDER_6).setKey(SERVICE_TYPE_CODE_SPACE).setTitle("Service Type Code Space")
            .setDescription("SOS Service Type Code Space.").setOptional(true);

    public static final FileSettingDefinition FILE_DEFINITION = new FileSettingDefinition()
            .setGroup(GROUP)
            .setOrder(ORDER_7)
            .setKey(FILE)
            .setTitle("Service Identification File")
            .setOptional(true)
            .setDescription(
                    "The path to a file containing an ows:ServiceIdentification"
                            + " overriding the above settings. It can be either an absolute path "
                            + "(like <code>/home/user/sosconfig/identification.xml</code>) or a path "
                            + "relative to the web application directory "
                            + "(e.g. <code>WEB-INF/identification.xml</code>).");

    private static final Set<SettingDefinition<?, ?>> DEFINITIONS = Sets.<SettingDefinition<?, ?>> newHashSet(
            TITLE_DEFINITION, ABSTRACT_DEFINITION, SERVICE_TYPE_DEFINITION, SERVICE_TYPE_CODE_SPACE_DEFINITION,
            KEYWORDS_DEFINITION, FEES_DEFINITION, ACCESS_CONSTRAINTS_DEFINITION, FILE_DEFINITION);

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return Collections.unmodifiableSet(DEFINITIONS);
    }
}
