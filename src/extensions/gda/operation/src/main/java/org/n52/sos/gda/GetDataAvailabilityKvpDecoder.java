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
package org.n52.sos.gda;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.n52.sos.decode.DecoderKey;
import org.n52.sos.decode.OperationDecoderKey;
import org.n52.sos.decode.kvp.AbstractKvpDecoder;
import org.n52.sos.exception.ows.concrete.MissingRequestParameterException;
import org.n52.sos.exception.ows.concrete.MissingServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingVersionParameterException;
import org.n52.sos.exception.ows.concrete.ParameterNotSupportedException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.util.KvpHelper;
import org.n52.sos.util.http.MediaTypes;

/**
 * @since 4.0.0
 * 
 */
public class GetDataAvailabilityKvpDecoder extends AbstractKvpDecoder {
    private static final DecoderKey KVP_DECODER_KEY_TYPE = new OperationDecoderKey(SosConstants.SOS,
            Sos2Constants.SERVICEVERSION, GetDataAvailabilityConstants.OPERATION_NAME, MediaTypes.APPLICATION_KVP);

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.singleton(KVP_DECODER_KEY_TYPE);
    }

    @Override
    public GetDataAvailabilityRequest decode(Map<String, String> element) throws OwsExceptionReport {
        GetDataAvailabilityRequest request = new GetDataAvailabilityRequest();
        CompositeOwsException exceptions = new CompositeOwsException();
        boolean foundRequest = false, foundService = false, foundVersion = false;

        for (String name : element.keySet()) {
            String parameterValues = element.get(name);
            try {
                if (name.equalsIgnoreCase(OWSConstants.RequestParams.service.name())) {
                    request.setService(KvpHelper.checkParameterSingleValue(parameterValues, name));
                    foundService = true;
                } else if (name.equalsIgnoreCase(OWSConstants.RequestParams.version.name())) {
                    request.setVersion(KvpHelper.checkParameterSingleValue(parameterValues, name));
                    foundVersion = true;
                } else if (name.equalsIgnoreCase(OWSConstants.RequestParams.request.name())) {
                    KvpHelper.checkParameterSingleValue(parameterValues, name);
                    foundRequest = true;
                } else if (name
                        .equalsIgnoreCase(GetDataAvailabilityConstants.GetDataAvailabilityParams.observedProperty
                                .name())) {
                    for (String observedProperty : KvpHelper.checkParameterMultipleValues(parameterValues, name)) {
                        request.addObservedProperty(observedProperty);
                    }
                } else if (name.equalsIgnoreCase(GetDataAvailabilityConstants.GetDataAvailabilityParams.procedure
                        .name())) {
                    for (String procedure : KvpHelper.checkParameterMultipleValues(parameterValues, name)) {
                        request.addProcedure(procedure);
                    }
                } else if (name
                        .equalsIgnoreCase(GetDataAvailabilityConstants.GetDataAvailabilityParams.featureOfInterest
                                .name())) {
                    for (String featureOfInterest : KvpHelper.checkParameterMultipleValues(parameterValues, name)) {
                        request.addFeatureOfInterest(featureOfInterest);
                    }
                } else {
                    exceptions.add(new ParameterNotSupportedException(name));
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

        exceptions.throwIfNotEmpty();

        return request;
    }
}
