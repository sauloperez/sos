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
package org.n52.sos.decode;

import java.io.IOException;
import java.util.Locale;

import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.soap.SoapFault;
import org.n52.sos.soap.SoapHelper;
import org.n52.sos.soap.SoapRequest;
import org.n52.sos.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.x2003.x05.soapEnvelope.Body;
import org.w3.x2003.x05.soapEnvelope.EnvelopeDocument;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Joiner;

/**
 * class encapsulates decoding methods for SOAP elements.
 * 
 * @since 4.0.0
 */
public class Soap12Decoder extends AbstractSoapDecoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(Soap12Decoder.class);

    public Soap12Decoder() {
        super(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE);
        LOGGER.debug("Decoder for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(getDecoderKeyTypes()));
    }

    /**
     * Parses SOAP 1.2 Envelope to a SOS internal SOAP request.
     * 
     * @param doc
     *            request as xml representation
     * 
     * @return SOS internal SOAP request
     * 
     * @throws OwsExceptionReport
     *             if an error occurs.
     */
    @Override
    protected SoapRequest createEnvelope(XmlObject doc) throws OwsExceptionReport {
        SoapRequest soapRequest =
                new SoapRequest(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, SOAPConstants.SOAP_1_2_PROTOCOL);

        String soapAction = "";
        try {
            SOAPMessage message;
            try {
                message = SoapHelper.getSoapMessageForProtocol(SOAPConstants.SOAP_1_2_PROTOCOL, doc.newInputStream());
            } catch (IOException ioe) {
                throw new NoApplicableCodeException().causedBy(ioe).withMessage(
                        "Error while parsing SOAPMessage from request string!");
            } catch (SOAPException soape) {
                throw new NoApplicableCodeException().causedBy(soape).withMessage(
                        "Error while parsing SOAPMessage from request string!");
            }
            try {
                if (message.getSOAPHeader() != null) {
                    soapRequest.setSoapHeader(getSoapHeader(message.getSOAPHeader()));
                }
                soapRequest.setAction(checkSoapAction(soapAction, soapRequest.getSoapHeader()));
                soapRequest.setSoapBodyContent(getBodyContent((EnvelopeDocument) doc));
            } catch (SOAPException soape) {
                throw new NoApplicableCodeException().causedBy(soape).withMessage("Error while parsing SOAPMessage!");
            }
        } catch (OwsExceptionReport owse) {
            throw owse;
        }
        return soapRequest;
    }

    @Override
    protected SoapRequest createFault(OwsExceptionReport owse) {
        SoapFault fault = new SoapFault();
        fault.setFaultCode(SOAPConstants.SOAP_SENDER_FAULT);
        fault.setLocale(Locale.ENGLISH);
        fault.setFaultReason(owse.getMessage());
        SoapRequest r = new SoapRequest(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, SOAPConstants.SOAP_1_2_PROTOCOL);
        r.setSoapFault(fault);
        return r;
    }

    private XmlObject getBodyContent(EnvelopeDocument doc) throws CodedException {
        Body body = doc.getEnvelope().getBody();
        try {
            Node domNode = body.getDomNode();
            if (domNode.hasChildNodes()) {
                NodeList childNodes = domNode.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node node = childNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        XmlObject content = XmlObject.Factory.parse(node);
                        // fix problem with invalid prefix in xsi:type value for
                        // om:result and OM_SWEArrayObservation type.
                        XmlHelper.fixNamespaceForXsiType(content, SweConstants.QN_DATA_ARRAY_PROPERTY_TYPE_SWE_200);
                        return content;
                    }
                }
            }
            return body;
        } catch (XmlException xmle) {
            throw new NoApplicableCodeException().causedBy(xmle).withMessage("Error while parsing SOAP body element!");
        }
    }
}
