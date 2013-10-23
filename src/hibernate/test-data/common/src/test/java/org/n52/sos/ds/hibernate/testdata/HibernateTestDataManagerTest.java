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
package org.n52.sos.ds.hibernate.testdata;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.hibernate.Session;
import org.junit.Test;
import org.n52.sos.ds.TestDataManager;
import org.n52.sos.ds.TestDataManagerRepository;
import org.n52.sos.ds.hibernate.HibernateTestCase;
import org.n52.sos.ds.hibernate.dao.OfferingDAO;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * @since 4.0.0
 * 
 */
public class HibernateTestDataManagerTest extends HibernateTestCase {
    private TestDataManagerRepository testDataManagerRepository = TestDataManagerRepository.getInstance();

    private OfferingDAO offeringDao = new OfferingDAO();

    @Test
    public void testTestHibernateDataManager() throws OwsExceptionReport {
        String testOffering = TestHibernateTestDataManager.TEST_OFFERING;

        if (testDataManagerRepository.hasTestDataManager()) {
            TestDataManager testDataManager = testDataManagerRepository.getFirstTestDataManager();

            assertNotNull("HibernateTestDataManager instance is null", testDataManager);
            assertFalse("TestHibernateTestDataManager doesn't know it doesn't have test data",
                    testDataManager.hasTestData());
            assertTrue("TestHibernateTestDataManager doesn't support test data insertion",
                    testDataManager.supportsInsertTestData());
            testDataManager.insertTestData();

            Session session = getSession();
            assertNotNull("Test offering not inserted", offeringDao.getOfferingForIdentifier(testOffering, session));
            returnSession(session);

            assertTrue("TestHibernateTestDataManager doesn't support test data detection",
                    testDataManager.supportsHasTestData());
            assertTrue("TestHibernateTestDataManager doesn't know it has test data", testDataManager.hasTestData());
            assertTrue("TestHibernateTestDataManager doesn't support test data removal",
                    testDataManager.supportsRemoveTestData());
            testDataManager.removeTestData();

            session = getSession();
            assertNull("Test offering not removed", offeringDao.getOfferingForIdentifier(testOffering, session));
            returnSession(session);

            assertFalse("TestHibernateTestDataManager doesn't know it doesn't have test data after removal",
                    testDataManager.hasTestData());
        }
    }
}
