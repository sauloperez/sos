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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.n52.sos.ConfiguredSettingsManager;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.InsertObservationRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.util.JsonLoader;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class InsertObservationRequestDecoderTest {
    @ClassRule
    public static final ConfiguredSettingsManager csm = new ConfiguredSettingsManager();

    private InsertObservationRequestDecoder decoder;

    @Rule
    public final ErrorCollector errors = new ErrorCollector();

    @Before
    public void before() {
        this.decoder = new InsertObservationRequestDecoder();
    }

    @Test
    public void singleObservation() throws IOException, OwsExceptionReport {
        final JsonNode json =
                JsonLoader.fromResource("/examples/sos/InsertObservationRequest-single-observation.json");
        final InsertObservationRequest req = decoder.decodeJSON(json, true);
        errors.checkThat(req.getService(), is(equalTo("SOS")));
        errors.checkThat(req.getVersion(), is(equalTo("2.0.0")));
        errors.checkThat(req.getOperationName(), is(equalTo("InsertObservation")));
        assertThat(req.getOfferings(), is(notNullValue()));
        errors.checkThat(req.getOfferings(), hasSize(1));
        assertThat(req.getOfferings().get(0), is(equalTo("offering2")));
        assertThat(req.getObservations(), is(notNullValue()));
        assertThat(req.getObservations(), hasSize(1));
        assertThat(req.getObservations().get(0), is(notNullValue()));
        assertThat(req.getObservations().get(0).getValue().getValue(), is(instanceOf(TextValue.class)));
    }

    @Test
    public void multipleObservation() throws IOException, OwsExceptionReport {
        final JsonNode json =
                JsonLoader.fromResource("/examples/sos/InsertObservationRequest-multiple-observations.json");
        final InsertObservationRequest req = decoder.decodeJSON(json, true);
        assertThat(req, is(notNullValue()));
        errors.checkThat(req.getService(), is(equalTo("SOS")));
        errors.checkThat(req.getVersion(), is(equalTo("2.0.0")));
        errors.checkThat(req.getOperationName(), is(equalTo("InsertObservation")));
        assertThat(req.getOfferings(), is(notNullValue()));
        errors.checkThat(req.getOfferings(), hasSize(2));
        assertThat(req.getOfferings().get(0), is(equalTo("offering1")));
        assertThat(req.getOfferings().get(1), is(equalTo("offering2")));
        assertThat(req.getObservations(), is(notNullValue()));
        assertThat(req.getObservations(), hasSize(2));
        assertThat(req.getObservations().get(0), is(notNullValue()));
        assertThat(req.getObservations().get(0).getValue().getValue(), is(instanceOf(TextValue.class)));
        assertThat(req.getObservations().get(1), is(notNullValue()));
        assertThat(req.getObservations().get(1).getValue().getValue(), is(instanceOf(TextValue.class)));
    }

    @Test
    public void singleOffering() throws IOException, OwsExceptionReport {
        final JsonNode json = JsonLoader.fromResource("/examples/sos/InsertObservationRequest-single-offering.json");
        final InsertObservationRequest req = decoder.decodeJSON(json, true);
        errors.checkThat(req.getService(), is(equalTo("SOS")));
        errors.checkThat(req.getVersion(), is(equalTo("2.0.0")));
        errors.checkThat(req.getOperationName(), is(equalTo("InsertObservation")));
        assertThat(req.getOfferings(), is(notNullValue()));
        errors.checkThat(req.getOfferings(), hasSize(1));
        assertThat(req.getOfferings().get(0), is(equalTo("offering2")));
        assertThat(req.getObservations(), is(notNullValue()));
        assertThat(req.getObservations(), hasSize(1));
        assertThat(req.getObservations().get(0), is(notNullValue()));
        assertThat(req.getObservations().get(0).getValue().getValue(), is(instanceOf(TextValue.class)));
    }

    @Test
    public void multipleOfferings() throws IOException, OwsExceptionReport {
        final JsonNode json =
                JsonLoader.fromResource("/examples/sos/InsertObservationRequest-multiple-offerings.json");
        final InsertObservationRequest req = decoder.decodeJSON(json, true);
        assertThat(req, is(notNullValue()));
        errors.checkThat(req.getService(), is(equalTo("SOS")));
        errors.checkThat(req.getVersion(), is(equalTo("2.0.0")));
        errors.checkThat(req.getOperationName(), is(equalTo("InsertObservation")));
        assertThat(req.getOfferings(), is(notNullValue()));
        errors.checkThat(req.getOfferings(), hasSize(2));
        assertThat(req.getOfferings().get(0), is(equalTo("offering1")));
        assertThat(req.getOfferings().get(1), is(equalTo("offering2")));
        assertThat(req.getObservations(), is(notNullValue()));
        assertThat(req.getObservations(), hasSize(1));
        assertThat(req.getObservations().get(0), is(notNullValue()));
        assertThat(req.getObservations().get(0).getValue().getValue(), is(instanceOf(TextValue.class)));
    }
}
