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
package org.n52.sos.encode.json.impl;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.n52.sos.coding.json.JSONConstants.EXCEPTIONS;
import static org.n52.sos.coding.json.JSONConstants.LOCATOR;
import static org.n52.sos.coding.json.JSONConstants.TEXT;
import static org.n52.sos.coding.json.JSONConstants.VERSION;
import static org.n52.sos.coding.json.matchers.Does.does;
import static org.n52.sos.coding.json.matchers.JSONMatchers.arrayOfLength;
import static org.n52.sos.coding.json.matchers.JSONMatchers.equalTo;
import static org.n52.sos.coding.json.matchers.JSONMatchers.exist;
import static org.n52.sos.coding.json.matchers.JSONMatchers.isObject;
import static org.n52.sos.coding.json.matchers.ValidationMatchers.instanceOf;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.encode.json.JSONEncodingException;
import org.n52.sos.exception.ows.concrete.EncoderResponseUnsupportedException;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class OwsExceptionReportEncoderTest {
    private OwsExceptionReportEncoder enc;

    @Rule
    public final ErrorCollector e = new ErrorCollector();

    @Before
    public void setUp() {
        enc = new OwsExceptionReportEncoder();
    }

    @Test
    public void testExceptionWithoutCause() throws JSONEncodingException {
        final EncoderResponseUnsupportedException owse = new EncoderResponseUnsupportedException();
        owse.setVersion("2.0.0");
        final JsonNode json = enc.encodeJSON(owse);
        assertThat(json, is(notNullValue()));
        final String message = "The encoder response is not supported!";
        e.checkThat(json, is(instanceOf(SchemaConstants.Common.EXCEPTION_REPORT)));
        e.checkThat(json.path(VERSION), is(equalTo("2.0.0")));
        e.checkThat(json.path(EXCEPTIONS), is(arrayOfLength(1)));
        e.checkThat(json.path(EXCEPTIONS).path(0), isObject());
        e.checkThat(json.path(EXCEPTIONS).path(0).path(LOCATOR), does(not(exist())));
        e.checkThat(json.path(EXCEPTIONS).path(0).path(TEXT), is(equalTo(message)));
    }
}
