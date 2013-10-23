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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;

import org.n52.sos.decode.json.JSONDecodingException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.cfg.LoadingConfiguration;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.load.ResourceURIDownloader;
import com.github.fge.jsonschema.load.URIDownloader;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public final class JSONValidator {
    private static final Logger LOG = LoggerFactory.getLogger(JSONValidator.class);

    private static final JSONValidator INSTANCE = new JSONValidator();

    private final JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory
            .newBuilder()
            .setLoadingConfiguration(
                    LoadingConfiguration.newBuilder().addScheme("http", new ResourceRedirect()).freeze()).freeze();

    private JSONValidator() {
    }

    public static JSONValidator getInstance() {
        return INSTANCE;
    }

    public JsonSchemaFactory getJsonSchemaFactory() {
        return jsonSchemaFactory;
    }

    public ProcessingReport validate(String json, String schema) throws IOException {
        return validate(JSONUtils.loadString(json), schema);
    }

    public boolean isValid(String json, String schema) throws IOException {
        return isValid(JSONUtils.loadString(json), schema);
    }

    public ProcessingReport validate(URL url, String schema) throws IOException {
        return validate(JSONUtils.loadURL(url), schema);
    }

    public boolean isValid(URL url, String schema) throws IOException {
        return isValid(JSONUtils.loadURL(url), schema);
    }

    public ProcessingReport validate(File file, String schema) throws IOException {
        return validate(JSONUtils.loadFile(file), schema);
    }

    public boolean isValid(File file, String schema) throws IOException {
        return isValid(JSONUtils.loadFile(file), schema);
    }

    public ProcessingReport validate(InputStream is, String schema) throws IOException {
        return validate(JSONUtils.loadStream(is), schema);
    }

    public boolean isValid(InputStream is, String schema) throws IOException {
        return isValid(JSONUtils.loadStream(is), schema);
    }

    public ProcessingReport validate(Reader reader, String schema) throws IOException {
        return validate(JSONUtils.loadReader(reader), schema);
    }

    public boolean isValid(Reader reader, String schema) throws IOException {
        return isValid(JSONUtils.loadReader(reader), schema);
    }

    public ProcessingReport validate(JsonNode node, String schema) {
        JsonSchema jsonSchema;
        try {
            jsonSchema = getJsonSchemaFactory().getJsonSchema(schema);
        } catch (ProcessingException ex) {
            throw new IllegalArgumentException("Unknown schema: " + schema, ex);
        }
        return jsonSchema.validateUnchecked(node);
    }

    public boolean isValid(JsonNode node, String schema) {
        return validate(node, schema).isSuccess();
    }

    public String encode(ProcessingReport report, JsonNode instance) {
        ObjectNode objectNode = JSONUtils.nodeFactory().objectNode();
        objectNode.put(JSONConstants.INSTANCE, instance);
        ArrayNode errors = objectNode.putArray(JSONConstants.ERRORS);
        for (ProcessingMessage m : report) {
            errors.add(m.asJson());
        }
        return JSONUtils.print(objectNode);
    }

    public void validateAndThrow(JsonNode instance, String schema) throws OwsExceptionReport {
        ProcessingReport report = JSONValidator.getInstance().validate(instance, schema);
        if (!report.isSuccess()) {
            String message = encode(report, instance);
            LOG.info("Invalid JSON instance:\n{}", message);
            throw new JSONDecodingException(message);
        }
    }

    private class ResourceRedirect implements URIDownloader {
        private final URIDownloader resource = ResourceURIDownloader.getInstance();

        @Override
        public InputStream fetch(URI source) throws IOException {
            return resource.fetch(URI.create(toResource(source)));
        }

        protected String toResource(URI source) {
            return String.format("resource://%s.json", source.getPath().replace("/json", ""));
        }
    }
}
