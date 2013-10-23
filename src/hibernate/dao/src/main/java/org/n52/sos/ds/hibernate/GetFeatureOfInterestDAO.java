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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.AbstractGetFeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.dao.HibernateSqlQueryConstants;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.ObservationInfo;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.TemporalRestrictions;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.om.features.FeatureCollection;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.GetFeatureOfInterestRequest;
import org.n52.sos.response.GetFeatureOfInterestResponse;
import org.n52.sos.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @since 4.0.0
 * 
 */
public class GetFeatureOfInterestDAO extends AbstractGetFeatureOfInterestDAO implements HibernateSqlQueryConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetFeatureOfInterestDAO.class);

    private final HibernateSessionHolder sessionHolder = new HibernateSessionHolder();

    private static final String SQL_QUERY_GET_FEATURE_FOR_IDENTIFIER = "getFeatureForIdentifier";

    private static final String SQL_QUERY_GET_FEATURE_FOR_IDENTIFIER_PROCEDURE = "getFeatureForIdentifierProcedure";

    private static final String SQL_QUERY_GET_FEATURE_FOR_IDENTIFIER_OBSERVED_PROPERTY =
            "getFeatureForIdentifieObservablePropertyr";

    private static final String SQL_QUERY_GET_FEATURE_FOR_IDENTIFIER_PROCEDURE_OBSERVED_PROPERTY =
            "getFeatureForIdentifierProcedureObservableProperty";

    private static final String SQL_QUERY_GET_FEATURE_FOR_PROCEDURE = "getFeatureForProcedure";

    private static final String SQL_QUERY_GET_FEATURE_FOR_PROCEDURE_OBSERVED_PROPERTY =
            "getFeatureForProcedureObservableProperty";

    private static final String SQL_QUERY_GET_FEATURE_FOR_OBSERVED_PROPERTY = "getFeatureForObservableProperty";

    public GetFeatureOfInterestDAO() {
        super(SosConstants.SOS);
    }

    @Override
    public GetFeatureOfInterestResponse getFeatureOfInterest(final GetFeatureOfInterestRequest request)
            throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            FeatureCollection featureCollection;

            if (isSos100(request)) {
                // sos 1.0.0 either or
                if (isMixedFeatureIdentifierAndSpatialFilters(request)) {
                    throw new NoApplicableCodeException()
                            .withMessage("Only one out of featureofinterestid or location possible.");
                } else if (isFeatureIdentifierRequest(request) || isSpatialFilterRequest(request)) {
                    featureCollection = getFeatures(request, session);
                } else {
                    throw new CompositeOwsException(new MissingParameterValueException(
                            Sos1Constants.GetFeatureOfInterestParams.featureOfInterestID),
                            new MissingParameterValueException(Sos1Constants.GetFeatureOfInterestParams.location));
                }
            } else // SOS 2.0
            {
                featureCollection = getFeatures(request, session);
                /*
                 * Now, we return the list of returned features and not a
                 * complex encoded relatedFeature See
                 * AbstractGetFeatureOfInterestDAO:100-195 Don't forget to
                 * activate in MiscSettings the relatedFeature setting
                 * featureCollection = processRelatedFeatures(
                 * request.getFeatureIdentifiers(), featureCollection,
                 * ServiceConfiguration
                 * .getInstance().getRelatedSamplingFeatureRoleForChildFeatures
                 * ());
                 */
            }
            final GetFeatureOfInterestResponse response = new GetFeatureOfInterestResponse();
            response.setService(request.getService());
            response.setVersion(request.getVersion());
            response.setAbstractFeature(featureCollection);
            return response;
        } catch (final HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage(
                    "Error while querying feature of interest data!");
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    private boolean isSpatialFilterRequest(final GetFeatureOfInterestRequest request) {
        return request.getSpatialFilters() != null && !request.getSpatialFilters().isEmpty();
    }

    private boolean isFeatureIdentifierRequest(final GetFeatureOfInterestRequest request) {
        return request.getFeatureIdentifiers() != null && !request.getFeatureIdentifiers().isEmpty();
    }

    private boolean isMixedFeatureIdentifierAndSpatialFilters(final GetFeatureOfInterestRequest request) {
        return isFeatureIdentifierRequest(request) && isSpatialFilterRequest(request);
    }

    private boolean isSos100(final GetFeatureOfInterestRequest request) {
        return request.getVersion().equals(Sos1Constants.SERVICEVERSION);
    }

    private FeatureCollection getFeatures(final GetFeatureOfInterestRequest request, final Session session)
            throws OwsExceptionReport {
        final Set<String> foiIDs = new HashSet<String>(queryFeatureIdentifiersForParameter(request, session));
        if (request.isSetFeatureOfInterestIdentifiers()) {
            addRequestedRelatedFeatures(foiIDs, request.getFeatureIdentifiers());
        }
        // feature of interest
        return new FeatureCollection(getConfigurator().getFeatureQueryHandler().getFeatures(
                new ArrayList<String>(foiIDs), request.getSpatialFilters(), session, request.getVersion(), -1));
    }

    /**
     * Adds the identifiers from <tt>featureIdentifiers</tt> to the
     * <tt>foiIDs</tt> if the feature is an relatedFeature and a child is
     * already contained in <tt>foiIDs</tt>
     * 
     */
    private void addRequestedRelatedFeatures(final Set<String> foiIDs, final List<String> featureIdentifiers) {
        requestedFeatures: for (final String requestedFeature : featureIdentifiers) {
            if (isRelatedFeature(requestedFeature)) {
                final Set<String> childFeatures = getCache().getChildFeatures(requestedFeature, true, false);
                for (final String featureWithObservation : foiIDs) {
                    if (childFeatures.contains(featureWithObservation)) {
                        foiIDs.add(requestedFeature);
                        continue requestedFeatures;
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> queryFeatureIdentifiersForParameter(final GetFeatureOfInterestRequest req,
            final Session session) throws OwsExceptionReport {
        Criteria c = null;
        if (req.containsOnlyFeatureParameter()) {
            if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_FEATURE_FOR_IDENTIFIER, session)) {
                Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_FEATURE_FOR_IDENTIFIER);
                namedQuery.setParameter(FEATURES, req.getFeatureIdentifiers());
                LOGGER.debug("QUERY queryFeatureIdentifiersForParameter(feature) with NamedQuery: {}",
                        SQL_QUERY_GET_FEATURE_FOR_IDENTIFIER);
                return namedQuery.list();
            } else {
                c =
                        session.createCriteria(FeatureOfInterest.class).setProjection(
                                Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));
                // relates to observations.
                if (req.isSetFeatureOfInterestIdentifiers()) {
                    final Collection<String> features = getFeatureIdentifiers(req.getFeatureIdentifiers());
                    if (features != null && !features.isEmpty()) {
                        c.add(Restrictions.in(FeatureOfInterest.IDENTIFIER, features));
                    }
                }
            }
        } else {
            if (checkForNamedQueries(req, session)) {
                return executeNamedQuery(req, session);
            }

            // TODO get foi ids from foi table. Else only fois returned which
            c = session.createCriteria(ObservationInfo.class);
            final Criteria fc = c.createCriteria(ObservationInfo.FEATURE_OF_INTEREST);
            fc.setProjection(Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));

            // relates to observations.
            if (req.isSetFeatureOfInterestIdentifiers()) {
                final Collection<String> features = getFeatureIdentifiers(req.getFeatureIdentifiers());
                if (features != null && !features.isEmpty()) {
                    fc.add(Restrictions.in(FeatureOfInterest.IDENTIFIER, features));
                }
            }
            // observableProperties
            if (req.isSetObservableProperties()) {
                c.createCriteria(ObservationInfo.OBSERVABLE_PROPERTY).add(
                        Restrictions.in(ObservableProperty.IDENTIFIER, req.getObservedProperties()));
            }
            // procedures
            if (req.isSetProcedures()) {
                c.createCriteria(ObservationInfo.PROCEDURE).add(
                        Restrictions.in(Procedure.IDENTIFIER, req.getProcedures()));
            }
            // temporal filters (SOS 1.0.0)
            if (req.isSetTemporalFilters()) {
                c.add(TemporalRestrictions.filter(req.getTemporalFilters()));
            }
        }
        LOGGER.debug("QUERY queryFeatureIdentifiersForParameter(request): {}", HibernateHelper.getSqlString(c));
        return c.list();
    }

    private boolean checkForNamedQueries(GetFeatureOfInterestRequest req, Session session) {
        final boolean features = req.isSetFeatureOfInterestIdentifiers();
        final boolean observableProperties = req.isSetObservableProperties();
        final boolean procedures = req.isSetProcedures();
        // all
        if (features && observableProperties && procedures) {
            return HibernateHelper.isNamedQuerySupported(
                    SQL_QUERY_GET_FEATURE_FOR_IDENTIFIER_PROCEDURE_OBSERVED_PROPERTY, session);
        }
        // observableProperties and procedures
        else if (!features && observableProperties && procedures) {
            return HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_FEATURE_FOR_PROCEDURE_OBSERVED_PROPERTY,
                    session);
        }
        // only observableProperties
        else if (!features && observableProperties && !procedures) {
            return HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_FEATURE_FOR_OBSERVED_PROPERTY, session);
        }
        // only procedures
        else if (!features && !observableProperties && procedures) {
            return HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_FEATURE_FOR_PROCEDURE, session);
        }
        // features and observableProperties
        else if (features && observableProperties && !procedures) {
            return HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_FEATURE_FOR_IDENTIFIER_OBSERVED_PROPERTY,
                    session);
        }
        // features and procedures
        else if (features && !observableProperties && procedures) {
            return HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_FEATURE_FOR_IDENTIFIER_PROCEDURE, session);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private List<String> executeNamedQuery(GetFeatureOfInterestRequest req, Session session) {
        final boolean features = req.isSetFeatureOfInterestIdentifiers();
        final boolean observableProperties = req.isSetObservableProperties();
        final boolean procedures = req.isSetProcedures();
        String namedQueryName = null;
        Map<String, Collection<String>> parameter = Maps.newHashMap();
        // all
        if (features && observableProperties && procedures) {
            namedQueryName = SQL_QUERY_GET_FEATURE_FOR_IDENTIFIER_PROCEDURE_OBSERVED_PROPERTY;
            parameter.put(FEATURES, req.getFeatureIdentifiers());
            parameter.put(OBSERVABLE_PROPERTIES, req.getObservedProperties());
            parameter.put(PROCEDURES, req.getProcedures());
        }
        // observableProperties and procedures
        else if (!features && observableProperties && procedures) {
            namedQueryName = SQL_QUERY_GET_FEATURE_FOR_PROCEDURE_OBSERVED_PROPERTY;
            parameter.put(OBSERVABLE_PROPERTIES, req.getObservedProperties());
            parameter.put(PROCEDURES, req.getProcedures());
        }
        // only observableProperties
        else if (!features && observableProperties && !procedures) {
            namedQueryName = SQL_QUERY_GET_FEATURE_FOR_OBSERVED_PROPERTY;
            parameter.put(OBSERVABLE_PROPERTIES, req.getObservedProperties());
        }
        // only procedures
        else if (!features && !observableProperties && procedures) {
            namedQueryName = SQL_QUERY_GET_FEATURE_FOR_PROCEDURE;
            parameter.put(PROCEDURES, req.getProcedures());
        }
        // features and observableProperties
        else if (features && observableProperties && !procedures) {
            namedQueryName = SQL_QUERY_GET_FEATURE_FOR_IDENTIFIER_OBSERVED_PROPERTY;
            parameter.put(FEATURES, req.getFeatureIdentifiers());
            parameter.put(OBSERVABLE_PROPERTIES, req.getObservedProperties());
        }
        // features and procedures
        else if (features && !observableProperties && procedures) {
            namedQueryName = SQL_QUERY_GET_FEATURE_FOR_IDENTIFIER_PROCEDURE;
            parameter.put(FEATURES, req.getFeatureIdentifiers());
            parameter.put(PROCEDURES, req.getProcedures());
        }
        if (StringHelper.isNotEmpty(namedQueryName)) {
            Query namedQuery = session.getNamedQuery(namedQueryName);
            for (String key : parameter.keySet()) {
                namedQuery.setParameterList(key, parameter.get(key));
            }
            LOGGER.debug("QUERY getProceduresForFeatureOfInterest(feature) with NamedQuery: {}", namedQuery);
            return namedQuery.list();
        }
        return Lists.newLinkedList();
    }

}
