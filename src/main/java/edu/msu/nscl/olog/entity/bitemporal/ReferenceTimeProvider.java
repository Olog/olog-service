package edu.msu.nscl.olog.entity.bitemporal;

import org.joda.time.DateTime;

/**
 * Strategy for dealing with the reference time.
 * 
 * <p>
 * 
 * You can override the default {@link ReferenceTimeProvider}
 * implementation {@link TimeUtils} works with by calling the
 * {@link TimeUtils#setReferenceProvider(ReferenceTimeProvider)
 * setReferenceProvider} method.
 * 
 * @see TimeUtils
 * 
 * @author michal.jemala
 * @author vojtech.szocs
 */
public interface ReferenceTimeProvider {

    /**
     * Returns the reference time, possibly <tt>null</tt>.
     */
    DateTime getReference();
    
    /**
     * Sets the reference time to the specified value.
     */
    void setReference(DateTime dateTime);
    
    /**
     * Clears the reference time. After clearing, {@link #getReference()
     * getReference} should return <tt>null</tt> to indicate the reference
     * time is no longer being set.
     */
    void clearReference();
    
}