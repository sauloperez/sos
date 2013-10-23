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
package org.n52.sos.ogc.om;

import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.Constants;
import org.n52.sos.util.JTSHelper;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import org.junit.Test;

import com.vividsolutions.jts.geom.Geometry;

/**
 * @since 4.0.0
 * 
 */
public class OmObservationTest {

    @Test
    public final void should_have_SpatialFilteringProfileParameter() throws OwsExceptionReport {
        OmObservation omObservation = new OmObservation();
        NamedValue<Geometry> namedValue = new NamedValue<Geometry>();
        namedValue.setName(new ReferenceType(OmConstants.PARAM_NAME_SAMPLING_GEOMETRY));
        namedValue.setValue(new GeometryValue(JTSHelper.createGeometryFromWKT("POINT (34.5 76.4)", Constants.EPSG_WGS84)));
        // test no parameter is set
        assertFalse(omObservation.isSetParameter());
        assertFalse(omObservation.isSetSpatialFilteringProfileParameter());
        omObservation.addParameter(namedValue);
        // test with set SpatialFilteringProfile parameter
        assertTrue(omObservation.isSetParameter());
        assertTrue(omObservation.isSetSpatialFilteringProfileParameter());
        assertThat(omObservation.getSpatialFilteringProfileParameter(), is(equalTo(namedValue)));
    }

}
