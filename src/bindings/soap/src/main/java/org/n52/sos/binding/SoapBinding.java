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

import static org.n52.sos.util.http.HTTPStatus.BAD_REQUEST;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPConstants;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.coding.OperationKey;
import org.n52.sos.decode.Decoder;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.SoapChain;
import org.n52.sos.event.SosEventBus;
import org.n52.sos.event.events.ExceptionEvent;
import org.n52.sos.exception.HTTPException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.NoDecoderForKeyException;
import org.n52.sos.exception.ows.concrete.NoEncoderForKeyException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.ConformanceClasses;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.GetCapabilitiesRequest;
import org.n52.sos.service.CommunicationObjectWithSoapHeader;
import org.n52.sos.service.SoapHeader;
import org.n52.sos.soap.SoapHelper;
import org.n52.sos.soap.SoapRequest;
import org.n52.sos.soap.SoapResponse;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.http.HTTPStatus;
import org.n52.sos.util.http.HTTPUtils;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.util.http.MediaTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 4.0.0
 * 
 */
public class SoapBinding extends SimpleBinding {
    private static final Logger LOGGER = LoggerFactory.getLogger(SoapBinding.class);

    private static final Set<String> CONFORMANCE_CLASSES = Collections
            .singleton(ConformanceClasses.SOS_V2_SOAP_BINDING);

    @Override
    public String getUrlPattern() {
        return BindingConstants.SOAP_BINDING_ENDPOINT;
    }

    @Override
    public boolean checkOperationHttpPostSupported(OperationKey k) {
        return hasDecoder(k, MediaTypes.TEXT_XML) || hasDecoder(k, MediaTypes.APPLICATION_XML);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
    }

    @Override
    public Set<MediaType> getSupportedEncodings() {
        return Collections.singleton(MediaTypes.APPLICATION_SOAP_XML);
    }

    @Override
    protected MediaType getDefaultContentType() {
        return MediaTypes.APPLICATION_SOAP_XML;
    }

    @Override
    public void doPostOperation(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws HTTPException, IOException {
        final SoapChain chain = new SoapChain(httpRequest, httpResponse);
        try {
            parseSoapRequest(chain);
            createSoapResponse(chain);
            if (!chain.getSoapRequest().hasSoapFault()) {
                parseBodyRequest(chain);
                createBodyResponse(chain);
            }
            writeResponse(chain);
        } catch (OwsExceptionReport t) {
            writeOwsExceptionReport(chain, t);
        }
    }

    private void parseSoapRequest(SoapChain soapChain) throws OwsExceptionReport {
        String soapAction = SoapHelper.checkSoapHeader(soapChain.getHttpRequest());
        XmlObject doc = XmlHelper.parseXmlSosRequest(soapChain.getHttpRequest());
        LOGGER.debug("SOAP-REQUEST: {}", doc.xmlText());
        Decoder<SoapRequest, XmlObject> decoder = getDecoder(CodingHelper.getDecoderKey(doc));
        SoapRequest soapRequest = decoder.decode(doc);
        if (soapRequest.getSoapAction() == null && soapAction != null) {
            soapRequest.setAction(soapAction);
        }
        soapChain.setSoapRequest(soapRequest);
    }

    private void parseBodyRequest(SoapChain chain) throws OwsExceptionReport, OwsExceptionReport {

        final XmlObject xmlObject = chain.getSoapRequest().getSoapBodyContent();
        DecoderKey key = CodingHelper.getDecoderKey(xmlObject);
        final Decoder<?, XmlObject> bodyDecoder = getDecoder(key);
        if (bodyDecoder == null) {
            throw new NoDecoderForKeyException(key).setStatus(BAD_REQUEST);
        }
        final Object aBodyRequest = bodyDecoder.decode(xmlObject);
        if (!(aBodyRequest instanceof AbstractServiceRequest)) {
            throw new NoApplicableCodeException().withMessage(
                    "The returned object is not an AbstractServiceRequest implementation").setStatus(BAD_REQUEST);
        }
        AbstractServiceRequest bodyRequest = (AbstractServiceRequest) aBodyRequest;
        bodyRequest.setRequestContext(getRequestContext(chain.getHttpRequest()));
        if (bodyRequest instanceof CommunicationObjectWithSoapHeader) {
            ((CommunicationObjectWithSoapHeader) bodyRequest).setSoapHeader(chain.getSoapRequest().getSoapHeader());
        }
        chain.setBodyRequest(bodyRequest);
    }

    private void encodeBodyResponse(SoapChain chain) throws OwsExceptionReport {
        XmlObject encodedResponse = (XmlObject) encodeResponse(chain.getBodyResponse(), MediaTypes.APPLICATION_XML);
        chain.getSoapResponse().setSoapBodyContent(encodedResponse);
    }

    private Object encodeSoapResponse(SoapChain chain) throws OwsExceptionReport {
        // soap injection
        if (chain.getBodyResponse() instanceof CommunicationObjectWithSoapHeader) {
            final CommunicationObjectWithSoapHeader soapHeaderObject =
                    (CommunicationObjectWithSoapHeader) chain.getBodyResponse();
            if (soapHeaderObject.isSetSoapHeader()) {
                final Map<String, SoapHeader> header =
                        ((CommunicationObjectWithSoapHeader) chain.getSoapRequest()).getSoapHeader();
                // TODO do things
                chain.getSoapResponse().setHeader(header);
            }
        }

        final EncoderKey key =
                CodingHelper.getEncoderKey(chain.getSoapResponse().getSoapNamespace(), chain.getSoapResponse());
        final Encoder<?, SoapResponse> encoder = getEncoder(key);
        if (encoder != null) {
            return encoder.encode(chain.getSoapResponse());
        } else {
            throw new NoEncoderForKeyException(key);
        }
    }

    private void writeOwsExceptionReport(SoapChain chain, OwsExceptionReport owse) throws HTTPException, IOException {
        try {
            String version = chain.hasBodyRequest() ? chain.getBodyRequest().getVersion() : null;
            SosEventBus.fire(new ExceptionEvent(owse));
            chain.getSoapResponse().setException(owse.setVersion(version));
            if (!chain.getSoapResponse().hasSoapVersion()) {
                chain.getSoapResponse().setSoapVersion(SOAPConstants.SOAP_1_2_PROTOCOL);
            }
            if (!chain.getSoapResponse().hasSoapNamespace()) {
                chain.getSoapResponse().setSoapNamespace(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE);
            }
            writeSoapResponse(chain);
        } catch (OwsExceptionReport t) {
            throw new HTTPException(HTTPStatus.INTERNAL_SERVER_ERROR, t);
        }
    }

    private void writeResponse(SoapChain chain) throws IOException, HTTPException {
        HTTPUtils.getAcceptHeader(chain.getHttpRequest());
        MediaType contentType =
                chooseResponseContentType(chain.getBodyResponse(), HTTPUtils.getAcceptHeader(chain.getHttpRequest()),
                        getDefaultContentType());
        // TODO allow other bindings to encode response as soap messages

        if (contentType.isCompatible(getDefaultContentType())) {
            try {
                if (chain.hasBodyResponse()) {
                    encodeBodyResponse(chain);
                }
                writeSoapResponse(chain);
            } catch (OwsExceptionReport e) {
                writeOwsExceptionReport(chain, e);
            }
        } else {
            writeResponse(chain.getHttpRequest(), chain.getHttpResponse(), chain.getBodyResponse(), contentType);
        }
    }

    private void createSoapResponse(SoapChain chain) {
        SoapResponse soapResponse = new SoapResponse();
        soapResponse.setSoapVersion(chain.getSoapRequest().getSoapVersion());
        soapResponse.setSoapNamespace(chain.getSoapRequest().getSoapNamespace());
        soapResponse.setHeader(chain.getSoapRequest().getSoapHeader());
        chain.setSoapResponse(soapResponse);
    }

    private void createBodyResponse(SoapChain chain) throws OwsExceptionReport {
        AbstractServiceRequest req = chain.getBodyRequest();
        chain.setBodyResponse(getServiceOperator(req).receiveRequest(req));
    }

    private void writeSoapResponse(SoapChain chain) throws IOException, OwsExceptionReport {
        Object encodedSoapResponse = encodeSoapResponse(chain);
        if (chain.getSoapResponse().hasException() && chain.getSoapResponse().getException().hasStatus()) {
            chain.getHttpResponse().setStatus(chain.getSoapResponse().getException().getStatus().getCode());
        }
        MediaType mt = MediaTypes.APPLICATION_SOAP_XML;
        if (chain.getBodyRequest() instanceof GetCapabilitiesRequest) {
            GetCapabilitiesRequest r = (GetCapabilitiesRequest) chain.getBodyRequest();
            if (r.isSetAcceptFormats()) {
                mt = MediaType.parse(r.getAcceptFormats().get(0));
            }
        }
        HTTPUtils.writeObject(chain.getHttpRequest(), chain.getHttpResponse(), mt, encodedSoapResponse);
    }
}
