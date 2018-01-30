package edu.msu.nscl.olog.bitemporal.control;

import org.joda.time.DateTime;

/**
 * {@link ReferenceTimeProvider} which holds the reference
 * time in a thread-local variable.
 * 
 * <p>
 * 
 * If you need to deal with the reference time in a multi-threaded
 * way, you should use a custom {@link ReferenceTimeProvider}
 * implementation.
 * 
 * @see ReferenceTimeProvider
 * 
 * @author michal.jemala
 * @author vojtech.szocs
 */
public class ThreadLocalReferenceProvider implements ReferenceTimeProvider {

    private static final ThreadLocal<DateTime> reference = new ThreadLocal<DateTime>();
    
    /**
     * @see com.anasoft.os.daofusion.bitemporal.ReferenceTimeProvider#getReference()
     */
    public DateTime getReference() {
        return reference.get();
    }
    
    /**
     * @see com.anasoft.os.daofusion.bitemporal.ReferenceTimeProvider#setReference(org.joda.time.DateTime)
     */
    public void setReference(DateTime dateTime) {
        reference.set(dateTime);
    }
    
    /**
     * @see com.anasoft.os.daofusion.bitemporal.ReferenceTimeProvider#clearReference()
     */
    public void clearReference() {
        reference.remove();
    }
    
}