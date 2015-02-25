/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.annotation.*;

/**
 * Property object that can be represented as XML/JSON in payload data.
 *
 * @author berryman
 */
@XmlRootElement(name = "property")
public class XmlProperty {

    private Long id;
    private String name = null;
    private Map<String, String> attributes = new HashMap<String, String>();;
    private HashSet<XmlLog> logs = new HashSet<XmlLog>();

    /**
     * Creates a new instance of XmlProperty.
     *
     */
    public XmlProperty() {
    }

    /**
     * Creates a new instance of XmlProperty.
     *
     * @param name
     * @param value
     */
    public XmlProperty(String name) {
        this.name = name;
    }

    /**
     * @param name
     * @param attributes
     */
    public XmlProperty(String name, Map<String, String> attributes) {
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

    /**
     * @return the attributes
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    /**
     * Getter for property's Logs.
     *
     * @return XmlChannels object
     */
    //@XmlJavaTypeAdapter(XmlLogAdapter.class)
    @XmlTransient
    @JsonIgnore
    public HashSet<XmlLog> getLogs() {
        return logs;
    }

    /**
     * Setter for property's Logs.
     *
     * @param logs Logs object
     */
    public void setLogs(HashSet<XmlLog> logs) {
        this.logs = logs;
    }

    public Property toProperty() {
        Property prop = new Property(this.getName());
        for (Map.Entry<String, String> att : this.getAttributes().entrySet()) {
            Attribute newAtt = new Attribute(att.getKey());
            newAtt.setState(State.Active);
            newAtt.setProperty(prop);
            prop.addAttribute(newAtt);
        }
        prop.setId(this.getId());
        prop.setState(State.Active);
        return prop;
    }

    /**
     * Creates a compact string representation for the log.
     *
     * @param data the XmlProperty to log
     * @return string representation for log
     */
    public static String toLogger(XmlProperty data) {
        if (data.logs == null) {
            return data.getName() + "(" + data.getAttributes().toString() + ")";
        } else {
            return data.getName() + "(" + data.getAttributes().toString() + ")"
                    + XmlLogs.toLogger(data.logs);
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.id);
        hash = 37 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final XmlProperty other = (XmlProperty) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    

}
