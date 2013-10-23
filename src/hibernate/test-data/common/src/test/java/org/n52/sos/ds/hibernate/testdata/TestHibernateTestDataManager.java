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

import java.util.ArrayList;

import org.hibernate.Session;
import org.junit.BeforeClass;
import org.n52.sos.ds.hibernate.H2Configuration;
import org.n52.sos.ds.hibernate.dao.OfferingDAO;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterestType;
import org.n52.sos.ds.hibernate.entities.ObservationType;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.RelatedFeature;

/**
 * @since 4.0.0
 * 
 */
public class TestHibernateTestDataManager extends HibernateTestDataManager {
    private OfferingDAO offeringDao = new OfferingDAO();

    public static final String TEST_OFFERING = "test_offering";

    @BeforeClass
    public static void init() {
        H2Configuration.assertInitialized();
    }

    protected static Session getSession() {
        return H2Configuration.getSession();
    }

    protected static void returnSession(Session session) {
        H2Configuration.returnSession(session);
    }

    @Override
    public boolean supportsInsertTestData() {
        return true;
    }

    @Override
    public void insertTestData() {
        Session session = getSession();
        offeringDao.getAndUpdateOrInsertNewOffering(TEST_OFFERING, TEST_OFFERING, new ArrayList<RelatedFeature>(),
                new ArrayList<ObservationType>(), new ArrayList<FeatureOfInterestType>(), session);
        returnSession(session);
    }

    @Override
    public boolean supportsHasTestData() {
        return true;
    }

    @Override
    public boolean hasTestData() {
        Session session = getSession();
        Offering offering = offeringDao.getOfferingForIdentifier(TEST_OFFERING, session);
        returnSession(session);
        return offering != null;
    }

    @Override
    public boolean supportsRemoveTestData() {
        return true;
    }

    @Override
    public void removeTestData() {
        if (hasTestData()) {
            Session session = getSession();
            TestHibernateTestDataDAO.deleteOfferingByIdentifier(TEST_OFFERING, session);
            returnSession(session);
        }
    }
}
