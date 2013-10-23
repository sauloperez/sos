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

import java.util.Collection;

import org.n52.sos.cache.ContentCache;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.AbstractSensorML;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sensorML.elements.SmlIdentifier;
import org.n52.sos.ogc.sensorML.elements.SmlIdentifierPredicates;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.ProcedureDescriptionSettings;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public abstract class ProcedureDescriptionEnrichment {
    private SosProcedureDescription description;
    private String version;
    private String identifier;
    
    public abstract void enrich() throws OwsExceptionReport;

    protected ProcedureDescriptionSettings procedureSettings() {
        return ProcedureDescriptionSettings.getInstance();
    }

    protected ContentCache getCache() {
        return Configurator.getInstance().getCache();
    }

    protected Predicate<SmlIdentifier> longNamePredicate() {
        return SmlIdentifierPredicates.nameOrDefinition(
                SensorMLConstants.ELEMENT_NAME_LONG_NAME,
                procedureSettings().getIdentifierLongNameDefinition());
    }

    protected Predicate<SmlIdentifier> shortNamePredicate() {
        return SmlIdentifierPredicates.nameOrDefinition(
                SensorMLConstants.ELEMENT_NAME_SHORT_NAME,
                procedureSettings().getIdentifierShortNameDefinition());
    }

    protected Predicate<SmlIdentifier> uniqueIdPredicate() {
        return SmlIdentifierPredicates.nameOrDefinition(
                OGCConstants.URN_UNIQUE_IDENTIFIER_END,
                OGCConstants.URN_UNIQUE_IDENTIFIER);
    }

    protected Collection<SosOffering> getSosOfferings() {
        Collection<String> identifiers = getCache().getOfferingsForProcedure(getIdentifier());
        Collection<SosOffering> offerings = Lists.newArrayListWithCapacity(identifiers.size());
        for (String offering : identifiers) {
            offerings.add(new SosOffering(offering, getCache().getNameForOffering(offering)));
        }
        return offerings;
    }

    public SosProcedureDescription getDescription() {
        return description;
    }

    public void setDescription(SosProcedureDescription description) {
        this.description = Preconditions.checkNotNull(description);
    }

    public AbstractSensorML getSensorML() {
        return (AbstractSensorML) description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = Preconditions.checkNotNull(version);
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = Preconditions.checkNotNull(identifier);
    }
}
