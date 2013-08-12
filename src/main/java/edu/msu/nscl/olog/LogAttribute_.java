/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import java.util.Date;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author berryman
 */
@StaticMetamodel(LogAttribute.class)
public class LogAttribute_ {

    public static volatile SingularAttribute<LogAttribute, Long> id;
    public static volatile SingularAttribute<LogAttribute, String> value;
    public static volatile SingularAttribute<LogAttribute, Attribute> attribute;
    public static volatile SingularAttribute<LogAttribute, Log> log;
}
