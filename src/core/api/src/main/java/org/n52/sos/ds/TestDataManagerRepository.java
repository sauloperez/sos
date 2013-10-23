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

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.util.AbstractConfiguringServiceLoaderRepository;
import org.n52.sos.util.CollectionHelper;

/**
 * Repository for TestDataManager implementations
 * 
 * @since 4.0.0
 * 
 */
public class TestDataManagerRepository extends AbstractConfiguringServiceLoaderRepository<TestDataManager> {

    private List<TestDataManager> testDataManagers = new LinkedList<TestDataManager>();

    /**
     * Private constructor
     */
    private TestDataManagerRepository() {
        super(TestDataManager.class, false);
        load(false);
    }

    /**
     * Gets the singleton instance of the TestDataManagerRepository.
     * <p/>
     * 
     * @return the test data manager repository
     */
    public static TestDataManagerRepository getInstance() {
        return TestDataManagerRepositoryHolder.INSTANCE;
    }

    @Override
    protected void processConfiguredImplementations(Set<TestDataManager> managers) throws ConfigurationException {
        this.testDataManagers.clear();
        for (TestDataManager manager : managers) {
            testDataManagers.add(manager);
        }
        Collections.sort(testDataManagers, new TestDataManagerComparator());
    }

    /**
     * Get the first TestDataManager implementation
     * 
     * @return First TestDataManager
     */
    public TestDataManager getFirstTestDataManager() {
        Iterator<TestDataManager> it = getTestDataManagers().iterator();
        return it.hasNext() ? it.next() : null;
    }

    /**
     * Check if TestDataManager implementations are loaded
     * 
     * @return TestDataManager loaded or not
     */
    public boolean hasTestDataManager() {
        return CollectionHelper.isNotEmpty(getTestDataManagers());
    }

    /**
     * Get the ordered list of TestDataManager
     * 
     * @return Ordered List of TestDataManager implementations
     */
    private List<TestDataManager> getTestDataManagers() {
        return Collections.unmodifiableList(testDataManagers);
    }

    private static class TestDataManagerRepositoryHolder {
        public static final TestDataManagerRepository INSTANCE = new TestDataManagerRepository();

        private TestDataManagerRepositoryHolder() {
        }
    }

    /**
     * Order TestDataManagers first by service loader rank and then by examining
     * class inheritance, so that TestDataManagers that inherit from other
     * TestDataManagers are used first.
     */
    private class TestDataManagerComparator implements Comparator<TestDataManager> {
        @Override
        public int compare(TestDataManager o1, TestDataManager o2) {
            if (o1 == null ^ o2 == null) {
                return (o1 == null) ? -1 : 1;
            }
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1.getServiceLoaderPriority() != o2.getServiceLoaderPriority()) {
                return o1.getServiceLoaderPriority() > o2.getServiceLoaderPriority() ? -1 : 1;
            }

            if (o1.getClass().isAssignableFrom(o2.getClass())) {
                return 1;
            } else if (o2.getClass().isAssignableFrom(o1.getClass())) {
                return -1;
            }
            return 0;
        }
    }

}
