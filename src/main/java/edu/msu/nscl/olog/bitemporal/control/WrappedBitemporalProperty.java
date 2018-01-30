/*
 * (c) Copyright Ervacon 2007.
 * All Rights Reserved.
 */

package edu.msu.nscl.olog.bitemporal.control;

import java.util.Collection;

/**
 * {@link BitemporalProperty} implementation that uses {@link BitemporalWrapper}
 * as the {@link Bitemporal} object type.
 * 
 * @param <V> Value wrapped by the {@link BitemporalWrapper}.
 * @param <T> {@link BitemporalWrapper} implementation that wraps the given value type.
 * 
 * @see BitemporalProperty
 * 
 * @author Erwin Vervaet
 * @author Christophe Vanfleteren
 */
public class WrappedBitemporalProperty<V, T extends BitemporalWrapper<V>> extends BitemporalProperty<V, T> {

    /**
     * Create a new bitemporal property on top of given data collection and using
     * the given value accessor.
     */
    public WrappedBitemporalProperty(Collection<? extends Bitemporal> data, ValueAccessor<V,T> valueAccessor) {
        super(data, valueAccessor);
    }

}
