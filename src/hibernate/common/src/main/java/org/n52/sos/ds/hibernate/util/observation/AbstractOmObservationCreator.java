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
package org.n52.sos.ds.hibernate.util.observation;

import java.util.List;

import org.hibernate.Session;
import org.n52.sos.cache.ContentCache;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.service.profile.Profile;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public abstract class AbstractOmObservationCreator {
    private final String version;
    private final Session session;

    public AbstractOmObservationCreator(String version, Session session) {
        this.version = version;
        this.session = session;
    }

    protected ContentCache getCache() {
        return Configurator.getInstance().getCache();
    }

    protected FeatureQueryHandler getFeatureQueryHandler() {
        return Configurator.getInstance().getFeatureQueryHandler();
    }

    protected Profile getActiveProfile() {
        return Configurator.getInstance().getProfileHandler().getActiveProfile();
    }

    protected String getTokenSeparator() {
        return ServiceConfiguration.getInstance().getTokenSeparator();
    }

    protected String getTupleSeparator() {
        return ServiceConfiguration.getInstance().getTupleSeparator();
    }

    protected String getNoDataValue() {
        return getActiveProfile().getResponseNoDataPlaceholder();
    }

    public abstract List<OmObservation> create() throws OwsExceptionReport,
                                                        ConverterException;

    public String getVersion() {
        return version;
    }

    public Session getSession() {
        return session;
    }
}
