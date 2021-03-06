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

import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static org.n52.sos.ext.deleteobservation.DeleteObservationConstants.CONFORMANCE_CLASSES;
import static org.n52.sos.ext.deleteobservation.DeleteObservationConstants.NS_SOSDO_1_0;
import static org.n52.sos.ogc.sos.SosConstants.SOS;
import static org.n52.sos.util.CodingHelper.decoderKeysForElements;
import static org.n52.sos.util.CodingHelper.xmlDecoderKeysForOperation;
import static org.n52.sos.util.CollectionHelper.union;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import net.opengis.sosdo.x10.DeleteObservationDocument;
import net.opengis.sosdo.x10.DeleteObservationType;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.decode.Decoder;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 1.0.0
 */
public class DeleteObservationDecoder implements Decoder<DeleteObservationRequest, XmlObject> {

    @SuppressWarnings("unchecked")
    private static final Set<DecoderKey> DECODER_KEYS = union(
            decoderKeysForElements(NS_SOSDO_1_0, DeleteObservationDocument.class),
            xmlDecoderKeysForOperation(SOS, Sos2Constants.SERVICEVERSION,
                    DeleteObservationConstants.Operations.DeleteObservation));

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteObservationDecoder.class);

    public DeleteObservationDecoder() {
        LOGGER.info("Decoder for the following keys initialized successfully: {}!", Joiner.on(", ").join(DECODER_KEYS));
    }

    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    public DeleteObservationRequest decode(XmlObject xmlObject) throws OwsExceptionReport {
        LOGGER.debug(format("REQUESTTYPE: %s", xmlObject != null ? xmlObject.getClass() : "null recevied"));
        // XmlHelper.validateDocument(xmlObject);
        if (xmlObject instanceof DeleteObservationDocument) {
            DeleteObservationDocument delObsDoc = (DeleteObservationDocument) xmlObject;
            DeleteObservationRequest decodedRequest = parseDeleteObservation(delObsDoc);
            LOGGER.debug(String.format("Decoded request: %s", decodedRequest));
            return decodedRequest;
        } else {
            throw new UnsupportedDecoderInputException(this, xmlObject);
        }
    }

    private DeleteObservationRequest parseDeleteObservation(DeleteObservationDocument xbDelObsDoc)
            throws OwsExceptionReport {
        DeleteObservationRequest delObsRequest = null;

        DeleteObservationType xbDelObsType = xbDelObsDoc.getDeleteObservation();

        if (xbDelObsType != null) {
            delObsRequest = new DeleteObservationRequest();
            delObsRequest.setVersion(xbDelObsType.getVersion());
            delObsRequest.setService(xbDelObsType.getService());
            delObsRequest.setObservationIdentifier(xbDelObsType.getObservation());
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("Received XML document is not valid. Set log level to debug to get more details");
        }

        return delObsRequest;
    }

    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return emptyMap();
    }

    public Set<String> getConformanceClasses() {
        return CONFORMANCE_CLASSES;
    }

}
