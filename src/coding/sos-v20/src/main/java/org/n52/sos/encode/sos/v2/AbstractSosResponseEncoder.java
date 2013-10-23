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

import static org.n52.sos.util.CodingHelper.encodeObjectToXml;

import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.AbstractResponseEncoder;
import org.n52.sos.ogc.filter.FilterConstants;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.w3c.SchemaLocation;

import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 * 
 * @param <T>
 *            the response type
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public abstract class AbstractSosResponseEncoder<T extends AbstractServiceResponse> extends AbstractResponseEncoder<T> {
    public AbstractSosResponseEncoder(String operation, Class<T> responseType) {
        super(SosConstants.SOS, Sos2Constants.SERVICEVERSION, operation, Sos2Constants.NS_SOS_20,
                SosConstants.NS_SOS_PREFIX, responseType);
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(Sos2Constants.SOS_SCHEMA_LOCATION);
    }

    protected Profile getActiveProfile() {
        return Configurator.getInstance().getProfileHandler().getActiveProfile();
    }

    protected XmlObject encodeGml(Object o) throws OwsExceptionReport {
        return encodeObjectToXml(GmlConstants.NS_GML_32, o);
    }

    protected XmlObject encodeGml(Map<HelperValues, String> helperValues, Object o) throws OwsExceptionReport {
        return encodeObjectToXml(GmlConstants.NS_GML_32, o, helperValues);
    }

    protected XmlObject encodeOws(Object o) throws OwsExceptionReport {
        return encodeObjectToXml(OWSConstants.NS_OWS, o);
    }

    protected XmlObject encodeOws(Map<HelperValues, String> helperValues, Object o) throws OwsExceptionReport {
        return encodeObjectToXml(OWSConstants.NS_OWS, o, helperValues);
    }

    protected XmlObject encodeFes(Object o) throws OwsExceptionReport {
        return encodeObjectToXml(FilterConstants.NS_FES_2, o);
    }

    protected XmlObject encodeFes(Map<HelperValues, String> helperValues, Object o) throws OwsExceptionReport {
        return encodeObjectToXml(FilterConstants.NS_FES_2, o, helperValues);
    }

    protected XmlObject encodeSwe(Object o) throws OwsExceptionReport {
        return encodeObjectToXml(SweConstants.NS_SWE_20, o);
    }

    protected XmlObject encodeSwe(Map<HelperValues, String> helperValues, Object o) throws OwsExceptionReport {
        return encodeObjectToXml(SweConstants.NS_SWE_20, o, helperValues);
    }
}
