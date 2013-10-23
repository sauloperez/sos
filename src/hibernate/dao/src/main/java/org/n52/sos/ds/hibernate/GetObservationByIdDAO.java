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

import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.ds.AbstractGetObservationByIdDAO;
import org.n52.sos.ds.hibernate.dao.ObservationDAO;
import org.n52.sos.ds.hibernate.dao.SpatialFilteringProfileDAO;
import org.n52.sos.ds.hibernate.entities.Observation;
import org.n52.sos.ds.hibernate.entities.SpatialFilteringProfile;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.GetObservationByIdRequest;
import org.n52.sos.response.GetObservationByIdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 4.0.0
 * 
 */
public class GetObservationByIdDAO extends AbstractGetObservationByIdDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetObservationByIdDAO.class);

    private HibernateSessionHolder sessionHolder = new HibernateSessionHolder();

    public GetObservationByIdDAO() {
        super(SosConstants.SOS);
    }

    @Override
    public GetObservationByIdResponse getObservationById(GetObservationByIdRequest request) throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            List<Observation> observations = queryObservation(request, session);
            GetObservationByIdResponse response = new GetObservationByIdResponse();
            response.setService(request.getService());
            response.setVersion(request.getVersion());
            response.setResponseFormat(request.getResponseFormat());
            Map<Long, SpatialFilteringProfile> spatialFilteringProfile =
                    new SpatialFilteringProfileDAO().getSpatialFilertingProfiles(
                            HibernateObservationUtilities.getObservationIds(observations), session);
            response.setObservationCollection(HibernateObservationUtilities.createSosObservationsFromObservations(
                    observations, spatialFilteringProfile, request.getVersion(), request.getResultModel(), session));
            return response;

        } catch (HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!");
        } catch (ConverterException ce) {
            throw new NoApplicableCodeException().causedBy(ce).withMessage("Error while processing observation data!");
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Observation> queryObservation(GetObservationByIdRequest request, Session session) {
        final Criteria c =
                new ObservationDAO().getObservationClassCriteriaForResultModel(request.getResultModel(), session);
        c.add(Restrictions.in(Observation.IDENTIFIER, request.getObservationIdentifier()));
        LOGGER.debug("QUERY queryObservation(request): {}", HibernateHelper.getSqlString(c));
        return c.list();
    }
}
