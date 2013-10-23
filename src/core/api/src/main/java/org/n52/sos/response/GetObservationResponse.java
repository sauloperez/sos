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
package org.n52.sos.response;

import java.util.LinkedList;
import java.util.List;

import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.sos.SosConstants;

/**
 * @since 4.0.0
 * 
 */
public class GetObservationResponse extends AbstractObservationResponse {

    public void mergeObservationsWithSameConstellation() {
        // TODO merge all observations with the same observationContellation
        // FIXME Failed to set the observation type to sweArrayObservation for
        // the merged Observations
        // (proc, obsProp, foi)
        if (getObservationCollection() != null) {
            final List<OmObservation> mergedObservations = new LinkedList<OmObservation>();
            int obsIdCounter = 1;
            for (final OmObservation sosObservation : getObservationCollection()) {
                if (mergedObservations.isEmpty()) {
                    sosObservation.setObservationID(Integer.toString(obsIdCounter++));
                    mergedObservations.add(sosObservation);
                } else {
                    boolean combined = false;
                    for (final OmObservation combinedSosObs : mergedObservations) {
                        if (combinedSosObs.getObservationConstellation().equals(
                                sosObservation.getObservationConstellation())) {
                            combinedSosObs.setResultTime(null);
                            combinedSosObs.mergeWithObservation(sosObservation);
                            combined = true;
                            break;
                        }
                    }
                    if (!combined) {
                        mergedObservations.add(sosObservation);
                    }
                }
            }
            setObservationCollection(mergedObservations);
        }
    }

    /*
     * TODO uncomment when WaterML support is activated public
     * Collection<SosObservation> mergeObservations(boolean
     * mergeObservationValuesWithSameParameters) { Collection<SosObservation>
     * combinedObsCol = new ArrayList<SosObservation>(); int obsIdCounter = 1;
     * for (SosObservation sosObservation : observationCollection) { if
     * (combinedObsCol.isEmpty()) {
     * sosObservation.setObservationID(Integer.toString(obsIdCounter++));
     * combinedObsCol.add(sosObservation); } else { boolean combined = false;
     * for (SosObservation combinedSosObs : combinedObsCol) { if
     * (mergeObservationValuesWithSameParameters) { if
     * (combinedSosObs.getObservationConstellation().equals(
     * sosObservation.getObservationConstellation())) {
     * combinedSosObs.mergeWithObservation(sosObservation, false); combined =
     * true; break; } } else { if
     * (combinedSosObs.getObservationConstellation().equalsExcludingObsProp(
     * sosObservation.getObservationConstellation())) {
     * combinedSosObs.mergeWithObservation(sosObservation, true); combined =
     * true; break; } } } if (!combined) { combinedObsCol.add(sosObservation); }
     * } } return combinedObsCol; }
     */

    @Override
    public String getOperationName() {
        return SosConstants.Operations.GetObservation.name();
    }

}
