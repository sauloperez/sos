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

import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.joda.time.DateTime;
import org.n52.sos.exception.ows.concrete.DateTimeFormatException;
import org.n52.sos.gda.GetDataAvailabilityResponse.DataAvailability;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.time.Time.TimeFormat;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.w3c.W3CConstants;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class GetDataAvailabilityStreamWriter {
    private static final String TIME_PERIOD_PREFIX = "tp_";

    private static final String DATA_AVAILABILITY_PREFIX = "dam_";

    private final XMLEventFactory eventFactory = XMLEventFactory.newInstance();

    private final XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

    private final List<DataAvailability> gdas;

    private final Map<TimePeriod, String> times;

    private final String version;

    private int dataAvailabilityCount = 1;

    private int timePeriodCount = 1;

    private XMLEventWriter w;

    public GetDataAvailabilityStreamWriter(String version, List<DataAvailability> gdas) {
        this.gdas = gdas == null ? Collections.<DataAvailability> emptyList() : gdas;
        this.times = new HashMap<TimePeriod, String>(this.gdas.size());
        this.version = version == null ? Sos2Constants.SERVICEVERSION : version;
    }

    protected void attr(QName name, String value) throws XMLStreamException {
        w.add(eventFactory.createAttribute(name, value));
    }

    protected void attr(String name, String value) throws XMLStreamException {
        w.add(eventFactory.createAttribute(name, value));
    }

    protected void chars(String chars) throws XMLStreamException {
        w.add(eventFactory.createCharacters(chars));
    }

    protected void end(QName name) throws XMLStreamException {
        w.add(eventFactory.createEndElement(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart()));
    }

    protected void end() throws XMLStreamException {
        w.add(eventFactory.createEndDocument());
    }

    protected void namespace(String prefix, String namespace) throws XMLStreamException {
        w.add(eventFactory.createNamespace(prefix, namespace));
    }

    protected void start(QName name) throws XMLStreamException {
        w.add(eventFactory.createStartElement(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart()));
    }

    protected void start() throws XMLStreamException {
        w.add(eventFactory.createStartDocument());
    }

    public void write(OutputStream out) throws XMLStreamException, DateTimeFormatException {
        this.w = outputFactory.createXMLEventWriter(out, "UTF-8");
        start();
        writeGetDataAvailabilityResponse();
        end();
        this.w.flush();
        this.w.close();
    }

    protected void writeGetDataAvailabilityResponse() throws XMLStreamException, DateTimeFormatException {
        start(GetDataAvailabilityConstants.SOS_GET_DATA_AVAILABILITY_RESPONSE);
        attr(GetDataAvailabilityConstants.AN_SERVICE, SosConstants.SOS);
        attr(GetDataAvailabilityConstants.AN_VERSION, version);
        namespace(SosConstants.NS_SOS_PREFIX, Sos2Constants.NS_SOS_20);
        namespace(GmlConstants.NS_GML_PREFIX, GmlConstants.NS_GML_32);
        namespace(OmConstants.NS_OM_PREFIX, OmConstants.NS_OM_2);
        namespace(W3CConstants.NS_XLINK_PREFIX, W3CConstants.NS_XLINK);
        for (DataAvailability da : this.gdas) {
            wirteDataAvailabilityMember(da);
        }
        end(GetDataAvailabilityConstants.SOS_GET_DATA_AVAILABILITY_RESPONSE);
    }

    protected void wirteDataAvailabilityMember(DataAvailability da) throws XMLStreamException, DateTimeFormatException {
        start(GetDataAvailabilityConstants.SOS_DATA_AVAILABILITY_MEMBER);
        attr(GetDataAvailabilityConstants.GML_ID, DATA_AVAILABILITY_PREFIX + dataAvailabilityCount++);
        writeFeatureOfInterest(da);
        writeProcedure(da);
        writeObservedProperty(da);
        writePhenomenonTime(da);
        end(GetDataAvailabilityConstants.SOS_DATA_AVAILABILITY_MEMBER);
    }

    protected void writePhenomenonTime(DataAvailability da) throws DateTimeFormatException, XMLStreamException {
        start(GetDataAvailabilityConstants.OM_PHENOMENON_TIME);
        if (times.containsKey(da.getPhenomenonTime())) {
            attr(GetDataAvailabilityConstants.XLINK_HREF, "#" + times.get(da.getPhenomenonTime()));
        } else {
            da.getPhenomenonTime().setGmlId(TIME_PERIOD_PREFIX + timePeriodCount++);
            times.put(da.getPhenomenonTime(), da.getPhenomenonTime().getGmlId());
            writeTimePeriod(da.getPhenomenonTime());
        }
        end(GetDataAvailabilityConstants.OM_PHENOMENON_TIME);
    }

    protected void writeFeatureOfInterest(DataAvailability da) throws XMLStreamException {
        start(GetDataAvailabilityConstants.OM_FEATURE_OF_INTEREST);
        attr(GetDataAvailabilityConstants.XLINK_HREF, da.getFeatureOfInterest().getHref());
        if (da.getFeatureOfInterest().isSetTitle()) {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getFeatureOfInterest().getTitle());
        } else {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getFeatureOfInterest().getTitleFromHref());
        }
        end(GetDataAvailabilityConstants.OM_FEATURE_OF_INTEREST);
    }

    protected void writeProcedure(DataAvailability da) throws XMLStreamException {
        start(GetDataAvailabilityConstants.OM_PROCEDURE);
        attr(GetDataAvailabilityConstants.XLINK_HREF, da.getProcedure().getHref());
        if (da.getProcedure().isSetTitle()) {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getProcedure().getTitle());
        } else {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getProcedure().getTitleFromHref());
        }
        end(GetDataAvailabilityConstants.OM_PROCEDURE);
    }

    protected void writeObservedProperty(DataAvailability da) throws XMLStreamException {
        start(GetDataAvailabilityConstants.OM_OBSERVED_PROPERTY);
        attr(GetDataAvailabilityConstants.XLINK_HREF, da.getObservedProperty().getHref());
        if (da.getObservedProperty().isSetTitle()) {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getObservedProperty().getTitle());
        } else {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getObservedProperty().getTitleFromHref());
        }
        end(GetDataAvailabilityConstants.OM_OBSERVED_PROPERTY);
    }

    protected void writeTimePeriod(TimePeriod tp) throws XMLStreamException, DateTimeFormatException {
        start(GetDataAvailabilityConstants.GML_TIME_PERIOD);
        attr(GetDataAvailabilityConstants.GML_ID, tp.getGmlId());
        writeBegin(tp);
        writeEnd(tp);
        end(GetDataAvailabilityConstants.GML_TIME_PERIOD);
    }

    protected void writeBegin(TimePeriod tp) throws XMLStreamException, DateTimeFormatException {
        start(GetDataAvailabilityConstants.GML_BEGIN_POSITION);
        writeTimeString(tp.getStart(), tp.getTimeFormat());
        end(GetDataAvailabilityConstants.GML_BEGIN_POSITION);
    }

    protected void writeEnd(TimePeriod tp) throws XMLStreamException, DateTimeFormatException {
        start(GetDataAvailabilityConstants.GML_END_POSITION);
        writeTimeString(tp.getEnd(), tp.getTimeFormat());
        end(GetDataAvailabilityConstants.GML_END_POSITION);
    }

    protected void writeTimeString(DateTime time, TimeFormat format) throws XMLStreamException,
            DateTimeFormatException {
        chars(DateTimeHelper.formatDateTime2String(time, format));
    }
}
