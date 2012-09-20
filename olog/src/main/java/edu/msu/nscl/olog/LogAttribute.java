/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

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
}
