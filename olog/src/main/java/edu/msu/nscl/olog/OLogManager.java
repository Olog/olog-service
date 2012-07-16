/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */
package edu.msu.nscl.olog;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jcr.*;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.JcrConstants;

/**
 * Central business logic layer that implements all directory operations.
 * 
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
public class OLogManager {

    private static OLogManager instance = new OLogManager();

    public static class Attachment {

        public Attachment() {
        }
        
        private InputStream content;
        private String mimeType;
        private String encoding;
        private String fileName;
        private Long fileSize;
        
        public void setContent(InputStream content){
            this.content = content;
        }
        
        public InputStream getContent(){
            return content;
        }
        
        public void setMimeType(String mimeType){
            this.mimeType = mimeType;
        }
        
        public String getMimeType(){
            return mimeType;
        }
        
        public void setFileName(String fileName){
            this.fileName = fileName;
        }
        
        public String getFileName(){
            return fileName;
        }
        
        public void setFileSize(Long fileSize){
            this.fileSize = fileSize;
        }
        
        public Long getFileSize(){
            return fileSize;
        }
        
        public void setEncoding(String encoding){
            this.encoding = encoding;
        }
        
        public String getEncoding(){
            return encoding;
        }
    }

    /**
     * Create an instance of OlogManager
     */
    private OLogManager() {
    }

    /**
     * Returns the (singleton) instance of OlogManager
     *
     * @return the instance of OlogManager
     */
    public static OLogManager getInstance() {
        return instance;
    }

    /**
     * Merges XmlLogbooks and XmlTags of two logs in place
     *
     * @param dest destination log
     * @param src source log
     */
    public static void mergeXmlLogs(XmlLog dest, XmlLog src) {
//        if(src.getSubject() != null)
//            dest.setSubject(src.getSubject());
        if (src.getDescription() != null) {
            dest.setDescription(src.getDescription());
        }
        if (src.getLevel() != null) {
            dest.setLevel(src.getLevel());
        }
        src_logbooks:
        for (XmlLogbook s : src.getXmlLogbooks()) {
            for (XmlLogbook d : dest.getXmlLogbooks()) {
                if (d.getName().equals(s.getName())) {
                    continue src_logbooks;
                }
            }
            dest.getXmlLogbooks().add(s);
        }
        src_tags:
        for (XmlTag s : src.getXmlTags()) {
            for (XmlTag d : dest.getXmlTags()) {
                if (d.getName().equals(s.getName())) {
//TODO: here                   d.setState(s.getState());
                    continue src_tags;
                }
            }
            dest.getXmlTags().add(s);
        }
        src_properties:
        for (XmlProperty s : src.getXmlProperties()) {
            for (XmlProperty d : dest.getXmlProperties()) {
                if (d.getName().equals(s.getName())) {
                    d.setAttributes(s.getAttributes());
                    continue src_properties;
                }
            }
            dest.getXmlProperties().add(s);
        }
    }

    /**
     * Return single log found by log id.
     *
     * @param logId id to look for
     * @return XmlLog with found log and its logbooks
     * @throws CFException on SQLException
     */
    public XmlLog findLogById(Long logId) throws CFException, UnsupportedEncodingException, NoSuchAlgorithmException {
        return FindLogsQuery.findLogById(logId);
    }

    /**
     * Return logs found by matching tags against a collection of name patterns.
     *
     * @param name matches collection of name patterns to match
     * @return XmlLogs container with all found logs and their logbooks
     * @throws CFException wrapping an SQLException
     */
    public XmlLogs findLogsByLogbookName(String name) throws CFException {
        return FindLogsQuery.findLogsByLogbookName(name);
    }

    /**
     * Returns logs found by matching logbook names, tag names, log description.
     *
     * @param matches multivalued map of logbook, tag, log names and patterns to match
     * their values against.
     * @return XmlLogs container with all found logs and their logbooks
     * @throws CFException wrapping an SQLException
     */
    public XmlLogs findLogsByMultiMatch(MultivaluedMap<String, String> matches) throws CFException, RepositoryException, UnsupportedEncodingException, NoSuchAlgorithmException {
        return FindLogsQuery.findLogsByMultiMatch(matches);
    }

    /**
     * Deletes a log identified by <tt>logId</tt>.
     *
     * @param logId log to delete
     * @throws CFException wrapping an SQLException
     */
    public void removeLog(Long logId) throws CFException {
        DeleteLogQuery.deleteLogIgnoreNoexist(logId);
    }

    /**
     * Deletes a log identified by <tt>logId</tt>.
     *
     * @param logId log to delete
     * @throws CFException wrapping an SQLException
     */
    public void removeExistingLog(Long logId) throws CFException {
        DeleteLogQuery.deleteLogFailNoexist(logId);
    }

    /**
     * List all Logbooks in the database.
     *
     * @throws CFException wrapping an SQLException
     */
    public XmlLogbooks listLogbooks() throws CFException {
        return ListLogbooksQuery.getLogbooks();
    }

    /**
     * Return single logbook found by name.
     *
     * @param name name to look for
     * @return XmlLogbook with found logbook and its logs
     * @throws CFException on SQLException
     */
    public XmlLogbook findLogbookByName(String name) throws CFException {
        XmlLogbook p = ListLogbooksQuery.findLogbook(name);
        if (p != null) {
            XmlLogs c = FindLogsQuery.findLogsByLogbookName(name);
            if (c != null) {
                p.setXmlLogs(c);
            }
        }
        return p;
    }

    /**
     * Add the logbook identified by <tt>logbook</tt> to the logs
     * specified in the XmlLogs <tt>data</tt>.
     *
     * @param logbook logbook to add
     * @param data XmlLogbook data with all logs
     * @throws CFException on ownership mismatch, or wrapping an SQLException
     */
    public XmlLogbook updateLogbook(String logbook, XmlLogbook data) throws CFException {
        return UpdateValuesQuery.updateLogbook(logbook, data);
    }

    /**
     * Adds the logbook identified by <tt>tag</tt> <b>exclusively</b>
     * to the logs specified in the XmlLogbook payload <tt>data</tt>, creating it
     * if necessary.
     *
     * @param logbook logbook to add
     * @param data XmlLogbook container with all logs to add logbook to
     * @throws CFException on ownership mismatch, or wrapping an SQLException
     */
    public XmlLogbook createOrReplaceLogbook(String logbook, XmlLogbook data) throws CFException {
        DeleteLogbookQuery.removeLogbook(logbook);
        CreateLogbookQuery.createLogbook(data.getName(), data.getOwner());
        return UpdateValuesQuery.updateLogbook(data.getName(), data);
    }

    /**
     * Create or replace logbooks specified in <tt>data</tt>.
     *
     * @param data XmlLogbooks data
     * @throws CFException on ownership mismatch, or wrapping an SQLException
     */
    public XmlLogbooks createOrReplaceLogbooks(XmlLogbooks data) throws CFException {
        XmlLogbooks xmlLogbooks = null;
        for (XmlLogbook logbook : data.getLogbooks()) {
            removeLogbook(logbook.getName());
            xmlLogbooks.addXmlLogbook(createOrReplaceLogbook(logbook.getName(), logbook));
        }
        return xmlLogbooks;
    }

    /**
     * Add the logbook identified by <tt>logbook</tt>
     * to the single log <tt>id</tt>.
     *
     * TODO: this couldn't work as stated
     *
     * @param logbook logbook to add
     * @param logId log to add the logbook to
     * @param data XmlLogbook
     * @throws CFException on ownership mismatch, or wrapping an SQLException
     */
    public XmlLogbook addSingleLogbook(String logbook, Long logId) throws CFException {
        return UpdateValuesQuery.updateLogbookWithLog(logbook, logId);
    }

    /**
     * Deletes a logbook identified by <tt>name</tt> from all logs.
     *
     * @param logbook tag to delete
     * @throws CFException wrapping an SQLException
     */
    public void removeLogbook(String logbook) throws CFException {
        DeleteLogbookQuery.removeLogbook(logbook);
    }

    /**
     * Deletes a logbook identified by <tt>name</tt> from all logs, failing if
     * the logbook does not exist.
     *
     * @param logbook tag to delete
     * @throws CFException wrapping an SQLException or on failure
     */
    public void removeExistingLogbook(String logbook) throws CFException {
        DeleteLogbookQuery.removeExistingLogbook(logbook);
    }

    /**
     * Deletes a logbook identified by <tt>name</tt> from a single log.
     *
     * @param logbook tag to delete
     * @param logId log to delete it from
     * @throws CFException wrapping an SQLException
     */
    public void removeSingleLogbook(String logbook, Long logId) throws CFException {
        DeleteLogbookQuery.deleteOneValue(logbook, logId);
    }

    /**
     * List all tags in the database.
     *
     * @throws CFException wrapping an SQLException
     */
    public XmlTags listTags() throws CFException {
        return ListLogbooksQuery.getTags();
    }

    /**
     * Return single tag found by name.
     *
     * @param name name to look for
     * @return XmlTag with found tag and its logs/state
     * @throws CFException on SQLException
     */
    public XmlTag findTagByName(String name) throws CFException {
        XmlTag t = ListLogbooksQuery.findTag(name);
        if (t != null) {
            XmlLogs c = FindLogsQuery.findLogsByLogbookName(name);
            if (c != null) {
                t.setXmlLogs(c);
            }
        }
        return t;
    }

    /**
     * Add the tag identified by <tt>tag</tt> and <tt>state</tt> to the logs
     * specified in the XmlLogs <tt>data</tt>.
     *
     * @param tag tag to add
     * @param data XmlTag with list of all logs to add tag to
     * @throws CFException on ownership mismatch, or wrapping an SQLException
     */
    public XmlTag updateTag(String tag, XmlTag data) throws CFException {
        return UpdateValuesQuery.updateTag(tag, data);
    }

    /**
     * Adds the tag identified by <tt>tag</tt> <b>exclusively</b>
     * to the logs specified in the XmlTag payload <tt>data</tt>, creating it
     * if necessary.
     *
     * @TODO Right now, this is only a create tag; not create/replace.
     *       To Delete, and recreate log key ids every time is too expensive
     * @param tag tag to add
     * @param data XmlTag container with all logs to add tag to
     * @throws CFException on ownership mismatch, or wrapping an SQLException
     */
    public XmlTag createOrReplaceTag(String tag, XmlTag data) throws CFException {
        DeleteLogbookQuery.removeLogbook(tag);
        CreateLogbookQuery.createTag(data.getName());
        return UpdateValuesQuery.updateTag(data.getName(), data);
    }

    /**
     * Create tags specified in <tt>data</tt>.
     *
     * @param data XmlTags data
     * @throws CFException on ownership mismatch, or wrapping an SQLException
     */
    public XmlTags createOrReplaceTags(XmlTags data) throws CFException {
        XmlTags xmlTags = null;
        for (XmlTag tag : data.getTags()) {
            removeTag(tag.getName());
            xmlTags.addXmlTag(createOrReplaceTag(tag.getName(), tag));
        }
        return xmlTags;
    }

    /**
     * Add the tag identified by <tt>tag</tt> to the single log <tt>logId</tt>.
     *
     * @param tag tag to add
     * @param logId
     * @throws CFException on ownership mismatch, or wrapping an SQLException
     */
    public XmlTag addSingleTag(String tag, Long logId) throws CFException {
        return UpdateValuesQuery.updateTag(tag, logId);
    }

    /**
     * Deletes a tag identified by <tt>name</tt> from all logs.
     *
     * @param tag tag to delete
     * @throws CFException wrapping an SQLException
     */
    public void removeTag(String tag) throws CFException {
        DeleteLogbookQuery.removeLogbook(tag);
    }

    /**
     * Deletes a tag identified by <tt>name</tt> from a single log.
     *
     * @param tag tag to delete
     * @param logId log to delete it from
     * @throws CFException wrapping an SQLException
     */
    public void removeSingleTag(String tag, Long logId) throws CFException {
        DeleteLogbookQuery.deleteOneValue(tag, logId);
    }

    /**
     * Return a list of all properties
     *
     * @param 
     * @throws CFException wrapping an SQLException
     */
    public XmlProperties listProperties() throws CFException {
        return ListPropertiesQuery.getProperties();
    }

    /**
     * Return a list of attributes for a given property
     *
     * @param 
     * @throws CFException wrapping an SQLException
     */
    XmlProperty listAttributes(String property) throws CFException {
        return ListPropertiesQuery.getAttributes(property);
    }

    /**
     * Adds a new property.
     *
     * @param String newProperty name of the new property
     * @param XmlProperty data incoming payload
     * @param boolean destructive is this action to be destructive (true) or not
     * @throws CFException wrapping an SQLException
     */
    XmlProperty addProperty(String newProperty, XmlProperty data, boolean destructive) throws CFException {
        return CreatePropertyQuery.addProperty(newProperty, data, destructive);
    }

    /**
     * Adds a new property attribute to a log entry.
     *
     * @param String hostAddress IP from where the call is coming from
     * @param String property name of the property
     * @param Long logId log that the property attribute will be associated with
     * @param XmlProperty data incoming payload
     * @throws CFException wrapping an SQLException
     */
    XmlLog addAttribute(String hostAddress, String property, Long logId, XmlProperty data) throws CFException, UnsupportedEncodingException, NoSuchAlgorithmException {
        XmlLog currentLog = findLogById(logId);

        Collection<XmlProperty> currentProperties = currentLog.getXmlProperties();
        currentProperties.add(data);
        currentLog.setXmlProperties(currentProperties);

        return createOrReplaceLog(hostAddress, logId, currentLog);
    }

    /**
     * Remove a property.
     *
     * @param String property name of the property to be removed
     * @throws CFException wrapping an SQLException
     */
    void removeProperty(String property, XmlProperty data) throws CFException {
        DeletePropertyQuery.removeProperty(property, data);
    }

    /**
     * Removes a properties attribute from a log entry.
     *
     * @param String hostAddress IP from where the call is coming from
     * @param String property name of the property
     * @param XmlProperty data incoming payload
     * @throws CFException wrapping an SQLException
     */
    XmlLog removeAttribute(String hostAddress, String property, Long logId, XmlProperty data) throws CFException, UnsupportedEncodingException, NoSuchAlgorithmException {
        XmlLog currentLog = findLogById(logId);

        // Remove attributes from incoming payload from the current log
        Collection<XmlProperty> currentProperties = currentLog.getXmlProperties();
        if (currentProperties.isEmpty() || currentProperties == null) {
            throw new CFException(Response.Status.NOT_FOUND,
                    "Log entry " + logId + " could not be updated: Property '" + data.getName() + "' is not associated with this log.");
        }
        for (XmlProperty prop : currentProperties) {
            if (prop.getName().equals(data.getName())) {
                Map<String, String> attributes = prop.getAttributes();
                Set<String> attributesToDelete = data.getAttributes().keySet();
                for (String attr : attributesToDelete) {
                    if (attributes.containsKey(attr)) {
                        attributes.remove(attr);
                    }
                }
                prop.setAttributes(attributes);
            } else {
                throw new CFException(Response.Status.NOT_FOUND,
                        "Log entry " + logId + " could not be updated: Property '" + data.getName() + "' is not associated with this log.");
            }
        }
        currentLog.setXmlProperties(currentProperties);

        // Create new log with the correct properties removed
        return createOrReplaceLog(hostAddress, logId, currentLog);
    }

    /**
     * Update a log identified by <tt>logId</tt>, creating it when necessary.
     * The logbook set in <tt>data</tt> has to be complete, i.e. the existing
     * log logbooks are <b>replaced</b> with the logbooks in <tt>data</tt>.
     *
     * @param logId log to update
     * @param data XmlLog data
     * @throws CFException on ownership or name mismatch, or wrapping an SQLException
     */
    public XmlLog createOrReplaceLog(String hostAddress, Long logId, XmlLog data) throws CFException, UnsupportedEncodingException, NoSuchAlgorithmException {
        data.setSource(hostAddress);
        DeleteLogQuery.deleteLogIgnoreNoexist(logId);
        return CreateLogQuery.createLog(data);
    }

    /**
     * Create logs specified in <tt>data</tt>.
     *
     * @param data XmlLogs data
     * @throws CFException on ownership mismatch, or wrapping an SQLException
     */
    public XmlLogs createOrReplaceLogs(String hostAddress, XmlLogs data) throws CFException, UnsupportedEncodingException, NoSuchAlgorithmException {
        XmlLogs xmlLogs = new XmlLogs();
        for (XmlLog log : data.getLogs()) {
            log.setSource(hostAddress);
            removeLog(log.getId());
            xmlLogs.addXmlLog(createOneLog(log));
        }
        return xmlLogs;
    }

    /**
     * Create a new log using the logbook set in <tt>data</tt>.
     *
     * @param data XmlLog data
     * @throws CFException on ownership or name mismatch, or wrapping an SQLException
     */
    private XmlLog createOneLog(XmlLog data) throws CFException, UnsupportedEncodingException, NoSuchAlgorithmException {
        return CreateLogQuery.createLog(data);
    }

    /**
     * Merge logbook set in <tt>data</tt> into the existing log <tt>logId</tt>.
     *
     * @param logId log to merge the logbooks and tags into
     * @param data XmlLog data containing logbooks and tags
     * @throws CFException on name or owner mismatch, or wrapping an SQLException
     */
    public XmlLog updateLog(String hostAddress, Long logId, XmlLog data) throws CFException, UnsupportedEncodingException, NoSuchAlgorithmException {
        XmlLog dest = findLogById(logId);
        if (dest == null) {
            throw new CFException(Response.Status.NOT_FOUND,
                    "Log entry " + logId + " could not be updated: Does not exist");
        }
        dest.setId(data.getId());
        dest.setOwner(data.getOwner());
        mergeXmlLogs(dest, data);
        return createOrReplaceLog(hostAddress, logId, dest);
    }
    
    public XmlAttachment createOrReplaceAttachment(Attachment attachment, Long logId) throws CFException {
        XmlAttachment result = new XmlAttachment();
        try {
            Session session = ContentRepository.getSession();
            ValueFactory valueFactory = session.getValueFactory();
            Node rn = session.getRootNode();
            String mimeType = attachment.getMimeType();
            String fileName = attachment.getFileName();
            Long fileSize = attachment.getFileSize();
            final int ndx = fileName.lastIndexOf(".");
            final String extension = fileName.substring(ndx + 1);
            InputStream stream;
            
            if(attachment.getEncoding().equalsIgnoreCase("base64")){
             StringWriter writer = new StringWriter();
                IOUtils.copy(attachment.getContent(), writer, null);
                stream = new ByteArrayInputStream(DatatypeConverter.parseBase64Binary(writer.toString()));
            }else{
                stream = attachment.getContent();
            }
            
            if (mimeType == null) mimeType = "application/octet-stream";
            
            Node folderNode;
            
            if(rn.hasNode(logId.toString())){
                folderNode = rn.getNode(logId.toString());
                if (!folderNode.isNodeType(JcrConstants.NT_FOLDER))
                    folderNode = rn.addNode(logId.toString(), JcrConstants.NT_FOLDER);
            } else {
                folderNode = rn.addNode(logId.toString(), JcrConstants.NT_FOLDER);
            }
            Node fileNode  = folderNode.addNode(fileName, JcrConstants.NT_FILE);
            Node resNode = fileNode.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);
            resNode.setProperty(JcrConstants.JCR_MIMETYPE, mimeType);
            resNode.setProperty(JcrConstants.JCR_ENCODING, "");

            Binary binFile = valueFactory.createBinary(stream);
            resNode.setProperty(JcrConstants.JCR_DATA, binFile);
            
            // Add thumbnail
            if ((extension.equals("jpeg") || extension.equals("jpg")
                                          || extension.equals("gif") 
                                          || extension.equals("png"))) {
                Node tfolderNode;
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Thumbnails.of(binFile.getStream()).size(80, 80).outputFormat(extension).toOutputStream(outputStream);
                InputStream fis = new ByteArrayInputStream(outputStream.toByteArray());
                Binary binThumbnail = valueFactory.createBinary(fis);
                
                if(!rn.hasNode("thumbnails"))
                    rn.addNode("thumbnails", JcrConstants.NT_FOLDER);
                
                if(rn.hasNode("thumbnails/"+logId.toString())){
                    tfolderNode = rn.getNode("thumbnails/"+logId.toString());
                    if (!tfolderNode.isNodeType(JcrConstants.NT_FOLDER))
                        tfolderNode = rn.addNode("thumbnails/"+logId.toString(), JcrConstants.NT_FOLDER);
                } else {
                    tfolderNode = rn.addNode("thumbnails/"+logId.toString(), JcrConstants.NT_FOLDER);
                }
                Node tfileNode  = tfolderNode.addNode(fileName, JcrConstants.NT_FILE);
                Node tresNode = tfileNode.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);
                tresNode.setProperty(JcrConstants.JCR_MIMETYPE, "image/"+extension);
                tresNode.setProperty(JcrConstants.JCR_ENCODING, "");
                tresNode.setProperty(JcrConstants.JCR_DATA, binThumbnail);
                binThumbnail.dispose();
                result.setThumbnail(true);
            }
            binFile.dispose();

            session.save();
            result.setContentType(mimeType);
            result.setFileName(fileName);
            result.setFileSize(fileSize);
            
            return result;
            
        } catch (IOException ex) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "Log entry " + logId.toString() + " could not create thumbnail. " + ex);
        } catch (LoginException ex) {
            throw new CFException(Response.Status.BAD_REQUEST,
                    "Log entry " + logId.toString() + " could not login to repository. " + ex);
        } catch (RepositoryException ex) {
            throw new CFException(Response.Status.CONFLICT,
                    "Log entry " + logId.toString() + " could not put item in repository. " + ex);
        }
    }

    
    public Attachment getAttachment(String filePath, String fileName) throws CFException {
        InputStream content = null;
        String mimeType = null;
        try {
            Session session = ContentRepository.getSession();
            Node rn = session.getRootNode();
            Node folderNode = rn.getNode(filePath);
            Node contentNode = folderNode.getNode(fileName).getNode(JcrConstants.JCR_CONTENT);
            Property dataProperty = contentNode.getProperty(JcrConstants.JCR_DATA);
            Property mimeProperty = contentNode.getProperty(JcrConstants.JCR_MIMETYPE);
            
            mimeType = mimeProperty.getString();

            Binary bin = dataProperty.getBinary();
            content = bin.getStream();
            bin.dispose();
            
        } catch (LoginException ex) {
            throw new CFException(Response.Status.BAD_REQUEST,
                    filePath + ", could not login to repository. " + ex);
        } catch (RepositoryException ex) {
            throw new CFException(Response.Status.NOT_FOUND,
                    filePath + ", could not find item in repository. " + ex);
        }
        Attachment attachment = new Attachment();
        attachment.setContent(content);
        attachment.setMimeType(mimeType);
        
        return attachment;
    }
    
    public XmlAttachments findAttachmentsById(Long logId) throws CFException {
        XmlAttachments xmlAttachments = new XmlAttachments();
        try {
            Session session = ContentRepository.getSession();
            Node rn = session.getRootNode();
            Node folderNode = rn.getNode(logId.toString());        
            NodeIterator nodes = folderNode.getNodes();
            while(nodes.hasNext()){
                Node contentNode = nodes.nextNode();
                String tfileName = contentNode.getName();
                XmlAttachment xmlAttachment = new XmlAttachment();
                xmlAttachment.setFileName(contentNode.getName());
                xmlAttachment.setContentType(contentNode.getNode(JcrConstants.JCR_CONTENT).getProperty(JcrConstants.JCR_MIMETYPE).getString());
                xmlAttachment.setFileSize(contentNode.getNode(JcrConstants.JCR_CONTENT).getProperty(JcrConstants.JCR_DATA).getLength());
                if(rn.hasNode("thumbnails/"+logId.toString()+"/"+tfileName))
                    xmlAttachment.setThumbnail(true);
                xmlAttachments.addXmlAttachment(xmlAttachment);
            }
            return xmlAttachments;
            
        } catch (LoginException ex) {
            throw new CFException(Response.Status.BAD_REQUEST,
                    "Log entry " + logId.toString() + " could not login to repository. " + ex);
        } catch (RepositoryException ex) {
            // TODO: Return Empty set only for javax.jcr.PathNotFoundException
            return xmlAttachments;
            //
            //throw new CFException(Response.Status.NOT_FOUND,
            //        "Log entry " + logId.toString() + " could not find item in repository. " + ex);
        }
    }
    
    public void removeExistingAttachment(String fileName, Long logId) throws CFException {
        XmlAttachments xmlAttachments = new XmlAttachments();
        try {
            Session session = ContentRepository.getSession();
            Node rn = session.getRootNode();
            Node folderNode = rn.getNode(logId.toString()); 
            Node contentNode = folderNode.getNode(fileName);
            contentNode.remove();
            if(rn.hasNode("thumbnails/"+logId.toString()+"/"+fileName)){
                Node tfolderNode = rn.getNode("thumbnails/"+logId.toString());
                Node tcontentNode = tfolderNode.getNode(fileName);
                tcontentNode.remove();
            }
            session.save();
            
        } catch (LoginException ex) {
            throw new CFException(Response.Status.BAD_REQUEST,
                    "Log entry " + logId.toString() + " could not login to repository. " + ex);
        } catch (RepositoryException ex) {
            throw new CFException(Response.Status.NOT_FOUND,
                    "Log entry " + logId.toString() + " could not find item in repository. " + ex);
        }
    }
    
    /**
     * Check that <tt>logId</tt> matches the log id in <tt>data</tt>.
     *
     * @param logId log id to check
     * @param data XmlLog data to check against
     * @throws CFException on name mismatch
     */
    //TODO: fix this
    public void checkIdMatchesPayload(Long logId, XmlLog data) throws CFException {
        //    if (!logId.equals(data.getId())) {
        //        throw new CFException(Response.Status.BAD_REQUEST,
        //                "Specified log id '" + logId
        //               + "' and payload log id '" + data.getId() + "' do not match");
        //   }
    }

    /**
     * Check the log in <tt>data</tt> for valid owner data.
     *
     * @param data XmlLog data to check
     * @throws CFException on error
     */
    public void checkValidOwner(XmlLog data) throws CFException {

        if (data.getOwner() == null || data.getOwner().equals("")) {
            throw new CFException(Response.Status.BAD_REQUEST,
                    "Log entry " + data.getId() + " does not have an owner.");
        }
    }

    /**
     * Check all logs in <tt>data</tt> for valid owner data.
     *
     * @param data XmlLogs data to check
     * @throws CFException on error
     */
    public void checkValidOwner(XmlLogs data) throws CFException {
        if (data == null || data.getLogs() == null) {
            return;
        }
        for (XmlLog c : data.getLogs()) {
            checkValidOwner(c);
        }
    }

    /**
     * Check that <tt>name</tt> matches the tag name in <tt>data</tt>.
     *
     * @param name tag name to check
     * @param data XmlTag data to check against
     * @throws CFException on name mismatch
     */
    public void checkNameMatchesPayload(String name, XmlTag data) throws CFException {
        if (data == null) {
            return;
        }
        if (!name.equals(data.getName())) {
            throw new CFException(Response.Status.BAD_REQUEST,
                    "Tag specified in the URL '" + name
                    + "' and tag specified in the payload '" + data.getName() + "' do not match");
        }
    }

    /**
     * Check the tag in <tt>data</tt> for valid name data.
     *
     * @param data XmlTag data to check
     * @throws CFException on name mismatch
     */
    public void checkValidNameAndOwner(XmlTag data) throws CFException {
        if (data.getName() == null || data.getName().equals("")) {
            throw new CFException(Response.Status.BAD_REQUEST,
                    "The specified tag does not have a name");
        }
    }

    /**
     * Check the property name in <tt>data</tt> with the property name in the URL.
     *
     * @param propertyName name coming from the URL
     * @param data XmlProperty data
     * @throws CFException on name mismatch
     */
    public void checkPropertyName(String propertyName, XmlProperty data) throws CFException {
        if (data.getName() == null || !data.getName().equals(propertyName)) {
            throw new CFException(Response.Status.BAD_REQUEST,
                    "The property name in the URL '" + propertyName
                    + "' and the property name in the payload '" + data.getName()
                    + "' do not match");
        }
    }

    /**
     * Check all tags in <tt>data</tt> for valid name data.
     *
     * @param data XmlTags data to check
     * @throws CFException on error
     */
    public void checkValidNameAndOwner(XmlTags data) throws CFException {
        if (data == null || data.getTags() == null) {
            return;
        }
        for (XmlTag t : data.getTags()) {
            checkValidNameAndOwner(t);
        }
    }

    /**
     * Check that <tt>name</tt> matches the logbook name in <tt>data</tt>.
     *
     * @param name logbook name to check
     * @param data XmlLogbook data to check against
     * @throws CFException on name mismatch
     */
    public void checkNameMatchesPayload(String name, XmlLogbook data) throws CFException {
        if (!name.equals(data.getName())) {
            throw new CFException(Response.Status.BAD_REQUEST,
                    "The logbook in the URL '" + name
                    + "' and the logbook in the payload '" + data.getName() + "' do not match");
        }
    }

    /**
     * Check the logbook in <tt>data</tt> for valid name/owner data.
     *
     * @param data XmlLogbook data to check
     * @throws CFException on error
     */
    public void checkValidNameAndOwner(XmlLogbook data) throws CFException {
        if (data.getName() == null || data.getName().equals("")) {
            throw new CFException(Response.Status.BAD_REQUEST,
                    "Logbook name is empty");
        }
        if (data.getOwner() == null || data.getOwner().equals("")) {
            throw new CFException(Response.Status.BAD_REQUEST,
                    "Logbook '" + data.getName() + "' does not have an owner");
        }
    }

    /**
     * Check all logbooks in <tt>data</tt> for valid name/owner data.
     *
     * @param data XmlLogbooks data to check
     * @throws CFException on error
     */
    public void checkValidNameAndOwner(XmlLogbooks data) throws CFException {
        if (data == null || data.getLogbooks() == null) {
            return;
        }
        for (XmlLogbook p : data.getLogbooks()) {
            checkValidNameAndOwner(p);
        }
    }

    /**
     * Check that <tt>user</tt> belongs to the owner group specified in the database for
     * log <tt>logId</tt>.
     *
     * @param user user name
     * @param logId name of log to check ownership for
     * @throws CFException on name mismatch
     */
    public void checkUserBelongsToGroupOfLog(String user, Long logId) throws CFException, UnsupportedEncodingException, NoSuchAlgorithmException {
        if (logId == 0) {
            return;
        }
        checkUserBelongsToGroup(user, FindLogsQuery.findLogById(logId));
    }

    /**
     * Check that <tt>user</tt> belongs to the owner group specified in the database for
     * logbook <tt>logbook</tt>.
     *
     * @param user user name
     * @param logbook name of logbook to check ownership for
     * @throws CFException on name mismatch
     */
    public void checkUserBelongsToGroupOfLogbook(String user, String logbook) throws CFException {
        if (logbook == null || logbook.equals("")) {
            return;
        }
        checkUserBelongsToGroup(user, ListLogbooksQuery.findLogbook(logbook));
    }

    /**
     * Check that <tt>user</tt> belongs to the owner group specified in the
     * log <tt>data</tt>.
     *
     * @param user user name
     * @param data XmlLog data to check ownership for
     * @throws CFException on name mismatch
     */
    public void checkUserBelongsToGroup(String user, XmlLog data) throws CFException {
        if (data == null) {
            return;
        }
        UserManager um = UserManager.getInstance();
        if (!um.userIsInGroup(data.getOwner())) {
            throw new CFException(Response.Status.FORBIDDEN,
                    "User '" + um.getUserName()
                    + "' does not belong to owner group '" + data.getOwner()
                    + "' of log '" + data.getId() + "'");
        }
    }

    /**
     * Check that <tt>user</tt> belongs to the owner groups of all logs in <tt>data</tt>.
     *
     * @param user user name
     * @param data XmlLogs data to check ownership for
     * @throws CFException on name mismatch
     */
    public void checkUserBelongsToGroup(String user, XmlLogs data) throws CFException {
        if (data == null || data.getLogs() == null) {
            return;
        }
        for (XmlLog log : data.getLogs()) {
            checkUserBelongsToGroup(user, log);
        }
    }

    /**
     * Check that <tt>user</tt> belongs to the owner group specified in the
     * logbook <tt>data</tt>.
     *
     * @param user user name
     * @param data XmlLogbook data to check ownership for
     * @throws CFException on name mismatch
     */
    public void checkUserBelongsToGroup(String user, XmlLogbook data) throws CFException {
        if (data == null) {
            return;
        }
        UserManager um = UserManager.getInstance();
        if (!um.userIsInGroup(data.getOwner())) {
            throw new CFException(Response.Status.FORBIDDEN,
                    "User '" + um.getUserName()
                    + "' does not belong to owner group '" + data.getOwner()
                    + "' of logbook '" + data.getName() + "'");
        }
    }

    /**
     * Check that <tt>user</tt> belongs to the owner groups of all logbooks in <tt>data</tt>.
     *
     * @param user user name
     * @param data XmlLogs data to check ownership for
     * @throws CFException on name mismatch
     */
    public void checkUserBelongsToGroup(String user, XmlLogbooks data) throws CFException {
        if (data == null || data.getLogbooks() == null) {
            return;
        }
        for (XmlLogbook logbook : data.getLogbooks()) {
            checkUserBelongsToGroup(user, logbook);
        }
    }
}
