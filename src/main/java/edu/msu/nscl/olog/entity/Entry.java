/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog.entity;

import edu.msu.nscl.olog.entity.bitemporal.WrappedBitemporalProperty;
import edu.msu.nscl.olog.entity.bitemporal.WrappedValueAccessor;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import javax.persistence.*;
import org.joda.time.Interval;

/**
 *
 * @author berryman
 */
@Entity
@Table(name = "entries")
public class Entry implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL , orphanRemoval=true)
    @JoinColumn(name="log")
    private Collection<BitemporalLog> logs = new LinkedList<BitemporalLog>();

    @PrePersist
    public void setUpdated() {
        this.setCreatedDate(new Date());
    }
    
    public WrappedBitemporalProperty<Log, BitemporalLog> log() {
        return new WrappedBitemporalProperty<Log, BitemporalLog>(logs,
                new WrappedValueAccessor<Log, BitemporalLog>() {
                    @Override
                    public BitemporalLog wrapValue(Log value, Interval validityInterval) {
                        return new BitemporalLog(value, validityInterval);
                    }
                });
    }
    
    public void addLog(Log log){
        this.log().set(log);
    }

    /**
     * Creates a new instance of Entry
     */
    public Entry() {
    }

    /**
     * Getter for entry id.
     *
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * Setter for entry id.
     *
     * @param logId
     */
    public void setId(Long logId) {
        this.id = logId;
    }

    /**
     * Getter for entry created date.
     *
     * @return createdDate
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * Setter for entry created date.
     *
     * @param createdDate
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entry)) return false;

        Entry entry = (Entry) o;

        if (createdDate != null ? !createdDate.equals(entry.createdDate) : entry.createdDate != null) return false;
        if (!id.equals(entry.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        return result;
    }
}
