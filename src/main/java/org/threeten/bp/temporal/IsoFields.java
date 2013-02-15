/*
 * Copyright (c) 2007-2013, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.threeten.bp.temporal;

import static org.threeten.bp.DayOfWeek.THURSDAY;
import static org.threeten.bp.DayOfWeek.WEDNESDAY;
import static org.threeten.bp.temporal.ChronoField.DAY_OF_WEEK;
import static org.threeten.bp.temporal.ChronoField.DAY_OF_YEAR;
import static org.threeten.bp.temporal.ChronoField.EPOCH_DAY;
import static org.threeten.bp.temporal.ChronoField.MONTH_OF_YEAR;
import static org.threeten.bp.temporal.ChronoField.YEAR;
import static org.threeten.bp.temporal.ChronoUnit.DAYS;
import static org.threeten.bp.temporal.ChronoUnit.FOREVER;
import static org.threeten.bp.temporal.ChronoUnit.MONTHS;
import static org.threeten.bp.temporal.ChronoUnit.WEEKS;
import static org.threeten.bp.temporal.ChronoUnit.YEARS;

import org.threeten.bp.DateTimeException;
import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;
import org.threeten.bp.chrono.Chronology;
import org.threeten.bp.chrono.IsoChronology;
import org.threeten.bp.format.DateTimeBuilder;
import org.threeten.bp.jdk8.Jdk8Methods;

/**
 * Fields and units specific to the ISO-8601 calendar system,
 * including quarter-of-year and week-based-year.
 * <p>
 * This class defines fields and units that are specific to the ISO calendar system.
 *
 * <h3>Quarter of year</h3>
 * The ISO-8601 standard is based on the standard civic 12 month year.
 * This is commonly divided into four quarters, often abbreviated as Q1, Q2, Q3 and Q4.
 * <p>
 * January, February and March are in Q1.
 * April, May and June are in Q2.
 * July, August and September are in Q3.
 * October, November and December are in Q4.
 * <p>
 * The complete date is expressed using three fields:
 * <p><ul>
 * <li>{@link #DAY_OF_QUARTER DAY_OF_QUARTER} - the day within the quarter, from 1 to 90, 91 or 92
 * <li>{@link #QUARTER_OF_YEAR QUARTER_OF_YEAR} - the week within the week-based-year
 * <li>{@link ChronoField#YEAR YEAR} - the standard ISO year
 * </ul><p>
 *
 * <h3>Week based years</h3>
 * The ISO-8601 standard was originally intended as a data interchange format,
 * defining a string format for dates and times. However, it also defines an
 * alternate way of expressing the date, based on the concept of week-based-year.
 * <p>
 * The date is expressed using three fields:
 * <p><ul>
 * <li>{@link ChronoField#DAY_OF_WEEK DAY_OF_WEEK} - the standard field defining the
 *  day-of-week from Monday (1) to Sunday (7)
 * <li>{@link #WEEK_OF_WEEK_BASED_YEAR} - the week within the week-based-year
 * <li>{@link #WEEK_BASED_YEAR WEEK_BASED_YEAR} - the week-based-year
 * </ul><p>
 * The week-based-year itself is defined relative to the standard ISO proleptic year.
 * It differs from the standard year in that it always starts on a Monday.
 * <p>
 * The first week of a week-based-year is the first Monday-based week of the standard
 * ISO year that has at least 4 days in the new year.
 * <p><ul>
 * <li>If January 1st is Monday then week 1 starts on January 1st
 * <li>If January 1st is Tuesday then week 1 starts on December 31st of the previous standard year
 * <li>If January 1st is Wednesday then week 1 starts on December 30th of the previous standard year
 * <li>If January 1st is Thursday then week 1 starts on December 29th of the previous standard year
 * <li>If January 1st is Friday then week 1 starts on January 4th
 * <li>If January 1st is Saturday then week 1 starts on January 3rd
 * <li>If January 1st is Sunday then week 1 starts on January 2nd
 * </ul><p>
 * There are 52 weeks in most week-based years, however on occasion there are 53 weeks.
 * <p>
 * For example:
 * <p>
 * <table cellpadding="0" cellspacing="3" border="0" style="text-align: left; width: 50%;">
 * <caption>Examples of Week based Years</caption>
 * <tr><th>Date</th><th>Day-of-week</th><th>Field values</th></tr>
 * <tr><th>2008-12-28</th><td>Sunday</td><td>Week 52 of week-based-year 2008</td></tr>
 * <tr><th>2008-12-29</th><td>Monday</td><td>Week 1 of week-based-year 2009</td></tr>
 * <tr><th>2008-12-31</th><td>Wednesday</td><td>Week 1 of week-based-year 2009</td></tr>
 * <tr><th>2009-01-01</th><td>Thursday</td><td>Week 1 of week-based-year 2009</td></tr>
 * <tr><th>2009-01-04</th><td>Sunday</td><td>Week 1 of week-based-year 2009</td></tr>
 * <tr><th>2009-01-05</th><td>Monday</td><td>Week 2 of week-based-year 2009</td></tr>
 * </table>
 *
 * <h3>Specification for implementors</h3>
 * <p>
 * This class is immutable and thread-safe.
 */
public final class IsoFields {

    /**
     * The field that represents the day-of-quarter.
     * <p>
     * This field allows the day-of-quarter value to be queried and set.
     * The day-of-quarter has values from 1 to 90 in Q1 of a standard year, from 1 to 91
     * in Q1 of a leap year, from 1 to 91 in Q2 and from 1 to 92 in Q3 and Q4.
     * <p>
     * The day-of-quarter can only be calculated if the day-of-year, month-of-year and year
     * are available.
     * <p>
     * When setting this field, the value is allowed to be partially lenient, taking any
     * value from 1 to 92. If the quarter has less than 92 days, then day 92, and
     * potentially day 91, is in the following quarter.
     * <p>
     * This unit is an immutable and thread-safe singleton.
     */
    public static final TemporalField DAY_OF_QUARTER = Field.DAY_OF_QUARTER;
    /**
     * The field that represents the quarter-of-year.
     * <p>
     * This field allows the quarter-of-year value to be queried and set.
     * The quarter-of-year has values from 1 to 4.
     * <p>
     * The day-of-quarter can only be calculated if the month-of-year is available.
     * <p>
     * This unit is an immutable and thread-safe singleton.
     */
    public static final TemporalField QUARTER_OF_YEAR = Field.QUARTER_OF_YEAR;
    /**
     * The field that represents the week-of-week-based-year.
     * <p>
     * This field allows the week of the week-based-year value to be queried and set.
     * <p>
     * This unit is an immutable and thread-safe singleton.
     */
    public static final TemporalField WEEK_OF_WEEK_BASED_YEAR = Field.WEEK_OF_WEEK_BASED_YEAR;
    /**
     * The field that represents the week-based-year.
     * <p>
     * This field allows the week-based-year value to be queried and set.
     * <p>
     * This unit is an immutable and thread-safe singleton.
     */
    public static final TemporalField WEEK_BASED_YEAR = Field.WEEK_BASED_YEAR;
    /**
     * The unit that represents week-based-years for the purpose of addition and subtraction.
     * <p>
     * This allows a number of week-based-years to be added to, or subtracted from, a date.
     * The unit is equal to either 52 or 53 weeks.
     * The estimated duration of a week-based-year is the same as that of a standard ISO
     * year at {@code 365.2425 Days}.
     * <p>
     * The rules for addition add the number of week-based-years to the existing value
     * for the week-based-year field. If the resulting week-based-year only has 52 weeks,
     * then the date will be in week 1 of the following week-based-year.
     * <p>
     * This unit is an immutable and thread-safe singleton.
     */
    public static final TemporalUnit WEEK_BASED_YEARS = Unit.WEEK_BASED_YEARS;
    /**
     * Unit that represents the concept of a quarter-year.
     * For the ISO calendar system, it is equal to 3 months.
     * The estimated duration of a quarter-year is one quarter of {@code 365.2425 Days}.
     * <p>
     * This unit is an immutable and thread-safe singleton.
     */
    public static final TemporalUnit QUARTER_YEARS = Unit.QUARTER_YEARS;

    /**
     * Restricted constructor.
     */
    private IsoFields() {
        throw new AssertionError("Not instantiable");
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the field.
     */
    private static enum Field implements TemporalField {
        DAY_OF_QUARTER {
            @Override
            public String getName() {
                return "DayOfQuarter";
            }
            @Override
            public TemporalUnit getBaseUnit() {
                return DAYS;
            }
            @Override
            public TemporalUnit getRangeUnit() {
                return QUARTER_YEARS;
            }
            @Override
            public ValueRange range() {
                return ValueRange.of(1, 90, 92);
            }
            @Override
            public boolean doIsSupported(TemporalAccessor temporal) {
                return temporal.isSupported(DAY_OF_YEAR) && temporal.isSupported(MONTH_OF_YEAR) &&
                        temporal.isSupported(YEAR) && Chronology.from(temporal).equals(IsoChronology.INSTANCE);
            }
            @Override
            public ValueRange doRange(TemporalAccessor temporal) {
                if (doIsSupported(temporal) == false) {
                    throw new DateTimeException("Unsupported field: DayOfQuarter");
                }
                long qoy = temporal.getLong(QUARTER_OF_YEAR);
                if (qoy == 1) {
                    long year = temporal.getLong(YEAR);
                    return (IsoChronology.INSTANCE.isLeapYear(year) ? ValueRange.of(1, 91) : ValueRange.of(1, 90));
                } else if (qoy == 2) {
                    return ValueRange.of(1, 91);
                } else if (qoy == 3 || qoy == 4) {
                    return ValueRange.of(1, 92);
                } // else value not from 1 to 4, so drop through
                return range();
            }
            @Override
            public long doGet(TemporalAccessor temporal) {
                if (doIsSupported(temporal) == false) {
                    throw new DateTimeException("Unsupported field: DayOfQuarter");
                }
                int doy = temporal.get(DAY_OF_YEAR);
                int moy = temporal.get(MONTH_OF_YEAR);
                long year = temporal.getLong(YEAR);
                return doy - QUARTER_DAYS[((moy - 1) / 3) + (IsoChronology.INSTANCE.isLeapYear(year) ? 4 : 0)];
            }
            @Override
            public <R extends Temporal> R doWith(R temporal, long newValue) {
                long curValue = doGet(temporal);
                range().checkValidValue(newValue, this);
                return (R) temporal.with(DAY_OF_YEAR, temporal.getLong(DAY_OF_YEAR) + (newValue - curValue));
            }
        },
        QUARTER_OF_YEAR {
            @Override
            public String getName() {
                return "QuarterOfYear";
            }
            @Override
            public TemporalUnit getBaseUnit() {
                return QUARTER_YEARS;
            }
            @Override
            public TemporalUnit getRangeUnit() {
                return YEARS;
            }
            @Override
            public ValueRange range() {
                return ValueRange.of(1, 4);
            }
            @Override
            public boolean doIsSupported(TemporalAccessor temporal) {
                return temporal.isSupported(MONTH_OF_YEAR) && Chronology.from(temporal).equals(IsoChronology.INSTANCE);
            }
            @Override
            public ValueRange doRange(TemporalAccessor temporal) {
                return range();
            }
            @Override
            public long doGet(TemporalAccessor temporal) {
                if (doIsSupported(temporal) == false) {
                    throw new DateTimeException("Unsupported field: DayOfQuarter");
                }
                long moy = temporal.getLong(MONTH_OF_YEAR);
                return ((moy + 2) / 3);
            }
            @Override
            public <R extends Temporal> R doWith(R temporal, long newValue) {
                long curValue = doGet(temporal);
                range().checkValidValue(newValue, this);
                return (R) temporal.with(MONTH_OF_YEAR, temporal.getLong(MONTH_OF_YEAR) + (newValue - curValue) * 3);
            }
            @Override
            public boolean resolve(DateTimeBuilder builder, long value) {
                Long[] values = builder.queryFieldValues(YEAR, QUARTER_OF_YEAR, DAY_OF_QUARTER);
                if (values[0] != null && values[1] != null && values[2] != null) {
                    int y = YEAR.range().checkValidIntValue(values[0], YEAR);
                    int qoy = QUARTER_OF_YEAR.range().checkValidIntValue(values[1], QUARTER_OF_YEAR);
                    int doq = DAY_OF_QUARTER.range().checkValidIntValue(values[2], DAY_OF_QUARTER);
                    LocalDate date = LocalDate.of(y, ((qoy - 1) * 3) + 1, 1).plusDays(doq - 1);
                    builder.addFieldValue(EPOCH_DAY, date.toEpochDay());
                    builder.removeFieldValues(QUARTER_OF_YEAR, DAY_OF_QUARTER);
                }
                return false;
            }
        },
        WEEK_OF_WEEK_BASED_YEAR {
            @Override
            public String getName() {
                return "WeekOfWeekBasedYear";
            }
            @Override
            public TemporalUnit getBaseUnit() {
                return WEEKS;
            }
            @Override
            public TemporalUnit getRangeUnit() {
                return WEEK_BASED_YEARS;
            }
            @Override
            public ValueRange range() {
                return ValueRange.of(1, 52, 53);
            }
            @Override
            public boolean doIsSupported(TemporalAccessor temporal) {
                return temporal.isSupported(EPOCH_DAY);
            }
            @Override
            public ValueRange doRange(TemporalAccessor temporal) {
                return getWeekRange(LocalDate.from(temporal));
            }
            @Override
            public long doGet(TemporalAccessor temporal) {
                return getWeek(LocalDate.from(temporal));
            }
            @Override
            public <R extends Temporal> R doWith(R temporal, long newValue) {
                ValueRange.of(1, 53).checkValidValue(newValue, this);
                return (R) temporal.plus(Jdk8Methods.safeSubtract(newValue, doGet(temporal)), WEEKS);
            }
        },
        WEEK_BASED_YEAR {
            @Override
            public String getName() {
                return "WeekBasedYear";
            }
            @Override
            public TemporalUnit getBaseUnit() {
                return WEEK_BASED_YEARS;
            }
            @Override
            public TemporalUnit getRangeUnit() {
                return FOREVER;
            }
            @Override
            public ValueRange range() {
                return YEAR.range();
            }
            @Override
            public boolean doIsSupported(TemporalAccessor temporal) {
                return temporal.isSupported(EPOCH_DAY);
            }
            @Override
            public ValueRange doRange(TemporalAccessor temporal) {
                return YEAR.range();
            }
            @Override
            public long doGet(TemporalAccessor temporal) {
                return getWeekBasedYear(LocalDate.from(temporal));
            }
            @Override
            public <R extends Temporal> R doWith(R temporal, long newValue) {
                int newVal = range().checkValidIntValue(newValue, WEEK_BASED_YEAR);
                LocalDate date = LocalDate.from(temporal);
                int week = getWeek(date);
                date = date.withDayOfYear(180).withYear(newVal).with(WEEK_OF_WEEK_BASED_YEAR, week);
                return (R) date.with(date);
            }
            @Override
            public boolean resolve(DateTimeBuilder builder, long value) {
                Long[] values = builder.queryFieldValues(WEEK_BASED_YEAR, WEEK_OF_WEEK_BASED_YEAR, DAY_OF_WEEK);
                if (values[0] != null && values[1] != null && values[2] != null) {
                    int wby = WEEK_BASED_YEAR.range().checkValidIntValue(values[0], WEEK_BASED_YEAR);
                    int week = WEEK_OF_WEEK_BASED_YEAR.range().checkValidIntValue(values[1], WEEK_OF_WEEK_BASED_YEAR);
                    int dow = DAY_OF_WEEK.range().checkValidIntValue(values[2], DAY_OF_WEEK);
                    LocalDate date = LocalDate.of(wby, 2, 1).with(WEEK_OF_WEEK_BASED_YEAR, week).with(DAY_OF_WEEK, dow);
                    builder.addFieldValue(EPOCH_DAY, date.toEpochDay());
                    builder.removeFieldValues(WEEK_BASED_YEAR, WEEK_OF_WEEK_BASED_YEAR, DAY_OF_WEEK);
                }
                return false;
            }
        };

        @Override
        public int compare(TemporalAccessor temporal1, TemporalAccessor temporal2) {
            return Long.compare(temporal1.getLong(this), temporal2.getLong(this));
        }

        @Override
        public boolean resolve(DateTimeBuilder builder, long value) {
            return false;
        }

        @Override
        public String toString() {
            return getName();
        }

        //-------------------------------------------------------------------------
        private static final int[] QUARTER_DAYS = {0, 90, 181, 273, 0, 91, 182, 274};

        private static ValueRange getWeekRange(LocalDate date) {
            int wby = getWeekBasedYear(date);
            date = date.withDayOfYear(1).withYear(wby);
            // 53 weeks if standard year starts on Thursday, or Wed in a leap year
            if (date.getDayOfWeek() == THURSDAY || (date.getDayOfWeek() == WEDNESDAY && date.isLeapYear())) {
                return ValueRange.of(1, 53);
            }
            return ValueRange.of(1, 52);
        }

        private static int getWeek(LocalDate date) {
            int dow0 = date.getDayOfWeek().ordinal();
            int doy0 = date.getDayOfYear() - 1;
            int doyThu0 = doy0 + (3 - dow0);  // adjust to mid-week Thursday (which is 3 indexed from zero)
            int alignedWeek = doyThu0 / 7;
            int firstThuDoy0 = doyThu0 - (alignedWeek * 7);
            int firstMonDoy0 = firstThuDoy0 - 3;
            if (firstMonDoy0 < -3) {
                firstMonDoy0 += 7;
            }
            if (doy0 < firstMonDoy0) {
                return (int) getWeekRange(date.withDayOfYear(180).minusYears(1)).getMaximum();
            }
            int week = ((doy0 - firstMonDoy0) / 7) + 1;
            if (week == 53) {
                if ((firstMonDoy0 == -3 || (firstMonDoy0 == -2 && date.isLeapYear())) == false) {
                    week = 1;
                }
            }
            return week;
        }

        private static int getWeekBasedYear(LocalDate date) {
            int year = date.getYear();
            int doy = date.getDayOfYear();
            if (doy <= 3) {
                int dow = date.getDayOfWeek().ordinal();
                if (doy - dow < -2) {
                    year--;
                }
            } else if (doy >= 363) {
                int dow = date.getDayOfWeek().ordinal();
                doy = doy - 363 - (date.isLeapYear() ? 1 : 0);
                if (doy - dow >= 0) {
                    year++;
                }
            }
            return year;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of the period unit.
     */
    private static enum Unit implements TemporalUnit {
        WEEK_BASED_YEARS("WeekBasedYears", Duration.ofSeconds(31556952L)),
        QUARTER_YEARS("QuarterYears", Duration.ofSeconds(31556952L / 4));

        private final String name;
        private final Duration duration;

        private Unit(String name, Duration estimatedDuration) {
            this.name = name;
            this.duration = estimatedDuration;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Duration getDuration() {
            return duration;
        }

        @Override
        public boolean isDurationEstimated() {
            return true;
        }

        @Override
        public boolean isSupported(Temporal temporal) {
            return temporal.isSupported(EPOCH_DAY);
        }

        @Override
        public <R extends Temporal> R doPlus(R temporal, long periodToAdd) {
            switch(this) {
                case WEEK_BASED_YEARS:
                    long added = Jdk8Methods.safeAdd(temporal.get(WEEK_BASED_YEAR), periodToAdd);
                    return (R) temporal.with(WEEK_BASED_YEAR, added);
                case QUARTER_YEARS:
                    // no overflow (256 is multiple of 4)
                    return (R) temporal.plus(periodToAdd / 256, YEARS).plus((periodToAdd % 256) * 3, MONTHS);
                default:
                    throw new IllegalStateException("Unreachable");
            }
        }

        @Override
        public long between(Temporal temporal1, Temporal temporal2) {
            switch(this) {
                case WEEK_BASED_YEARS:
                    return Jdk8Methods.safeSubtract(temporal2.getLong(WEEK_BASED_YEAR), temporal1.getLong(WEEK_BASED_YEAR));
                case QUARTER_YEARS:
                    return temporal1.periodUntil(temporal2, MONTHS) / 3;
                default:
                    throw new IllegalStateException("Unreachable");
            }
        }

        @Override
        public String toString() {
            return getName();

        }
    }
}
