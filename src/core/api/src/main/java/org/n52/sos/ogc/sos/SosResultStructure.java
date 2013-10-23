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
package org.n52.sos.ogc.sos;

import java.util.Map;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject.Factory;
import org.n52.sos.exception.ows.concrete.DecoderResponseUnsupportedException;
import org.n52.sos.exception.ows.concrete.XmlDecodingException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.SosHelper;
import org.n52.sos.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * @since 4.0.0
 * 
 */
public class SosResultStructure {

    private static final Logger LOGGER = LoggerFactory.getLogger(SosHelper.class);

    private SweAbstractDataComponent resultStructure;

    private String xml;

    public SosResultStructure() {
    }

    public SosResultStructure(String resultStructure) throws OwsExceptionReport {
        this.xml = resultStructure;
        this.resultStructure = parseResultStructure();
    }

    public String getXml() throws OwsExceptionReport {
        if (!isSetXml() && resultStructure != null) {
            if (resultStructure.isSetXml()) {
                setXml(resultStructure.getXml());
            } else {
                setXml(encodeResultStructure());
            }
        }
        return xml;
    }

    public SosResultStructure setResultStructure(SweAbstractDataComponent resultStructure) {
        this.resultStructure = resultStructure;
        return this;
    }

    public SweAbstractDataComponent getResultStructure() throws OwsExceptionReport {
        if (resultStructure == null && xml != null && !xml.isEmpty()) {
            resultStructure = parseResultStructure();
        }
        return resultStructure;
    }

    public SosResultStructure setXml(String xml) {
        this.xml = xml;
        return this;
    }

    private SweAbstractDataComponent parseResultStructure() throws OwsExceptionReport {
        try {
            Object decodedObject = CodingHelper.decodeXmlObject(Factory.parse(xml));
            if (decodedObject instanceof SweAbstractDataComponent) {
                return (SweAbstractDataComponent) decodedObject;
            } else {
                throw new DecoderResponseUnsupportedException(xml, decodedObject);
            }
        } catch (XmlException xmle) {
            throw new XmlDecodingException("resultStructure", xml, xmle);
        }
    }

    private String encodeResultStructure() throws OwsExceptionReport {
        Map<HelperValues, String> map = Maps.newEnumMap(HelperValues.class);
        map.put(HelperValues.DOCUMENT, null);
        return CodingHelper.encodeObjectToXmlText(SweConstants.NS_SWE_20, getResultStructure(), map);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SosResultStructure) {
            SosResultStructure other = (SosResultStructure) o;
            try {
                if (getResultStructure() == other.getResultStructure()) {
                    return true;
                } else if (getResultStructure() != null) {
                    return getResultStructure().equals(other.getResultStructure());
                }
            } catch (OwsExceptionReport ex) {
                return false;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        try {
            return getResultStructure().hashCode();
        } catch (OwsExceptionReport e) {
            LOGGER.error("Error while parsing resultStructure", e);
        }
        return super.hashCode();
    }

    public boolean isEmpty() {
        return StringHelper.isNotEmpty(xml);
    }

    public boolean isSetXml() {
        return StringHelper.isNotEmpty(xml);
    }
}
