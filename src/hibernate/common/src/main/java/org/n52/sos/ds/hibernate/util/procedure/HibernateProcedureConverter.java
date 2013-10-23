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
package org.n52.sos.ds.hibernate.util.procedure;

import java.util.ArrayList;
import java.util.Map;

import org.hibernate.Session;
import org.n52.sos.convert.Converter;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.convert.ConverterRepository;
import org.n52.sos.ds.hibernate.dao.HibernateSqlQueryConstants;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasProcedureDescriptionFormat;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.ValidProcedureTime;
import org.n52.sos.ds.hibernate.util.procedure.create.DescriptionCreationStrategy;
import org.n52.sos.ds.hibernate.util.procedure.create.FileDescriptionCreationStrategy;
import org.n52.sos.ds.hibernate.util.procedure.create.GeneratedDescriptionCreationStrategy;
import org.n52.sos.ds.hibernate.util.procedure.create.ValidProcedureTimeDescriptionCreationStrategy;
import org.n52.sos.ds.hibernate.util.procedure.create.XmlStringDescriptionCreationStrategy;
import org.n52.sos.ds.hibernate.util.procedure.enrich.HibernateProcedureEnrichment;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.AbstractProcess;
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.util.http.HTTPStatus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike HinderkJ&uuml;rrens</a>
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 * @author <a href="mailto:shane@axiomalaska.com">Shane StClair</a>
 *
 * @since 4.0.0
 *
 * TODO - apply description enrichment to all types of procedures
 * (creates, file, or database) - use setting switches for code flow
 */
public class HibernateProcedureConverter implements HibernateSqlQueryConstants {
    /**
     * Create procedure description from file, single XML text or generate
     *
     * @param procedure                  Hibernate procedure entity
     * @param requestedDescriptionFormat Requested procedure descriptionFormat
     * @param requestedServiceVersion    Requested SOS version
     * @param session                    Hibernate session
     *
     * @return created SosProcedureDescription
     *
     * @throws OwsExceptionReport If an error occurs
     */
    public SosProcedureDescription createSosProcedureDescription(
            Procedure procedure, String requestedDescriptionFormat,
            String requestedServiceVersion, Session session) throws OwsExceptionReport {
        // child hierarchy procedures haven't been queried yet, pass null
        return createSosProcedureDescription(procedure,
                                             requestedDescriptionFormat,
                                             requestedServiceVersion,
                                             null,
                                             session);
    }

    /**
     * Create procedure description from file, single XML text or generate
     *
     * @param procedure                  Hibernate procedure entity
     * @param requestedDescriptionFormat Requested procedure descriptionFormat
     * @param requestedServiceVersion    Requested SOS version
     * @param loadedProcedures           Loaded procedure hierarchy (passed to
     *                                   recursive requests to avoid multiple
     *                                   queries)
     * @param session                    Hibernate session
     *
     * @return created SosProcedureDescription
     *
     * @throws OwsExceptionReport If an error occurs
     */
    public SosProcedureDescription createSosProcedureDescription(
            Procedure procedure,
            String requestedDescriptionFormat, String requestedServiceVersion,
            Map<String, Procedure> loadedProcedures, Session session) throws
            OwsExceptionReport {
        if (procedure == null) {
            throw new NoApplicableCodeException()
                    .causedBy(new IllegalArgumentException("Parameter 'procedure' should not be null!"))
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
        final String descriptionFormat = getFormat(procedure);
        checkOutputFormatWithDescriptionFormat(procedure, requestedDescriptionFormat, descriptionFormat);

        SosProcedureDescription desc = create(procedure, null, session).orNull();
        if (desc != null) {
            enrich(desc, procedure, requestedServiceVersion, descriptionFormat, loadedProcedures, session);
            if (!requestedDescriptionFormat.equals(descriptionFormat)) {
                desc = convert(descriptionFormat, requestedDescriptionFormat, desc);
            }
            desc.setDescriptionFormat(requestedDescriptionFormat);
        }
        return desc;
    }

    /**
     * Create procedure description from XML text stored in ValidProcedureTime
     * table
     *
     * @param procedure          Hibernate procedure entity
     * @param validProcedureTime Hibernate ValidProcedureTime entity
     * @param version            Requested SOS version
     * @param session            Hibernate session
     *
     * @return created SosProcedureDescription
     *
     * @throws OwsExceptionReport If an error occurs
     */
    public SosProcedureDescription createSosProcedureDescriptionFromValidProcedureTime(
            Procedure procedure, ValidProcedureTime validProcedureTime, String version,
            Session session) throws OwsExceptionReport {
        String descriptionFormat = getFormat(validProcedureTime);
        Optional<SosProcedureDescription> description =
                create(procedure, validProcedureTime, session);
        if (description.isPresent()) {
            enrich(description.get(), procedure, version, descriptionFormat, null, session);
            description.get().setDescriptionFormat(descriptionFormat);
        }
        return description.orNull();
    }

    private String getFormat(HasProcedureDescriptionFormat hpdf) {
        return hpdf.getProcedureDescriptionFormat()
                .getProcedureDescriptionFormat();
    }

    /**
     * Checks the requested procedureDescriptionFormat with the datasource
     * procedureDescriptionFormat.
     *
     * @param procedure         the procedure
     * @param requested         requested procedureDescriptionFormat
     * @param descriptionFormat Data source procedureDescriptionFormat
     *
     * @throws OwsExceptionReport If procedureDescriptionFormats are invalid
     */
    @VisibleForTesting
    void checkOutputFormatWithDescriptionFormat(
            Procedure procedure,
            String requested,
            String descriptionFormat) throws OwsExceptionReport {

        if (!requested.equalsIgnoreCase(descriptionFormat) &&
            !(isSensorMLFormat(descriptionFormat) &&
              isSensorMLFormat(requested)) &&
            !existConverter(descriptionFormat, requested)) {
            throw new InvalidParameterValueException()
                    .at(SosConstants.DescribeSensorParams.procedure)
                    .withMessage("The value of the output format is wrong and has to be %s for procedure %s",
                                 descriptionFormat, procedure.getIdentifier())
                    .setStatus(HTTPStatus.BAD_REQUEST);
        }
    }

    private boolean isSensorMLFormat(String pdf) {
        return SensorMLConstants.SENSORML_OUTPUT_FORMAT_MIME_TYPE.equalsIgnoreCase(pdf) ||
               SensorMLConstants.SENSORML_OUTPUT_FORMAT_URL.equalsIgnoreCase(pdf);

    }

    private boolean existConverter(String from, String to) {
        return ConverterRepository.getInstance().hasConverter(from, to);
    }

    private Optional<SosProcedureDescription> create(Procedure procedure, ValidProcedureTime vpt, Session session) throws OwsExceptionReport {
        Optional<DescriptionCreationStrategy> strategy = getCreationStrategy(procedure, vpt);
        if (strategy.isPresent()) {
            return Optional.fromNullable(strategy.get().create(procedure, session));
        } else {
            return Optional.absent();
        }
    }

    private Optional<DescriptionCreationStrategy> getCreationStrategy(Procedure p, ValidProcedureTime vpt) {
        for (DescriptionCreationStrategy strategy : getCreationStrategies(vpt)) {
            if (strategy.apply(p)) {
                return Optional.of(strategy);
            }
        }
        return Optional.absent();
    }

    private ArrayList<DescriptionCreationStrategy> getCreationStrategies(ValidProcedureTime vpt) {
        return Lists.newArrayList(new ValidProcedureTimeDescriptionCreationStrategy(vpt),
                                  new XmlStringDescriptionCreationStrategy(),
                                  new FileDescriptionCreationStrategy(),
                                  new GeneratedDescriptionCreationStrategy());
    }

    /**
     * Enrich the procedure description.
     *
     * @param desc      the description
     * @param procedure the procedure
     * @param version   the version
     * @param format    the format
     * @param cache     the procedure cache
     * @param session   the session
     *
     * @see HibernateProcedureEnrichment
     * @throws OwsExceptionReport if the enrichment fails
     */
    private void enrich(SosProcedureDescription desc, Procedure procedure,
                        String version, String format, Map<String, Procedure> cache,
                        Session session) throws OwsExceptionReport {
        HibernateProcedureEnrichment enricher = new HibernateProcedureEnrichment(this);
        if (desc instanceof SensorML && ((SensorML) desc).isWrapper()) {
            for (AbstractProcess abstractProcess : ((SensorML) desc).getMembers()) {
                enricher.enrich(procedure, abstractProcess, version, format, cache, session);
            }
        } else {
            enricher.enrich(procedure, desc, version, format, cache, session);
        }
    }

    /**
     * Convert the description to another procedure description format.
     *
     * @param fromFormat  the source format
     * @param toFormat    the target format
     * @param description the procedure description.
     *
     * @return the converted description
     *
     * @throws OwsExceptionReport if conversion fails
     */
    private SosProcedureDescription convert(String fromFormat, String toFormat,
                                            SosProcedureDescription description)
            throws OwsExceptionReport {
        try {
            Converter<SosProcedureDescription, Object> converter =
                    ConverterRepository.getInstance()
                    .getConverter(fromFormat, toFormat);
            return converter.convert(description);
        } catch (ConverterException ce) {
            throw new NoApplicableCodeException().causedBy(ce)
                    .withMessage("Error while processing data for DescribeSensor document!");
        }
    }
}
