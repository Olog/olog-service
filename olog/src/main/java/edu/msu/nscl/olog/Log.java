/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin fuer Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */
package edu.msu.nscl.olog;

import java.io.Serializable;
import java.util.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
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
@XmlType(propOrder = {"id","createdDate","modifiedDate","level","owner","source","version","description","logbooks","tags","xmlProperties","xmlAttachments","children"})
@XmlRootElement(name = "log")
public class Log implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    
    @Transient
    private int version;
    
    @Column(name = "owner", nullable = false, length = 50, insertable = true)
    private String owner;
    
    @Column(name = "source", nullable = false, length = 50, insertable = true)
    private String source;

    @ManyToOne(optional = true)
    @JoinColumn(name = "level_id", referencedColumnName="id")
    private Level level;
    
    @Enumerated(EnumType.STRING)
    private Status status;
    
    @Column(name = "md5entry", nullable = false, length = 50, insertable = true)
    private String md5Entry;
    
    @Column(name = "md5recent", nullable = false, length = 50, insertable = true)
    private String md5Recent;
    
    @Transient
    private Long tableId;
    
    @Column(name = "created", nullable = false, length = 50, insertable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    
    @Transient
    private Date modifiedDate;
    
    @Column(name = "description", nullable = false, length = 50, insertable = true)
    private String description;
    
    @Transient
    private Collection<XmlProperty> properties = new ArrayList<XmlProperty>();
    

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "logs_logbooks", joinColumns = {
        @JoinColumn(name = "log_id", unique = true)
    },
    inverseJoinColumns = {
        @JoinColumn(name = "logbook_id")
    })
    @NotNull
    private Set<Logbook> logbooks;
    

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "parent_id",insertable=false,updatable=false)
    private Log parent;
    
    @OneToMany
    @JoinColumn(name = "parent_id")
    private Collection<Log> children = new ArrayList<Log>();
    
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "logs_logbooks", joinColumns = {
        @JoinColumn(name = "log_id", unique = true)
    },
    inverseJoinColumns = {
        @JoinColumn(name = "logbook_id")
    })
    private Set<Tag> tags;
    
    @Transient
    private Collection<XmlAttachment> attachments = new ArrayList<XmlAttachment>();

    /** Creates a new instance of Log */
    public Log() {
        this.level = new Level();
    }

    /**
     * Creates a new instance of Log.
     *
     * @param logId log id
     */
    public Log(Long logId) {
        this.level = new Level();
        this.id = logId;
    }

    /**
     * Creates a new instance of Log.
     *
     * @param owner log owner
     */
    public Log(String owner) {
//        this.subject = subject;
        this.level = new Level();
        this.owner = owner;
    }

    /**
     * Creates a new instance of Log.
     *
     * @param logId log id
     * @param owner log owner
     */
    public Log(Long logId, String owner) {
        this.level = new Level();
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
        if(parent!=null)
            return parent.id;
        return id;
    }
    
    @XmlTransient
    public Long getInternalId(){
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
    @XmlTransient
    public Status getStatus() {
        return status;
    }

    /**
     * @return the status
     */
    public void setStatus(Status status) {
        this.status = status;
    }
    
    /**
     * Getter for log version id.
     *
     * @return versionId
     */
    @XmlAttribute
    public int getVersion() {
        if(parent!=null)
            return parent.children.size();
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
        return level.getName();
    }

    /**
     * Setter for log owner.
     *
     * @param level
     */
    public void setLevel(String level) {
        this.level.setName(level);
    }

    /**
     * Getter for log created date.
     *
     * @return createdDate
     */
    @XmlAttribute
    public Date getCreatedDate() {
        if(parent!=null)
            return parent.createdDate;
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
        if(parent!=null)
            return createdDate;
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
     * Getter for MD5 entry.
     *
     * @return description
     */
    public String getMD5Entry() {
        return md5Entry;
    }

    /**
     * Setter for MD5 entry.
     *
     * @param description the value to set
     */
    public void setMD5Entry(String md5entry) {
        this.md5Entry = md5entry;
    }
    
    /**
     * Getter for Table id.
     *
     * @return table id
     */
    public Long getTableId() {
        return tableId;
    }

    /**
     * Setter for Table id.
     *
     * @param Table id to set
     */
    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }
    
    /**
     * Getter for log's XmlProperties.
     *
     * @return properties XmlProperties
     */
    @XmlElementWrapper(name = "properties")
    @XmlElement(name = "property")
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
     * Adds an XmlProperty to the log.
     *
     * @param property single XmlProperty
     */
    public void addXmlProperty(XmlProperty property) {
        this.properties.add(property);
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
    public void addXmlTag(Tag tag) {
        this.tags.add(tag);
    }

    /**
     * Getter for the log's XmlAttachments.
     *
     * @return XmlAttachments for this log
     */
    @XmlElement(name = "attachments")
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

    /**
     * @return the children
     */
    @XmlElementWrapper(name = "edits")
    @XmlElement(name = "log")
    public Collection<Log> getChildren() {
        return children;        
    }

    /**
     * @param children the children to set
     */
    public void setChildren(Collection<Log> children) {
        this.children = children;
    }

    /**
     * @return the parent
     */
    public Log getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(Log parent) {
        this.parent = parent;
    }
    
    /**
     * Creates a compact string representation for the log.
     *
     * @param data Log to create the string representation for
     * @return string representation
     */
    public static String toLogger(Log data) {
        Logbooks xl = new Logbooks();
        xl.setLogbooks(data.logbooks);
        
        Tags xt = new Tags();
        xt.setTags(data.tags);
        
        return data.getId() + "-v." + data.getVersion() + " : " + /*data.getSubject() +*/ "(" + data.getOwner() + "):["
                + Logbooks.toLogger(xl)
                + Tags.toLogger(xt)
                + "]\n";
    }
}
