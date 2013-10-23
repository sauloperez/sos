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
package org.n52.sos.ogc.om;

import java.io.Serializable;

import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.om.values.Value;

/**
 * Interface for observation values.
 * 
 * @since 4.0.0
 * 
 * @param <T>
 *            observation value type
 */
public interface ObservationValue<T extends Value<?>> extends Serializable {

    /**
     * Get phenomenon or sampling time of the observation
     * 
     * @return Phenomenon or sampling time of the observation
     */
    Time getPhenomenonTime();

    /**
     * Set phenomenon or sampling time of the observation
     * 
     * @param phenomenonTime
     *            Phenomenon or sampling time of the observation
     */
    void setPhenomenonTime(Time phenomenonTime);

    /**
     * Get observation value
     * 
     * @return Observation value
     */
    T getValue();

    /**
     * Set observation value
     * 
     * @param value
     *            Observation value
     */
    void setValue(T value);

}