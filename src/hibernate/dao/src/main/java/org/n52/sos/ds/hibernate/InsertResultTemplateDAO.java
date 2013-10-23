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

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.ds.AbstractInsertResultTemplateDAO;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.dao.ObservationConstellationDAO;
import org.n52.sos.ds.hibernate.dao.ResultTemplateDAO;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.InvalidObservationTypeException;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.CapabilitiesExtension;
import org.n52.sos.ogc.sos.CapabilitiesExtensionKey;
import org.n52.sos.ogc.sos.CapabilitiesExtensionProvider;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosInsertionCapabilities;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.request.InsertResultTemplateRequest;
import org.n52.sos.response.InsertResultTemplateResponse;

/**
 * @since 4.0.0
 * 
 */
public class InsertResultTemplateDAO extends AbstractInsertResultTemplateDAO implements CapabilitiesExtensionProvider {

    private HibernateSessionHolder sessionHolder = new HibernateSessionHolder();

    public InsertResultTemplateDAO() {
        super(SosConstants.SOS);
    }

    @Override
    public InsertResultTemplateResponse insertResultTemplate(InsertResultTemplateRequest request)
            throws OwsExceptionReport {
        InsertResultTemplateResponse response = new InsertResultTemplateResponse();
        response.setService(request.getService());
        response.setVersion(request.getVersion());
        response.setAcceptedTemplate(request.getIdentifier());
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionHolder.getSession();
            transaction = session.beginTransaction();
            OmObservationConstellation sosObsConst = request.getObservationTemplate();
            // if (request.getResultStructure().getResultStructure() instanceof
            // SosSweDataArray
            // || request.getResultStructure().getResultStructure() instanceof
            // SosSweDataRecord) {
            // sosObsConst.setObservationType(OMConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
            // } else {
            // String exceptionText =
            // "The requested resultStructure is not supported!";
            // throw Util4Exceptions.createInvalidParameterValueException(
            // Sos2Constants.InsertResultTemplateParams.observationType.name(),
            // exceptionText);
            // }
            ObservationConstellation obsConst = null;
            for (String offeringID : sosObsConst.getOfferings()) {
                obsConst =
                        new ObservationConstellationDAO().checkObservationConstellation(sosObsConst, offeringID,
                                session, Sos2Constants.InsertResultTemplateParams.proposedTemplate.name());
                if (obsConst != null) {
                    FeatureOfInterestDAO featureOfInterestDAO = new FeatureOfInterestDAO();
                    FeatureOfInterest feature =
                            featureOfInterestDAO.checkOrInsertFeatureOfInterest(sosObsConst.getFeatureOfInterest(),
                                    session);
                    featureOfInterestDAO.checkOrInsertFeatureOfInterestRelatedFeatureRelation(feature,
                            obsConst.getOffering(), session);
                    new ResultTemplateDAO().checkOrInsertResultTemplate(request, obsConst, feature, session);
                } else {
                    // TODO make better exception.
                    throw new InvalidObservationTypeException(request.getObservationTemplate().getObservationType());
                }
            }
            transaction.commit();
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new NoApplicableCodeException().causedBy(he).withMessage(
                    "Insert result template into database failed!");
        } catch (OwsExceptionReport owse) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw owse;
        } finally {
            sessionHolder.returnSession(session);
        }
        return response;
    }

    @Override
    public CapabilitiesExtension getExtension() {
        final SosInsertionCapabilities insertionCapabilities = new SosInsertionCapabilities();
        insertionCapabilities.addFeatureOfInterestTypes(getCache().getFeatureOfInterestTypes());
        insertionCapabilities.addObservationTypes(getCache().getObservationTypes());
        insertionCapabilities.addProcedureDescriptionFormats(CodingRepository.getInstance()
                .getSupportedProcedureDescriptionFormats(SosConstants.SOS, Sos2Constants.SERVICEVERSION));
        // TODO dynamic
        insertionCapabilities.addSupportedEncoding(SweConstants.ENCODING_TEXT);
        return insertionCapabilities;
    }

    @Override
    public CapabilitiesExtensionKey getCapabilitiesExtensionKey() {
        return new CapabilitiesExtensionKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION);
    }

    @Override
    public boolean hasRelatedOperation() {
        return true;
    }

    @Override
    public String getRelatedOperation() {
        return getOperationName();
    }

}
