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

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.n52.sos.ConfiguredSettingsManager;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.encoding.SweTextEncoding;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweTime;
import org.n52.sos.ogc.swe.simpleType.SweTimeRange;
import org.n52.sos.request.InsertResultTemplateRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.util.JsonLoader;

/**
 * @since 4.0.0
 * 
 */
public class InsertResultTemplateRequestDecoderTest {
    @ClassRule
    public static final ConfiguredSettingsManager csm = new ConfiguredSettingsManager();

    private InsertResultTemplateRequestDecoder decoder;

    @Rule
    public final ErrorCollector errors = new ErrorCollector();

    @Before
    public void before() {
        this.decoder = new InsertResultTemplateRequestDecoder();
    }

    @Test
    public void resultEncoding() throws IOException, OwsExceptionReport {
        InsertResultTemplateRequest req = load();
        assertThat(req.getResultEncoding(), is(notNullValue()));
        assertThat(req.getResultEncoding().getEncoding(), is(instanceOf(SweTextEncoding.class)));
        SweTextEncoding encoding = (SweTextEncoding) req.getResultEncoding().getEncoding();
        errors.checkThat(encoding.getTokenSeparator(), is(","));
        errors.checkThat(encoding.getBlockSeparator(), is("#"));
    }

    @Test
    public void resultStructure() throws IOException, OwsExceptionReport {
        InsertResultTemplateRequest req = load();
        assertThat(req.getResultStructure(), is(notNullValue()));
        assertThat(req.getResultStructure().getResultStructure(), is(instanceOf(SweDataRecord.class)));
        SweDataRecord structure = (SweDataRecord) req.getResultStructure().getResultStructure();
        assertThat(structure.getFields(), is(notNullValue()));
        assertThat(structure.getFields(), hasSize(3));

        SweField field1 = structure.getFields().get(0);
        assertThat(field1, is(notNullValue()));
        errors.checkThat(field1.getName(), is("phenomenonTime"));
        assertThat(field1.getElement(), is(instanceOf(SweTimeRange.class)));
        SweTimeRange phenomenonTime = (SweTimeRange) field1.getElement();
        errors.checkThat(phenomenonTime.getDefinition(),
                is("http://www.opengis.net/def/property/OGC/0/PhenomenonTime"));
        errors.checkThat(phenomenonTime.getUom(), is("http://www.opengis.net/def/uom/ISO-8601/0/Gregorian"));

        SweField field2 = structure.getFields().get(1);
        assertThat(field2, is(notNullValue()));
        errors.checkThat(field2.getName(), is("resultTime"));
        assertThat(field2.getElement(), is(instanceOf(SweTime.class)));
        SweTime resultTime = (SweTime) field2.getElement();
        errors.checkThat(resultTime.getDefinition(), is("http://www.opengis.net/def/property/OGC/0/ResultTime"));
        errors.checkThat(resultTime.getUom(), is("testunit1"));

        SweField field3 = structure.getFields().get(2);
        assertThat(field3, is(notNullValue()));
        errors.checkThat(field3.getName(), is("observable_property_6"));
        assertThat(field3.getElement(), is(instanceOf(SweQuantity.class)));
        SweQuantity quantity = (SweQuantity) field3.getElement();
        errors.checkThat(quantity.getDefinition(), is("http://www.52north.org/test/observableProperty/6"));
        errors.checkThat(quantity.getUom(), is("test_unit_6"));
    }

    @Test
    public void observationTemplate() throws IOException, OwsExceptionReport {
        InsertResultTemplateRequest req = load();
        OmObservationConstellation oc = req.getObservationTemplate();
        assertThat(oc, is(notNullValue()));
        assertThat(oc.getObservationType(), is("http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement"));
        assertThat(oc.getProcedure(), is(notNullValue()));
        assertThat(oc.getProcedure().getIdentifier(), is("http://52north.org/example/procedure/6"));
        assertThat(oc.getObservableProperty(), is(notNullValue()));
        assertThat(oc.getObservableProperty().getIdentifier(), is("http://52north.org/example/observedProperty/6"));
        assertThat(oc.getFeatureOfInterest(), is(notNullValue()));
        assertThat(oc.getFeatureOfInterest().getIdentifier(), is(notNullValue()));
        assertThat(oc.getFeatureOfInterest().getIdentifier().getCodeSpace(),
                is("http://www.opengis.net/def/nil/OGC/0/unknown"));
        assertThat(oc.getFeatureOfInterest().getIdentifier().getValue(), is("http://52north.org/example/feature/6"));
    }

    @Test
    public void offering() throws IOException, OwsExceptionReport {
        InsertResultTemplateRequest req = load();
        assertThat(req.getObservationTemplate(), is(notNullValue()));
        assertThat(req.getObservationTemplate().getOfferings(), hasSize(1));
        assertThat(req.getObservationTemplate().getOfferings(), hasItem("offering6"));
    }

    @Test
    public void identifier() throws IOException, OwsExceptionReport {
        assertThat(load().getIdentifier(), is("http://www.52north.org/test/procedure/6/template/1"));
    }

    protected InsertResultTemplateRequest load() throws IOException, OwsExceptionReport {
        final JsonNode json = JsonLoader.fromResource("/examples/sos/InsertResultTemplateRequest.json");
        final InsertResultTemplateRequest req = decoder.decodeJSON(json, true);
        assertThat(req, is(notNullValue()));
        return req;
    }
}
