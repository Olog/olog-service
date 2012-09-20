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
@StaticMetamodel(Property.class)
public class Property_ {

    public static volatile SingularAttribute<Property, Long> id;
    public static volatile SingularAttribute<Property, String> name;
    public static volatile SingularAttribute<Property, State> state;
    public static volatile SetAttribute<Property, Attribute> attributes;
}
