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
package org.n52.sos.service.it;

import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.service.it.v2.soap.DeleteObservationTest;
import org.n52.sos.service.it.v2.soap.DeleteSensorTest;
import org.n52.sos.service.it.v2.soap.DescribeSensorTest;
import org.n52.sos.service.it.v2.soap.GetCapabilitiesTest;
import org.n52.sos.service.it.v2.soap.GetDataAvailabilityTest;
import org.n52.sos.service.it.v2.soap.GetFeatureOfInterestTest;
import org.n52.sos.service.it.v2.soap.GetObservationByIdTest;
import org.n52.sos.service.it.v2.soap.GetObservationTest;
import org.n52.sos.service.it.v2.soap.GetResultTemplateTest;
import org.n52.sos.service.it.v2.soap.GetResultTest;
import org.n52.sos.service.it.v2.soap.InsertObservationTest;
import org.n52.sos.service.it.v2.soap.InsertResultTemplateTest;
import org.n52.sos.service.it.v2.soap.InsertResultTest;
import org.n52.sos.service.it.v2.soap.InsertSensorTest;
import org.n52.sos.service.it.v2.soap.UpdateSensorDescriptionTest;

/**
 * @since 4.0.0
 * 
 */
@RunWith(ComplianceSuiteRunner.class)
public class SosV2SoapSuiteTest extends SOS40Executor {

    @AfterClass
    public static void cleanup() {
        SettingsManager.getInstance().cleanup();
    }

    @Override
    public Class<?>[] getTests() {
        return new Class<?>[] { DeleteObservationTest.class, DeleteSensorTest.class, DescribeSensorTest.class,
                GetCapabilitiesTest.class, GetDataAvailabilityTest.class, GetFeatureOfInterestTest.class,
                GetObservationByIdTest.class, GetObservationTest.class, GetResultTemplateTest.class,
                GetResultTest.class, InsertObservationTest.class, InsertResultTemplateTest.class,
                InsertResultTest.class, InsertSensorTest.class, UpdateSensorDescriptionTest.class };
    }
}
