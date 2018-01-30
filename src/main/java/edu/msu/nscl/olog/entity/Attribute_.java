package edu.msu.nscl.olog.entity;

import javax.persistence.metamodel.*;

@StaticMetamodel(Attribute.class)
public abstract class Attribute_
{
    public static volatile SingularAttribute<Attribute, java.lang.Long> id;
    public static volatile SingularAttribute<Attribute, java.lang.String> name;
    public static volatile SingularAttribute<Attribute, edu.msu.nscl.olog.entity.State> state;
    public static volatile SetAttribute<Attribute, edu.msu.nscl.olog.entity.LogAttribute> logs;
    public static volatile SingularAttribute<Attribute, edu.msu.nscl.olog.entity.Property> property;
}
