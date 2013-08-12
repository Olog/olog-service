/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import java.io.Serializable;


/**
 *
 * @author berryman
 */
public class LogAttributeId implements Serializable {
    
    private Long id;

    private Long logId;

    private Long attributeId;

    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 89 * hash + (this.logId != null ? this.logId.hashCode() : 0);
        hash = 89 * hash + (this.attributeId != null ? this.attributeId.hashCode() : 0);
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
        final LogAttributeId other = (LogAttributeId) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.logId != other.logId && (this.logId == null || !this.logId.equals(other.logId))) {
            return false;
        }
        if (this.attributeId != other.attributeId && (this.attributeId == null || !this.attributeId.equals(other.attributeId))) {
            return false;
        }
        return true;
    }

}
