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
package org.n52.sos.ds.hibernate;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
import org.hibernate.mapping.Table;
import org.hibernate.spatial.dialect.h2geodb.GeoDBDialect;
import org.n52.sos.cache.ctrl.ScheduledContentCacheControllerSettings;
import org.n52.sos.config.sqlite.SQLiteSessionFactory;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.SosContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * @since 4.0.0
 * 
 */
public class H2Configuration {
    private static final Logger LOG = LoggerFactory.getLogger(H2Configuration.class);

    private static final String HIBERNATE_CONNECTION_URL = SQLiteSessionFactory.HIBERNATE_CONNECTION_URL;

    private static final String HIBERNATE_CONNECTION_DRIVER_CLASS =
            SQLiteSessionFactory.HIBERNATE_CONNECTION_DRIVER_CLASS;

    private static final String HIBERNATE_DIALECT = SQLiteSessionFactory.HIBERNATE_DIALECT;

    private static final String H2_DRIVER = "org.h2.Driver";

    private static final String H2_CONNECTION_URL = "jdbc:h2:mem:sos;DB_CLOSE_DELAY=-1;MULTI_THREADED=true";

    private static Properties properties = new Properties() {
        private static final long serialVersionUID = 3109256773218160485L;

        {
            put(HIBERNATE_CONNECTION_URL, H2_CONNECTION_URL);
            put(HIBERNATE_CONNECTION_DRIVER_CLASS, H2_DRIVER);
            put(HIBERNATE_DIALECT, GeoDBDialect.class.getName());
            put(SessionFactoryProvider.HIBERNATE_RESOURCES, getResources());
        }

        private List<String> getResources() {
            List<String> resources = Lists.newLinkedList();
            // core
            resources.add("mapping/core/Codespace.hbm.xml");
            resources.add("mapping/core/FeatureOfInterest.hbm.xml");
            resources.add("mapping/core/FeatureOfInterestType.hbm.xml");
            resources.add("mapping/core/ObservableProperty.hbm.xml");
            resources.add("mapping/core/Observation.hbm.xml");
            resources.add("mapping/core/ObservationInfo.hbm.xml");
            resources.add("mapping/core/Offering.hbm.xml");
            resources.add("mapping/core/Procedure.hbm.xml");
            resources.add("mapping/core/ProcedureDescriptionFormat.hbm.xml");
            resources.add("mapping/core/Unit.hbm.xml");
            // transactional module
            resources.add("mapping/transactional/ObservationConstellation.hbm.xml");
            resources.add("mapping/transactional/ObservationType.hbm.xml");
            resources.add("mapping/transactional/RelatedFeature.hbm.xml");
            resources.add("mapping/transactional/RelatedFeatureRole.hbm.xml");
            resources.add("mapping/transactional/ResultTemplate.hbm.xml");
            resources.add("mapping/transactional/ValidProcedureTime.hbm.xml");
            resources.add("mapping/transactional/TFeatureOfInterest.hbm.xml");
            resources.add("mapping/transactional/TObservableProperty.hbm.xml");
            resources.add("mapping/transactional/TOffering.hbm.xml");
            resources.add("mapping/transactional/TProcedure.hbm.xml");
            return resources;
        }
    };

    private static final Object LOCK = new Object();

    private static H2Configuration instance;

    private File tempDir;

    private Configuration configuration;

    private String[] createScript;

    private String[] dropScript;

    public static void assertInitialized() {
        synchronized (LOCK) {
            if (instance == null) {
                try {
                    instance = new H2Configuration();
                } catch (final IOException ex) {
                    throw new RuntimeException(ex);
                } catch (final OwsExceptionReport ex) {
                    throw new RuntimeException(ex);
                } catch (final ConnectionProviderException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    public static Session getSession() {
        H2Configuration.assertInitialized();
        try {
            return (Session) Configurator.getInstance().getDataConnectionProvider().getConnection();
        } catch (final ConnectionProviderException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void returnSession(final Session session) {
        if (session != null) {
            Configurator.getInstance().getDataConnectionProvider().returnConnection(session);
        }
    }

    public static void recreate() {
        synchronized (LOCK) {
            if (instance == null) {
                throw new IllegalStateException("Database is not initialized");
            }
            Session session = null;
            Transaction transaction = null;
            try {
                session = getSession();
                transaction = session.beginTransaction();
                session.doWork(new Work() {
                    @Override
                    public void execute(final Connection connection) throws SQLException {
                        Statement stmt = null;
                        try {
                            stmt = connection.createStatement();
                            for (final String cmd : instance.getDropScript()) {
                                stmt.addBatch(cmd);
                            }
                            for (final String cmd : instance.getCreateScript()) {
                                stmt.addBatch(cmd);
                            }
                            stmt.executeBatch();
                        } finally {
                            if (stmt != null) {
                                stmt.close();
                            }
                        }
                    }
                });
                transaction.commit();
            } catch (final HibernateException e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            } finally {
                returnSession(session);
            }
        }
    }

    public static void truncate() {
        synchronized (LOCK) {
            if (instance == null) {
                throw new IllegalStateException("Database is not initialized");
            }
            final Iterator<Table> tableMappings = instance.getConfiguration().getTableMappings();
            final List<String> tableNames = new LinkedList<String>();
            while (tableMappings.hasNext()) {
                tableNames.add(tableMappings.next().getName());
            }
            Session session = null;
            Transaction transaction = null;
            try {
                session = getSession();
                transaction = session.beginTransaction();
                session.doWork(new Work() {
                    @Override
                    public void execute(final Connection connection) throws SQLException {
                        Statement stmt = null;
                        try {
                            stmt = connection.createStatement();
                            stmt.addBatch("SET REFERENTIAL_INTEGRITY FALSE");
                            for (final String table : tableNames) {
                                stmt.addBatch("DELETE FROM " + table);
                            }
                            stmt.addBatch("SET REFERENTIAL_INTEGRITY TRUE");
                            stmt.executeBatch();
                        } finally {
                            if (stmt != null) {
                                stmt.close();
                            }
                        }
                    }
                });
                transaction.commit();
            } catch (final HibernateException e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            } finally {
                returnSession(session);
            }
        }
    }

    private H2Configuration() throws IOException, OwsExceptionReport, ConnectionProviderException {
        init();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                cleanup();
            }
        }));
    }

    private void cleanup() {
        try {
            final Configurator configurator = Configurator.getInstance();
            if (configurator != null) {
                configurator.cleanup();
            }
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
        try {
            final File directory = getTempDir();
            if (directory != null && directory.exists()) {
                for (File file : directory.listFiles()) {
                    if (file.exists()) {
                        FileUtils.forceDelete(file);
                    }
                }
                FileUtils.forceDelete(directory);
            }
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void setDefaultSettings() {
        ScheduledContentCacheControllerSettings.CACHE_UPDATE_INTERVAL_DEFINITION.setDefaultValue(0);
    }

    private File getTempDir() {
        return tempDir;
    }

    private void setTempDir(final File aTempDir) {
        tempDir = aTempDir;
    }

    private void createTempDir() throws IOException {
        setTempDir(File.createTempFile("hibernate-test-case", ""));
        getTempDir().delete();
        FileUtils.forceMkdir(getTempDir());
        SosContextListener.setPath(getTempDir().getAbsolutePath());
    }

    private void createConfigurator() throws ConfigurationException {
        Configurator.createInstance(properties, getTempDir().getAbsolutePath());
    }

    private void prepareDatabase() {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(H2_DRIVER);
            conn = DriverManager.getConnection(H2_CONNECTION_URL);
            stmt = conn.createStatement();
            final String cmd = "create domain if not exists geometry as blob";
            LOG.debug("Executing {}", cmd);
            stmt.execute(cmd);
            configuration = new Configuration().configure("/sos-hibernate.cfg.xml");
            @SuppressWarnings("unchecked")
            List<String> resources = (List<String>) properties.get(SessionFactoryProvider.HIBERNATE_RESOURCES);
            for (String resource : resources) {
                configuration.addResource(resource);
            }
            final GeoDBDialect dialect = new GeoDBDialect();
            createScript = configuration.generateSchemaCreationScript(dialect);
            dropScript = configuration.generateDropSchemaScript(dialect);
            for (final String s : createScript) {
                LOG.debug("Executing {}", s);
                stmt.execute(s);
            }
        } catch (final ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (final SQLException ex) {
            throw new RuntimeException(ex);
        } catch (MappingException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (final SQLException ex) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (final SQLException ex) {
                }
            }
        }
    }

    private void init() throws ConfigurationException, IOException {
        setDefaultSettings();
        createTempDir();
        prepareDatabase();
        createConfigurator();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String[] getCreateScript() {
        return createScript;
    }

    public String[] getDropScript() {
        return dropScript;
    }
}
