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
package org.n52.sos.config;

import java.io.File;
import java.net.URI;
import java.util.Set;

import org.n52.sos.binding.BindingKey;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.encode.ProcedureDescriptionFormatKey;
import org.n52.sos.encode.ResponseFormatKey;
import org.n52.sos.request.operator.RequestOperatorKey;

/**
 * @since 4.0.0
 * 
 */
public class SettingsManagerForTesting extends AbstractSettingsManager {

    public static final SettingValueFactory SETTING_FACTORY = new SettingFactoryForTesting();

    @Override
    protected Set<SettingValue<?>> getSettingValues() throws ConnectionProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected SettingValue<?> getSettingValue(String key) throws ConnectionProviderException {
        return null;
    }

    @Override
    protected void deleteSettingValue(String key) throws ConnectionProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    protected void saveSettingValue(SettingValue<?> setting) throws ConnectionProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    protected void setOperationStatus(RequestOperatorKey requestOperatorKeyType, boolean active)
            throws ConnectionProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    protected void setResponseFormatStatus(ResponseFormatKey rfkt, boolean active) throws ConnectionProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    protected void setProcedureDescriptionFormatStatus(String pdf, boolean active) throws ConnectionProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    protected void setBindingStatus(BindingKey bk, boolean active) throws ConnectionProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    public SettingValueFactory getSettingFactory() {
        return SETTING_FACTORY;
    }

    @Override
    public Set<AdministratorUser> getAdminUsers() throws ConnectionProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AdministratorUser getAdminUser(String username) throws ConnectionProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AdministratorUser createAdminUser(String username, String password) throws ConnectionProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void saveAdminUser(AdministratorUser user) throws ConnectionProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAdminUser(String username) throws ConnectionProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAll() throws ConnectionProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    public void cleanup() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isActive(RequestOperatorKey rokt) throws ConnectionProviderException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isActive(ResponseFormatKey rfkt) throws ConnectionProviderException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isActive(BindingKey bk) throws ConnectionProviderException {
        // TODO Auto-generated method stub
        return false;
    }

    private static class SettingFactoryForTesting extends AbstractSettingValueFactory {

        @Override
        protected SettingValue<Boolean> newBooleanSettingValue() {
            return new BooleanSettingValueForTesting();
        }

        @Override
        protected SettingValue<Integer> newIntegerSettingValue() {
            return new IntegerSettingValueForTesting();
        }

        @Override
        protected SettingValue<String> newStringSettingValue() {
            return new StringSettingValueForTesting();
        }

        @Override
        protected SettingValue<File> newFileSettingValue() {
            return new FileSettingValueForTesting();
        }

        @Override
        protected SettingValue<URI> newUriSettingValue() {
            return new UriSettingValueForTesting();
        }

        @Override
        protected SettingValue<Double> newNumericSettingValue() {
            return new NumericSettingValueForTesting();
        }
    }

    @Override
    protected void setProcedureDescriptionFormatStatus(ProcedureDescriptionFormatKey pdfkt, boolean active)
            throws ConnectionProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isActive(ProcedureDescriptionFormatKey pdfkt) throws ConnectionProviderException {
        // TODO Auto-generated method stub
        return false;
    }

}
