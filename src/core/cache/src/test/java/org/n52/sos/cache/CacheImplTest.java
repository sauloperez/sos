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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.n52.sos.ogc.sos.SosEnvelope;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 * 
 */
public class CacheImplTest {
    private static WritableCache instance;

    @Before
    public void initInstance() {
        instance = new WritableCache();
    }

    @After
    public void resetInstance() {
        instance = null;
    }

    @Test
    public void defaultConstructorReturnsObject() {
        initInstance();
        assertNotNull("instance is null", instance);
        assertTrue("right class", instance instanceof ReadableCache);
    }

    @Test
    public void equalsWithNewInstances() {
        ReadableCache anotherInstance = new ReadableCache();
        assertEquals("equals failed", instance, anotherInstance);
    }

    @Test
    public void equalsWithSelf() {
        assertEquals("I am not equal with me", instance, instance);
    }

    @Test
    public void equalsWithNull() {
        assertNotEquals("equal with null", instance, null);
    }

    @Test
    public void equalWithOtherClass() {
        assertNotEquals("equal with Object", instance, new Object());
    }

    @Test
    public void should_return_different_hashCodes_for_different_instances() {
        WritableCache cache = new WritableCache();
        cache.setObservationIdentifiers(Collections.singleton("o_1"));
        assertNotEquals("hashCode() of different caches are equal", cache.hashCode(), new ReadableCache());
    }

    @Test
    public void should_return_empty_global_envelope_when_setEnvelope_is_called_with_null_parameter() {
        instance.setGlobalEnvelope(null);
        final SosEnvelope emptySosEnvelope = new SosEnvelope(null, instance.getDefaultEPSGCode());

        assertThat(instance.getGlobalEnvelope(), not(nullValue()));
        assertThat(instance.getGlobalEnvelope(), is(emptySosEnvelope));
    }
}
