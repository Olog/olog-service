/*
 * (c) Copyright Ervacon 2007.
 * All Rights Reserved.
 */

package edu.msu.nscl.olog.entity.bitemporal;

import java.io.Serializable;

import org.joda.time.Interval;

/**
 * Simple strategy to access the value in a {@link Bitemporal} object.
 * 
 * @param <V> Value wrapped by the {@link Bitemporal} object.
 * @param <T> {@link Bitemporal} object implementation that wraps the given value type.
 * 
 * @see Bitemporal
 * 
 * @author Erwin Vervaet
 * @author Christophe Vanfleteren
 */
public interface ValueAccessor<V, T extends Bitemporal> extends Serializable {

        /**
         * Extract the value from the given {@link Bitemporal} object.
         */
        V extractValue(T bitemporalObject);

        /**
         * Create a {@link Bitemporal} object wrapping given value,
         * valid for the specified interval.
         */
        T wrapValue(V value, Interval validityInterval);

}
