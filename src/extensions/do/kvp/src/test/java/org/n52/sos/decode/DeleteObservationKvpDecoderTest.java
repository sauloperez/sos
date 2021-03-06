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
package org.n52.sos.decode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.n52.sos.ext.deleteobservation.DeleteObservationConstants.PARAMETER_NAME;
import static org.n52.sos.ogc.sos.SosConstants.SOS;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sos.ext.deleteobservation.DeleteObservationConstants;
import org.n52.sos.ext.deleteobservation.DeleteObservationRequest;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.util.http.MediaTypes;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 1.0.0
 */
public class DeleteObservationKvpDecoderTest {

    private static DeleteObservationKvpDecoder instance;

    private static Set<OperationDecoderKey> correctDecoderKey;

    @BeforeClass
    public static void initGlobalFixtures() {
        correctDecoderKey =
                Collections.singleton(new OperationDecoderKey(SOS, Sos2Constants.SERVICEVERSION,
                        DeleteObservationConstants.Operations.DeleteObservation, MediaTypes.APPLICATION_KVP));
    }

    final private String OPERATION_NAME = DeleteObservationConstants.Operations.DeleteObservation.name();

    @Before
    public void initInstance() {
        instance = new DeleteObservationKvpDecoder();
    }

    @Test
    public void should_return_correct_set_of_decoder_keys() {
        assertTrue(instance.getDecoderKeyTypes().equals(correctDecoderKey));
    }

    @Test(expected = OwsExceptionReport.class)
    public void should_throw_OwsExceptionReport_in_case_of_missing_parameters() throws OwsExceptionReport {
        instance.decode(new HashMap<String, String>(0));
    }

    @Test(expected = OwsExceptionReport.class)
    public void should_throw_OwsExceptionReport_in_case_of_null_parameter() throws OwsExceptionReport {
        instance.decode(null);
    }

    @Test(expected = OwsExceptionReport.class)
    public void should_throw_OwsExceptionReport_in_case_of_missing_parameters2() throws OwsExceptionReport {
        HashMap<String, String> evolvingMap = new HashMap<String, String>(1);
        evolvingMap.put("service", SOS);
        instance.decode(evolvingMap);
    }

    @Test(expected = OwsExceptionReport.class)
    public void should_throw_OwsExceptionReport_in_case_of_missing_parameters3() throws OwsExceptionReport {
        HashMap<String, String> evolvingMap = new HashMap<String, String>(2);
        evolvingMap.put("service", SOS);
        evolvingMap.put("version", Sos2Constants.SERVICEVERSION);

        instance.decode(evolvingMap);
    }

    @Test(expected = OwsExceptionReport.class)
    public void should_throw_OwsExceptionReport_in_case_of_missing_parameters4() throws OwsExceptionReport {
        HashMap<String, String> evolvingMap = new HashMap<String, String>(3);
        evolvingMap.put("service", SOS);
        evolvingMap.put("version", Sos2Constants.SERVICEVERSION);
        evolvingMap.put("request", OPERATION_NAME);

        instance.decode(evolvingMap);
    }

    @Test(expected = OwsExceptionReport.class)
    public void should_throw_OwsExceptionReport_in_case_of_missing_parameters5() throws OwsExceptionReport {
        HashMap<String, String> evolvingMap = new HashMap<String, String>(2);
        evolvingMap.put("service", SOS);
        evolvingMap.put("request", OPERATION_NAME);

        instance.decode(evolvingMap);
    }

    @Test(expected = OwsExceptionReport.class)
    public void should_throw_OwsExceptionReport_in_case_of_missing_parameters6() throws OwsExceptionReport {
        HashMap<String, String> evolvingMap = new HashMap<String, String>(3);
        evolvingMap.put("service", SOS);
        evolvingMap.put("request", OPERATION_NAME);
        evolvingMap.put(PARAMETER_NAME, "something");

        instance.decode(evolvingMap);
    }

    @Test(expected = OwsExceptionReport.class)
    public void should_throw_OwsExceptionReport_in_case_of_missing_parameters7() throws OwsExceptionReport {
        HashMap<String, String> evolvingMap = new HashMap<String, String>(3);
        evolvingMap.put("version", Sos2Constants.SERVICEVERSION);
        evolvingMap.put("request", OPERATION_NAME);
        evolvingMap.put(PARAMETER_NAME, "something");

        instance.decode(evolvingMap);
    }

    @Test(expected = OwsExceptionReport.class)
    public void should_throw_OwsExceptionReport_in_case_of_missing_parameters8() throws OwsExceptionReport {
        HashMap<String, String> evolvingMap = new HashMap<String, String>(3);
        evolvingMap.put("version", Sos2Constants.SERVICEVERSION);
        evolvingMap.put("request", OPERATION_NAME);
        evolvingMap.put("request", OPERATION_NAME + "2");
        evolvingMap.put(PARAMETER_NAME, "something");

        instance.decode(evolvingMap);
    }

    @Test
    public void should_decode_valid_request() throws OwsExceptionReport {
        final String observationIdentifier = "test-observation-identifier";
        HashMap<String, String> parameters = new HashMap<String, String>(4);
        parameters.put("service", SOS);
        parameters.put("version", Sos2Constants.SERVICEVERSION);
        parameters.put("request", OPERATION_NAME);
        parameters.put(PARAMETER_NAME, observationIdentifier);

        DeleteObservationRequest decodedRequest = instance.decode(parameters);

        assertThat(decodedRequest, is(not(nullValue())));
        assertThat(decodedRequest.getVersion(), is(Sos2Constants.SERVICEVERSION));
        assertThat(decodedRequest.getService(), is(SOS));
        assertThat(decodedRequest.getOperationName(), is(OPERATION_NAME));
        assertThat(decodedRequest.getObservationIdentifier(), is(observationIdentifier));
    }

}
