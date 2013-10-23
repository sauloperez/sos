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

import org.n52.sos.exception.ows.concrete.InvalidAcceptVersionsParameterException;
import org.n52.sos.exception.ows.concrete.InvalidServiceOrVersionException;
import org.n52.sos.exception.ows.concrete.InvalidServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingVersionParameterException;
import org.n52.sos.exception.ows.concrete.VersionNotSupportedException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsOperation;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.BatchRequest;
import org.n52.sos.request.GetCapabilitiesRequest;
import org.n52.sos.response.BatchResponse;
import org.n52.sos.service.operator.ServiceOperator;
import org.n52.sos.service.operator.ServiceOperatorKey;
import org.n52.sos.service.operator.ServiceOperatorRepository;
import org.n52.sos.util.BatchConstants;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class BatchOperationDAO extends AbstractOperationDAO {
    public BatchOperationDAO() {
        super(SosConstants.SOS, BatchConstants.OPERATION_NAME);
    }

    public BatchResponse executeRequests(BatchRequest request) throws OwsExceptionReport {
        BatchResponse response = new BatchResponse();
        response.setService(request.getService());
        response.setVersion(request.getVersion());
        for (AbstractServiceRequest r : request) {
            try {
                response.add(getServiceOperator(r).receiveRequest(r));
            } catch (OwsExceptionReport e) {
                response.add(e.setVersion(r.getVersion() != null ? r.getVersion() : request.getVersion()));
                if (request.isStopAtFailure()) {
                    break;
                }
            }
        }
        return response;
    }

    protected ServiceOperator getServiceOperator(AbstractServiceRequest request) throws OwsExceptionReport {
        checkServiceOperatorKeys(request);
        for (ServiceOperatorKey sokt : request.getServiceOperatorKeyType()) {
            ServiceOperator so = ServiceOperatorRepository.getInstance().getServiceOperator(sokt);
            if (so != null) {
                return so;
            }
        }
        // no operator found
        if (request instanceof GetCapabilitiesRequest) {
            throw new InvalidAcceptVersionsParameterException(((GetCapabilitiesRequest) request).getAcceptVersions());
        } else {
            throw new InvalidServiceOrVersionException(request.getService(), request.getVersion());
        }
    }

    protected void checkServiceOperatorKeys(AbstractServiceRequest request) throws OwsExceptionReport {
        CompositeOwsException exceptions = new CompositeOwsException();
        for (ServiceOperatorKey sokt : request.getServiceOperatorKeyType()) {
            checkService(sokt, exceptions);
            if (request instanceof GetCapabilitiesRequest) {
                checkAcceptVersions(request, exceptions);
            } else {
                checkVersion(sokt, exceptions);
            }
        }
        exceptions.throwIfNotEmpty();
    }

    protected boolean isVersionSupported(String service, String version) {
        return ServiceOperatorRepository.getInstance().isVersionSupported(service, version);
    }

    @Override
    protected void setOperationsMetadata(OwsOperation operation, String service, String version)
            throws OwsExceptionReport {
        /* nothing to do here */
    }

    private void checkAcceptVersions(AbstractServiceRequest request, CompositeOwsException exceptions) {
        GetCapabilitiesRequest gcr = (GetCapabilitiesRequest) request;
        if (gcr.isSetAcceptVersions()) {
            boolean hasSupportedVersion = false;
            for (String version : gcr.getAcceptVersions()) {
                if (isVersionSupported(gcr.getService(), version)) {
                    hasSupportedVersion = true;
                }
            }
            if (!hasSupportedVersion) {
                exceptions.add(new InvalidAcceptVersionsParameterException(gcr.getAcceptVersions()));
            }
        }
    }

    private void checkVersion(ServiceOperatorKey sokt, CompositeOwsException exceptions) {
        if (sokt.hasVersion()) {
            if (sokt.getVersion().isEmpty()) {
                exceptions.add(new MissingVersionParameterException());
            } else if (!isVersionSupported(sokt.getService(), sokt.getVersion())) {
                exceptions.add(new VersionNotSupportedException());
            }
        }
    }

    private void checkService(ServiceOperatorKey sokt, CompositeOwsException exceptions) {
        if (sokt.hasService()) {
            if (sokt.getService().isEmpty()) {
                exceptions.add(new MissingServiceParameterException());
            } else if (!ServiceOperatorRepository.getInstance().isServiceSupported(sokt.getService())) {
                exceptions.add(new InvalidServiceParameterException(sokt.getService()));
            }
        }
    }
}
