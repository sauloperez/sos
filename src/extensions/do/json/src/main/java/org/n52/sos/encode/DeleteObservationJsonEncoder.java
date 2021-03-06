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

package org.n52.sos.encode;

import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.encode.json.AbstractSosResponseEncoder;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.exception.ows.concrete.MissingServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingVersionParameterException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ext.deleteobservation.DeleteObservationConstants;
import org.n52.sos.ext.deleteobservation.DeleteObservationResponse;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * TODO JavaDoc
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class DeleteObservationJsonEncoder extends AbstractSosResponseEncoder<DeleteObservationResponse> {

    public DeleteObservationJsonEncoder() {
        super(DeleteObservationResponse.class,
              DeleteObservationConstants.Operations.DeleteObservation);
    }

    @Override
    protected void encodeResponse(ObjectNode json, DeleteObservationResponse t)
            throws OwsExceptionReport {
        if (t == null) {
            throw new UnsupportedEncoderInputException(this, t);
        }
        final CompositeOwsException exceptions = new CompositeOwsException();
        if (t.getService() == null) {
            exceptions.add(new MissingServiceParameterException());
        }
        if (t.getVersion() == null) {
            exceptions.add(new MissingVersionParameterException());
        }
        if (t.getObservationId() == null || t.getObservationId().isEmpty()) {
            exceptions.add(new MissingParameterValueException(DeleteObservationConstants.PARAMETER_NAME));
        }
        exceptions.throwIfNotEmpty();
        json.put(JSONConstants.DELETED_OBSERVATION, t.getObservationId());
    }

}
