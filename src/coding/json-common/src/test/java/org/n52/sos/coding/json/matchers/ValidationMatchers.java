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
package org.n52.sos.coding.json.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.coding.json.JSONValidator;
import org.n52.sos.coding.json.SchemaConstants;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.JacksonUtils;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <autermann@uni-muenster.de>
 * 
 * @since 4.0.0
 */
public class ValidationMatchers {
    @Factory
    public static Matcher<JsonNode> instanceOf(String schemaURI) {
        return new IsValidInstance(schemaURI);
    }

    @Factory
    public static Matcher<JsonNode> validObservation() {
        return new IsValidInstance(SchemaConstants.Observation.OBSERVATION);
    }

    @Factory
    public static Matcher<JsonNode> validSchema() {
        return new IsValidInstance(SchemaConstants.SCHEMA_URI);
    }

    public static class IsValidInstance extends TypeSafeDiagnosingMatcher<JsonNode> {
        private final String schemaURI;

        public IsValidInstance(String schemaURI) {
            this.schemaURI = schemaURI;
        }

        @Override
        protected boolean matchesSafely(JsonNode item, Description mismatchDescription) {
            try {
                JsonSchema jsonSchema = JSONValidator.getInstance().getJsonSchemaFactory().getJsonSchema(schemaURI);
                ProcessingReport report = jsonSchema.validate(item);
                return describeProcessingReport(report, item, mismatchDescription);
            } catch (ProcessingException ex) {
                mismatchDescription.appendText(ex.getMessage());
            } catch (JsonProcessingException ex) {
                mismatchDescription.appendText(ex.getMessage());
            }
            return false;
        }

        protected boolean describeProcessingReport(ProcessingReport report, JsonNode item,
                Description mismatchDescription) throws JsonProcessingException {
            if (!report.isSuccess()) {
                ObjectNode objectNode = JacksonUtils.nodeFactory().objectNode();
                objectNode.put(JSONConstants.INSTANCE, item);
                ArrayNode errors = objectNode.putArray(JSONConstants.ERRORS);
                for (ProcessingMessage m : report) {
                    errors.add(m.asJson());
                }
                mismatchDescription.appendText(JacksonUtils.prettyPrint(objectNode));
            }
            return report.isSuccess();
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("valid instance of ").appendText(schemaURI);
        }
    }
}
