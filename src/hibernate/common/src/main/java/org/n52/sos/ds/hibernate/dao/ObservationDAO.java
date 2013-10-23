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

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.ds.hibernate.entities.BlobObservation;
import org.n52.sos.ds.hibernate.entities.BooleanObservation;
import org.n52.sos.ds.hibernate.entities.CategoryObservation;
import org.n52.sos.ds.hibernate.entities.CountObservation;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.GeometryObservation;
import org.n52.sos.ds.hibernate.entities.NumericObservation;
import org.n52.sos.ds.hibernate.entities.Observation;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.ObservationInfo;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.SweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.TextObservation;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.OptionNotSupportedException;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.Time.TimeIndeterminateValue;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.om.values.UnknownValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Hibernate data access class for observation
 * 
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class ObservationDAO extends TimeCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObservationDAO.class);

    public static final String SQL_QUER_GET_LATEST_OBSERVATION_TIME = "getLatestObservationTime";

    public static final String SQL_QUER_GET_FIRST_OBSERVATION_TIME = "getFirstObservationTime";

    /**
     * Get all observation identifiers
     * 
     * @param session
     *            Hibernate session
     * @return Observation identifiers
     */
    @SuppressWarnings("unchecked")
    public List<String> getObservationIdentifiers(Session session) {
        Criteria criteria =
                session.createCriteria(ObservationInfo.class).add(Restrictions.eq(Observation.DELETED, false))
                        .add(Restrictions.isNotNull(Observation.IDENTIFIER))
                        .setProjection(Projections.distinct(Projections.property(Observation.IDENTIFIER)));
        LOGGER.debug("QUERY getObservationIdentifiers(): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Check if there are numeric observations for the offering
     * 
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return If there are observations or not
     */
    public boolean checkNumericObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(NumericObservation.class, offeringIdentifier, session);
    }

    /**
     * Check if there are boolean observations for the offering
     * 
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return If there are observations or not
     */
    public boolean checkBooleanObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(BooleanObservation.class, offeringIdentifier, session);
    }

    /**
     * Check if there are count observations for the offering
     * 
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return If there are observations or not
     */
    public boolean checkCountObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(CountObservation.class, offeringIdentifier, session);
    }

    /**
     * Check if there are category observations for the offering
     * 
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return If there are observations or not
     */
    public boolean checkCategoryObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(CategoryObservation.class, offeringIdentifier, session);
    }

    /**
     * Check if there are text observations for the offering
     * 
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return If there are observations or not
     */
    public boolean checkTextObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(TextObservation.class, offeringIdentifier, session);
    }

    /**
     * Check if there are blob observations for the offering
     * 
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return If there are observations or not
     */
    public boolean checkBlobObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(BlobObservation.class, offeringIdentifier, session);
    }

    /**
     * Check if there are geometry observations for the offering
     * 
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return If there are observations or not
     */
    public boolean checkGeometryObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(GeometryObservation.class, offeringIdentifier, session);
    }

    /**
     * Check if there are geometry observations for the offering
     * 
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return If there are observations or not
     */
    public boolean checkSweDataArrayObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(SweDataArrayObservation.class, offeringIdentifier, session);
    }

    /**
     * Check if there are observations for the offering
     * 
     * @param clazz
     *            Observation sub class
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return If there are observations or not
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private boolean checkObservationFor(Class clazz, String offeringIdentifier, Session session) {
        Criteria c = session.createCriteria(clazz).add(Restrictions.eq(Observation.DELETED, false));
        c.createCriteria(Observation.OFFERINGS).add(Restrictions.eq(Offering.IDENTIFIER, offeringIdentifier));
        c.setMaxResults(1);
        LOGGER.debug("QUERY checkObservationFor(clazz, offeringIdentifier): {}", HibernateHelper.getSqlString(c));
        return CollectionHelper.isNotEmpty(c.list());
    }

    /**
     * Get min phenomenon time from observations
     * 
     * @param session
     *            Hibernate session Hibernate session
     * @return min time
     */
    public DateTime getMinPhenomenonTime(Session session) {
        Criteria criteria =
                session.createCriteria(ObservationInfo.class)
                        .setProjection(Projections.min(Observation.PHENOMENON_TIME_START))
                        .add(Restrictions.eq(Observation.DELETED, false));
        LOGGER.debug("QUERY getMinPhenomenonTime(): {}", HibernateHelper.getSqlString(criteria));
        Object min = criteria.uniqueResult();
        if (min != null) {
            return new DateTime(min, DateTimeZone.UTC);
        }
        return null;
    }

    /**
     * Get max phenomenon time from observations
     * 
     * @param session
     *            Hibernate session Hibernate session
     * 
     * @return max time
     */
    public DateTime getMaxPhenomenonTime(Session session) {

        Criteria criteriaStart =
                session.createCriteria(ObservationInfo.class)
                        .setProjection(Projections.max(Observation.PHENOMENON_TIME_START))
                        .add(Restrictions.eq(Observation.DELETED, false));
        LOGGER.debug("QUERY getMaxPhenomenonTime() start: {}", HibernateHelper.getSqlString(criteriaStart));
        Object maxStart = criteriaStart.uniqueResult();

        Criteria criteriaEnd =
                session.createCriteria(ObservationInfo.class)
                        .setProjection(Projections.max(Observation.PHENOMENON_TIME_END))
                        .add(Restrictions.eq(Observation.DELETED, false));
        LOGGER.debug("QUERY getMaxPhenomenonTime() end: {}", HibernateHelper.getSqlString(criteriaEnd));
        Object maxEnd = criteriaEnd.uniqueResult();
        if (maxStart == null && maxEnd == null) {
            return null;
        } else {
            DateTime start = new DateTime(maxStart, DateTimeZone.UTC);
            if (maxEnd != null) {
                DateTime end = new DateTime(maxEnd, DateTimeZone.UTC);
                if (end.isAfter(start)) {
                    return end;
                }
            }
            return start;
        }
    }

    /**
     * Get min result time from observations
     * 
     * @param session
     *            Hibernate session Hibernate session
     * 
     * @return min time
     */
    public DateTime getMinResultTime(Session session) {

        Criteria criteria =
                session.createCriteria(ObservationInfo.class).setProjection(Projections.min(Observation.RESULT_TIME))
                        .add(Restrictions.eq(Observation.DELETED, false));
        LOGGER.debug("QUERY getMinResultTime(): {}", HibernateHelper.getSqlString(criteria));
        Object min = criteria.uniqueResult();
        if (min != null) {
            return new DateTime(min, DateTimeZone.UTC);
        }
        return null;
    }

    /**
     * Get max phenomenon time from observations
     * 
     * @param session
     *            Hibernate session Hibernate session
     * 
     * @return max time
     */
    public DateTime getMaxResultTime(Session session) {

        Criteria criteria =
                session.createCriteria(ObservationInfo.class).setProjection(Projections.max(Observation.RESULT_TIME))
                        .add(Restrictions.eq(Observation.DELETED, false));
        LOGGER.debug("QUERY getMaxResultTime(): {}", HibernateHelper.getSqlString(criteria));
        Object max = criteria.uniqueResult();
        if (max == null) {
            return null;
        } else {
            return new DateTime(max, DateTimeZone.UTC);
        }
    }

    /**
     * Get global temporal bounding box
     * 
     * @param session
     *            Hibernate session the session
     * 
     * @return the global getEqualRestiction bounding box over all observations,
     *         or <tt>null</tt>
     */
    public TimePeriod getGlobalTemporalBoundingBox(Session session) {
        if (session != null) {
            Criteria criteria = session.createCriteria(ObservationInfo.class);
            criteria.add(Restrictions.eq(Observation.DELETED, false));
            criteria.setProjection(Projections.projectionList()
                    .add(Projections.min(Observation.PHENOMENON_TIME_START))
                    .add(Projections.max(Observation.PHENOMENON_TIME_START))
                    .add(Projections.max(Observation.PHENOMENON_TIME_END)));
            LOGGER.debug("QUERY getGlobalTemporalBoundingBox(): {}", HibernateHelper.getSqlString(criteria));
            Object temporalBoundingBox = criteria.uniqueResult();
            if (temporalBoundingBox instanceof Object[]) {
                Object[] record = (Object[]) temporalBoundingBox;
                TimePeriod bBox =
                        createTimePeriod((Timestamp) record[0], (Timestamp) record[1], (Timestamp) record[2]);
                return bBox;
            }
        }
        return null;
    }

    /**
     * Insert a sinlge observation for observation constellations and
     * featureOfInterest
     * 
     * @param observationConstellations
     *            Observation constellation objects
     * @param feature
     *            FeatureOfInterest object
     * @param sosObservation
     *            SOS observation to insert
     * @param session
     *            Hibernate session
     * @throws OwsExceptionReport
     */
    public void insertObservationSingleValue(Set<ObservationConstellation> observationConstellations,
            FeatureOfInterest feature, OmObservation sosObservation, Session session) throws OwsExceptionReport {
        SingleObservationValue<?> value = (SingleObservationValue) sosObservation.getValue();
        Observation hObservation = createObservationFromValue(value.getValue(), session);
        hObservation.setDeleted(false);
        if (sosObservation.isSetIdentifier()) {
            hObservation.setIdentifier(sosObservation.getIdentifier().getValue());
            if (sosObservation.getIdentifier().isSetCodeSpace()) {
                hObservation.setCodespace(new CodespaceDAO().getOrInsertCodespace(sosObservation.getIdentifier()
                        .getCodeSpace(), session));
            }
        }
        if (!hObservation.isSetCodespace()) {
            hObservation.setCodespace(new CodespaceDAO().getOrInsertCodespace(OGCConstants.UNKNOWN, session));
        }
        Iterator<ObservationConstellation> iterator = observationConstellations.iterator();
        boolean firstObsConst = true;
        while (iterator.hasNext()) {
            ObservationConstellation observationConstellation = iterator.next();
            if (firstObsConst) {
                // TODO should subsequent obsConsts be checked for obsProp and
                // procedure agreement with the first?
                hObservation.setObservableProperty(observationConstellation.getObservableProperty());
                hObservation.setProcedure(observationConstellation.getProcedure());
                firstObsConst = false;
            }
            hObservation.getOfferings().add(observationConstellation.getOffering());
        }
        hObservation.setFeatureOfInterest(feature);
        addPhenomeonTimeAndResultTimeToObservation(hObservation, sosObservation.getPhenomenonTime(),
                sosObservation.getResultTime());

        if (value.getValue().getUnit() != null) {
            hObservation.setUnit(new UnitDAO().getOrInsertUnit(value.getValue().getUnit(), session));
        }

        // TODO if this observation is a deleted=true, how to set deleted=false
        // instead of insert
        session.saveOrUpdate(hObservation);
        session.flush();

        if (sosObservation.isSetParameter()) {
            insertParameter(sosObservation.getParameter(), hObservation, session);
        }
    }

    /**
     * Insert om:parameter into database. Differs between Spatial Filtering
     * Profile parameter and others.
     * 
     * @param parameter
     *            om:Parameter to insert
     * @param observation
     *            related observation
     * @param session
     *            Hibernate session
     * @throws OwsExceptionReport
     */
    @SuppressWarnings("unchecked")
    private void insertParameter(Collection<NamedValue<?>> parameter, Observation observation, Session session)
            throws OwsExceptionReport {
        for (NamedValue<?> namedValue : parameter) {
            if (Sos2Constants.HREF_PARAMETER_SPATIAL_FILTERING_PROFILE.equals(namedValue.getName().getHref())) {
                new SpatialFilteringProfileDAO().insertSpatialfilteringProfile((NamedValue<Geometry>) namedValue,
                        observation, session);
            } else {
                throw new OptionNotSupportedException().at("om:parameter").withMessage(
                        "The om:parameter support is not yet implemented!");
                // new ParameterDAO().insertParameter(namedValue, observation,
                // session);
            }
        }
    }

    /**
     * Insert a multi value observation for observation constellations and
     * featureOfInterest
     * 
     * @param observationConstellations
     *            Observation constellation objects
     * @param feature
     *            FeatureOfInterest object
     * @param containerObservation
     *            SOS observation
     * @param session
     *            Hibernate session
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public void insertObservationMutliValue(Set<ObservationConstellation> observationConstellations,
            FeatureOfInterest feature, OmObservation containerObservation, Session session) throws OwsExceptionReport {
        List<OmObservation> unfoldObservations = HibernateObservationUtilities.unfoldObservation(containerObservation);
        for (OmObservation sosObservation : unfoldObservations) {
            insertObservationSingleValue(observationConstellations, feature, sosObservation, session);
        }
    }

    // /**
    // * Set observation identifier from container observation to observation
    // *
    // * @param containerObservation
    // * Container observation
    // * @param sosObservation
    // * Observation
    // * @param antiSubsettingId
    // * Anti subsetting identifier
    // * @param idExtension
    // * Extension for observation identifier
    // */
    // private void setIdentifier(OmObservation containerObservation,
    // OmObservation sosObservation,
    // String antiSubsettingId, String idExtension) {
    // if (containerObservation.isSetIdentifier()) {
    // String subObservationIdentifier = String.format("%s-%s",
    // antiSubsettingId, idExtension);
    // CodeWithAuthority subObsIdentifier = new
    // CodeWithAuthority(subObservationIdentifier);
    // subObsIdentifier.setCodeSpace(containerObservation.getIdentifier().getCodeSpace());
    // sosObservation.setIdentifier(subObsIdentifier);
    // }
    // }
    //
    // /**
    // * Set set id to observation
    // *
    // * @param observation
    // * Observation
    // * @return Set id
    // */
    // private String getSetId(OmObservation observation) {
    // String antiSubsettingId = null;
    // if (observation.getIdentifier() != null) {
    // antiSubsettingId = observation.getIdentifier().getValue();
    // }
    //
    // if (antiSubsettingId == null || antiSubsettingId.isEmpty()) {
    // // if identifier of sweArrayObservation is not set, generate UUID
    // // for antisubsetting column
    // antiSubsettingId = UUID.randomUUID().toString();
    // }
    // return antiSubsettingId;
    // }

    /**
     * Add phenomenon and result time to observation object
     * 
     * @param observation
     *            Observation object
     * @param phenomenonTime
     *            SOS phenomenon time
     * @param resultTime
     *            SOS result Time
     * @throws CodedException
     *             If an error occurs
     */
    public void addPhenomeonTimeAndResultTimeToObservation(Observation observation, Time phenomenonTime,
            TimeInstant resultTime) throws CodedException {
        addPhenomenonTimeToObservation(observation, phenomenonTime);
        addResultTimeToObservation(observation, resultTime, phenomenonTime);
    }

    /**
     * Add phenomenon time to observation object
     * 
     * @param observation
     *            Observation object
     * @param phenomenonTime
     *            SOS phenomenon time
     */
    public void addPhenomenonTimeToObservation(Observation observation, Time phenomenonTime) {
        if (phenomenonTime instanceof TimeInstant) {
            TimeInstant time = (TimeInstant) phenomenonTime;
            observation.setPhenomenonTimeStart(time.getValue().toDate());
            observation.setPhenomenonTimeEnd(time.getValue().toDate());
        } else if (phenomenonTime instanceof TimePeriod) {
            TimePeriod time = (TimePeriod) phenomenonTime;
            observation.setPhenomenonTimeStart(time.getStart().toDate());
            observation.setPhenomenonTimeEnd(time.getEnd().toDate());
        }
    }

    /**
     * Add result time to observation object
     * 
     * @param observation
     *            Observation object
     * @param resultTime
     *            SOS result time
     * @param phenomenonTime
     *            SOS phenomenon time
     * @throws CodedException
     *             If an error occurs
     */
    public void addResultTimeToObservation(Observation observation, TimeInstant resultTime, Time phenomenonTime)
            throws CodedException {
        if (resultTime != null) {
            if (resultTime.getValue() != null) {
                observation.setResultTime(resultTime.getValue().toDate());
            } else if (TimeIndeterminateValue.contains(Sos2Constants.EN_PHENOMENON_TIME)
                    && phenomenonTime instanceof TimeInstant) {
                observation.setResultTime(((TimeInstant) phenomenonTime).getValue().toDate());
            } else {
                throw new NoApplicableCodeException()
                        .withMessage("Error while adding result time to Hibernate Observation entitiy!");
            }
        } else {
            if (phenomenonTime instanceof TimeInstant) {
                observation.setResultTime(((TimeInstant) phenomenonTime).getValue().toDate());
            } else {
                throw new NoApplicableCodeException()
                        .withMessage("Error while adding result time to Hibernate Observation entitiy!");
            }
        }
    }

    /**
     * Add valid time to observation object
     * 
     * @param observation
     *            Observation object
     * @param validTime
     *            SOS valid time
     */
    public void addValidTimeToObservation(Observation observation, TimePeriod validTime) {
        if (validTime != null) {
            observation.setValidTimeStart(validTime.getStart().toDate());
            observation.setValidTimeEnd(validTime.getEnd().toDate());
        }
    }

    /**
     * Create an observation object from SOS calue
     * 
     * @param value
     *            SOS value
     * @param session
     *            Hibernate session
     * @return Observation object
     */
    public Observation createObservationFromValue(Value<?> value, Session session) {
        if (value instanceof BooleanValue) {
            BooleanObservation observation = new BooleanObservation();
            observation.setValue(((BooleanValue) value).getValue());
            return observation;
        } else if (value instanceof UnknownValue) {
            BlobObservation observation = new BlobObservation();
            observation.setValue(((UnknownValue) value).getValue());
            return observation;
        } else if (value instanceof CategoryValue) {
            CategoryObservation observation = new CategoryObservation();
            observation.setValue(((CategoryValue) value).getValue());
            return observation;
        } else if (value instanceof CountValue) {
            CountObservation observation = new CountObservation();
            observation.setValue(((CountValue) value).getValue());
            return observation;
        } else if (value instanceof GeometryValue) {
            GeometryObservation observation = new GeometryObservation();
            observation.setValue(((GeometryValue) value).getValue());
            return observation;
        } else if (value instanceof QuantityValue) {
            NumericObservation observation = new NumericObservation();
            observation.setValue(((QuantityValue) value).getValue());
            return observation;
        } else if (value instanceof TextValue) {
            TextObservation observation = new TextObservation();
            observation.setValue(((TextValue) value).getValue());
            return observation;
        } else if (value instanceof SweDataArrayValue) {
            SweDataArrayObservation observation = new SweDataArrayObservation();
            observation.setValue(((SweDataArrayValue) value).getValue().getXml());
            return observation;
        }
        return new Observation();
    }

    /**
     * Get Hibernate Criteria for result model
     * 
     * @param resultModel
     *            Result model
     * @param session
     *            Hibernate session
     * @return Hibernate Criteria
     */
    public Criteria getObservationClassCriteriaForResultModel(String resultModel, Session session) {
        if (StringHelper.isNotEmpty(resultModel)) {
            if (resultModel.equals(OmConstants.OBS_TYPE_MEASUREMENT)) {
                return createCriteriaForObservationClass(NumericObservation.class, session);
            } else if (resultModel.equals(OmConstants.OBS_TYPE_COUNT_OBSERVATION)) {
                return createCriteriaForObservationClass(CountObservation.class, session);
            } else if (resultModel.equals(OmConstants.OBS_TYPE_CATEGORY_OBSERVATION)) {
                return createCriteriaForObservationClass(CategoryObservation.class, session);
            } else if (resultModel.equals(OmConstants.OBS_TYPE_TRUTH_OBSERVATION)) {
                return createCriteriaForObservationClass(BooleanObservation.class, session);
            } else if (resultModel.equals(OmConstants.OBS_TYPE_TEXT_OBSERVATION)) {
                return createCriteriaForObservationClass(TextObservation.class, session);
            } else if (resultModel.equals(OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION)) {
                return createCriteriaForObservationClass(GeometryObservation.class, session);
            } else if (resultModel.equals(OmConstants.OBS_TYPE_COMPLEX_OBSERVATION)) {
                return createCriteriaForObservationClass(BlobObservation.class, session);
            }
        }
        return createCriteriaForObservationClass(Observation.class, session);
    }

    /**
     * Create Hibernate Criteria for Class
     * 
     * @param clazz
     *            Class
     * @param session
     *            Hibernate session
     * @return Hibernate Criteria for Class
     */
    @SuppressWarnings("rawtypes")
    public Criteria createCriteriaForObservationClass(Class clazz, Session session) {
        return session.createCriteria(clazz).add(Restrictions.eq(Observation.DELETED, false))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

}
