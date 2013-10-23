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

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.coding.OperationKey;
import org.n52.sos.decode.Decoder;
import org.n52.sos.exception.HTTPException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.ConformanceClasses;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.util.http.MediaTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 *
 */
public class PoxBinding extends SimpleBinding {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(PoxBinding.class);
    private static final Set<String> CONFORMANCE_CLASSES = Collections
            .singleton(ConformanceClasses.SOS_V2_POX_BINDING);

    @Override
    public void doPostOperation(HttpServletRequest req,
                                HttpServletResponse res)
            throws HTTPException, IOException {
        AbstractServiceRequest sosRequest = null;
        try {
            sosRequest = parseRequest(req);
            AbstractServiceResponse sosResponse = getServiceOperator(sosRequest)
                    .receiveRequest(sosRequest);
            writeResponse(req, res, sosResponse);
        } catch (OwsExceptionReport oer) {
            oer.setVersion(sosRequest != null ? sosRequest.getVersion() : null);
            writeOwsExceptionReport(req, res, oer);
        }
    }

    protected AbstractServiceRequest parseRequest(HttpServletRequest request)
            throws OwsExceptionReport {
        XmlObject doc = XmlHelper.parseXmlSosRequest(request);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("XML-REQUEST: {}", doc.xmlText());
        }
        Decoder<AbstractServiceRequest, XmlObject> decoder =
                getDecoder(CodingHelper.getDecoderKey(doc));
        return decoder.decode(doc).setRequestContext(getRequestContext(request));
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
    }

    @Override
    public String getUrlPattern() {
        return BindingConstants.POX_BINDING_ENDPOINT;
    }

    @Override
    public boolean checkOperationHttpPostSupported(OperationKey k) {
        return hasDecoder(k, MediaTypes.TEXT_XML) ||
               hasDecoder(k, MediaTypes.APPLICATION_XML);
    }

    @Override
    public Set<MediaType> getSupportedEncodings() {
        return Sets.newHashSet(MediaTypes.TEXT_XML, MediaTypes.APPLICATION_XML);
    }

    @Override
    protected MediaType getDefaultContentType() {
        return MediaTypes.TEXT_XML;
    }
}
