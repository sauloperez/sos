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

import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import net.opengis.sos.x20.GetObservationDocument;

import org.apache.xmlbeans.XmlException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.service.AbstractServiceCommunicationObject;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class SosDecoderv20Test {

    private SosDecoderv20 decoder;

    @Before
    public void initDecoder() {
        decoder = new SosDecoderv20();
    }

    @After
    public void nullDecoder() {
        decoder = null;
    }

    @Test
    public void should_decode_boolean_swesExtensions() throws XmlException, OwsExceptionReport {
        final GetObservationDocument doc =
                GetObservationDocument.Factory
                        .parse("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                                + "<sos:GetObservation service=\"SOS\" version=\"2.0.0\"\n"
                                + "    xmlns:sos=\"http://www.opengis.net/sos/2.0\"\n"
                                + "    xmlns:swe=\"http://www.opengis.net/swe/2.0\"\n"
                                + "    xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n"
                                + "    xmlns:swes=\"http://www.opengis.net/swes/2.0\"\n"
                                + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/sos/2.0 http://schemas.opengis.net/sos/2.0/sos.xsd\">\n"
                                + "    <swes:extension>\n"
                                + "        <swe:Boolean definition=\"MergeObservationsIntoDataArray\">\n"
                                + "            <swe:value>true</swe:value>\n" + "        </swe:Boolean>\n"
                                + "    </swes:extension>\n" + "</sos:GetObservation>");

        final AbstractServiceCommunicationObject decodedObject = decoder.decode(doc);

        assertThat(decodedObject, instanceOf(GetObservationRequest.class));

        final GetObservationRequest request = (GetObservationRequest) decodedObject;
        assertThat(request.isSetExtensions(), is(TRUE));
        assertThat(request.getExtensions().isBooleanExtensionSet("MergeObservationsIntoDataArray"), is(TRUE));
    }

    @Test
    public void should_decode_text_swesExtensions() throws XmlException, OwsExceptionReport {
        final GetObservationDocument doc =
                GetObservationDocument.Factory
                        .parse("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                                + "<sos:GetObservation service=\"SOS\" version=\"2.0.0\"\n"
                                + "    xmlns:sos=\"http://www.opengis.net/sos/2.0\"\n"
                                + "    xmlns:swe=\"http://www.opengis.net/swe/2.0\"\n"
                                + "    xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n"
                                + "    xmlns:swes=\"http://www.opengis.net/swes/2.0\"\n"
                                + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/sos/2.0 http://schemas.opengis.net/sos/2.0/sos.xsd\">\n"
                                + "    <swes:extension>\n" + "        <swe:Text definition=\"my-text-extension\">\n"
                                + "            <swe:value>true</swe:value>\n" + "        </swe:Text>\n"
                                + "    </swes:extension>\n" + "</sos:GetObservation>");

        final AbstractServiceCommunicationObject decodedObject = decoder.decode(doc);

        assertThat(decodedObject, instanceOf(GetObservationRequest.class));

        final GetObservationRequest request = (GetObservationRequest) decodedObject;
        assertThat(request.isSetExtensions(), is(TRUE));

    }

}
