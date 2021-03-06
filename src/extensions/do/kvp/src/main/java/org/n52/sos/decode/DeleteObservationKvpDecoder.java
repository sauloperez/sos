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
package org.n52.sos.decode;

import static org.n52.sos.ext.deleteobservation.DeleteObservationConstants.PARAMETER_NAME;
import static org.n52.sos.util.KvpHelper.checkParameterSingleValue;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.n52.sos.decode.kvp.AbstractKvpDecoder;
import org.n52.sos.exception.ows.concrete.MissingRequestParameterException;
import org.n52.sos.exception.ows.concrete.MissingServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingVersionParameterException;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ext.deleteobservation.DeleteObservationConstants;
import org.n52.sos.ext.deleteobservation.DeleteObservationRequest;
import org.n52.sos.ext.deleteobservation.MissingObservationParameterException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.util.KvpHelper;
import org.n52.sos.util.http.MediaTypes;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 1.0.0
 */
public class DeleteObservationKvpDecoder extends AbstractKvpDecoder {

    private static final DecoderKey KVP_DECODER_KEY_TYPE = new OperationDecoderKey(SosConstants.SOS,
            Sos2Constants.SERVICEVERSION, DeleteObservationConstants.Operations.DeleteObservation,
            MediaTypes.APPLICATION_KVP);

    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.singleton(KVP_DECODER_KEY_TYPE);
    }

    public DeleteObservationRequest decode(Map<String, String> objectToDecode) throws OwsExceptionReport {
        if (objectToDecode == null) {
            throw new UnsupportedDecoderInputException(this, objectToDecode);
        }
        DeleteObservationRequest deleteObservationRequest = new DeleteObservationRequest();
        CompositeOwsException exceptions = new CompositeOwsException();
        boolean foundRequest = false, foundService = false, foundVersion = false, foundObservation = false;

        for (String parameterName : objectToDecode.keySet()) {
            String parameterValues = objectToDecode.get(parameterName);
            try {
                if (parameterName.equalsIgnoreCase(OWSConstants.RequestParams.service.name())) {
                    deleteObservationRequest.setService(KvpHelper.checkParameterSingleValue(parameterValues,
                            parameterName));
                    foundService = true;
                } else if (parameterName.equalsIgnoreCase(OWSConstants.RequestParams.version.name())) {
                    deleteObservationRequest.setVersion(KvpHelper.checkParameterSingleValue(parameterValues,
                            parameterName));
                    foundVersion = true;
                } else if (parameterName.equalsIgnoreCase(OWSConstants.RequestParams.request.name())) {
                    KvpHelper.checkParameterSingleValue(parameterValues, parameterName);
                    foundRequest = true;
                } else if (parameterName.equalsIgnoreCase(PARAMETER_NAME)) {
                    deleteObservationRequest.setObservationIdentifier(checkParameterSingleValue(parameterValues,
                            parameterName));
                    foundObservation = true;
                }
            } catch (OwsExceptionReport owse) {
                exceptions.add(owse);
            }
        }

        if (!foundService) {
            exceptions.add(new MissingServiceParameterException());
        }

        if (!foundVersion) {
            exceptions.add(new MissingVersionParameterException());
        }

        if (!foundRequest) {
            exceptions.add(new MissingRequestParameterException());
        }

        if (!foundObservation) {
            exceptions.add(new MissingObservationParameterException());
        }

        exceptions.throwIfNotEmpty();

        return deleteObservationRequest;
    }

}
