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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.soap.SoapRequest;
import org.n52.sos.soap.SoapResponse;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public class SoapChain {
    private final HttpServletRequest httpRequest;

    private final HttpServletResponse httpResponse;

    private AbstractServiceRequest bodyRequest;

    private AbstractServiceResponse bodyResponse;

    private SoapRequest soapRequest;

    private SoapResponse soapResponse = new SoapResponse();

    public SoapChain(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
    }

    public AbstractServiceRequest getBodyRequest() {
        return bodyRequest;
    }

    public boolean hasBodyRequest() {
        return getBodyRequest() != null;
    }

    public void setBodyRequest(AbstractServiceRequest bodyRequest) {
        this.bodyRequest = bodyRequest;
    }

    public AbstractServiceResponse getBodyResponse() {
        return bodyResponse;
    }

    public boolean hasBodyResponse() {
        return getBodyResponse() != null;
    }

    public void setBodyResponse(AbstractServiceResponse bodyResponse) {
        this.bodyResponse = bodyResponse;
    }

    public SoapRequest getSoapRequest() {
        return soapRequest;
    }

    public boolean hasSoapRequest() {
        return getSoapRequest() != null;
    }

    public void setSoapRequest(SoapRequest soapRequest) {
        this.soapRequest = soapRequest;
    }

    public SoapResponse getSoapResponse() {
        return soapResponse;
    }

    public boolean hasSoapResponse() {
        return getSoapResponse() != null;
    }

    public void setSoapResponse(SoapResponse soapResponse) {
        this.soapResponse = soapResponse;
    }

    public HttpServletRequest getHttpRequest() {
        return httpRequest;
    }

    public boolean hasHttpRequest() {
        return getHttpRequest() != null;
    }

    public HttpServletResponse getHttpResponse() {
        return httpResponse;
    }

    public boolean hasHttpResponse() {
        return getHttpResponse() != null;
    }
}
