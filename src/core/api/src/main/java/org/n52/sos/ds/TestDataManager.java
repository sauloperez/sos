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
package org.n52.sos.ds;

import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * Abstract TestDataManager class
 * 
 * @since 4.0.0
 * 
 */
public abstract class TestDataManager {

    /**
     * Return an integer for use in sorting when loading a test data manager.
     * Implementing classes can override this method to establish load order.
     * 
     * @return sort priority for service loading
     */
    public int getServiceLoaderPriority() {
        return 0;
    }

    /**
     * Supports this TestDataManager the insertion of test data.
     * 
     * @return Supports or not test data insertion
     */
    public boolean supportsInsertTestData() {
        // base class supports no operation by default
        return false;
    }

    /**
     * Supports this TestDataManager the has test data function.
     * 
     * @return Supports or not the has test data function
     */
    public boolean supportsHasTestData() {
        // base class supports no operation by default
        return false;
    }

    /**
     * Provides this TestDataManager test data
     * 
     * @return Provides or not test data
     * @throws OwsExceptionReport
     */
    public boolean hasTestData() throws OwsExceptionReport {
        // base class supports no operation by default
        return false;
    }

    /**
     * Supports this TestDataManager the removal of the test data
     * 
     * @return Supports or not the removal or test data.
     */
    public boolean supportsRemoveTestData() {
        // base class supports no operation by default
        return false;
    }

    /**
     * Method to insert test data into datasource. Implementations should reload
     * the cache after testdata insertion if needed.
     */
    public abstract void insertTestData() throws OwsExceptionReport;

    /**
     * Method to remove test data from datasource. Implementations should reload
     * the cache after test data removal if needed.
     */
    public abstract void removeTestData() throws OwsExceptionReport;

}
