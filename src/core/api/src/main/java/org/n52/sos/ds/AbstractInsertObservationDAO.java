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
package org.n52.sos.ds;

import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsOperation;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.InsertObservationRequest;
import org.n52.sos.response.InsertObservationResponse;

/**
 * @since 4.0.0
 * 
 */
public abstract class AbstractInsertObservationDAO extends AbstractOperationDAO {

    public AbstractInsertObservationDAO(String service) {
        super(service, SosConstants.Operations.InsertObservation.name());
    }

    @Override
    protected void setOperationsMetadata(OwsOperation opsMeta, String service, String version)
            throws OwsExceptionReport {
        opsMeta.addPossibleValuesParameter(Sos2Constants.InsertObservationParams.offering, getCache().getOfferings());
        opsMeta.addAnyParameterValue(Sos2Constants.InsertObservationParams.observation);
        opsMeta.addDataTypeParameter(Sos2Constants.InsertObservationParams.observation,
                OmConstants.SCHEMA_LOCATION_URL_OM_20_OM_OBSERVATION);
    }

    public abstract InsertObservationResponse insertObservation(InsertObservationRequest request)
            throws OwsExceptionReport;

}
