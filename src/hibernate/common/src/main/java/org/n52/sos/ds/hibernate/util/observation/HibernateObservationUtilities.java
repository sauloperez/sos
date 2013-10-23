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
package org.n52.sos.ds.hibernate.util.observation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.ds.hibernate.dao.ObservationConstellationDAO;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Observation;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.SpatialFilteringProfile;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 *
 */
public class HibernateObservationUtilities {
    private HibernateObservationUtilities() {
    }

    /**
     * @deprecated use {@link ObservationConstellationDAO#getFirstObservationConstellationForOfferings(org.n52.sos.ds.hibernate.entities.Procedure, org.n52.sos.ds.hibernate.entities.ObservableProperty, java.util.Collection, org.hibernate.Session)
     * }
     */
    @Deprecated
    public static ObservationConstellation getFirstObservationConstellation(
            Procedure p,
            ObservableProperty op, Collection<Offering> o, Session s) {
        return new ObservationConstellationDAO()
                .getFirstObservationConstellationForOfferings(p, op, o, s);
    }

    /**
     * @deprecated use {@link ObservationConstellationDAO#getObservationConstellationsForOfferings(org.n52.sos.ds.hibernate.entities.Procedure, org.n52.sos.ds.hibernate.entities.ObservableProperty, java.util.Collection, org.hibernate.Session)
     * }
     */
    @Deprecated
    public static List<ObservationConstellation> getObservationConstellations(
            Procedure p,
            ObservableProperty op, Collection<Offering> o, Session s) {
        return new ObservationConstellationDAO()
                .getObservationConstellationsForOfferings(p, op, o, s);
    }

    /**
     * Create SOS internal observation from Observation objects
     *
     * @param o
     *            List of Observation objects
     * @param spf
     *            Map with spatial filtering profile entities, key observation
     *            entity id
     * @param v
     *            Service v
     * @param rm
     *            Requested result model
     * @param s
     *            Hibernate s
     *
     * @return SOS internal observation
     *
     * @throws OwsExceptionReport
     *                            If an error occurs
     * @throws ConverterException
     *                            If procedure creation fails
     */
    public static List<OmObservation> createSosObservationsFromObservations(
            Collection<Observation> o, Map<Long, SpatialFilteringProfile> spf,
            String v, String rm, Session s) throws OwsExceptionReport, ConverterException {
        return new ObservationOmObservationCreator(o, spf, v, rm, s).create();

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Collection<? extends OmObservation> createSosObservationFromObservationConstellation(
           ObservationConstellation oc, List<String> fois, String version, Session session)
            throws OwsExceptionReport, ConverterException {
        return new ObservationConstellationOmObservationCreator(oc, fois, version, session).create();
    }

    public static List<OmObservation> unfoldObservation(OmObservation o)
            throws OwsExceptionReport {
        return new ObservationUnfolder(o).unfold();
    }

    public static Set<Long> getObservationIds(
            Collection<Observation> observations) {
        Set<Long> observationIds = Sets.newHashSet();
        for (Observation observation : observations) {
            observationIds.add(observation.getObservationId());
        }
        return observationIds;
    }
}
