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
package org.n52.sos.encode;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.opengis.fes.x20.BBOXType;

import org.apache.xmlbeans.XmlObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.filter.FilterConstants;
import org.n52.sos.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.http.MediaTypes;
import org.n52.sos.w3c.SchemaLocation;

import com.google.common.collect.Maps;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class FesEncoderv20Test {

    FesEncoderv20 fesEncoder = new FesEncoderv20();

    @BeforeClass
    public final static void initDecoders() {
        CodingRepository.getInstance();
    }

    @AfterClass
    public static void cleanUp() {
        SettingsManager.getInstance().cleanup();
    }

    @Test
    public final void should_return_correct_encoder_keys() {
        final Set<EncoderKey> expectedKeySet =
                CodingHelper.encoderKeysForElements(FilterConstants.NS_FES_2, TemporalFilter.class,
                        org.n52.sos.ogc.filter.FilterCapabilities.class, SpatialFilter.class);
        final Set<EncoderKey> returnedKeySet = fesEncoder.getEncoderKeyType();

        assertThat(returnedKeySet.size(), is(3));
        assertThat(returnedKeySet, is(expectedKeySet));
    }

    @Test
    public final void should_return_emptyMap_for_supportedTypes() {
        assertThat(fesEncoder.getSupportedTypes(), is(not(nullValue())));
        assertThat(fesEncoder.getSupportedTypes().isEmpty(), is(TRUE));
    }

    @Test
    public final void should_return_emptySet_for_conformanceClasses() {
        assertThat(fesEncoder.getConformanceClasses(), is(not(nullValue())));
        assertThat(fesEncoder.getConformanceClasses().isEmpty(), is(TRUE));
    }

    @Test
    public final void should_add_own_prefix_to_prefixMap() {
        final Map<String, String> prefixMap = Maps.newHashMap();
        fesEncoder.addNamespacePrefixToMap(prefixMap);
        assertThat(prefixMap.isEmpty(), is(FALSE));
        assertThat(prefixMap.containsKey(FilterConstants.NS_FES_2), is(TRUE));
        assertThat(prefixMap.containsValue(FilterConstants.NS_FES_2_PREFIX), is(TRUE));
    }

    @Test
    public final void should_not_fail_if_prefixMap_is_null() {
        fesEncoder.addNamespacePrefixToMap(null);
    }

    @Test
    public final void should_return_contentType_xml() {
        assertThat(fesEncoder.getContentType(), is(MediaTypes.TEXT_XML));
    }

    @Test
    public final void should_return_correct_schema_location() {
        assertThat(fesEncoder.getSchemaLocations().size(), is(1));
        final SchemaLocation schemLoc = fesEncoder.getSchemaLocations().iterator().next();
        assertThat(schemLoc.getNamespace(), is("http://www.opengis.net/fes/2.0"));
        assertThat(schemLoc.getSchemaFileUrl(), is("http://schemas.opengis.net/filter/2.0/filterAll.xsd"));
    }

    @Test(expected = UnsupportedEncoderInputException.class)
    public final void should_return_exception_if_received_null() throws OwsExceptionReport {
        fesEncoder.encode(null);
        fesEncoder.encode(null, null);
        fesEncoder.encode(null, new HashMap<SosConstants.HelperValues, String>());
    }

    // @Test
    // deactivated until test fails on build server.
    public final void should_return_BBoxType_for_spatialFilter() throws OwsExceptionReport {
        final SpatialFilter filter = new SpatialFilter();
        filter.setOperator(SpatialOperator.BBOX);
        filter.setGeometry(new GeometryFactory().toGeometry(new Envelope(1, 2, 3, 4)));
        filter.setValueReference("valueReference");
        final XmlObject encode = fesEncoder.encode(filter);

        assertThat(encode, is(instanceOf(BBOXType.class)));
        final BBOXType xbBBox = (BBOXType) encode;
        assertThat(xbBBox.isSetExpression(), is(TRUE));
    }

}
