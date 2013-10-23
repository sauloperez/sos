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

import java.util.ArrayList;

import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.sos.SosProcedureDescription;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.0.0
 */
public class ObservationConstellationBuilder {

    public static ObservationConstellationBuilder anObservationConstellation() {
        return new ObservationConstellationBuilder();
    }

    private AbstractFeature featureOfInterest;

    private SosProcedureDescription procedure;

    private String observationType;

    private OmObservableProperty observableProperty;

    private ArrayList<String> offerings;

    public ObservationConstellationBuilder setFeature(AbstractFeature featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
        return this;
    }

    public ObservationConstellationBuilder setProcedure(SosProcedureDescription procedure) {
        this.procedure = procedure;
        return this;
    }

    public ObservationConstellationBuilder setObservationType(String observationType) {
        this.observationType = observationType;
        return this;
    }

    public ObservationConstellationBuilder setObservableProperty(OmObservableProperty observableProperty) {
        this.observableProperty = observableProperty;
        return this;
    }

    public ObservationConstellationBuilder addOffering(String offeringIdentifier) {
        if (offeringIdentifier != null && !offeringIdentifier.isEmpty()) {
            if (offerings == null) {
                offerings = new ArrayList<String>();
            }
            offerings.add(offeringIdentifier);
        }
        return this;
    }

    public OmObservationConstellation build() {
        OmObservationConstellation sosObservationConstellation = new OmObservationConstellation();
        sosObservationConstellation.setFeatureOfInterest(featureOfInterest);
        sosObservationConstellation.setObservableProperty(observableProperty);
        sosObservationConstellation.setObservationType(observationType);
        sosObservationConstellation.setProcedure(procedure);
        if (offerings != null && !offerings.isEmpty()) {
            sosObservationConstellation.setOfferings(offerings);
        }
        return sosObservationConstellation;
    }

}
