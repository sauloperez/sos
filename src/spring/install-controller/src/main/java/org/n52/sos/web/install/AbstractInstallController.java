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

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingValue;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.ds.TestDataManagerRepository;
import org.n52.sos.web.AbstractController;
import org.n52.sos.web.ControllerConstants;
import org.n52.sos.web.install.InstallConstants.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @since 4.0.0
 * 
 */
@Controller
public abstract class AbstractInstallController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractInstallController.class);

    private static final String INSTALLATION_CONFIGURATION = "installation_configuration";

    private static final String SUPPORTS_TEST_DATA = "supportsTestData";

    public static InstallationConfiguration getSettings(HttpSession s) {
        InstallationConfiguration c = (InstallationConfiguration) s.getAttribute(INSTALLATION_CONFIGURATION);
        if (c == null) {
            c = new InstallationConfiguration();
            // try to read default settings from existing configuration
            try {
                c.setSettings(SettingsManager.getInstance().getSettings());
                // remove null values (in case new settings have been added
                // since configuration was generated)
                Iterator<Entry<SettingDefinition<?, ?>, SettingValue<?>>> iterator =
                        c.getSettings().entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<SettingDefinition<?, ?>, SettingValue<?>> setting = iterator.next();
                    if (setting.getValue() == null) {
                        iterator.remove();
                    }
                }
            } catch (Exception ex) {
                LOG.warn("Couldn't read existing settings", ex);
            }
            setSettings(s, c);
        }
        return c;
    }

    public static void setSettings(HttpSession session, InstallationConfiguration settings) {
        session.setAttribute(INSTALLATION_CONFIGURATION, settings);
    }

    protected Map<String, Object> toModel(InstallationConfiguration c) {
        Map<String, Object> model = new HashMap<String, Object>(4);
        model.put(ControllerConstants.SETTINGS_MODEL_ATTRIBUTE, toSimpleMap(c.getSettings()));
        model.put(ControllerConstants.DATABASE_SETTINGS_MODEL_ATTRIBUTE, c.getDatabaseSettings());
        model.put(ControllerConstants.ADMIN_USERNAME_REQUEST_PARAMETER, c.getUsername());
        model.put(ControllerConstants.ADMIN_PASSWORD_REQUEST_PARAMETER, c.getPassword());

        boolean supportsTestData = false;
        TestDataManagerRepository testDataManagerRepository = TestDataManagerRepository.getInstance();
        if (testDataManagerRepository.hasTestDataManager()) {
            supportsTestData = testDataManagerRepository.getFirstTestDataManager().supportsInsertTestData();
        }
        model.put(SUPPORTS_TEST_DATA, supportsTestData);
        return model;
    }

    protected Map<String, String> getParameters(HttpServletRequest req) {
        Map<String, String> parameters = new HashMap<String, String>();
        Enumeration<?> e = req.getParameterNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            parameters.put(key, req.getParameter(key));
        }
        return parameters;
    }

    protected ModelAndView redirect(String path) {
        return new ModelAndView(new RedirectView(path, true));
    }

    protected void setComplete(HttpSession session) {
        session.setAttribute(getStep().getCompletionAttribute(), true);
    }

    protected HttpSession checkPrevious(HttpServletRequest req) throws InstallationRedirectError {
        HttpSession session = req.getSession(true);
        checkPrevious(session);
        return session;
    }

    private void checkPrevious(HttpSession session) throws InstallationRedirectError {
        for (Step step : reverseSteps(getStep())) {
            if (!isComplete(session, step)) {
                throw new InstallationRedirectError(step.getPath());
            }
        }
    }

    private Step[] reverseSteps(Step step) {
        return Arrays.copyOfRange(Step.values(), 0, step.ordinal());
    }

    private boolean isComplete(HttpSession session, Step step) {
        return session.getAttribute(step.getCompletionAttribute()) != null;
    }

    @ExceptionHandler(InstallationRedirectError.class)
    public ModelAndView onError(InstallationRedirectError e) {
        return redirect(e.getPath());
    }

    @ExceptionHandler(InstallationSettingsError.class)
    public ModelAndView onError(HttpSession session, InstallationSettingsError e) {
        if (e.getCause() != null) {
            LOG.error(e.getMessage(), e.getCause());
        } else {
            LOG.error(e.getMessage());
        }
        setSettings(session, e.getSettings());
        Map<String, Object> model = toModel(e.getSettings());
        model.put(ControllerConstants.ERROR_MODEL_ATTRIBUTE, e.getMessage());
        return new ModelAndView(getStep().getView(), model);
    }

    protected abstract Step getStep();
}
