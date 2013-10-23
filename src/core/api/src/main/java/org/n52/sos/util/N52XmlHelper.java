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

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlCursor.TokenType;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.ogc.swes.SwesConstants;
import org.n52.sos.w3c.SchemaLocation;
import org.n52.sos.w3c.W3CConstants;

import com.google.common.collect.Sets;

/**
 * Utility class for 52N
 * 
 * @since 4.0.0
 * 
 */
public final class N52XmlHelper implements Constants {
    public static final SchemaLocation SCHEMA_LOCATION_SOS_V1 = new SchemaLocation(Sos1Constants.NS_SOS,
            Sos1Constants.SCHEMA_LOCATION_SOS);

    public static final SchemaLocation SCHEMA_LOCATION_SOS_V2 = new SchemaLocation(Sos2Constants.NS_SOS_20,
            Sos2Constants.SCHEMA_LOCATION_URL_SOS);

    public static final SchemaLocation SCHEMA_LOCATION_OM_100 = new SchemaLocation(OmConstants.NS_OM,
            OmConstants.SCHEMA_LOCATION_URL_OM_CONSTRAINT);

    public static final SchemaLocation SCHEMA_LOCATION_OM_200 = new SchemaLocation(OmConstants.NS_OM_2,
            OmConstants.SCHEMA_LOCATION_URL_OM_20);

    public static final SchemaLocation SCHEMA_LOCATION_GML_311 = new SchemaLocation(GmlConstants.NS_GML,
            GmlConstants.SCHEMA_LOCATION_URL_GML_311);

    public static final SchemaLocation SCHEMA_LOCATION_GML_321 = new SchemaLocation(GmlConstants.NS_GML_32,
            GmlConstants.SCHEMA_LOCATION_URL_GML_32);

    public static final SchemaLocation SCHEMA_LOCATION_OGC = new SchemaLocation(OGCConstants.NS_OGC,
            OGCConstants.SCHEMA_LOCATION_OGC);

    public static final SchemaLocation SCHEMA_LOCATION_OWS_110 = new SchemaLocation(OWSConstants.NS_OWS,
            OWSConstants.SCHEMA_LOCATION_URL_OWS);

    public static final SchemaLocation SCHEMA_LOCATION_OWS_110_ER = new SchemaLocation(OWSConstants.NS_OWS,
            OWSConstants.SCHEMA_LOCATION_URL_OWS_EXCEPTIONREPORT);

    public static final SchemaLocation SCHEMA_LOCATION_SA_100 = new SchemaLocation(SfConstants.NS_SA,
            SfConstants.SCHEMA_LOCATION_URL_SA);

    public static final SchemaLocation SCHEMA_LOCATION_SF_20 = new SchemaLocation(SfConstants.NS_SF,
            SfConstants.SCHEMA_LOCATION_URL_SF);

    public static final SchemaLocation SCHEMA_LOCATION_SAMS_20 = new SchemaLocation(SfConstants.NS_SAMS,
            SfConstants.SCHEMA_LOCATION_URL_SAMS);

    public static final SchemaLocation SCHEMA_LOCATION_SML_101 = new SchemaLocation(SensorMLConstants.NS_SML,
            SensorMLConstants.SCHEMA_LOCATION_URL_SML_101);

    public static final SchemaLocation SCHEMA_LOCATION_SWE_101 = new SchemaLocation(SweConstants.NS_SWE_101,
            SweConstants.SCHEMA_LOCATION_URL_SWE_101);

    public static final SchemaLocation SCHEMA_LOCATION_SWE_200 = new SchemaLocation(SweConstants.NS_SWE_20,
            SweConstants.SCHEMA_LOCATION_URL_SWE_20);

    public static final SchemaLocation SCHEMA_LOCATION_SWES_200 = new SchemaLocation(SwesConstants.NS_SWES_20,
            SwesConstants.SCHEMA_LOCATION_URL_SWES_20);

    public static final SchemaLocation SCHEMA_LOCATION_XLINK = new SchemaLocation(W3CConstants.NS_XLINK,
            W3CConstants.SCHEMA_LOCATION_XLINK);

    public static final SchemaLocation SCHEMA_LOCATION_SOAP_12 = new SchemaLocation(
            SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE);

    /**
     * Sets the schema location to a XmlObject
     * 
     * @param document
     *            XML document
     * @param schemaLocations
     *            schema location
     */
    public static void setSchemaLocationToDocument(XmlObject document, String schemaLocations) {
        XmlCursor cursor = document.newCursor();
        if (cursor.toFirstChild()) {
            cursor.setAttributeText(getSchemaLocationQName(), schemaLocations);
        }
        cursor.dispose();
    }

    /**
     * Sets the schema locations to a XmlObject
     * 
     * @param document
     *            XML document
     * @param schemaLocations
     *            List of schema locations
     */
    public static void setSchemaLocationsToDocument(XmlObject document, Collection<SchemaLocation> schemaLocations) {
        StringBuilder schemaLocation = new StringBuilder();
        Iterator<SchemaLocation> it = schemaLocations.iterator();
        if (it.hasNext()) {
            schemaLocation.append(it.next().getSchemaLocationString());
        }
        while (it.hasNext()) {
            schemaLocation.append(BLANK_CHAR).append(it.next().getSchemaLocationString());
        }
        setSchemaLocationToDocument(document, schemaLocation.toString());
    }

    public static Set<String> getNamespaces(XmlObject xmlObject) {
        Set<String> namespaces = Sets.newHashSet();
        XmlCursor newCursor = xmlObject.newCursor();
        while (newCursor.hasNextToken()) {
            TokenType evt = newCursor.toNextToken();
            if (evt == TokenType.START) {
                QName qn = newCursor.getName();
                if (qn != null) {
                    namespaces.add(qn.getNamespaceURI());
                }
            }
        }
        return namespaces;
    }

    /**
     * W3C XSI schema location
     * 
     * @return QName of schema location
     */
    public static QName getSchemaLocationQName() {
        return W3CConstants.QN_SCHEMA_LOCATION;
    }

    /**
     * W3C XSI schema location with prefix
     * 
     * @return QName of schema location
     */
    public static QName getSchemaLocationQNameWithPrefix() {
        return W3CConstants.QN_SCHEMA_LOCATION_PREFIXED;
    }

    /**
     * SOS 1.0.0 schema location
     * 
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForSOS100() {
        return SCHEMA_LOCATION_SOS_V1;
    }

    /**
     * SOS 2.0 schema location
     * 
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForSOS200() {
        return SCHEMA_LOCATION_SOS_V2;
    }

    /**
     * OM 1.0.0 schema location
     * 
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForOM100() {
        return SCHEMA_LOCATION_OM_100;
    }

    /**
     * OM 2.0 schema location
     * 
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForOM200() {
        return SCHEMA_LOCATION_OM_200;
    }

    /**
     * GML 3.1.1 schema location
     * 
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForGML311() {
        return SCHEMA_LOCATION_GML_311;
    }

    /**
     * GML 3.2.1 schema location
     * 
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForGML321() {
        return SCHEMA_LOCATION_GML_321;
    }

    /**
     * SOS OGC schema location
     * 
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForOGC() {
        return SCHEMA_LOCATION_OGC;
    }

    /**
     * OWS 1.1.0 schema location
     * 
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForOWS110() {
        return SCHEMA_LOCATION_OWS_110;
    }

    /**
     * OWS 1.1.0 schema location
     * 
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForOWS110Exception() {
        return SCHEMA_LOCATION_OWS_110_ER;
    }

    /**
     * Sampling 1.0.0 schema location
     * 
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForSA100() {
        return SCHEMA_LOCATION_SA_100;
    }

    /**
     * Sampling 2.0 schema location
     * 
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForSF200() {
        return SCHEMA_LOCATION_SF_20;
    }

    /**
     * SamplingSpatial 2.0 schema location
     * 
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForSAMS200() {
        return SCHEMA_LOCATION_SAMS_20;
    }

    /**
     * SensorML 1.0.1 schema location
     * 
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForSML101() {
        return SCHEMA_LOCATION_SML_101;
    }

    /**
     * SWECommon 1.0.1 schema location
     * 
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForSWE101() {
        return SCHEMA_LOCATION_SWE_101;
    }

    /**
     * SWECommon 2.0 schema location
     * 
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForSWE200() {
        return SCHEMA_LOCATION_SWE_200;
    }

    /**
     * SWECommon 2.0 schema location
     * 
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForSWES200() {
        return SCHEMA_LOCATION_SWES_200;
    }

    /**
     * W3C XLINK schema location
     * 
     * @return QName of schema location
     */
    public static SchemaLocation getSchemaLocationForXLINK() {
        return SCHEMA_LOCATION_XLINK;
    }

    public static SchemaLocation getSchemaLocationForSOAP12() {
        return SCHEMA_LOCATION_SOAP_12;
    }

    private N52XmlHelper() {
    }
}
