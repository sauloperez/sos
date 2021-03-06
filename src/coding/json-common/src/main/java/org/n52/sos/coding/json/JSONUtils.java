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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.io.Closeables;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public final class JSONUtils {
    private static final JsonNodeFactory FACTORY = JsonNodeFactory.withExactBigDecimals(false);

    private static final ObjectReader READER;

    private static final ObjectWriter WRITER;

    static {
        final ObjectMapper mapper =
                new ObjectMapper().setNodeFactory(FACTORY).enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        READER = mapper.reader();
        DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
        pp.indentArraysWith(DefaultPrettyPrinter.Lf2SpacesIndenter.instance);
        WRITER = mapper.writer(pp);
    }

    private JSONUtils() {
    }

    public static ObjectReader getReader() {
        return READER;
    }

    public static ObjectWriter getWriter() {
        return WRITER;
    }

    public static JsonNodeFactory nodeFactory() {
        return FACTORY;
    }

    public static String print(final JsonNode node) {
        final StringWriter writer = new StringWriter();
        try {
            print(writer, node);
            writer.flush();
        } catch (IOException e) {
            // cannot happen
        } finally {
            Closeables.closeQuietly(writer);
        }
        return writer.toString();
    }

    public static void print(final Writer writer, final JsonNode node) throws IOException {
        getWriter().writeValue(writer, node);
    }

    public static void print(final OutputStream writer, final JsonNode node) throws IOException {
        getWriter().writeValue(writer, node);
    }

    public static JsonNode loadURL(final URL url) throws IOException {
        return getReader().readTree(url.openStream());
    }

    public static JsonNode loadPath(final String path) throws IOException {
        final JsonNode ret;
        final FileInputStream in = new FileInputStream(path);
        try {
            ret = getReader().readTree(in);
        } finally {
            in.close();
        }
        return ret;
    }

    public static JsonNode loadFile(final File file) throws IOException {
        final JsonNode ret;

        final FileInputStream in = new FileInputStream(file);
        try {
            ret = getReader().readTree(in);
        } finally {
            in.close();
        }

        return ret;
    }

    public static JsonNode loadStream(final InputStream in) throws IOException {
        return getReader().readTree(in);
    }

    public static JsonNode loadReader(final Reader reader) throws IOException {
        return getReader().readTree(reader);
    }

    public static JsonNode loadString(final String json) throws IOException {
        return loadReader(new StringReader(json));
    }
}
