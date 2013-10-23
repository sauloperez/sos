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
package org.n52.sos.util;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * @since 4.0.0
 * 
 */
public class SosHelperTest extends SosHelper {
    public static final int EPSG4326 = 4326;

    public static final int EPSG31466 = 31466;

    public static final int DEFAULT_EPSG = EPSG4326;

    public static final String FOI_ID = "test_foi";

    public static final String PROC_ID = "test_proc";

    public static final String VERSION_1 = "1.0.0";

    public static final String VERSION_2 = "2.0.0";

    public static final String SERVICE_URL = "http://localhos:8080/SOS";

    public static final String URL_PATTERN = "/kvp";

    @BeforeClass
    public static void setUp() {
        setConfiguration(new TestableConfiguration());
    }

    @Test
    public void envelopeForEpsg4326() {
        double maxY = 52.15034, maxX = 8.05847;
        double minY = 51.95104, minX = 7.61353;
        Envelope e = new Envelope(new Coordinate(minY, minX), new Coordinate(maxY, maxX));
        checkMinMax(getMinMaxFromEnvelope(e), minY, minX, maxY, maxX);
    }

    @Test
    public void envelopeForEpsg31466() {
        double maxX = 3435628, maxY = 5780049;
        double minX = 3404751, minY = 5758364;
        Envelope e = new Envelope(new Coordinate(minX, minY), new Coordinate(maxX, maxY));
        checkMinMax(getMinMaxFromEnvelope(e), minX, minY, maxX, maxY);
    }

    @Test
    public void shouldValidHttpGetGetFeatureOfInterestRequest() {
        assertThat(createFoiGetUrl(FOI_ID, VERSION_1, SERVICE_URL, URL_PATTERN), is(getFoi100Url()));
        assertThat(createFoiGetUrl(FOI_ID, VERSION_2, SERVICE_URL, URL_PATTERN), is(getFoi200Url()));
    }

    @Test
    public void shouldValidHttpGetDescribeSensorRequest() throws UnsupportedEncodingException {
        assertThat(
                getDescribeSensorUrl(VERSION_1, SERVICE_URL, PROC_ID, URL_PATTERN,
                        SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE), is(getProcDesc100Url()));
        assertThat(
                getDescribeSensorUrl(VERSION_2, SERVICE_URL, PROC_ID, URL_PATTERN,
                        SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL), is(getProcDesc200Url()));
    }

    protected void checkMinMax(MinMax<String> minmax, double minY, double minX, double maxY, double maxX) {
        assertThat(minmax, is(notNullValue()));
        assertThat(minmax.getMinimum(), is(minY + " " + minX));
        assertThat(minmax.getMaximum(), is(maxY + " " + maxX));
    }

    protected String getFoi100Url() {
        StringBuilder builder = new StringBuilder();
        builder.append(SERVICE_URL).append(URL_PATTERN);
        builder.append("?").append(OWSConstants.RequestParams.request.name()).append("=")
                .append(SosConstants.Operations.GetFeatureOfInterest.name());
        builder.append("&").append(OWSConstants.RequestParams.service.name()).append("=").append(SosConstants.SOS);
        builder.append("&").append(OWSConstants.RequestParams.version.name()).append("=").append(VERSION_1);
        builder.append("&").append(Sos1Constants.GetFeatureOfInterestParams.featureOfInterestID.name()).append("=")
                .append(FOI_ID);
        return builder.toString();
    }

    protected String getFoi200Url() {
        StringBuilder builder = new StringBuilder();
        builder.append(SERVICE_URL).append(URL_PATTERN);
        builder.append("?").append(OWSConstants.RequestParams.request.name()).append("=")
                .append(SosConstants.Operations.GetFeatureOfInterest.name());
        builder.append("&").append(OWSConstants.RequestParams.service.name()).append("=").append(SosConstants.SOS);
        builder.append("&").append(OWSConstants.RequestParams.version.name()).append("=").append(VERSION_2);
        builder.append("&").append(Sos2Constants.GetFeatureOfInterestParams.featureOfInterest.name()).append("=")
                .append(FOI_ID);
        return builder.toString();
    }

    protected String getProcDesc100Url() throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        builder.append(SERVICE_URL).append(URL_PATTERN);
        builder.append("?").append(OWSConstants.RequestParams.request.name()).append("=")
                .append(SosConstants.Operations.DescribeSensor.name());
        builder.append("&").append(OWSConstants.RequestParams.service.name()).append("=").append(SosConstants.SOS);
        builder.append("&").append(OWSConstants.RequestParams.version.name()).append("=").append(VERSION_1);
        builder.append("&").append(SosConstants.DescribeSensorParams.procedure.name()).append("=").append(PROC_ID);
        builder.append("&").append(Sos1Constants.DescribeSensorParams.outputFormat.name()).append("=")
                .append(URLEncoder.encode(SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE, "UTF-8"));
        return builder.toString();
    }

    protected String getProcDesc200Url() throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        builder.append(SERVICE_URL).append(URL_PATTERN);
        builder.append("?").append(OWSConstants.RequestParams.request.name()).append("=")
                .append(SosConstants.Operations.DescribeSensor.name());
        builder.append("&").append(OWSConstants.RequestParams.service.name()).append("=").append(SosConstants.SOS);
        builder.append("&").append(OWSConstants.RequestParams.version.name()).append("=").append(VERSION_2);
        builder.append("&").append(SosConstants.DescribeSensorParams.procedure.name()).append("=").append(PROC_ID);
        builder.append("&").append(Sos2Constants.DescribeSensorParams.procedureDescriptionFormat.name()).append("=")
                .append(URLEncoder.encode(SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL, "UTF-8"));
        return builder.toString();
    }

    private static class TestableConfiguration extends Configuration {
    }

}
