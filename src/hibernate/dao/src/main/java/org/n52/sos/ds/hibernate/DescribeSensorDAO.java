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

import static org.n52.sos.util.http.HTTPStatus.INTERNAL_SERVER_ERROR;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.convert.ConverterRepository;
import org.n52.sos.ds.AbstractDescribeSensorDAO;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.dao.ValidProcedureTimeDAO;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.TProcedure;
import org.n52.sos.ds.hibernate.entities.ValidProcedureTime;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.procedure.HibernateProcedureConverter;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.Time.TimeIndeterminateValue;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsOperation;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.request.DescribeSensorRequest;
import org.n52.sos.response.DescribeSensorResponse;
import org.n52.sos.service.operator.ServiceOperatorKey;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Implementation of the interface IDescribeSensorDAO
 * 
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @author ShaneStClair
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 * 
 * @since 4.0.0
 */
public class DescribeSensorDAO extends AbstractDescribeSensorDAO {
    private final HibernateSessionHolder sessionHolder = new HibernateSessionHolder();

    private final HibernateProcedureConverter procedureConverter = new HibernateProcedureConverter();

    public DescribeSensorDAO() {
        super(SosConstants.SOS);
    }

    @Override
    public DescribeSensorResponse getSensorDescription(final DescribeSensorRequest request) throws OwsExceptionReport {
        // sensorDocument which should be returned
        Session session = null;
        try {
            final DescribeSensorResponse response = new DescribeSensorResponse();
            response.setService(request.getService());
            response.setVersion(request.getVersion());
            response.setOutputFormat(request.getProcedureDescriptionFormat());
            session = sessionHolder.getSession();
            // check for transactional SOS.
            if (HibernateHelper.isEntitySupported(ValidProcedureTime.class, session)) {
                response.setSensorDescriptions(getProcedureDescriptions(request, session));
            } else {
                response.addSensorDescription(getProcedureDescription(request, session));
            }
            return response;
        } catch (final HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage(
                    "Error while querying data for DescribeSensor document!");
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    /**
     * Get procedure description for non transactional SOS
     * 
     * @param request
     *            DescribeSensorRequest request
     * @param session
     *            Hibernate session
     * @return Matched procedure description
     * @throws OwsExceptionReport
     *             If an error occurs
     * @throws ConverterException
     *             If an error occurs
     */
    private SosProcedureDescription getProcedureDescription(DescribeSensorRequest request, Session session)
            throws OwsExceptionReport {
        final Procedure procedure = new ProcedureDAO().getProcedureForIdentifier(request.getProcedure(), session);
        if (procedure == null) {
            throw new NoApplicableCodeException().causedBy(
                    new IllegalArgumentException("Parameter 'procedure' should not be null!")).setStatus(
                    INTERNAL_SERVER_ERROR);
        }
        return procedureConverter.createSosProcedureDescription(procedure, request.getProcedureDescriptionFormat(),
                request.getVersion(), session);
    }

    /**
     * @param request
     *            DescribeSensorRequest request
     * @param session
     *            Hibernate session
     * @return Matching procedure descriptions
     * @throws OwsExceptionReport
     *             If an error occurs
     * @throws ConverterException
     *             If an error occurs
     */
    private List<SosProcedureDescription> getProcedureDescriptions(DescribeSensorRequest request, Session session)
            throws OwsExceptionReport {
        Set<String> possibleProcedureDescriptionFormats =
                getPossibleProcedureDescriptionFormats(request.getProcedureDescriptionFormat());
        final TProcedure procedure =
                new ProcedureDAO().getTProcedureForIdentifier(request.getProcedure(),
                        possibleProcedureDescriptionFormats, request.getValidTime(), session);
        List<SosProcedureDescription> list = Lists.newLinkedList();
        if (procedure != null) {
            List<ValidProcedureTime> validProcedureTimes =
                    new ValidProcedureTimeDAO().getValidProcedureTimes(procedure, possibleProcedureDescriptionFormats,
                            request.getValidTime(), session);
            for (ValidProcedureTime validProcedureTime : validProcedureTimes) {
                SosProcedureDescription sosProcedureDescription =
                        procedureConverter.createSosProcedureDescriptionFromValidProcedureTime(procedure,
                                validProcedureTime, request.getVersion(), session);
                sosProcedureDescription.setValidTime(getValidTime(validProcedureTime));
                list.add(sosProcedureDescription);
            }
        } else {
            if (!request.isSetValidTime()) {
                throw new NoApplicableCodeException().causedBy(
                        new IllegalArgumentException("Parameter 'procedure' should not be null!")).setStatus(
                        INTERNAL_SERVER_ERROR);
            }
        }
        return list;
    }

    /**
     * @param validProcedureTime
     * @return Valid time
     */
    private Time getValidTime(ValidProcedureTime validProcedureTime) {
        TimePeriod time = new TimePeriod();
        time.setStart(new DateTime(validProcedureTime.getStartTime(), DateTimeZone.UTC));
        if (validProcedureTime.getEndTime() == null) {
            time.setEndIndet(TimeIndeterminateValue.unknown);
        } else {
            time.setEnd(new DateTime(validProcedureTime.getEndTime(), DateTimeZone.UTC));
        }
        return time;
    }

    @Override
    protected void setOperationsMetadata(OwsOperation opsMeta, String service, String version)
            throws OwsExceptionReport {
        super.setOperationsMetadata(opsMeta, service, version);
        if (version.equals(Sos2Constants.SERVICEVERSION)) {
            opsMeta.addAnyParameterValue(Sos2Constants.DescribeSensorParams.validTime);
        }
    }

    private Set<String> getPossibleProcedureDescriptionFormats(String procedureDescriptionFormat) {
        Set<String> possibleFormats = Sets.newHashSet();
        possibleFormats.add(procedureDescriptionFormat);
        if (SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE.equalsIgnoreCase(procedureDescriptionFormat)) {
            possibleFormats.add(SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL);
        } else if (SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL.equalsIgnoreCase(procedureDescriptionFormat)) {
            possibleFormats.add(SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE);
        }
        String procedureDescriptionFormatMatchingString =
                getProcedureDescriptionFormatMatchingString(procedureDescriptionFormat);
        for (Entry<ServiceOperatorKey, Set<String>> pdfByServiceOperatorKey : CodingRepository.getInstance()
                .getAllProcedureDescriptionFormats().entrySet()) {
            for (String pdfFromRepository : pdfByServiceOperatorKey.getValue()) {
                if (procedureDescriptionFormatMatchingString
                        .equals(getProcedureDescriptionFormatMatchingString(pdfFromRepository))) {
                    possibleFormats.add(pdfFromRepository);
                }
            }
        }
        possibleFormats.addAll(ConverterRepository.getInstance().getFromNamespaceConverterTo(
                procedureDescriptionFormat));
        return possibleFormats;
    }

    private String getProcedureDescriptionFormatMatchingString(String procedureDescriptionFormat) {
        // match against lowercase string, ignoring whitespace
        return procedureDescriptionFormat.toLowerCase().replaceAll("\\s", "");
    }
}
