/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import java.util.Date;
import javax.persistence.metamodel.*;

/**
 *
 * @author berryman
 */
@StaticMetamodel(Entry.class)
public class Entry_ {
        public static volatile SingularAttribute<Entry, Long> id;
        public static volatile SingularAttribute<Entry, Date> createdDate;
        public static volatile ListAttribute<Entry, Log> logs;
    
}
