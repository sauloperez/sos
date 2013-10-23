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

import java.io.Serializable;

import org.n52.sos.util.GeometryHandler;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Class for internal Envelope representation TODO should this class offer
 * merging capabilities like SosEnvelope.expandTo(SosEnvelope) considering
 * coordinate transformations?
 * 
 * @since 4.0.0
 */
public class SosEnvelope implements Serializable {
    private static final long serialVersionUID = 6525679408878064331L;

    /**
     * JTS envelope object
     */
    private Envelope envelope;

    /**
     * SRID
     */
    private int srid;

    public SosEnvelope() {
    }

    /**
     * constructor
     * 
     * @param envelope
     *            JTS envelope
     * @param srid
     *            SRID
     */
    public SosEnvelope(Envelope envelope, int srid) {
        setEnvelope(envelope);
        setSrid(srid);
    }

    /**
     * Get envelope
     * 
     * @return the envelope
     */
    public Envelope getEnvelope() {
        return envelope;
    }

    /**
     * Expand this envelope to include the given envelope.
     *
     * @param e the envelope (may be <code>null</code>)
     */
    public void expandToInclude(Envelope e) {
        if (e != null) {
            if (isSetEnvelope()) {
                getEnvelope().expandToInclude(e);
            } else {
                setEnvelope(e);
            }
        }
    }

    /**
     * Expand this envelope to include the given envelope.
     *
     * @param e the envelope (may be <code>null</code>)
     */
    public void expandToInclude(SosEnvelope e) {
        if (e != null && e.isSetEnvelope()) {
            expandToInclude(e.getEnvelope());
        }
    }

    /**
     * Set envelope
     * 
     * @param envelope
     *            the envelope to set
     */
    public void setEnvelope(Envelope envelope) {
        this.envelope = envelope;
    }

    /**
     * Get SRID
     * 
     * @return the srid
     */
    public int getSrid() {
        return srid;
    }

    public boolean isSetSrid() {
        return srid != -1;
    }

    /**
     * Set SRID
     * 
     * @param srid
     *            the srid to set
     */
    public void setSrid(int srid) {
        this.srid = srid;
    }

    public boolean isSetEnvelope() {
        return getEnvelope() != null && !getEnvelope().isNull();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getEnvelope() != null) ? getEnvelope().hashCode() : 0);
        result = prime * result + getSrid();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SosEnvelope) {
            final SosEnvelope other = (SosEnvelope) obj;
            return getSrid() == other.getSrid() && getEnvelope() == null ? other.getEnvelope() == null : getEnvelope()
                    .equals(other.getEnvelope());

        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("SosEnvelope[envelope=%s, srid=%s]", getEnvelope(), getSrid());
    }

    /**
     * Static mehtod to check if an SosEnvelope is not null and is not empty
     * 
     * @param envelope
     *            SosEnvelope to check
     * @return <code>true</code>, if SosEnvelope is not null and not empty.
     */
    public static boolean isNotNullOrEmpty(SosEnvelope envelope) {
        return envelope != null && envelope.isSetEnvelope();
    }

    /**
     * Switches the coordinates of this Envelope if needed.
     *
     * @return this
     *
     * @see GeometryHandler#isAxisOrderSwitchRequired(int)
     */
    public SosEnvelope switchCoordinatesIfNeeded() {
        if (isSetEnvelope() && GeometryHandler.getInstance().isAxisOrderSwitchRequired(getSrid())) {
            this.envelope = new Envelope(getEnvelope().getMinY(),
                                         getEnvelope().getMaxY(),
                                         getEnvelope().getMinX(),
                                         getEnvelope().getMaxX());
        }
        return this;
    }
}
