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
package org.n52.sos.service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.sos.binding.Binding;
import org.n52.sos.binding.BindingRepository;
import org.n52.sos.event.SosEventBus;
import org.n52.sos.event.events.ExceptionEvent;
import org.n52.sos.exception.HTTPException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.http.HTTPHeaders;
import org.n52.sos.util.http.HTTPMethods;
import org.n52.sos.util.http.HTTPStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The servlet of the SOS which receives the incoming HttpPost and HttpGet
 * requests and sends the operation result documents to the client TODO review
 * exception handling
 * 
 * @since 4.0.0
 */
public class SosService extends ConfiguratedHttpServlet {
    private static final long serialVersionUID = -2103692310137045855L;

    private static final Logger LOGGER = LoggerFactory.getLogger(SosService.class);

    public static final String BINDING_DELETE_METHOD = "doDeleteOperation";

    public static final String BINDING_PUT_METHOD = "doPutOperation";

    public static final String BINDING_POST_METHOD = "doPostOperation";

    public static final String BINDING_GET_METHOD = "doGetOperation";

    @Override
    public void init() throws ServletException {
        LOGGER.info("SOS endpoint initalized successfully!");
    }

    protected HttpServletRequest logRequest(HttpServletRequest request) {
        if (LOGGER.isDebugEnabled()) {
            Enumeration<?> headerNames = request.getHeaderNames();
            StringBuilder headers = new StringBuilder();
            while (headerNames.hasMoreElements()) {
                String name = (String) headerNames.nextElement();
                headers.append("> ").append(name).append(": ").append(request.getHeader(name)).append("\n");
            }
            LOGGER.debug("Incoming request:\n> [{} {} {}] from {} {}\n{}", request.getMethod(),
                    request.getRequestURI(), request.getProtocol(), request.getRemoteAddr(), request.getRemoteHost(),
                    headers);
        }
        return request;
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        logRequest(request);
        try {
            getBinding(request).doDeleteOperation(request, response);
        } catch (HTTPException exception) {
            onHttpException(request, response, exception);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        logRequest(request);
        try {
            getBinding(request).doGetOperation(request, response);
        } catch (HTTPException exception) {
            onHttpException(request, response, exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        logRequest(request);
        try {
            getBinding(request).doPostOperation(request, response);
        } catch (HTTPException exception) {
            onHttpException(request, response, exception);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        logRequest(request);
        try {
            getBinding(request).doPutOperation(request, response);
        } catch (HTTPException exception) {
            onHttpException(request, response, exception);
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        logRequest(request);
        Binding binding = null;
        try {

            binding = getBinding(request);
            binding.doOptionsOperation(request, response);
        } catch (HTTPException exception) {
            if (exception.getStatus() == HTTPStatus.METHOD_NOT_ALLOWED) {
                if (binding != null) {
                    doDefaultOptions(binding, request, response);
                } else {
                    super.doOptions(request, response);
                }
            } else {
                onHttpException(request, response, exception);
            }
        }
    }

    /**
     * Get the implementation of {@link Binding} that is registered for the
     * given <code>request</code>.
     * 
     * @param request
     *            URL pattern from request URL
     * 
     * @return The implementation of {@link Binding} that is registered for the
     *         given <code>urlPattern</code>.
     * 
     * 
     * @throws OwsExceptionReport
     *             * If the URL pattern is not supported by this SOS.
     */
    private Binding getBinding(HttpServletRequest request) throws HTTPException {
        String requestURI = request.getPathInfo();
        if (requestURI == null) {
            throw new HTTPException(HTTPStatus.BAD_REQUEST);
        }
        for (String prefix : BindingRepository.getInstance().getBindings().keySet()) {
            if (requestURI.startsWith(prefix)) {
                return BindingRepository.getInstance().getBinding(prefix);
            }
        }
        throw new HTTPException(HTTPStatus.NOT_FOUND);
    }

    protected void onHttpException(HttpServletRequest request, HttpServletResponse response, HTTPException exception)
            throws IOException {
        SosEventBus.fire(new ExceptionEvent(exception));
        response.sendError(exception.getStatus().getCode(), exception.getMessage());
    }

    protected void doDefaultOptions(Binding binding, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        Set<String> methods = getDeclaredBindingMethods(binding.getClass());
        StringBuilder allow = new StringBuilder();
        if (methods.contains(BINDING_GET_METHOD)) {
            allow.append(HTTPMethods.GET);
            allow.append(", ");
            allow.append(HTTPMethods.HEAD);
        }
        if (methods.contains(BINDING_POST_METHOD)) {
            if (allow.length() != 0) {
                allow.append(", ");
            }
            allow.append(HTTPMethods.POST);
        }
        if (methods.contains(BINDING_PUT_METHOD)) {
            if (allow.length() != 0) {
                allow.append(", ");
            }
            allow.append(HTTPMethods.PUT);
        }
        if (methods.contains(BINDING_DELETE_METHOD)) {
            if (allow.length() != 0) {
                allow.append(", ");
            }
            allow.append(HTTPMethods.DELETE);
        }

        if (allow.length() != 0) {
            allow.append(", ");
        }
        allow.append(HTTPMethods.TRACE);
        allow.append(", ");
        allow.append(HTTPMethods.OPTIONS);
        response.setHeader(HTTPHeaders.ALLOW, allow.toString());
    }

    private Set<String> getDeclaredBindingMethods(Class<?> c) {
        if (c.equals(Binding.class)) {
            return new HashSet<String>();
        } else {
            Set<String> parent = getDeclaredBindingMethods(c.getSuperclass());
            for (Method m : c.getDeclaredMethods()) {
                parent.add(m.getName());
            }
            return parent;
        }
    }
}
