/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.sound.midi.SysexMessage;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 *
 * @author berryman
 */
public class LogManagerTest {

    private static EntityManager em = null;

    private LogManagerTest() {
    }

    /**
     * Returns the list of logs in the database.
     *
     * @return Logs
     * @throws OlogException wrapping an SQLException
     */
    public static Logs findAll() throws OlogException {
        em = JPAUtilTest.getEntityManagerFactory().createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Log> cq = cb.createQuery(Log.class);
        Root<Log> from = cq.from(Log.class);
        CriteriaQuery<Log> select = cq.select(from);
        Predicate statusPredicate = cb.equal(from.get(Log_.state), State.Active);
        select.where(statusPredicate);
        select.orderBy(cb.desc(from.get(Log_.modifiedDate)));
        TypedQuery<Log> typedQuery = em.createQuery(select);
        JPAUtilTest.startTransaction(em);
        try {
            Logs result = new Logs();
            List<Log> rs = typedQuery.getResultList();

            if (rs != null) {
                Iterator<Log> iterator = rs.iterator();
                while (iterator.hasNext()) {
                    result.addLog(iterator.next());
                }
            }

            return result;
        } catch (Exception e) {
            throw new OlogException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);
        } finally {
            JPAUtilTest.finishTransacton(em);
        }
    }

    public static Logs findLog(MultivaluedMap<String, String> matches) throws OlogException {


        List<String> log_patterns = new ArrayList();
        List<String> id_patterns = new ArrayList();
        List<String> tag_matches = new ArrayList();
        List<String> tag_patterns = new ArrayList();
        List<String> logbook_matches = new ArrayList();
        List<String> logbook_patterns = new ArrayList();
        List<String> property_matches = new ArrayList();
        List<String> property_patterns = new ArrayList();
        Multimap<String, String> date_matches = ArrayListMultimap.create();
        Multimap<String, String> paginate_matches = ArrayListMultimap.create();
        Multimap<String, String> value_patterns = ArrayListMultimap.create();
        Boolean empty = false;
        Boolean history = false;

        em = JPAUtilTest.getEntityManagerFactory().createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
        Root<Entry> from = cq.from(Entry.class);
        Join<Entry,Log> logs = from.join(Entry_.logs, JoinType.INNER);
        SetJoin<Log, Tag> tags = logs.join(Log_.tags, JoinType.LEFT);
        SetJoin<Log, Logbook> logbooks = logs.join(Log_.logbooks, JoinType.INNER);
        Join<Attribute, Property> property = logs.join(Log_.attributes, JoinType.LEFT).join(LogAttribute_.attribute, JoinType.LEFT).join(Attribute_.property, JoinType.LEFT);
        Join<LogAttribute, Attribute> attribute = logs.join(Log_.attributes, JoinType.LEFT).join(LogAttribute_.attribute, JoinType.LEFT);
        Join<Log, LogAttribute> logAttribute = logs.join(Log_.attributes, JoinType.LEFT);

        for (Map.Entry<String, List<String>> match : matches.entrySet()) {
            String key = match.getKey().toLowerCase();
            Collection<String> matchesValues = match.getValue();
            if (key.equals("search")) {
                for (String m : matchesValues) {
                    if (m.contains("?") || m.contains("*")) {
                        if (m.contains("\\?") || m.contains("\\*")) {
                            m = m.replace("\\", "");
                            log_patterns.add(m);
                        } else {
                            m = m.replace("*", "%");
                            m = m.replace("?", "_");
                            log_patterns.add(m);
                        }
                    } else {
                        log_patterns.add(m);
                    }
                }
            } else if (key.equals("id")) {
                for (String m : matchesValues) {
                    if (m.contains("?") || m.contains("*")) {
                        if (m.contains("\\?") || m.contains("\\*")) {
                            m = m.replace("\\", "");
                            id_patterns.add(m);
                        } else {
                            m = m.replace("*", "%");
                            m = m.replace("?", "_");
                            id_patterns.add(m);
                        }
                    } else {
                        id_patterns.add(m);
                    }
                }
            } else if (key.equals("tag")) {
                for (String m : matchesValues) {
                    if (m.contains("?") || m.contains("*")) {
                        if (m.contains("\\?") || m.contains("\\*")) {
                            m = m.replace("\\", "");
                            tag_matches.add(m);
                        } else {
                            m = m.replace("*", "%");
                            m = m.replace("?", "_");
                            tag_patterns.add(m);
                        }
                    } else {
                        tag_matches.add(m);
                    }
                }
                if (tag_matches.size() == 1) {
                    String match1 = tag_matches.get(0);
                    tag_matches.clear();
                    tag_matches.addAll(Arrays.asList(match1.split(",")));
                }
            } else if (key.equals("logbook")) {
                for (String m : matchesValues) {
                    if (m.contains("?") || m.contains("*")) {
                        if (m.contains("\\?") || m.contains("\\*")) {
                            m = m.replace("\\", "");
                            logbook_matches.add(m);
                        } else {
                            m = m.replace("*", "%");
                            m = m.replace("?", "_");
                            logbook_patterns.add(m);
                        }
                    } else {
                        logbook_matches.add(m);
                    }
                }
                if (logbook_matches.size() == 1) {
                    String match1 = logbook_matches.get(0);
                    logbook_matches.clear();
                    logbook_matches.addAll(Arrays.asList(match1.split(",")));
                }
            } else if (key.equals("property")) {
                for (String m : matchesValues) {
                    if (m.contains("?") || m.contains("*")) {
                        if (m.contains("\\?") || m.contains("\\*")) {
                            m = m.replace("\\", "");
                            property_matches.add(m);
                        } else {
                            m = m.replace("*", "%");
                            m = m.replace("?", "_");
                            property_patterns.add(m);
                        }
                    } else {
                        property_matches.add(m);
                    }
                }
            } else if (key.equals("page")) {
                paginate_matches.putAll(key, match.getValue());
            } else if (key.equals("limit")) {
                paginate_matches.putAll(key, match.getValue());
            } else if (key.equals("start")) {
                date_matches.putAll(key, match.getValue());
            } else if (key.equals("end")) {
                date_matches.putAll(key, match.getValue());
            } else if (key.equals("empty")) {
                empty = true;
            } else if (key.equals("history")){
                history = true;
            } else {
                Collection<String> cleanedMatchesValues = new HashSet<String>();
                for (String m : matchesValues) {
                    if (m.contains("?") || m.contains("*")) {
                        if (m.contains("\\?") || m.contains("\\*")) {
                            m = m.replace("\\", "");
                            cleanedMatchesValues.add(m);
                        } else {
                            m = m.replace("*", "%");
                            m = m.replace("?", "_");
                            cleanedMatchesValues.add(m);
                        }
                    } else {
                        cleanedMatchesValues.add(m);
                    }
                }
                value_patterns.putAll(key, cleanedMatchesValues);
            }
        }

        //cb.or() causes an error in eclipselink with p1 as first argument
        Predicate tagPredicate = cb.disjunction();
        if (!tag_matches.isEmpty()) {
            tagPredicate = cb.or(tags.get(Tag_.name).in(tag_matches), tagPredicate);
        }
        for (String s : tag_patterns) {
            tagPredicate = cb.or(cb.like(tags.get(Tag_.name), s), tagPredicate);
        }

        Predicate logbookPredicate = cb.disjunction();
        if (!logbook_matches.isEmpty()) {
            logbookPredicate = cb.and(logbookPredicate, logbooks.get(Logbook_.name).in(logbook_matches));
        }
        for (String s : logbook_patterns) {
            logbookPredicate = cb.and(logbookPredicate, cb.like(logbooks.get(Logbook_.name), s));
        }

        Predicate propertyPredicate = cb.disjunction();
        if (!property_matches.isEmpty()) {
            propertyPredicate = cb.and(propertyPredicate, property.get(Property_.name).in(property_matches));
        }
        for (String s : property_patterns) {
            propertyPredicate = cb.and(propertyPredicate, cb.like(property.get(Property_.name), s));
        }

        Predicate propertyAttributePredicate = cb.disjunction();
        for (Map.Entry<String, String> match : value_patterns.entries()) {
            // Key is coming in as property.attribute
            List<String> group = Arrays.asList(match.getKey().split("\\."));
            if (group.size() == 2) {
                propertyAttributePredicate = cb.and(propertyAttributePredicate,
                        cb.like(logAttribute.get(LogAttribute_.value),
                                match.getValue()), property.get(Property_.name).in(group.get(0),
                        attribute.get(Attribute_.name).in(group.get(1))));
            }
        }

        Predicate idPredicate = cb.disjunction();
        for (String s : id_patterns) {
            idPredicate = cb.or(cb.equal(from.get(Entry_.id), Long.valueOf(s)), idPredicate);
        }

        Predicate searchPredicate = cb.disjunction();
        for (String s : log_patterns) {
            searchPredicate = cb.or(cb.like(logs.get(Log_.description), s), searchPredicate);
            List<Long> ids = AttachmentManagerTest.findAll(s);
            if (!ids.isEmpty()) {
                searchPredicate = cb.or(from.get(Entry_.id).in(ids), searchPredicate);
            }
        }

        Predicate datePredicate = cb.disjunction();
        if (!date_matches.isEmpty()) {
            String start = null, end = null;
            for (Map.Entry<String, Collection<String>> match : date_matches.asMap().entrySet()) {
                if (match.getKey().toLowerCase().equals("start")) {
                    start = match.getValue().iterator().next();
                }
                if (match.getKey().toLowerCase().equals("end")) {
                    end = match.getValue().iterator().next();
                }
            }
            if (start != null && end == null) {
                Date jStart = new java.util.Date(Long.valueOf(start) * 1000);
                Date jEndNow = new java.util.Date(Calendar.getInstance().getTime().getTime());
                datePredicate = cb.between(from.get(Entry_.createdDate),
                        jStart,
                        jEndNow);
            } else if (start == null && end != null) {
                Date jStart1970 = new java.util.Date(0);
                Date jEnd = new java.util.Date(Long.valueOf(end) * 1000);
                datePredicate = cb.between(from.get(Entry_.createdDate),
                        jStart1970,
                        jEnd);
            } else {
                Date jStart = new java.util.Date(Long.valueOf(start) * 1000);
                Date jEnd = new java.util.Date(Long.valueOf(end) * 1000);
                datePredicate = cb.between(from.get(Entry_.createdDate),
                        jStart,
                        jEnd);
            }
        }

        cq.distinct(true);
        Predicate statusPredicate = cb.disjunction();
        if(history){
            statusPredicate = cb.or(cb.equal(logs.get(Log_.state), State.Active), cb.equal(logs.get(Log_.state), State.Inactive));
        } else {
            statusPredicate = cb.equal(logs.get(Log_.state), State.Active);
        }
        Predicate finalPredicate = cb.and(statusPredicate, logbookPredicate, tagPredicate, propertyPredicate, propertyAttributePredicate, datePredicate, searchPredicate, idPredicate);
        cq.multiselect(from,logs);
        cq.where(finalPredicate);
       // cq.groupBy(from);
        cq.orderBy(cb.desc(from.get(Entry_.createdDate)));
        TypedQuery<Tuple> typedQuery = em.createQuery(cq);

        if (!paginate_matches.isEmpty()) {
            String page = null, limit = null;
            for (Map.Entry<String, Collection<String>> match : paginate_matches.asMap().entrySet()) {
                if (match.getKey().toLowerCase().equals("limit")) {
                    limit = match.getValue().iterator().next();
                }
                if (match.getKey().toLowerCase().equals("page")) {
                    page = match.getValue().iterator().next();
                }
            }
            if (limit != null && page != null) {
                Integer offset = Integer.valueOf(page) * Integer.valueOf(limit) - Integer.valueOf(limit);
                typedQuery.setFirstResult(offset);
                typedQuery.setMaxResults(Integer.valueOf(limit));
            }
        }

        JPAUtilTest.startTransaction(em);

        try {
            Logs result = new Logs();

            //result.setCount(JPAUtilTest.count(em, cq));
            result.setCount(0L);

            if (empty) {
                return result;
            }

            List<Tuple> rs = typedQuery.getResultList();
            Map<Long, Integer> versionMap = new HashMap<Long, Integer>();

            if (rs != null) {
                Iterator<Tuple> iterator = rs.iterator();
                while (iterator.hasNext()) {
                    Tuple tuple = iterator.next();
                    Entry e = tuple.get(0, Entry.class);
                    List<Log> all= ((Entry)JPAUtilTest.findByID(Entry.class, e.getId())).getLogs();

                    if (history) {
                        for (Log log : all) {
                            int version;
                            if (versionMap.containsKey(e.getId())) {
                                version = versionMap.get(e.getId()) + 1;
                            } else {
                                version = 1;
                            }
                            versionMap.put(e.getId(), version);
                            log.setVersion(String.valueOf(version));

                            log = populateLog(log);
                            result.addLog(log);
                        }
                    } else {
                        Log log = tuple.get(1, Log.class);
                        log.setVersion(String.valueOf(all.size()));
                        log = populateLog(log);
                        result.addLog(log);
                    }
                }
            }

            return result;
        } catch (OlogException e) {
            throw new OlogException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);
        } finally {
            JPAUtilTest.finishTransacton(em);
        }
    }

    private static Log populateLog(Log log) throws OlogException {

        log.setXmlAttachments(AttachmentManagerTest.findAll(log.getEntryId()).getAttachments());
        Iterator<LogAttribute> iter = log.getAttributes().iterator();
        Set<XmlProperty> xmlProperties = new HashSet<XmlProperty>();
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
        log.setXmlProperties(xmlProperties);
        return log;
    }

    /**
     * Finds a log and edits in the database by id.
     *
     * @return Log
     * @throws OlogException wrapping an SQLException
     */
    public static Log findLog(Long id) throws OlogException {
        try {
            Entry entry = (Entry) JPAUtilTest.findByID(Entry.class, id);
            Collection<Log> logs = entry.getLogs();
            Log result = Collections.max(logs);
            result.setVersion(String.valueOf(logs.size()));
            result.setXmlAttachments(AttachmentManagerTest.findAll(result.getEntryId()).getAttachments());
            Iterator<LogAttribute> iter = result.getAttributes().iterator();
            Set<XmlProperty> xmlProperties = new HashSet<XmlProperty>();
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
            result.setXmlProperties(xmlProperties);
            return result;
        } catch (Exception e) {
            throw new OlogException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);
        }
    }

    /**
     * Creates a Log in the database.
     *
     * @param name name of tag
     * @param owner owner of tag
     * @throws OlogException wrapping an SQLException
     */
    public static Log create(Log log) throws OlogException {
        em = JPAUtilTest.getEntityManagerFactory().createEntityManager();
        JPAUtilTest.startTransaction(em);
        Log newLog = new Log();
        newLog.setState(State.Active);
        newLog.setLevel(log.getLevel());
        newLog.setOwner(log.getOwner());
        newLog.setDescription(log.getDescription());
        newLog.setSource(log.getSource());
        em.persist(newLog);
        if (!log.getLogbooks().isEmpty()) {
            Iterator<Logbook> iterator = log.getLogbooks().iterator();
            Set<Logbook> logbooks = new HashSet<Logbook>();
            while (iterator.hasNext()) {
                String logbookName = iterator.next().getName();
                Logbook logbook = LogbookManagerTest.findLogbook(logbookName);
                if (logbook != null) {
                    logbook = em.merge(logbook);
                    logbook.addLog(newLog);
                    logbooks.add(logbook);
                } else {
                    throw new OlogException(Response.Status.NOT_FOUND,
                            "Log entry " + log.getId() + " logbook:" + logbookName + " does not exists.");
                }
            }
            newLog.setLogbooks(logbooks);
        } else {
            throw new OlogException(Response.Status.NOT_FOUND,
                    "Log entry " + log.getId() + " must be in at least one logbook.");
        }
        if (log.getTags() != null) {
            Iterator<Tag> iterator2 = log.getTags().iterator();
            Set<Tag> tags = new HashSet<Tag>();
            while (iterator2.hasNext()) {
                String tagName = iterator2.next().getName();
                Tag tag = TagManagerTest.findTag(tagName);
                if (tag != null) {
                    tag = em.merge(tag);
                    tag.addLog(newLog);
                    tags.add(tag);
                } else {
                    throw new OlogException(Response.Status.NOT_FOUND,
                            "Log entry " + log.getId() + " tag:" + tagName + " does not exists.");
                }
            }
            newLog.setTags(tags);
        }
        try {
            if (log.getEntryId() != null) {
                Entry entry = (Entry) JPAUtilTest.findByID(Entry.class, log.getEntryId());
                if (entry.getLogs() != null) {
                    List<Log> logs = entry.getLogs();
                    ListIterator<Log> iterator = logs.listIterator();
                    while (iterator.hasNext()) {
                        Log sibling = iterator.next();
                        sibling = em.merge(sibling);
                        sibling.setState(State.Inactive);
                        iterator.set(sibling);
                    }
                    entry.addLog(newLog);
                }
                newLog.setState(State.Active);
                newLog.setEntry(entry);
                em.merge(entry);
            } else {
                Entry entry = new Entry();
                newLog.setState(State.Active);
                entry.addLog(newLog);
                newLog.setEntry(entry);
                em.persist(entry);
            }
            em.flush();
            if (log.getXmlProperties() != null) {
                Set<LogAttribute> logattrs = new HashSet<LogAttribute>();
                Long i = 0L;
                for (XmlProperty p : log.getXmlProperties()) {
                    Property prop = PropertyManagerTest.findProperty(p.getName());
                    if(prop != null){
                        for (Map.Entry<String, String> att : p.getAttributes().entrySet()) {
                            Attribute newAtt = AttributeManagerTest.findAttribute(prop, att.getKey());
                            if(newAtt != null){
                                LogAttribute logattr = new LogAttribute();
                                logattr.setAttribute(newAtt);
                                logattr.setLog(newLog);
                                logattr.setAttributeId(newAtt.getId());
                                logattr.setLogId(newLog.getId());
                                logattr.setValue(att.getValue());
                                logattr.setGroupingNum(i);
                                em.persist(logattr);
                                logattrs.add(logattr);
                            }else{
                                throw new OlogException(Response.Status.NOT_FOUND,
                                        "Log entry " + log.getId() + " property attribute:" + prop.getName() + newAtt.getName() + " does not exists.");
                            }
                        }
                        newLog.setAttributes(logattrs);
                        i++;
                    } else {
                        throw new OlogException(Response.Status.NOT_FOUND,
                                "Log entry " + log.getId() + " prop:" + prop.getName() + " does not exists.");
                    }
                }
            }
            newLog.setXmlProperties(log.getXmlProperties());
            JPAUtilTest.finishTransacton(em);
            return newLog;
        } catch (OlogException e) {
            JPAUtilTest.transactionFailed(em);
            throw new OlogException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);
        }

    }

    /**
     * Remove a tag (mark as Inactive).
     *
     * @param name tag name
     */
    public static void remove(Long id) throws OlogException {
        try {
            Entry entry = (Entry) JPAUtilTest.findByID(Entry.class, id);
            if (entry != null) {
                if (entry.getLogs() != null) {
                    List<Log> logs = entry.getLogs();
                    ListIterator<Log> iterator = logs.listIterator();
                    while (iterator.hasNext()) {
                        Log sibling = iterator.next();
                        sibling.setState(State.Inactive);
                        iterator.set(sibling);
                        JPAUtilTest.update(sibling);
                    }
                }
            }
        } catch (Exception e) {
            throw new OlogException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);

        }
    }
}
