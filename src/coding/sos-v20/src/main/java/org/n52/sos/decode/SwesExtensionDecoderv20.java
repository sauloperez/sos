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

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swes.SwesConstants;
import org.n52.sos.ogc.swes.SwesExtension;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class SwesExtensionDecoderv20 implements Decoder<SwesExtension<?>, XmlObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwesDecoderv20.class);

    private static final Set<DecoderKey> DECODER_KEYS = CodingHelper.decoderKeysForElements(
    /* SwesConstants.NS_SWES_20 */"http://www.w3.org/2001/XMLSchema", XmlAnyTypeImpl.class);

    public SwesExtensionDecoderv20() {
        LOGGER.debug("Decoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(DECODER_KEYS));
    }

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(DECODER_KEYS);
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
    public SwesExtension<?> decode(final XmlObject xmlObject) throws OwsExceptionReport,
            UnsupportedDecoderInputException {
        if (isSwesExtension(xmlObject)) {
            final XmlObject[] children = xmlObject.selectPath("./*");

            if (children.length == 1) {
                final Object xmlObj = CodingHelper.decodeXmlElement(children[0]);
                final SwesExtension<Object> extension = new SwesExtension<Object>();
                extension.setValue(xmlObj);
                if (isSweAbstractDataComponent(xmlObj)) {
                    extension.setDefinition(((SweAbstractDataComponent) xmlObj).getDefinition());
                }
                return extension;
            }
        }
        throw new UnsupportedDecoderInputException(this, xmlObject);
    }

    private boolean isSweAbstractDataComponent(final Object xmlObj) {
        return xmlObj instanceof SweAbstractDataComponent && ((SweAbstractDataComponent) xmlObj).isSetDefinition();
    }

    private boolean isSwesExtension(final XmlObject xmlObject) {
        return xmlObject.getDomNode().getNamespaceURI().equalsIgnoreCase(SwesConstants.NS_SWES_20)
                && xmlObject.getDomNode().getLocalName().equalsIgnoreCase("extension");
    }

}
