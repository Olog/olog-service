package edu.msu.nscl.olog.entity;

import javax.persistence.metamodel.*;

@StaticMetamodel(Log.class)
public abstract class Log_
{
    public static volatile SingularAttribute<Log, java.lang.Long> id;
    public static volatile SingularAttribute<Log, java.lang.String> version;
    public static volatile SingularAttribute<Log, java.lang.String> owner;
    public static volatile SingularAttribute<Log, java.lang.String> source;
    public static volatile SingularAttribute<Log, edu.msu.nscl.olog.entity.Level> level;
    public static volatile SingularAttribute<Log, edu.msu.nscl.olog.entity.State> state;
    public static volatile SingularAttribute<Log, java.util.Date> modifiedDate;
    public static volatile SingularAttribute<Log, java.lang.String> description;
    public static volatile SetAttribute<Log, edu.msu.nscl.olog.entity.LogAttribute> attributes;
    public static volatile SetAttribute<Log, edu.msu.nscl.olog.entity.Logbook> logbooks;
    public static volatile SetAttribute<Log, edu.msu.nscl.olog.entity.Tag> tags;
    public static volatile SingularAttribute<Log, edu.msu.nscl.olog.entity.Entry> entry;
}
