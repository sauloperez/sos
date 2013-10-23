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

package org.n52.sos.util;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.http.HTTPStatus;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class HasStatusCode extends TypeSafeMatcher<OwsExceptionReport> {

    private final HTTPStatus status;

    public HasStatusCode(HTTPStatus status) {
        this.status = status;
    }

    @Override
    protected boolean matchesSafely(OwsExceptionReport item) {
        return item.getStatus() != null && item.getStatus() == status;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("has status code ").appendValue(status.getCode());
    }

    @Factory
    public static HasStatusCode hasStatusCode(HTTPStatus code) {
        return new HasStatusCode(code);
    }
}
