/*
 * (c) Copyright Ervacon 2007.
 * All Rights Reserved.
 */

package edu.msu.nscl.olog.bitemporal.control;

import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * Static utilities dealing with <i>time</i>.
 * 
 * <p>
 * 
 * This class controls the record time using a {@link ReferenceTimeProvider}.
 * You can override the default {@link ThreadLocalReferenceProvider}
 * implementation by calling the {@link #setReferenceProvider(ReferenceTimeProvider)
 * setReferenceProvider} method upon application startup.
 * 
 * @see ReferenceTimeProvider
 * 
 * @author Erwin Vervaet
 * @author Christophe Vanfleteren
 * @author michal.jemala
 * @author vojtech.szocs
 */
public final class TimeUtils {

        private TimeUtils() {
            // no need to instantiate this class
        }


        // time framing functionality

        private static ReferenceTimeProvider referenceProvider = new ThreadLocalReferenceProvider();

        /**
         * Overrides the default {@link ReferenceTimeProvider} implementation.
         */
        public static synchronized void setReferenceProvider(ReferenceTimeProvider provider) {
            TimeUtils.referenceProvider = provider;
        }

        /**
         * Determines whether or not a reference time has been set.
         */
        public static boolean isReferenceSet() {
                return referenceProvider.getReference() != null;
        }

        /**
         * Returns the reference time, or <i>wallclock now</i> if no reference
         * time has been set.
         */
        public static DateTime reference() {
                return isReferenceSet() ? referenceProvider.getReference() : current();
        }

        /**
         * Set the reference time to the specified value.
         */
        public static void setReference(DateTime dateTime) {
                referenceProvider.setReference(dateTime);
        }

        /**
         * Clears the reference time.
         */
        public static void clearReference() {
                referenceProvider.clearReference();
        }


        // general purpose date/time related utilities

    /**
     * Timestamp value that represents N/A date.
     */
    public static final long ACTUAL_END_OF_TIME = 38742530400000L;  // 14.9.3197
        public static final long END_OF_TIME = ACTUAL_END_OF_TIME - 1;
        private static final DateTime endOfTime = new DateTime(END_OF_TIME);

        /**
         * Create a {@link DateTime} object representing given day of given month
         * in given year.
         */
        public static DateTime day(int day, int month, int year) {
                return new DateTime(year, month, day, 0, 0, 0, 0);
        }

        /**
         * Returns the current reference time. If a reference time is set, it is that
         * reference time that will be returned.
         * 
         * @see #reference()
         */
        public static DateTime now() {
                return reference();
        }

        /**
         * Returns the current local time (<i>wallclock now</i>), ignoring the reference time.
         */
        public static DateTime current() {
                return new DateTime();
        }

        /**
         * Returns a {@link DateTime} object representing the end of time.
         */
        public static DateTime endOfTime() {
                return endOfTime;
        }

        /**
         * Returns a interval running for the specified period. The returned interval
         * is half-open: it includes the start time, but not the end time.
         */
        public static Interval interval(DateTime start, DateTime end) {
                return new Interval(start.getMillis(), end.getMillis());
        }

        /**
         * Returns an interval running from given start time till the end of time.
         * 
         * @see #endOfTime()
         */
        public static Interval from(DateTime start) {
                return interval(start, new DateTime(ACTUAL_END_OF_TIME));
        }

        /**
         * Returns an interval running from now till the end of time.
         */
        public static Interval fromNow() {
                return from(now());
        }

}
