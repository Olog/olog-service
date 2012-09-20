/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin fuer Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */
package edu.msu.nscl.olog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Logs (collection) object that can be represented as XML/JSON in payload data.
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
@XmlRootElement(name = "logs")
public class Logs extends ArrayList<Log> {

    /**
     * Creates a new instance of Logs.
     */
    public Logs() {
    }

    /**
     * Creates a new instance of Logs with one initial log.
     *
     * @param log Log initial element
     */
    public Logs(Log log) {
        this.add(log);
    }

    /**
     * Returns a collection of Log.
     *
     * @return logs a collection of Log
     */
    @XmlElementRef(type=Log.class, name = "log")
    public List<Log> getLogList() {
        return this;
    }
    
    @XmlTransient
    public List<Log> getLogs() {
        return this;
    }

    /**
     * Sets the collection of logs.
     *
     * @param items new log collection
     */
    public void setLogs(List<Log> items) {
        this.clear();
        this.addAll(items);
    }

    /**
     * Adds a log to the log collection.
     *
     * @param item the Log to add
     */
    public void addLog(Log item) {
        this.add(item);
    }

    /**
     * Creates a compact string representation for the log.
     *
     * @param data Log to create the string representation for
     * @return string representation
     */
    public static String toLogger(Logs data) {
        if (data.getLogs().size() == 0) {
            return "[None]";
        } else {
            StringBuilder s = new StringBuilder();
            s.append("[");
            for (Log c : data.getLogs()) {
                s.append(Log.toLogger(c) + ",");
            }
            s.delete(s.length() - 1, s.length());
            s.append("]");
            return s.toString();
        }
    }
}
