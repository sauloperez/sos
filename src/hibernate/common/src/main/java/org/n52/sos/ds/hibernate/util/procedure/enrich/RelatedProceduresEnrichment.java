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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.util.procedure.HibernateProcedureConverter;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.util.CollectionHelper;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class RelatedProceduresEnrichment extends ProcedureDescriptionEnrichment {
    private final String procedureDescriptionFormat;
    private final Session session;
    private final HibernateProcedureConverter converter;
    private Map<String, Procedure> procedureCache;

    public RelatedProceduresEnrichment(String procedureDescriptionFormat,
                                       Map<String, Procedure> procedureCache,
                                       Session session,
                                       HibernateProcedureConverter converter) {
        this.procedureDescriptionFormat = Preconditions
                .checkNotNull(procedureDescriptionFormat);
        this.procedureCache = procedureCache;
        this.session = Preconditions.checkNotNull(session);
        this.converter = Preconditions.checkNotNull(converter);
    }

    

    @Override
    public void enrich() throws OwsExceptionReport {
        Set<String> parentProcedures = getParentProcedures();
        if (CollectionHelper.isNotEmpty(parentProcedures)) {
            getDescription().addParentProcedures(parentProcedures);
        }
        Set<SosProcedureDescription> childProcedures = getChildProcedures();
        if (CollectionHelper.isNotEmpty(childProcedures)) {
            getDescription().addChildProcedures(childProcedures);
        }
    }

    /**
     * Add a collection of child procedures to a procedure
     *
     * @param procedure
     *            Parent procedure identifier
     * @param outputFormat
     *            Procedure description format
     * @param version
     *            Service version
     * @param cache
     *            Loaded procedure map
     * @param session
     *            Hibernate session
     * @return Set with child procedure descriptions
     * @throws OwsExceptionReport
     *             If an error occurs
     * @throws ConverterException
     *             If creation of child procedure description fails
     */
    private Set<SosProcedureDescription> getChildProcedures()
            throws OwsExceptionReport {

        final Collection<String> childIdentfiers =
                getCache().getChildProcedures(getIdentifier(), false, false);

        if (CollectionHelper.isEmpty(childIdentfiers)) {
            return Sets.newHashSet();
        }

        if (procedureCache == null) {
            procedureCache = createProcedureCache();
        }

        Set<SosProcedureDescription> childProcedures = Sets.newHashSet();
        for (String childId : childIdentfiers) {
            Procedure child = procedureCache.get(childId);
            SosProcedureDescription childDescription = converter.createSosProcedureDescription(
                    child, procedureDescriptionFormat, getVersion(), procedureCache, session);
            // TODO check if call is necessary because it is also called in
            // createSosProcedureDescription()
            // addValuesToSensorDescription(childProcID,childProcedureDescription,
            // version, outputFormat, session);
            childProcedures.add(childDescription);
        }
        return childProcedures;
    }

    private Map<String, Procedure> createProcedureCache() {
        Set<String> identifiers = getCache().getChildProcedures(getIdentifier(), true, false);
        List<Procedure> children = new ProcedureDAO().getProceduresForIdentifiers(identifiers, session);
        Map<String, Procedure> cache = Maps.newHashMapWithExpectedSize(children.size());
        for (Procedure child : children) {
            cache.put(child.getIdentifier(), child);
        }
        return cache;
    }

     /**
     * Add parent procedures to a procedure
     *
     * @param procID
     *            procedure identifier to add parent procedures to
     *
     * @throws OwsExceptionReport
     */
    private Set<String> getParentProcedures() throws OwsExceptionReport {
        return getCache().getParentProcedures(getIdentifier(), false, false);
    }
}
