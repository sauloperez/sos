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
package org.n52.sos.encode.sos.v1;

import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.Encoder;
import org.n52.sos.exception.ows.concrete.NoEncoderForResponseException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.response.GetFeatureOfInterestResponse;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.N52XmlHelper;
import org.n52.sos.w3c.SchemaLocation;

import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class GetFeatureOfInterestResponseEncoder extends AbstractSosResponseEncoder<GetFeatureOfInterestResponse> {
    public GetFeatureOfInterestResponseEncoder() {
        super(SosConstants.Operations.GetFeatureOfInterest.name(), GetFeatureOfInterestResponse.class);
    }

    @Override
    protected XmlObject create(GetFeatureOfInterestResponse response) throws OwsExceptionReport {
        Encoder<XmlObject, AbstractFeature> encoder =
                CodingHelper.getEncoder(GmlConstants.NS_GML, response.getAbstractFeature());
        if (encoder != null) {
            return encoder.encode(response.getAbstractFeature());
        } else {
            throw new NoEncoderForResponseException();
        }

    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(N52XmlHelper.getSchemaLocationForSOS100(), N52XmlHelper.getSchemaLocationForGML311(),
                N52XmlHelper.getSchemaLocationForSA100());
    }
}
