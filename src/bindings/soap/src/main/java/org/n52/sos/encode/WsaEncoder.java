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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.util.http.MediaTypes;
import org.n52.sos.w3c.SchemaLocation;
import org.n52.sos.wsa.WsaConstants;
import org.n52.sos.wsa.WsaHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 * 
 */
public class WsaEncoder implements Encoder<Map<QName, String>, WsaHeader> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WsaEncoder.class);

    private static final Set<EncoderKey> ENCODER_KEYS = CodingHelper.encoderKeysForElements(WsaConstants.NS_WSA,
            WsaHeader.class);

    public WsaEncoder() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(ENCODER_KEYS));
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.emptyMap();
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
    }

    @Override
    public MediaType getContentType() {
        return MediaTypes.TEXT_XML;
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        // TODO return valid schemaLocation
        return Sets.newHashSet();
    }

    @Override
    public Map<QName, String> encode(WsaHeader response) throws OwsExceptionReport {
        return encode(response, null);
    }

    @Override
    public Map<QName, String> encode(WsaHeader response, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        if (response == null) {
            throw new UnsupportedEncoderInputException(this, response);
        }
        Map<QName, String> wsaHeaderValues = new HashMap<QName, String>(3);
        if (response.getReplyToAddress() != null && !response.getReplyToAddress().isEmpty()) {
            wsaHeaderValues.put(WsaConstants.QN_TO, response.getReplyToAddress());
        }
        if (response.getMessageID() != null) {
            wsaHeaderValues.put(WsaConstants.QN_RELATES_TO, response.getMessageID());
        }
        if (response.getActionValue() != null) {
            wsaHeaderValues.put(WsaConstants.QN_ACTION, response.getActionValue());
        }
        return wsaHeaderValues;
    }

}
