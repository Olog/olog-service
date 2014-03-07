/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin fuer Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */
package edu.msu.nscl.olog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

/**
 * Logbooks (collection) object that can be represented as XML/JSON in payload
 * data.
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
@XmlRootElement(name = "logbooks")
public class Logbooks {

    private Collection<Logbook> logbooks = new ArrayList<Logbook>();

    /**
     * Creates a new instance of Logbooks.
     */
    public Logbooks() {
    }

    /**
     * Creates a new instance of Logbooks with one initial logbook.
     *
     * @param logbook initial element
     */
    public Logbooks(Logbook logbook) {
        logbooks.add(logbook);
    }

    /**
     * Returns a collection of Logbook.
     *
     * @return logbooks a collection of Logbook
     */
    @XmlElement(name = "logbook")
    public Collection<Logbook> getLogbooks() {
        return logbooks;
    }

    /**
     * Sets the collection of logbooks.
     *
     * @param items new logbook collection
     */
    public void setLogbooks(Collection<Logbook> items) {
        this.logbooks = items;
    }

    /**
     * Adds a logbook to the logbook collection.
     *
     * @param item the Logbook to add
     */
    public void addLogbook(Logbook item) {
        this.logbooks.add(item);
    }

    /**
     * Creates a compact string representation for the log.
     *
     * @param data XmlLog to create the string representation for
     * @return string representation
     */
    public static String toLogger(Logbooks data) {
        if (data.getLogbooks().size() == 0) {
            return "[None]";
        } else {
            StringBuilder s = new StringBuilder();
            s.append("[");
            for (Logbook p : data.getLogbooks()) {
                s.append(Logbook.toLogger(p) + ",");
            }
            s.delete(s.length() - 1, s.length());
            s.append("]");
            return s.toString();
        }
    }

    public static String toLogger(Set<Logbook> data) {
        if (data.size() == 0) {
            return "[None]";
        } else {
            StringBuilder s = new StringBuilder();
            s.append("[");
            for (Logbook p : data) {
                s.append(Logbook.toLogger(p) + ",");
            }
            s.delete(s.length() - 1, s.length());
            s.append("]");
            return s.toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Logbooks)) return false;

        Logbooks logbooks1 = (Logbooks) o;

        if (logbooks != null ? !logbooks.equals(logbooks1.logbooks) : logbooks1.logbooks != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return logbooks != null ? logbooks.hashCode() : 0;
    }
}
