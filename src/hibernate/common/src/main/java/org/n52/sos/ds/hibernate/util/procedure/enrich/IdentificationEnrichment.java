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
package org.n52.sos.ds.hibernate.util.procedure.enrich;

import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sensorML.elements.SmlIdentifier;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class IdentificationEnrichment extends ProcedureDescriptionEnrichment {
    @Override
    public void enrich() throws OwsExceptionReport {
        enrichUniqueId();
        enrichShortName();
        enrichLongName();
    }

    private void enrichUniqueId() {
        if (!getSensorML().findIdentification(uniqueIdPredicate()).isPresent()) {
            getSensorML().addIdentifier(createUniqueId());
        }
    }

    private void enrichShortName() {
        if (!getSensorML().findIdentification(shortNamePredicate()).isPresent()) {
            getSensorML().addIdentifier(createShortName());
        }
    }

    private void enrichLongName() {
        if (!getSensorML().findIdentification(longNamePredicate()).isPresent()) {
            getSensorML().addIdentifier(createLongName());
        }
    }

    private SmlIdentifier createUniqueId() {
        return new SmlIdentifier(OGCConstants.URN_UNIQUE_IDENTIFIER_END,
                                 OGCConstants.URN_UNIQUE_IDENTIFIER,
                                 getIdentifier());
    }

    private SmlIdentifier createLongName() {
        return new SmlIdentifier(SensorMLConstants.ELEMENT_NAME_LONG_NAME,
                                 procedureSettings().getIdentifierLongNameDefinition(),
                                 getIdentifier());
    }

    private SmlIdentifier createShortName() {
        return new SmlIdentifier(SensorMLConstants.ELEMENT_NAME_SHORT_NAME,
                                 procedureSettings().getIdentifierShortNameDefinition(),
                                 getIdentifier());
    }
}
