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

import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.ISOPeriodFormat;
import org.n52.sos.exception.ows.concrete.DateTimeException;
import org.n52.sos.exception.ows.concrete.DateTimeFormatException;
import org.n52.sos.exception.ows.concrete.DateTimeParseException;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.Time.TimeFormat;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Utility class for Time formatting and parsing. Uses Joda Time.
 * 
 * @since 4.0.0
 * 
 */
public final class DateTimeHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateTimeHelper.class);

    /**
     * response format for time
     */
    private static String responseFormat;

    private static final String YMD_RESPONSE_FORMAT = "yyyy-MM-dd";

    private static final String YM_RESPONSE_FORMAT = "yyyy-MM";

    private static final String Y_RESPONSE_FORMAT = "yyyy";

    private static final int YEAR = 4;

    private static final int YEAR_MONTH = 7;

    private static final int YEAR_MONTH_DAY = 10;

    private static final int YEAR_MONTH_DAY_HOUR = 13;

    private static final int YEAR_MONTH_DAY_HOUR_MINUTE = 16;

    private static final int YEAR_MONTH_DAY_HOUR_MINUTE_SECOND = 19;

    private static final int ONE_VALUE = 1;

    private static final String Z = "Z";

    private static final String UTC_OFFSET = "+00:00";

    /**
     * lease value
     */
    private static int lease;

    /**
     * Parses a time String to a Joda Time DateTime object
     * 
     * @param timeString
     *            Time String
     * @return DateTime object
     * @throws DateTimeException
     *             If an error occurs.
     */
    public static DateTime parseIsoString2DateTime(final String timeString) throws DateTimeParseException {
        checkForValidity(timeString);
        if (Strings.isNullOrEmpty(timeString)) {
            return null;
        }
        try {
            if (timeString.contains("+") || Pattern.matches("-\\d", timeString) || timeString.contains(Z)
                    || timeString.contains("z")) {
                return ISODateTimeFormat.dateOptionalTimeParser().withOffsetParsed().parseDateTime(timeString);
            } else {
                return ISODateTimeFormat.dateOptionalTimeParser().withZone(DateTimeZone.UTC).parseDateTime(timeString);
            }
        } catch (final RuntimeException uoe) {
            throw new DateTimeParseException(timeString, uoe);
        }
    }

    /**
     * Parses the given ISO 8601 String to a {@link Time} including
     * {@link TimeInstant} and {@link TimePeriod}
     * 
     * @param timeString
     *            a ISO 8601 formatted time string
     * @return a Time object
     * @throws DateTimeParseException
     *             If an error occurs.
     */
    public static Time parseIsoString2DateTime2Time(final String timeString) throws DateTimeParseException {
        if (timeString.contains(Constants.SLASH_STRING)) {
            final String[] subTokens = timeString.split(Constants.SLASH_STRING);
            return new TimePeriod(parseIsoString2DateTime(subTokens[0]), parseIsoString2DateTime(subTokens[1]));
        } else {
            return new TimeInstant(parseIsoString2DateTime(timeString));
        }
    }

    private static void checkForValidity(final String timeString) throws DateTimeParseException {
        if (!(timeString.length() == YEAR || timeString.length() == YEAR_MONTH || timeString.length() >= YEAR_MONTH_DAY)) {
            throw new DateTimeParseException(timeString);
        }
    }

    /**
     * Formats the given Time to ISO 8601 string.
     * 
     * @param time
     *            an {@link Time} object to be formatted
     * @return an ISO 8601 conform {@link String}.
     * @throws IllegalArgumentException
     *             in the case of receiving <tt>null</tt> or not supported
     *             types.
     * @see #formatDateTime2IsoString(DateTime)
     */
    public static String format(final Time time) {
        if (time != null) {
            if (time instanceof TimeInstant) {
                return formatDateTime2IsoString(((TimeInstant) time).getValue());
            } else if (time instanceof TimePeriod) {
                return String.format("%s/%s", formatDateTime2IsoString(((TimePeriod) time).getStart()),
                        formatDateTime2IsoString(((TimePeriod) time).getEnd()));
            }
        }
        final String exceptionMsg = String.format("Given Time object is not valid: %s", time);
        LOGGER.debug(exceptionMsg);
        throw new IllegalArgumentException(exceptionMsg);
    }

    /**
     * Formats a DateTime to a ISO-8601 String
     * 
     * @param dateTime
     *            Time object
     * @return ISO-8601 formatted time String
     */
    public static String formatDateTime2IsoString(final DateTime dateTime) {
        if (dateTime == null) {
            return getZeroUtcDateTime().toString().replace(Z, UTC_OFFSET);
        }
        return dateTime.toString();
    }

    /**
     * Formats a DateTime to a String using the response format
     * 
     * @param dateTime
     *            Time object
     * @return Response formatted time String
     * 
     * @throws DateTimeFormatException
     *             If an error occurs.
     */
    public static String formatDateTime2ResponseString(final DateTime dateTime) throws DateTimeFormatException {
        return formatDateTime2FormattedString(dateTime, responseFormat);
    }

    public static String formatDateTime2String(final DateTime dateTime, final TimeFormat timeFormat)
            throws DateTimeFormatException {
        switch (timeFormat) {
        case Y:
            return formatDateTime2YearDateString(dateTime);
        case YM:
            return formatDateTime2YearMonthDateString(dateTime);
        case YMD:
            return formatDateTime2YearMonthDayDateStringYMD(dateTime);
        default:
            return formatDateTime2ResponseString(dateTime);
        }
    }

    /**
     * Formats a DateTime to a String using specified format
     * 
     * @param dateTime
     *            Time object
     * @param dateFormat
     *            the date time format
     * 
     * @return Specified formatted time String
     * 
     * @throws DateTimeFormatException
     *             If an error occurs.
     */
    public static String formatDateTime2FormattedString(final DateTime dateTime, final String dateFormat)
            throws DateTimeFormatException {
        try {
            if (Strings.isNullOrEmpty(dateFormat)) {
                return formatDateTime2IsoString(dateTime);
            } else {
                if (dateTime == null) {
                    return getZeroUtcDateTime().toString(DateTimeFormat.forPattern(dateFormat));
                }
                return dateTime.toString(DateTimeFormat.forPattern(dateFormat)).replace(Z, UTC_OFFSET);
            }
        } catch (final IllegalArgumentException iae) {
            throw new DateTimeFormatException(dateTime, iae);
        }
    }

    /**
     * formats a DateTime to a string with year-month-day.
     * 
     * @param dateTime
     *            The DateTime.
     * @return Returns formatted time String.
     * 
     * @throws DateTimeFormatException
     */
    public static String formatDateTime2YearMonthDayDateStringYMD(final DateTime dateTime)
            throws DateTimeFormatException {
        try {
            DateTime result = checkAndGetDateTimeWithZoneUtc(dateTime);
            return result.toString(DateTimeFormat.forPattern(YMD_RESPONSE_FORMAT));
        } catch (final IllegalArgumentException iae) {
            throw new DateTimeFormatException(dateTime, iae);
        }
    }

    /**
     * formats a DateTime to a string with year-month.
     * 
     * @param dateTime
     *            The DateTime.
     * @return Returns formatted time String.
     * 
     * @throws DateTimeFormatException
     */
    public static String formatDateTime2YearMonthDateString(final DateTime dateTime) throws DateTimeFormatException {
        try {
            DateTime result = checkAndGetDateTimeWithZoneUtc(dateTime);
            return result.toString(DateTimeFormat.forPattern(YM_RESPONSE_FORMAT));
        } catch (final IllegalArgumentException iae) {
            throw new DateTimeFormatException(dateTime, iae);
        }
    }

    /**
     * formats a DateTime to a string with year.
     * 
     * @param dateTime
     *            The DateTime.
     * @return Returns formatted time String.
     * 
     * @throws DateTimeFormatException
     */
    public static String formatDateTime2YearDateString(final DateTime dateTime) throws DateTimeFormatException {
        try {
            DateTime result = checkAndGetDateTimeWithZoneUtc(dateTime);
            return result.toString(DateTimeFormat.forPattern(Y_RESPONSE_FORMAT));
        } catch (final IllegalArgumentException iae) {
            throw new DateTimeFormatException(dateTime, iae);
        }
    }

    private static DateTime checkAndGetDateTimeWithZoneUtc(final DateTime dateTime) {
        if (dateTime == null) {
            return getZeroUtcDateTime();
        } else {
            return new DateTime(dateTime.getMillis(), DateTimeZone.UTC);
        }
    }

    private static DateTime getZeroUtcDateTime() {
        return new DateTime(0000, 01, 01, 00, 00, 00, 000, DateTimeZone.UTC);
    }

    /**
     * Set the time object to the end values (seconds, minutes, hours, days,..)
     * if the time Object has not all values
     * 
     * @param dateTime
     *            Time object
     * @param isoTimeLength
     *            Length of the time object
     * @return Modified time object.
     */
    public static DateTime setDateTime2EndOfDay4RequestedEndPosition(final DateTime dateTime, final int isoTimeLength) {
        switch (isoTimeLength) {
        // year
        case YEAR:
            return dateTime.plusYears(ONE_VALUE).minusMillis(ONE_VALUE);
            // year, month
        case YEAR_MONTH:
            return dateTime.plusMonths(ONE_VALUE).minusMillis(ONE_VALUE);
            // year, month, day
        case YEAR_MONTH_DAY:
            return dateTime.plusDays(ONE_VALUE).minusMillis(ONE_VALUE);
            // year, month, day, hour
        case YEAR_MONTH_DAY_HOUR:
            return dateTime.plusHours(ONE_VALUE).minusMillis(ONE_VALUE);
            // year, month, day, hour, minute
        case YEAR_MONTH_DAY_HOUR_MINUTE:
            return dateTime.plusMinutes(ONE_VALUE).minusMillis(ONE_VALUE);
            // year, month, day, hour, minute, second
        case YEAR_MONTH_DAY_HOUR_MINUTE_SECOND:
            return dateTime.plusSeconds(ONE_VALUE).minusMillis(ONE_VALUE);
        default:
            return dateTime;
        }
    }

    /**
     * Parse a duration from a String representation
     * 
     * @param stringDuration
     *            Duration as String
     * @return Period object of duration
     */
    public static Period parseDuration(final String stringDuration) {
        return ISOPeriodFormat.standard().parsePeriod(stringDuration);
    }

    /**
     * Calculates the expire time for a time object
     * 
     * @param start
     *            Time object
     * @return Expire time
     */
    public static DateTime calculateExpiresDateTime(final DateTime start) {
        return start.plusMinutes(lease);
    }

    /**
     * Set the response format
     * 
     * @param responseFormat
     *            Defined response format
     */
    public static void setResponseFormat(final String responseFormat) {
        DateTimeHelper.responseFormat = responseFormat;
    }

    /**
     * Set the lease value
     * 
     * @param lease
     *            Defined lease value
     */
    public static void setLease(final int lease) {
        DateTimeHelper.lease = lease;
    }

    /**
     * Hide utility constructor
     */
    private DateTimeHelper() {
    }
}
