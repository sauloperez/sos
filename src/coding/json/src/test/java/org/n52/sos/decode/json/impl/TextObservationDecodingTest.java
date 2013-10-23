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
package org.n52.sos.decode.json.impl;

import static com.github.fge.jsonschema.util.JsonLoader.fromResource;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.n52.sos.util.DateTimeHelper.parseIsoString2DateTime;

import java.io.IOException;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.n52.sos.ConfiguredSettingsManager;
import org.n52.sos.exception.ows.concrete.DateTimeParseException;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class TextObservationDecodingTest {
    @ClassRule
    public static final ConfiguredSettingsManager csm = new ConfiguredSettingsManager();

    private static DateTime phenomenonTime;

    private static JsonNode json;

    private ObservationDecoder decoder;

    private OmObservation observation;

    @BeforeClass
    public static void beforeClass() throws DateTimeParseException, IOException {
        json = fromResource("/examples/text-observation.json");
        phenomenonTime = parseIsoString2DateTime("2013-01-01T00:00:00+02:00");
    }

    @Before
    public void before() throws OwsExceptionReport {
        this.decoder = new ObservationDecoder();
        this.observation = decoder.decodeJSON(json, true);
    }

    @Test
    public void testObservation() {
        assertThat(observation, is(notNullValue()));
        final String type = observation.getObservationConstellation().getObservationType();
        assertThat(type, is(equalTo(OmConstants.OBS_TYPE_TEXT_OBSERVATION)));
        final ObservationValue<?> value = observation.getValue();
        assertThat(value, is(instanceOf(SingleObservationValue.class)));
        assertThat(value.getPhenomenonTime(), is(instanceOf(TimeInstant.class)));
        TimeInstant pt = (TimeInstant) value.getPhenomenonTime();
        assertThat(pt.getValue(), is(equalTo(phenomenonTime)));
        assertThat(value.getValue(), is(instanceOf(TextValue.class)));
        TextValue v = (TextValue) value.getValue();
        assertThat(v.getValue(), is(equalTo("Some Value")));
        assertThat(v.getUnit(), is(nullValue()));
    }
}
