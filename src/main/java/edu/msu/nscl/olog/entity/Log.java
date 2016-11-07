/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin fuer Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */
package edu.msu.nscl.olog.entity;

import edu.msu.nscl.olog.bitemporal.control.TimeUtils;
import java.io.Serializable;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;


/**
 * Log object that can be represented as XML/JSON in payload data.
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
@Entity
@Cache(coordinationType=CacheCoordinationType.SEND_NEW_OBJECTS_WITH_CHANGES)
@Table(name = "logs")
public class Log implements Serializable, Comparable<Log> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "version", nullable = false, length = 50, insertable = true, updatable = false)
    private String version;
    
    @Column(name = "owner", nullable = false, length = 50, insertable = true, updatable = false)
    private String owner;
    
    @Column(name = "source", nullable = false, length = 50, insertable = true, updatable = false)
    private String source;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    private Level level;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private State state;
    
    @Column(name = "modified", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;
    
    @Column(name = "description", nullable = false, insertable = true, updatable = false)
    private String description;
    
    @OneToMany(mappedBy = "log", cascade = CascadeType.PERSIST)
    private Set<LogAttribute> attributes = new HashSet<LogAttribute>();
    
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "logs_logbooks", 
        joinColumns = {@JoinColumn(name = "log_id", unique = true)},
        inverseJoinColumns = {@JoinColumn(name = "logbook_id", insertable = false, updatable = false)})
    @NotNull
    private Set<Logbook> logbooks = new HashSet<Logbook>();
    
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "logs_logbooks", 
        joinColumns = {@JoinColumn(name = "log_id", unique = true)},
        inverseJoinColumns = {@JoinColumn(name = "logbook_id", insertable = false, updatable = false)})
    private Set<Tag> tags = new HashSet<Tag>();
    
    @ManyToOne(targetEntity = Entry.class)
    @JoinColumn(nullable = false, updatable = false)
    private Entry entry;

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
     * Creates a new instance of Log merged with Log
     */
    public Log(Log log1, Log log2){
        this.description = (log1.description!=null)?log1.description:log2.description;
        this.entry = (log1.entry!=null)?log1.entry:log2.entry;
        this.id = (log1.id!=null)?log1.id:log2.id;
        this.level = (log1.level!=null)?log1.level:log2.level;
        this.modifiedDate = (log1.modifiedDate!=null)?log1.modifiedDate:log2.modifiedDate;
        this.owner = (log1.owner!=null)?log1.owner:log2.owner;
        this.source = (log1.source!=null)?log1.source:log2.source;
        this.state = (log1.state!=null)?log1.state:log2.state;
        this.version = (log1.version!=null)?log1.version:log2.version;
        
        this.tags.addAll(log1.tags);
        this.tags.addAll(log2.tags);
        this.logbooks.addAll(log1.logbooks);
        this.logbooks.addAll(log2.logbooks);
        this.attributes.addAll(log1.attributes);
        //this.attributes.addAll(log2.attributes);
    }

    public Log(Long id, String version, String owner, String source, Level level, State state, Date modifiedDate, String description, Entry entry) {
        this.id = id;
        this.version = version;
        this.owner = owner;
        this.source = source;
        this.level = level;
        this.state = state;
        this.modifiedDate = modifiedDate;
        this.description = description;
        this.entry = entry;
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
     * Getter for log's Logbooks.
     *
     * @return Logbooks
     */
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
     * @return the entry
     */
    public Entry getEntry() {
        return entry;
    }

    /**
     * @param entry the entry to set
     */
    public void setEntry(Entry entry) {
        this.entry = entry;
    }
    
    public Set<LogAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<LogAttribute> attributes) {
        this.attributes = attributes;
    }

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
