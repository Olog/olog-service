/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author berryman
 */
@Entity
@Table(name = "levels")
public class Level implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    
    @Column(name = "name", nullable = false, length = 250, insertable = true)
    private String name;

    @OneToMany(mappedBy = "level", fetch = FetchType.EAGER)
    private Collection<Log> logs;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
        /**
     * Getter for level's Logs.
     *
     * @return logs Logs object
     */
    @XmlTransient
    public Logs getLogs() {
        if (logs != null) {
            Iterator<Log> iterator = logs.iterator();
            Logs xmlLogs = new Logs();
            while (iterator.hasNext()) {
                xmlLogs.addXmlLog(iterator.next());
            }
            return xmlLogs;
        } else {
            return null;
        }
    }

    /**
     * Setter for level's Logs.
     *
     * @param logs Logs object
     */
    public void setLogs(Logs logs) {
        for (Log xmlLog : logs.getLogs()) {
            this.logs.add(xmlLog);
        }
    }
}
