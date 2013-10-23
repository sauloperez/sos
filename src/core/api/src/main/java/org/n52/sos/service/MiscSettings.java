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

import static java.lang.Boolean.FALSE;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionGroup;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.BooleanSettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;

import com.google.common.collect.ImmutableSet;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class MiscSettings implements SettingDefinitionProvider {
    public static final String TOKEN_SEPARATOR = "misc.tokenSeparator";

    public static final String TUPLE_SEPARATOR = "misc.tupleSeparator";

    public static final String CHARACTER_ENCODING = "misc.characterEncoding";

    public static final String GML_DATE_FORMAT = "misc.gmlDateFormat";

    public static final String SRS_NAME_PREFIX_SOS_V1 = "misc.srsNamePrefixSosV1";

    public static final String SRS_NAME_PREFIX_SOS_V2 = "misc.srsNamePrefixSosV2";

    public static final String DEFAULT_OFFERING_PREFIX = "misc.defaultOfferingPrefix";

    public static final String DEFAULT_PROCEDURE_PREFIX = "misc.defaultProcedurePrefix";

    public static final String DEFAULT_OBSERVABLEPROPERTY_PREFIX = "misc.defaultObservablePropertyPrefix";

    public static final String DEFAULT_FEATURE_PREFIX = "misc.defaultFeaturePrefix";

    public static final String HTTP_STATUS_CODE_USE_IN_KVP_POX_BINDING = "misc.httpResponseCodeUseInKvpAndPoxBinding";

    public static final String RELATED_SAMPLING_FEATURE_ROLE_FOR_CHILD_FEATURES =
            "misc.relatedSamplingFeatureRoleForChildFeatures";

    public static final SettingDefinitionGroup GROUP = new SettingDefinitionGroup().setTitle("Miscellaneous")
            .setOrder(ORDER_3);

    public static final StringSettingDefinition TOKEN_SEPERATOR_DEFINITION = new StringSettingDefinition()
            .setGroup(GROUP).setOrder(ORDER_0).setKey(TOKEN_SEPARATOR).setDefaultValue(",")
            .setTitle("Token separator").setDescription("Token separator in result element (a character)");

    public static final StringSettingDefinition TUPLE_SEPERATOR_DEFINITION = new StringSettingDefinition()
            .setGroup(GROUP).setOrder(ORDER_1).setKey(TUPLE_SEPARATOR).setDefaultValue(";")
            .setTitle("Tuple separator").setDescription("Tuple separator in result element (a character)");

    public static final StringSettingDefinition GML_DATE_FORMAT_DEFINITION = new StringSettingDefinition()
            .setGroup(GROUP).setOrder(ORDER_2).setKey(GML_DATE_FORMAT).setOptional(true)
            .setTitle("Date format of GML").setDescription("Date format of Geography Markup Language");

    public static final StringSettingDefinition SRS_NAME_PREFIX_SOS_V1_DEFINITION = new StringSettingDefinition()
            .setGroup(GROUP).setOrder(ORDER_3).setKey(SRS_NAME_PREFIX_SOS_V1)
            .setDefaultValue("urn:ogc:def:crs:EPSG::").setTitle("SOSv1 SRS Prefix")
            .setDescription("Prefix for the SRS name in SOS v1.0.0.");

    public static final StringSettingDefinition SRS_NAME_PREFIX_SOS_V2_DEFINITION = new StringSettingDefinition()
            .setGroup(GROUP).setOrder(ORDER_4).setKey(SRS_NAME_PREFIX_SOS_V2)
            .setDefaultValue("http://www.opengis.net/def/crs/EPSG/0/").setTitle("SOSv2 SRS Prefix")
            .setDescription("Prefix for the SRS name in SOS v2.0.0.");

    public static final StringSettingDefinition CHARACTER_ENCODING_DEFINITION = new StringSettingDefinition()
            .setGroup(GROUP).setOrder(ORDER_5).setKey(CHARACTER_ENCODING).setDefaultValue("UTF-8")
            .setTitle("Character Encoding").setDescription("The character encoding used for responses.");

    public static final StringSettingDefinition DEFAULT_OFFERING_PREFIX_DEFINITION =
            new StringSettingDefinition()
                    .setGroup(MiscSettings.GROUP)
                    .setOrder(ORDER_6)
                    .setKey(DEFAULT_OFFERING_PREFIX)
                    .setDefaultValue("http://www.example.org/offering/")
                    .setTitle("Default Offering Prefix")
                    .setDescription(
                            "The default prefix for offerings (generated if not defined in Register-/InsertSensor requests or values from custom db).");

    public static final StringSettingDefinition DEFAULT_PROCEDURE_PREFIX_DEFINITION =
            new StringSettingDefinition()
                    .setGroup(MiscSettings.GROUP)
                    .setOrder(ORDER_7)
                    .setKey(DEFAULT_PROCEDURE_PREFIX)
                    .setDefaultValue("http://www.example.org/procedure/")
                    .setTitle("Default Procedure Prefix")
                    .setDescription(
                            "The default prefix for procedures (generated if not defined in Register-/InsertSensor requests or values from custom db).");

    public static final StringSettingDefinition DEFAULT_OBSERVABLEPROPERTY_PREFIX_DEFINITION =
            new StringSettingDefinition().setGroup(MiscSettings.GROUP).setOrder(ORDER_8)
                    .setKey(DEFAULT_OBSERVABLEPROPERTY_PREFIX)
                    .setDefaultValue("http://www.example.org/observableProperty/")
                    .setTitle("Default ObservableProperty Prefix")
                    .setDescription("The default prefix for observableProperty (values from custom db).");

    public static final StringSettingDefinition DEFAULT_FEATURE_PREFIX_DEFINITION = new StringSettingDefinition()
            .setGroup(MiscSettings.GROUP).setOrder(ORDER_9).setKey(DEFAULT_FEATURE_PREFIX)
            .setDefaultValue("http://www.example.org/feature/").setTitle("Default Feature Prefix")
            .setDescription("The default prefix for features (values from custom db).");

    public static final BooleanSettingDefinition HTTP_STATUS_CODE_USE_IN_KVP_POX_BINDING_DEFINITION =
            new BooleanSettingDefinition()
                    .setGroup(GROUP)
                    .setOrder(ORDER_12)
                    .setKey(HTTP_STATUS_CODE_USE_IN_KVP_POX_BINDING)
                    .setDefaultValue(FALSE)
                    .setTitle("Use HTTP Status Codes in KVP and POX Binding?")
                    .setDescription(
                            "Should the response returned by KVP and POX binding use the exception specific HTTP status code or always <tt>HTTP 200 - OK</tt>.");

    public static final StringSettingDefinition RELATED_SAMPLING_FEATURE_ROLE_FOR_CHILD_FEATURES_DEFINITION =
            new StringSettingDefinition()
                    .setGroup(GROUP)
                    .setKey(RELATED_SAMPLING_FEATURE_ROLE_FOR_CHILD_FEATURES)
                    .setDefaultValue("isSampledAt")
                    .setTitle("Role for childs of related features")
                    .setDescription(
                            "The value for the role of an child feature. It is used when a related feature is already sampled at the child feature.")
                    .setOrder(ORDER_13);

    private static final Set<SettingDefinition<?, ?>> DEFINITIONS = ImmutableSet.<SettingDefinition<?, ?>> of(
            TOKEN_SEPERATOR_DEFINITION, TUPLE_SEPERATOR_DEFINITION, GML_DATE_FORMAT_DEFINITION,
            SRS_NAME_PREFIX_SOS_V1_DEFINITION, SRS_NAME_PREFIX_SOS_V2_DEFINITION, DEFAULT_OFFERING_PREFIX_DEFINITION,
            DEFAULT_PROCEDURE_PREFIX_DEFINITION, DEFAULT_OBSERVABLEPROPERTY_PREFIX_DEFINITION,
            DEFAULT_FEATURE_PREFIX_DEFINITION, CHARACTER_ENCODING_DEFINITION,
            HTTP_STATUS_CODE_USE_IN_KVP_POX_BINDING_DEFINITION/*
                                                               * ,
                                                               * RELATED_SAMPLING_FEATURE_ROLE_FOR_CHILD_FEATURES_DEFINITION
                                                               */);

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return Collections.unmodifiableSet(DEFINITIONS);
    }
}
