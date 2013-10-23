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
package org.n52.sos.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.n52.sos.service.CommunicationObjectWithSoapHeader;
import org.n52.sos.service.SoapHeader;
import org.n52.sos.util.http.HTTPStatus;
import org.n52.sos.util.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * TODO add generic field for headers<br />
 * v0.1 with enumeration and set of default headers<br />
 * private Map<HeaderCode, String> httpHeaders;<br />
 * could be extended in future versions
 * 
 * @since 4.0.0
 */
public class ServiceResponse implements CommunicationObjectWithSoapHeader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceResponse.class);

    /**
     * output stream of document
     */
    private final ByteArrayOutputStream byteArrayOutputStream;

    /**
     * the HTTP response code as specified in {@link HttpServletResponse}
     */
    private HTTPStatus status;

    /**
     * the content type of this response
     */
    private MediaType contentType;

    /**
     * the header field and values to be set in the {@link HttpServletResponse}
     */
    private final Map<String, String> headerMap = Maps.newHashMap();

    private final Map<String, SoapHeader> soapHeaderMap = Maps.newHashMap();

    /**
     * constructor with content and response code
     * 
     * @param baos
     *            Output stream of the SOS response
     * @param contentType
     *            Content type
     * @param status
     *            the HTTP response code as specified in
     *            {@link HttpServletResponse}
     */
    public ServiceResponse(ByteArrayOutputStream baos, MediaType contentType, HTTPStatus status) {
        this.byteArrayOutputStream = baos;
        this.contentType = contentType;
        this.status = status;
    }

    /**
     * constructor with content but not specified response code
     * 
     * @param baos
     *            Output stream of the SOS response
     * @param contentType
     *            Content type
     */
    public ServiceResponse(ByteArrayOutputStream baos, MediaType contentType) {
        this(baos, contentType, null);
    }

    /**
     * constructor without content type but with specified response code
     * 
     * @param contentType
     *            Content type
     * @param status
     *            the HTTP response code as specified in
     *            {@link HttpServletResponse}
     */
    public ServiceResponse(MediaType contentType, HTTPStatus status) {
        this(null, contentType, status);
    }

    /**
     * @return Returns the content type of this response
     */
    public MediaType getContentType() {
        return contentType;
    }

    /**
     * @return <b>true</b> if as minimum one header value is contained in the
     *         map
     */
    public boolean isSetHeaderMap() {
        return !headerMap.isEmpty();
    }

    public void setHeader(String headerIdentifier, String headerValue) {
        headerMap.put(headerIdentifier, headerValue);
    }

    public Map<String, String> getHeaderMap() {
        return Collections.unmodifiableMap(headerMap);
    }

    /**
     * @param outputStream
     *            The stream the content of this response is written to
     * 
     * @see #isContentLess()
     */
    public void writeToOutputStream(OutputStream outputStream) {
        if (byteArrayOutputStream == null) {
            LOGGER.error("no response to write to.");
            return;
        }
        try {

            byteArrayOutputStream.writeTo(outputStream);
            byteArrayOutputStream.flush();

        } catch (IOException ioe) {
            LOGGER.error("doResponse", ioe);
        } finally {
            try {
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }

            } catch (IOException ioe) {
                LOGGER.error("doSoapResponse, close streams", ioe);
            }
        }
    }

    /**
     * @return the content of this response as <code>byte[]</code>, or<br />
     *         <code>null</code>, if this response is content less.
     * 
     * @see #isContentLess()
     */
    public byte[] getByteArray() {
        if (byteArrayOutputStream != null) {
            return byteArrayOutputStream.toByteArray();
        }
        return null;
    }

    /**
     * Check, if this response contains content to be written.
     * 
     * @return <code>true</code>, if content is <b>NOT</b> available,<br />
     *         else <code>false</code>, if content is available
     * 
     * @see #writeToOutputStream(OutputStream).
     */
    public boolean isContentLess() {
        return byteArrayOutputStream == null;
    }

    /**
     * @return the status code
     */
    public HTTPStatus getStatus() {
        return status != null ? status : isContentLess() ? HTTPStatus.NO_CONTENT : HTTPStatus.OK;
    }

    public void setStatus(HTTPStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format(
                "ServiceResponse [byteArrayOutputStream=%s, httpResponseCode=%s, contentType=%s, headerMap=%s]",
                byteArrayOutputStream, status, contentType, headerMap);
    }

    @Override
    public Map<String, SoapHeader> getSoapHeader() {
        return Collections.unmodifiableMap(this.soapHeaderMap);
    }

    @Override
    public void setSoapHeader(Map<String, SoapHeader> header) {
        this.soapHeaderMap.clear();
        if (header != null) {
            this.soapHeaderMap.putAll(header);
        }
    }

    @Override
    public boolean isSetSoapHeader() {
        return !this.soapHeaderMap.isEmpty();
    }
}
