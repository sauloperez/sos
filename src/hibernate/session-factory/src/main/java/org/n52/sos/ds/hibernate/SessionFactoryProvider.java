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
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.ds.DataConnectionProvider;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.type.UtcTimestampType;
import org.n52.sos.exception.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Implementation of the SessionFactory.
 * 
 * @since 4.0.0
 */
public class SessionFactoryProvider extends AbstractSessionFactoryProvider implements DataConnectionProvider,
        HibernateDatasourceConstants {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionFactoryProvider.class);

    /**
     * SessionFactory instance
     */
    private SessionFactory sessionFactory = null;

    /**
     * Configuration instance
     */
    private Configuration configuration = null;

    /**
     * constructor. Opens a new Hibernate SessionFactory
     */
    public SessionFactoryProvider() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sos.ds.ConnectionProvider#getConnection()
     */
    @Override
    public Session getConnection() throws ConnectionProviderException {
        try {
            if (sessionFactory == null) {
                return null;
            }
            return sessionFactory.openSession();
        } catch (HibernateException he) {
            String exceptionText = "Error while getting connection!";
            LOGGER.error(exceptionText, he);
            ConnectionProviderException cpe = new ConnectionProviderException(exceptionText, he);
            throw cpe;
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sos.ds.ConnectionProvider#returnConnection(java.lang.Object)
     */
    @Override
    public void returnConnection(Object connection) {
        try {
            if (connection instanceof Session) {
                Session session = (Session) connection;
                if (session.isOpen()) {
                    session.clear();
                    session.close();
                }
            }
        } catch (HibernateException he) {
            LOGGER.error("Error while returning connection!", he);
        }

    }

    // /*
    // * (non-Javadoc)
    // *
    // * @see org.n52.sos.ds.ConnectionProvider#cleanup()
    // */
    // @Override
    // public void cleanup() {
    // if (this.sessionFactory != null) {
    // try {
    // if (this.sessionFactory instanceof SessionFactoryImpl) {
    // SessionFactoryImpl sf = (SessionFactoryImpl) this.sessionFactory;
    // ConnectionProvider conn = sf.getConnectionProvider();
    // if (conn instanceof C3P0ConnectionProvider) {
    // ((C3P0ConnectionProvider) conn).close();
    // }
    // }
    // this.sessionFactory.close();
    // LOGGER.info("Connection provider closed successfully!");
    // } catch (HibernateException he) {
    // LOGGER.error("Error while closing connection provider!", he);
    // }
    // }
    // }

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(Properties properties) throws ConfigurationException {
        try {
            LOGGER.debug("Instantiating configuration and session factory");
            configuration = new Configuration().configure("/sos-hibernate.cfg.xml");
            if (properties.containsKey(HIBERNATE_RESOURCES)) {
                List<String> resources = (List<String>) properties.get(HIBERNATE_RESOURCES);
                for (String resource : resources) {
                    configuration.addResource(resource);
                }
                properties.remove(HIBERNATE_RESOURCES);
            } else if (properties.containsKey(HIBERNATE_DIRECTORY)) {
                String directories = (String) properties.get(HIBERNATE_DIRECTORY);
                for (String directory : directories.split(PATH_SEPERATOR)) {
                    File hibernateDir = new File(directory);
                    if (!hibernateDir.exists()) {
                        throw new ConfigurationException("Hibernate directory " + directory + " doesn't exist!");
                    }
                    configuration.addDirectory(new File(directory));
                }
            } else {
                // keep this as default/fallback
                configuration.addDirectory(new File(getClass().getResource(HIBERNATE_MAPPING_CORE_PATH).toURI()));
                configuration.addDirectory(new File(getClass().getResource(HIBERNATE_MAPPING_TRANSACTIONAL_PATH)
                        .toURI()));
            }
            configuration.mergeProperties(properties);

            // set timestamp mapping to a special type to ensure time is always
            // queried in UTC

            configuration.registerTypeOverride(new UtcTimestampType());

            ServiceRegistry serviceRegistry =
                    new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
            this.sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (HibernateException he) {
            String exceptionText = "An error occurs during instantiation of the database connection pool!";
            LOGGER.error(exceptionText, he);
            cleanup();
            throw new ConfigurationException(exceptionText, he);
        } catch (URISyntaxException urise) {
            String exceptionText = "An error occurs during instantiation of the database connection pool!";
            LOGGER.error(exceptionText, urise);
            cleanup();
            throw new ConfigurationException(exceptionText, urise);
        }
    }

    @Override
    protected SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    public String getUpdateScript() throws ConnectionProviderException, SQLException {
        if (configuration == null) {
            throw new ConfigurationException("configuration is null");
        }
        if (sessionFactory == null) {
            throw new ConfigurationException("sessionFactory is null");
        }
        Dialect dialect = ((SessionFactoryImplementor) sessionFactory).getDialect();
        if (dialect == null) {
            throw new ConfigurationException("dialect is null");
        }
        Session session = getConnection();
        Connection conn = ((SessionImplementor) session).connection();
        DatabaseMetadata databaseMetadata = new DatabaseMetadata(conn, dialect);
        String[] udpateSql = configuration.generateSchemaUpdateScript(dialect, databaseMetadata);
        returnConnection(session);
        StringBuilder updateSqlString = new StringBuilder();
        for (String sqlLine : udpateSql) {
            updateSqlString.append(sqlLine + ";\n\n");
        }
        return updateSqlString.toString();
    }
}
