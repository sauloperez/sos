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

import net.opengis.fes.x20.BBOXType;
import net.opengis.fes.x20.BinaryTemporalOpType;
import net.opengis.fes.x20.SpatialOpsType;
import net.opengis.fes.x20.TemporalOpsType;
import net.opengis.fes.x20.ValueReferenceDocument;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlObject.Factory;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ogc.filter.FilterConstants;
import org.n52.sos.ogc.filter.FilterConstants.TimeOperator;
import org.n52.sos.ogc.filter.FilterConstants.TimeOperator2;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import com.vividsolutions.jts.geom.Geometry;

/**
 * @since 4.0.0
 * 
 */
public class FesDecoderv20 implements Decoder<Object, XmlObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FesDecoderv20.class);

    private static final Set<DecoderKey> DECODER_KEYS = CodingHelper.decoderKeysForElements(FilterConstants.NS_FES_2,
            SpatialOpsType.class, TemporalOpsType.class);

    public FesDecoderv20() {
        StringBuilder builder = new StringBuilder();
        for (DecoderKey decoderKeyType : DECODER_KEYS) {
            builder.append(decoderKeyType.toString());
            builder.append(", ");
        }
        builder.delete(builder.lastIndexOf(", "), builder.length());
        LOGGER.debug("Decoder for the following keys initialized successfully: " + builder.toString() + "!");
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
    public Object decode(XmlObject xmlObject) throws OwsExceptionReport {
        if (xmlObject instanceof SpatialOpsType) {
            return parseSpatialFilterType((SpatialOpsType) xmlObject);
        } else if (xmlObject instanceof TemporalOpsType) {
            return parseTemporalFilterType((TemporalOpsType) xmlObject);
        } else {
            throw new UnsupportedDecoderInputException(this, xmlObject);
        }
    }

    /**
     * Parses the spatial filter of a request.
     * 
     * @param xbSpatialOpsType
     *            XmlBean representing the feature of interest parameter of the
     *            request
     * @return Returns SpatialFilter created from the passed foi request
     *         parameter
     * 
     * 
     * @throws OwsExceptionReport
     *             * if creation of the SpatialFilter failed
     */
    private SpatialFilter parseSpatialFilterType(SpatialOpsType xbSpatialOpsType) throws OwsExceptionReport {
        SpatialFilter spatialFilter = new SpatialFilter();
        try {
            if (xbSpatialOpsType instanceof BBOXType) {
                spatialFilter.setOperator(FilterConstants.SpatialOperator.BBOX);
                BBOXType xbBBOX = (BBOXType) xbSpatialOpsType;
                if (XmlHelper.getLocalName(xbBBOX.getExpression()).equals(FilterConstants.EN_VALUE_REFERENCE)) {
                    ValueReferenceDocument valueRefernece =
                            ValueReferenceDocument.Factory.parse(xbBBOX.getExpression().getDomNode());
                    spatialFilter.setValueReference(valueRefernece.getValueReference().trim());
                }
                XmlCursor geometryCursor = xbSpatialOpsType.newCursor();
                if (geometryCursor.toChild(GmlConstants.QN_ENVELOPE_32)) {
                    Object sosGeometry = CodingHelper.decodeXmlObject(Factory.parse(geometryCursor.getDomNode()));
                    if (sosGeometry instanceof Geometry) {
                        spatialFilter.setGeometry((Geometry) sosGeometry);
                    } else {
                        throw new UnsupportedDecoderInputException(this, xbSpatialOpsType);
                    }

                } else {
                    throw new InvalidParameterValueException().at(Sos2Constants.GetObservationParams.spatialFilter)
                            .withMessage("The requested spatial filter operand is not supported by this SOS!");
                }
                geometryCursor.dispose();
            } else {
                throw new InvalidParameterValueException().at(Sos2Constants.GetObservationParams.spatialFilter)
                        .withMessage("The requested spatial filter is not supported by this SOS!");
            }
        } catch (XmlException xmle) {
            throw new NoApplicableCodeException().causedBy(xmle).withMessage("Error while parsing spatial filter!");
        }
        return spatialFilter;
    }

    /**
     * parses a single temporal filter of the requests and returns SOS temporal
     * filter
     * 
     * @param xbTemporalOpsType
     *            XmlObject representing the temporal filter
     * @return Returns SOS representation of temporal filter
     * 
     * 
     * @throws OwsExceptionReport
     *             * if parsing of the element failed
     */
    private TemporalFilter parseTemporalFilterType(TemporalOpsType xbTemporalOpsType) throws OwsExceptionReport {
        TemporalFilter temporalFilter = new TemporalFilter();
        try {
            if (xbTemporalOpsType instanceof BinaryTemporalOpType) {
                BinaryTemporalOpType btot = (BinaryTemporalOpType) xbTemporalOpsType;
                if (btot.getValueReference() != null && !btot.getValueReference().isEmpty()) {
                    temporalFilter.setValueReference(btot.getValueReference().trim());
                }
                NodeList nodes = btot.getDomNode().getChildNodes();
                for (int i = 0; i < nodes.getLength(); i++) {
                    if (nodes.item(i).getNamespaceURI() != null
                            && !nodes.item(i).getLocalName().equals(FilterConstants.EN_VALUE_REFERENCE)) {
                        Object timeObject = CodingHelper.decodeXmlObject(Factory.parse(nodes.item(i)));
                        if (timeObject instanceof Time) {
                            TimeOperator operator;
                            Time time = (Time) timeObject;
                            String localName = XmlHelper.getLocalName(xbTemporalOpsType);
                            if (localName.equals(TimeOperator2.After.name())) {
                                operator = TimeOperator.TM_After;
                            } else if (localName.equals(TimeOperator2.Before.name())) {
                                operator = TimeOperator.TM_Before;
                            } else if (localName.equals(TimeOperator2.Begins.name())) {
                                operator = TimeOperator.TM_Begins;
                            } else if (localName.equals(TimeOperator2.BegunBy.name())) {
                                operator = TimeOperator.TM_BegunBy;
                            } else if (localName.equals(TimeOperator2.TContains.name())) {
                                operator = TimeOperator.TM_Contains;
                            } else if (localName.equals(TimeOperator2.During.name())) {
                                operator = TimeOperator.TM_During;
                            } else if (localName.equals(TimeOperator2.EndedBy.name())) {
                                operator = TimeOperator.TM_EndedBy;
                            } else if (localName.equals(TimeOperator2.Ends.name())) {
                                operator = TimeOperator.TM_Ends;
                            } else if (localName.equals(TimeOperator2.TEquals.name())) {
                                operator = TimeOperator.TM_Equals;
                            } else if (localName.equals(TimeOperator2.Meets.name())) {
                                operator = TimeOperator.TM_Meets;
                            } else if (localName.equals(TimeOperator2.MetBy.name())) {
                                operator = TimeOperator.TM_MetBy;
                            } else if (localName.equals(TimeOperator2.TOverlaps.name())) {
                                operator = TimeOperator.TM_Overlaps;
                            } else if (localName.equals(TimeOperator2.OverlappedBy.name())) {
                                operator = TimeOperator.TM_OverlappedBy;
                            } else {
                                throw new InvalidParameterValueException().at(
                                        Sos2Constants.GetObservationParams.temporalFilter).withMessage(
                                        "The requested temporal filter operand is not supported by this SOS!");
                            }
                            temporalFilter.setOperator(operator);
                            temporalFilter.setTime(time);
                            break;
                        } else {
                            throw new InvalidParameterValueException().at(
                                    Sos2Constants.GetObservationParams.temporalFilter).withMessage(
                                    "The requested temporal filter value is not supported by this SOS!");
                        }
                    }
                }
            } else {
                throw new InvalidParameterValueException().at(Sos2Constants.GetObservationParams.temporalFilter)
                        .withMessage("The requested temporal filter operand is not supported by this SOS!");
            }
        } catch (XmlException xmle) {
            throw new NoApplicableCodeException().withMessage("Error while parsing temporal filter!").causedBy(xmle);
        }
        return temporalFilter;
    }

}
