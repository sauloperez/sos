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
package org.n52.sos.binding.rest.resources.sensors;

import net.opengis.sensorML.x101.SystemType;

import org.n52.sos.request.DescribeSensorRequest;
import org.n52.sos.request.UpdateSensorRequest;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class SensorsPutRequest extends TransactionalSensorRequest {

    private UpdateSensorRequest updateSensorRequest;
    private DescribeSensorRequest describeSensorRequest;
    
    public SensorsPutRequest(UpdateSensorRequest updateSensorRequest,
            SystemType xb_smlSystem)
    {
        super(xb_smlSystem);
        this.updateSensorRequest = updateSensorRequest;
    }

    public UpdateSensorRequest getUpdateSensorRequest()
    {
        return updateSensorRequest;
    }

    public DescribeSensorRequest getDescribeSensorRequest()
    {
        return describeSensorRequest;
    }

}
