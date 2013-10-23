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
package org.n52.sos.encode.sos.v2;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.ObservationEncoder;
import org.n52.sos.encode.XmlEncoderKey;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.InvalidResponseFormatParameterException;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.response.AbstractObservationResponse;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public abstract class AbstractObservationResponseEncoder<T extends AbstractObservationResponse> extends
        AbstractSosResponseEncoder<T> {
    public AbstractObservationResponseEncoder(String operation, Class<T> responseType) {
        super(operation, responseType);
    }

    /**
     * Finds a O&Mv2 compatible {@link ObservationEncoder}
     * 
     * @param responseFormat
     *            the response format
     * 
     * @return the encoder or {@code null} if none is found
     * 
     * @throws OwsExceptionReport
     *             if the found encoder is not a {@linkplain ObservationEncoder}
     */
    private ObservationEncoder<XmlObject, OmObservation> findObservationEncoder(String responseFormat)
            throws OwsExceptionReport {
        Encoder<XmlObject, OmObservation> encoder =
                CodingRepository.getInstance().getEncoder(new XmlEncoderKey(responseFormat, OmObservation.class));
        if (encoder == null) {
            return null;
        } else if (encoder instanceof ObservationEncoder) {
            ObservationEncoder<XmlObject, OmObservation> oe = (ObservationEncoder<XmlObject, OmObservation>) encoder;
            return oe.isObservationAndMeasurmentV20Type() ? oe : null;
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("Error while encoding response, encoder is not of type ObservationEncoder!");
        }
    }

    /**
     * Finds a compatible response encoder to delegate to.
     * 
     * @param responseFormat
     *            the response format
     * 
     * @return the encoder or {@code null} if no encoder was found
     */
    private Encoder<XmlObject, T> findResponseEncoder(String responseFormat) {
        return CodingRepository.getInstance().getEncoder(new XmlEncoderKey(responseFormat, getResponseType()));
    }

    @Override
    protected XmlObject create(T response) throws OwsExceptionReport {
        final String responseFormat = response.getResponseFormat();
        // search for an O&M2 encoder for this response format
        ObservationEncoder<XmlObject, OmObservation> encoder = findObservationEncoder(responseFormat);
        if (encoder != null) {
            // encode the response as a GetObservationResponseDocument
            return createResponse(encoder, response);
        }
        // there is no O&M2 compatible observation encoder:
        // search for a encoder for the response and delegate
        Encoder<XmlObject, T> responseEncoder = findResponseEncoder(responseFormat);
        if (encoder != null) {
            return responseEncoder.encode(response);
        } else {
            // unsupported responseFormat
            throw new InvalidResponseFormatParameterException(responseFormat);
        }
    }

    /**
     * Create a response using the provided O&M2 compatible observation encoder.
     * 
     * @param encoder
     *            the encoder
     * @param response
     *            the response
     * 
     * @return the encoded response
     * 
     * @throws OwsExceptionReport
     *             if an error occurs
     */
    protected abstract XmlObject createResponse(ObservationEncoder<XmlObject, OmObservation> encoder, T response)
            throws OwsExceptionReport;
}
