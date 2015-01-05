package edu.msu.nscl.olog.entity;

import javax.persistence.metamodel.*;

@StaticMetamodel(Entry.class)
public abstract class Entry_
{
    public static volatile SingularAttribute<Entry, java.lang.Long> id;
    public static volatile SingularAttribute<Entry, java.util.Date> createdDate;
    public static volatile CollectionAttribute<Entry, edu.msu.nscl.olog.entity.BitemporalLog> logs;
}
