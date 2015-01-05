package edu.msu.nscl.olog.entity;

import javax.persistence.metamodel.*;

@StaticMetamodel(Property.class)
public abstract class Property_
{
    public static volatile SingularAttribute<Property, java.lang.Long> id;
    public static volatile SingularAttribute<Property, java.lang.String> name;
    public static volatile SingularAttribute<Property, edu.msu.nscl.olog.entity.State> state;
    public static volatile SetAttribute<Property, edu.msu.nscl.olog.entity.Attribute> attributes;
}
