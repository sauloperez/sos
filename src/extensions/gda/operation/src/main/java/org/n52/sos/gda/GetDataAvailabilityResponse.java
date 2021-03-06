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
package org.n52.sos.gda;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.response.AbstractServiceResponse;

/**
 * Response of a {@link GetDataAvailabilityRequest}.
 * 
 * @author Christian Autermann
 * 
 * @since 4.0.0
 */
public class GetDataAvailabilityResponse extends AbstractServiceResponse {
    private final List<DataAvailability> dataAvailabilities = new LinkedList<DataAvailability>();

    /**
     * Creates a new {@code GetDataAvailabilityResponse} consisting of zero or
     * more {@code DataAvailability} objects.
     * 
     * @param dataAvailabilities
     *            the data availabilities
     */
    public GetDataAvailabilityResponse(DataAvailability... dataAvailabilities) {
        Collections.addAll(this.dataAvailabilities, dataAvailabilities);
    }

    @Override
    public String getOperationName() {
        return GetDataAvailabilityConstants.OPERATION_NAME;
    }

    /**
     * @return the {@code DataAvailabilities}.
     */
    public List<DataAvailability> getDataAvailabilities() {
        return Collections.unmodifiableList(dataAvailabilities);
    }

    /**
     * Adds a new {@code DataAvailability} to the response.
     * 
     * @param dataAvailability
     *            the {@code DataAvailability}.
     */
    public void addDataAvailability(DataAvailability dataAvailability) {
        this.dataAvailabilities.add(dataAvailability);
    }

    /**
     * Sets the {@code DataAvailabilities} of the response.
     * 
     * @param dataAvailabilities
     *            the {@code DataAvailabilities}
     */
    public void setDataAvailabilities(Collection<? extends DataAvailability> dataAvailabilities) {
        this.dataAvailabilities.clear();
        this.dataAvailabilities.addAll(dataAvailabilities);
    }

    /**
     * Describes the availability of observation with a specified combination of
     * {@code featureOfInterest}, {@code observedProperty} and {@code procedure}
     * .
     */
    public static class DataAvailability {

        private final ReferenceType featureOfInterest;

        private final ReferenceType observedProperty;

        private final ReferenceType procedure;

        private final TimePeriod phenomenonTime;

        /**
         * Creates a new {@code DataAvailability}.
         * 
         * @param featureOfInterest
         *            the {@code featureOfInterest}
         * @param observedProperty
         *            the {@code observedProperty}
         * @param procedure
         *            the {@code procedure}
         * @param phenomenonTime
         *            the {@code phenomenonTime} for which data is available.
         */
        public DataAvailability(ReferenceType procedure, ReferenceType observedProperty,
                ReferenceType featureOfInterest, TimePeriod phenomenonTime) {
            this.observedProperty = observedProperty;
            this.procedure = procedure;
            this.featureOfInterest = featureOfInterest;
            this.phenomenonTime = phenomenonTime;
        }

        /**
         * @return the {@code featureOfInterest}
         */
        public ReferenceType getFeatureOfInterest() {
            return featureOfInterest;
        }

        /**
         * @return the {@code observedProperty}
         */
        public ReferenceType getObservedProperty() {
            return observedProperty;
        }

        /**
         * @return the {@code procedure}
         */
        public ReferenceType getProcedure() {
            return procedure;
        }

        /**
         * @return the {@code phenomenonTime} for which data is available.
         */
        public TimePeriod getPhenomenonTime() {
            return phenomenonTime;
        }
    }
}
