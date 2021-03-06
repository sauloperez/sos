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

package org.n52.sos.ext.deleteobservation;

import static org.n52.sos.util.ConfiguringSingletonServiceLoader.loadAndConfigure;

import org.n52.sos.cache.ContentCacheUpdate;
import org.n52.sos.exception.ows.concrete.NoImplementationFoundException;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 1.0.0
 */
public class DeleteObservationCacheControllerUpdate extends ContentCacheUpdate {
    private final OmObservation o;

    private DeleteObservationCacheFeederDAO cacheFeederDAO;

    public DeleteObservationCacheControllerUpdate(OmObservation o) {
        this.o = o;
    }

    protected DeleteObservationCacheFeederDAO getDao() throws NoImplementationFoundException {
        if (cacheFeederDAO == null) {
            cacheFeederDAO = loadAndConfigure(DeleteObservationCacheFeederDAO.class, false);
            if (cacheFeederDAO == null) {
                throw new NoImplementationFoundException(DeleteObservationCacheFeederDAO.class);
            }
        }
        return cacheFeederDAO;
    }

    @Override
    public void execute() {
        try {
            CompositeOwsException errors = new CompositeOwsException();
            getDao().setErrors(errors);
            getDao().setCache(getCache());
            getDao().setDeletedObservation(o);
            getDao().execute();
            errors.throwIfNotEmpty();
        } catch (OwsExceptionReport e) {
            fail(e);
        }
    }
}
