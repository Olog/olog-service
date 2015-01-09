package edu.msu.nscl.olog.entity.bitemporal;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.joda.time.Interval;

@StaticMetamodel(BitemporalWrapper.class)
public abstract class BitemporalWrapper_<V> implements Bitemporal{
    public static volatile SingularAttribute<BitemporalWrapper, org.joda.time.Interval> validityInterval;    
    public static volatile SingularAttribute<BitemporalWrapper, org.joda.time.Interval> recordInterval;
}
