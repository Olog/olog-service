/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin fuer Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */
package edu.msu.nscl.olog.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.*;
import javax.xml.bind.annotation.*;

/**
 * Log object that can be represented as XML/JSON in payload data.
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {"createdDate", "modifiedDate", "owner", "source", "version", "description", "logbooks", "tags", "xmlProperties", "xmlAttachments"})
@XmlRootElement(name = "log")
public class XmlLog implements Serializable, Comparable<XmlLog> {
    
    private Long id;

    private String version;
    
    private String owner;
    
    private String source;
    
    private Level level;

    private State state;
    
    private Date modifiedDate;
    
    private Date createdDate;
    
    private Date eventStart;
    
    private Date eventEnd;
    
    private String description;
    
    private Collection<XmlProperty> xmlProperties = new ArrayList<XmlProperty>();
    
    private Set<Logbook> logbooks = new HashSet<Logbook>();
    
    private Set<Tag> tags = new HashSet<Tag>();
    
    private Collection<XmlAttachment> xmlAttachments = new ArrayList<XmlAttachment>();

    /**
     * Creates a new instance of Log
     */
    public XmlLog() {
        // this.level = new Level();
    }

    public XmlLog(Long id, String version, String owner, String source, Level level, State state, Date modifiedDate, Date createdDate, Date eventStart, Date eventEnd, String description) {
        this.id = id;
        this.version = version;
        this.owner = owner;
        this.source = source;
        this.level = level;
        this.state = state;
        this.modifiedDate = modifiedDate;
        this.createdDate = createdDate;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
        this.description = description;
    }

    /**
     * Creates a new instance of Log.
     *
     * @param logId log id
     */
    public XmlLog(Long logId) {
        //      this.level = new Level();
        this.id = logId;
    }

    /**
     * Creates a new instance of Log.
     *
     * @param logId log id
     * @param owner log owner
     */
    public XmlLog(Long logId, String owner) {
        //    this.level = new Level();
        this.id = logId;
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
     * @param logId
     */
    public void setId(Long logId) {
        this.id = logId;
    }

    /**
     * @return the status
     */
    @XmlAttribute
    public State getState() {
        return state;
    }

    /**
     * @return the status
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * Getter for log version id.
     *
     * @return versionId
     */
    @XmlAttribute
    public String getVersion() {
        return version;
    }

    /**
     * Setter for log version id.
     *
     * @param version
     */
    public void setVersion(String version) {
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
    public Level getLevel() {
        return level;
    }

    /**
     * Setter for log owner.
     *
     * @param level
     */
    public void setLevel(Level level) {
        this.level = level;
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
     * @param modifiedDate
     */
    public void setCreatedDate(Date createdDate){
        this.createdDate = createdDate;
    }

    /**
     * Getter for log event start date.
     *
     * @return eventStart
     */
    @XmlAttribute
    public Date getEventStart() {
        return eventStart;
    }

    /**
     * Setter for log event start date.
     *
     * @param eventStart
     */
    public void setEventStart(Date eventStart) {
        this.eventStart = eventStart;
    }
    
    /**
     * Getter for log event end date.
     *
     * @return eventEnd
     */
    @XmlAttribute
    public Date getEventEnd() {
        return eventEnd;
    }
    
    /**
     * Setter for log event end date.
     *
     * @param eventEnd
     */
    public void setEventEnd(Date eventEnd) {
        this.eventEnd = eventEnd;
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
     * Getter for log description.
     *
     * @return description
     */
    @XmlElement(name = "description")
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
    @XmlElementWrapper(name = "properties")
    @XmlElement(name = "property")
    @JsonProperty("properties")
    public Collection<XmlProperty> getXmlProperties() {
        return xmlProperties;
    }

    /**
     * Setter for log's XmlProperties.
     *
     * @param properties XmlProperties
     */
    public void setXmlProperties(Collection<XmlProperty> properties) {
        this.xmlProperties = properties;
    }

    /**
     * Adds an Property to the log.
     *
     * @param property single Property
     */
    public void addXmlProperty(XmlProperty property) {
        this.xmlProperties.add(property);
    }
    
    public void removeXmlProperty(XmlProperty property) {
        this.xmlProperties.remove(property);
    }

    /**
     * Getter for log's Logbooks.
     *
     * @return Logbooks
     */
    @XmlElementWrapper(name = "logbooks")
    @XmlElement(name = "logbook")
    public Set<Logbook> getLogbooks() {
        return logbooks;
    }

    /**
     * Setter for log's Logbooks.
     *
     * @param logbooks Logbooks
     */
    public void setLogbooks(Set<Logbook> logbooks) {
        this.logbooks = logbooks;
    }

    /**
     * Adds an Logbook to the log.
     *
     * @param logbook single Logbook
     */
    public void addLogbook(Logbook logbook) {
        this.logbooks.add(logbook);
    }
    
    public void removeLogbook(Logbook logbook) {
        this.logbooks.remove(logbook);
    }

    /**
     * Getter for the log's Tags.
     *
     * @return Tags for this log
     */
    @XmlElementWrapper(name = "tags")
    @XmlElement(name = "tag")
    public Set<Tag> getTags() {
        return tags;
    }

    /**
     * Setter for the log's Tags.
     *
     * @param tags Tags
     */
    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    /**
     * Adds an Tag to the collection.
     *
     * @param tag
     */
    public void addTag(Tag tag) {
        this.tags.add(tag);
    }
    
    public void removeTag(Tag tag) {
        this.tags.remove(tag);
    }

    /**
     * Getter for the log's XmlAttachments.
     *
     * @return XmlAttachments for this log
     */
    @XmlElementWrapper(name = "attachments")
    @XmlElement(name = "attachment")
    @JsonProperty("attachments")
    public Collection<XmlAttachment> getXmlAttachments() {
        return xmlAttachments;
    }

    /**
     * Setter for the log's XmlAttachments.
     *
     * @param attachments XmlAttachments
     */
    public void setXmlAttachments(Collection<XmlAttachment> xmlAttachments) {
        this.xmlAttachments = xmlAttachments;
    }

    /**
     * Adds an XmlAttachment to the collection.
     *
     * @param attachment
     */
    public void addXmlAttachment(XmlAttachment xmlAttachments) {
        this.xmlAttachments.add(xmlAttachments);
    }
    
    public void removeXmlAttachment(XmlAttachment xmlAttachments) {
        this.xmlAttachments.remove(xmlAttachments);
    }

    public int compareTo(XmlLog num) {
        int x = modifiedDate.compareTo(num.modifiedDate);
        return x;
    }

    /**
     * Creates a compact string representation for the log.
     *
     * @param data Log to create the string representation for
     * @return string representation
     */
    public static String toLogger(XmlLog data) {
        Logbooks xl = new Logbooks(data.getLogbooks());
        Tags xt = new Tags(data.getTags());

        return data.getId() + "-v." + data.getVersion() + " : " + "(" + data.getOwner() + "):["
                + Logbooks.toLogger(xl)
                + Tags.toLogger(xt)
                + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XmlLog)) return false;

        XmlLog log = (XmlLog) o;

        if (description != null ? !description.equals(log.description) : log.description != null) return false;
        if (!id.equals(log.id)) return false;
        if (level != log.level) return false;
        if (modifiedDate != null ? !modifiedDate.equals(log.modifiedDate) : log.modifiedDate != null) return false;
        if (owner != null ? !owner.equals(log.owner) : log.owner != null) return false;
        if (source != null ? !source.equals(log.source) : log.source != null) return false;
        if (state != log.state) return false;
        if (version != null ? !version.equals(log.version) : log.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (level != null ? level.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (modifiedDate != null ? modifiedDate.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
