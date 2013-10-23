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
package org.n52.sos.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.decode.Decoder;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.decode.OperationDecoderKey;
import org.n52.sos.decode.XmlNamespaceDecoderKey;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.XmlEncoderKey;
import org.n52.sos.exception.ows.concrete.NoDecoderForKeyException;
import org.n52.sos.exception.ows.concrete.NoEncoderForKeyException;
import org.n52.sos.exception.ows.concrete.XmlDecodingException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.util.http.MediaTypes;

import com.google.common.collect.Maps;

/**
 * @author Christian Autermann <c.autermann@52north.org> TODO implement
 *         encodeToXml(Object o) using a Map from o.getClass().getName() ->
 *         namespaces
 * @since 4.0.0
 * 
 */
public final class CodingHelper {

    public static Object decodeXmlElement(final XmlObject x) throws OwsExceptionReport {
        return decodeXmlObject(x);
    }

    public static <T> XmlObject encodeObjectToXml(String namespace, T o,
            Map<SosConstants.HelperValues, String> helperValues) throws OwsExceptionReport {
        return getEncoder(namespace, o).encode(o, helperValues);
    }

    public static <T> Encoder<XmlObject, T> getEncoder(final String namespace, final T o) throws OwsExceptionReport {
        final EncoderKey key = getEncoderKey(namespace, o);
        final Encoder<XmlObject, T> encoder = CodingRepository.getInstance().getEncoder(key);
        if (encoder == null) {
            throw new NoEncoderForKeyException(key);
        }
        return encoder;
    }

    public static XmlObject encodeObjectToXml(final String namespace, final Object o) throws OwsExceptionReport {
        return encodeObjectToXml(namespace, o, Maps.<HelperValues, String> newEnumMap(HelperValues.class));
    }

    public static String encodeObjectToXmlText(final String namespace, final Object o) throws OwsExceptionReport {
        return encodeObjectToXml(namespace, o, Maps.<HelperValues, String> newEnumMap(HelperValues.class)).xmlText(
                XmlOptionsHelper.getInstance().getXmlOptions());
    }

    public static String encodeObjectToXmlText(final String namespace, final Object o,
            Map<SosConstants.HelperValues, String> additionalValues) throws OwsExceptionReport {
        return encodeObjectToXml(namespace, o, additionalValues).xmlText(
                XmlOptionsHelper.getInstance().getXmlOptions());
    }

    public static Set<DecoderKey> decoderKeysForElements(final String namespace, final Class<?>... elements) {
        final HashSet<DecoderKey> keys = new HashSet<DecoderKey>(elements.length);
        for (final Class<?> x : elements) {
            keys.add(new XmlNamespaceDecoderKey(namespace, x));
        }
        return keys;
    }

    public static Set<DecoderKey> xmlDecoderKeysForOperation(String service, String version, Enum<?>... operations) {
        final HashSet<DecoderKey> set = new HashSet<DecoderKey>(operations.length);
        for (final Enum<?> o : operations) {
            set.add(new OperationDecoderKey(service, version, o.name(), MediaTypes.TEXT_XML));
            set.add(new OperationDecoderKey(service, version, o.name(), MediaTypes.APPLICATION_XML));
        }
        return set;
    }

    public static Set<DecoderKey> xmlDecoderKeysForOperation(String service, String version, String... operations) {
        HashSet<DecoderKey> set = new HashSet<DecoderKey>(operations.length);
        for (String o : operations) {
            set.add(new OperationDecoderKey(service, version, o, MediaTypes.TEXT_XML));
            set.add(new OperationDecoderKey(service, version, o, MediaTypes.APPLICATION_XML));
        }
        return set;
    }

    public static Set<EncoderKey> encoderKeysForElements(final String namespace, final Class<?>... elements) {
        final HashSet<EncoderKey> keys = new HashSet<EncoderKey>(elements.length);
        for (final Class<?> x : elements) {
            keys.add(new XmlEncoderKey(namespace, x));
        }
        return keys;
    }

    public static EncoderKey getEncoderKey(final String namespace, final Object o) {
        return new XmlEncoderKey(namespace, o.getClass());
    }

    public static DecoderKey getDecoderKey(final XmlObject doc) {
        return new XmlNamespaceDecoderKey(XmlHelper.getNamespace(doc), doc.getClass());
    }

    public static <T extends XmlObject> DecoderKey getDecoderKey(final T[] doc) {
        return new XmlNamespaceDecoderKey(XmlHelper.getNamespace(doc[0]), doc.getClass());
    }

    public static Object decodeXmlObject(final XmlObject xbObject) throws OwsExceptionReport {
        final DecoderKey key = getDecoderKey(xbObject);
        final Decoder<?, XmlObject> decoder = CodingRepository.getInstance().getDecoder(key);
        if (decoder == null) {
            throw new NoDecoderForKeyException(key);
        }
        return decoder.decode(xbObject);
    }

    public static Object decodeXmlObject(final String xmlString) throws OwsExceptionReport {
        try {
            return decodeXmlObject(XmlObject.Factory.parse(xmlString));
        } catch (final XmlException e) {
            throw new XmlDecodingException("XML string", xmlString, e);
        }
    }

    private CodingHelper() {
    }
}
