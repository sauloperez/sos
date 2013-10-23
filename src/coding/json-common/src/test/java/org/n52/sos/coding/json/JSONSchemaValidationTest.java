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
package org.n52.sos.coding.json;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.n52.sos.coding.json.matchers.ValidationMatchers.validSchema;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.util.JsonLoader;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
@RunWith(Parameterized.class)
public class JSONSchemaValidationTest {
    private static final String[] SCHEMATA = { "BaseObservation", "CategoryObservation", "CodeType",
            "ComplexObservation", "CountObservation", "Envelope", "ExceptionReport", "FeatureOfInterest", "Field",
            "FieldWithValue", "GenericObservation", "Geometry", "GeometryObservation", "Measurement", "Observation",
            "ObservationWithResult", "SWEArrayObservation", "SpatialFilter", "TemplateObservation", "TemporalFilter",
            "TextObservation", "TimeInstant", "TimePeriod", "TimePrimitive", "TruthObservation", "sos/request/Batch",
            "sos/request/GetObservation", "sos/request/GetObservationById", "sos/request/GetFeatureOfInterest",
            "sos/request/InsertObservation", "sos/request/InsertResultTemplate", "sos/request/InsertSensor",
            "sos/request/GetCapabilities", "sos/request/DeleteSensor", "sos/request/DescribeSensor",
            "sos/request/UpdateSensorDescription", "sos/request/InsertResult", "sos/request/GetResult",
            "sos/request/GetResultTemplate", "sos/request/Request", "sos/response/Response", "sos/response/Batch",
            "sos/response/InsertSensor", "sos/response/GetObservation", "sos/response/GetObservationById",
            "sos/response/InsertObservation", "sos/response/GetFeatureOfInterest", "sos/response/InsertResult",
            "sos/response/GetResult", "sos/response/GetResultTemplate", "sos/response/UpdateSensorDescription",
            "sos/response/DeleteSensor", "sos/response/DescribeSensor", "sos/response/GetCapabilities" };

    private String name;

    private JsonNode schema;

    public JSONSchemaValidationTest(String name) {
        this.name = name;
    }

    @Before
    public void setUp() throws IOException {
        schema = JsonLoader.fromResource("/schema/" + name + ".json");
    }

    @Test
    public void isValidSchema() throws IOException {
        assertThat(name + " is not valid", schema, is(validSchema()));
    }

    @Parameters(name = "{0}")
    public static List<String[]> schemata() {
        String[][] params = new String[SCHEMATA.length][];
        for (int i = 0; i < params.length; ++i) {
            params[i] = new String[] { SCHEMATA[i] };
        }
        return Arrays.asList(params);
    }
}