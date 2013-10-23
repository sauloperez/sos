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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.n52.sos.util.net.IPAddress;

/**
 * @since 4.0.0
 * 
 */
public class RequestContextTest {

    @Test
    public void shouldEmpty() {
        RequestContext rc = new RequestContext();
        assertThat(rc.getIPAddress().isPresent(), is(false));
        assertThat(rc.getToken().isPresent(), is(false));
    }

    @Test
    public void shouldNotEmptyTokenSet() {
        RequestContext rc = new RequestContext();
        rc.setToken("asfsf");
        assertThat(rc.getIPAddress().isPresent(), is(false));
        assertThat(rc.getToken().isPresent(), is(true));
    }

    @Test
    public void shouldNotEmptyIpSet() {
        RequestContext rc = new RequestContext();
        rc.setIPAddress(new IPAddress("192.168.1.1"));
        assertThat(rc.getIPAddress().isPresent(), is(true));
    }

    @Test
    public void shouldNotEmptyIpAndTokenSet() {
        RequestContext rc = new RequestContext();
        rc.setIPAddress(new IPAddress("192.168.1.1"));
        rc.setToken("asfsf");
        assertThat(rc.getIPAddress().isPresent(), is(true));
        assertThat(rc.getToken().isPresent(), is(true));
    }

}
