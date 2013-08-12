/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author berryman
 */
@StaticMetamodel(Logbook.class)
public class Logbook_ {

    public static volatile SingularAttribute<Logbook, Long> id;
    public static volatile SingularAttribute<Logbook, String> name;
    public static volatile SingularAttribute<Logbook, String> owner;
}
