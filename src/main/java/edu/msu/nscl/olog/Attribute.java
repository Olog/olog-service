/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author berryman
 */
@Entity
@Table(name = "attributes")
@XmlRootElement(name = "attribute")
public class Attribute implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name", nullable = false)
    private String name = null;
    @Enumerated(EnumType.STRING)
    private State state;
    @OneToMany(mappedBy = "attribute", cascade = CascadeType.PERSIST)
    private Set<LogAttribute> logs;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "property_id", unique = true)
    private Property property;

    public Attribute() {
    }

    public Attribute(String name) {
        this.name = name;
    }

    public Attribute(String name, Set<LogAttribute> logs) {
        this.name = name;
        this.logs = logs;
    }

    @XmlAttribute
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @XmlTransient
    public Set<LogAttribute> getLogs() {
        return logs;
    }

    public void setLogs(Set<LogAttribute> logs) {
        this.logs = logs;
    }

    @XmlTransient
    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attribute)) return false;

        Attribute attribute = (Attribute) o;

        if (!id.equals(attribute.id)) return false;
        if (name != null ? !name.equals(attribute.name) : attribute.name != null) return false;
        if (state != attribute.state) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        return result;
    }
}
