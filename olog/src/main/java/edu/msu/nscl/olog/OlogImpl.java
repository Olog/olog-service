/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */
package edu.msu.nscl.olog;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import javax.jcr.*;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * Central business logic layer that implements all directory operations.
 * 
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
public class OlogImpl {

    private static OlogImpl instance = new OlogImpl();

    /**
     * Create an instance of OlogManager
     */
    private OlogImpl() {
    }

    /**
     * Returns the (singleton) instance of OlogManager
     *
     * @return the instance of OlogManager
     */
    public static OlogImpl getInstance() {
        return instance;
    }

    /**
     * Merges Logbooks and Tags of two logs in place
     *
     * @param dest destination log
     * @param src source log
     */
    public static void mergeXmlLogs(Log dest, Log src) {
//        if(src.getSubject() != null)
//            dest.setSubject(src.getSubject());
        if (src.getDescription() != null) {
            dest.setDescription(src.getDescription());
        }
        if (src.getLevel() != null) {
            dest.setLevel(src.getLevel());
        }
        src_logbooks:
        for (Logbook s : src.getLogbooks()) {
            for (Logbook d : dest.getLogbooks()) {
                if (d.getName().equals(s.getName())) {
                    continue src_logbooks;
                }
            }
            dest.getLogbooks().add(s);
        }
        src_tags:
        for (Tag s : src.getTags()) {
            for (Tag d : dest.getTags()) {
                if (d.getName().equals(s.getName())) {
//TODO: here                   d.setState(s.getState());
                    continue src_tags;
                }
            }
            dest.getTags().add(s);
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
     * @return Log with found log and its logbooks
     * @throws CFException on SQLException
     */
    public Log findLogById(Long logId) throws CFException, UnsupportedEncodingException, NoSuchAlgorithmException {
        return LogManager.findLog(logId);
    }

    /**
     * Returns logs found by matching logbook names, tag names, log description.
     *
     * @param matches multivalued map of logbook, tag, log names and patterns to match
     * their values against.
     * @return Logs container with all found logs and their logbooks
     * @throws CFException wrapping an SQLException
     */
    public Logs findLogsByMultiMatch(MultivaluedMap<String, String> matches) throws CFException, RepositoryException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //return FindLogsQuery.findLogsByMultiMatch(matches);
        return LogManager.findLog(matches);
    }

    /**
     * Deletes a log identified by <tt>logId</tt>.
     *
     * @param logId log to delete
     * @throws CFException wrapping an SQLException
     */
    public void removeLog(Long logId) throws CFException {
        LogManager.remove(logId);
    }

    /**
     * List all Logbooks in the database.
     *
     * @throws CFException wrapping an SQLException
     */
    public Logbooks listLogbooks() throws CFException {
        return LogbookManager.findAll();
    }

    /**
     * Return single logbook found by name.
     *
     * @param name name to look for
     * @return Logbook with found logbook and its logs
     * @throws CFException on SQLException
     */
    public Logbook findLogbookByName(String name) throws CFException {
        return LogbookManager.findLogbook(name);
    }

    /**
     * Add the logbook identified by <tt>logbook</tt> to the logs
     * specified in the Logs <tt>data</tt>.
     *
     * @param logbook logbook to add
     * @param data Logbook data with all logs
     * @throws CFException on ownership mismatch, or wrapping an SQLException
     */
    public Logbook updateLogbook(String logbook, Logbook data) throws CFException {
        return UpdateValuesQuery.updateLogbook(logbook, data);
    }

    /**
     * Adds the logbook identified by <tt>tag</tt> <b>exclusively</b>
     * to the logs specified in the Logbook payload <tt>data</tt>, creating it
     * if necessary.
     *
     * @param logbook logbook to add
     * @param data Logbook container with all logs to add logbook to
     * @throws CFException on ownership mismatch, or wrapping an SQLException
     */
    public Logbook createOrReplaceLogbook(String logbook, Logbook data) throws CFException {
        LogbookManager.remove(logbook);
        LogbookManager.create(data.getName(), data.getOwner());
        return UpdateValuesQuery.updateLogbook(data.getName(), data);
    }

    /**
     * Create or replace logbooks specified in <tt>data</tt>.
     *
     * @param data Logbooks data
     * @throws CFException on ownership mismatch, or wrapping an SQLException
     */
    public Logbooks createOrReplaceLogbooks(Logbooks data) throws CFException {
        Logbooks xmlLogbooks = null;
        for (Logbook logbook : data.getLogbooks()) {
            //removeLogbook(logbook.getName());
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
     * @param data Logbook
     * @throws CFException on ownership mismatch, or wrapping an SQLException
     */
    public Logbook addSingleLogbook(String logbookName, Long logId) throws CFException {
        Log log = LogManager.findLog(logId);
        Set<Logbook> logbooks  = log.getLogbooks();
        Logbook logbook = LogbookManager.findLogbook(logbookName);
        logbooks.add(logbook);
        log.setLogbooks(logbooks);
        LogManager.create(log);
        return logbook;
    }

    /**
     * Deletes a logbook identified by <tt>name</tt> from all logs.
     *
     * @param logbook tag to delete
     * @throws CFException wrapping an SQLException
     */
    public void removeLogbook(String logbook) throws CFException {
        LogbookManager.remove(logbook);
    }

    /**
     * Deletes a logbook identified by <tt>name</tt> from all logs, failing if
     * the logbook does not exist.
     *
     * @param logbook tag to delete
     * @throws CFException wrapping an SQLException or on failure
     */
    public void removeExistingLogbook(String logbook) throws CFException {
        LogbookManager.remove(logbook);
    }

    /**
     * Deletes a logbook identified by <tt>name</tt> from a single log.
     *
     * @param logbook tag to delete
     * @param logId log to delete it from
     * @throws CFException wrapping an SQLException
     */
    public void removeSingleLogbook(String logbookName, Long logId) throws CFException {
        Log log = LogManager.findLog(logId);
        Set<Logbook> logbooks  = log.getLogbooks();
        Logbook logbook = LogbookManager.findLogbook(logbookName);
        logbooks.remove(logbook);
        log.setLogbooks(logbooks);
        LogManager.create(log);
    }

    /**
     * List all tags in the database.
     *
     * @throws CFException wrapping an SQLException
     */
    public Tags listTags() throws CFException {
        return TagManager.findAll();
    }

    /**
     * Return single tag found by name.
     *
     * @param name name to look for
     * @return Tag with found tag and its logs/state
     * @throws CFException on SQLException
     */
    public Tag findTagByName(String name) throws CFException {
        return TagManager.findTag(name);
    }

    /**
     * Add the tag identified by <tt>tag</tt> and <tt>state</tt> to the logs
     * specified in the Logs <tt>data</tt>.
     *
     * @param tag tag to add
     * @param data Tag with list of all logs to add tag to
     * @throws CFException on ownership mismatch, or wrapping an SQLException
     */
    public Tag updateTag(String tag, Tag data) throws CFException {
        return UpdateValuesQuery.updateTag(tag, data);
    }

    /**
     * Adds the tag identified by <tt>tag</tt> <b>exclusively</b>
     * to the logs specified in the Tag payload <tt>data</tt>, creating it
     * if necessary.
     *
     * @TODO Right now, this is only a create tag; not create/replace.
     *       To Delete, and recreate log key ids every time is too expensive
     * @param tag tag to add
     * @param data Tag container with all logs to add tag to
     * @throws CFException on ownership mismatch, or wrapping an SQLException
     */
    public Tag createOrReplaceTag(String tag, Tag data) throws CFException {
        TagManager.remove(tag);
        TagManager.create(data.getName());
        return UpdateValuesQuery.updateTag(data.getName(), data);
    }

    /**
     * Create tags specified in <tt>data</tt>.
     *
     * @param data Tags data
     * @throws CFException on ownership mismatch, or wrapping an SQLException
     */
    public Tags createOrReplaceTags(Tags data) throws CFException {
        Tags xmlTags = null;
        for (Tag tag : data.getTags()) {
            //removeTag(tag.getName());
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
    public Tag addSingleTag(String tagName, Long logId) throws CFException {
        Log log = LogManager.findLog(logId);
        Set<Tag> tags  = log.getTags();
        Tag tag = TagManager.findTag(tagName);
        tags.add(tag);
        log.setTags(tags);
        LogManager.create(log);
        return tag;
    }

    /**
     * Deletes a tag identified by <tt>name</tt> from all logs.
     *
     * @param tag tag to delete
     * @throws CFException wrapping an SQLException
     */
    public void removeTag(String tag) throws CFException {
        TagManager.remove(tag);
    }

    /**
     * Deletes a tag identified by <tt>name</tt> from a single log.
     *
     * @param tag tag to delete
     * @param logId log to delete it from
     * @throws CFException wrapping an SQLException
     */
    public void removeSingleTag(String tagName, Long logId) throws CFException {
        Log log = LogManager.findLog(logId);
        Set<Tag> tags  = log.getTags();
        Tag tag = TagManager.findTag(tagName);
        tags.remove(tag);
        log.setTags(tags);
        LogManager.create(log);
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
    Log addAttribute(String hostAddress, String property, Long logId, XmlProperty data) throws CFException, UnsupportedEncodingException, NoSuchAlgorithmException {
        Log currentLog = findLogById(logId);

        Collection<XmlProperty> currentProperties = currentLog.getXmlProperties();
        currentProperties.add(data);
        currentLog.setXmlProperties(currentProperties);

        return createOrReplaceLog(logId, currentLog);
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
    Log removeAttribute(String hostAddress, String property, Long logId, XmlProperty data) throws CFException, UnsupportedEncodingException, NoSuchAlgorithmException {
        Log currentLog = findLogById(logId);

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
        return createOrReplaceLog(logId, currentLog);
    }

    /**
     * Update a log identified by <tt>logId</tt>, creating it when necessary.
     * The logbook set in <tt>data</tt> has to be complete, i.e. the existing
     * log logbooks are <b>replaced</b> with the logbooks in <tt>data</tt>.
     *
     * @param logId log to update
     * @param data Log data
     * @throws CFException on ownership or name mismatch, or wrapping an SQLException
     */
    public Log createOrReplaceLog(Long logId, Log data) throws CFException, UnsupportedEncodingException, NoSuchAlgorithmException {
        UserManager um = UserManager.getInstance();
        data.setSource(um.getHostAddress());
        data.setOwner(um.getUserName());
        return LogManager.create(data);
    }

    /**
     * Create logs specified in <tt>data</tt>.
     *
     * @param data Logs data
     * @throws CFException on ownership mismatch, or wrapping an SQLException
     */
    public Logs createOrReplaceLogs(Logs data) throws CFException, UnsupportedEncodingException, NoSuchAlgorithmException {
        Logs xmlLogs = new Logs();
        for (Log log : data.getLogs()) {
            
            //removeLog(log.getId());
            xmlLogs.addLog(createOneLog(log));
        }
        return xmlLogs;
    }

    /**
     * Create a new log using the logbook set in <tt>data</tt>.
     *
     * @param data Log data
     * @throws CFException on ownership or name mismatch, or wrapping an SQLException
     */
    private Log createOneLog(Log data) throws CFException, UnsupportedEncodingException, NoSuchAlgorithmException {
        UserManager um = UserManager.getInstance();
        data.setSource(um.getHostAddress());
        data.setOwner(um.getUserName());
        return LogManager.create(data);
    }

    /**
     * Merge logbook set in <tt>data</tt> into the existing log <tt>logId</tt>.
     *
     * @param logId log to merge the logbooks and tags into
     * @param data Log data containing logbooks and tags
     * @throws CFException on name or owner mismatch, or wrapping an SQLException
     */
    public Log updateLog(Long logId, Log data) throws CFException, UnsupportedEncodingException, NoSuchAlgorithmException {
        Log dest = findLogById(logId);
        if (dest == null) {
            throw new CFException(Response.Status.NOT_FOUND,
                    "Log entry " + logId + " could not be updated: Does not exist");
        }
        dest.setId(data.getId());
        dest.setOwner(data.getOwner());
        mergeXmlLogs(dest, data);
        return createOrReplaceLog(logId, dest);
    }
    


    

    

    

    
    /**
     * Check that <tt>logId</tt> matches the log id in <tt>data</tt>.
     *
     * @param logId log id to check
     * @param data Log data to check against
     * @throws CFException on name mismatch
     */
    //TODO: fix this
    public void checkIdMatchesPayload(Long logId, Log data) throws CFException {
        //    if (!logId.equals(data.getId())) {
        //        throw new CFException(Response.Status.BAD_REQUEST,
        //                "Specified log id '" + logId
        //               + "' and payload log id '" + data.getId() + "' do not match");
        //   }
    }

    /**
     * Check the log in <tt>data</tt> for valid owner data.
     *
     * @param data Log data to check
     * @throws CFException on error
     */
    public void checkValidOwner(Log data) throws CFException {

        if (data.getOwner() == null || data.getOwner().equals("")) {
            throw new CFException(Response.Status.BAD_REQUEST,
                    "Log entry " + data.getId() + " does not have an owner.");
        }
    }

    /**
     * Check all logs in <tt>data</tt> for valid owner data.
     *
     * @param data Logs data to check
     * @throws CFException on error
     */
    public void checkValidOwner(Logs data) throws CFException {
        if (data == null || data.getLogs() == null) {
            return;
        }
        for (Log c : data.getLogs()) {
            checkValidOwner(c);
        }
    }

    /**
     * Check that <tt>name</tt> matches the tag name in <tt>data</tt>.
     *
     * @param name tag name to check
     * @param data Tag data to check against
     * @throws CFException on name mismatch
     */
    public void checkNameMatchesPayload(String name, Tag data) throws CFException {
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
     * @param data Tag data to check
     * @throws CFException on name mismatch
     */
    public void checkValidNameAndOwner(Tag data) throws CFException {
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
     * @param data Tags data to check
     * @throws CFException on error
     */
    public void checkValidNameAndOwner(Tags data) throws CFException {
        if (data == null || data.getTags() == null) {
            return;
        }
        for (Tag t : data.getTags()) {
            checkValidNameAndOwner(t);
        }
    }

    /**
     * Check that <tt>name</tt> matches the logbook name in <tt>data</tt>.
     *
     * @param name logbook name to check
     * @param data Logbook data to check against
     * @throws CFException on name mismatch
     */
    public void checkNameMatchesPayload(String name, Logbook data) throws CFException {
        if (!name.equals(data.getName())) {
            throw new CFException(Response.Status.BAD_REQUEST,
                    "The logbook in the URL '" + name
                    + "' and the logbook in the payload '" + data.getName() + "' do not match");
        }
    }

    /**
     * Check the logbook in <tt>data</tt> for valid name/owner data.
     *
     * @param data Logbook data to check
     * @throws CFException on error
     */
    public void checkValidNameAndOwner(Logbook data) throws CFException {
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
     * @param data Logbooks data to check
     * @throws CFException on error
     */
    public void checkValidNameAndOwner(Logbooks data) throws CFException {
        if (data == null || data.getLogbooks() == null) {
            return;
        }
        for (Logbook p : data.getLogbooks()) {
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
        checkUserBelongsToGroup(user, LogbookManager.findLogbook(logbook));
    }

    /**
     * Check that <tt>user</tt> belongs to the owner group specified in the
     * log <tt>data</tt>.
     *
     * @param user user name
     * @param data Log data to check ownership for
     * @throws CFException on name mismatch
     */
    public void checkUserBelongsToGroup(String user, Log data) throws CFException {
        if (data == null) {
            return;
        }
        for (Logbook logbook: data.getLogbooks()){
            checkUserBelongsToGroup(user, LogbookManager.findLogbook(logbook.getName()));
        }
    }

    /**
     * Check that <tt>user</tt> belongs to the owner groups of all logs in <tt>data</tt>.
     *
     * @param user user name
     * @param data Logs data to check ownership for
     * @throws CFException on name mismatch
     */
    public void checkUserBelongsToGroup(String user, Logs data) throws CFException {
        if (data == null || data.getLogs() == null) {
            return;
        }
        for (Log log : data.getLogs()) {
            checkUserBelongsToGroup(user, log);
        }
    }

    /**
     * Check that <tt>user</tt> belongs to the owner group specified in the
     * logbook <tt>data</tt>.
     *
     * @param user user name
     * @param data Logbook data to check ownership for
     * @throws CFException on name mismatch
     */
    public void checkUserBelongsToGroup(String user, Logbook data) throws CFException {
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
     * @param data Logs data to check ownership for
     * @throws CFException on name mismatch
     */
    public void checkUserBelongsToGroup(String user, Logbooks data) throws CFException {
        if (data == null || data.getLogbooks() == null) {
            return;
        }
        for (Logbook logbook : data.getLogbooks()) {
            checkUserBelongsToGroup(user, logbook);
        }
    }

    XmlAttachments findAttachmentsById(Long logId) throws CFException {
        return AttachmentManager.findAll(logId);
    }

    Attachment getAttachment(String filePath, String fileName) throws CFException {
        return AttachmentManager.findAttachment(filePath, fileName);
    }

    XmlAttachment createAttachment(Attachment attachment, Long logId) throws CFException {
        return AttachmentManager.create(attachment, logId);
    }

    void removeAttachment(String fileName, Long logId) throws CFException {
        AttachmentManager.remove(fileName, logId);
    }

    Logs findLogsTest() throws CFException {
        return LogManager.findAll();
    }
}
