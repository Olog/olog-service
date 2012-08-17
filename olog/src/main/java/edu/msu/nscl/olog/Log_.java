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
@StaticMetamodel(Log.class)
public class Log_ {

    public static volatile SingularAttribute<Log, Long> id;
    public static volatile SingularAttribute<Log, State> state;
    public static volatile SingularAttribute<Log, Date> createdDate;
    public static volatile SingularAttribute<Log, Date> modifiedDate;
    public static volatile SetAttribute<Log, Tag> tags;
    public static volatile SetAttribute<Log, Logbook> logbooks;
    public static volatile SingularAttribute<Log, Log> parent;
}
