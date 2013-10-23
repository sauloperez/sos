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
package org.n52.sos.util.builder;

import java.util.List;

import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.sos.SosInsertionMetadata;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.swes.SwesFeatureRelationship;
import org.n52.sos.request.InsertSensorRequest;
import org.n52.sos.util.CollectionHelper;

import com.google.common.collect.Lists;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.0.0
 */
public class InsertSensorRequestBuilder {

    private SosProcedureDescription procedureDescription;

    private List<String> observableProperties;

    private List<String> observationTypes;
    
    private List<String> featureOfInterestTypes;

    private List<SwesFeatureRelationship> featureRelationships;

    public static InsertSensorRequestBuilder anInsertSensorRequest() {
        return new InsertSensorRequestBuilder();
    }

    public InsertSensorRequestBuilder setProcedure(SosProcedureDescription procedureDescription) {
        this.procedureDescription = procedureDescription;
        return this;
    }

    public InsertSensorRequestBuilder addObservableProperty(String observableProperty) {
        if (observableProperties == null) {
            observableProperties = Lists.newLinkedList();
        }
        observableProperties.add(observableProperty);
        return this;
    }

    public InsertSensorRequestBuilder addObservationType(String observationType) {
        if (observationTypes == null) {
            observationTypes = Lists.newLinkedList();
        }
        observationTypes.add(observationType);
        return this;
    }

    public InsertSensorRequestBuilder addFeatureOfInterestType(String featureOfInterestType) {
        if (featureOfInterestTypes == null) {
            featureOfInterestTypes = Lists.newLinkedList();
        }
        featureOfInterestTypes.add(featureOfInterestType);
        return this;
    }

    public InsertSensorRequestBuilder addRelatedFeature(AbstractFeature relatedFeature, String featureRole) {
        if (featureRelationships == null) {
            featureRelationships = Lists.newLinkedList();
        }
        SwesFeatureRelationship rel = new SwesFeatureRelationship();
        rel.setFeature(relatedFeature);
        rel.setRole(featureRole);
        featureRelationships.add(rel);
        return this;
    }

    public InsertSensorRequest build() {
        InsertSensorRequest request = new InsertSensorRequest();
        if (procedureDescription != null) {
            request.setProcedureDescription(procedureDescription);
            if (procedureDescription.isSetOfferings()) {
                request.setAssignedOfferings(Lists.newArrayList(procedureDescription.getOfferings()));
            }
        }
        if (CollectionHelper.isNotEmpty(observableProperties)) {
            request.setObservableProperty(observableProperties);
        }
        if (CollectionHelper.isNotEmpty(featureRelationships)) {
            request.setRelatedFeature(featureRelationships);
        }
        SosInsertionMetadata meta = null;
        if (CollectionHelper.isNotEmpty(observationTypes)) {
            meta = new SosInsertionMetadata();
            meta.setObservationTypes(observationTypes);
        }
        if (CollectionHelper.isNotEmpty(featureOfInterestTypes)) {
            if (meta == null) {
                meta = new SosInsertionMetadata();
            }
            meta.setFeatureOfInterestTypes(featureOfInterestTypes);
        }
        if (meta != null) {
            request.setMetadata(meta);
        }
        return request;
    }

}
