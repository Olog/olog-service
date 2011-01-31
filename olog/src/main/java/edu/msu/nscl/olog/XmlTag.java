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
 * Tag object that can be represented as XML/JSON in payload data.
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
@XmlType(propOrder = {"name","status","xmlLogs"})
@XmlRootElement(name = "tag")
public class XmlTag {
    private String name = null;
    private String status = null;
    private XmlLogs logs = null;

    /**
     * Creates a new instance of XmlTag.
     *
     */
    public XmlTag() {
    }

    /**
     * Creates a new instance of XmlTag.
     *
     * @param name
     */
    public XmlTag(String name) {
        this.name = name;
    }

    /**
     * Creates a new instance of XmlTag.
     *
     * @param name
     * @param owner
     */
    public XmlTag(String name, String status) {
        this.name = name;
        this.status = status;
    }

    /**
     * Getter for tag name.
     *
     * @return tag name
     */
    @XmlAttribute
    public String getName() {
        return name;
    }

    /**
     * Setter for tag name.
     *
     * @param name tag name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for tag status.
     *
     * @return tag status
     */
    @XmlAttribute
    public String getStatus() {
        return status;
    }

    /**
     * Setter for tag status.
     *
     * @param owner tag status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Getter for tag's XmlLogs.
     *
     * @return XmlLogs object
     */
    @XmlElement(name = "logs")
    public XmlLogs getXmlLogs() {
        return logs;
    }

    /**
     * Setter for tag's XmlLogs.
     *
     * @param logs XmlLogs object
     */
    public void setXmlLogs(XmlLogs logs) {
        this.logs = logs;
    }

    /**
     * Creates a compact string representation for the log.
     *
     * @param data the XmlTag to log
     * @return string representation for log
     */
    public static String toLog(XmlTag data) {
        if (data.logs == null) {
            return data.getName() + "(" + data.getStatus() + ")";
        } else {
            return data.getName() + "(" + data.getStatus() + ")"
                    + XmlLogs.toLog(data.logs);
        }
    }
}
