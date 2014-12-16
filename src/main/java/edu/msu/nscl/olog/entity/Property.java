/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010-2011 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * All rights reserved. Use is subject to license terms and conditions.
 */
package edu.msu.nscl.olog.entity;

import java.io.Serializable;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

/**
 * Property object that can be represented as XML/JSON in payload data.
 *
 * @author Eric Berryman taken from Ralph Lange
 * <Ralph.Lange@helmholtz-berlin.de>
 */
@Entity
@Table(name = "properties")
@XmlRootElement(name = "property")
public class Property implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Transient
    private int groupingNum;
    @Column(name = "name", nullable = false)
    private String name = null;
    @Enumerated(EnumType.STRING)
    private State state;
    @OneToMany(mappedBy = "property")
    private Set<Attribute> attributes = new HashSet<Attribute>();

    /**
     * Creates a new instance of Property.
     *
     */
    public Property() {
    }

    /**
     * Creates a new instance of Property.
     *
     * @param name
     * @param value
     */
    public Property(String name) {
        this.name = name;
    }

    /**
     * @param name
     * @param attributes
     */
    public Property(String name, Set<Attribute> attributes) {
        this.name = name;
        this.attributes = attributes;
    }

    /**
     * Getter for property id.
     *
     * @return property id
     */
    @XmlAttribute
    public Long getId() {
        return id;
    }

    /**
     * Setter for property id.
     *
     * @param id property id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter for property id.
     *
     * @return property id
     */
    @XmlAttribute
    public int getGroupingNum() {
        return groupingNum;
    }

    /**
     * Setter for property id.
     *
     * @param id property id
     */
    public void setGroupingNum(int groupingNum) {
        this.groupingNum = groupingNum;
    }

    /**
     * Getter for property name.
     *
     * @return property name
     */
    @XmlAttribute
    public String getName() {
        return name;
    }

    /**
     * Setter for property name.
     *
     * @param name property name
     */
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

    /**
     * @return the attributes
     */
    @XmlTransient
    public Set<Attribute> getAttributes() {
        return attributes;
    }

    @XmlElementRef(type = Attribute.class, name = "attribute")
    public Set<Attribute> getAttributeSet() {
        return attributes;
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(Set<Attribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * Adds a attribute to the collection.
     *
     * @param attribute
     */
    public void addAttribute(Attribute attribute) {
        this.attributes.add(attribute);
    }

    public XmlProperty toXmlProperty() {

        XmlProperty xmlProperty = new XmlProperty();
        HashMap<String, String> map = new HashMap<String, String>();
        xmlProperty.setName(this.getName());
        xmlProperty.setId(this.getId());
        for (Attribute attr : this.getAttributes()) {
            if (attr.getState() == State.Active) {
                map.put(attr.getName(), null);
            }
        }
        xmlProperty.setAttributes(map);
        return xmlProperty;
    }

    /**
     * Creates a compact string representation for the log.
     *
     * @param data the Property to log
     * @return string representation for log
     */
    public static String toLogger(Property data) {
        if (data.attributes == null) {
            return data.getName();
        } else {
            return data.getName() + "(" + data.getAttributes().toString() + ")";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Property)) return false;

        Property property = (Property) o;

        if (groupingNum != property.groupingNum) return false;
        if (!id.equals(property.id)) return false;
        if (name != null ? !name.equals(property.name) : property.name != null) return false;
        if (state != property.state) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + groupingNum;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        return result;
    }
}
