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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.settings.IntegerSettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.n52.sos.util.JavaHelper;

import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 * 
 */
public abstract class AbstractHibernateFullDBDatasource extends AbstractHibernateDatasource {
    private String usernameDefault, usernameDescription;

    private String passwordDefault, passwordDescription;

    private String databaseDefault, databaseDescription;

    private String hostDefault, hostDescription;

    private int portDefault;

    private String portDescription;

    private String schemaDefault, schemaDescription;

    private int minPoolSizeDefault, maxPoolSizeDefault;

    private boolean providedJdbc;

    AbstractHibernateFullDBDatasource() {
        super();
        setMinPoolSizeDefault(MIN_POOL_SIZE_DEFAULT_VALUE);
        setMaxPoolSizeDefault(MAX_POOL_SIZE_DEFAULT_VALUE);
    }

    @Deprecated
    public AbstractHibernateFullDBDatasource(String usernameDefault, String usernameDescription,
            String passwordDefault, String passwordDescription, String databaseDefault, String databaseDescription,
            String hostDefault, String hostDescription, int portDefault, String portDescription, String schemaDefault,
            String schemaDescription) {
        super();
    }

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        Set<SettingDefinition<?, ?>> set =
                Sets.<SettingDefinition<?, ?>> newHashSet(createUsernameDefinition(usernameDefault),
                        createPasswordDefinition(passwordDefault), createDatabaseDefinition(databaseDefault),
                        createHostDefinition(hostDefault), createPortDefinition(portDefault),
                        createSchemaDefinition(schemaDefault), createMinPoolSizeDefinition(minPoolSizeDefault),
                        createMaxPoolSizeDefinition(maxPoolSizeDefault),
                        createProvidedJdbcDriverDefinition(providedJdbc));
        if (isTransactionalDatasource()) {
            set.add(getTransactionalDefiniton());
        }
        if (isSpatialFilteringProfileDatasource()) {
            set.add(getSpatialFilteringProfileDefiniton());
        }
        return set;
    }

    @Override
    public Set<SettingDefinition<?, ?>> getChangableSettingDefinitions(Properties current) {
        Map<String, Object> settings = parseDatasourceProperties(current);
        return Sets.<SettingDefinition<?, ?>> newHashSet(
                createUsernameDefinition((String) settings.get(USERNAME_KEY)),
                createPasswordDefinition((String) settings.get(PASSWORD_KEY)),
                createDatabaseDefinition((String) settings.get(DATABASE_KEY)),
                createHostDefinition((String) settings.get(HOST_KEY)),
                createPortDefinition(JavaHelper.asInteger(settings.get(PORT_KEY))),
                createSchemaDefinition((String) settings.get(SCHEMA_KEY)),
                createMinPoolSizeDefinition(JavaHelper.asInteger(settings.get(MIN_POOL_SIZE_KEY))),
                createMaxPoolSizeDefinition(JavaHelper.asInteger(settings.get(MAX_POOL_SIZE_KEY))));
    }

    protected StringSettingDefinition createUsernameDefinition(String defaultValue) {
        return createUsernameDefinition().setDescription(usernameDescription).setDefaultValue(defaultValue);
    }

    protected StringSettingDefinition createPasswordDefinition(String defaultValue) {
        return createPasswordDefinition().setDescription(passwordDescription).setDefaultValue(defaultValue);
    }

    protected StringSettingDefinition createDatabaseDefinition(String defaultValue) {
        return createDatabaseDefinition().setDescription(databaseDescription).setDefaultValue(defaultValue);
    }

    protected StringSettingDefinition createHostDefinition(String defaultValue) {
        return createHostDefinition().setDescription(hostDescription).setDefaultValue(defaultValue);
    }

    protected IntegerSettingDefinition createPortDefinition(int defaultValue) {
        return createPortDefinition().setDescription(portDescription).setDefaultValue(defaultValue);
    }

    protected StringSettingDefinition createSchemaDefinition(String defaultValue) {
        return createSchemaDefinition().setDescription(schemaDescription).setDefaultValue(defaultValue);
    }

    protected SettingDefinition<?, ?> createMinPoolSizeDefinition(Integer defaultValue) {
        return createMinPoolSizeDefinition().setDefaultValue(defaultValue);
    }

    protected SettingDefinition<?, ?> createMaxPoolSizeDefinition(Integer defaultValue) {
        return createMaxPoolSizeDefinition().setDefaultValue(defaultValue);
    }

    protected SettingDefinition<?, ?> createProvidedJdbcDriverDefinition(Boolean defaultValue) {
        return createProvidedJdbcDriverDefinition().setDefaultValue(defaultValue);
    }

    @Override
    public Properties getDatasourceProperties(Map<String, Object> settings) {
        Properties p = new Properties();
        p.put(HibernateConstants.DEFAULT_SCHEMA, settings.get(SCHEMA_KEY));
        p.put(HibernateConstants.CONNECTION_USERNAME, settings.get(USERNAME_KEY));
        p.put(HibernateConstants.CONNECTION_PASSWORD, settings.get(PASSWORD_KEY));
        p.put(HibernateConstants.CONNECTION_URL, toURL(settings));
        p.put(HibernateConstants.CONNECTION_PROVIDER_CLASS, C3P0_CONNECTION_POOL);
        p.put(HibernateConstants.DIALECT, getDialectClass());
        p.put(HibernateConstants.DRIVER_CLASS, getDriverClass());
        p.put(HibernateConstants.C3P0_MIN_SIZE, settings.get(MIN_POOL_SIZE_KEY).toString());
        p.put(HibernateConstants.C3P0_MAX_SIZE, settings.get(MAX_POOL_SIZE_KEY).toString());
        p.put(HibernateConstants.C3P0_IDLE_TEST_PERIOD, "1");
        p.put(HibernateConstants.C3P0_ACQUIRE_INCREMENT, "1");
        p.put(HibernateConstants.C3P0_TIMEOUT, "0");
        p.put(HibernateConstants.C3P0_MAX_STATEMENTS, "0");
        p.put(HibernateConstants.CONNECTION_AUTO_RECONNECT, "true");
        p.put(HibernateConstants.CONNECTION_AUTO_RECONNECT_FOR_POOLS, "true");
        p.put(HibernateConstants.CONNECTION_TEST_ON_BORROW, "true");
        p.put(PROVIDED_JDBC, settings.get(PROVIDED_JDBC_DRIVER_KEY).toString());
        addMappingFileDirectories(settings, p);

        return p;
    }

    @Override
    protected Map<String, Object> parseDatasourceProperties(Properties current) {
        Map<String, Object> settings = new HashMap<String, Object>(current.size());
        settings.put(SCHEMA_KEY, current.getProperty(HibernateConstants.DEFAULT_SCHEMA));
        settings.put(USERNAME_KEY, current.getProperty(HibernateConstants.CONNECTION_USERNAME));
        settings.put(PASSWORD_KEY, current.getProperty(HibernateConstants.CONNECTION_PASSWORD));
        settings.put(MIN_POOL_SIZE_KEY, current.getProperty(HibernateConstants.C3P0_MIN_SIZE));
        settings.put(MAX_POOL_SIZE_KEY, current.getProperty(HibernateConstants.C3P0_MAX_SIZE));
        settings.put(TRANSACTIONAL_KEY, isTransactional(current));
        settings.put(PROVIDED_JDBC_DRIVER_KEY,
                current.getProperty(PROVIDED_JDBC, PROVIDED_JDBC_DRIVER_DEFAULT_VALUE.toString()));
        String url = current.getProperty(HibernateConstants.CONNECTION_URL);

        String[] parsed = parseURL(url);
        String host = parsed[0];
        String port = parsed[1];
        String db = parsed[2];

        settings.put(createHostDefinition().getKey(), host);
        settings.put(createPortDefinition().getKey(), JavaHelper.asInteger(port));
        settings.put(createDatabaseDefinition().getKey(), db);
        return settings;
    }

    private String getDialectClass() {
        return createDialect().getClass().getCanonicalName();
    }

    /**
     * Converts the given connection settings into a valid JDBC string.
     * 
     * @param settings
     *            the connection settings, containing keys from
     *            {@link AbstractHibernateDatasource} (<code>HOST_KEY</code>,
     *            <code>PORT_KEY</code>, ...).
     * @return a valid JDBC connection string
     */
    protected abstract String toURL(Map<String, Object> settings);

    /**
     * Parses the given JDBC string searching for host, port and database
     * 
     * @param url
     *            the JDBC string to parse
     * @return an array with three strings:
     *         <ul>
     *         <li>[0] - Host
     *         <li>[1] - Port (parseable int as string)
     *         <li>[2] - Database
     *         </ul>
     */
    protected abstract String[] parseURL(String url);

    /**
     * @param usernameDefault
     *            the usernameDefault to set
     */
    public void setUsernameDefault(String usernameDefault) {
        this.usernameDefault = usernameDefault;
    }

    /**
     * @param usernameDescription
     *            the usernameDescription to set
     */
    public void setUsernameDescription(String usernameDescription) {
        this.usernameDescription = usernameDescription;
    }

    /**
     * @param passwordDefault
     *            the passwordDefault to set
     */
    public void setPasswordDefault(String passwordDefault) {
        this.passwordDefault = passwordDefault;
    }

    /**
     * @param passwordDescription
     *            the passwordDescription to set
     */
    public void setPasswordDescription(String passwordDescription) {
        this.passwordDescription = passwordDescription;
    }

    /**
     * @param databaseDefault
     *            the databaseDefault to set
     */
    public void setDatabaseDefault(String databaseDefault) {
        this.databaseDefault = databaseDefault;
    }

    /**
     * @param databaseDescription
     *            the databaseDescription to set
     */
    public void setDatabaseDescription(String databaseDescription) {
        this.databaseDescription = databaseDescription;
    }

    /**
     * @param hostDefault
     *            the hostDefault to set
     */
    public void setHostDefault(String hostDefault) {
        this.hostDefault = hostDefault;
    }

    /**
     * @param hostDescription
     *            the hostDescription to set
     */
    public void setHostDescription(String hostDescription) {
        this.hostDescription = hostDescription;
    }

    /**
     * @param portDefault
     *            the portDefault to set
     */
    public void setPortDefault(int portDefault) {
        this.portDefault = portDefault;
    }

    /**
     * @param portDescription
     *            the portDescription to set
     */
    public void setPortDescription(String portDescription) {
        this.portDescription = portDescription;
    }

    /**
     * @param schemaDefault
     *            the schemaDefault to set
     */
    public void setSchemaDefault(String schemaDefault) {
        this.schemaDefault = schemaDefault;
    }

    /**
     * @param schemaDescription
     *            the schemaDescription to set
     */
    public void setSchemaDescription(String schemaDescription) {
        this.schemaDescription = schemaDescription;
    }

    public void setProvidedJdbcDefault(boolean providedJdbc) {
        this.providedJdbc = providedJdbc;
    }

    public void setMinPoolSizeDefault(int minPoolSizeDefault) {
        this.minPoolSizeDefault = minPoolSizeDefault;
    }

    public void setMaxPoolSizeDefault(int maxPoolSizeDefault) {
        this.maxPoolSizeDefault = maxPoolSizeDefault;
    }
}
