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

import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.util.procedure.HibernateProcedureConverter;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.AbstractSensorML;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.service.ProcedureDescriptionSettings;

import com.google.common.collect.Lists;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class HibernateProcedureEnrichment {
    private final HibernateProcedureConverter converter;

    public HibernateProcedureEnrichment(HibernateProcedureConverter converter) {
        this.converter = converter;
    }

    /**
     * Add optional values to procedure description like featureOfInterest
     * identifiers. Helpful for Sensor Web Client or to provide OGC DP Discovery
     * Profile.
     *
     * @param procedure     Procedure
     * @param description    Created procedure description
     * @param version        Requested SOS version
     * @param format         Requested format
     * @param procedureCache Loaded procedure hierarchy (passed to
     *                       recursive requests to avoid multiple
     *                       queries)
     * @param session        Hibernate session
     *
     * @throws OwsExceptionReport If an error occurs
     */
    public void enrich(Procedure procedure,
                       SosProcedureDescription description,
                       String version,
                       String format,
                       Map<String, Procedure> procedureCache,
                       Session session)
            throws OwsExceptionReport {
        ProcedureEnrichmentFactory factory =
                createFactory(procedure, version, description, format, session, procedureCache);

        List<ProcedureDescriptionEnrichment> enrichments = Lists.newLinkedList();
        enrichments.add(factory.createFeatureOfInterestEnrichment());
        enrichments.add(factory.createRelatedProceduresEnrichment());
        enrichments.add(factory.createOfferingEnrichment());
        
        // enrich according to OGC#09-033 Profile for sensor discovery
        if (enrichWithDiscoveryInformation(description)) {
            enrichments.add(factory.createBoundingBoxEnrichment());
            enrichments.add(factory.createClassifierEnrichment());
            enrichments.add(factory.createIdentificationEnrichment());
            enrichments.add(factory.createContactsEnrichment());
            enrichments.add(factory.createKeywordEnrichment());
        }
        
        for (ProcedureDescriptionEnrichment enrichment : enrichments) {
            enrichment.enrich();
        }
    }

    private ProcedureDescriptionSettings procedureSettings() {
        return ProcedureDescriptionSettings.getInstance();
    }

    private boolean enrichWithDiscoveryInformation(SosProcedureDescription description) {
        return description instanceof AbstractSensorML &&
               procedureSettings().isEnrichWithDiscoveryInformation();
    }

    private ProcedureEnrichmentFactory createFactory(Procedure procedure,
                                                     String version,
                                                     SosProcedureDescription description,
                                                     String format,
                                                     Session session,
                                                     Map<String, Procedure> procedureCache) {
        ProcedureEnrichmentFactory factory = new ProcedureEnrichmentFactory();
        factory.setIdentifier(procedure.getIdentifier());
        factory.setVersion(version);
        factory.setDescription(description);
        factory.setProcedureDescriptionFormat(format);
        factory.setSession(session);
        factory.setProcedureCache(procedureCache);
        factory.setConverter(converter);
        return factory;
    }
}
