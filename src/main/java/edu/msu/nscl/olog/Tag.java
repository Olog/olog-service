/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import java.io.Serializable;
import java.util.*;
import javax.persistence.*;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author berryman
 */
@Entity
@Table(name = "logbooks")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "is_tag", discriminatorType = DiscriminatorType.INTEGER)
@XmlType(propOrder = {"id", "name", "logs"})
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
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private List<Log> logs = new Logs();

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
    @XmlElement
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
     * Getter for tag's Logs.
     *
     * @return logs Logs object
     */
    @XmlJavaTypeAdapter(XmlLogAdapter.class)
    public Logs getLogs() {
        return new Logs(logs);
    }

    /**
     * Setter for tag's Logs.
     *
     * @param logs Logs object
     */
    public void setLogs(Logs logs) {
        this.logs = logs;
    }

    public void addLog(Log item) {
        this.logs.add(item);
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
