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
package org.n52.sos.ds.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import junit.framework.TestCase;

import org.hibernate.dialect.Dialect;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.n52.sos.config.SettingDefinition;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.util.HibernateConstants;

/**
 * @since 4.0.0
 * 
 */
public class AbstractHibernateFullDBDatasourceTest extends TestCase {
    private AbstractHibernateFullDBDatasource ds;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ds = new MockDatasource();
    }

    public void testGetSettingDefinitions() throws Exception {
        Set<SettingDefinition<?, ?>> settings = ds.getSettingDefinitions();
        checkSettingDefinitionsTransactional(settings);
    }

    public void testGetChangableSettingDefinitions() throws Exception {
        Set<SettingDefinition<?, ?>> settings = ds.getChangableSettingDefinitions(new Properties());
        checkSettingDefinitionsChangableSetting(settings);
    }

    public void testParseDatasourceProperties() throws Exception {
        Properties current = new Properties();
        current.put(HibernateConstants.DEFAULT_CATALOG, "public");
        current.put(HibernateConstants.CONNECTION_USERNAME, "postgres");
        current.put(HibernateConstants.CONNECTION_PASSWORD, "postgres");
        current.put(HibernateConstants.CONNECTION_URL, "jdbc:postgresql://localhost:5432/test");
        current.put(HibernateConstants.C3P0_MIN_SIZE, "10");
        current.put(HibernateConstants.C3P0_MAX_SIZE, "30");
        current.put(HibernateDatasourceConstants.PROVIDED_JDBC, "true");

        Map<String, Object> settings = ds.parseDatasourceProperties(current);
        checkSettingKeysTransactional(settings.keySet());
    }

    private void checkSettingDefinitionsTransactional(Set<SettingDefinition<?, ?>> settings) {
        checkSettingDefinitions(settings, false);
    }

    private void checkSettingDefinitionsChangableSetting(Set<SettingDefinition<?, ?>> settings) {
        checkSettingDefinitions(settings, true);

    }

    private void checkSettingDefinitions(Set<SettingDefinition<?, ?>> settings, boolean changeable) {
        List<String> keys = new ArrayList<String>();
        Iterator<SettingDefinition<?, ?>> iterator = settings.iterator();
        while (iterator.hasNext()) {
            keys.add(iterator.next().getKey());
        }
        checkSettingKeys(keys, changeable);
    }

    private void checkSettingKeysTransactional(Collection<String> keys) {
        checkSettingKeys(keys, false);
    }

    private void checkSettingKeys(Collection<String> keys, boolean changeable) {
        final int maxCount = 11;
        int counter = 11;
        boolean spatialFilteringProfile = true;
        boolean transactional = true;
        if (!keys.contains(AbstractHibernateDatasource.SPATIAL_FILTERING_PROFILE_KEY)) {
            counter--;
            spatialFilteringProfile = false;
        }
        if (!keys.contains(AbstractHibernateDatasource.TRANSACTIONAL_KEY)) {
            counter--;
            transactional = false;
        }
        if (changeable) {
            assertEquals(8, keys.size());
        } else {
            assertEquals((transactional && spatialFilteringProfile) ? maxCount : counter, keys.size());
        }
        assertTrue(keys.contains(AbstractHibernateDatasource.HOST_KEY));
        assertTrue(keys.contains(AbstractHibernateDatasource.PORT_KEY));
        assertTrue(keys.contains(AbstractHibernateDatasource.DATABASE_KEY));
        assertTrue(keys.contains(AbstractHibernateDatasource.USERNAME_KEY));
        assertTrue(keys.contains(AbstractHibernateDatasource.PASSWORD_KEY));
        assertTrue(keys.contains(AbstractHibernateDatasource.SCHEMA_KEY));
        assertTrue(keys.contains(AbstractHibernateDatasource.MIN_POOL_SIZE_KEY));
        assertTrue(keys.contains(AbstractHibernateDatasource.MAX_POOL_SIZE_KEY));
        if (!changeable) {
            assertTrue(keys.contains(AbstractHibernateDatasource.PROVIDED_JDBC_DRIVER_KEY));
        }
        if (transactional) {
            assertTrue(keys.contains(AbstractHibernateDatasource.TRANSACTIONAL_KEY));
        }
        if (spatialFilteringProfile) {
            assertTrue(keys.contains(AbstractHibernateDatasource.SPATIAL_FILTERING_PROFILE_KEY));
        }
    }

    private class MockDatasource extends AbstractHibernateFullDBDatasource {
        @Override
        protected Dialect createDialect() {
            return null;
        }

        @Override
        public String getDialectName() {
            return null;
        }

        @Override
        public boolean checkSchemaCreation(Map<String, Object> settings) {
            return false;
        }

        @Override
        protected String toURL(Map<String, Object> settings) {
            return null;
        }

        @Override
        protected String[] parseURL(String url) {
            return new String[] { "localhost", "5432", "db" };
        }

        @Override
        protected String getDriverClass() {
            return null;
        }

        @Override
        public void clear(Properties settings) {
        }

        @Override
        public boolean supportsClear() {
            return false;
        }

        @Override
        protected void validatePrerequisites(Connection con, DatabaseMetadata metadata, Map<String, Object> settings) {
        }

        @Override
        protected Connection openConnection(Map<String, Object> settings) throws SQLException {
            return null;
        }

        @Override
        protected String[] checkDropSchema(String[] dropSchema) {
            // TODO Auto-generated method stub
            return null;
        }
    }
}
