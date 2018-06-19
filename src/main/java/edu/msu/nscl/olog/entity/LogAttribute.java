/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog.entity;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author berryman
 */
@Entity
@Table(name = "logs_attributes")
@IdClass(LogAttributeId.class)
public class LogAttribute implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Id
    @Column(name = "log_id")
    private Long logId;
    @Id
    @Column(name = "attribute_id")
    private Long attributeId;
    @Column(name = "value")
    private String value;
    @Column(name = "grouping_num")
    private Long groupingNum;
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "log_id", referencedColumnName = "id")
    private Log log;
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "attribute_id", referencedColumnName = "id")
    private Attribute attribute;

    public LogAttribute() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }

    public Long getGroupingNum() {
        return groupingNum;
    }

    public void setGroupingNum(Long groupingNum) {
        this.groupingNum = groupingNum;
    }

    @XmlTransient
    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogAttribute)) return false;

        LogAttribute that = (LogAttribute) o;

        if (!attributeId.equals(that.attributeId)) return false;
        if (groupingNum != null ? !groupingNum.equals(that.groupingNum) : that.groupingNum != null) return false;
        if (!id.equals(that.id)) return false;
        if (logId != null ? !logId.equals(that.logId) : that.logId != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (logId != null ? logId.hashCode() : 0);
        result = 31 * result + attributeId.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (groupingNum != null ? groupingNum.hashCode() : 0);
        return result;
    }
}
