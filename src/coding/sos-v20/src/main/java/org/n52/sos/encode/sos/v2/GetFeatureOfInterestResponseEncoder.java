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
package org.n52.sos.encode.sos.v2;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import net.opengis.sos.x20.GetFeatureOfInterestResponseDocument;
import net.opengis.sos.x20.GetFeatureOfInterestResponseType;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.om.features.FeatureCollection;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.response.GetFeatureOfInterestResponse;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.util.XmlHelper;
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
        GetFeatureOfInterestResponseDocument document =
                GetFeatureOfInterestResponseDocument.Factory.newInstance(getXmlOptions());
        GetFeatureOfInterestResponseType xbGetFoiResponse = document.addNewGetFeatureOfInterestResponse();
        AbstractFeature feature = response.getAbstractFeature();
        if (feature instanceof FeatureCollection) {
            for (AbstractFeature f : (FeatureCollection) feature) {
                addFeatureOfInterest(f, xbGetFoiResponse);
            }
        } else if (feature instanceof SamplingFeature) {
            addFeatureOfInterest(feature, xbGetFoiResponse);
        }
        XmlHelper.makeGmlIdsUnique(document.getDomNode());
        return document;
    }

    private void addFeatureOfInterest(AbstractFeature feature, GetFeatureOfInterestResponseType response)
            throws OwsExceptionReport {
        Map<HelperValues, String> additionalValues =
                new EnumMap<SosConstants.HelperValues, String>(HelperValues.class);
        Profile activeProfile = getActiveProfile();
        if (activeProfile.isSetEncodeFeatureOfInterestNamespace()) {
            additionalValues.put(HelperValues.ENCODE_NAMESPACE,
                    activeProfile.getEncodingNamespaceForFeatureOfInterest());
        }
        XmlObject encodeObjectToXml = encodeGml(additionalValues, feature);
        response.addNewFeatureMember().set(encodeObjectToXml);
    }

    @Override
    public Set<SchemaLocation> getConcreteSchemaLocations() {
        return Sets.newHashSet(Sos2Constants.SOS_GET_FEATURE_OF_INTEREST_SCHEMA_LOCATION);
    }
}
