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
package org.n52.sos.binding;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.sos.coding.OperationKey;
import org.n52.sos.decode.Decoder;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.decode.OperationDecoderKey;
import org.n52.sos.exception.HTTPException;
import org.n52.sos.exception.ows.concrete.InvalidServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingRequestParameterException;
import org.n52.sos.exception.ows.concrete.NoDecoderForKeyException;
import org.n52.sos.exception.ows.concrete.VersionNotSupportedException;
import org.n52.sos.ogc.ows.OWSConstants.RequestParams;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.ConformanceClasses;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.util.KvpHelper;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.util.http.MediaTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SOS operator for Key-Value-Pair (HTTP-Get) requests
 * 
 * @since 4.0.0
 */
public class KvpBinding extends SimpleBinding {
    private static final Logger LOGGER = LoggerFactory.getLogger(KvpBinding.class);

    private static final Set<String> CONFORMANCE_CLASSES = Collections
            .singleton(ConformanceClasses.SOS_V2_KVP_CORE_BINDING);

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
    }

    @Override
    public String getUrlPattern() {
        return BindingConstants.KVP_BINDING_ENDPOINT;
    }

    @Override
    public Set<MediaType> getSupportedEncodings() {
        return Collections.singleton(MediaTypes.APPLICATION_KVP);
    }

    @Override
    protected MediaType getDefaultContentType() {
        return MediaTypes.APPLICATION_XML;
    }

    @Override
    public void doGetOperation(HttpServletRequest req, HttpServletResponse res) throws HTTPException, IOException {
        LOGGER.debug("KVP-REQUEST: {}", req.getQueryString());
        AbstractServiceRequest serviceRequest = null;
        try {
            serviceRequest = parseRequest(req);
            // add request context information
            serviceRequest.setRequestContext(getRequestContext(req));
            AbstractServiceResponse response = getServiceOperator(serviceRequest).receiveRequest(serviceRequest);
            writeResponse(req, res, response);
        } catch (OwsExceptionReport oer) {
            oer.setVersion(serviceRequest != null ? serviceRequest.getVersion() : null);
            writeOwsExceptionReport(req, res, oer);
        }
    }

    private String getServiceParameterValue(Map<String, String> map) throws OwsExceptionReport {
        final String service = KvpHelper.getParameterValue(RequestParams.service, map);
        if (service == null && isGetCapabilities(map)) {
            // unclear behaviour because of imprecise spec:
            // OGC 12-006 8.1.1 and OGC 12-006 13.2.1/OGC 06-121r3 7.2.3
            map.put(RequestParams.service.name(), SosConstants.SOS);
            return SosConstants.SOS;
        } else {
            KvpHelper.checkParameterValue(service, RequestParams.service);
        }
        if (!isServiceSupported(service)) {
            throw new InvalidServiceParameterException(service);
        }
        return service;
    }

    private String getVersionParameterValue(Map<String, String> map) throws OwsExceptionReport {
        final String version = KvpHelper.getParameterValue(RequestParams.version, map);
        final String service = KvpHelper.getParameterValue(RequestParams.service, map);
        if (!isGetCapabilities(map)) {
            KvpHelper.checkParameterValue(version, RequestParams.version);
            KvpHelper.checkParameterValue(service, RequestParams.service);
            if (!isVersionSupported(service, version)) {
                throw new VersionNotSupportedException();
            }
        }
        return version;
    }

    @Override
    public boolean checkOperationHttpGetSupported(OperationKey k) {
        return hasDecoder(k, MediaTypes.APPLICATION_KVP);
    }

    protected boolean isGetCapabilities(Map<String, String> map) throws OwsExceptionReport {
        return SosConstants.Operations.GetCapabilities.name().equals(getRequestParameterValue(map));
    }

    public String getRequestParameterValue(Map<String, String> map) throws OwsExceptionReport {
        String value = KvpHelper.getParameterValue(RequestParams.request, map);
        KvpHelper.checkParameterValue(value, RequestParams.request);
        return value;
    }

    protected AbstractServiceRequest parseRequest(HttpServletRequest req) throws OwsExceptionReport {
        if (req.getParameterMap() == null || (req.getParameterMap() != null && req.getParameterMap().isEmpty())) {
            throw new MissingRequestParameterException();
        }
        Map<String, String> parameterValueMap = KvpHelper.getKvpParameterValueMap(req);
        // check if request contains request parameter
        String operation = getRequestParameterValue(parameterValueMap);
        KvpHelper.checkParameterValue(operation, RequestParams.request);
        String service = getServiceParameterValue(parameterValueMap);
        String version = getVersionParameterValue(parameterValueMap);
        if (version != null && !isVersionSupported(service, version)) {
            throw new VersionNotSupportedException();
        }
        DecoderKey k = new OperationDecoderKey(service, version, operation, MediaTypes.APPLICATION_KVP);
        Decoder<AbstractServiceRequest, Map<String, String>> decoder = getDecoder(k);
        if (decoder != null) {
            return decoder.decode(parameterValueMap);
        } else {
            throw new NoDecoderForKeyException(k);
        }
    }
}
