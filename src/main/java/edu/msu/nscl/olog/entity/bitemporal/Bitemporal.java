/*
 * (c) Copyright Ervacon 2007.
 * All Rights Reserved.
 */

package edu.msu.nscl.olog.entity.bitemporal;

import org.joda.time.Interval;

/**
 * A bitemporal object. A bitemporal object is essentially a value tracked in two time dimensions:
 * 
 * <ul>
 *      <li><i>Validity time</i> - indicates when the value was valid.
 *      <li><i>Recording time</i> - indicates when the value was known.
 * </ul>
 * 
 * <p>
 * 
 * Tracking information bitemporally allows you to aswer questions along the lines of
 * "On January 2, 1999, what did we think the valid value was for September 1, 1980?".
 * 
 * <p>
 * 
 * In most cases, application level should not directly implement this interface, but should
 * instead wrap existing value classes bitemporally using a {@link BitemporalWrapper}.
 * 
 * @see BitemporalTrace
 * @see BitemporalWrapper
 * 
 * @author Erwin Vervaet
 * @author Christophe Vanfleteren
 */
public interface Bitemporal {

        /**
         * Returns the interval in which this bitemporal object is valid.
         */
        public Interval getValidityInterval();

        /**
         * Returns the interval in which this bitemporal object is known.
         */
        public Interval getRecordInterval();

        /**
         * End the recording interval of this bitemporal object, indicating that it has been superseded by a new object,
         * or is deemed as no longer relevant (i.e. because it was faulty knowledge) and should be "forgotten".
         */
        public void end();

        /**
         * Create and return a new bitemporal object representing the same value as this object, but with specified
         * validity. The recording interval of the returned object will always be {@link TimeUtils#fromNow() from now on}.
         * 
         * @param validityInterval Validity interval of the new object.
         * @return New bitemporal object with the given validity and its value retained from its original.
         */
        public Bitemporal copyWith(Interval validityInterval);

}
