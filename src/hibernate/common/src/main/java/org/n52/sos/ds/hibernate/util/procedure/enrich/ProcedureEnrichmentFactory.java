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

import java.util.Map;

import org.hibernate.Session;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.util.procedure.HibernateProcedureConverter;
import org.n52.sos.ogc.sos.SosProcedureDescription;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class ProcedureEnrichmentFactory {
    private String identifier;
    private SosProcedureDescription description;
    private String version;
    private String procedureDescriptionFormat;
    private Map<String, Procedure> procedureCache;
    private Session session;
    private HibernateProcedureConverter converter;

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setDescription(SosProcedureDescription description) {
        this.description = description;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setProcedureDescriptionFormat(String pdf) {
        this.procedureDescriptionFormat = pdf;
    }

    public void setProcedureCache(
            Map<String, Procedure> procedureCache) {
        this.procedureCache = procedureCache;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void setConverter(HibernateProcedureConverter converter) {
        this.converter = converter;
    }

    public ProcedureDescriptionEnrichment createBoundingBoxEnrichment() {
        return setValues(new BoundingBoxEnrichment());
    }

    public ProcedureDescriptionEnrichment createClassifierEnrichment() {
        return setValues(new ClassifierEnrichment());
    }

    public ProcedureDescriptionEnrichment createIdentificationEnrichment() {
        return setValues(new IdentificationEnrichment());
    }

    public ProcedureDescriptionEnrichment createContactsEnrichment() {
        return setValues(new ContactsEnrichment());
    }

    public ProcedureDescriptionEnrichment createKeywordEnrichment() {
        return setValues(new KeywordEnrichment());
    }

    public ProcedureDescriptionEnrichment createFeatureOfInterestEnrichment() {
        return setValues(new FeatureOfInterestEnrichment());
    }

    public ProcedureDescriptionEnrichment createRelatedProceduresEnrichment() {
        return setValues(new RelatedProceduresEnrichment(procedureDescriptionFormat, procedureCache, session, converter));
    }

    public ProcedureDescriptionEnrichment createOfferingEnrichment() {
        return setValues(new OfferingEnrichment());
    }

    private ProcedureDescriptionEnrichment setValues(
            ProcedureDescriptionEnrichment enrichment) {
        enrichment.setDescription(description);
        enrichment.setIdentifier(identifier);
        enrichment.setVersion(version);
        return enrichment;
    }
}
