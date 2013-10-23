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
package org.hibernate.spatial.dialect.postgis;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.spatial.dialect.AbstractJTSGeometryValueExtractor;
import org.hibernate.spatial.jts.mgeom.MCoordinate;
import org.hibernate.spatial.jts.mgeom.MLineString;
import org.postgis.GeometryCollection;
import org.postgis.MultiLineString;
import org.postgis.MultiPoint;
import org.postgis.MultiPolygon;
import org.postgis.PGboxbase;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.postgis.Polygon;
import org.postgresql.util.PGobject;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

/*
 * This file is part of Hibernate Spatial, an extension to the
 *  hibernate ORM solution for spatial (geographic) data.
 *
 *  Copyright Â© 2007-2012 Geovise BVBA
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
/**
 * @since 4.0.0
 * 
 */
public class PGGeometryValueExtractor52N extends AbstractJTSGeometryValueExtractor {

    private static Pattern boxPattern = Pattern.compile(".*box.*\\((.*)\\)", Pattern.CASE_INSENSITIVE);

    public Geometry toJTS(Object object) {
        if (object == null) {
            return null;
        }

        // in some cases, Postgis returns not PGgeometry objects
        // but org.postgis.Geometry instances.
        // This has been observed when retrieving GeometryCollections
        // as the result of an SQL-operation such as Union.
        if (object instanceof org.postgis.Geometry) {
            object = new PGgeometry((org.postgis.Geometry) object);
        }

        if (object instanceof PGgeometry) {
            PGgeometry geom = (PGgeometry) object;
            com.vividsolutions.jts.geom.Geometry out = null;
            switch (geom.getGeoType()) {
            case org.postgis.Geometry.POINT:
                out = convertPoint((org.postgis.Point) geom.getGeometry());
                break;
            case org.postgis.Geometry.LINESTRING:
                out = convertLineString((org.postgis.LineString) geom.getGeometry());
                break;
            case org.postgis.Geometry.POLYGON:
                out = convertPolygon((org.postgis.Polygon) geom.getGeometry());
                break;
            case org.postgis.Geometry.MULTILINESTRING:
                out = convertMultiLineString((org.postgis.MultiLineString) geom.getGeometry());
                break;
            case org.postgis.Geometry.MULTIPOINT:
                out = convertMultiPoint((org.postgis.MultiPoint) geom.getGeometry());
                break;
            case org.postgis.Geometry.MULTIPOLYGON:
                out = convertMultiPolygon((org.postgis.MultiPolygon) geom.getGeometry());
                break;
            case org.postgis.Geometry.GEOMETRYCOLLECTION:
                out = convertGeometryCollection((org.postgis.GeometryCollection) geom.getGeometry());
                break;
            default:
                throw new RuntimeException("Unknown type of PGgeometry");
            }
            out.setSRID(geom.getGeometry().srid);
            return out;
        } else if (object instanceof org.postgis.PGboxbase) {
            return convertBox((org.postgis.PGboxbase) object);
            // The next two else if blocks are added to support pre-ProstGIS 2.0
            // Versions
        } else if (object instanceof PGobject && ((PGobject) object).getType().contains("box")) {
            PGobject pgo = (PGobject) object;
            // try to extract the box object (if available)
            String boxStr = extractBoxString(pgo);
            if (boxStr == null) {
                throw new IllegalArgumentException("Can't convert object: " + pgo.getType() + " : " + pgo.getValue());
            }
            String[] pointsStr = boxStr.split(",");
            Point ll = toPoint(pointsStr[0]);
            Point ur = toPoint(pointsStr[1]);
            return cornerPointsToPolygon(ll, ur, false);
            // The Postgis driver not always registers geography objects
        } else if (object instanceof PGobject
                && (((PGobject) object).getType().equals("geometry") || ((PGobject) object).getType().equals(
                        "geography"))) {
            return convertFromPGobject((PGobject) object);
        } else {
            throw new IllegalArgumentException("Can't convert object of type " + object.getClass().getCanonicalName());

        }

    }

    private Geometry convertBox(PGboxbase box) {
        Point ll = box.getLLB();
        Point ur = box.getURT();
        Coordinate[] ringCoords = new Coordinate[5];
        if (box instanceof org.postgis.PGbox2d) {
            ringCoords[0] = new Coordinate(ll.x, ll.y);
            ringCoords[1] = new Coordinate(ur.x, ll.y);
            ringCoords[2] = new Coordinate(ur.x, ur.y);
            ringCoords[3] = new Coordinate(ll.x, ur.y);
            ringCoords[4] = new Coordinate(ll.x, ll.y);
        } else {
            ringCoords[0] = new Coordinate(ll.x, ll.y, ll.z);
            ringCoords[1] = new Coordinate(ur.x, ll.y, ll.z);
            ringCoords[2] = new Coordinate(ur.x, ur.y, ur.z);
            ringCoords[3] = new Coordinate(ll.x, ur.y, ur.z);
            ringCoords[4] = new Coordinate(ll.x, ll.y, ll.z);
        }
        com.vividsolutions.jts.geom.LinearRing shell = getGeometryFactory().createLinearRing(ringCoords);
        return getGeometryFactory().createPolygon(shell, null);
    }

    private Geometry convertGeometryCollection(GeometryCollection collection) {
        org.postgis.Geometry[] geometries = collection.getGeometries();
        com.vividsolutions.jts.geom.Geometry[] jtsGeometries =
                new com.vividsolutions.jts.geom.Geometry[geometries.length];
        for (int i = 0; i < geometries.length; i++) {
            jtsGeometries[i] = toJTS(geometries[i]);
            // TODO - refactor this so the following line is not necessary
            jtsGeometries[i].setSRID(0); // convert2JTS sets SRIDs, but
                                         // constituent geometries in a
                                         // collection must have srid == 0
        }
        com.vividsolutions.jts.geom.GeometryCollection jtsGCollection =
                getGeometryFactory().createGeometryCollection(jtsGeometries);
        return jtsGCollection;
    }

    private Geometry convertMultiPolygon(MultiPolygon pgMultiPolygon) {
        com.vividsolutions.jts.geom.Polygon[] polygons =
                new com.vividsolutions.jts.geom.Polygon[pgMultiPolygon.numPolygons()];

        for (int i = 0; i < polygons.length; i++) {
            Polygon pgPolygon = pgMultiPolygon.getPolygon(i);
            polygons[i] = (com.vividsolutions.jts.geom.Polygon) convertPolygon(pgPolygon);
        }

        return getGeometryFactory().createMultiPolygon(polygons);
    }

    private Geometry convertMultiPoint(MultiPoint pgMultiPoint) {
        com.vividsolutions.jts.geom.Point[] points = new com.vividsolutions.jts.geom.Point[pgMultiPoint.numPoints()];

        for (int i = 0; i < points.length; i++) {
            points[i] = convertPoint(pgMultiPoint.getPoint(i));
        }
        com.vividsolutions.jts.geom.MultiPoint out = getGeometryFactory().createMultiPoint(points);
        out.setSRID(pgMultiPoint.srid);
        return out;
    }

    private com.vividsolutions.jts.geom.Geometry convertMultiLineString(MultiLineString mlstr) {
        com.vividsolutions.jts.geom.MultiLineString out;
        if (mlstr.haveMeasure) {
            MLineString[] lstrs = new MLineString[mlstr.numLines()];
            for (int i = 0; i < mlstr.numLines(); i++) {
                MCoordinate[] coordinates = toJTSCoordinates(mlstr.getLine(i).getPoints());
                lstrs[i] = getGeometryFactory().createMLineString(coordinates);
            }
            out = getGeometryFactory().createMultiMLineString(lstrs);
        } else {
            com.vividsolutions.jts.geom.LineString[] lstrs =
                    new com.vividsolutions.jts.geom.LineString[mlstr.numLines()];
            for (int i = 0; i < mlstr.numLines(); i++) {
                lstrs[i] = getGeometryFactory().createLineString(toJTSCoordinates(mlstr.getLine(i).getPoints()));
            }
            out = getGeometryFactory().createMultiLineString(lstrs);
        }
        return out;
    }

    private com.vividsolutions.jts.geom.Geometry convertPolygon(Polygon polygon) {
        com.vividsolutions.jts.geom.LinearRing shell =
                getGeometryFactory().createLinearRing(toJTSCoordinates(polygon.getRing(0).getPoints()));
        com.vividsolutions.jts.geom.Polygon out = null;
        if (polygon.numRings() > 1) {
            com.vividsolutions.jts.geom.LinearRing[] rings =
                    new com.vividsolutions.jts.geom.LinearRing[polygon.numRings() - 1];
            for (int r = 1; r < polygon.numRings(); r++) {
                rings[r - 1] = getGeometryFactory().createLinearRing(toJTSCoordinates(polygon.getRing(r).getPoints()));
            }
            out = getGeometryFactory().createPolygon(shell, rings);
        } else {
            out = getGeometryFactory().createPolygon(shell, null);
        }
        return out;
    }

    private com.vividsolutions.jts.geom.Point convertPoint(Point pnt) {
        return getGeometryFactory().createPoint(this.toJTSCoordinate(pnt));
    }

    private com.vividsolutions.jts.geom.LineString convertLineString(org.postgis.LineString lstr) {
        com.vividsolutions.jts.geom.LineString out =
                lstr.haveMeasure ? getGeometryFactory().createMLineString(toJTSCoordinates(lstr.getPoints()))
                        : getGeometryFactory().createLineString(toJTSCoordinates(lstr.getPoints()));
        return out;
    }

    private MCoordinate[] toJTSCoordinates(Point[] points) {
        MCoordinate[] coordinates = new MCoordinate[points.length];
        for (int i = 0; i < points.length; i++) {
            coordinates[i] = this.toJTSCoordinate(points[i]);
        }
        return coordinates;
    }

    private MCoordinate toJTSCoordinate(Point pt) {
        MCoordinate mc;
        if (pt.dimension == 2) {
            mc =
                    pt.haveMeasure ? MCoordinate.create2dWithMeasure(pt.getX(), pt.getY(), pt.getM()) : MCoordinate
                            .create2d(pt.getX(), pt.getY());
        } else {
            mc =
                    pt.haveMeasure ? MCoordinate.create3dWithMeasure(pt.getX(), pt.getY(), pt.getZ(), pt.getM())
                            : MCoordinate.create3d(pt.getX(), pt.getY(), pt.getZ());
        }
        return mc;
    }

    // added methods
    private String extractBoxString(PGobject pgo) {
        String boxStr = null;
        Matcher m = boxPattern.matcher(pgo.getValue());
        if (m.matches() && m.groupCount() >= 1) {
            boxStr = m.group(1);
        }
        return boxStr;

    }

    private Geometry convertFromPGobject(PGobject object) {
        String value = object.getValue();
        try {
            PGgeometry pg = new PGgeometry(value);
            return toJTS(pg);
        } catch (SQLException e) {
            throw new IllegalArgumentException("Can't convert PGobject of type " + object.getType());
        }
    }

    private Point toPoint(String s) {
        String[] coords = s.split("\\s");
        double x = Double.parseDouble(coords[0]);
        double y = Double.parseDouble(coords[1]);
        return new Point(x, y);
    }

    private Geometry cornerPointsToPolygon(Point ll, Point ur, boolean is3D) {
        Coordinate[] ringCoords = new Coordinate[5];
        ringCoords[0] = is3D ? new Coordinate(ll.x, ll.y, ll.z) : new Coordinate(ll.x, ll.y);
        ringCoords[1] = is3D ? new Coordinate(ur.x, ll.y, ll.z) : new Coordinate(ur.x, ll.y);
        ringCoords[2] = is3D ? new Coordinate(ur.x, ur.y, ur.z) : new Coordinate(ur.x, ur.y);
        ringCoords[3] = is3D ? new Coordinate(ll.x, ur.y, ur.z) : new Coordinate(ll.x, ur.y);
        ringCoords[4] = is3D ? new Coordinate(ll.x, ll.y, ll.z) : new Coordinate(ll.x, ll.y);
        com.vividsolutions.jts.geom.LinearRing shell = getGeometryFactory().createLinearRing(ringCoords);
        return getGeometryFactory().createPolygon(shell, null);
    }
    
    public String getLicense() {
        StringBuilder builder = new StringBuilder();
        String lineBreak = System.getProperty("line.separator");
        builder.append(" /*").append(lineBreak);
        builder.append(" * This file is part of Hibernate Spatial, an extension to the").append(
                lineBreak);
        builder.append("*  hibernate ORM solution for spatial (geographic) data.").append(
                lineBreak);
        builder.append(" *").append(lineBreak);
        builder.append(" *  Copyright Â© 2007-2012 Geovise BVBA").append(lineBreak);
        builder.append(" *").append(lineBreak);
        builder.append(" *  This library is free software; you can redistribute it and/or").append(
                lineBreak);
        builder.append(" *  modify it under the terms of the GNU Lesser General Public").append(
                lineBreak);
        builder.append(" *  License as published by the Free Software Foundation; either").append(
                lineBreak);
        builder.append(" *  version 2.1 of the License, or (at your option) any later version.").append(
                lineBreak);
        builder.append(" *").append(lineBreak);
        builder.append(" *  This library is distributed in the hope that it will be useful,").append(
                lineBreak);
        builder.append(" *  but WITHOUT ANY WARRANTY; without even the implied warranty of").append(
                lineBreak);
        builder.append(" *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU").append(
                lineBreak);
        builder.append(" *  Lesser General Public License for more details.").append(
                lineBreak);
        builder.append(" *").append(lineBreak);
        builder.append(" *  You should have received a copy of the GNU Lesser General Public").append(
                lineBreak);
        builder.append(" *  License along with this library; if not, write to the Free Software").append(
                lineBreak);
        builder.append(" *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA").append(
                lineBreak);
        builder.append(" */").append(lineBreak);
        return builder.toString();
    }
}
