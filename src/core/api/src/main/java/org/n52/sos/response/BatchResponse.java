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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.response.BatchResponse.ExceptionOrResponse;
import org.n52.sos.util.BatchConstants;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class BatchResponse extends AbstractServiceResponse implements Iterable<ExceptionOrResponse> {
    private final List<ExceptionOrResponse> responses;

    public BatchResponse(List<ExceptionOrResponse> responses) {
        this.responses = checkNotNull(responses);
    }

    public BatchResponse() {
        this(new LinkedList<ExceptionOrResponse>());
    }

    private static <T> T checkNotNull(T t) {
        if (t == null) {
            throw new NullPointerException();
        }
        return t;
    }

    @Override
    public String getOperationName() {
        return BatchConstants.OPERATION_NAME;
    }

    public List<ExceptionOrResponse> getResponses() {
        return Collections.unmodifiableList(responses);
    }

    public void add(OwsExceptionReport e) {
        this.responses.add(new ExceptionOrResponse(e));
    }

    public void add(AbstractServiceResponse r) {
        this.responses.add(new ExceptionOrResponse(r));
    }

    public void add(ExceptionOrResponse eor) {
        this.responses.add(checkNotNull(eor));
    }

    public boolean isEmpty() {
        return getResponses().isEmpty();
    }

    @Override
    public Iterator<ExceptionOrResponse> iterator() {
        return getResponses().iterator();
    }

    public static class ExceptionOrResponse {
        private final OwsExceptionReport exception;

        private final AbstractServiceResponse response;

        private ExceptionOrResponse(OwsExceptionReport exception, AbstractServiceResponse response) {
            this.exception = exception;
            this.response = response;
        }

        public ExceptionOrResponse(AbstractServiceResponse response) {
            this(null, checkNotNull(response));
        }

        public ExceptionOrResponse(OwsExceptionReport exception) {
            this(checkNotNull(exception), null);
        }

        public boolean isException() {
            return exception != null;
        }

        public OwsExceptionReport getException() {
            return exception;
        }

        public AbstractServiceResponse getResponse() {
            return response;
        }
    }
}
