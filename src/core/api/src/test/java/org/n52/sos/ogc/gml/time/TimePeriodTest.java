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
package org.n52.sos.ogc.gml.time;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.junit.Test;
import org.n52.sos.ogc.gml.time.Time.TimeIndeterminateValue;
import org.n52.sos.ogc.sos.SosConstants.SosIndeterminateTime;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a> TODO test extent to methods!!!
 * 
 * @since 4.0.0
 */
public class TimePeriodTest {

    @Test
    public void isEmptyForDefaultConstructorTest() {
        assertTrue("new Timeperiod is NOT empty", new TimePeriod().isEmpty());
    }

    @Test
    public void isEmptyForConstructorWithNullStartAndEndTimeTest() {
        assertTrue("new TimePeriod(null, null) is NOT empty",
                new TimePeriod((DateTime) null, (DateTime) null).isEmpty());
    }

    @Test
    public void isEmptyForConstructorWithAllNullTest() {
        assertTrue("new TimePeriod(null, null, null) is NOT empty", new TimePeriod(null, null, null).isEmpty());
    }

    @Test
    public void isEmptyForConstructorWithNullStartAndEndTimeAndGmlIdTest() {
        assertTrue("new TimePeriod(null, null, \"gmlId\") is NOT empty", new TimePeriod(null, null, "gmlId").isEmpty());
    }

    @Test
    public void isEmptyForConstructorWithStartTimeAndNullEndTimeTest() {
        assertFalse("new TimePeriod(new DateTime(), null) is empty", new TimePeriod(new DateTime(), null).isEmpty());
    }

    @Test
    public void isEmptyForConstructorWithNullStartTimeAndEndTimeTest() {
        assertFalse("new TimePeriod(null, ew DateTime()) is empty", new TimePeriod(null, new DateTime()).isEmpty());
    }

    @Test
    public void isEmptyForConstructorWithNullStartAndEndTimeInstantTest() {
        assertTrue("new TimePeriod(null, null) is NOT empty",
                new TimePeriod((TimeInstant) null, (TimeInstant) null).isEmpty());
    }

    @Test
    public void isSetStartTest() {
        assertTrue("new TimePeriod(new DateTime(),null).isSetStart() == false",
                new TimePeriod(new DateTime(), null).isSetStart());
    }

    @Test
    public void isSetEndTest() {
        assertTrue("new TimePeriod(null,new DateTime()).isSetEnd() == false",
                new TimePeriod(null, new DateTime()).isSetEnd());
    }

    @Test
    public void isSetStartTestTimeInstant() {
        assertTrue("new TimePeriod(new DateTime(),null).isSetStart() == false", new TimePeriod(new TimeInstant(
                new DateTime()), null).isSetStart());
    }

    @Test
    public void isSetEndTestTimeInstant() {
        assertTrue("new TimePeriod(null,new DateTime()).isSetEnd() == false", new TimePeriod(null, new TimeInstant(
                new DateTime())).isSetEnd());
    }

    @Test
    public void emptyTimePeriodExtendedByTimeInstantShouldHaveTheSameValueForStartAndEnd() {
        TimePeriod timePeriod = new TimePeriod();

        timePeriod.extendToContain(new TimeInstant(new DateTime()));

        assertFalse("TimePeriod is emtpy after extending", timePeriod.isEmpty());
        assertTrue("Start value not set", timePeriod.isSetStart());
        assertTrue("End value not set", timePeriod.isSetEnd());
    }

    @Test
    public void shouldRemoveReferencPrefixForGetGmlIdTest() {
        TimePeriod timePeriod = new TimePeriod();
        timePeriod.setGmlId("#test");
        assertTrue("GmlId starts with '#' for getGmlId()", !timePeriod.getGmlId().startsWith("#"));
    }

    @Test
    public void isReferencedTest() {
        TimePeriod timePeriod = new TimePeriod();
        timePeriod.setGmlId("#test");
        assertTrue("TimePeriod is NOT referenced", timePeriod.isReferenced());
        timePeriod.setGmlId("test");
        assertFalse("TimePeriod is referenced", timePeriod.isReferenced());
    }

    @Test
    public void testIndeterminateNowStart() {
        TimePeriod timePeriod = new TimePeriod(null, TimeIndeterminateValue.now, new DateTime(), null);
        DateTime beforeAccess = new DateTime();
        DateTime nowValue = timePeriod.resolveStart();
        assertNotNull("TimePeriod start now value is null", nowValue);
        assertTrue("TimePeriod start now value is too early",
                nowValue.isAfter(beforeAccess) || nowValue.isEqual(beforeAccess));
        assertTrue("TimePeriod start now value is too late", nowValue.isBeforeNow() || nowValue.isEqualNow());
    }

    @Test
    public void testIndeterminateNowEnd() {
        TimePeriod timePeriod = new TimePeriod(new DateTime(), null, null, TimeIndeterminateValue.now);
        DateTime beforeAccess = new DateTime();
        DateTime nowValue = timePeriod.resolveEnd();
        assertNotNull("TimePeriod end now value is null", nowValue);
        assertTrue("TimePeriod end now value is too early",
                nowValue.isAfter(beforeAccess) || nowValue.isEqual(beforeAccess));
        assertTrue("TimePeriod end now value is too late", nowValue.isBeforeNow() || nowValue.isEqualNow());
    }

}
