/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin fuer Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */

package edu.msu.nscl.olog;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import java.util.Date;

/**
 * Log object that can be represented as XML/JSON in payload data.
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */

@XmlRootElement(name = "log")
public class XmlLog {
    private Long id;
    private int version;
    private String owner;
    private String source;
    private String level;
    private String md5Entry;
    private String md5Recent;
    private Date createdDate;
    private Date modifiedDate;
    private String subject;
    private String description;
    private XmlProperties properties = new XmlProperties();
    private XmlLogbooks logbooks = new XmlLogbooks();
    private XmlTags tags = new XmlTags();
  
    /** Creates a new instance of XmlLog */
    public XmlLog() {
    }

    /**
     * Creates a new instance of XmlLog.
     *
     * @param logId Log id
     */
    public XmlLog(Long logId) {
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
    public XmlLog(Long id, String owner) {
        this.id = id;
        this.owner = owner;
    }

    /**
     * Getter for log id.
     *
     * @return id
     */
    @XmlAttribute
    public Long getId() {
        return id;
    }

    /**
     * Setter for log id.
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter for log version id.
     *
     * @return version
     */
    @XmlAttribute
    public int getVersion() {
        return version;
    }

    /**
     * Setter for log version id.
     *
     * @param version
     */
    public void setVersion(int version) {
        this.version = version;
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
     * Setter for log level.
     *
     * @param level
     */
    public void setLevel(String level) {
        this.level = level;
    }

    /**
     * Getter for log created date.
     *
     * @return createdDate
     */
    @XmlAttribute
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * Setter for log created date.
     *
     * @param createdDate
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Getter for log modified date.
     *
     * @return modifiedDate
     */
    @XmlAttribute
    public Date getModifiedDate() {
        return modifiedDate;
    }

    /**
     * Setter for log modified date.
     *
     * @param modifiedDate
     */
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    /**
     * Getter for log source IP.
     *
     * @return source IP
     */
    @XmlAttribute
    public String getSource() {
        return source;
    }

    /**
     * Setter for log source IP.
     *
     * @param source IP
     */
    public void setSource(String source) {
        this.source = source;
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
     * Getter for log's XmlProperties.
     *
     * @return properties XmlProperties
     */
    @XmlElement(name = "properties")
    public XmlProperties getXmlProperties() {
        return properties;
    }

    /**
     * Setter for log's XmlProperties.
     *
     * @param properties XmlProperties
     */
    public void setXmlProperties(XmlProperties properties) {
        this.properties = properties;
    }

    /**
     * Adds an XmlProperty to the log.
     *
     * @param property single XmlProperty
     */
    public void addXmlProperty(XmlProperty property) {
        this.properties.addXmlProperty(property);
    }

    /**
     * Getter for log's XmlLogbooks.
     *
     * @return logbooks XmlLogbooks
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
     * @return tags XmlTags for this log
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
     * @param tag XmlTag
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
