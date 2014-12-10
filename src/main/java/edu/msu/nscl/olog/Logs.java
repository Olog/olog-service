/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin fuer Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */
package edu.msu.nscl.olog;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
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
public class Logs {

    private Long count;
    private List<Log> logs = new ArrayList<Log>();
    
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
        logs.add(log);
    }

    public Logs(List<Log> logs) {
        if (!CollectionUtils.isEmpty(logs)) {
            this.logs.addAll(logs);
        }
    }
    
    @XmlAttribute(name = "count")
    public Long getCount() {
        return count;
    }
    
    public void setCount(Long count) {
        this.count = count;
    }

    /**
     * Returns a collection of Log.
     *
     * @return logs a collection of Log
     */
    @XmlElementRef(type = Log.class, name = "log")
    @JsonProperty("log")
    public List<Log> getLogs() {
        return logs;
    }
    

    /**
     * Sets the collection of logs.
     *
     * @param items new log collection
     */
    public void setLogs(List<Log> items) {
        logs.clear();
        logs.addAll(items);
    }

    /**
     * Adds a log to the log collection.
     *
     * @param item the Log to add
     */
    public void addLog(Log item) {
        logs.add(item);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Logs)) return false;
        if (!super.equals(o)) return false;

        Logs logs = (Logs) o;

        if (count != null ? !count.equals(logs.count) : logs.count != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (count != null ? count.hashCode() : 0);
        return result;
    }
}
