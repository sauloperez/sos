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
package org.n52.sos.ds.hibernate.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.spatial.criterion.SpatialProjections;
import org.hibernate.spatial.dialect.h2geodb.GeoDBDialect;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.Observation;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.SpatialFilteringProfile;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.SpatialRestrictions;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.OptionNotSupportedException;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.JTSHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Hibernate DAO class for SOS 2.0 Spatial Filtering Profile
 * 
 * @since 4.0.0
 * 
 */
@Configurable
public class SpatialFilteringProfileDAO {

    private static final String OBSERVATION = "observation";

    private static final Logger LOGGER = LoggerFactory.getLogger(SpatialFilteringProfileDAO.class);

    /**
     * Get SpatialFilteringProfile for observation id.
     * 
     * @param observationId
     *            Related observation id
     * @param session
     *            Hibernate session
     * @return SpatialFilteringProfile for observation id
     * @throws CodedException
     *             If SpatialFilteringProfile is not supported
     */
    public SpatialFilteringProfile getSpatialFilertingProfile(Long observationId, Session session)
            throws CodedException {
        if (HibernateHelper.isEntitySupported(SpatialFilteringProfile.class, session)) {
            Criteria criteria = session.createCriteria(SpatialFilteringProfile.class);
            criteria.createCriteria(SpatialFilteringProfile.OBSERVATION).add(
                    Restrictions.eq(Observation.ID, observationId));
            LOGGER.debug("QUERY getSpatialFilertingProfile(observationId): {}", HibernateHelper.getSqlString(criteria));
            return (SpatialFilteringProfile) criteria.uniqueResult();
        }
        throw new OptionNotSupportedException().at("SpatialFilteringProfile").withMessage(
                "SpatialFilteringProfile is not supported! Add mapping to ressources!");
    }

    /**
     * Get observation ids for SpatialFilteringProfile spatial filter
     * 
     * @param spatialFilter
     *            Spatial filter
     * @param session
     *            Hibernate session
     * @return Observation ids which are valid for spatial filter
     * @throws OwsExceptionReport
     *             If SpatialFilteringProfile is not supported
     */
    @SuppressWarnings("unchecked")
    public Set<Long> getObservationIdsForSpatialFilter(SpatialFilter spatialFilter, Session session)
            throws OwsExceptionReport {
        if (HibernateHelper.isEntitySupported(SpatialFilteringProfile.class, session)) {
            // TODO check if other parameter (Offering, Procedure, ...) should
            // be set in query to reduce the number of returned observation ids.
            final Criteria criteria = getObservationCriteria(getDetachedCriteria(spatialFilter), session);
            LOGGER.debug("QUERY getObservationIdsForSpatialFilter(spatialFilter): {}",
                    HibernateHelper.getSqlString(criteria));
            return Sets.newHashSet(criteria.list());
        }
        return Sets.newHashSet();
    }

    /**
     * Insert SpatialFilteringProfile definition into datasource
     * 
     * @param namedValue
     *            SpatialFilteringProfile definition
     * @param observation
     *            Observation entity
     * @param session
     *            Hibernate session
     * @throws OwsExceptionReport
     *             If SpatialFilteringProfile is not supported
     */
    public void insertSpatialfilteringProfile(NamedValue<Geometry> namedValue, Observation observation, Session session)
            throws OwsExceptionReport {
        if (HibernateHelper.isEntitySupported(SpatialFilteringProfile.class, session)) {
            SpatialFilteringProfile spatialFilteringProfile = new SpatialFilteringProfile();
            spatialFilteringProfile.setObservation(observation);
            spatialFilteringProfile.setDefinition(namedValue.getName().getHref());
            if (namedValue.getName().isSetTitle()) {
                spatialFilteringProfile.setTitle(namedValue.getName().getTitle());
            }

            spatialFilteringProfile.setGeom(GeometryHandler.getInstance().switchCoordinateAxisOrderIfNeeded(
                    namedValue.getValue().getValue()));
            session.saveOrUpdate(spatialFilteringProfile);
            session.flush();
        } else {
            throw new OptionNotSupportedException().at(Sos2Constants.InsertObservationParams.parameter).withMessage(
                    "The SOS 2.0 Spatial Filtering Profile is not supported by this service!");
        }
    }

    /**
     * Get map with observation id key and related SpatialFilteringProfile
     * 
     * @param observationIds
     *            Observation ids to get SpatialFilteringProfile for
     * @param session
     *            Hibernate session
     * @return Map with observation id and related SpatialFilteringProfile
     */
    public Map<Long, SpatialFilteringProfile> getSpatialFilertingProfiles(Set<Long> observationIds, Session session) {
        // TODO optimize query and map creation, no reload
        Long count = getSpatialFilteringProfileCount(session);
        if (HibernateHelper.isEntitySupported(SpatialFilteringProfile.class, session)
                && CollectionHelper.isNotEmpty(observationIds) && hasSpatialFilteringProfileValues(count, session)) {
            Map<Long, SpatialFilteringProfile> spatialFilteringProfilesMap = Maps.newHashMap();
            if (count <= observationIds.size()) {
                List<SpatialFilteringProfile> spatialFileringProfiles = getSpatialFileringProfiles(session);
                for (SpatialFilteringProfile spatialFilteringProfile : spatialFileringProfiles) {
                    if (observationIds.contains(spatialFilteringProfile.getObservation().getObservationId())) {
                        spatialFilteringProfilesMap.put(spatialFilteringProfile.getObservation().getObservationId(),
                                spatialFilteringProfile);
                    }
                }
            } else {
                List<SpatialFilteringProfile> queriedSpatiaFilteringProfiles =
                        querySpatialFilteringProfileCriteria(HibernateHelper.getValidSizedLists(observationIds),
                                session);
                if (CollectionHelper.isNotEmpty(queriedSpatiaFilteringProfiles)) {
                    for (SpatialFilteringProfile spatialFilteringProfile : queriedSpatiaFilteringProfiles) {
                        spatialFilteringProfilesMap.put(spatialFilteringProfile.getObservation().getObservationId(),
                                spatialFilteringProfile);
                    }
                }
            }
            return spatialFilteringProfilesMap;
        }
        return Maps.newHashMap();
    }

    @SuppressWarnings("unchecked")
    public List<SpatialFilteringProfile> getSpatialFileringProfiles(Session session) {
        Criteria criteria = session.createCriteria(SpatialFilteringProfile.class);
        LOGGER.debug("QUERY getSpatialFileringProfiles(): {}", HibernateHelper.getSqlString(criteria));
        return (List<SpatialFilteringProfile>) criteria.list();
    }

    @SuppressWarnings("unchecked")
    private List<SpatialFilteringProfile> querySpatialFilteringProfileCriteria(List<List<Long>> observationIdsList,
            Session session) {
        List<SpatialFilteringProfile> list = Lists.newArrayList();
        for (List<Long> observationIds : observationIdsList) {
            Criteria criteria = session.createCriteria(SpatialFilteringProfile.class);
            criteria.createCriteria(SpatialFilteringProfile.OBSERVATION).add(
                    Restrictions.in(Observation.ID, observationIds));
            LOGGER.debug("QUERY querySpatialFilteringProfileCriteria(observationIdsList): {}",
                    HibernateHelper.getSqlString(criteria));
            list.addAll((List<SpatialFilteringProfile>) criteria.list());
        }
        return list;
    }

    private boolean hasSpatialFilteringProfileValues(Long count, Session session) {
        if (count != null) {
            return count == 0 ? false : true;
        } else {
            return getSpatialFilteringProfileCount(session) == 0 ? false : true;
        }
    }

    private Long getSpatialFilteringProfileCount(Session session) {
        Criteria criteria = session.createCriteria(SpatialFilteringProfile.class);
        criteria.setProjection(Projections.countDistinct(OBSERVATION));
        LOGGER.debug("QUERY hasSpatialFilteringProfileValues(): {}", HibernateHelper.getSqlString(criteria));
        return (Long) criteria.uniqueResult();
    }

    /**
     * Get envelope for offering id
     * 
     * @param offeringID
     *            Offering id
     * @param session
     *            Hibernate session
     * @return SOS envelope
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public SosEnvelope getEnvelopeForOfferingId(String offeringID, Session session) throws OwsExceptionReport {
        try {
            // XXX workaround for Hibernate Spatial's lack of support for
            // GeoDB's extent aggregate see
            // http://www.hibernatespatial.org/pipermail/hibernatespatial-users/2013-August/000876.html
            Dialect dialect = ((SessionFactoryImplementor) session.getSessionFactory()).getDialect();
            if (GeometryHandler.getInstance().isSpatialDatasource()
                    && HibernateHelper.supportsFunction(dialect, HibernateConstants.FUNC_EXTENT)) {
                Criteria criteria = session.createCriteria(SpatialFilteringProfile.class);
                criteria.setProjection(SpatialProjections.extent(FeatureOfInterest.GEOMETRY));
                Criteria createCriteria = criteria.createCriteria(SpatialFilteringProfile.OBSERVATION);
                createCriteria.createCriteria(Observation.OFFERINGS).add(
                        Restrictions.eq(Offering.IDENTIFIER, offeringID));
                LOGGER.debug("QUERY getEnvelopeForOfferingId(offeringID): {}", HibernateHelper.getSqlString(criteria));
                Geometry geom = (Geometry) criteria.uniqueResult();
                geom = GeometryHandler.getInstance().switchCoordinateAxisOrderIfNeeded(geom);
                if (geom != null) {
                    return new SosEnvelope(geom.getEnvelopeInternal(), GeometryHandler.getInstance().getDefaultEPSG());
                }
            } else {
                final Envelope envelope = new Envelope();
                Criteria criteria = session.createCriteria(SpatialFilteringProfile.class);
                Criteria createCriteria = criteria.createCriteria(SpatialFilteringProfile.OBSERVATION);
                createCriteria.createCriteria(Observation.OFFERINGS).add(
                        Restrictions.eq(Offering.IDENTIFIER, offeringID));
                LOGGER.debug("QUERY getEnvelopeForOfferingId(offeringID): {}", HibernateHelper.getSqlString(criteria));
                @SuppressWarnings("unchecked")
                final List<SpatialFilteringProfile> spatialFilteringProfiles = criteria.list();
                if (CollectionHelper.isNotEmpty(spatialFilteringProfiles)) {
                    for (final SpatialFilteringProfile spatialFilteringProfile : spatialFilteringProfiles) {
                        try {
                            final Geometry geom = getGeomtery(spatialFilteringProfile);
                            if (geom != null && geom.getEnvelopeInternal() != null) {
                                envelope.expandToInclude(geom.getEnvelopeInternal());
                            }
                        } catch (final OwsExceptionReport owse) {
                            LOGGER.warn(
                                    String.format("Error while adding '%s' to envelope!",
                                            spatialFilteringProfile.getSpatialFilteringProfileId()), owse);
                        }

                    }
                    if (!envelope.isNull()) {
                        return new SosEnvelope(envelope, GeometryHandler.getInstance().getDefaultEPSG());
                    }
                }
            }
        } catch (final HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage(
                    "Exception thrown while requesting feature envelope for observation ids");
        }
        return null;
    }

    /**
     * Get geometry from SpatialFilteringProfile
     * 
     * @param spatialFilteringProfile
     *            SpatialFilteringProfile to get geomnetry from
     * @return Geometry
     * @throws OwsExceptionReport
     *             If an error occurs.
     */
    private Geometry getGeomtery(SpatialFilteringProfile spatialFilteringProfile) throws OwsExceptionReport {
        if (spatialFilteringProfile.isSetGeometry()) {
            return GeometryHandler.getInstance().switchCoordinateAxisOrderIfNeeded(spatialFilteringProfile.getGeom());
        } else if (spatialFilteringProfile.isSetLongLat()) {
            int epsg = GeometryHandler.getInstance().getDefaultEPSG();
            if (spatialFilteringProfile.isSetSrid()) {
                epsg = spatialFilteringProfile.getSrid();
            }
            final String wktString =
                    GeometryHandler.getInstance().getWktString(spatialFilteringProfile.getLongitude(),
                            spatialFilteringProfile.getLatitude());
            final Geometry geom = JTSHelper.createGeometryFromWKT(wktString, epsg);
            if (spatialFilteringProfile.isSetAltitude()) {
                geom.getCoordinate().z =
                        GeometryHandler.getInstance().getValueAsDouble(spatialFilteringProfile.getAltitude());
                if (geom.getSRID() == GeometryHandler.getInstance().getDefaultEPSG()) {
                    geom.setSRID(GeometryHandler.getInstance().getDefault3DEPSG());
                }
            }
            return GeometryHandler.getInstance().switchCoordinateAxisOrderIfNeeded(geom);
        }
        return null;
    }

    private DetachedCriteria getDetachedCriteria(SpatialFilter spatialFilter) throws OwsExceptionReport {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(SpatialFilteringProfile.class);
        if (spatialFilter != null) {
            detachedCriteria.add(SpatialRestrictions.filter(SpatialFilteringProfile.GEOMETRY,
                    spatialFilter.getOperator(),
                    GeometryHandler.getInstance().switchCoordinateAxisOrderIfNeeded(spatialFilter.getGeometry())));
        }
        detachedCriteria
                .setProjection(Projections.distinct(Projections.property(SpatialFilteringProfile.OBSERVATION)));
        return detachedCriteria;
    }

    private Criteria getObservationCriteria(DetachedCriteria detachedCriteria, Session session) {
        final Criteria criteria = session.createCriteria(Observation.class);
        criteria.add(Subqueries.propertyIn(Observation.ID, detachedCriteria));
        criteria.setProjection(Projections.distinct(Projections.property(Observation.ID)));
        return criteria;
    }

}
