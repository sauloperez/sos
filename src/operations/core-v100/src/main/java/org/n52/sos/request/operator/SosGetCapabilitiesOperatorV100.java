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
package org.n52.sos.request.operator;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.ds.AbstractGetCapabilitiesDAO;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.GetCapabilitiesRequest;
import org.n52.sos.response.GetCapabilitiesResponse;

/**
 * @since 4.0.0
 * 
 */
public class SosGetCapabilitiesOperatorV100 extends
        AbstractV1RequestOperator<AbstractGetCapabilitiesDAO, GetCapabilitiesRequest, GetCapabilitiesResponse> {
    // TODO check for SOS 1.0.0
    private static final Set<String> CONFORMANCE_CLASSES = Collections
            .singleton("http://www.opengis.net/spec/SOS/1.0/conf/core");

    private static final String OPERATION_NAME = SosConstants.Operations.GetCapabilities.name();

    public SosGetCapabilitiesOperatorV100() {
        super(OPERATION_NAME, GetCapabilitiesRequest.class);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
    }

    @Override
    public GetCapabilitiesResponse receive(GetCapabilitiesRequest sosRequest) throws OwsExceptionReport {
        return getDao().getCapabilities(sosRequest);
    }

    @Override
    protected void checkParameters(GetCapabilitiesRequest request) throws OwsExceptionReport {
    }
}
