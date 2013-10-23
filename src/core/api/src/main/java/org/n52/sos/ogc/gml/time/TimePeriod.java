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

import java.text.ParseException;
import java.util.Collection;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.n52.sos.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class represents a GML conform timePeriod element.
 * 
 * @since 4.0.0
 */
public class TimePeriod extends Time {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TimePeriod.class);

    /**
     * serial number
     */
    private static final long serialVersionUID = -1784577421975774171L;

    /** start Date of timePeriod */
    private DateTime start;

    /** indeterminate position of startPosition */
    private TimeIndeterminateValue startIndet;

    /** end Date of timePeriod */
    private DateTime end;

    /** indeterminate position of endPosition */
    private TimeIndeterminateValue endIndet;

    /** duration value */
    private Period duration = null; // ISO8601 format

    /** interval value */
    private String interval = null; // ISO8601 format

    /**
     * default constructor
     * 
     */
    public TimePeriod() {
    }

    /**
     * constructor with start and end date as parameters
     * 
     * @param start
     *            start date of the time period
     * @param end
     *            end date of the time period
     */
    public TimePeriod(DateTime start, DateTime end) {
        this.start = start;
        this.end = end;
    }

    /**
     * constructor
     * 
     * @param start
     *            start date of the time period
     * @param end
     *            end date of the time period
     * @param id
     *            GML id
     */
    public TimePeriod(DateTime start, DateTime end, String id) {
        super(id);
        this.start = start;
        this.end = end;
    }

    /**
     * constructor
     * 
     * @param start
     *            start date of the time period
     * @param startIndet
     *            indeterminate time position of start
     * @param end
     *            end date of the time period
     * @param endIndet
     *            indeterminate time value of end position
     */
    public TimePeriod(DateTime start, TimeIndeterminateValue startIndet, DateTime end, TimeIndeterminateValue endIndet) {
        this.start = start;
        this.startIndet = startIndet;
        this.end = end;
        this.endIndet = endIndet;
    }

    /**
     * standard constructor
     * 
     * @param start
     *            timeString of start position in ISO8601 format
     * @param startIndet
     *            indeterminate time position of start
     * @param end
     *            timeString of end position in ISO8601 format
     * @param endIndet
     *            indeterminate time value of end position
     * @param duration
     *            duration in ISO8601 format
     * @throws ParseException
     *             if parsing the time strings of start or end into
     *             java.util.Date failed
     */
    public TimePeriod(DateTime start, TimeIndeterminateValue startIndet, DateTime end,
            TimeIndeterminateValue endIndet, String duration, String id) throws ParseException {
        super(id);
        this.start = start;
        this.startIndet = startIndet;
        this.end = end;
        this.endIndet = endIndet;
        this.duration = ISOPeriodFormat.standard().parsePeriod(duration);
    }

    /**
     * Constructor using TimeInstants
     * 
     * @param startTime
     *            Start TimeInstant
     * @param endTime
     *            End TimeInstant
     * @throws ParseException
     */
    public TimePeriod(TimeInstant startTime, TimeInstant endTime) {
        if (startTime != null) {
            this.start = startTime.getValue();
            this.startIndet = startTime.getIndeterminateValue();
        }
        if (endTime != null) {
            this.end = endTime.getValue();
            this.endIndet = endTime.getIndeterminateValue();
        }
    }

    /**
     * Get duration
     * 
     * @return Returns the duration.
     */
    public Period getDuration() {
        return duration;
    }

    /**
     * Set duration
     * 
     * @param duration
     *            The duration to set.
     */
    public void setDuration(Period duration) {
        this.duration = duration;
    }

    /**
     * Get start time
     * 
     * @return Returns the start.
     */
    public DateTime getStart() {
        return start;
    }

    /**
     * Get start time, resolving indeterminate value if start is null
     * 
     * @return Returns the resolved start time.
     */
    public DateTime resolveStart() {
        return resolveDateTime(start, startIndet);
    }

    /**
     * Set start time
     * 
     * @param start
     *            The start to set.
     */
    public void setStart(DateTime start) {
        this.start = start;
    }

    /**
     * Get end time
     * 
     * @return Returns the end.
     */
    public DateTime getEnd() {
        return end;
    }

    /**
     * Get end time, resolving indeterminate value if start is null
     * 
     * @return Returns the resolved end time.
     */
    public DateTime resolveEnd() {
        return resolveDateTime(end, endIndet);
    }

    /**
     * Set end time
     * 
     * @param end
     *            The end to set.
     */
    public void setEnd(DateTime end) {
        this.end = end;
    }

    /**
     * Get start indet time
     * 
     * @return Returns the startIndet.
     */
    public TimeIndeterminateValue getStartIndet() {
        return startIndet;
    }

    /**
     * Set start indet time
     * 
     * @param startIndet
     *            The startIndet to set.
     */
    public void setStartIndet(TimeIndeterminateValue startIndet) {
        this.startIndet = startIndet;
    }

    /**
     * Get end indet time
     * 
     * @return Returns the endIndet.
     */
    public TimeIndeterminateValue getEndIndet() {
        return endIndet;
    }

    /**
     * Set end indet time
     * 
     * @param endIndet
     *            The endIndet to set.
     */
    public void setEndIndet(TimeIndeterminateValue endIndet) {
        this.endIndet = endIndet;
    }

    /**
     * Get interval
     * 
     * @return Interval string
     */
    public String getInterval() {
        return this.interval;
    }

    /**
     * Set interval
     * 
     * @param interval
     */
    public void setInterval(String interval) {
        this.interval = interval;
    }

    /**
     * Get TimePosition for start.
     * 
     * @return Start TimePosition object
     */
    public TimePosition getStartTimePosition() {
        if (isSetStartIndeterminateValue()) {
            return new TimePosition(getStartIndet());
        } else {
            return new TimePosition(getStart(), getTimeFormat());
        }
    }

    /**
     * Get TimePosition for end.
     * 
     * @return End TimePosition object
     */
    public TimePosition getEndTimePosition() {
        if (isSetEndIndeterminateValue()) {
            return new TimePosition(getEndIndet());
        } else {
            return new TimePosition(getEnd(), getTimeFormat());
        }
    }

    /**
     * Extend TimePeriod to contain Collection<ISosTime>
     * 
     * @param times
     */
    public void extendToContain(Collection<Time> times) {
        for (Time time : times) {
            extendToContain(time);
        }
    }

    /**
     * Extend TimePeriod to contain ISosTime
     * 
     * @param time
     *            To contain {@link Time}
     */
    public void extendToContain(Time time) {
        if (time instanceof TimeInstant) {
            extendToContain((TimeInstant) time);
        } else if (time instanceof TimePeriod) {
            extendToContain((TimePeriod) time);
        } else {
            String errorMsg =
                    String.format("Received ITime type \"%s\" unknown.", time != null ? time.getClass().getName()
                            : time);
            LOGGER.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

    }

    /**
     * Extend TimePeriod to contain another {@link TimePeriod}
     * 
     * @param period
     *            To contain {@link TimePeriod}
     */
    public void extendToContain(TimePeriod period) {
        extendToContain(period.getStart());
        extendToContain(period.getEnd());
        checkTimeFormat(period.getTimeFormat());
    }

    /**
     * Extend TimePeriod to contain {@link TimeInstant}
     * 
     * @param instant
     *            To contain {@link TimeInstant}
     */
    public void extendToContain(TimeInstant instant) {
        if (instant != null) {
            extendToContain(instant.getValue());
            checkTimeFormat(instant.getTimeFormat());
        }
    }

    /**
     * Extend TimePeriod to contain DateTime. Used by other extendToContain
     * methods.
     * 
     * @param time
     */
    public void extendToContain(DateTime time) {
        if (time != null) {
            if (!isSetStart() || time.isBefore(getStart())) {
                setStart(time);
            }
            if (!isSetEnd() || time.isAfter(getEnd())) {
                setEnd(time);
            }
        }
    }

    /**
     * Checks this timeFormat with passed
     * 
     * @param timeFormat
     *            TimeFormat to check with local
     */
    private void checkTimeFormat(TimeFormat timeFormat) {
        if (this.getTimeFormat().equals(TimeFormat.NOT_SET)) {
            this.setTimeFormat(timeFormat);
        } else {
            if (!this.getTimeFormat().equals(timeFormat) && TimeFormat.SUPPORTED_FORAMTS.contains(timeFormat)) {
                this.setTimeFormat(timeFormat);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Time period: ");
        if (isSetStart()) {
            result.append(getStart().toString()).append(Constants.COMMA_SPACE_STRING);
        }
        result.append(getStartIndet()).append(Constants.COMMA_SPACE_STRING);
        if (isSetEnd()) {
            result.append(getEnd().toString()).append(Constants.COMMA_SPACE_STRING);
        }
        result.append(getEndIndet());
        return result.toString();
    }

    @Override
    public int compareTo(Time o) {
        if (o instanceof TimeInstant) {
            TimeInstant ti = (TimeInstant) o;
            if (getEnd().isBefore(ti.getValue())) {
                return -1;
            } else if (getStart().isAfter(ti.getValue())) {
                return 1;
            }
        } else if (o instanceof TimePeriod) {
            TimePeriod tp = (TimePeriod) o;
            if (getStart().isBefore(tp.getStart())) {
                return -1;
            } else if (getEnd().isAfter(tp.getEnd())) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * @return <tt>true</tt>, if start and end are NOT set
     * @see #isSetStart()
     * @see #isSetEnd()
     */
    public boolean isEmpty() {
        return !isSetEnd() && !isSetStart();
    }

    /**
     * @return <tt>true</tt>, if start is set
     * @see #isEmpty()
     * @see #isSetEnd()
     */
    public boolean isSetStart() {
        return getStart() != null;
    }

    /**
     * @return <tt>true</tt>, if start IndeterminateValue is set
     */
    public boolean isSetStartIndeterminateValue() {
        return getStartIndet() != null;
    }

    /**
     * @return <tt>true</tt>, if end IndeterminateValue is set
     */
    public boolean isSetEndIndeterminateValue() {
        return getEndIndet() != null;
    }

    /**
     * @return <tt>true</tt>, if end is set
     * @see #isSetStart()
     * @see #isEmpty()
     */
    public boolean isSetEnd() {
        return getEnd() != null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((getDuration() != null) ? getDuration().hashCode() : 0);
        result = prime * result + ((getEnd() != null) ? getEnd().hashCode() : 0);
        result = prime * result + ((getEndIndet() != null) ? getEndIndet().hashCode() : 0);
        result = prime * result + ((getInterval() != null) ? getInterval().hashCode() : 0);
        result = prime * result + ((getStart() != null) ? getStart().hashCode() : 0);
        result = prime * result + ((getStartIndet() != null) ? getStartIndet().hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TimePeriod)) {
            return false;
        }
        TimePeriod other = (TimePeriod) obj;
        if (getDuration() == null) {
            if (other.getDuration() != null) {
                return false;
            }
        } else if (!getDuration().equals(other.getDuration())) {
            return false;
        }
        if (getEnd() == null) {
            if (other.getEnd() != null) {
                return false;
            }
        } else if (!getEnd().equals(other.getEnd())) {
            return false;
        }
        if (getEndIndet() == null) {
            if (other.getEndIndet() != null) {
                return false;
            }
        } else if (!getEndIndet().equals(other.getEndIndet())) {
            return false;
        }
        if (getInterval() == null) {
            if (other.getInterval() != null) {
                return false;
            }
        } else if (!getInterval().equals(other.getInterval())) {
            return false;
        }
        if (getStart() == null) {
            if (other.getStart() != null) {
                return false;
            }
        } else if (!getStart().equals(other.getStart())) {
            return false;
        }
        if (getStartIndet() == null) {
            if (other.getStartIndet() != null) {
                return false;
            }
        } else if (!getStartIndet().equals(other.getStartIndet())) {
            return false;
        }
        return true;
    }
}
