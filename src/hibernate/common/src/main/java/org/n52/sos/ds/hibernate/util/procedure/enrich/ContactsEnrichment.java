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


import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.SosServiceProvider;
import org.n52.sos.ogc.sensorML.SmlResponsibleParty;
import org.n52.sos.service.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class ContactsEnrichment extends ProcedureDescriptionEnrichment {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContactsEnrichment.class);

    @Override
    public void enrich() throws OwsExceptionReport {
        // set contacts --> take from service information
        if (procedureSettings().isUseServiceContactAsProcedureContact() &&
            !getSensorML().isSetContact()) {
            Optional<SmlResponsibleParty> contact = createContactFromServiceContact();
            if (contact.isPresent()) {
                getSensorML().addContact(contact.get());
            }
        }
    }

    /**
     * Get SerivceProvider object,
     *
     * @return SerivceProvider object
     */
    @VisibleForTesting
    Optional<SosServiceProvider> getServiceProvider() {
        try {
            return Optional.fromNullable(Configurator.getInstance().getServiceProvider());
        } catch (final OwsExceptionReport e) {
            LOGGER.error(String.format("Exception thrown: %s", e.getMessage()), e);
            return Optional.absent();
        }
    }

    /**
     * Create SensorML Contact form service contact informations
     *
     * @return SensorML Contact
     */
    private Optional<SmlResponsibleParty> createContactFromServiceContact() {
        Optional<SosServiceProvider> serviceProvider = getServiceProvider();
        if (!serviceProvider.isPresent()) {
            return Optional.absent();
        }
        SmlResponsibleParty rp = new SmlResponsibleParty();
        SosServiceProvider sp = serviceProvider.get();
        if (sp.hasIndividualName()) {
            rp.setIndividualName(sp.getIndividualName());
        }
        if (sp.hasName()) {
            rp.setOrganizationName(sp.getName());
        }
        if (sp.hasSite()) {
            rp.addOnlineResource(sp.getSite());
        }
        if (sp.hasPositionName()) {
            rp.setPositionName(sp.getPositionName());
        }
        if (sp.hasDeliveryPoint()) {
            rp.addDeliveryPoint(sp.getDeliveryPoint());
        }
        if (sp.hasPhone()) {
            rp.addPhoneVoice(sp.getPhone());
        }
        if (sp.hasCity()) {
            rp.setCity(sp.getCity());
        }
        if (sp.hasCountry()) {
            rp.setCountry(sp.getCountry());
        }
        if (sp.hasPostalCode()) {
            rp.setPostalCode(sp.getPostalCode());
        }
        if (sp.hasMailAddress()) {
            rp.setEmail(sp.getMailAddress());
        }
        return Optional.of(rp);
    }
}
