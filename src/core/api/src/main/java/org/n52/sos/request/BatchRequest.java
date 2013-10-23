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
package org.n52.sos.request;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.n52.sos.util.BatchConstants;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class BatchRequest extends AbstractServiceRequest implements Iterable<AbstractServiceRequest> {
    private final List<AbstractServiceRequest> requests;

    private boolean stopAtFailure = false;

    public BatchRequest(List<AbstractServiceRequest> requests) {
        this.requests = checkNotNull(requests);
    }

    public BatchRequest() {
        this(new LinkedList<AbstractServiceRequest>());
    }

    @Override
    public String getOperationName() {
        return BatchConstants.OPERATION_NAME;
    }

    public List<AbstractServiceRequest> getRequests() {
        return Collections.unmodifiableList(requests);
    }

    public void add(AbstractServiceRequest request) {
        this.requests.add(checkNotNull(request));
    }

    public boolean isEmpty() {
        return getRequests().isEmpty();
    }

    @Override
    public Iterator<AbstractServiceRequest> iterator() {
        return getRequests().iterator();
    }

    private static <T> T checkNotNull(T t) {
        if (t == null) {
            throw new NullPointerException();
        }
        return t;
    }

    public boolean isStopAtFailure() {
        return stopAtFailure;
    }

    public void setStopAtFailure(boolean stopAtFailure) {
        this.stopAtFailure = stopAtFailure;
    }
}
