/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.msu.nscl.olog;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Attachment object that can be represented as XML/JSON in payload data.
 * TODO: pass attachments over XML / without webdav? make log entries with attachments atomic?
 * @author Eric Berryman
 */


@XmlType
@XmlRootElement(name = "attachment")
public class XmlAttachment {
    @XmlTransient
    protected String fileName;

    @XmlTransient
    protected String contentType;
    
    @XmlTransient
    protected Boolean thumbnail;
    
    @XmlTransient
    protected Long fileSize;

    /**
     * Creates a new instance of XmlAttachment
     */
    public XmlAttachment() {
        this.thumbnail = false;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
    	return fileName;
    }

    /**
     * @param fileName
     *            the fileName to set
     */
    public void setFileName(String fileName) {
    	this.fileName = fileName;
    }
    
        /**
     * @return the fileSize
     */
    public Long getFileSize() {
    	return fileSize;
    }

    /**
     * @param fileSize
     *            the fileSize to set
     */
    public void setFileSize(Long fileSize) {
    	this.fileSize = fileSize;
    }

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType
     *            the contentType to set
     */
    public void setContentType(String contentType) {
	this.contentType = contentType;
    }
    
        /**
     * @return the thumbnail name
     */
    public boolean getThumbnail() {
        return thumbnail;
    }

    /**
     * @param thumbnail name
     *            the contentType to set
     */
    public void setThumbnail(Boolean thumbnail) {
	this.thumbnail = thumbnail;
    }

    /**
     * Creates a compact string representation for the log.
     *
     * @param data the XmlAttach to log
     * @return string representation for log
     */
    public static String toLogger(XmlAttachment data) {
        return data.getFileName() + "(" + data.getContentType() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XmlAttachment)) return false;

        XmlAttachment that = (XmlAttachment) o;

        if (contentType != null ? !contentType.equals(that.contentType) : that.contentType != null) return false;
        if (fileName != null ? !fileName.equals(that.fileName) : that.fileName != null) return false;
        if (fileSize != null ? !fileSize.equals(that.fileSize) : that.fileSize != null) return false;
        if (thumbnail != null ? !thumbnail.equals(that.thumbnail) : that.thumbnail != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fileName != null ? fileName.hashCode() : 0;
        result = 31 * result + (contentType != null ? contentType.hashCode() : 0);
        result = 31 * result + (thumbnail != null ? thumbnail.hashCode() : 0);
        result = 31 * result + (fileSize != null ? fileSize.hashCode() : 0);
        return result;
    }
}
