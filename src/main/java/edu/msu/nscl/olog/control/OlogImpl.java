/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */
package edu.msu.nscl.olog.control;

import edu.msu.nscl.olog.OlogException;
import edu.msu.nscl.olog.ResourceBinder;
import edu.msu.nscl.olog.UserManager;
import edu.msu.nscl.olog.boundry.LogbookManager;
import edu.msu.nscl.olog.boundry.LogManager;
import edu.msu.nscl.olog.boundry.PropertyManager;
import edu.msu.nscl.olog.boundry.TagManager;
import edu.msu.nscl.olog.entity.Tags;
import edu.msu.nscl.olog.entity.Logbooks;
import edu.msu.nscl.olog.entity.XmlProperty;
import edu.msu.nscl.olog.entity.Tag;
import edu.msu.nscl.olog.entity.XmlAttachments;
import edu.msu.nscl.olog.entity.XmlAttachment;
import edu.msu.nscl.olog.boundry.AttachmentManager;
import edu.msu.nscl.olog.boundry.AttributeManager;
import edu.msu.nscl.olog.entity.Attachment;
import edu.msu.nscl.olog.entity.XmlProperties;
import edu.msu.nscl.olog.entity.Log;
import edu.msu.nscl.olog.entity.Property;
import edu.msu.nscl.olog.entity.Attribute;
import edu.msu.nscl.olog.entity.BitemporalLog;
import edu.msu.nscl.olog.entity.BitemporalLogs;
import edu.msu.nscl.olog.entity.LogAttribute;
import edu.msu.nscl.olog.entity.Logbook;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.jcr.*;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import javax.ws.rs.core.Response;

/**
 * Central business logic layer that implements all directory operations.
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
@Startup
@Singleton
@DependsOn("ResourceBinder")
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class OlogImpl {

    @Inject
    private ResourceBinder rb;

    /**
     * Return single log found by log id.
     *
     * @param logId id to look for
     * @return Log with found log and its logbooks
     * @throws OlogException on SQLException
     */
    public BitemporalLog findLogById(Long logId) throws OlogException, UnsupportedEncodingException, NoSuchAlgorithmException {
        return LogManager.findLog(logId);
    }

    /**
     * Return single log found by log id.
     *
     * @param logId id to look for
     * @return Log with found log and its logbooks
     * @throws OlogException on SQLException
     */
    public BitemporalLog findLogById(Long logId, MultivaluedMap<String, String> matches) throws OlogException, UnsupportedEncodingException, NoSuchAlgorithmException {
        if (matches != null && matches.containsKey("version") && !matches.get("version").isEmpty()) {
            return LogManager.findLogWithVersion(logId, matches.get("version").iterator().next());
        } else {
            return LogManager.findLog(logId);
        }
    }

    /**
     * Returns logs found by matching logbook names, tag names, log description.
     *
     * @param matches multivalued map of logbook, tag, log names and patterns to
     * match their values against.
     * @return Logs container with all found logs and their logbooks
     * @throws OlogException wrapping an SQLException
     */
    public BitemporalLogs findLogsByMultiMatch(MultivaluedMap<String, String> matches) throws OlogException, RepositoryException, UnsupportedEncodingException, NoSuchAlgorithmException {
        return LogManager.findLog(matches);
    }

    /**
     * Deletes a log identified by <tt>logId</tt>.
     *
     * @param logId log to delete
     * @throws OlogException wrapping an SQLException
     */
    public void removeLog(Long logId) throws OlogException {
        BitemporalLog log = LogManager.findLog(logId);
        if (log != null) {
            LogManager.remove(logId);
        } else {
            throw new OlogException(Response.Status.NOT_FOUND,
                    "Log entry " + logId.toString() + " does not exists.");
        }
    }

    /**
     * List all Logbooks in the database.
     *
     * @throws OlogException wrapping an SQLException
     */
    public Logbooks listLogbooks() throws OlogException {
        return LogbookManager.findAll();
    }

    /**
     * Return single logbook found by name.
     *
     * @param name name to look for
     * @return Logbook with found logbook and its logs
     * @throws OlogException on SQLException
     */
    public Logbook findLogbookByName(String name) throws OlogException {
        return LogbookManager.findLogbook(name);
    }

    /**
     * Add the logbook identified by <tt>logbook</tt> to the logs specified in
     * the Logs <tt>data</tt>.
     *
     * @param data Logbook data with all logs
     * @throws OlogException on ownership mismatch, or wrapping an SQLException
     */
    public Logbook updateLogbook(Logbook data) throws OlogException {
        return LogbookManager.create(data.getOwner(), data.getName());
    }

    /**
     * Adds the logbook identified by <tt>tag</tt> <b>exclusively</b> to the
     * logs specified in the Logbook payload <tt>data</tt>, creating it if
     * necessary.
     *
     * @param data Logbook container with all logs to add logbook to
     * @throws OlogException on ownership mismatch, or wrapping an SQLException
     */
    public Logbook createOrReplaceLogbook(String logbookName, Logbook data) throws OlogException {
        Logbook logbook = LogbookManager.create(logbookName, data.getOwner());
        List<BitemporalLog> logsData = new ArrayList<BitemporalLog>();
        for (Log log : data.getLogs()) {
            logsData.add(LogManager.findLog(log.getId()));
            if (log == null) {
                throw new OlogException(Response.Status.BAD_REQUEST,
                        "Log entry " + log.getId() + " does not exists.");
            }
        }
        for (BitemporalLog log : logsData) {
            log.getLog().addLogbook(logbook);
            LogManager.create(log);
        }
        return logbook;

    }

    /**
     * Create or replace logbooks specified in <tt>data</tt>.
     *
     * @param data Logbooks data
     * @throws OlogException on ownership mismatch, or wrapping an SQLException
     */
    public Logbooks createOrReplaceLogbooks(Logbooks data) throws OlogException {
        Logbooks xmlLogbooks = new Logbooks();
        for (Logbook logbook : data.getLogbooks()) {
            //removeLogbook(logbook.getName());
            xmlLogbooks.addLogbook(createOrReplaceLogbook(logbook.getName(), logbook));
        }
        return xmlLogbooks;
    }

    /**
     * Add the logbook identified by <tt>logbook</tt> to the single log
     * <tt>id</tt>.
     *
     * TODO: this couldn't work as stated
     *
     * @param logId log to add the logbook to
     * @param logbookName String
     * @throws OlogException on ownership mismatch, or wrapping an SQLException
     */
    public Logbook addSingleLogbook(String logbookName, Long logId) throws OlogException {
        BitemporalLog log = LogManager.findLog(logId);
        Set<Logbook> logbooks = log.getLog().getLogbooks();
        Logbook logbook = LogbookManager.findLogbook(logbookName);
        logbooks.add(logbook);
        log.getLog().setLogbooks(logbooks);
        LogManager.create(log);
        return logbook;
    }

    /**
     * Deletes a logbook identified by <tt>name</tt> from all logs.
     *
     * @param logbook tag to delete
     * @throws OlogException wrapping an SQLException
     */
    public void removeLogbook(String logbook) throws OlogException {
        LogbookManager.remove(logbook);
    }

    /**
     * Deletes a logbook identified by <tt>name</tt> from all logs, failing if
     * the logbook does not exists.
     *
     * @param logbook tag to delete
     * @throws OlogException wrapping an SQLException or on failure
     */
    public void removeExistingLogbook(String logbook) throws OlogException {
        LogbookManager.remove(logbook);
    }

    /**
     * Deletes a logbook identified by <tt>name</tt> from a single log.
     *
     * @param logbookName String to delete
     * @param logId log to delete it from
     * @throws OlogException wrapping an SQLException
     */
    public void removeSingleLogbook(String logbookName, Long logId) throws OlogException {
        BitemporalLog log = LogManager.findLog(logId);
        Set<Logbook> logbooks = log.getLog().getLogbooks();
        Logbook logbook = LogbookManager.findLogbook(logbookName);
        if (logbooks.contains(logbook) && logbooks.size() > 1) {
            logbooks.remove(logbook);
        } else {
            throw new OlogException(Response.Status.BAD_REQUEST,
                    "Log entry " + logId.toString() + " does not have logbook:" + logbook.getName() + ".");
        }
        log.getLog().setLogbooks(logbooks);
        LogManager.create(log);
    }

    /**
     * List all tags in the database.
     *
     * @throws OlogException wrapping an SQLException
     */
    public Tags listTags() throws OlogException {
        return TagManager.findAll();
    }

    /**
     * Return single tag found by name.
     *
     * @param name name to look for
     * @return Tag with found tag and its logs/state
     * @throws OlogException on SQLException
     */
    public Tag findTagByName(String name) throws OlogException {
        return TagManager.findTag(name);
    }

    /**
     * Add the tag identified by <tt>tag</tt> and <tt>state</tt> to the logs
     * specified in the Logs <tt>data</tt>.
     *
     * @param tagName tag to add
     * @param data Tag with list of all logs to add tag to
     * @throws OlogException on ownership mismatch, or wrapping an SQLException
     */
    public Tag updateTag(String tagName, Tag data) throws OlogException {
        Tag tag = TagManager.create(tagName);
        if (data.getLogs().size() > 0) {
            List<BitemporalLog> logsData = new ArrayList<BitemporalLog>();
            for (Log log : data.getLogs()) {
                logsData.add(LogManager.findLog(log.getId()));
                if (log == null) {
                    throw new OlogException(Response.Status.BAD_REQUEST,
                            "Log entry " + log.getId() + " does not exists.");
                }
            }
            for (BitemporalLog log : logsData) {
                log.getLog().addTag(tag);
                LogManager.create(log);
            }
        }
        return tag;
    }

    /**
     * Adds the tag identified by <tt>tag</tt> <b>exclusively</b> to the logs
     * specified in the Tag payload <tt>data</tt>, creating it if necessary.
     *
     * @TODO Right now, this is only a create tag; not create/replace. To
     * Delete, and recreate log key ids every time is too expensive
     *
     * @param tagName tag to add
     * @param data Tag container with all logs to add tag to
     * @throws OlogException on ownership mismatch, or wrapping an SQLException
     */
    public Tag createOrReplaceTag(String tagName, Tag data) throws OlogException {
        Tag tag = TagManager.create(tagName);
        if (data.getLogs().size() > 0) {
            MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
            map.add("tag", tagName);
            List<BitemporalLog> logs = LogManager.findLog(map);
            List<BitemporalLog> logsData = new ArrayList<BitemporalLog>();
            for (Log log : data.getLogs()) {
                logsData.add(LogManager.findLog(log.getId()));
                if (log == null) {
                    throw new OlogException(Response.Status.BAD_REQUEST,
                            "Log entry " + log.getId() + " does not exists.");
                }
            }
            for (BitemporalLog log : logs) {
                log.getLog().removeTag(tag);
                LogManager.create(log);
            }
            for (BitemporalLog log : logsData) {
                log.getLog().addTag(tag);
                LogManager.create(log);
            }
        }
        return tag;
    }

    /**
     * Create tags specified in <tt>data</tt>.
     *
     * @param data Tags data
     * @throws OlogException on ownership mismatch, or wrapping an SQLException
     */
    public Tags createOrReplaceTags(Tags data) throws OlogException {
        Tags xmlTags = new Tags();
        for (Tag tag : data.getTags()) {
            //removeTag(tag.getName());
            xmlTags.addTag(createOrReplaceTag(tag.getName(), tag));
        }
        return xmlTags;
    }

    /**
     * Deletes a logbook identified by <tt>name</tt> from all logs, failing if
     * the logbook does not exists.
     *
     * @param tag tag to delete
     * @throws OlogException wrapping an SQLException or on failure
     */
    public void removeExistingTag(String tag) throws OlogException {
        TagManager.remove(tag);
    }

    /**
     * Add the tag identified by <tt>tag</tt> to the single log <tt>logId</tt>.
     *
     * @param tagName tag to add
     * @param logId
     * @throws OlogException on ownership mismatch, or wrapping an SQLException
     */
    public Tag addSingleTag(String tagName, Long logId) throws OlogException {
        BitemporalLog log = LogManager.findLog(logId);
        Set<Tag> tags = log.getLog().getTags();
        Tag tag = TagManager.findTag(tagName);
        tags.add(tag);
        log.getLog().setTags(tags);
        LogManager.create(log);
        return tag;
    }

    /**
     * Deletes a tag identified by <tt>name</tt> from all logs.
     *
     * @param tag tag to delete
     * @throws OlogException wrapping an SQLException
     */
    public void removeTag(String tag) throws OlogException {
        TagManager.remove(tag);
    }

    /**
     * Deletes a tag identified by <tt>name</tt> from a single log.
     *
     * @param tagName tag to delete
     * @param logId log to delete it from
     * @throws OlogException wrapping an SQLException
     */
    public void removeSingleTag(String tagName, Long logId) throws OlogException {
        BitemporalLog log = LogManager.findLog(logId);
        Set<Tag> tags = log.getLog().getTags();
        Tag tag = TagManager.findTag(tagName);
        if (tags.contains(tag)) {
            tags.remove(tag);
        } else {
            throw new OlogException(Response.Status.BAD_REQUEST,
                    "Log entry " + logId.toString() + " does not have tag:" + tag.getName() + ".");
        }
        log.getLog().setTags(tags);
        LogManager.create(log);
    }

    /**
     * Return a list of all properties
     *
     * @param
     * @throws OlogException wrapping an SQLException
     */
    public XmlProperties listProperties() throws OlogException {
        Set<Property> prop = PropertyManager.findAll();
        XmlProperties xmlProp = new XmlProperties();
        for (Property p : prop) {
            xmlProp.addProperty(p.toXmlProperty());
        }
        return xmlProp;
    }

    /**
     * Return a list of attributes for a given property
     *
     * @param
     * @throws OlogException wrapping an SQLException
     */
    public XmlProperty listAttributes(String property) throws OlogException {
        return PropertyManager.findProperty(property).toXmlProperty();
    }

    /**
     * Adds a new property.
     *
     * @param data XmlProperty incoming payload
     * @param destructive boolean is this action to be destructive (true) or not
     * @throws OlogException wrapping an SQLException
     */
    public XmlProperty addProperty(XmlProperty data, boolean destructive) throws OlogException {
        if (destructive) {
            Property property = PropertyManager.create(data.toProperty());
            for (Map.Entry<String, String> att : data.getAttributes().entrySet()) {
                property = AttributeManager.create(property, att.getKey());
            }
            return property.toXmlProperty();
        } else {
            //TODO: be able to add new Attributes to an existing Property
            Property property = PropertyManager.findProperty(data.getName());
            Set<Attribute> attributes = property.getAttributes();
            for (Map.Entry<String, String> att : data.getAttributes().entrySet()) {
                attributes.add(new Attribute(att.getKey()));
            }
            property.setAttributes(attributes);
            return PropertyManager.create(property).toXmlProperty();
        }
    }

    /**
     * Adds a new property attribute to a log entry.
     *
     * @param logId Long log that the property attribute will be associated with
     * @param data Property incoming payload
     * @throws OlogException wrapping an SQLException
     */
    public BitemporalLog addAttribute(Long logId, LogAttribute data) throws OlogException, UnsupportedEncodingException, NoSuchAlgorithmException {
        if (data.getId() != null) {
            BitemporalLog log = LogManager.findLog(logId);
            Set<LogAttribute> currentLogAttributes = log.getLog().getAttributes();
            data.setLog(log.getLog());
            data.setAttributeId(data.getId());
            data.setLogId(log.getLog().getId());
            currentLogAttributes.add(data);
            log.getLog().setAttributes(currentLogAttributes);
            return LogManager.create(log);
        } else {
            throw new OlogException(Response.Status.NOT_FOUND,
                    "LogAttribute id for " + data.getValue() + " could not be updated: Does not exists");
        }   
    }

    /**
     * Remove a property.
     *
     * @param propertyName String of the property to be removed
     * @throws OlogException wrapping an SQLException
     */
    public void removeProperty(String propertyName) throws OlogException {
        PropertyManager.remove(propertyName);
    }

    /**
     * Removes a properties attribute from a log entry.
     *
     * @param logId Long log that the property attribute will be removed
     * @param data Property incoming payload
     * @throws OlogException wrapping an SQLException
     */
    public BitemporalLog removeAttribute(Long logId, LogAttribute data) throws OlogException, UnsupportedEncodingException, NoSuchAlgorithmException {
        BitemporalLog log = LogManager.findLog(logId);
        Set<LogAttribute> currentLogAttributes = log.getLog().getAttributes();
        data.setLog(log.getLog());
        data.setAttributeId(data.getId());
        data.setLogId(log.getLog().getId());
        currentLogAttributes.remove(data);
        log.getLog().setAttributes(currentLogAttributes);
        return LogManager.create(log);
    }

    /**
     * Update a log identified by <tt>logId</tt>, creating it when necessary.
     * The logbook set in <tt>data</tt> has to be complete, i.e. the existing
     * log logbooks are <b>replaced</b> with the logbooks in <tt>data</tt>.
     *
     * @param logId log to update
     * @param data Log data
     * @throws OlogException on ownership or name mismatch, or wrapping an
     * SQLException
     */
    public BitemporalLog createOrReplaceLog(Long logId, BitemporalLog data) throws OlogException, UnsupportedEncodingException, NoSuchAlgorithmException {
        UserManager um = rb.getUserManager();
        data.getLog().setSource(um.getHostAddress());
        data.getLog().setOwner(um.getUserName());
        return LogManager.create(data);
    }

    /**
     * Create logs specified in <tt>data</tt>.
     *
     * @param logs Logs data
     * @throws OlogException on ownership mismatch, or wrapping an SQLException
     */
    public List<BitemporalLog> createOrReplaceLogs(List<BitemporalLog> logs) throws OlogException, UnsupportedEncodingException, NoSuchAlgorithmException {
        ListIterator<BitemporalLog> iterator = logs.listIterator();
        while (iterator.hasNext()) {
            BitemporalLog log = iterator.next();
            iterator.set(createOneLog(log));
        }
        return logs;
    }

    /**
     * Create a new log using the logbook set in <tt>data</tt>.
     *
     * @param data Log data
     * @throws OlogException on ownership or name mismatch, or wrapping an
     * SQLException
     */
    private BitemporalLog createOneLog(BitemporalLog data) throws OlogException, UnsupportedEncodingException, NoSuchAlgorithmException {
        UserManager um = rb.getUserManager();
        data.getLog().setSource(um.getHostAddress());
        //data.getLog().setOwner(um.getUserName());
        return LogManager.create(data);
    }

    /**
     * Merge logbook set in <tt>data</tt> into the existing log <tt>logId</tt>.
     *
     * @param logId log to merge the logbooks and tags into
     * @param data Log data containing logbooks and tags
     * @throws OlogException on name or owner mismatch, or wrapping an
     * SQLException
     */
    public BitemporalLog updateLog(Long logId, BitemporalLog data) throws OlogException, UnsupportedEncodingException, NoSuchAlgorithmException {
        BitemporalLog dest = findLogById(logId, null);
        if (dest == null) {
            throw new OlogException(Response.Status.NOT_FOUND,
                    "Log entry " + logId + " could not be updated: Does not exists");
        }
        
        data.setLog(new Log(dest.getLog(), data.getLog()));
        return createOrReplaceLog(logId, data);
    }

    /**
     * Check that <tt>logId</tt> matches the log id in <tt>data</tt>.
     *
     * @param logId log id to check
     * @param data Log data to check against
     * @throws OlogException on name mismatch
     */
    //TODO: fix this
    public void checkIdMatchesPayload(Long logId, BitemporalLog data) throws OlogException {
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
     * @throws OlogException on error
     */
    public void checkValid(BitemporalLog data) throws OlogException {
        Log log = data.getLog();
        if (log.getOwner() == null || log.getOwner().equals("")) {
            throw new OlogException(Response.Status.BAD_REQUEST,
                    "Log entry " + log.getId() + " does not have an owner.");
        } else if (log.getLogbooks().isEmpty()) {
            throw new OlogException(Response.Status.BAD_REQUEST,
                    "Log entry " + log.getId() + " does not have a logbook.");
        } else if (!log.getAttributes().isEmpty()) {
            for (LogAttribute logAttribute : log.getAttributes()) {
                if (logAttribute.getAttribute().getName().isEmpty()) {
                    throw new OlogException(Response.Status.BAD_REQUEST,
                            "Log entry " + log.getId() + " property does not have an attribute.");
                } else {
                    if (logAttribute.getValue() == null){
                        throw new OlogException(Response.Status.BAD_REQUEST,
                                "Log entry " + log.getId() + " property:" + logAttribute.getAttribute().getProperty().getName() + " attribute does not have a key or value.");
                    }
                    if (logAttribute.getValue().isEmpty() || logAttribute.getAttribute().getName().isEmpty()) {
                        throw new OlogException(Response.Status.BAD_REQUEST,
                                "Log entry " + log.getId() + " property:" + logAttribute.getAttribute().getProperty().getName() + " attribute does not have a key or value.");
                    }
                }
            }
        }
    }

    /**
     * Check all logs in <tt>data</tt> for valid owner data.
     *
     * @param data Logs data to check
     * @throws OlogException on error
     */
    public void checkValid(List<BitemporalLog> data) throws OlogException {
        if (data == null) {
            return;
        }
        for (BitemporalLog c : data) {
            checkValid(c);
        }
    }

    /**
     * Check that <tt>name</tt> matches the tag name in <tt>data</tt>.
     *
     * @param name tag name to check
     * @param data Tag data to check against
     * @throws OlogException on name mismatch
     */
    public void checkNameMatchesPayload(String name, Tag data) throws OlogException {
        if (data == null) {
            return;
        }
        if (!name.equals(data.getName())) {
            throw new OlogException(Response.Status.BAD_REQUEST,
                    "Tag specified in the URL '" + name
                    + "' and tag specified in the payload '" + data.getName() + "' do not match");
        }
    }

    /**
     * Check the tag in <tt>data</tt> for valid name data.
     *
     * @param data Tag data to check
     * @throws OlogException on name mismatch
     */
    public void checkValidNameAndOwner(Tag data) throws OlogException {
        if (data.getName() == null || data.getName().equals("")) {
            throw new OlogException(Response.Status.BAD_REQUEST,
                    "The specified tag does not have a name");
        }
    }

    /**
     * Check the property name in <tt>data</tt> with the property name in the
     * URL.
     *
     * @param propertyName name coming from the URL
     * @param data Property data
     * @throws OlogException on name mismatch
     */
    public void checkPropertyName(String propertyName, Property data) throws OlogException {
        if (data.getName() == null || !data.getName().equals(propertyName)) {
            throw new OlogException(Response.Status.BAD_REQUEST,
                    "The property name in the URL '" + propertyName
                    + "' and the property name in the payload '" + data.getName()
                    + "' do not match");
        }
    }

    /**
     * Check the property name in <tt>data</tt> with the property name in the
     * URL.
     *
     * @param propertyName name coming from the URL
     * @param data Property data
     * @throws OlogException on name mismatch
     */
    public void checkPropertyName(String propertyName, XmlProperty data) throws OlogException {
        if (data.getName() == null || !data.getName().equals(propertyName)) {
            throw new OlogException(Response.Status.BAD_REQUEST,
                    "The property name in the URL '" + propertyName
                    + "' and the property name in the payload '" + data.getName()
                    + "' do not match");
        }
    }

    /**
     * Check all tags in <tt>data</tt> for valid name data.
     *
     * @param data Tags data to check
     * @throws OlogException on error
     */
    public void checkValidNameAndOwner(Tags data) throws OlogException {
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
     * @throws OlogException on name mismatch
     */
    public void checkNameMatchesPayload(String name, Logbook data) throws OlogException {
        if (!name.equals(data.getName())) {
            throw new OlogException(Response.Status.BAD_REQUEST,
                    "The logbook in the URL '" + name
                    + "' and the logbook in the payload '" + data.getName() + "' do not match");
        }
    }

    /**
     * Check the logbook in <tt>data</tt> for valid name/owner data.
     *
     * @param data Logbook data to check
     * @throws OlogException on error
     */
    public void checkValidNameAndOwner(Logbook data) throws OlogException {
        if (data.getName() == null || data.getName().equals("")) {
            throw new OlogException(Response.Status.BAD_REQUEST,
                    "Logbook name is empty");
        }
        if (data.getOwner() == null || data.getOwner().equals("")) {
            throw new OlogException(Response.Status.BAD_REQUEST,
                    "Logbook '" + data.getName() + "' does not have an owner");
        }
    }

    /**
     * Check all logbooks in <tt>data</tt> for valid name/owner data.
     *
     * @param data Logbooks data to check
     * @throws OlogException on error
     */
    public void checkValidNameAndOwner(Logbooks data) throws OlogException {
        if (data == null || data.getLogbooks() == null) {
            return;
        }
        for (Logbook p : data.getLogbooks()) {
            checkValidNameAndOwner(p);
        }
    }

    /**
     * Check that <tt>user</tt> belongs to the owner group specified in the
     * database for log <tt>logId</tt>.
     *
     * @param user user name
     * @param logId name of log to check ownership for
     * @throws OlogException on name mismatch
     */
    public void checkUserBelongsToGroupOfLog(String user, Long logId) throws OlogException, UnsupportedEncodingException, NoSuchAlgorithmException {
        if (logId == 0) {
            return;
        }
        checkUserBelongsToGroup(user, LogManager.findLog(logId));
    }

    /**
     * Check that <tt>user</tt> belongs to the owner group specified in the
     * database for logbook <tt>logbook</tt>.
     *
     * @param user user name
     * @param logbook name of logbook to check ownership for
     * @throws OlogException on name mismatch
     */
    public void checkUserBelongsToGroupOfLogbook(String user, String logbook) throws OlogException {
        if (logbook == null || logbook.equals("")) {
            return;
        }
        checkUserBelongsToGroup(user, LogbookManager.findLogbook(logbook));
    }

    /**
     * Check that <tt>user</tt> belongs to the owner group specified in the log
     * <tt>data</tt>.
     *
     * @param user user name
     * @param data Log data to check ownership for
     * @throws OlogException on name mismatch
     */
    public void checkUserBelongsToGroup(String user, BitemporalLog data) throws OlogException {
        if (data == null) {
            return;
        }
        for (Logbook logbook : data.getLog().getLogbooks()) {
            checkUserBelongsToGroup(user, LogbookManager.findLogbook(logbook.getName()));
        }
    }

    /**
     * Check that <tt>user</tt> belongs to the owner groups of all logs in
     * <tt>data</tt>.
     *
     * @param user user name
     * @param data Logs data to check ownership for
     * @throws OlogException on name mismatch
     */
    public void checkUserBelongsToGroup(String user, List<BitemporalLog> data) throws OlogException {
        if (data == null) {
            return;
        }
        for (BitemporalLog log : data) {
            checkUserBelongsToGroup(user, log);
        }
    }

    /**
     * Check that <tt>user</tt> belongs to the owner group specified in the
     * logbook <tt>data</tt>.
     *
     * @param user user name
     * @param data Logbook data to check ownership for
     * @throws OlogException on name mismatch
     */
    public void checkUserBelongsToGroup(String user, Logbook data) throws OlogException {
        if (data == null) {
            return;
        }
        UserManager um = rb.getUserManager();
        if (!um.userIsInGroup(data.getOwner())) {
            throw new OlogException(Response.Status.FORBIDDEN,
                    "User '" + um.getUserName()
                    + "' does not belong to owner group '" + data.getOwner()
                    + "' of logbook '" + data.getName() + "'");
        }
    }

    /**
     * Check that <tt>user</tt> belongs to the owner groups of all logbooks in
     * <tt>data</tt>.
     *
     * @param user user name
     * @param data Logs data to check ownership for
     * @throws OlogException on name mismatch
     */
    public void checkUserBelongsToGroup(String user, Logbooks data) throws OlogException {
        if (data == null || data.getLogbooks() == null) {
            return;
        }
        for (Logbook logbook : data.getLogbooks()) {
            checkUserBelongsToGroup(user, logbook);
        }
    }

    public XmlAttachments findAttachmentsById(Long logId) throws OlogException {
        return AttachmentManager.findAll(logId);
    }

    public Attachment getAttachment(String filePath, String fileName) throws OlogException {
        return AttachmentManager.findAttachment(filePath, fileName);
    }

    public XmlAttachment createAttachment(Attachment attachment, Long logId) throws OlogException {
        return AttachmentManager.create(attachment, logId);
    }

    public void removeAttachment(String fileName, Long logId) throws OlogException {
        AttachmentManager.remove(fileName, logId);
    }
}
