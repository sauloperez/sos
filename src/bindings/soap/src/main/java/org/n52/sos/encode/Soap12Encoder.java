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
package org.n52.sos.encode;

import java.util.AbstractMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.OwsExceptionCode;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.soap.SoapFault;
import org.n52.sos.soap.SoapHelper;
import org.n52.sos.soap.SoapResponse;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.N52XmlHelper;
import org.n52.sos.util.OwsHelper;
import org.n52.sos.w3c.SchemaLocation;
import org.n52.sos.w3c.W3CConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.x2003.x05.soapEnvelope.Body;
import org.w3.x2003.x05.soapEnvelope.Envelope;
import org.w3.x2003.x05.soapEnvelope.EnvelopeDocument;
import org.w3.x2003.x05.soapEnvelope.Fault;
import org.w3.x2003.x05.soapEnvelope.FaultDocument;
import org.w3.x2003.x05.soapEnvelope.Faultcode;
import org.w3.x2003.x05.soapEnvelope.Reasontext;
import org.w3.x2003.x05.soapEnvelope.Subcode;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 * 
 */
public class Soap12Encoder extends AbstractSoapEncoder<XmlObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Soap12Encoder.class);

    public Soap12Encoder() {
        super(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE);
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(getEncoderKeyType()));
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        // TODO return valid schemaLocation
        return Sets.newHashSet();
    }

    @Override
    public XmlObject encode(final SoapResponse response, final Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        if (response == null) {
            throw new UnsupportedEncoderInputException(this, response);
        }
        String action = null;
        final EnvelopeDocument envelopeDoc = EnvelopeDocument.Factory.newInstance();
        final Envelope envelope = envelopeDoc.addNewEnvelope();
        final Body body = envelope.addNewBody();
        if (response.getSoapFault() != null) {
            body.set(createSOAP12Fault(response.getSoapFault()));
        } else {
            if (response.getException() != null) {
                if (!response.getException().getExceptions().isEmpty()) {
                    final CodedException firstException = response.getException().getExceptions().get(0);
                    action = getExceptionActionURI(firstException.getCode());
                }
                body.set(createSOAP12FaultFromExceptionResponse(response.getException()));
                N52XmlHelper.setSchemaLocationsToDocument(
                        envelopeDoc,
                        Sets.newHashSet(N52XmlHelper.getSchemaLocationForSOAP12(),
                                N52XmlHelper.getSchemaLocationForOWS110Exception()));
            } else {
                action = response.getSoapAction();
                final XmlObject bodyContent = response.getSoapBodyContent();
                String value = null;
                Node nodeToRemove = null;
                final NamedNodeMap attributeMap = bodyContent.getDomNode().getFirstChild().getAttributes();
                for (int i = 0; i < attributeMap.getLength(); i++) {
                    final Node node = attributeMap.item(i);
                    if (node.getLocalName().equals(W3CConstants.AN_SCHEMA_LOCATION)) {
                        value = node.getNodeValue();
                        nodeToRemove = node;
                    }
                }
                if (nodeToRemove != null) {
                    attributeMap.removeNamedItem(nodeToRemove.getNodeName());
                }
                final Set<SchemaLocation> schemaLocations = Sets.newHashSet();
                schemaLocations.add(N52XmlHelper.getSchemaLocationForSOAP12());
                if (value != null && !value.isEmpty()) {
                    String[] split = value.split(" ");
                    for (int i = 0; i < split.length; i += 2) {
                        schemaLocations.add(new SchemaLocation(split[i], split[i + 1]));
                    }
                }
                N52XmlHelper.setSchemaLocationsToDocument(envelopeDoc, schemaLocations);
                body.set(bodyContent);
            }
        }

        // if (response.getHeader() != null) {
        // Map<String, SoapHeader> headers = response.getHeader();
        // for (String namespace : headers.keySet()) {
        // SoapHeader header = headers.get(namespace);
        // if (namespace.equals(WsaConstants.NS_WSA)) {
        // WsaHeader wsa = (WsaHeader) header;
        // wsa.setActionValue(action);
        // }
        // try {
        // Encoder encoder = Configurator.getInstance().getEncoder(namespace);
        // if (encoder != null) {
        // Map<QName, String> headerElements = (Map<QName, String>)
        // encoder.encode(header);
        // for (QName qName : headerElements.keySet()) {
        // soapResponseMessage.getSOAPHeader().addChildElement(qName)
        // .setTextContent(headerElements.get(qName));
        // }
        // }
        // } catch (OwsExceptionReport owse) {
        // throw owse;
        // }
        // }
        //
        // } else {
        // soapResponseMessage.getSOAPHeader().detachNode();
        // }

        // TODO for testing an validating
        // checkAndValidateSoapMessage(envelopeDoc);

        return envelopeDoc;
    }

    private XmlObject createSOAP12Fault(final SoapFault soapFault) {
        final FaultDocument faultDoc = FaultDocument.Factory.newInstance();
        final Fault fault = faultDoc.addNewFault();
        fault.addNewCode().setValue(soapFault.getFaultCode());
        final Reasontext addNewText = fault.addNewReason().addNewText();
        addNewText.setLang(soapFault.getLocale().getDisplayLanguage());
        addNewText.setStringValue(soapFault.getFaultReason());
        if (soapFault.getDetailText() != null) {
            final XmlString xmlString = XmlString.Factory.newInstance();
            xmlString.setStringValue(soapFault.getDetailText());
            fault.addNewDetail().set(xmlString);
        }
        return faultDoc;
    }

    @SuppressWarnings("unchecked")
    // see
    // http://www.angelikalanger.com/GenericsFAQ/FAQSections/ProgrammingIdioms.html#FAQ300
    // for more details
    private XmlObject createSOAP12FaultFromExceptionResponse(final OwsExceptionReport owsExceptionReport)
            throws OwsExceptionReport {
        final FaultDocument faultDoc = FaultDocument.Factory.newInstance();
        final Fault fault = faultDoc.addNewFault();
        final Faultcode code = fault.addNewCode();
        code.setValue(SOAPConstants.SOAP_SENDER_FAULT);

        // we encode only the first exception because of OGC#09-001 Section
        // 19.2.3 SOAP 1.2 Fault Binding
        if (!owsExceptionReport.getExceptions().isEmpty()) {
            final CodedException firstException = owsExceptionReport.getExceptions().get(0);
            final Subcode subcode = code.addNewSubcode();
            QName qName;
            if (firstException.getCode() != null) {
                qName = OwsHelper.getQNameForLocalName(firstException.getCode().toString());
            } else {
                qName = OwsHelper.getQNameForLocalName(OwsExceptionCode.NoApplicableCode.name());
            }
            subcode.setValue(qName);
            final Reasontext addNewText = fault.addNewReason().addNewText();
            addNewText.setLang(Locale.ENGLISH.getLanguage());
            addNewText.setStringValue(SoapHelper.getSoapFaultReasonText(firstException.getCode()));

            fault.addNewDetail().set(
                    CodingHelper.encodeObjectToXml(OWSConstants.NS_OWS, firstException, CollectionHelper
                            .map(new AbstractMap.SimpleEntry<SosConstants.HelperValues, String>(
                                    SosConstants.HelperValues.ENCODE_OWS_EXCEPTION_ONLY, ""))));
        }
        return faultDoc;
    }

    // private void checkAndValidateSoapMessage(XmlObject response) {
    // try {
    // XmlHelper.validateDocument(response);
    // } catch (OwsExceptionReport e) {
    // LOGGER.info("Error while checking SOAP response", e);
    // }
    // }
}
