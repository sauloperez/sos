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
package org.n52.sos.cache.ctrl.action;

import java.util.Collection;

import org.n52.sos.cache.WritableContentCache;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.swes.SwesFeatureRelationship;
import org.n52.sos.request.InsertSensorRequest;
import org.n52.sos.response.InsertSensorResponse;
import org.n52.sos.util.Action;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * When executing this &auml;ction (see {@link Action}), the following relations
 * are added and some settings are updated in cache:
 * <ul>
 * <li>Procedure</li>
 * <li>Procedure &harr; parent procedures</li>
 * <li>Offering &harr; procedure</li>
 * <li>Offering &rarr; name</li>
 * </ul>
 * <li>Offering &rarr; allowed observation type</li> <li>Offering &rarr; related
 * feature</li> <li>Related features &rarr; role</li> <li>Observable Property
 * &harr; Procedure</li> <li>Offering &harr; observable property</li>
 * 
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.0.0
 * 
 */
public class SensorInsertionUpdate extends InMemoryCacheUpdate {
    private static final Logger LOGGER = LoggerFactory.getLogger(SensorInsertionUpdate.class);

    private final InsertSensorResponse response;

    private final InsertSensorRequest request;

    public SensorInsertionUpdate(InsertSensorRequest request, InsertSensorResponse response) {
        if (request == null || response == null) {
            String msg =
                    String.format("Missing argument: '%s': %s; '%s': %s", InsertSensorRequest.class.getName(),
                            request, InsertSensorResponse.class.getName(), response);
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
        this.response = response;
        this.request = request;
    }

    @Override
    public void execute() {
        final WritableContentCache cache = getCache();
        final String procedure = response.getAssignedProcedure();

        // procedure relations
        cache.addProcedure(procedure);
        if (request.getProcedureDescription().isSetParentProcedures()) {
            cache.addParentProcedures(procedure, request.getProcedureDescription().getParentProcedures());
        }
        // TODO child procedures

        // offerings
        for (SosOffering sosOffering : request.getAssignedOfferings()) {
            if (sosOffering.isParentOffering()) {
                cache.addHiddenChildProcedureForOffering(sosOffering.getOfferingIdentifier(), procedure);
            } else {
                cache.addOffering(sosOffering.getOfferingIdentifier());
                cache.addProcedureForOffering(sosOffering.getOfferingIdentifier(), procedure);
                if (sosOffering.isSetOfferingName()) {
                    cache.setNameForOffering(sosOffering.getOfferingIdentifier(), sosOffering.getOfferingName());
                }
            }

            // add offering for procedure whether it's a normal offering or
            // hidden child
            cache.addOfferingForProcedure(procedure, sosOffering.getOfferingIdentifier());

            // allowed observation types
            cache.addAllowedObservationTypesForOffering(sosOffering.getOfferingIdentifier(), request.getMetadata()
                    .getObservationTypes());
            // allowed featureOfInterest types
            cache.addAllowedFeatureOfInterestTypesForOffering(sosOffering.getOfferingIdentifier(), request
                    .getMetadata().getFeatureOfInterestTypes());
        }

        // related features
        final Collection<SwesFeatureRelationship> relatedFeatures = request.getRelatedFeatures();
        if (CollectionHelper.isNotEmpty(relatedFeatures)) {
            for (SwesFeatureRelationship relatedFeature : relatedFeatures) {
                final String identifier = relatedFeature.getFeature().getIdentifier().getValue();
                for (SosOffering sosOffering : request.getAssignedOfferings()) {
                    // TODO check if check for parent offering is necessary;
                    cache.addRelatedFeatureForOffering(sosOffering.getOfferingIdentifier(), identifier);
                }
                cache.addRoleForRelatedFeature(identifier, relatedFeature.getRole());
            }
        }

        // observable property relations
        for (String observableProperty : request.getObservableProperty()) {
            cache.addProcedureForObservableProperty(observableProperty, procedure);
            cache.addObservablePropertyForProcedure(procedure, observableProperty);
            for (SosOffering sosOffering : request.getAssignedOfferings()) {
                cache.addOfferingForObservableProperty(observableProperty, sosOffering.getOfferingIdentifier());
                cache.addObservablePropertyForOffering(sosOffering.getOfferingIdentifier(), observableProperty);
            }
        }
    }
}
