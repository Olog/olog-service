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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author berryman
 */
@Entity
@Table(name = "logbooks")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "is_tag", discriminatorType = DiscriminatorType.INTEGER)
@XmlType(propOrder = {"id", "name"})
@DiscriminatorValue("1")
@XmlRootElement(name = "tag")
public class Tag implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id = null;
    @Column(name = "name", nullable = false, length = 250, insertable = true)
    private String name = null;
    @Enumerated(EnumType.STRING)
    private State state;
    //for joing the tables (many-to-many)
    @ManyToMany(mappedBy = "tags", fetch = FetchType.EAGER)
    private Collection<Log> logs;

    public Tag() {
    }

    /**
     * Creates a new instance of Tag.
     *
     * @param name
     */
    public Tag(String name) {
        this.name = name;
    }

    /**
     * Getter for tag id.
     *
     * @return id tag id
     */
    public Long getId() {
        return id;
    }

    /**
     * Setter for tag id.
     *
     * @param id tag id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter for tag name.
     *
     * @return name tag name
     */
    @XmlAttribute
    public String getName() {
        return name;
    }

    /**
     * Setter for tag name.
     *
     * @param name tag name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the status
     */
    @XmlTransient
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
     * Getter for tag's Logs.
     *
     * @return logs Logs object
     */
    //@XmlElement(name = "logs")
    @XmlTransient
    public Logs getLogs() {
        if (logs != null) {
            Iterator<Log> iterator = logs.iterator();
            Logs xmlLogs = new Logs();
            while (iterator.hasNext()) {
                xmlLogs.addLog(iterator.next());
            }
            return xmlLogs;
        } else {
            return null;
        }
    }

    /**
     * Setter for tag's Logs.
     *
     * @param logs Logs object
     */
    public void setLogs(Logs logs) {
        for (Log xmlLog : logs.getLogs()) {
            this.logs.add(xmlLog);
        }
    }

    /**
     * Creates a compact string representation for the log.
     *
     * @param data the Label to log
     * @return string representation for log
     */
    public static String toLogger(Tag data) {
        return data.getName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0
                : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Tag other = (Tag) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
}
