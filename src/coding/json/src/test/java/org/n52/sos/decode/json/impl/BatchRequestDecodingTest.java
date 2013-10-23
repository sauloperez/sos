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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.n52.sos.ConfiguredSettingsManager;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.BatchRequest;
import org.n52.sos.request.InsertObservationRequest;
import org.n52.sos.request.InsertResultTemplateRequest;
import org.n52.sos.request.InsertSensorRequest;
import org.n52.sos.util.BatchConstants;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class BatchRequestDecodingTest {
    @ClassRule
    public static final ConfiguredSettingsManager csm = new ConfiguredSettingsManager();

    private static JsonNode json;

    private BatchRequestDecoder decoder;

    private BatchRequest request;

    @Rule
    public final ErrorCollector errors = new ErrorCollector();

    @BeforeClass
    public static void beforeClass() {
        try {
            json = fromResource("/examples/sos/BatchRequest.json");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Before
    public void before() throws OwsExceptionReport {
        this.decoder = new BatchRequestDecoder();
        this.request = decoder.decodeJSON(json, true);
    }

    @Test
    public void testService() {
        assertThat(request.getService(), is(SosConstants.SOS));
    }

    @Test
    public void testVersion() {
        assertThat(request.getVersion(), is(Sos2Constants.SERVICEVERSION));
    }

    @Test
    public void testOperationName() {
        assertThat(request.getOperationName(), is(BatchConstants.OPERATION_NAME));
    }

    @Test
    public void testParsedRequests() {
        assertThat(request.getRequests(), is(notNullValue()));
        assertThat(request.getRequests(), hasSize(19));
        errors.checkThat(request.getRequests().get(0), is(instanceOf(InsertSensorRequest.class)));
        errors.checkThat(request.getRequests().get(1), is(instanceOf(InsertObservationRequest.class)));
        errors.checkThat(request.getRequests().get(2), is(instanceOf(InsertSensorRequest.class)));
        errors.checkThat(request.getRequests().get(3), is(instanceOf(InsertObservationRequest.class)));
        errors.checkThat(request.getRequests().get(4), is(instanceOf(InsertSensorRequest.class)));
        errors.checkThat(request.getRequests().get(5), is(instanceOf(InsertObservationRequest.class)));
        errors.checkThat(request.getRequests().get(6), is(instanceOf(InsertSensorRequest.class)));
        errors.checkThat(request.getRequests().get(7), is(instanceOf(InsertObservationRequest.class)));
        errors.checkThat(request.getRequests().get(8), is(instanceOf(InsertSensorRequest.class)));
        errors.checkThat(request.getRequests().get(9), is(instanceOf(InsertObservationRequest.class)));
        errors.checkThat(request.getRequests().get(10), is(instanceOf(InsertSensorRequest.class)));
        errors.checkThat(request.getRequests().get(11), is(instanceOf(InsertResultTemplateRequest.class)));
        errors.checkThat(request.getRequests().get(12), is(instanceOf(InsertObservationRequest.class)));
        errors.checkThat(request.getRequests().get(13), is(instanceOf(InsertSensorRequest.class)));
        errors.checkThat(request.getRequests().get(14), is(instanceOf(InsertObservationRequest.class)));
        errors.checkThat(request.getRequests().get(15), is(instanceOf(InsertSensorRequest.class)));
        errors.checkThat(request.getRequests().get(16), is(instanceOf(InsertObservationRequest.class)));
        errors.checkThat(request.getRequests().get(17), is(instanceOf(InsertSensorRequest.class)));
        errors.checkThat(request.getRequests().get(18), is(instanceOf(InsertObservationRequest.class)));
    }
}
