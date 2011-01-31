/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin fuer Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */

package edu.msu.nscl.olog;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Channel object that can be represented as XML/JSON in payload data.
 *
 * @author berryman from Ralph Lange <Ralph.Lange@bessy.de>
 */

@XmlRootElement(name = "log")
public class XmlLog {
    private int id;
    private String owner;
    private String level;
    private String subject;
    private String description;
    private XmlLogbooks logbooks = new XmlLogbooks();
    private XmlTags tags = new XmlTags();
  
    /** Creates a new instance of XmlLog */
    public XmlLog() {
    }

    /**
     * Creates a new instance of XmlLog.
     *
     * @param subject log subject
     */
    public XmlLog(int logId) {
        this.id = logId;
    }

    /**
     * Creates a new instance of XmlLog.
     *
     * @param subject log subject
     */
    public XmlLog(String subject) {
        this.subject = subject;
    }

    /**
     * Creates a new instance of XmlLog.
     *
     * @param subject log subject
     * @param owner log owner
     */
    public XmlLog(String subject, String owner) {
        this.subject = subject;
        this.owner = owner;
    }

    /**
     * Creates a new instance of XmlLog.
     *
     * @param id log id
     * @param owner log owner
     */
    public XmlLog(int logId, String owner) {
        this.id = logId;
        this.owner = owner;
    }

    /**
     * Getter for log id.
     *
     * @return id
     */
    @XmlAttribute
    public int getId() {
        return id;
    }

    /**
     * Setter for log id.
     *
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter for log owner.
     *
     * @return owner
     */
    @XmlAttribute
    public String getOwner() {
        return owner;
    }

    /**
     * Setter for log owner.
     *
     * @param owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Getter for log level.
     *
     * @return level
     */
    @XmlAttribute
    public String getLevel() {
        return level;
    }

    /**
     * Setter for log owner.
     *
     * @param level
     */
    public void setLevel(String level) {
        this.level = level;
    }

    /**
     * Getter for log subject.
     *
     * @return subject
     */
    @XmlElement(name="subject")
    public String getSubject() {
        return subject;
    }

    /**
     * Setter for log subject.
     *
     * @param subject the value to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

       /**
     * Getter for log description.
     *
     * @return description
     */
    @XmlElement(name="description")
    public String getDescription() {
        return description;
    }

    /**
     * Setter for log description.
     *
     * @param description the value to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for log's XmlLogbooks.
     *
     * @return XmlLogbooks
     */
    @XmlElement(name = "logbooks")
    public XmlLogbooks getXmlLogbooks() {
        return logbooks;
    }

    /**
     * Setter for log's XmlLogbooks.
     *
     * @param logbooks XmlLogbooks
     */
    public void setXmlLogbooks(XmlLogbooks logbooks) {
        this.logbooks = logbooks;
    }

    /**
     * Adds an XmlLogbook to the log.
     *
     * @param logbook single XmlLogbook
     */
    public void addXmlLogbook(XmlLogbook logbook) {
        this.logbooks.addXmlLogbook(logbook);
    }

    /**
     * Getter for the log's XmlTags.
     *
     * @return XmlTags for this log
     */
    @XmlElement(name = "tags")
    public XmlTags getXmlTags() {
        return tags;
    }

    /**
     * Setter for the log's XmlTags.
     *
     * @param tags XmlTags
     */
    public void setXmlTags(XmlTags tags) {
        this.tags = tags;
    }

    /**
     * Adds an XmlTag to the collection.
     *
     * @param tag
     */
    public void addXmlTag(XmlTag tag) {
        this.tags.addXmlTag(tag);
    }

    /**
     * Creates a compact string representation for the log.
     *
     * @param data XmlLog to create the string representation for
     * @return string representation
     */
    public static String toLog(XmlLog data) {
        return data.getSubject() + "(" + data.getOwner() + "):["
                + XmlLogbooks.toLog(data.logbooks)
                + XmlTags.toLog(data.tags)
                + "]";
    }
}
