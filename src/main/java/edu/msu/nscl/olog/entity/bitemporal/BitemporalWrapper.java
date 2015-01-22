/*
 * (c) Copyright Ervacon 2007.
 * All Rights Reserved.
 */
package edu.msu.nscl.olog.entity.bitemporal;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import org.eclipse.persistence.annotations.ReadTransformer;
import org.eclipse.persistence.annotations.Transformation;
import org.eclipse.persistence.annotations.WriteTransformer;
import org.eclipse.persistence.annotations.WriteTransformers;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;
import org.joda.time.Interval;

/**
 * Decorates a value with bitemporal information, making it possible to
 * bitemporally track the value in a {@link BitemporalTrace}. A
 * {@link BitemporalWrapper} allows you to bitemporally track existing value
 * classes, for instance strings. This class implements the {@link Bitemporal}
 * interface for use with JPA / Hibernate.
 *
 * <p>
 *
 * Due to the nature of bitemporality, the wrapped value should be immutable.
 * The value itself will never change, instead new values will be added to the
 * {@link BitemporalTrace} to represent changes of the value. A
 * {@link BitemporalWrapper} itself is not immutable, its record interval can be
 * {@link #end() ended}.
 *
 * <p>
 *
 * Instances of this class are serializable if the wrapped value is
 * serializable.
 *
 * <p>
 *
 * Objects of this class are not thread-safe.
 *
 * @param <V> Value tracked by {@link BitemporalTrace}.
 *
 * @see Bitemporal
 * @see BitemporalTrace
 *
 * @author Erwin Vervaet
 * @author Christophe Vanfleteren
 * @author igor.mihalik
 */
@MappedSuperclass
public abstract class BitemporalWrapper<V> implements Bitemporal, Serializable {
    
    @Transformation(fetch = FetchType.LAZY)
    @ReadTransformer(method = "readValidityInterval")
    @WriteTransformers({
        @WriteTransformer(method = "writeValidityIntervalStart", column = @Column(name = "validityinterval_0")),
        @WriteTransformer(method = "writeValidityIntervalEnd", column = @Column(name = "validityinterval"))
    })
    private Interval validityInterval;
    
    @Transformation(fetch = FetchType.LAZY)
    @ReadTransformer(method = "readRecordInterval")
    @WriteTransformers({
        @WriteTransformer(method = "writeRecordIntervalStart", column = @Column(name = "recordinterval_0")),
        @WriteTransformer(method = "writeRecordIntervalEnd", column = @Column(name = "recordinterval"))
    })
    private Interval recordInterval;

    
    private Interval readValidityInterval(Record row, Session session) {
        /**
         * This conversion allows for the database type not to match, i.e. may
         * be a Timestamp or String.
         */
        Timestamp start = (Timestamp) session.getDatasourcePlatform().convertObject(row.get("validityinterval_0"), java.sql.Timestamp.class);
        Timestamp end = (Timestamp) session.getDatasourcePlatform().convertObject(row.get("validityinterval"), java.sql.Timestamp.class);
        return new Interval(start.getTime(), end.getTime());
    }

    private java.sql.Timestamp writeValidityIntervalStart() {
        if(validityInterval==null) return null;
        return new java.sql.Timestamp(validityInterval.getStartMillis());
    }

    private java.sql.Timestamp writeValidityIntervalEnd() {
        if(validityInterval==null) return null;
        return new java.sql.Timestamp(validityInterval.getEndMillis());
    }

    
    private Interval readRecordInterval(Record row, Session session) {
        /**
         * This conversion allows for the database type not to match, i.e. may
         * be a Timestamp or String.
         */
        Timestamp start = (Timestamp) session.getDatasourcePlatform().convertObject(row.get("recordinterval_0"), java.sql.Timestamp.class);
        Timestamp end = (Timestamp) session.getDatasourcePlatform().convertObject(row.get("recordinterval"), java.sql.Timestamp.class);
        return new Interval(start.getTime(), end.getTime());
    }

    private java.sql.Timestamp writeRecordIntervalStart() {
        if(recordInterval==null) return null;
        return new java.sql.Timestamp(recordInterval.getStartMillis());
    }

    private java.sql.Timestamp writeRecordIntervalEnd() {
        if(recordInterval==null) return null;
        return new java.sql.Timestamp(recordInterval.getEndMillis());
    }

    protected BitemporalWrapper() {
        // default constructor required
    }

    /**
     * Bitemporally wrap the given value. Validity will be as specified, and the
     * recording interval will be {@link TimeUtils#fromNow() from now on}.
     *
     * @param value The value to wrap (can be <tt>null</tt>).
     * @param validityInterval Validity of the value.
     */
    public BitemporalWrapper(V value, Interval validityInterval) {
        if (validityInterval == null) {
            throw new IllegalArgumentException("The validity interval is required");
        }
        this.validityInterval = validityInterval;
        this.recordInterval = TimeUtils.fromNow();
        setValue(value);
    }

    /**
     * Set the wrapped value, possibly <tt>null</tt>.
     */
    protected abstract void setValue(V value);

    /**
     * Returns the wrapped value, possibly <tt>null</tt>.
     */
    public abstract V getValue();

    /**
     * @see com.anasoft.os.daofusion.bitemporal.Bitemporal#getValidityInterval()
     */
    public Interval getValidityInterval() {
        return validityInterval;
    }

    /**
     * Set the validity interval for the wrapped value.
     */
    protected void setValidityInterval(Interval validityInterval) {
        this.validityInterval = validityInterval;
    }

    /**
     * Set the validity interval for the wrapped value.
     */
    protected void setRecordInterval(Interval recordInterval) {
        this.recordInterval = recordInterval;
    }

    /**
     * @see com.anasoft.os.daofusion.bitemporal.Bitemporal#getRecordInterval()
     */
    public Interval getRecordInterval() {
        return recordInterval;
    }

    /**
     * @see com.anasoft.os.daofusion.bitemporal.Bitemporal#end()
     */
    public void end() {
        this.recordInterval = TimeUtils.interval(getRecordInterval().getStart(), TimeUtils.now());
    }

    @Override
    public String toString() {
        return getValidityInterval() + "  ~  " + getRecordInterval() + "  ~  "
                + String.valueOf(getValue());
    }

}
