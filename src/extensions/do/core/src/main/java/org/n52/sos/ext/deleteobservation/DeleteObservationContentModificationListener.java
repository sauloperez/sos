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

import java.util.Collections;
import java.util.Set;

import org.n52.sos.event.SosEvent;
import org.n52.sos.event.SosEventListener;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 1.0.0
 */
public class DeleteObservationContentModificationListener implements SosEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteObservationContentModificationListener.class);

    private static final Set<Class<? extends SosEvent>> TYPES = Collections
            .<Class<? extends SosEvent>> singleton(DeleteObservationEvent.class);

    @Override
    public Set<Class<? extends SosEvent>> getTypes() {
        return Collections.unmodifiableSet(TYPES);
    }

    @Override
    public void handle(SosEvent event) {
        if (event instanceof DeleteObservationEvent) {
            DeleteObservationEvent e = (DeleteObservationEvent) event;
            DeleteObservationCacheControllerUpdate update =
                    new DeleteObservationCacheControllerUpdate(e.getDeletedObservation());
            LOGGER.debug("Updating Cache after content modification: {}", update);
            try {
                Configurator.getInstance().getCacheController().update(update);
            } catch (OwsExceptionReport ex) {
                LOGGER.error("Error processing Event", ex);
            }
        } else {
            LOGGER.debug("Can not handle modification event: {}", event);
        }
    }
}
