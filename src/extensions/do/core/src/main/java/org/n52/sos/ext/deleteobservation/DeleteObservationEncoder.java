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
package org.n52.sos.ext.deleteobservation;

import java.util.Set;

import net.opengis.sosdo.x10.DeleteObservationResponseDocument;
import net.opengis.sosdo.x10.DeleteObservationResponseType;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.AbstractResponseEncoder;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.exception.ows.concrete.MissingServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingVersionParameterException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.w3c.SchemaLocation;

import com.google.common.collect.Sets;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 1.0.0
 */
public class DeleteObservationEncoder extends AbstractResponseEncoder<DeleteObservationResponse> {
    public static final SchemaLocation SCHEMA_LOCATION = new SchemaLocation(DeleteObservationConstants.NS_SOSDO_1_0,
            DeleteObservationConstants.NS_SOSDO_1_0_SCHEMA_LOCATION);

    public DeleteObservationEncoder() {
        super(SosConstants.SOS, Sos2Constants.SERVICEVERSION, DeleteObservationConstants.Operations.DeleteObservation
                .name(), DeleteObservationConstants.NS_SOSDO_1_0, DeleteObservationConstants.NS_SOSDO_1_0_PREFIX,
                DeleteObservationResponse.class);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return DeleteObservationConstants.CONFORMANCE_CLASSES;
    }

    @Override
    protected XmlObject create(DeleteObservationResponse dor) throws OwsExceptionReport {
        if (dor == null) {
            throw new UnsupportedEncoderInputException(this, dor);
        }
        final CompositeOwsException exceptions = new CompositeOwsException();
        if (dor.getService() == null) {
            exceptions.add(new MissingServiceParameterException());
        }
        if (dor.getVersion() == null) {
            exceptions.add(new MissingVersionParameterException());
        }
        if (dor.getObservationId() == null || dor.getObservationId().isEmpty()) {
            exceptions.add(new MissingParameterValueException(DeleteObservationConstants.PARAMETER_NAME));
        }
        exceptions.throwIfNotEmpty();

        String observationId = dor.getObservationId();
        DeleteObservationResponseDocument xbDeleteObsDoc =
                DeleteObservationResponseDocument.Factory.newInstance(getXmlOptions());
        DeleteObservationResponseType xbDeleteObservationResponse = xbDeleteObsDoc.addNewDeleteObservationResponse();
        xbDeleteObservationResponse.setDeletedObservation(observationId);
        return xbDeleteObsDoc;
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(SCHEMA_LOCATION);
    }

    @Override
    protected Set<SchemaLocation> getConcreteSchemaLocations() {
        return Sets.newHashSet();
    }
}
