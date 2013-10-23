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
package org.n52.sos.cache;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.n52.sos.cache.Existing.existing;

import org.junit.Test;
import org.n52.sos.cache.ctrl.ContentCacheControllerImpl;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class PersistingCacheControllerTest extends AbstractCacheControllerTest {
    public static final String IDENTIFIER = "identifier";

    @Test
    public void testSerialization() {
        assertThat(getTempFile(), is(not(existing())));
        ContentCacheControllerImpl cc = new TestableInMemoryCacheController();
        assertThat(cc.getCache().getObservationIdentifiers(), is(empty()));
        cc.getCache().addObservationIdentifier(IDENTIFIER);
        assertThat(cc.getCache().getObservationIdentifiers(), contains(IDENTIFIER));
        cc.cleanup();
        assertThat(getTempFile(), is(existing()));
        cc = new TestableInMemoryCacheController();
        assertThat(getTempFile(), is(not(existing())));
        assertThat(cc.getCache().getObservationIdentifiers(), contains(IDENTIFIER));
    }

}
