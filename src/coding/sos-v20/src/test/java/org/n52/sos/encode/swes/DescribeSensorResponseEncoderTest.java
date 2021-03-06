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
package org.n52.sos.encode.swes;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.OperationEncoderKey;
import org.n52.sos.encode.XmlEncoderKey;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.swes.SwesConstants;
import org.n52.sos.response.DescribeSensorResponse;
import org.n52.sos.util.http.MediaTypes;
import org.n52.sos.w3c.SchemaLocation;

import com.google.common.collect.Maps;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class DescribeSensorResponseEncoderTest {
    @BeforeClass
    public static void initDecoders() {
        CodingRepository.getInstance();
    }

    @AfterClass
    public static void cleanUp() {
        SettingsManager.getInstance().cleanup();
    }

    @Test
    public void should_return_correct_encoder_keys() {
        Set<EncoderKey> returnedKeySet = new DescribeSensorResponseEncoder().getEncoderKeyType();
        assertThat(returnedKeySet.size(), is(3));
        assertThat(returnedKeySet, hasItem(new XmlEncoderKey(SwesConstants.NS_SWES_20, DescribeSensorResponse.class)));
        assertThat(returnedKeySet, hasItem(new OperationEncoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                SosConstants.Operations.DescribeSensor, MediaTypes.TEXT_XML)));
        assertThat(returnedKeySet, hasItem(new OperationEncoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                SosConstants.Operations.DescribeSensor, MediaTypes.APPLICATION_XML)));
    }

    @Test
    public void should_return_emptyMap_for_supportedTypes() {
        assertThat(new DescribeSensorResponseEncoder().getSupportedTypes(), is(not(nullValue())));
        assertThat(new DescribeSensorResponseEncoder().getSupportedTypes().isEmpty(), is(TRUE));
    }

    @Test
    public void should_return_emptySet_for_conformanceClasses() {
        assertThat(new DescribeSensorResponseEncoder().getConformanceClasses(), is(not(nullValue())));
        assertThat(new DescribeSensorResponseEncoder().getConformanceClasses().isEmpty(), is(TRUE));
    }

    @Test
    public void should_add_own_prefix_to_prefixMap() {
        Map<String, String> prefixMap = Maps.newHashMap();
        new DescribeSensorResponseEncoder().addNamespacePrefixToMap(prefixMap);
        assertThat(prefixMap.isEmpty(), is(FALSE));
        assertThat(prefixMap.containsKey(SwesConstants.NS_SWES_20), is(TRUE));
        assertThat(prefixMap.containsValue(SwesConstants.NS_SWES_PREFIX), is(TRUE));
    }

    @Test
    public void should_not_fail_if_prefixMap_is_null() {
        new DescribeSensorResponseEncoder().addNamespacePrefixToMap(null);
    }

    @Test
    public void should_return_contentType_xml() {
        assertThat(new DescribeSensorResponseEncoder().getContentType(), is(MediaTypes.TEXT_XML));
    }

    @Test
    public void should_return_correct_schema_location() {
        assertThat(new DescribeSensorResponseEncoder().getSchemaLocations().size(), is(1));
        SchemaLocation schemLoc = new DescribeSensorResponseEncoder().getSchemaLocations().iterator().next();
        assertThat(schemLoc.getNamespace(), is("http://www.opengis.net/swes/2.0"));
        assertThat(schemLoc.getSchemaFileUrl(), is("http://schemas.opengis.net/swes/2.0/swes.xsd"));
    }

    @Test(expected = UnsupportedEncoderInputException.class)
    public void should_return_exception_if_received_null() throws OwsExceptionReport {
        new DescribeSensorResponseEncoder().encode(null);
        new DescribeSensorResponseEncoder().encode(null, null);
        new DescribeSensorResponseEncoder().encode(null, new HashMap<SosConstants.HelperValues, String>());
    }
}
