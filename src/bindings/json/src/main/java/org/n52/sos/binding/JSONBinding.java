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
package org.n52.sos.binding;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.sos.coding.OperationKey;
import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.coding.json.JSONUtils;
import org.n52.sos.decode.Decoder;
import org.n52.sos.decode.OperationDecoderKey;
import org.n52.sos.exception.HTTPException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.NoDecoderForKeyException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.util.http.MediaTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public class JSONBinding extends SimpleBinding {
    private static final String URL_PATTERN = "/json";
    private static final Set<String> CONFORMANCE_CLASSES = Collections
            .singleton("http://www.opengis.net/spec/SOS/2.0/conf/json");
    private static final Logger LOG = LoggerFactory.getLogger(JSONBinding.class);

    @Override
    public String getUrlPattern() {
        return URL_PATTERN;
    }

    @Override
    public Set<MediaType> getSupportedEncodings() {
        return Collections.singleton(MediaTypes.APPLICATION_JSON);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
    }

    @Override
    protected boolean isUseHttpResponseCodes() {
        return true;
    }

    @Override
    protected MediaType getDefaultContentType() {
        return MediaTypes.APPLICATION_JSON;
    }

    @Override
    public boolean checkOperationHttpPostSupported(OperationKey k) throws HTTPException {
        return getDecoder(new OperationDecoderKey(k, MediaTypes.APPLICATION_JSON)) != null;
    }

    @Override
    public void doPostOperation(HttpServletRequest req, HttpServletResponse res)
            throws HTTPException, IOException {
        AbstractServiceRequest request = null;
        try {
            request = parseRequest(req);
            checkServiceOperatorKeyTypes(request);
            AbstractServiceResponse response = getServiceOperator(request).receiveRequest(request);
            writeResponse(req, res, response);
        } catch (OwsExceptionReport oer) {
            oer.setVersion(request != null ? request.getVersion() : null);
            writeOwsExceptionReport(req, res, oer);
        }
    }

    private AbstractServiceRequest parseRequest(HttpServletRequest request)
            throws OwsExceptionReport {
        try {
            JsonNode json = JSONUtils.loadReader(request.getReader());
            if (LOG.isDebugEnabled()) {
                LOG.debug("JSON-REQUEST: {}", JSONUtils.print(json));
            }
            OperationDecoderKey key = new OperationDecoderKey(
                    json.path(JSONConstants.SERVICE).textValue(),
                    json.path(JSONConstants.VERSION).textValue(),
                    json.path(JSONConstants.REQUEST).textValue(),
                    MediaTypes.APPLICATION_JSON);
            Decoder<AbstractServiceRequest, JsonNode> decoder =
                    getDecoder(key);
            if (decoder == null) {
                throw new NoDecoderForKeyException(key);
            }
            AbstractServiceRequest sosRequest = decoder.decode(json);
            sosRequest.setRequestContext(getRequestContext(request));
            return sosRequest;
        } catch (IOException ioe) {
            throw new NoApplicableCodeException().causedBy(ioe).withMessage(
                    "Error while reading request! Message: %s", ioe.getMessage());
        }
    }
}
