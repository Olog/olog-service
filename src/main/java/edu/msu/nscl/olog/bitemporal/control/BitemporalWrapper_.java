package edu.msu.nscl.olog.bitemporal.control;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.joda.time.Interval;

@StaticMetamodel(BitemporalWrapper.class)
public abstract class BitemporalWrapper_<V> implements Bitemporal{
    public static volatile SingularAttribute<BitemporalWrapper, java.util.Date> validityStart;
    public static volatile SingularAttribute<BitemporalWrapper, java.util.Date> validityEnd;  
    public static volatile SingularAttribute<BitemporalWrapper, java.util.Date> recordStart;
    public static volatile SingularAttribute<BitemporalWrapper, java.util.Date> recordEnd;
}
