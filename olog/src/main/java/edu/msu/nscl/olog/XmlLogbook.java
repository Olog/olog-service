/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */

package edu.msu.nscl.olog;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Logbook object that can be represented as XML/JSON in payload data.
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
@XmlType(propOrder = {"name","owner","xmlLogs"})
@XmlRootElement(name = "logbook")
public class XmlLogbook {
    private String name = null;
    private String owner = null;
    private XmlLogs logs = null;

    /**
     * Creates a new instance of XmlLogbook.
     *
     */
    public XmlLogbook() {
    }

    /**
     * Creates a new instance of XmlLogbook.
     *
     * @param name
     * @param owner
     */
    public XmlLogbook(String name, String owner) {
        this.owner = owner;
        this.name = name;
    }

    /**
     * Getter for logbook name.
     *
     * @return name logbook name
     */
    @XmlAttribute
    public String getName() {
        return name;
    }

    /**
     * Setter for logbook name.
     *
     * @param name logbook name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for logbook owner.
     *
     * @return owner logbook owner
     */
    @XmlAttribute
    public String getOwner() {
        return owner;
    }

    /**
     * Setter for logbook owner.
     *
     * @param owner logbook owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Getter for logbook's XmlLogs.
     *
     * @return logs XmlLogs object
     */
    @XmlElement(name = "logs")
    public XmlLogs getXmlLogs() {
        return logs;
    }

    /**
     * Setter for logbook's XmlLogs.
     *
     * @param logs XmlLogs object
     */
    public void setXmlLogs(XmlLogs logs) {
        this.logs = logs;
    }

    /**
     * Creates a compact string representation for the log.
     *
     * @param data the XmlLogbook to log
     * @return string representation for log
     */
    public static String toLog(XmlLogbook data) {
         if (data.logs == null) {
            return data.getName() + "(" + data.getOwner() + ")";
        } else {
            return data.getName() + "(" + data.getOwner() + ")"
                    + XmlLogs.toLog(data.logs);
        }
    }
}
