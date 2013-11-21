/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author berryman
 */
@Entity
@Table(name = "entries")
public class Entry implements Serializable, Comparable<Entry> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "created", nullable = false, insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @OneToMany(mappedBy = "entry", fetch=FetchType.EAGER)
    private List<Log> logs = new ArrayList<Log>();

    @PrePersist
    public void setUpdated() {
        this.setCreatedDate(new Date());
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

    /**
     * @return the logs
     */
    public List<Log> getLogs() {
        return logs;
    }

    /**
     * @param logs the logs to set
     */
    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }

    /**
     * Adds a log to the collection.
     *
     * @param log
     */
    public void addLog(Log log) {
        this.logs.add(log);
    }

    public int compareTo(Entry num) {
        int x = createdDate.compareTo(num.createdDate);
        return x;
    }
}
