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

import org.n52.sos.ds.OperationDAO;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.service.TransactionalSecurityConfiguration;

/**
 * @since 4.0.0
 *
 * @param <D>
 * @param <Q>
 * @param <A>
 */
public abstract class AbstractTransactionalRequestOperator<D extends OperationDAO, Q extends AbstractServiceRequest, A extends AbstractServiceResponse>
        extends AbstractRequestOperator<D, Q, A> {
    public AbstractTransactionalRequestOperator(String service,
                                                String version,
                                                String operationName,
                                                Class<Q> requestType) {
        super(service, version, operationName, requestType);
    }

    @Override
    public AbstractServiceResponse receiveRequest(AbstractServiceRequest request)
            throws OwsExceptionReport {
        try {
            new TransactionalRequestChecker(getConfig())
                    .check(request.getRequestContext());
        } catch (ConfigurationException ce) {
            throw new NoApplicableCodeException().causedBy(ce);
        }
        return super.receiveRequest(request);
    }

    private TransactionalSecurityConfiguration getConfig() {
        return TransactionalSecurityConfiguration.getInstance();
    }
}
