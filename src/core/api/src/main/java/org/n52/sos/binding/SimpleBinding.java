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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.sos.coding.CodingRepository;
import org.n52.sos.coding.OperationKey;
import org.n52.sos.decode.Decoder;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.decode.OperationDecoderKey;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.ExceptionEncoderKey;
import org.n52.sos.encode.OperationEncoderKey;
import org.n52.sos.event.SosEventBus;
import org.n52.sos.event.events.ExceptionEvent;
import org.n52.sos.exception.HTTPException;
import org.n52.sos.exception.ows.concrete.InvalidAcceptVersionsParameterException;
import org.n52.sos.exception.ows.concrete.InvalidServiceOrVersionException;
import org.n52.sos.exception.ows.concrete.InvalidServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingVersionParameterException;
import org.n52.sos.exception.ows.concrete.NoEncoderForKeyException;
import org.n52.sos.exception.ows.concrete.VersionNotSupportedException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.GetCapabilitiesRequest;
import org.n52.sos.request.RequestContext;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.service.operator.ServiceOperator;
import org.n52.sos.service.operator.ServiceOperatorKey;
import org.n52.sos.service.operator.ServiceOperatorRepository;
import org.n52.sos.util.http.HTTPStatus;
import org.n52.sos.util.http.HTTPUtils;
import org.n52.sos.util.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public abstract class SimpleBinding extends Binding {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleBinding.class);
    public static final String QUALITY = "q";

    protected boolean isUseHttpResponseCodes() {
        return ServiceConfiguration.getInstance().isUseHttpStatusCodesInKvpAndPoxBinding();
    }

    protected RequestContext getRequestContext(HttpServletRequest req) {
        return RequestContext.fromRequest(req);
    }

    protected boolean isVersionSupported(String service, String acceptVersion) {
        return getServiceOperatorRepository().isVersionSupported(service, acceptVersion);
    }

    protected boolean isServiceSupported(String service) {
        return getServiceOperatorRepository().isServiceSupported(service);
    }

    protected ServiceOperatorRepository getServiceOperatorRepository() {
        return ServiceOperatorRepository.getInstance();
    }

    protected <F, T> Decoder<F, T> getDecoder(DecoderKey key) {
        return CodingRepository.getInstance().getDecoder(key);
    }

    protected <F, T> Encoder<F, T> getEncoder(EncoderKey key) {
        return CodingRepository.getInstance().getEncoder(key);
    }

    protected boolean hasDecoder(DecoderKey key) {
        return CodingRepository.getInstance().hasDecoder(key);
    }

    protected boolean hasEncoder(EncoderKey key) {
        return CodingRepository.getInstance().hasEncoder(key);
    }

    protected boolean hasDecoder(OperationKey key, MediaType mediaType) {
        return hasDecoder(new OperationDecoderKey(key, mediaType));
    }

    protected boolean hasEncoder(OperationKey key, MediaType mediaType) {
        return hasEncoder(new OperationEncoderKey(key, mediaType));
    }

    protected boolean hasEncoder(AbstractServiceResponse response, MediaType mediaType) {
        return hasEncoder(response.getOperationKey(), mediaType);
    }

    protected MediaType chooseResponseContentType(AbstractServiceResponse response, List<MediaType> acceptHeader,
            MediaType defaultContentType) throws HTTPException {
        /*
         * TODO get a list of response content types and check against
         * wildcards/qualities
         */
        if (!acceptHeader.isEmpty()) {
            if (!response.isSetContentType()) {
                for (MediaType mt : acceptHeader) {
                    MediaType mediaType = mt.withoutParameter(QUALITY);
                    if (defaultContentType.isCompatible(mediaType)) {
                        return defaultContentType;
                    } else if (hasEncoder(response, mediaType)) {
                        return mediaType;
                    }
                }
                // no encoder for any accept header content type
                throw new HTTPException(HTTPStatus.NOT_ACCEPTABLE);
            } else {
                for (MediaType mt : acceptHeader) {
                    MediaType mediaType = mt.withoutParameter(QUALITY);
                    if (response.getContentType().isCompatible(mediaType)) {
                        return response.getContentType();
                    }
                }
                // incompatible response content type and accept header
                throw new HTTPException(HTTPStatus.NOT_ACCEPTABLE);
            }
        } else {
            if (!response.isSetContentType()) {
                return defaultContentType;
            } else {
                MediaType mediaType = response.getContentType().withoutParameter(QUALITY);
                if (hasEncoder(response, mediaType)) {
                    return response.getContentType();
                }
            }
            // no encoder for response content type
            throw new HTTPException(HTTPStatus.NOT_ACCEPTABLE);
        }
    }

    protected MediaType chooseResponseContentTypeForExceptionReport(List<MediaType> requested, MediaType def)
            throws HTTPException {
        /*
         * TODO get a list of response content types and check against
         * wildcards/qualities
         */
        if (requested.isEmpty()) {
            return def;
        }
        for (MediaType mt : requested) {
            if (def.isCompatible(mt)) {
                return def;
            }
            if (CodingRepository.getInstance().hasEncoder(new ExceptionEncoderKey(mt.withoutParameter(QUALITY)))) {
                return mt;
            }
        }
        throw new HTTPException(HTTPStatus.NOT_ACCEPTABLE);
    }

    protected ServiceOperator getServiceOperator(ServiceOperatorKey sokt) throws OwsExceptionReport {
        return getServiceOperatorRepository().getServiceOperator(sokt);
    }

    protected ServiceOperator getServiceOperator(AbstractServiceRequest request) throws OwsExceptionReport {
        checkServiceOperatorKeyTypes(request);
        for (ServiceOperatorKey sokt : request.getServiceOperatorKeyType()) {
            ServiceOperator so = getServiceOperator(sokt);
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

    protected void checkServiceOperatorKeyTypes(AbstractServiceRequest request) throws OwsExceptionReport {
        CompositeOwsException exceptions = new CompositeOwsException();
        for (ServiceOperatorKey sokt : request.getServiceOperatorKeyType()) {
            if (sokt.hasService()) {
                if (sokt.getService().isEmpty()) {
                    exceptions.add(new MissingServiceParameterException());
                } else if (!getServiceOperatorRepository().isServiceSupported(sokt.getService())) {
                    exceptions.add(new InvalidServiceParameterException(sokt.getService()));
                }
            }
            if (request instanceof GetCapabilitiesRequest) {
                GetCapabilitiesRequest gcr = (GetCapabilitiesRequest) request;
                if (gcr.isSetAcceptVersions()) {
                    boolean hasSupportedVersion = false;
                    for (String acceptVersion : gcr.getAcceptVersions()) {
                        if (isVersionSupported(request.getService(), acceptVersion)) {
                            hasSupportedVersion = true;
                        }
                    }
                    if (!hasSupportedVersion) {
                        exceptions.add(new InvalidAcceptVersionsParameterException(gcr.getAcceptVersions()));
                    }
                }
            } else {
                if (sokt.hasVersion()) {
                    if (sokt.getVersion().isEmpty()) {
                        exceptions.add(new MissingVersionParameterException());
                    } else if (!isVersionSupported(sokt.getService(), sokt.getVersion())) {
                        exceptions.add(new VersionNotSupportedException());
                    }
                }
            }
        }
        exceptions.throwIfNotEmpty();
    }

    protected void writeResponse(HttpServletRequest request, HttpServletResponse response,
            AbstractServiceResponse serviceResponse) throws HTTPException, IOException {
        MediaType contentType =
                chooseResponseContentType(serviceResponse, HTTPUtils.getAcceptHeader(request), getDefaultContentType());
        writeResponse(request, response, serviceResponse, contentType);
    }

    protected Object encodeResponse(AbstractServiceResponse response, MediaType contentType) throws OwsExceptionReport {
        OperationEncoderKey key = new OperationEncoderKey(response.getOperationKey(), contentType);
        Encoder<Object, AbstractServiceResponse> encoder = CodingRepository.getInstance().getEncoder(key);
        if (encoder == null) {
            throw new NoEncoderForKeyException(key);
        }
        return encoder.encode(response);
    }

    protected void writeOwsExceptionReport(HttpServletRequest request, HttpServletResponse response,
            OwsExceptionReport oer) throws HTTPException {
        try {
            SosEventBus.fire(new ExceptionEvent(oer));
            MediaType contentType =
                    chooseResponseContentTypeForExceptionReport(HTTPUtils.getAcceptHeader(request),
                            getDefaultContentType());
            Object encoded = encodeOwsExceptionReport(oer, contentType);
            if (isUseHttpResponseCodes() && oer.hasStatus()) {
                response.setStatus(oer.getStatus().getCode());
            }
            HTTPUtils.writeObject(request, response, contentType, encoded);
        } catch (Exception e) {
            throw new HTTPException(HTTPStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    protected abstract MediaType getDefaultContentType();

    protected Object encodeOwsExceptionReport(OwsExceptionReport oer, MediaType contentType) throws OwsExceptionReport {
        Encoder<Object, OwsExceptionReport> encoder = getEncoder(new ExceptionEncoderKey(contentType));
        return encoder.encode(oer);
    }

    protected void writeResponse(HttpServletRequest request, HttpServletResponse response,
            AbstractServiceResponse serviceResponse, MediaType contentType) throws IOException, HTTPException {
        Object encodedResponse = null;
        try {
            encodedResponse = encodeResponse(serviceResponse, contentType);
        } catch (OwsExceptionReport oer) {
            writeOwsExceptionReport(request, response, oer);
        }

        if (encodedResponse != null) {
            HTTPUtils.writeObject(request, response, contentType, encodedResponse);
        }
    }
}
