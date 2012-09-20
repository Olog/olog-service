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
@StaticMetamodel(Attribute.class)
public class Attribute_ {

    public static volatile SingularAttribute<Attribute, Long> id;
    public static volatile SingularAttribute<Attribute, String> name;
    public static volatile SingularAttribute<Attribute, State> state;
    public static volatile SingularAttribute<Attribute, Property> property;
}
