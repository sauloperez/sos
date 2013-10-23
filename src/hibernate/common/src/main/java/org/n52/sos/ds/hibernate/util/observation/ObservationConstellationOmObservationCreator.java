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

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.util.procedure.HibernateProcedureConverter;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.quality.SosQuality;
import org.n52.sos.ogc.om.values.NilTemplateValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.sos.SosProcedureDescriptionUnknowType;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public class ObservationConstellationOmObservationCreator extends AbstractOmObservationCreator {
    protected final ObservationConstellation oc;
    protected final List<String> featureIds;

    public ObservationConstellationOmObservationCreator(
            ObservationConstellation observationConstellation,
            List<String> featureOfInterestIdentifiers,
            String version,
            Session session) {
        super(version, session);
        this.oc = observationConstellation;
        this.featureIds = featureOfInterestIdentifiers;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<OmObservation> create() throws OwsExceptionReport, ConverterException {
        final List<OmObservation> observations = Lists.newLinkedList();
        if (getObservationConstellation() != null && getFeatureIds() != null) {
            SosProcedureDescription procedure = getProcedure();
            OmObservableProperty obsProp = getObservableProperty();

            for (final String featureId : getFeatureIds()) {
                final AbstractFeature feature =
                        getFeatureQueryHandler()
                        .getFeatureByID(featureId, getSession(), getVersion(), -1);
                final OmObservationConstellation obsConst =
                        getObservationConstellation(procedure, obsProp, feature);

                final OmObservation sosObservation = new OmObservation();
                sosObservation.setNoDataValue(getNoDataValue());
                sosObservation.setTokenSeparator(getTokenSeparator());
                sosObservation.setTupleSeparator(getTupleSeparator());
                sosObservation.setObservationConstellation(obsConst);
                final NilTemplateValue value = new NilTemplateValue();
                value.setUnit(obsProp.getUnit());
                sosObservation
                        .setValue(new SingleObservationValue(new TimeInstant(), value,
                                                             new ArrayList<SosQuality>(0)));
                observations.add(sosObservation);
            }
        }
        return observations;
    }

    private SosProcedureDescription getProcedure() throws ConverterException, OwsExceptionReport {
        String id = getObservationConstellation().getProcedure().getIdentifier();
        // final SensorML procedure = new SensorML();
        // procedure.setIdentifier(procID);
        Procedure hProcedure = new ProcedureDAO()
               .getProcedureForIdentifier(id, getSession());
        String pdf = hProcedure.getProcedureDescriptionFormat()
            .getProcedureDescriptionFormat();
        if (getActiveProfile().isEncodeProcedureInObservation()) {
            return new HibernateProcedureConverter()
                    .createSosProcedureDescription(hProcedure, pdf, getVersion(), getSession());
        } else {
            return new SosProcedureDescriptionUnknowType(id, pdf, null);
        }
    }

    private OmObservableProperty getObservableProperty() {
        String phenID = getObservationConstellation().getObservableProperty().getIdentifier();
        String description = getObservationConstellation().getObservableProperty().getDescription();
        return new OmObservableProperty(phenID, description, null, null);
    }

    private OmObservationConstellation getObservationConstellation(
            SosProcedureDescription procedure,
            OmObservableProperty obsProp,
            AbstractFeature feature) {
        OmObservationConstellation obsConst =
                new OmObservationConstellation(procedure, obsProp, null, feature, null);
        /* get the offerings to find the templates */
        if (obsConst.getOfferings() == null) {
            obsConst.setOfferings(
                    Sets.newHashSet(
                    getCache().getOfferingsForProcedure(
                    obsConst.getProcedure().getIdentifier())));
        }
        return obsConst;
    }

    /**
     * @return the observation constellation
     */
    protected ObservationConstellation getObservationConstellation() {
        return oc;
    }

    /**
     * @return the featureIds
     */
    protected List<String> getFeatureIds() {
        return featureIds;
    }
}
