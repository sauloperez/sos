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
package org.n52.sos.decode.kvp.v2;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.request.GetObservationRequest;

import com.google.common.collect.Maps;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.0.0
 * 
 */
public class GetObservationKvpDecoderv20Test {

    @Test
    public void should_decode_extension_parameter_MergeObservationsIntoDataArray() throws OwsExceptionReport {
        final Map<String, String> mapTrue = Maps.newHashMap();
        mapTrue.put("MergeObservationsIntoDataArray", "true");
        mapTrue.put("service", "SOS");
        mapTrue.put("version", "2.0.0");
        mapTrue.put("request", "GetObservation");
        final GetObservationKvpDecoderv20 decoder = new GetObservationKvpDecoderv20();
        final GetObservationRequest requestTrue = decoder.decode(mapTrue);

        final Map<String, String> mapFalse = Maps.newHashMap();
        mapFalse.put("MergeObservationsIntoDataArray", "false");
        mapFalse.put("service", "SOS");
        mapFalse.put("version", "2.0.0");
        mapFalse.put("request", "GetObservation");
        final GetObservationRequest requestFalse = decoder.decode(mapFalse);

        assertThat(requestTrue.isSetExtensions(), is(TRUE));
        assertThat(
                requestTrue.getExtensions().isBooleanExtensionSet(
                        Sos2Constants.Extensions.MergeObservationsIntoDataArray.name()), is(TRUE));

        assertThat(requestFalse.isSetExtensions(), is(TRUE));
        assertThat(
                requestFalse.getExtensions().isBooleanExtensionSet(
                        Sos2Constants.Extensions.MergeObservationsIntoDataArray.name()), is(FALSE));
    }

}
