/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010-2011 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * All rights reserved. Use is subject to license terms and conditions.
 */

package edu.msu.nscl.olog;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Property object that can be represented as XML/JSON in payload data.
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@helmholtz-berlin.de>
 */
@XmlType(propOrder = {"name","value","owner","xmlLogs"})
@XmlRootElement(name = "property")
public class XmlProperty {
    private String name = null;
    private String value = null;
    private String owner = null;
    private XmlLogs logs = null;

    /**
     * Creates a new instance of XmlProperty.
     *
     */
    public XmlProperty() {
    }

    /**
     * Creates a new instance of XmlProperty.
     *
     * @param name
     * @param owner
     */
    public XmlProperty(String name, String owner) {
        this.owner = owner;
        this.name = name;
    }

    /**
     * Creates a new instance of XmlProperty.
     *
     * @param name
     * @param owner
     * @param value
     */
    public XmlProperty(String name, String owner, String value) {
        this.value = value;
        this.owner = owner;
        this.name = name;
    }

    /**
     * Getter for property name.
     *
     * @return property name
     */
    @XmlAttribute
    public String getName() {
        return name;
    }

    /**
     * Setter for property name.
     *
     * @param name property name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for property value.
     *
     * @return property value
     */
    @XmlAttribute
    public String getValue() {
        return value;
    }

    /**
     * Setter for property value.
     *
     * @param value property value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Getter for property owner.
     *
     * @return property owner
     */
    @XmlAttribute
    public String getOwner() {
        return owner;
    }

    /**
     * Setter for property owner.
     *
     * @param owner property owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Getter for property's XmlLogs.
     *
     * @return XmlChannels object
     */
    @XmlElement(name = "logs")
    public XmlLogs getXmlLogs() {
        return logs;
    }

    /**
     * Setter for property's XmlLogs.
     *
     * @param channels XmlLogs object
     */
    public void setXmlLogs(XmlLogs logs) {
        this.logs = logs;
    }

    /**
     * Creates a compact string representation for the log.
     *
     * @param data the XmlProperty to log
     * @return string representation for log
     */
    public static String toLog(XmlProperty data) {
         if (data.logs == null) {
            return data.getName() + "(" + data.getOwner() + ")";
        } else {
            return data.getName() + "(" + data.getOwner() + ")"
                    + XmlLogs.toLog(data.logs);
        }
    }
}
