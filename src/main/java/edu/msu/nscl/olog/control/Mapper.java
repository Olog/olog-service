
package edu.msu.nscl.olog.control;

import edu.msu.nscl.olog.OlogException;
import edu.msu.nscl.olog.boundry.AttachmentManager;
import edu.msu.nscl.olog.boundry.AttributeManager;
import edu.msu.nscl.olog.boundry.PropertyManager;
import edu.msu.nscl.olog.entity.Attribute;
import edu.msu.nscl.olog.entity.BitemporalLog;
import edu.msu.nscl.olog.entity.Entry;
import edu.msu.nscl.olog.entity.Log;
import edu.msu.nscl.olog.entity.LogAttribute;
import edu.msu.nscl.olog.entity.Property;
import edu.msu.nscl.olog.entity.XmlAttachment;
import edu.msu.nscl.olog.entity.XmlLog;
import edu.msu.nscl.olog.entity.XmlLogs;
import edu.msu.nscl.olog.entity.XmlProperty;
import edu.msu.nscl.olog.entity.bitemporal.TimeUtils;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 *
 * @author berryman
 */
public final class Mapper {
    private Mapper () {
        
    }
    
    public static BitemporalLog getBitemporalLog(XmlLog xmlLog) throws OlogException{
        Log log = getLog(xmlLog);
        
        Interval interval;
        if (xmlLog.getEventStart() != null && xmlLog.getEventEnd() != null) {
            interval = new Interval(xmlLog.getEventStart().getTime(), xmlLog.getEventEnd().getTime());
        } else if (xmlLog.getEventStart() != null && xmlLog.getEventEnd() == null) {
            interval = TimeUtils.from(new DateTime(xmlLog.getEventStart()));
        } else {
            interval = TimeUtils.fromNow();
        }
        return log.getEntry().log().set(log, interval);
    }
    
    public static BitemporalLog getBitemporalLogMergeInterval(Long id, XmlLog xmlLog) throws OlogException{
        OlogImpl cm = OlogImpl.getInstance();
        BitemporalLog dest = null;
        try {
            dest = cm.findLogById(id, null);
        } catch (OlogException | UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            Logger.getLogger(Mapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (dest == null) {
            throw new OlogException(Response.Status.NOT_FOUND,
                    "Log entry " + id + " could not be updated: Does not exists");
        }
        Log log = getLog(xmlLog);
        
        Interval interval;
        if (xmlLog.getEventStart() != null && xmlLog.getEventEnd() != null) {
            interval = new Interval(xmlLog.getEventStart().getTime(), xmlLog.getEventEnd().getTime());
        } else if (xmlLog.getEventStart() != null && xmlLog.getEventEnd() == null) {
            interval = TimeUtils.from(new DateTime(xmlLog.getEventStart()));
        } else {
            interval = dest.getValidityInterval();
        }
        return log.getEntry().log().set(log, interval);
    }
    
    public static XmlLogs getXmlLogs(List<BitemporalLog> bitemporalLogs){
        XmlLogs xmlLogs = new XmlLogs();
        for( BitemporalLog bitemporalLog: bitemporalLogs){
            XmlLog xmlLog = getXmlLog(bitemporalLog);
            xmlLogs.addLog(xmlLog);
        }
        return xmlLogs;
    }
    
    public static XmlLog getXmlLog(BitemporalLog bitemporalLog){
        Log log = bitemporalLog.getLog();
        XmlLog xmlLog = new XmlLog(
                log.getEntry().getId(),
                log.getVersion(),
                log.getOwner(),
                log.getSource(),
                log.getLevel(),
                log.getState(),
                log.getModifiedDate(),
                log.getEntry().getCreatedDate(),
                bitemporalLog.getValidityInterval().getStart().toDate(),
                bitemporalLog.getValidityInterval().getEnd().toDate(),
                log.getDescription()
        );
        xmlLog.setLogbooks(log.getLogbooks());
        xmlLog.setTags(log.getTags());
        xmlLog.setXmlAttachments(getXmlAttachments(log.getEntry().getId()));
        xmlLog.setXmlProperties(getXmlProperties(log.getAttributes()));

        return xmlLog;
    }
    
    private static Log getLog(XmlLog xmlLog) throws OlogException{
        Entry entry = new Entry(xmlLog.getId());
        Log log = new Log(
        null,
        xmlLog.getVersion(),
        xmlLog.getOwner(),
        xmlLog.getSource(),
        xmlLog.getLevel(),
        xmlLog.getState(),
        xmlLog.getModifiedDate(),
        xmlLog.getDescription(),
        entry); 
        log.setLogbooks(xmlLog.getLogbooks());
        log.setTags(xmlLog.getTags());
        log.setAttributes(getLogAttributes(xmlLog.getXmlProperties()));
        
        return log;
    }
    
    private static Collection<XmlAttachment> getXmlAttachments(long id) {
        try {
            return AttachmentManager.findAll(id).getAttachments();
        } catch (OlogException ex) {
            Logger.getLogger(Mapper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private static Collection<XmlProperty> getXmlProperties(Set<LogAttribute> logAttribute){
         Iterator<LogAttribute> iter = logAttribute.iterator();
        Collection<XmlProperty> xmlProperties = new ArrayList<XmlProperty>();
        while (iter.hasNext()) {
            XmlProperty xmlProperty = new XmlProperty();
            Map<String, String> map = new HashMap<String, String>();
            LogAttribute logattr = iter.next();
            Attribute attr = logattr.getAttribute();
            xmlProperty.setName(attr.getProperty().getName());
            xmlProperty.setId(attr.getProperty().getId());
            for (XmlProperty prevXmlProperty : xmlProperties) {
                if (prevXmlProperty.getId().equals(xmlProperty.getId())) {
                    map = prevXmlProperty.getAttributes();
                }
            }
            map.put(attr.getName(), logattr.getValue());
            xmlProperty.setAttributes(map);
            xmlProperties.add(xmlProperty);
        }
        return xmlProperties;
    }
    
    public static Set<LogAttribute> getLogAttributes(Collection<XmlProperty> xmlProperties) throws OlogException{
        Set<LogAttribute> logattrs = new HashSet<LogAttribute>();
        Long i = 0L;
        Iterator<XmlProperty> iter = xmlProperties.iterator();
        while (iter.hasNext()) {

            XmlProperty p = iter.next();
            Property prop = PropertyManager.findProperty(p.getName());
            if (prop != null) {
                for (Map.Entry<String, String> att : p.getAttributes().entrySet()) {
                    Attribute newAtt = AttributeManager.findAttribute(prop, att.getKey());
                    if (newAtt != null) {
                        LogAttribute logattr = new LogAttribute();
                        logattr.setAttribute(newAtt);
                        logattr.setAttributeId(newAtt.getId());
                        logattr.setValue(att.getValue());
                        logattr.setGroupingNum(i);
                        logattrs.add(logattr);
                    } else {
                        throw new OlogException(Response.Status.NOT_FOUND,
                                "Log entry  property attribute:" + prop.getName() + att.getKey() + " does not exists.");
                    }
                }
                i++;
            } else {
                throw new OlogException(Response.Status.NOT_FOUND,
                        "Log entry prop:" + p.getName() + " does not exists.");
            }

        }
        return logattrs;
    }
}
