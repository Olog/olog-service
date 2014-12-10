/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin fuer Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */
package edu.msu.nscl.olog;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.persistence.annotations.CacheType;

import java.io.Serializable;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

/**
 * Log object that can be represented as XML/JSON in payload data.
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
@Entity
@Table(name = "logs")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {"createdDate", "modifiedDate", "owner", "source", "version", "description", "logbooks", "tags", "xmlProperties", "xmlAttachments"})
@XmlRootElement(name = "log")
public class Log implements Serializable, Comparable<Log> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Transient
    private String version;
    
    @Column(name = "owner", nullable = false, length = 50, insertable = true, updatable = false)
    private String owner;
    
    @Column(name = "source", nullable = false, length = 50, insertable = true, updatable = false)
    private String source;
    
    @Enumerated(EnumType.STRING)
    private Level level;

    @Enumerated(EnumType.STRING)
    private State state;
    
    @Column(name = "modified", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;
    
    @Column(name = "description", nullable = false, insertable = true, updatable = false)
    private String description;
    
    @Transient
    private Collection<XmlProperty> properties = new ArrayList<XmlProperty>();
    
    @OneToMany(mappedBy = "log", cascade = CascadeType.PERSIST)
    private Set<LogAttribute> attributes = new HashSet<LogAttribute>();
    
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "logs_logbooks", joinColumns = {
        @JoinColumn(name = "log_id", unique = true)
    },
    inverseJoinColumns = {
        @JoinColumn(name = "logbook_id", insertable = false, updatable = false)
    })
    @NotNull
    private Set<Logbook> logbooks = new HashSet<Logbook>();
    
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "logs_logbooks", joinColumns = {
        @JoinColumn(name = "log_id", unique = true)
    },
    inverseJoinColumns = {
        @JoinColumn(name = "logbook_id", insertable = false, updatable = false)
    })
    private Set<Tag> tags = new HashSet<Tag>();
    
    @ManyToOne
    @JoinColumn(name = "entry_id", unique = true)
    private Entry entry;
    
    @Transient
    private Collection<XmlAttachment> attachments = new ArrayList<XmlAttachment>();

    @PrePersist
    public void setUpdated() {
        this.setModifiedDate(new Date());
    }

    /**
     * Creates a new instance of Log
     */
    public Log() {
        // this.level = new Level();
    }

    /**
     * Creates a new instance of Log.
     *
     * @param logId log id
     */
    public Log(Long logId) {
        //      this.level = new Level();
        this.id = logId;
    }

    /**
     * Creates a new instance of Log.
     *
     * @param logId log id
     * @param owner log owner
     */
    public Log(Long logId, String owner) {
        //    this.level = new Level();
        this.id = logId;
        this.owner = owner;
    }

    /**
     * Getter for log id.
     *
     * @return id
     */
    @XmlTransient
    @JsonIgnore
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

    @XmlAttribute(name = "id")
    @JsonProperty("id")
    public Long getEntryId() {
        if (entry != null) {
            return entry.getId();
        } else {
            return null;
        }
    }
    
    public void setEntryId(Long id) {
        if (entry != null) {
            entry.setId(id);
        } else {
            Entry newEntry = new Entry();
            newEntry.setId(id);
            newEntry.addLog(this);
            this.entry = newEntry;
        }
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
        if (entry != null) {
            return entry.getCreatedDate();
        } else {
            return null;
        }
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
        return properties;
    }

    /**
     * Setter for log's XmlProperties.
     *
     * @param properties XmlProperties
     */
    public void setXmlProperties(Collection<XmlProperty> properties) {
        this.properties = properties;
    }

    /**
     * Adds an Property to the log.
     *
     * @param property single Property
     */
    public void addXmlProperty(XmlProperty property) {
        this.properties.add(property);
    }
    
    public void removeXmlProperty(XmlProperty property) {
        this.properties.remove(property);
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
        return attachments;
    }

    /**
     * Setter for the log's XmlAttachments.
     *
     * @param attachments XmlAttachments
     */
    public void setXmlAttachments(Collection<XmlAttachment> attachments) {
        this.attachments = attachments;
    }

    /**
     * Adds an XmlAttachment to the collection.
     *
     * @param attachment
     */
    public void addXmlAttachment(XmlAttachment attachment) {
        this.attachments.add(attachment);
    }
    
    public void removeXmlAttachment(XmlAttachment attachment) {
        this.attachments.remove(attachment);
    }

    /**
     * @return the entry
     */
    @JsonIgnore
    public Entry getEntry() {
        return entry;
    }

    /**
     * @param entry the entry to set
     */
    public void setEntry(Entry entry) {
        this.entry = entry;
    }
    
    @XmlTransient
    public Set<LogAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<LogAttribute> attributes) {
        this.attributes = attributes;
    }
    
    //@XmlElementWrapper(name = "tests")
   // @XmlElement(name = "test")
    //public Map<String, LogAttribute> getTests() {
    //    return tests;
    //}

   // public void setTests(Map<String, LogAttribute> tests) {
   //     this.tests = tests;
   // }

    public int compareTo(Log num) {
        int x = modifiedDate.compareTo(num.modifiedDate);
        return x;
    }

    /**
     * Creates a compact string representation for the log.
     *
     * @param data Log to create the string representation for
     * @return string representation
     */
    public static String toLogger(Log data) {
        Set<Logbook> xl = data.getLogbooks();
        Set<Tag> xt = data.getTags();

        return data.getId() + "-v." + data.getVersion() + " : " + "(" + data.getOwner() + "):["
//                + Logbooks.toLogger(xl)
//                + Tags.toLogger(xt)
                + "]\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Log)) return false;

        Log log = (Log) o;

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
