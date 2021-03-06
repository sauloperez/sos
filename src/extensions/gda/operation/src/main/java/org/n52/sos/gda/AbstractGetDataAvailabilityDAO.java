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

import org.n52.sos.ds.AbstractOperationDAO;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsOperation;
import org.n52.sos.util.SosHelper;

/**
 * DAO to get the DataAvailabilities out of the database.
 * 
 * @author Christian Autermann
 * 
 * @since 4.0.0
 */
public abstract class AbstractGetDataAvailabilityDAO extends AbstractOperationDAO {
    public AbstractGetDataAvailabilityDAO(String service) {
        super(service, GetDataAvailabilityConstants.OPERATION_NAME);
    }

    @Override
    protected void setOperationsMetadata(OwsOperation operation, String service, String version)
            throws OwsExceptionReport {
        operation.addPossibleValuesParameter(GetDataAvailabilityConstants.GetDataAvailabilityParams.procedure,
                getCache().getProcedures());
        operation.addPossibleValuesParameter(GetDataAvailabilityConstants.GetDataAvailabilityParams.observedProperty,
                getCache().getObservableProperties());
        operation.addPossibleValuesParameter(GetDataAvailabilityConstants.GetDataAvailabilityParams.featureOfInterest,
                SosHelper.getFeatureIDs(getCache().getFeaturesOfInterest(), version));
    }

    /**
     * Get the DataAvailability out of the Database.
     * 
     * @param sosRequest
     *            the <code>GetDataAvailabilityRequest</code>
     * @return the <code>GetDataAvailabilityResponse</code>
     * 
     * 
     * @throws OwsExceptionReport
     *             if an error occurs
     */
    public abstract GetDataAvailabilityResponse getDataAvailability(GetDataAvailabilityRequest sosRequest)
            throws OwsExceptionReport;
}
