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

import javax.xml.namespace.QName;

import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Helper class for OGC GML. Contains methods to get QName for geometry or time
 * objects.
 * 
 * @since 4.0.0
 * 
 */
public final class GmlHelper {

    public static QName getGml321QnameForGeometry(Geometry geom) {
        if (geom instanceof Point) {
            return new QName(GmlConstants.NS_GML_32, GmlConstants.EN_POINT, GmlConstants.NS_GML);
        } else if (geom instanceof LineString) {
            return new QName(GmlConstants.NS_GML_32, GmlConstants.EN_LINE_STRING, GmlConstants.NS_GML);
        } else if (geom instanceof Polygon) {
            return new QName(GmlConstants.NS_GML_32, GmlConstants.EN_POLYGON, GmlConstants.NS_GML);
        }
        return new QName(GmlConstants.NS_GML_32, GmlConstants.EN_ABSTRACT_GEOMETRY, GmlConstants.NS_GML);
    }

    public static QName getGml321QnameForITime(Time iTime) {
        if (iTime instanceof TimeInstant) {
            return GmlConstants.QN_TIME_INSTANT_32;
        } else if (iTime instanceof TimePeriod) {
            return GmlConstants.QN_TIME_PERIOD_32;
        }
        return GmlConstants.QN_ABSTRACT_TIME_32;
    }

    public static QName getGml311QnameForITime(Time iTime) {
        if (iTime instanceof TimeInstant) {
            return GmlConstants.QN_TIME_INSTANT;
        } else if (iTime instanceof TimePeriod) {
            return GmlConstants.QN_TIME_PERIOD;
        }
        return GmlConstants.QN_ABSTRACT_TIME_32;
    }

    private GmlHelper() {
    }
}
