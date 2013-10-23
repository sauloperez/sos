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
package org.n52.sos.coding.json.impl;

import static com.github.fge.jsonschema.util.JsonLoader.fromResource;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.n52.sos.coding.json.matchers.ValidationMatchers.validObservation;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.n52.sos.coding.json.JSONConstants;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class ObservationValidationTest {
    @Test
    public void testMeasurementGeometryInline() throws IOException {
        JsonNode json = fromResource("/examples/measurement-geometry-inline.json");
        Assert.assertThat(json, is(validObservation()));
    }

    @Test
    public void testMeasuremenetGeometryRef() throws IOException {
        JsonNode json = fromResource("/examples/measurement-geometry-ref.json");
        Assert.assertThat(json, is(validObservation()));
    }

    @Test
    public void testMeasurementMissingUOM() throws IOException {
        JsonNode json = fromResource("/examples/measurement-geometry-ref.json");
        ((ObjectNode) json.path(JSONConstants.RESULT)).remove(JSONConstants.UOM);
        Assert.assertThat(json, is(not(validObservation())));
    }

    @Test
    public void testMeasurementMissingValue() throws IOException {
        JsonNode json = fromResource("/examples/measurement-geometry-ref.json");
        ((ObjectNode) json.path(JSONConstants.RESULT)).remove(JSONConstants.VALUE);
        Assert.assertThat(json, is(not(validObservation())));
    }

    @Test
    public void testMeasurementMissingProcedure() throws IOException {
        JsonNode json = fromResource("/examples/measurement-geometry-ref.json");
        ((ObjectNode) json).remove(JSONConstants.PROCEDURE);
        Assert.assertThat(json, is(not(validObservation())));
    }

    @Test
    public void testMeasurementMissingObservedProperty() throws IOException {
        JsonNode json = fromResource("/examples/measurement-geometry-ref.json");
        ((ObjectNode) json).remove(JSONConstants.OBSERVED_PROPERTY);
        Assert.assertThat(json, is(not(validObservation())));
    }

    @Test
    public void testMeasurementMissingPhenomenonTime() throws IOException {
        JsonNode json = fromResource("/examples/measurement-geometry-ref.json");
        ((ObjectNode) json).remove(JSONConstants.PHENOMENON_TIME);
        Assert.assertThat(json, is(not(validObservation())));
    }

    @Test
    public void testMeasurementMissingResultTime() throws IOException {
        JsonNode json = fromResource("/examples/measurement-geometry-ref.json");
        ((ObjectNode) json).remove(JSONConstants.RESULT_TIME);
        Assert.assertThat(json, is(not(validObservation())));
    }

    @Test
    public void testMeasurementTimePeriodResultTime() throws IOException {
        JsonNode json = fromResource("/examples/measurement-geometry-ref.json");
        ArrayNode resultTime = ((ObjectNode) json).putArray(JSONConstants.RESULT_TIME);
        resultTime.add("2013-01-01T00:00:00+02:00").add("2013-01-01T01:00:00+02:00");
        Assert.assertThat(json, is(not(validObservation())));
    }

    @Test
    public void testMeasurementMissingValidTime() throws IOException {
        JsonNode json = fromResource("/examples/measurement-geometry-ref.json");
        ((ObjectNode) json).remove(JSONConstants.VALID_TIME);
        Assert.assertThat(json, is(validObservation()));
    }

    @Test
    public void testMeasurementMissingFeatureOfInterest() throws IOException {
        JsonNode json = fromResource("/examples/measurement-geometry-ref.json");
        ((ObjectNode) json).remove(JSONConstants.FEATURE_OF_INTEREST);
        Assert.assertThat(json, is(not(validObservation())));
    }

    @Test
    public void testMeasurementMissingResult() throws IOException {
        JsonNode json = fromResource("/examples/measurement-geometry-ref.json");
        ((ObjectNode) json).remove(JSONConstants.RESULT);
        Assert.assertThat(json, is(not(validObservation())));
    }

    @Test
    public void testMeasuremenetPhenomenonTimePeriod() throws IOException {
        JsonNode json = fromResource("/examples/measurement-phenomenon-time-period.json");
        Assert.assertThat(json, is(validObservation()));
    }

    @Test
    public void testTruthObservation() throws IOException {
        JsonNode json = fromResource("/examples/truth-observation.json");
        Assert.assertThat(json, is(validObservation()));
    }

    @Test
    public void testCategoryObservation() throws IOException {
        JsonNode json = fromResource("/examples/category-observation.json");
        Assert.assertThat(json, is(validObservation()));
    }

    @Test
    public void testCountObservation() throws IOException {
        JsonNode json = fromResource("/examples/count-observation.json");
        Assert.assertThat(json, is(validObservation()));
    }

    @Test
    public void testCountObservationWithFloatingPointNumber() throws IOException {
        JsonNode json = fromResource("/examples/count-observation.json");
        ((ObjectNode) json).put(JSONConstants.RESULT, Math.PI);
        Assert.assertThat(json, is(not(validObservation())));
    }

    @Test
    public void testGeometryObservation() throws IOException {
        JsonNode json = fromResource("/examples/geometry-observation.json");
        Assert.assertThat(json, is(validObservation()));
    }

    @Test
    public void testTextObservation() throws IOException {
        JsonNode json = fromResource("/examples/text-observation.json");
        Assert.assertThat(json, is(validObservation()));
    }

    @Test
    public void testComplexObservation() throws IOException {
        JsonNode json = fromResource("/examples/complex-observation.json");
        Assert.assertThat(json, is(validObservation()));
    }

    @Test
    public void testSWEArrayObservation() throws IOException {
        JsonNode json = fromResource("/examples/swearray-observation.json");
        Assert.assertThat(json, is(validObservation()));
    }
}
