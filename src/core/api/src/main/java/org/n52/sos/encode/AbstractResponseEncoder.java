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
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.coding.OperationKey;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.util.N52XmlHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.util.http.MediaTypes;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 * 
 * @param <T>
 *            the response type
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public abstract class AbstractResponseEncoder<T extends AbstractServiceResponse> extends AbstractXmlEncoder<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractResponseEncoder.class);

    private final Set<EncoderKey> encoderKeys;

    private final String namespace;

    private final String prefix;

    private final String version;

    private final Class<T> responseType;

    private final boolean validate;

    public AbstractResponseEncoder(String service, String version, String operation, String namespace, String prefix,
            Class<T> responseType, boolean validate) {
        OperationKey key = new OperationKey(service, version, operation);
        this.encoderKeys =
                Sets.newHashSet(new XmlEncoderKey(namespace, responseType), new OperationEncoderKey(key,
                        MediaTypes.TEXT_XML), new OperationEncoderKey(key, MediaTypes.APPLICATION_XML));
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!", Joiner.on(", ").join(encoderKeys));
        this.namespace = namespace;
        this.prefix = prefix;
        this.version = version;
        this.responseType = responseType;
        this.validate = validate;
    }

    public AbstractResponseEncoder(String service, String version, String operation, String namespace, String prefix,
            Class<T> responseType) {
        this(service, version, operation, namespace, prefix, responseType, true);
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(encoderKeys);
    }

    @Override
    public void addNamespacePrefixToMap(final Map<String, String> nameSpacePrefixMap) {
        if (nameSpacePrefixMap != null) {
            nameSpacePrefixMap.put(this.namespace, this.prefix);
        }
    }

    @Override
    public XmlObject encode(T response) throws OwsExceptionReport {
        if (response == null) {
            throw new UnsupportedEncoderInputException(this, response);
        }
        final Map<HelperValues, String> additionalValues = new EnumMap<HelperValues, String>(HelperValues.class);
        additionalValues.put(HelperValues.VERSION, this.version);
        return encode(response, additionalValues);
    }

    @Override
    public XmlObject encode(T response, Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        if (response == null) {
            throw new UnsupportedEncoderInputException(this, response);
        }
        XmlObject xml = create(response);
        setSchemaLocations(xml);
        if (validate) {
            boolean valid = XmlHelper.validateDocument(xml);
            if (valid) {
                LOGGER.debug("Encoded object {} is valid: {}", xml.schemaType().toString(), valid);
            } else {
                LOGGER.warn("Encoded object {} is valid: {}", xml.schemaType().toString(), valid);
            }
        }
        return xml;
    }

    private void setSchemaLocations(XmlObject document) {
        Map<String, SchemaLocation> schemaLocations = Maps.newHashMap();
        for (String ns : N52XmlHelper.getNamespaces(document)) {
            for (SchemaLocation sl : CodingRepository.getInstance().getSchemaLocation(ns)) {
                schemaLocations.put(sl.getNamespace(), sl);
            }
        }
        for (SchemaLocation sl : getSchemaLocations()) {
            schemaLocations.put(sl.getNamespace(), sl);
        }
        // override default schema location with concrete URL's
        for (SchemaLocation sl : getConcreteSchemaLocations()) {
            schemaLocations.put(sl.getNamespace(), sl);
        }
        N52XmlHelper.setSchemaLocationsToDocument(document, schemaLocations.values());
    }

    protected XmlOptions getXmlOptions() {
        return XmlOptionsHelper.getInstance().getXmlOptions();
    }

    protected abstract Set<SchemaLocation> getConcreteSchemaLocations();

    protected abstract XmlObject create(T response) throws OwsExceptionReport;

    protected Class<T> getResponseType() {
        return responseType;
    }

}
