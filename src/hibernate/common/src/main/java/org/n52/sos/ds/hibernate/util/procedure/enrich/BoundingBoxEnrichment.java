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
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sensorML.elements.SmlCapabilities;
import org.n52.sos.ogc.sensorML.elements.SmlCapabilitiesPredicates;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweEnvelope;
import org.n52.sos.ogc.swe.SweField;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class BoundingBoxEnrichment extends ProcedureDescriptionEnrichment {
    public static final Predicate<SmlCapabilities> BBOX_PREDICATE =
            SmlCapabilitiesPredicates.name(SensorMLConstants.ELEMENT_NAME_OBSERVED_BBOX);

    @Override
    public void enrich() throws OwsExceptionReport {
        Optional<SmlCapabilities> existing =
                getSensorML().findCapabilities(BBOX_PREDICATE);
        if (existing.isPresent()) {
            int i = existing.get().getDataRecord().getFieldIndexByIdentifier(SensorMLConstants.ELEMENT_NAME_OBSERVED_BBOX);
            if (i >= 0) {
                SosEnvelope envelope = ((SweEnvelope) existing.get().getDataRecord()
                        .getFields().get(i).getElement()).toSosEnvelope();
                envelope.expandToInclude(createEnvelopeForOfferings());
                getSensorML().getCapabilities().remove(i);
                getSensorML().addCapabilities(createCapabilitiesForBoundingBox(envelope));
            }
        } else {
            SmlCapabilities observedBBox = createCapabilitiesForOfferings();
            if (observedBBox != null) {
                getSensorML().addCapabilities(observedBBox);
            }
        }
    }

    /**
     * Create a ObservedBBOX capabilities element
     *
     * @param identifier Procedure identifier to add ObservedBBOX
     *
     * @return ObservedBBOX capabilities element
     */
    private SmlCapabilities createCapabilitiesForOfferings() {
        // get bbox for each offering and merge to one bbox
        return createCapabilitiesForBoundingBox(createEnvelopeForOfferings());
    }

    /**
     * Merge offering envelopes.
     *
     * @return merged envelope
     */
    protected SosEnvelope createEnvelopeForOfferings() {
        SosEnvelope mergedEnvelope = new SosEnvelope();
        for (SosOffering offering : getSosOfferings()) {
            mergedEnvelope.expandToInclude(getEnvelope(offering));
        }
        return mergedEnvelope.switchCoordinatesIfNeeded();
    }

    /**
     * Get the envelope for the given offering.
     *
     * @param offering the offering
     *
     * @return the envelope (may be <code>null</code>)
     */
    private SosEnvelope getEnvelope(SosOffering offering) {
        return getCache().getEnvelopeForOffering(offering
                .getOfferingIdentifier());
    }

    private SmlCapabilities createCapabilitiesForBoundingBox(SosEnvelope bbox) {
        if (bbox.isSetEnvelope()) {
            // add merged bbox to capabilities as swe:envelope
            SweEnvelope envelope = new SweEnvelope(bbox, procedureSettings().getLatLongUom());
            SweField field = new SweField(SensorMLConstants.ELEMENT_NAME_OBSERVED_BBOX, envelope);
            return new SmlCapabilities()
                    .setName(SensorMLConstants.ELEMENT_NAME_OBSERVED_BBOX)
                    .setDataRecord(new SweDataRecord().addField(field));
        } else {
            return null;
        }
    }
}
