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
package org.n52.sos.ds.hibernate;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.n52.sos.cache.WritableCache;
import org.n52.sos.cache.WritableContentCache;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class SosCacheFeederDAOTest extends HibernateTestCase {
    /* FIXTURES */
    private SosCacheFeederDAO instance;

    @Before
    public void initCacheFeeder() {
        instance = new SosCacheFeederDAO();
    }

    @Test
    public void updateCacheFillsCapabilitiesCache() throws OwsExceptionReport {
        WritableContentCache cache = new WritableCache();
        instance.updateCache(cache);
        testCacheResult(cache);
    }

    @Test(expected = NullPointerException.class)
    public void updateNullThrowsNullPointerException() throws OwsExceptionReport {
        instance.updateCache(null);
    }

    /* HELPER */
    private void testCacheResult(WritableContentCache cache) {
        assertNotNull("cache is null", cache);
        assertNotNull("envelope of features is null", cache.getGlobalEnvelope());
        assertNotNull("feature types is null", cache.getFeatureOfInterestTypes());
        assertNotNull("offerings is null", cache.getOfferings());
        // assertNotNull("max phenomenon time is null",
        // cache.getMaxPhenomenonTime());
        // assertNotNull("min phenomenon time is null",
        // cache.getMinPhenomenonTime());
        // assertNotNull("max result time is null", cache.getMaxResultTime());
        // assertNotNull("min result time is null", cache.getMinResultTime());
        assertNotNull("observation types is null", cache.getObservationTypes());
        assertNotNull("result templates is null", cache.getResultTemplates());
    }
}
