/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

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
    public static volatile SetAttribute<Log, Tag> tags;
    public static volatile SetAttribute<Log, Logbook> logbooks;
}
