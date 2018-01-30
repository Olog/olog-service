package edu.msu.nscl.olog.entity;

import javax.persistence.metamodel.*;

@StaticMetamodel(Tag.class)
public abstract class Tag_
{
    public static volatile SingularAttribute<Tag, java.lang.Long> id;
    public static volatile SingularAttribute<Tag, java.lang.String> name;
    public static volatile SingularAttribute<Tag, edu.msu.nscl.olog.entity.State> state;
    public static volatile SetAttribute<Tag, edu.msu.nscl.olog.entity.Log> logs;
}
