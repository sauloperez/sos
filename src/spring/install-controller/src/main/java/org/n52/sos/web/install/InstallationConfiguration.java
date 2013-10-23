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
package org.n52.sos.web.install;

import java.util.HashMap;
import java.util.Map;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingValue;
import org.n52.sos.ds.Datasource;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class InstallationConfiguration {

    private Map<SettingDefinition<?, ?>, SettingValue<?>> settings =
            new HashMap<SettingDefinition<?, ?>, SettingValue<?>>();

    private Map<String, Object> databaseSettings = new HashMap<String, Object>();

    private String username;

    private String password;

    private Datasource datasource;

    private boolean createSchema = false;

    private boolean dropSchema = false;

    public InstallationConfiguration() {
    }

    public Map<SettingDefinition<?, ?>, SettingValue<?>> getSettings() {
        return settings;
    }

    public InstallationConfiguration setSettings(Map<SettingDefinition<?, ?>, SettingValue<?>> settings) {
        this.settings = settings;
        return this;
    }

    public Map<String, Object> getDatabaseSettings() {
        return databaseSettings;
    }

    public Datasource getDatasource() {
        return datasource;
    }

    public void setDatasource(Datasource datasource) {
        this.datasource = datasource;
    }

    public InstallationConfiguration setDatabaseSettings(Map<String, Object> databaseSettings) {
        this.databaseSettings = databaseSettings;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public InstallationConfiguration setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public InstallationConfiguration setPassword(String password) {
        this.password = password;
        return this;
    }

    public Object getDatabaseSetting(String k) {
        return databaseSettings.get(k);
    }

    public boolean getBooleanDatabaseSetting(String k) {
        return ((Boolean) getDatabaseSetting(k)).booleanValue();
    }

    public boolean hasDatabaseSetting(String k) {
        return databaseSettings.containsKey(k);
    }

    public InstallationConfiguration setDatabaseSetting(String k, Object v) {
        databaseSettings.put(k, v);
        return this;
    }

    public SettingValue<?> getSetting(SettingDefinition<?, ?> k) {
        return settings.get(k);
    }

    public InstallationConfiguration setSetting(SettingDefinition<?, ?> k, SettingValue<?> v) {
        settings.put(k, v);
        return this;
    }

    public boolean isCreateSchema() {
        return createSchema;
    }

    public void setCreateSchema(boolean createSchema) {
        this.createSchema = createSchema;
    }

    public boolean isDropSchema() {
        return dropSchema;
    }

    public void setDropSchema(boolean dropSchema) {
        this.dropSchema = dropSchema;
    }
}
