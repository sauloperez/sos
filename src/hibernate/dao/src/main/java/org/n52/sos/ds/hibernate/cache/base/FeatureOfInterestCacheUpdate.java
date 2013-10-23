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
package org.n52.sos.ds.hibernate.cache.base;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.n52.sos.ds.hibernate.cache.AbstractDatasourceCacheUpdate;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.TFeatureOfInterest;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class FeatureOfInterestCacheUpdate extends AbstractDatasourceCacheUpdate {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureOfInterestCacheUpdate.class);

    @Override
    public void execute() {
        final ProcedureDAO procedureDAO = new ProcedureDAO();
        // FIXME shouldn't the identifiers be translated using
        // CacheHelper.addPrefixAndGetFeatureIdentifier()?
        for (final FeatureOfInterest featureOfInterest : new FeatureOfInterestDAO()
                .getFeatureOfInterestObjects(getSession())) {
            getCache().addFeatureOfInterest(featureOfInterest.getIdentifier());
            getCache().setProceduresForFeatureOfInterest(featureOfInterest.getIdentifier(),
                    procedureDAO.getProceduresForFeatureOfInterest(getSession(), featureOfInterest));
            if (featureOfInterest instanceof TFeatureOfInterest) {
                getCache().addParentFeatures(featureOfInterest.getIdentifier(),
                        getFeatureIdentifiers(((TFeatureOfInterest) featureOfInterest).getParents()));
            }
        }
        try {
            getCache().setGlobalEnvelope(
                    getFeatureQueryHandler()
                            .getEnvelopeForFeatureIDs(getCache().getFeaturesOfInterest(), getSession()));
        } catch (final OwsExceptionReport ex) {
            getErrors().add(ex);
        }
    }

    protected Set<String> getFeatureIdentifiers(final Collection<FeatureOfInterest> featuresOfInterest) {
        final Set<String> featureList = new HashSet<String>(featuresOfInterest.size());
        for (final FeatureOfInterest featureOfInterest : featuresOfInterest) {
            featureList.add(featureOfInterest.getIdentifier());
        }
        return featureList;
    }
}
