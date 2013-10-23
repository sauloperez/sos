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
package org.n52.sos.ds.hibernate;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.ResultTransformer;
import org.joda.time.DateTime;
import org.n52.sos.ds.hibernate.dao.HibernateSqlQueryConstants;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.ObservationInfo;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.gda.AbstractGetDataAvailabilityDAO;
import org.n52.sos.gda.GetDataAvailabilityRequest;
import org.n52.sos.gda.GetDataAvailabilityResponse;
import org.n52.sos.gda.GetDataAvailabilityResponse.DataAvailability;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * {@code IGetDataAvailabilityDao} to handle {@link GetDataAvailabilityRequest}
 * s.
 * 
 * @author Christian Autermann
 * @since 4.0.0
 */
public class GetDataAvailabilityDAO extends AbstractGetDataAvailabilityDAO implements HibernateSqlQueryConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetDataAvailabilityDAO.class);

    private HibernateSessionHolder sessionHolder = new HibernateSessionHolder();

    private static final String SQL_QUERY_GET_DATA_AVAILABILITY_FOR_IDENTIFIER = "getDataAvailabilityForIdentifier";

    private static final String SQL_QUERY_GET_DATA_AVAILABILITY_FOR_IDENTIFIER_PROCEDURE =
            "getDataAvailabilityForIdentifierProcedure";

    private static final String SQL_QUERY_GET_DATA_AVAILABILITY_FOR_IDENTIFIER_OBSERVED_PROPERTY =
            "getDataAvailabilityForIdentifieObservablePropertyr";

    private static final String SQL_QUERY_GET_DATA_AVAILABILITY_FOR_IDENTIFIER_PROCEDURE_OBSERVED_PROPERTY =
            "getDataAvailabilityForIdentifierProcedureObservableProperty";

    private static final String SQL_QUERY_GET_DATA_AVAILABILITY_FOR_PROCEDURE = "getDataAvailabilityForProcedure";

    private static final String SQL_QUERY_GET_DATA_AVAILABILITY_FOR_PROCEDURE_OBSERVED_PROPERTY =
            "getDataAvailabilityForProcedureObservableProperty";

    private static final String SQL_QUERY_GET_DATA_AVAILABILITY_FOR_OBSERVED_PROPERTY =
            "getDataAvailabilityForObservableProperty";

    public GetDataAvailabilityDAO() {
        super(SosConstants.SOS);
    }

    @Override
    public GetDataAvailabilityResponse getDataAvailability(GetDataAvailabilityRequest req) throws OwsExceptionReport {
        Session session = sessionHolder.getSession();

        try {
            List<?> dataAvailabilityValues = Lists.newLinkedList();
            if (checkForNamedQueries(req, session)) {
                dataAvailabilityValues = executeNamedQuery(req, session);
            } else {
                Criteria c =
                        session.createCriteria(ObservationInfo.class).add(
                                Restrictions.eq(ObservationInfo.DELETED, false));

                if (req.isSetFeaturesOfInterest()) {
                    c.createCriteria(ObservationInfo.FEATURE_OF_INTEREST).add(
                            Restrictions.in(FeatureOfInterest.IDENTIFIER, req.getFeaturesOfInterest()));
                }
                if (req.isSetProcedures()) {
                    c.createCriteria(ObservationInfo.PROCEDURE).add(
                            Restrictions.in(Procedure.IDENTIFIER, req.getProcedures()));

                }
                if (req.isSetObservedProperties()) {
                    c.createCriteria(ObservationInfo.OBSERVABLE_PROPERTY).add(
                            Restrictions.in(ObservableProperty.IDENTIFIER, req.getObservedProperties()));
                }

                c.setProjection(Projections.projectionList().add(Projections.groupProperty(ObservationInfo.PROCEDURE))
                        .add(Projections.groupProperty(ObservationInfo.OBSERVABLE_PROPERTY))
                        .add(Projections.groupProperty(ObservationInfo.FEATURE_OF_INTEREST))
                        .add(Projections.min(ObservationInfo.PHENOMENON_TIME_START))
                        .add(Projections.max(ObservationInfo.PHENOMENON_TIME_END)));

                c.setResultTransformer(new DataAvailabilityTransformer(session));
                LOGGER.debug("QUERY getDataAvailability(request): {}", HibernateHelper.getSqlString(c));
                dataAvailabilityValues = c.list();
            }
            GetDataAvailabilityResponse response = new GetDataAvailabilityResponse();
            response.setService(req.getService());
            response.setVersion(req.getVersion());
            for (Object o : dataAvailabilityValues) {
                response.addDataAvailability((DataAvailability) o);
            }
            return response;
        } catch (HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage(
                    "Error while querying data for GetDataAvailability!");
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    private boolean checkForNamedQueries(GetDataAvailabilityRequest req, Session session) {
        final boolean features = req.isSetFeaturesOfInterest();
        final boolean observableProperties = req.isSetObservedProperties();
        final boolean procedures = req.isSetProcedures();
        // all
        if (features && observableProperties && procedures) {
            return HibernateHelper.isNamedQuerySupported(
                    SQL_QUERY_GET_DATA_AVAILABILITY_FOR_IDENTIFIER_PROCEDURE_OBSERVED_PROPERTY, session);
        }
        // observableProperties and procedures
        else if (!features && observableProperties && procedures) {
            return HibernateHelper.isNamedQuerySupported(
                    SQL_QUERY_GET_DATA_AVAILABILITY_FOR_PROCEDURE_OBSERVED_PROPERTY, session);
        }
        // only observableProperties
        else if (!features && observableProperties && !procedures) {
            return HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_DATA_AVAILABILITY_FOR_OBSERVED_PROPERTY,
                    session);
        }
        // only procedures
        else if (!features && !observableProperties && procedures) {
            return HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_DATA_AVAILABILITY_FOR_PROCEDURE, session);
        }
        // features and observableProperties
        else if (features && observableProperties && !procedures) {
            return HibernateHelper.isNamedQuerySupported(
                    SQL_QUERY_GET_DATA_AVAILABILITY_FOR_IDENTIFIER_OBSERVED_PROPERTY, session);
        }
        // features and procedures
        else if (features && !observableProperties && procedures) {
            return HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_DATA_AVAILABILITY_FOR_IDENTIFIER_PROCEDURE,
                    session);
        }
        // only features
        else if (features && !observableProperties && procedures) {
            return HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_DATA_AVAILABILITY_FOR_IDENTIFIER, session);
        }
        return false;
    }

    private List<?> executeNamedQuery(GetDataAvailabilityRequest req, Session session) {
        final boolean features = req.isSetFeaturesOfInterest();
        final boolean observableProperties = req.isSetObservedProperties();
        final boolean procedures = req.isSetProcedures();
        String namedQueryName = null;
        Map<String, Collection<String>> parameter = Maps.newHashMap();
        // all
        if (features && observableProperties && procedures) {
            namedQueryName = SQL_QUERY_GET_DATA_AVAILABILITY_FOR_IDENTIFIER_PROCEDURE_OBSERVED_PROPERTY;
            parameter.put(FEATURES, req.getFeaturesOfInterest());
            parameter.put(OBSERVABLE_PROPERTIES, req.getObservedProperties());
            parameter.put(PROCEDURES, req.getProcedures());
        }
        // observableProperties and procedures
        else if (!features && observableProperties && procedures) {
            namedQueryName = SQL_QUERY_GET_DATA_AVAILABILITY_FOR_PROCEDURE_OBSERVED_PROPERTY;
            parameter.put(OBSERVABLE_PROPERTIES, req.getObservedProperties());
            parameter.put(PROCEDURES, req.getProcedures());
        }
        // only observableProperties
        else if (!features && observableProperties && !procedures) {
            namedQueryName = SQL_QUERY_GET_DATA_AVAILABILITY_FOR_OBSERVED_PROPERTY;
            parameter.put(OBSERVABLE_PROPERTIES, req.getObservedProperties());
        }
        // only procedures
        else if (!features && !observableProperties && procedures) {
            namedQueryName = SQL_QUERY_GET_DATA_AVAILABILITY_FOR_PROCEDURE;
            parameter.put(PROCEDURES, req.getProcedures());
        }
        // features and observableProperties
        else if (features && observableProperties && !procedures) {
            namedQueryName = SQL_QUERY_GET_DATA_AVAILABILITY_FOR_IDENTIFIER_OBSERVED_PROPERTY;
            parameter.put(FEATURES, req.getFeaturesOfInterest());
            parameter.put(OBSERVABLE_PROPERTIES, req.getObservedProperties());
        }
        // features and procedures
        else if (features && !observableProperties && procedures) {
            namedQueryName = SQL_QUERY_GET_DATA_AVAILABILITY_FOR_IDENTIFIER_PROCEDURE;
            parameter.put(FEATURES, req.getFeaturesOfInterest());
            parameter.put(PROCEDURES, req.getProcedures());
        }
        // only features
        else if (features && !observableProperties && procedures) {
            namedQueryName = SQL_QUERY_GET_DATA_AVAILABILITY_FOR_IDENTIFIER;
            parameter.put(FEATURES, req.getFeaturesOfInterest());
        }
        if (StringHelper.isNotEmpty(namedQueryName)) {
            Query namedQuery = session.getNamedQuery(namedQueryName);
            for (String key : parameter.keySet()) {
                namedQuery.setParameterList(key, parameter.get(key));
            }
            LOGGER.debug("QUERY getProceduresForFeatureOfInterest(feature) with NamedQuery: {}", namedQuery);
            namedQuery.setResultTransformer(new DataAvailabilityTransformer(session));
            return namedQuery.list();
        }
        return Lists.newLinkedList();
    }

    /**
     * Class to transform ResultSets to DataAvailabilities.
     */
    private static class DataAvailabilityTransformer implements ResultTransformer {
        private static final long serialVersionUID = -373512929481519459L;

        private final Logger LOGGER = LoggerFactory.getLogger(DataAvailabilityTransformer.class);

        private Session session;

        public DataAvailabilityTransformer(Session session) {
            this.session = session;
        }

        @Override
        public DataAvailability transformTuple(Object[] tuple, String[] aliases) {
            Map<String, ReferenceType> procedures = new HashMap<String, ReferenceType>();
            Map<String, ReferenceType> observableProperties = new HashMap<String, ReferenceType>();
            Map<String, ReferenceType> featuresOfInterest = new HashMap<String, ReferenceType>();
            try {
                ReferenceType procedure = null;
                ReferenceType observableProperty = null;
                ReferenceType featureOfInterest = null;
                TimePeriod timePeriod = null;
                if (tuple.length == 5) {
                    procedure = getProcedureReferenceType(tuple[0], procedures);
                    observableProperty = getObservablePropertyReferenceType(tuple[1], observableProperties);
                    featureOfInterest = getFeatureOfInterestReferenceType(tuple[2], featuresOfInterest);
                    timePeriod =
                            new TimePeriod(new DateTime(((Timestamp) tuple[3]).getTime()), new DateTime(
                                    ((Timestamp) tuple[4]).getTime()));
                } else if (tuple.length == 8) {
                    procedure = getProcedureReferenceType(tuple[0], procedures);
                    addTitleToReferenceType(tuple[1], procedure);
                    observableProperty = getObservablePropertyReferenceType(tuple[2], observableProperties);
                    addTitleToReferenceType(tuple[3], observableProperty);
                    featureOfInterest = getFeatureOfInterestReferenceType(tuple[4], featuresOfInterest);
                    addTitleToReferenceType(tuple[5], featureOfInterest);
                    timePeriod = new TimePeriod(new DateTime(((Timestamp) tuple[6]).getTime()), new DateTime(
                            ((Timestamp) tuple[7]).getTime()));
                }
                return new DataAvailability(procedure, observableProperty, featureOfInterest, timePeriod);
            } catch (OwsExceptionReport e) {
                LOGGER.error("Error while querying GetDataAvailability", e);
            }
            return null;
        }

        private ReferenceType getProcedureReferenceType(Object procedure, Map<String, ReferenceType> procedures)
                throws OwsExceptionReport {
            String identifier = null;
            if (procedure instanceof Procedure) {
                identifier = ((Procedure) procedure).getIdentifier();
            } else if (procedure instanceof String) {
                identifier = (String) procedure;
            } else {
                throw new NoApplicableCodeException().withMessage(
                        "GetDataAvailability procedure query object type {} is not supported!", procedure.getClass()
                                .getName());
            }
            if (!procedures.containsKey(identifier)) {
                ReferenceType referenceType = new ReferenceType(identifier);
                // TODO
                // SosProcedureDescription sosProcedureDescription = new
                // HibernateProcedureConverter().createSosProcedureDescription(procedure,
                // procedure.getProcedureDescriptionFormat().getProcedureDescriptionFormat(),
                // Sos2Constants.SERVICEVERSION, session);
                // if ()
                procedures.put(identifier, referenceType);
            }
            return procedures.get(identifier);
        }

        private ReferenceType getObservablePropertyReferenceType(Object observableProperty,
                Map<String, ReferenceType> observableProperties) throws CodedException {
            String identifier = null;
            if (observableProperty instanceof ObservableProperty) {
                identifier = ((ObservableProperty) observableProperty).getIdentifier();
            } else if (observableProperty instanceof String) {
                identifier = (String) observableProperty;
            } else {
                throw new NoApplicableCodeException().withMessage(
                        "GetDataAvailability procedure query object type {} is not supported!", observableProperty
                                .getClass().getName());
            }
            if (!observableProperties.containsKey(identifier)) {
                ReferenceType referenceType = new ReferenceType(identifier);
                // TODO
                // if (observableProperty.isSetDescription()) {
                // referenceType.setTitle(observableProperty.getDescription());
                // }
                observableProperties.put(identifier, referenceType);
            }
            return observableProperties.get(identifier);
        }

        private ReferenceType getFeatureOfInterestReferenceType(Object featureOfInterest,
                Map<String, ReferenceType> featuresOfInterest) throws OwsExceptionReport {
            String identifier = null;
            if (featureOfInterest instanceof FeatureOfInterest) {
                identifier = ((FeatureOfInterest) featureOfInterest).getIdentifier();
            } else if (featureOfInterest instanceof String) {
                identifier = (String) featureOfInterest;
            } else {
                throw new NoApplicableCodeException().withMessage(
                        "GetDataAvailability procedure query object type {} is not supported!", featureOfInterest
                                .getClass().getName());
            }
            if (!featuresOfInterest.containsKey(identifier)) {
                ReferenceType referenceType = new ReferenceType(identifier);
                AbstractFeature feature =
                        Configurator.getInstance().getFeatureQueryHandler()
                                .getFeatureByID(identifier, session, Sos2Constants.SERVICEVERSION, -1);
                if (feature.isSetNames() && feature.getFirstName().isSetValue()) {
                    referenceType.setTitle(feature.getFirstName().getValue());
                }
                featuresOfInterest.put(identifier, referenceType);
            }
            return featuresOfInterest.get(identifier);
        }

        private void addTitleToReferenceType(Object object, ReferenceType referenceType) {
           if (!referenceType.isSetTitle() && object instanceof String) {
               referenceType.setTitle((String) object);
           }
        }

        @Override
        @SuppressWarnings("rawtypes")
        public List transformList(List collection) {
            return collection;
        }
    }
}
