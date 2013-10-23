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

import java.io.File;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.BooleanSettingDefinition;
import org.n52.sos.config.settings.IntegerSettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;
import org.n52.sos.ds.Datasource;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.SessionFactoryProvider;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.util.SQLConstants;
import org.n52.sos.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public abstract class AbstractHibernateDatasource implements Datasource, SQLConstants, HibernateDatasourceConstants {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractHibernateDatasource.class);

    protected static final String USERNAME_TITLE = "User Name";

    protected static final String PASSWORD_TITLE = "Password";

    protected static final String DATABASE_KEY = "jdbc.database";

    protected static final String DATABASE_TITLE = "Database";

    protected static final String DATABASE_DESCRIPTION =
            "Set this to the name of the database you want to use for SOS.";

    protected static final String DATABASE_DEFAULT_VALUE = "sos";

    protected static final String HOST_KEY = "jdbc.host";

    protected static final String HOST_TITLE = "Host";

    protected static final String HOST_DESCRIPTION =
            "Set this to the IP/net location of the database server. The default value for is \"localhost\".";

    protected static final String HOST_DEFAULT_VALUE = "localhost";

    protected static final String PORT_KEY = "jdbc.port";

    protected static final String PORT_TITLE = "Database Port";

    protected static final String SCHEMA_KEY = HibernateConstants.DEFAULT_SCHEMA;

    protected static final String SCHEMA_TITLE = "Schema";

    protected static final String SCHEMA_DESCRIPTION =
            "Qualifies unqualified table names with the given schema in generated SQL.";

    protected static final String SCHMEA_DEFAULT_VALUE = "public";

    protected static final String TRANSACTIONAL_TITLE = "Transactional Profile";

    protected static final String TRANSACTIONAL_DESCRIPTION = "Should the database support the transactional profile?";

    protected static final String TRANSACTIONAL_KEY = "sos.transactional";

    protected static final boolean TRANSACTIONAL_DEFAULT_VALUE = true;

    protected static final String SPATIAL_FILTERING_PROFILE_TITLE = "Spatial Filtering Profile";

    protected static final String SPATIAL_FILTERING_PROFILE_DESCRIPTION =
            "Should the database support the Spatial Filtering Profile?";

    protected static final String SPATIAL_FILTERING_PROFILE_KEY = "sos.spatialFilteringProfile";

    protected static final boolean SPATIAL_FILTERING_PROFILE_DEFAULT_VALUE = true;

    protected static final String USERNAME_KEY = HibernateConstants.CONNECTION_USERNAME;

    protected static final String PASSWORD_KEY = HibernateConstants.CONNECTION_PASSWORD;

    protected static final String C3P0_CONNECTION_POOL =
            "org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider";

    protected static final Boolean PROVIDED_JDBC_DRIVER_DEFAULT_VALUE = false;

    protected static final String PROVIDED_JDBC_DRIVER_TITLE = "Provided JDBC driver";

    protected static final String PROVIDED_JDBC_DRIVER_DESCRIPTION =
            "Is the JDBC driver provided and should not be derigistered during shutdown?";

    protected static final String PROVIDED_JDBC_DRIVER_KEY = "sos.jdbc.provided";

    protected static final String MIN_POOL_SIZE_KEY = "jdbc.pool.min";

    protected static final String MIN_POOL_SIZE_TITLE = "Minimum ConnectionPool size";

    protected static final String MIN_POOL_SIZE_DESCRIPTION = "Minimum size of the ConnectionPool";

    protected static final Integer MIN_POOL_SIZE_DEFAULT_VALUE = 10;

    protected static final String MAX_POOL_SIZE_KEY = "jdbc.pool.max";

    protected static final String MAX_POOL_SIZE_TITLE = "Maximum ConnectionPool size";

    protected static final String MAX_POOL_SIZE_DESCRIPTION = "Maximum size of the ConnectionPool";

    protected static final Integer MAX_POOL_SIZE_DEFAULT_VALUE = 30;

    private Dialect dialect;

    private final BooleanSettingDefinition transactionalDefiniton = createTransactionalDefinition();

    private boolean transactionalDatasource = true;

    private final BooleanSettingDefinition spatialFilteringProfileDefinition =
            createSpatialFilteringProfileDefinition();

    private boolean spatialFilteringProfileDatasource = true;

    protected StringSettingDefinition createUsernameDefinition() {
        return new StringSettingDefinition()
                .setGroup(BASE_GROUP)
                .setOrder(SettingDefinitionProvider.ORDER_1)
                .setKey(USERNAME_KEY)
                .setTitle(USERNAME_TITLE);
    }

    protected StringSettingDefinition createPasswordDefinition() {
        return new StringSettingDefinition()
                .setGroup(BASE_GROUP)
                .setOrder(SettingDefinitionProvider.ORDER_2)
                .setKey(PASSWORD_KEY)
                .setTitle(PASSWORD_TITLE);
    }

    protected StringSettingDefinition createDatabaseDefinition() {
        return new StringSettingDefinition()
                .setGroup(BASE_GROUP)
                .setOrder(SettingDefinitionProvider.ORDER_3)
                .setKey(DATABASE_KEY)
                .setTitle(DATABASE_TITLE)
                .setDescription(DATABASE_DESCRIPTION)
                .setDefaultValue(DATABASE_DEFAULT_VALUE);
    }

    protected StringSettingDefinition createHostDefinition() {
        return new StringSettingDefinition()
                .setGroup(BASE_GROUP)
                .setOrder(SettingDefinitionProvider.ORDER_4)
                .setKey(HOST_KEY)
                .setTitle(HOST_TITLE)
                .setDescription(HOST_DESCRIPTION)
                .setDefaultValue(HOST_DEFAULT_VALUE);
    }

    protected IntegerSettingDefinition createPortDefinition() {
        return new IntegerSettingDefinition()
                .setGroup(BASE_GROUP)
                .setOrder(SettingDefinitionProvider.ORDER_5)
                .setKey(PORT_KEY)
                .setTitle(PORT_TITLE);
    }

    protected StringSettingDefinition createSchemaDefinition() {
        return new StringSettingDefinition()
                .setGroup(ADVANCED_GROUP)
                .setOrder(SettingDefinitionProvider.ORDER_1)
                .setKey(SCHEMA_KEY)
                .setTitle(SCHEMA_TITLE)
                .setDescription(SCHEMA_DESCRIPTION)
                .setDefaultValue(SCHMEA_DEFAULT_VALUE);
    }

    protected BooleanSettingDefinition createTransactionalDefinition() {
        return new BooleanSettingDefinition()
                .setDefaultValue(TRANSACTIONAL_DEFAULT_VALUE)
                .setTitle(TRANSACTIONAL_TITLE)
                .setDescription(TRANSACTIONAL_DESCRIPTION)
                .setGroup(ADVANCED_GROUP)
                .setOrder(SettingDefinitionProvider.ORDER_2)
                .setKey(TRANSACTIONAL_KEY);
    }

    protected BooleanSettingDefinition createSpatialFilteringProfileDefinition() {
        return new BooleanSettingDefinition()
                .setDefaultValue(SPATIAL_FILTERING_PROFILE_DEFAULT_VALUE)
                .setTitle(SPATIAL_FILTERING_PROFILE_TITLE)
                .setDescription(SPATIAL_FILTERING_PROFILE_DESCRIPTION)
                .setGroup(ADVANCED_GROUP)
                .setOrder(SettingDefinitionProvider.ORDER_3)
                .setKey(SPATIAL_FILTERING_PROFILE_KEY);
    }

    protected BooleanSettingDefinition createProvidedJdbcDriverDefinition() {
        return new BooleanSettingDefinition()
                .setDefaultValue(PROVIDED_JDBC_DRIVER_DEFAULT_VALUE)
                .setTitle(PROVIDED_JDBC_DRIVER_TITLE)
                .setDescription(PROVIDED_JDBC_DRIVER_DESCRIPTION)
                .setDefaultValue(PROVIDED_JDBC_DRIVER_DEFAULT_VALUE)
                .setGroup(ADVANCED_GROUP)
                .setOrder(SettingDefinitionProvider.ORDER_4)
                .setKey(PROVIDED_JDBC_DRIVER_KEY);
    }

    protected IntegerSettingDefinition createMinPoolSizeDefinition() {
        return new IntegerSettingDefinition()
                .setGroup(ADVANCED_GROUP)
                .setOrder(SettingDefinitionProvider.ORDER_5)
                .setKey(MIN_POOL_SIZE_KEY)
                .setTitle(MIN_POOL_SIZE_TITLE)
                .setDescription(MIN_POOL_SIZE_DESCRIPTION)
                .setDefaultValue(MIN_POOL_SIZE_DEFAULT_VALUE);
    }

    protected IntegerSettingDefinition createMaxPoolSizeDefinition() {
        return new IntegerSettingDefinition()
                .setGroup(ADVANCED_GROUP)
                .setOrder(SettingDefinitionProvider.ORDER_6)
                .setKey(MAX_POOL_SIZE_KEY)
                .setTitle(MAX_POOL_SIZE_TITLE)
                .setDescription(MAX_POOL_SIZE_DESCRIPTION)
                .setDefaultValue(MAX_POOL_SIZE_DEFAULT_VALUE);
    }

    public CustomConfiguration getConfig(Map<String, Object> settings) {
        CustomConfiguration config = new CustomConfiguration();
        config.configure("/sos-hibernate.cfg.xml");
        config.addDirectory(resource(HIBERNATE_MAPPING_CORE_PATH));
        if (isTransactionalDatasource()) {
            Boolean transactional = (Boolean) settings.get(this.transactionalDefiniton.getKey());
            if (transactional != null && transactional.booleanValue()) {
                config.addDirectory(resource(HIBERNATE_MAPPING_TRANSACTIONAL_PATH));
            }
        }
        if (isSpatialFilteringProfileDatasource()) {
            Boolean spatialFilteringProfile = (Boolean) settings.get(this.spatialFilteringProfileDefinition.getKey());
            if (spatialFilteringProfile != null && spatialFilteringProfile.booleanValue()) {
                config.addDirectory(resource(HIBERNATE_MAPPING_SPATIAL_FILTERING_PROFILE_PATH));
            }
        }
        if (isSetSchema(settings)) {
            Properties properties = new Properties();
            properties.put(HibernateConstants.DEFAULT_SCHEMA, settings.get(HibernateConstants.DEFAULT_SCHEMA));
            config.addProperties(properties);
        }
        config.buildMappings();
        return config;
    }

    protected File resource(String resource) {
        try {
            return new File(getClass().getResource(resource).toURI());
        } catch (URISyntaxException ex) {
            throw new ConfigurationException(ex);
        }
    }

    @Override
    public String[] createSchema(Map<String, Object> settings) {
        String[] script = getConfig(settings).generateSchemaCreationScript(getDialectInternal());
        String[] pre = getPreSchemaScript();
        String[] post = getPostSchemaScript();

        script =
                (pre == null) ? (post == null) ? script : concat(script, post) : (post == null) ? concat(pre, script)
                        : concat(pre, script, post);

        return checkCreateSchema(script);
    }

    @Override
    public String[] dropSchema(Map<String, Object> settings) {
        Connection conn = null;
        try {
            conn = openConnection(settings);
            DatabaseMetadata metadata = new DatabaseMetadata(conn, getDialectInternal(), true);
            String[] dropScript =
                    checkDropSchema(getConfig(settings).generateDropSchemaScript(getDialectInternal(), metadata));
            return dropScript;
        } catch (SQLException ex) {
            throw new ConfigurationException(ex);
        } finally {
            close(conn);
        }
    }

    @Override
    public void validateSchema(Map<String, Object> settings) {
        Connection conn = null;
        try {
            conn = openConnection(settings);
            DatabaseMetadata metadata = new DatabaseMetadata(conn, getDialectInternal(), true);
            getConfig(settings).validateSchema(getDialectInternal(), metadata);
        } catch (SQLException ex) {
            throw new ConfigurationException(ex);
        } catch (HibernateException ex) {
            throw new ConfigurationException(ex);
        } finally {
            close(conn);
        }
    }

    @Override
    public boolean checkIfSchemaExists(Map<String, Object> settings) {
        Connection conn = null;
        try {
            /* check if any of the needed tables is exisiting */
            conn = openConnection(settings);
            DatabaseMetadata metadata = new DatabaseMetadata(conn, getDialectInternal(), true);
            Iterator<Table> iter = getConfig(settings).getTableMappings();
            while (iter.hasNext()) {
                Table table = iter.next();
                if (table.isPhysicalTable() && metadata.isTable(table.getName())) {
                    return true;
                }
            }
            return false;
        } catch (SQLException ex) {
            throw new ConfigurationException(ex);
        } finally {
            close(conn);
        }
    }

    protected Dialect getDialectInternal() {
        if (dialect == null) {
            dialect = createDialect();
        }
        return dialect;
    }

    public void execute(String[] sql, Map<String, Object> settings) throws HibernateException {
        Connection conn = null;
        try {
            conn = openConnection(settings);
            execute(sql, conn);
        } catch (SQLException ex) {
            throw new ConfigurationException(ex);
        } finally {
            close(conn);
        }
    }

    protected void execute(String[] sql, Connection conn) throws HibernateException {
        Statement stmt = null;
        String lastCmd = null;
        try {
            stmt = conn.createStatement();

            for (String cmd : sql) {
                lastCmd = cmd;
                stmt.execute(cmd);
            }
        } catch (SQLException ex) {
            if (lastCmd != null) {
                throw new ConfigurationException(ex.getMessage() + ". Command: " + lastCmd, ex);
            } else {
                throw new ConfigurationException(ex);
            }
        } finally {
            close(stmt);
        }
    }

    protected void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOG.error("Error closing connection", e);
            }
        }
    }

    protected void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                LOG.error("Error closing statement", e);
            }
        }
    }

    @Override
    public void validateConnection(Map<String, Object> settings) {
        Connection conn = null;
        try {
            conn = openConnection(settings);
        } catch (SQLException ex) {
            throw new ConfigurationException(ex);
        } finally {
            close(conn);
        }
    }

    @Override
    public boolean needsSchema() {
        return true;
    }

    @Override
    public void validatePrerequisites(Map<String, Object> settings) {
        Connection conn = null;
        try {
            conn = openConnection(settings);
            DatabaseMetadata metadata = new DatabaseMetadata(conn, getDialectInternal(), true);
            validatePrerequisites(conn, metadata, settings);
        } catch (SQLException ex) {
            throw new ConfigurationException(ex);
        } finally {
            close(conn);
        }
    }

    @Override
    public void validateConnection(Properties current, Map<String, Object> changed) {
        validateConnection(mergeProperties(current, changed));
    }

    @Override
    public void validatePrerequisites(Properties current, Map<String, Object> changed) {
        validatePrerequisites(mergeProperties(current, changed));
    }

    @Override
    public void validateSchema(Properties current, Map<String, Object> changed) {
        validateSchema(mergeProperties(current, changed));
    }

    @Override
    public boolean checkIfSchemaExists(Properties current, Map<String, Object> changed) {
        return checkIfSchemaExists(mergeProperties(current, changed));
    }

    @Override
    public Properties getDatasourceProperties(Properties current, Map<String, Object> changed) {
        return getDatasourceProperties(mergeProperties(current, changed));
    }

    protected Map<String, Object> mergeProperties(Properties current, Map<String, Object> changed) {
        Map<String, Object> settings = parseDatasourceProperties(current);
        settings.putAll(changed);
        return settings;
    }

    protected void addMappingFileDirectories(Map<String, Object> settings, Properties p) {
        String dirList = resource(HIBERNATE_MAPPING_CORE_PATH).getAbsolutePath();
        if (isTransactionalDatasource()) {
            Boolean t = (Boolean) settings.get(transactionalDefiniton.getKey());
            if (t.booleanValue()) {
                dirList +=
                        SessionFactoryProvider.PATH_SEPERATOR
                                + resource(HIBERNATE_MAPPING_TRANSACTIONAL_PATH).getAbsolutePath();
            }
        }
        if (isSpatialFilteringProfileDatasource()) {
            Boolean sfp = (Boolean) settings.get(spatialFilteringProfileDefinition.getKey());
            if (sfp.booleanValue()) {
                dirList +=
                        SessionFactoryProvider.PATH_SEPERATOR
                                + resource(HIBERNATE_MAPPING_SPATIAL_FILTERING_PROFILE_PATH).getAbsolutePath();
            }
        }
        p.put(SessionFactoryProvider.HIBERNATE_DIRECTORY, dirList);
    }

    protected boolean isTransactional(Properties properties) {
        String p = properties.getProperty(SessionFactoryProvider.HIBERNATE_DIRECTORY);
        return p == null || p.contains(HIBERNATE_MAPPING_TRANSACTIONAL_PATH);
    }

    protected BooleanSettingDefinition getTransactionalDefiniton() {
        return transactionalDefiniton;
    }

    protected boolean isSpatialFilteringProfile(Properties properties) {
        String p = properties.getProperty(SessionFactoryProvider.HIBERNATE_DIRECTORY);
        return p == null || p.contains(HIBERNATE_MAPPING_SPATIAL_FILTERING_PROFILE_PATH);
    }

    protected BooleanSettingDefinition getSpatialFilteringProfileDefiniton() {
        return spatialFilteringProfileDefinition;
    }

    private <T> T[] concat(T[] first, T[]... rest) {
        int length = first.length;
        for (int i = 0; i < rest.length; ++i) {
            length += rest[i].length;
        }
        T[] result = Arrays.copyOf(first, length);
        int offset = first.length;
        for (int i = 0; i < rest.length; ++i) {
            System.arraycopy(rest[i], 0, result, offset, rest[i].length);
            offset += rest[i].length;
        }
        return result;
    }

    /**
     * @return script to run before the schema creation
     */
    protected String[] getPreSchemaScript() {
        return null;
    }

    /**
     * @return script to run after the schema creation
     */
    protected String[] getPostSchemaScript() {
        return null;
    }

    protected boolean isSetSchema(Map<String, Object> settings) {
        if (settings.containsKey(HibernateConstants.DEFAULT_SCHEMA)) {
            return StringHelper.isNotEmpty((String) settings.get(HibernateConstants.DEFAULT_SCHEMA));
        }
        return false;
    }

    protected String getSchema(Map<String, Object> settings) {
        if (isSetSchema(settings)) {
            return (String) settings.get(HibernateConstants.DEFAULT_SCHEMA) + ".";
        }
        return "";
    }

    /**
     * @return the transactionalDatasource
     */
    public boolean isTransactionalDatasource() {
        return transactionalDatasource;
    }

    /**
     * @param transactionalDatasource
     *            the transactionalDatasource to set
     */
    public void setTransactional(boolean transactionalDatasource) {
        this.transactionalDatasource = transactionalDatasource;
    }

    /**
     * @return the spatialFilteringProfileDatasource
     */
    public boolean isSpatialFilteringProfileDatasource() {
        return spatialFilteringProfileDatasource;
    }

    /**
     * @param spatialFilteringProfileDatasource
     *            the spatialFilteringProfileDatasource to set
     */
    public void setSpatialFilteringProfile(boolean spatialFilteringProfileDatasource) {
        this.spatialFilteringProfileDatasource = spatialFilteringProfileDatasource;
    }

    /**
     * Gets the qualified name of the driver class.
     * 
     * @return the driver class.
     */
    protected abstract String getDriverClass();

    protected abstract Map<String, Object> parseDatasourceProperties(Properties current);

    protected abstract void validatePrerequisites(Connection con, DatabaseMetadata metadata,
            Map<String, Object> settings);

    protected abstract Dialect createDialect();

    protected abstract Connection openConnection(Map<String, Object> settings) throws SQLException;

    /**
     * Check if drop schema contains alter table ... drop constraint ... . Due
     * to dynamic generation some constraints are generated and differ.
     * 
     * @param dropSchema
     *            Schema to check
     * @return Checked schema
     */
    protected abstract String[] checkDropSchema(String[] dropSchema);

    /**
     * Remove duplicated foreign key definition for table observationHasOffering
     * otherwise database model creation fails in Oracle
     * 
     * @param script
     *            Create and not checked script.
     * @return Checked script without duplicate foreign key for
     *         observationHasOffering
     */
    private String[] checkCreateSchema(String[] script) {
        // creates upper case hexString form table name 'observationHasOffering'
        // hashCode() with prefix 'FK'
        String hexStringToCheck =
                new StringBuilder("FK").append(Integer.toHexString("observationHasOffering".hashCode()).toUpperCase())
                        .toString();
        boolean duplicate = false;
        List<String> checkedSchema = Lists.newLinkedList();
        for (String string : script) {
            if (string.contains(hexStringToCheck)) {
                if (!duplicate) {
                    checkedSchema.add(string);
                    duplicate = true;
                }
            } else {
                checkedSchema.add(string);
            }
        }
        return checkedSchema.toArray(new String[checkedSchema.size()]);
    }
}
