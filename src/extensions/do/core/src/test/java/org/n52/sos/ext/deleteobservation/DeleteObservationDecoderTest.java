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
package org.n52.sos.ext.deleteobservation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.n52.sos.ext.deleteobservation.DeleteObservationConstants.CONFORMANCE_CLASSES;
import static org.n52.sos.ext.deleteobservation.DeleteObservationConstants.NS_SOSDO_1_0;
import static org.n52.sos.ogc.sos.SosConstants.SOS;
import static org.n52.sos.util.CodingHelper.decoderKeysForElements;
import static org.n52.sos.util.CodingHelper.xmlDecoderKeysForOperation;
import static org.n52.sos.util.CollectionHelper.union;

import java.util.Set;

import net.opengis.sosdo.x10.DeleteObservationDocument;

import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.0.0
 */
public class DeleteObservationDecoderTest {

    private static DeleteObservationDecoder instance;

    private static Set<DecoderKey> dkt;

    private static XmlObject incorrectXmlObject;

    /*
     * <?xml version="1.0" encoding="UTF-8"?> <sosdo:DeleteObservation
     * version="2.0" service="SOS"
     * xmlns:sosdo="http://www.opengis.net/sosdo/1.0">
     * <sosdo:observation>test-observation-identifier</sosdo:observation>
     * </sosdo:DeleteObservation>
     */
    private static DeleteObservationDocument correctXmlObject;

    private static String observationId = "test_obs_id";

    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void initFixtures() {
        incorrectXmlObject = XmlObject.Factory.newInstance();
        dkt =
                union(decoderKeysForElements(NS_SOSDO_1_0, DeleteObservationDocument.class),
                        xmlDecoderKeysForOperation(SOS, Sos2Constants.SERVICEVERSION,
                                DeleteObservationConstants.Operations.DeleteObservation));
    }

    @Before
    public void initInstance() {
        instance = new DeleteObservationDecoder();
        correctXmlObject = DeleteObservationDocument.Factory.newInstance();
        correctXmlObject.addNewDeleteObservation().setObservation(observationId);
    }

    @Test
    public void constructorReturnsInstance() {
        String className = DeleteObservationDecoder.class.getName();
        assertNotNull("Instance is null. Constructor failed", instance);
        assertTrue("Instance of constructed object is not of class" + className,
                instance.getClass().getName().equals(className));
    }

    @Test
    public void testGetDecoderKeyTypes() {
        assertNotNull("DecoderKey is null", instance.getDecoderKeyTypes());
        assertTrue("DecoderKey does NOT equal " + dkt, instance.getDecoderKeyTypes().equals(dkt));
    }

    @Test
    public void testGetConformanceClasses() {
        assertNotNull("ConformanceClasses is null", instance.getConformanceClasses());
        assertTrue("ConformanceClasses contains " + CONFORMANCE_CLASSES,
                instance.getConformanceClasses().equals(CONFORMANCE_CLASSES));
    }

    @Test
    public void getSupportedTypesReturnsEmptyList() {
        assertNotNull("Supported Types is null", instance.getSupportedTypes());
        assertEquals("Supported Types size ", 0, instance.getSupportedTypes().size());
    }

    @Test(expected = OwsExceptionReport.class)
    public void decodeNullThrowsOwsExceptionReport() throws OwsExceptionReport {
        instance.decode(null);
    }

    @Test(expected = OwsExceptionReport.class)
    public void decodingIncorrectXmlObjectThrowsOwsExceptionReport() throws OwsExceptionReport {
        instance.decode(incorrectXmlObject);
    }

    @Test
    public void decodingCorrectXmlObjectReturnsCorrectServiceRequest() throws OwsExceptionReport {
        String className = DeleteObservationRequest.class.getName();
        assertNotNull("Decoding of correct XmlObject returned null", instance.decode(correctXmlObject));
        assertEquals("Class of Result ", className, instance.decode(correctXmlObject).getClass().getName());
        assertEquals("Id of observation to delete", observationId, instance.decode(correctXmlObject)
                .getObservationIdentifier());
    }

    @Test(expected = OwsExceptionReport.class)
    public void should_throw_OwsExceptionReport_when_receving_invalid_DeleteObservationDocument()
            throws OwsExceptionReport {
        correctXmlObject = DeleteObservationDocument.Factory.newInstance();
        instance.decode(correctXmlObject);
    }

}
