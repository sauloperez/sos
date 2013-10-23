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
package org.n52.sos.ds.hibernate.entities;

import java.io.Serializable;

import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasCoordinate;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasGeometry;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasObservation;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasSrid;
import org.n52.sos.util.StringHelper;

import com.vividsolutions.jts.geom.Geometry;

/**
 * @since 4.0.0
 * 
 */
public class SpatialFilteringProfile implements Serializable, HasGeometry, HasCoordinate, HasSrid, HasObservation {

    private static final long serialVersionUID = 7200974625085342134L;

    private long spatialFilteringProfileId;

    private Observation observation;

    private String definition;

    private String title;

    private Geometry geom;

    private Object longitude;

    private Object latitude;

    private Object altitude;

    private int srid;

    public SpatialFilteringProfile() {
        super();
    }

    public long getSpatialFilteringProfileId() {
        return this.spatialFilteringProfileId;
    }

    public void setSpatialFilteringProfileId(long spatialFilteringProfileId) {
        this.spatialFilteringProfileId = spatialFilteringProfileId;
    }

    public String getDefinition() {
        return definition;
    }

    public SpatialFilteringProfile setDefinition(String definition) {
        this.definition = definition;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public SpatialFilteringProfile setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public Geometry getGeom() {
        return geom;
    }

    @Override
    public SpatialFilteringProfile setGeom(Geometry geom) {
        this.geom = geom;
        return this;
    }

    @Override
    public int getSrid() {
        return srid;
    }

    @Override
    public SpatialFilteringProfile setSrid(int srid) {
        this.srid = srid;
        return this;
    }

    @Override
    public Object getLongitude() {
        return longitude;
    }

    @Override
    public SpatialFilteringProfile setLongitude(Object longitude) {
        this.longitude = longitude;
        return this;
    }

    @Override
    public Object getLatitude() {
        return latitude;
    }

    @Override
    public SpatialFilteringProfile setLatitude(Object latitude) {
        this.latitude = latitude;
        return this;
    }

    @Override
    public Object getAltitude() {
        return altitude;
    }

    @Override
    public SpatialFilteringProfile setAltitude(Object altitude) {
        this.altitude = altitude;
        return this;
    }

    public boolean isSetDefinition() {
        return StringHelper.isNotEmpty(getDefinition());
    }

    public boolean isSetTitle() {
        return StringHelper.isNotEmpty(getTitle());
    }

    public boolean isSetGeometry() {
        return getGeom() != null;
    }

    public boolean isSetLongLat() {
        return getLongitude() != null && getLatitude() != null;
    }

    public boolean isSetAltitude() {
        return getAltitude() != null;
    }

    public boolean isSetSrid() {
        return getSrid() > 0;
    }

    @Override
    public Observation getObservation() {
        return observation;
    }

    @Override
    public SpatialFilteringProfile setObservation(Observation observation) {
        this.observation = observation;
        return this;
    }

}
