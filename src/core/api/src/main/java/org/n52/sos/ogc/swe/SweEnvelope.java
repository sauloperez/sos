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

package org.n52.sos.ogc.swe;

import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.ogc.swe.SweConstants.SweCoordinateName;
import org.n52.sos.ogc.swe.SweConstants.SweDataComponentType;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweTimeRange;
import org.n52.sos.util.SosHelper;

import com.google.common.base.Objects;
import com.vividsolutions.jts.geom.Envelope;

/**
 * @since 4.0.0
 * 
 */
public class SweEnvelope extends SweAbstractDataComponent {
    private String referenceFrame;

    private SweVector upperCorner;

    private SweVector lowerCorner;

    private SweTimeRange time;

    public SweEnvelope() {
        this(null, null, null, null);
    }

    public SweEnvelope(String referenceFrame, SweVector upperCorner, SweVector lowerCorner) {
        this(referenceFrame, upperCorner, lowerCorner, null);
    }

    public SweEnvelope(SosEnvelope sosEnvelope, String uom) {
        this(String.valueOf(sosEnvelope.getSrid()), 
             createUpperCorner(sosEnvelope, uom),
             createLowerCorner(sosEnvelope, uom));
    }

    public SweEnvelope(String referenceFrame, SweVector upperCorner, SweVector lowerCorner, SweTimeRange time) {
        this.referenceFrame = referenceFrame;
        this.upperCorner = upperCorner;
        this.lowerCorner = lowerCorner;
        this.time = time;
    }

    public String getReferenceFrame() {
        return referenceFrame;
    }

    public boolean isReferenceFrameSet() {
        return getReferenceFrame() != null;
    }

    public SweEnvelope setReferenceFrame(String referenceFrame) {
        this.referenceFrame = referenceFrame;
        return this;
    }

    public SweVector getUpperCorner() {
        return upperCorner;
    }

    public boolean isUpperCornerSet() {
        return getUpperCorner() != null;
    }

    public SweEnvelope setUpperCorner(SweVector upperCorner) {
        this.upperCorner = upperCorner;
        return this;
    }

    public SweVector getLowerCorner() {
        return lowerCorner;
    }

    public boolean isLowerCornerSet() {
        return getLowerCorner() != null;
    }

    public SweEnvelope setLowerCorner(SweVector lowerCorner) {
        this.lowerCorner = lowerCorner;
        return this;
    }

    public SweTimeRange getTime() {
        return time;
    }

    public boolean isTimeSet() {
        return getTime() != null;
    }

    public SweEnvelope setTime(SweTimeRange time) {
        this.time = time;
        return this;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(getClass()).add("referenceFrame", getReferenceFrame())
                .add("upperCorner", getUpperCorner()).add("lowerCorner", getLowerCorner()).add("time", getTime())
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getReferenceFrame(), getUpperCorner(), getLowerCorner(), getTime());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SweEnvelope) {
            SweEnvelope other = (SweEnvelope) obj;
            return Objects.equal(getReferenceFrame(), other.getReferenceFrame())
                    && Objects.equal(getUpperCorner(), other.getUpperCorner())
                    && Objects.equal(getLowerCorner(), other.getLowerCorner())
                    && Objects.equal(getTime(), other.getTime());

        }
        return false;
    }

    @Override
    public SweDataComponentType getDataComponentType() {
        return SweDataComponentType.Envelope;
    }

    private static SweVector createLowerCorner(SosEnvelope env, String uom) {
        return createSweVector(env.getEnvelope().getMinX(), env.getEnvelope().getMinY(), uom);
    }

    private static SweVector createUpperCorner(SosEnvelope env, String uom) {
        return createSweVector(env.getEnvelope().getMaxX(), env.getEnvelope().getMaxY(), uom);
    }

    private static SweVector createSweVector(double x, double y, String uom) {
        SweQuantity xCoord = new SweQuantity().setValue(x).setAxisID("x").setUom(uom);
        SweQuantity yCoord = new SweQuantity().setValue(y).setAxisID("y").setUom(uom);
        return new SweVector(new SweCoordinate<Double>(SweCoordinateName.easting.name(), xCoord),
                             new SweCoordinate<Double>(SweCoordinateName.northing.name(), yCoord));
    }

    public SosEnvelope toSosEnvelope() throws OwsExceptionReport {
        Double minx = (Double) getLowerCorner().getCoordinates().get(0).getValue().getValue();
        Double miny = (Double) getLowerCorner().getCoordinates().get(1).getValue().getValue();
        Double maxx = (Double) getUpperCorner().getCoordinates().get(0).getValue().getValue();
        Double maxy = (Double) getUpperCorner().getCoordinates().get(1).getValue().getValue();
        return new SosEnvelope(new Envelope(minx, maxx, miny, maxy),
                               SosHelper.parseSrsName(getReferenceFrame()));
    }
}
