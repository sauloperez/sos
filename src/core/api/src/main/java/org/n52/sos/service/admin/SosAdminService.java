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
package org.n52.sos.service.admin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.XmlEncoderKey;
import org.n52.sos.exception.AdministratorException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.response.ServiceResponse;
import org.n52.sos.service.ConfiguratedHttpServlet;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.util.http.HTTPHeaders;
import org.n52.sos.util.http.HTTPUtils;
import org.n52.sos.util.http.MediaTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The servlet of the SOS administration backend which receives the incoming
 * HttpGet requests and sends the operation result documents to the client
 * 
 * @since 4.0.0
 */
public class SosAdminService extends ConfiguratedHttpServlet {
    private static final long serialVersionUID = -3279981432309569992L;

    private static final Logger LOGGER = LoggerFactory.getLogger(SosAdminService.class);

    /**
     * initializes the Servlet
     */
    @Override
    public void init() throws ServletException {
        super.init();
        LOGGER.info("Admin endpoint initalized successfully!");
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        LOGGER.debug("\n**********\n(GET) Connected from: " + req.getRemoteAddr() + " " + req.getRemoteHost());
        LOGGER.trace("Query String: " + req.getQueryString());

        this.setCorsHeaders(resp);

        ServiceResponse sosResp = null;
        try {
            sosResp = Configurator.getInstance().getAdminServiceOperator().doGetOperation(req);
        } catch (AdministratorException e) {
            handleException(new NoApplicableCodeException().withMessage("Error").causedBy(e));
        } catch (OwsExceptionReport e) {
            sosResp = handleException(e);
        }
        HTTPUtils.writeObject(req, resp, sosResp);
    }

    /**
     * Handles OPTIONS request to enable Cross-Origin Resource Sharing.
     * 
     * @param req
     *            the incoming request
     * 
     * @param resp
     *            the response for the incoming request
     */
    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doOptions(req, resp);
        this.setCorsHeaders(resp);
    }

    /**
     * Set HTTPHeaders according to CORS to enable Cross-Domain JavaScript
     * access.
     * 
     * @see <a href="http://www.w3.org/TR/cors/">http://www.w3.org/TR/cors/</a>
     */
    private void setCorsHeaders(HttpServletResponse resp) {
        resp.addHeader(HTTPHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        resp.addHeader(HTTPHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, OPTIONS");
        resp.addHeader(HTTPHeaders.ACCESS_CONTROL_ALLOW_HEADERS, HTTPHeaders.CONTENT_TYPE);
    }

    private ServiceResponse handleException(OwsExceptionReport owsExceptionReport) throws ServletException {
        try {

            EncoderKey key = new XmlEncoderKey(owsExceptionReport.getNamespace(), owsExceptionReport.getClass());
            Encoder<?, OwsExceptionReport> encoder = CodingRepository.getInstance().getEncoder(key);
            if (encoder != null) {
                Object encodedObject = encoder.encode(owsExceptionReport);

                if (encodedObject instanceof XmlObject) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    XmlObject xml = (XmlObject) encodedObject;
                    xml.save(baos, XmlOptionsHelper.getInstance().getXmlOptions());
                    baos.flush();
                    return new ServiceResponse(baos, MediaTypes.APPLICATION_XML);
                } else if (encodedObject instanceof ServiceResponse) {
                    return (ServiceResponse) encodedObject;
                } else {
                    throw logExceptionAndCreateServletException(null);
                }
            } else {
                throw logExceptionAndCreateServletException(null);
            }
        } catch (Exception owse) {
            throw logExceptionAndCreateServletException(owse);
        }
    }

    private ServletException logExceptionAndCreateServletException(Exception e) {
        String exceptionText = "Error while encoding exception response!";
        if (e != null) {
            LOGGER.debug(exceptionText, e);
        } else {
            LOGGER.debug(exceptionText);
        }
        return new ServletException(exceptionText);
    }
}
