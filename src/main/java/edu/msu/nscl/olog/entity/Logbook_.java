package edu.msu.nscl.olog.entity;

import javax.persistence.metamodel.*;

@StaticMetamodel(Logbook.class)
public abstract class Logbook_
{
    public static volatile SingularAttribute<Logbook, java.lang.Long> id;
    public static volatile SingularAttribute<Logbook, java.lang.String> name;
    public static volatile SingularAttribute<Logbook, java.lang.String> owner;
    public static volatile SingularAttribute<Logbook, edu.msu.nscl.olog.entity.State> state;
    public static volatile SetAttribute<Logbook, edu.msu.nscl.olog.entity.Log> logs;
}
