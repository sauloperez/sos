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

import java.util.HashSet;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.n52.sos.ds.AbstractInsertObservationDAO;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.dao.ObservationConstellationDAO;
import org.n52.sos.ds.hibernate.dao.ObservationDAO;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.SpatialFilteringProfile;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.om.MultiObservationValues;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.InsertObservationRequest;
import org.n52.sos.response.InsertObservationResponse;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.http.HTTPStatus;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 * 
 */
public class InsertObservationDAO extends AbstractInsertObservationDAO {
    private final HibernateSessionHolder sessionHolder = new HibernateSessionHolder();

    public InsertObservationDAO() {
        super(SosConstants.SOS);
    }

    @Override
    public synchronized InsertObservationResponse insertObservation(final InsertObservationRequest request)
            throws OwsExceptionReport {
        final InsertObservationResponse response = new InsertObservationResponse();
        response.setService(request.getService());
        response.setVersion(request.getVersion());
        Session session = null;
        Transaction transaction = null;
        // TODO: check unit and set if available and not defined in DB
        try {
            session = sessionHolder.getSession();
            transaction = session.beginTransaction();
            final ObservationConstellationDAO observationConstellationDAO = new ObservationConstellationDAO();
            final CompositeOwsException exceptions = new CompositeOwsException();
            final Set<String> allOfferings = Sets.newHashSet();
            allOfferings.addAll(request.getOfferings());

            for (final OmObservation sosObservation : request.getObservations()) {
                final OmObservationConstellation sosObsConst = sosObservation.getObservationConstellation();

                if (ServiceConfiguration.getInstance().isStrictSpatialFilteringProfile()
                        && HibernateHelper.isEntitySupported(SpatialFilteringProfile.class, session)
                        && !sosObservation.isSetSpatialFilteringProfileParameter()) {
                    throw new MissingParameterValueException(Sos2Constants.InsertObservationParams.parameter)
                            .withMessage("The sampling geometry definition is missing in the observation because the Spatial Filtering Profile isspecification conform. To use a less restrictive Spatial Filtering Profile you can change this in the Service-Settings!");
                }
                Set<String> offerings = getParentProcedureOfferings(sosObsConst);
                sosObsConst.setOfferings(offerings);
                allOfferings.addAll(offerings);

                final Set<ObservationConstellation> hObservationConstellations =
                        new HashSet<ObservationConstellation>(0);
                FeatureOfInterest hFeature = null;

                for (final String offeringID : sosObsConst.getOfferings()) {
                    ObservationConstellation hObservationConstellation = null;
                    try {
                        hObservationConstellation =
                                observationConstellationDAO.checkObservationConstellation(sosObsConst, offeringID,
                                        session, Sos2Constants.InsertObservationParams.observationType.name());
                    } catch (final OwsExceptionReport owse) {
                        exceptions.add(owse);
                    }
                    if (hObservationConstellation != null) {
                        final FeatureOfInterestDAO featureOfInterestDAO = new FeatureOfInterestDAO();
                        hFeature =
                                featureOfInterestDAO.checkOrInsertFeatureOfInterest(sosObservation
                                        .getObservationConstellation().getFeatureOfInterest(), session);
                        featureOfInterestDAO.checkOrInsertFeatureOfInterestRelatedFeatureRelation(hFeature,
                                hObservationConstellation.getOffering(), session);
                        hObservationConstellations.add(hObservationConstellation);
                    }
                }

                if (!hObservationConstellations.isEmpty()) {
                    final ObservationDAO observationDAO = new ObservationDAO();
                    if (sosObservation.getValue() instanceof SingleObservationValue) {
                        observationDAO.insertObservationSingleValue(hObservationConstellations, hFeature,
                                sosObservation, session);
                    } else if (sosObservation.getValue() instanceof MultiObservationValues) {
                        observationDAO.insertObservationMutliValue(hObservationConstellations, hFeature,
                                sosObservation, session);
                    }
                }
            }
            request.setOfferings(Lists.newArrayList(allOfferings));
            // if no observationConstellation is valid, throw exception
            if (exceptions.size() == request.getObservations().size()) {
                throw exceptions;
            }
            session.flush();
            transaction.commit();
        } catch (final HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            HTTPStatus status = HTTPStatus.INTERNAL_SERVER_ERROR;
            final String exceptionMsg = "Error while inserting new observation!";
            if (he instanceof ConstraintViolationException) {
                final ConstraintViolationException cve = (ConstraintViolationException) he;
                /*
                 * if (cve.getConstraintName() != null) { if
                 * (cve.getConstraintName
                 * ().equalsIgnoreCase(CONSTRAINT_OBSERVATION_IDENTITY)) {
                 * exceptionMsg =
                 * "Observation with same values already contained in database";
                 * } else if (cve.getConstraintName().equalsIgnoreCase(
                 * CONSTRAINT_OBSERVATION_IDENTIFIER_IDENTITY)) { exceptionMsg =
                 * "Observation identifier already contained in database"; } }
                 * else if (cve.getMessage() != null) { if
                 * (cve.getMessage().contains(CONSTRAINT_OBSERVATION_IDENTITY))
                 * { exceptionMsg =
                 * "Observation with same values already contained in database";
                 * exceptionMsg =
                 * "Observation identifier already contained in database"; }
                 * 
                 * }
                 */

                status = HTTPStatus.BAD_REQUEST;
            }
            throw new NoApplicableCodeException().causedBy(he).withMessage(exceptionMsg).setStatus(status);
        } finally {
            sessionHolder.returnSession(session);
        }
        /*
         * TODO: ... all the DS insertion stuff Requirement 68
         * proc/obsProp/Offering same obsType;
         */

        return response;
    }

    /**
     * Get parent offerings for requested procedure and observable property
     * 
     * @param sosObsConst
     *            Requested observation constellation
     * @return Requested offering and valid parent procedure offerings.
     */
    private Set<String> getParentProcedureOfferings(OmObservationConstellation sosObsConst) {
        Set<String> offerings = Sets.newHashSet(sosObsConst.getOfferings());
        // get parent procedures
        Set<String> parentProcedures =
                getCache().getParentProcedures(sosObsConst.getProcedure().getIdentifier(), true, false);
        if (CollectionHelper.isNotEmpty(parentProcedures)) {
            for (String parentProcedure : parentProcedures) {
                // get offerings for parent procdure
                Set<String> offeringsForParentProcedure = getCache().getOfferingsForProcedure(parentProcedure);
                if (CollectionHelper.isNotEmpty(offeringsForParentProcedure)) {
                    for (String offering : offeringsForParentProcedure) {
                        /*
                         * get observable properties for offering and check if
                         * observable property is contained in request and if
                         * parent procedure offering is contained in procedure
                         * offerings. If true, add offering to set.
                         */
                        Set<String> observablePropertiesForOffering =
                                getCache().getObservablePropertiesForOffering(offering);
                        Set<String> offeringsForProcedure =
                                getCache().getOfferingsForProcedure(sosObsConst.getProcedure().getIdentifier());
                        if (CollectionHelper.isNotEmpty(observablePropertiesForOffering)
                                && observablePropertiesForOffering.contains(sosObsConst.getObservableProperty()
                                        .getIdentifier()) && offeringsForProcedure.contains(offering)) {
                            offerings.add(offering);
                        }
                    }
                }
            }
        }
        return offerings;
    }
}