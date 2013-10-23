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

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.ThrowableMessageMatcher.hasMessage;
import static org.n52.sos.coding.json.matchers.JSONMatchers.equalTo;

import java.util.EnumMap;
import java.util.HashMap;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.n52.sos.encode.json.JSONEncoder;
import org.n52.sos.encode.json.JSONEncoderKey;
import org.n52.sos.encode.json.JSONEncodingException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.util.http.MediaTypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 */
public class JSONEncoderTest {
    private final JSONEncoder<String> encoder = new JSONEncoder<String>(String.class) {
        @Override
        public JsonNode encodeJSON(String t) throws JSONEncodingException {
            return JSONUtils.nodeFactory().textNode(t);
        }
    };

    private final JSONEncoder<String> throwingEncoder = new JSONEncoder<String>(String.class) {
        @Override
        public JsonNode encodeJSON(String t) throws JSONEncodingException {
            throw new JSONEncodingException("message");
        }
    };

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testEncoderKeyTypes() {
        assertThat(encoder.getEncoderKeyType(), is(notNullValue()));
        assertThat(encoder.getEncoderKeyType(), hasSize(1));
        assertThat(encoder.getEncoderKeyType(), hasItem(new JSONEncoderKey(String.class)));
    }

    @Test
    public void testSupportedTypes() {
        assertThat(encoder.getSupportedTypes(), is(notNullValue()));
        assertThat(encoder.getSupportedTypes().size(), is(0));
    }

    @Test
    public void testAddNamespacePrefixToMap() {
        HashMap<String, String> empty = Maps.newHashMap();
        encoder.addNamespacePrefixToMap(empty);
        assertThat(empty.size(), is(0));
    }

    @Test
    public void testContentType() {
        assertThat(encoder.getContentType(), is(MediaTypes.APPLICATION_JSON));
    }

    @Test
    public void testSchemaLocations() {
        assertThat(encoder.getSchemaLocations(), is(empty()));
    }

    @Test
    public void testEncode() throws OwsExceptionReport {
        assertThat(encoder.encode("test"), equalTo("test"));
    }

    @Test
    public void testEncodeWithHelperValues() throws OwsExceptionReport {
        final EnumMap<HelperValues, String> vals = Maps.newEnumMap(HelperValues.class);
        assertThat(encoder.encode("test", vals), equalTo("test"));
    }

    @Test
    public void testThrowingEncoder() throws OwsExceptionReport {
        thrown.expect(NoApplicableCodeException.class);
        thrown.expectCause(hasMessage(is("message")));
        throwingEncoder.encode("test");
    }

    @Test
    public void testConformaceClasses() throws OwsExceptionReport {
        assertThat(encoder.getConformanceClasses(), is(empty()));
    }
}
