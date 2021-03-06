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

import java.net.URI;

import javax.xml.namespace.QName;

import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.w3c.W3CConstants;
import org.n52.sos.wsdl.WSDLFault;
import org.n52.sos.wsdl.WSDLOperation;

/**
 * Constants for the GetDataAvailability SOS operation.
 * 
 * @author Christian Autermann
 * 
 * @since 4.0.0
 */
public interface GetDataAvailabilityConstants {
    String EN_GET_DATA_AVAILABILITY_MEMBER = "dataAvailabilityMember";

    String EN_GET_DATA_AVAILABILITY = "GetDataAvailability";

    String AN_VERSION = "version";

    String AN_SERVICE = "service";

    String EN_GET_DATA_AVAILABILITY_RESPONSE = "GetDataAvailabilityResponse";

    String DATA_AVAILABILITY = "dataAvailability";

    /* TODO is this one right? */
    String CONFORMANCE_CLASS = "http://www.opengis.net/spec/SOS/2.0/conf/daRetrieval";

    /**
     * The operation name.
     */
    String OPERATION_NAME = "GetDataAvailability";

    /**
     * The {@code QName} for {@code sos:dataAvailabilityMember}.
     */
    QName SOS_DATA_AVAILABILITY_MEMBER = new QName(Sos2Constants.NS_SOS_20, EN_GET_DATA_AVAILABILITY_MEMBER,
            SosConstants.NS_SOS_PREFIX);

    /**
     * The {@code QName} for {@code sos:GetDataAvailabilityResponse}.
     */
    QName SOS_GET_DATA_AVAILABILITY_RESPONSE = new QName(Sos2Constants.NS_SOS_20, EN_GET_DATA_AVAILABILITY_RESPONSE,
            SosConstants.NS_SOS_PREFIX);

    /**
     * The {@code QName} for {@code sos:GetDataAvailability}.
     */
    QName SOS_GET_DATA_AVAILABILITY = new QName(Sos2Constants.NS_SOS_20, EN_GET_DATA_AVAILABILITY,
            SosConstants.NS_SOS_PREFIX);

    /**
     * The {@code QName} for {@code version}.
     */
    QName VERSION = new QName(AN_VERSION);

    /**
     * The {@code QName} for {@code service}.
     */
    QName SERVICE = new QName(AN_SERVICE);

    /**
     * The {@code QName} for {@code sos:version}.
     */
    QName SOS_VERSION = new QName(Sos2Constants.NS_SOS_20, AN_VERSION, SosConstants.NS_SOS_PREFIX);

    /**
     * The {@code QName} for {@code sos:service}.
     */
    QName SOS_SERVICE = new QName(Sos2Constants.NS_SOS_20, AN_SERVICE, SosConstants.NS_SOS_PREFIX);

    /**
     * The {@code QName} for {@code xlink:href}.
     */
    QName XLINK_HREF = new QName(W3CConstants.NS_XLINK, W3CConstants.AN_HREF, W3CConstants.NS_XLINK_PREFIX);

    /**
     * The {@code QName} for {@code xlink:title}.
     */
    public static final QName XLINK_TITLE = new QName(W3CConstants.NS_XLINK, W3CConstants.AN_TITLE,
            W3CConstants.NS_XLINK_PREFIX);

    /**
     * The {@code QName} for {@code gml:id}.
     */
    QName GML_ID = new QName(GmlConstants.NS_GML, GmlConstants.AN_ID, GmlConstants.NS_GML_PREFIX);

    /**
     * The {@code QName} for {@code gml:TimePeriod}.
     */
    QName GML_TIME_PERIOD = new QName(GmlConstants.NS_GML, GmlConstants.EN_TIME_PERIOD, GmlConstants.NS_GML_PREFIX);

    /**
     * The {@code QName} for {@code gml:beginPosition}.
     */
    QName GML_BEGIN_POSITION = new QName(GmlConstants.NS_GML, GmlConstants.EN_BEGIN_POSITION,
            GmlConstants.NS_GML_PREFIX);

    /**
     * The {@code QName} for {@code gml:endPosition}.
     */
    QName GML_END_POSITION = new QName(GmlConstants.NS_GML, GmlConstants.EN_END_POSITION, GmlConstants.NS_GML_PREFIX);

    /**
     * The {@code QName} for {@code om:featureOfInterest}.
     */
    QName OM_FEATURE_OF_INTEREST = new QName(OmConstants.NS_OM_2, OmConstants.EN_FEATURE_OF_INTEREST,
            OmConstants.NS_OM_PREFIX);

    /**
     * The {@code QName} for {@code om:observedProperty}.
     */
    QName OM_OBSERVED_PROPERTY = new QName(OmConstants.NS_OM_2, OmConstants.EN_OBSERVED_PROPERTY,
            OmConstants.NS_OM_PREFIX);

    /**
     * The {@code QName} for {@code om:phenomenonTime}.
     */
    QName OM_PHENOMENON_TIME =
            new QName(OmConstants.NS_OM_2, OmConstants.EN_PHENOMENON_TIME, OmConstants.NS_OM_PREFIX);

    /**
     * The {@code QName} for {@code om:procedure}.
     */
    QName OM_PROCEDURE = new QName(OmConstants.NS_OM_2, OmConstants.EN_PROCEDURE, OmConstants.NS_OM_PREFIX);

    /**
     * The available parameters of the operation.
     */
    enum GetDataAvailabilityParams {
        featureOfInterest, observedProperty, procedure;
    }

    WSDLOperation WSDL_OPERATION =
            WSDLOperation
                    .newWSDLOperation()
                    .setName(OPERATION_NAME)
                    .setVersion(Sos2Constants.SERVICEVERSION)
                    .setRequest(SOS_GET_DATA_AVAILABILITY)
                    .setRequestAction(
                            URI.create("http://www.opengis.net/def/serviceOperation/sos/daRetrieval/2.0/GetDataAvailability"))
                    .setResponse(SOS_GET_DATA_AVAILABILITY_RESPONSE)
                    .setResponseAction(
                            URI.create("http://www.opengis.net/def/serviceOperation/sos/daRetrieval/2.0/GetDataAvailabilityResponse"))
                    .setFaults(WSDLFault.DEFAULT_FAULTS).build();
}
