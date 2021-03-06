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
package org.n52.sos.gda;

import java.util.Collections;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.n52.sos.decode.AbstractXmlDecoder;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * {@code Decoder} to handle {@link GetDataAvailabilityRequest}s.
 * 
 * @author Christian Autermann
 * 
 * @since 4.0.0
 */
public class GetDataAvailabilityXmlDecoder extends AbstractXmlDecoder<GetDataAvailabilityRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(GetDataAvailabilityXmlDecoder.class);

    private static final String XPATH_PREFIXES = String.format("declare namespace sos='%s';", Sos2Constants.NS_SOS_20);

    private static final String BASE_PATH = XPATH_PREFIXES + "/sos:GetDataAvailability";

    @SuppressWarnings("unchecked")
    private static final Set<DecoderKey> DECODER_KEYS = CollectionHelper.union(CodingHelper.decoderKeysForElements(
            Sos2Constants.NS_SOS_20, XmlObject.class), CodingHelper.xmlDecoderKeysForOperation(SosConstants.SOS,
            Sos2Constants.SERVICEVERSION, GetDataAvailabilityConstants.OPERATION_NAME));

    /**
     * Constructs a new {@code GetDataAvailabilityDecoder}.
     */
    public GetDataAvailabilityXmlDecoder() {
        LOG.debug("Decoder for the following keys initialized successfully: {}!", Joiner.on(", ").join(DECODER_KEYS));
    }

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public GetDataAvailabilityRequest decode(XmlObject xml) throws OwsExceptionReport {
        return parseGetDataAvailability(xml);
    }

    /**
     * Parses a {@code GetDataAvailabilityRequest}.
     * 
     * @param xml
     *            the request
     * 
     * @return the parsed request
     */
    public GetDataAvailabilityRequest parseGetDataAvailability(XmlObject xml) {
        GetDataAvailabilityRequest request = new GetDataAvailabilityRequest();
        XmlObject[] roots = xml.selectPath(BASE_PATH);
        if (roots != null && roots.length > 0) {
            XmlObject version = roots[0].selectAttribute(GetDataAvailabilityConstants.SOS_VERSION);
            if (version == null) {
                version = roots[0].selectAttribute(GetDataAvailabilityConstants.VERSION);
            }
            if (version != null) {
                request.setVersion(((XmlAnyTypeImpl) version).getStringValue());
            }
            XmlObject service = roots[0].selectAttribute(GetDataAvailabilityConstants.SOS_SERVICE);
            if (service == null) {
                service = roots[0].selectAttribute(GetDataAvailabilityConstants.SERVICE);
            }
            if (service != null) {
                request.setService(((XmlAnyTypeImpl) service).getStringValue());
            }
        }

        for (XmlObject x : xml.selectPath(BASE_PATH + "/sos:observedProperty")) {
            request.addObservedProperty(((XmlAnyTypeImpl) x).getStringValue());
        }
        for (XmlObject x : xml.selectPath(BASE_PATH + "/sos:procedure")) {
            request.addProcedure(((XmlAnyTypeImpl) x).getStringValue());
        }
        for (XmlObject x : xml.selectPath(BASE_PATH + "/sos:featureOfInterest")) {
            request.addFeatureOfInterest(((XmlAnyTypeImpl) x).getStringValue());
        }
        return request;
    }
}
