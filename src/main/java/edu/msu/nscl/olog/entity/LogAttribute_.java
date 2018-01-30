package edu.msu.nscl.olog.entity;

import javax.persistence.metamodel.*;

@StaticMetamodel(LogAttribute.class)
public abstract class LogAttribute_
{
    public static volatile SingularAttribute<LogAttribute, java.lang.Long> id;
    public static volatile SingularAttribute<LogAttribute, java.lang.Long> logId;
    public static volatile SingularAttribute<LogAttribute, java.lang.Long> attributeId;
    public static volatile SingularAttribute<LogAttribute, java.lang.String> value;
    public static volatile SingularAttribute<LogAttribute, java.lang.Long> groupingNum;
    public static volatile SingularAttribute<LogAttribute, edu.msu.nscl.olog.entity.Log> log;
    public static volatile SingularAttribute<LogAttribute, edu.msu.nscl.olog.entity.Attribute> attribute;
}
