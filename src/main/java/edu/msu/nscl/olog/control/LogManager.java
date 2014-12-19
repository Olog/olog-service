/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog.control;

import edu.msu.nscl.olog.entity.Logbook_;
import edu.msu.nscl.olog.entity.XmlProperty;
import edu.msu.nscl.olog.entity.Log_;
import edu.msu.nscl.olog.entity.Tag_;
import edu.msu.nscl.olog.entity.XmlLogs;
import edu.msu.nscl.olog.entity.Tag;
import edu.msu.nscl.olog.entity.Entry_;
import edu.msu.nscl.olog.entity.LogAttribute;
import edu.msu.nscl.olog.entity.Attribute_;
import edu.msu.nscl.olog.entity.Log;
import edu.msu.nscl.olog.entity.Property_;
import edu.msu.nscl.olog.entity.Property;
import edu.msu.nscl.olog.entity.Entry;
import edu.msu.nscl.olog.entity.Logbook;
import edu.msu.nscl.olog.entity.Attribute;
import edu.msu.nscl.olog.entity.LogAttribute_;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import edu.msu.nscl.olog.JPAUtil;
import edu.msu.nscl.olog.OlogException;
import edu.msu.nscl.olog.entity.BitemporalLog;
import edu.msu.nscl.olog.entity.BitemporalLog_;
import edu.msu.nscl.olog.entity.State;
import edu.msu.nscl.olog.entity.XmlLog;
import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 *
 * @author berryman
 */
public class LogManager {

    private static final int maxResults = 1000;

    private LogManager() {
    }

    /**
     * Returns the list of logs in the database.
     *
     * @return Logs
     * @throws OlogException wrapping an SQLException
     */
    @Deprecated
    public static List<Log> findAll() throws OlogException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Log> cq = cb.createQuery(Log.class);
            Root<Log> from = cq.from(Log.class);
            CriteriaQuery<Log> select = cq.select(from);
            Predicate statusPredicate = cb.equal(from.get(Log_.state), State.Active);
            select.where(statusPredicate);
            select.orderBy(cb.desc(from.get(Log_.modifiedDate)));
            TypedQuery<Log> typedQuery = em.createQuery(select);
            List<Log> result = new ArrayList<Log>();
            List<Log> rs = typedQuery.getResultList();

            if (rs != null) {
                Iterator<Log> iterator = rs.iterator();
                while (iterator.hasNext()) {
                    result.add(removeLogsFromLogBooks(iterator.next()));
                }
            }
            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
            throw new OlogException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);
        } finally {
            try {
                if (em.getTransaction() != null && em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            } catch (Exception e) {
            }
            em.close();
        }
    }

    public static List<Log> findLog(MultivaluedMap<String, String> matches) throws OlogException {

        // XXX: should mandate a limit for it, since for big db it can run out of memory
        List<Predicate> andPredicates = new ArrayList<Predicate>();
        List<Predicate> orPredicates = new ArrayList<Predicate>();
        List<String> log_patterns = new ArrayList();
        List<String> id_patterns = new ArrayList();
        List<String> tag_matches = new ArrayList();
        List<String> tag_patterns = new ArrayList();
        List<String> logbook_matches = new ArrayList();
        List<String> logbook_patterns = new ArrayList();
        List<String> property_matches = new ArrayList();
        List<String> property_patterns = new ArrayList();
        List<String> owner_patterns = new ArrayList();
        List<String> source_patterns = new ArrayList();
        Multimap<String, String> date_matches = ArrayListMultimap.create();
        Multimap<String, String> paginate_matches = ArrayListMultimap.create();
        Multimap<String, String> value_patterns = ArrayListMultimap.create();
        Boolean empty = false;
        Boolean history = false;
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Entry> cq = cb.createQuery(Entry.class);
            Root<Entry> from = cq.from(Entry.class);
            Join<Entry,BitemporalLog> bitemporalLog = from.join(Entry_.bitemporalLog, JoinType.LEFT);
            Join<BitemporalLog,Log> logs = bitemporalLog.join(BitemporalLog_.log, JoinType.LEFT);
            Join<Log, LogAttribute> logAttribute = null;
            Join<LogAttribute, Attribute> attribute = null;
            Join<Attribute, Property> property = null;
            
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
                } else if (key.equals("owner")) {
                    for (String m : matchesValues) {
                        if (m.contains("?") || m.contains("*")) {
                            if (m.contains("\\?") || m.contains("\\*")) {
                                m = m.replace("\\", "");
                                owner_patterns.add(m);
                            } else {
                                m = m.replace("*", "%");
                                m = m.replace("?", "_");
                                owner_patterns.add(m);
                            }
                        } else {
                            owner_patterns.add(m);
                        }
                    }
                } else if (key.equals("source")) {
                    for (String m : matchesValues) {
                        if (m.contains("?") || m.contains("*")) {
                            if (m.contains("\\?") || m.contains("\\*")) {
                                m = m.replace("\\", "");
                                source_patterns.add(m);
                            } else {
                                m = m.replace("*", "%");
                                m = m.replace("?", "_");
                                source_patterns.add(m);
                            }
                        } else {
                            source_patterns.add(m);
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
                } else if (key.equals("history")) {
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
            if (!tag_matches.isEmpty() || !tag_patterns.isEmpty()) {
                Predicate tagPredicate = cb.disjunction();
                SetJoin<Log, Tag> tags = logs.join(Log_.tags, JoinType.LEFT);
                if (!tag_matches.isEmpty()) {
                    tagPredicate = cb.or(tags.get(Tag_.name).in(tag_matches), tagPredicate);
                }
                for (String s : tag_patterns) {
                    tagPredicate = cb.or(cb.like(tags.get(Tag_.name), s), tagPredicate);
                }
                orPredicates.add(tagPredicate);
            }

            if (!logbook_matches.isEmpty() || !logbook_patterns.isEmpty()) {
                Predicate logbookPredicate = cb.conjunction();
                SetJoin<Log, Logbook> logbooks = logs.join(Log_.logbooks, JoinType.LEFT);
                if (!logbook_matches.isEmpty()) {
                    logbookPredicate = cb.and(logbookPredicate, logbooks.get(Logbook_.name).in(logbook_matches));
                }
                for (String s : logbook_patterns) {
                    logbookPredicate = cb.and(logbookPredicate, cb.like(logbooks.get(Logbook_.name), s));
                }
                andPredicates.add(logbookPredicate);
            }

            if (!value_patterns.entries().isEmpty()) {
                Predicate propertyAttributePredicate = cb.conjunction();
                for (Map.Entry<String, String> match : value_patterns.entries()) {
                    // Key is coming in as property.attribute
                    List<String> group = Arrays.asList(match.getKey().split("\\."));
                    if (group.size() == 2) {
                        if (logAttribute == null) {
                            logAttribute = logs.join(Log_.attributes, JoinType.LEFT);
                            attribute = logAttribute.join(LogAttribute_.attribute, JoinType.LEFT);
                            property = attribute.join(Attribute_.property, JoinType.LEFT);
                        }
                        propertyAttributePredicate = cb.and(propertyAttributePredicate,
                                cb.like(logAttribute.get(LogAttribute_.value),
                                        match.getValue()), property.get(Property_.name).in(group.get(0),
                                        attribute.get(Attribute_.name).in(group.get(1))));
                    }
                }
                andPredicates.add(propertyAttributePredicate);
            }

            if ((!property_matches.isEmpty() || !property_patterns.isEmpty())) {
                Predicate propertyPredicate = cb.conjunction();
                if (logAttribute == null) {
                    logAttribute = logs.join(Log_.attributes, JoinType.LEFT);
                    attribute = logAttribute.join(LogAttribute_.attribute, JoinType.LEFT);
                    property = attribute.join(Attribute_.property, JoinType.LEFT);
                }
                if (!property_matches.isEmpty()) {
                    propertyPredicate = cb.and(propertyPredicate, property.get(Property_.name).in(property_matches));
                }
                for (String s : property_patterns) {
                    propertyPredicate = cb.and(propertyPredicate, cb.like(property.get(Property_.name), s));
                }
                andPredicates.add(propertyPredicate);
            }

            if (!id_patterns.isEmpty()) {
                Predicate idPredicate = cb.disjunction();
                for (String s : id_patterns) {
                    idPredicate = cb.or(cb.equal(from.get(Entry_.id), Long.valueOf(s)), idPredicate);
                }
                orPredicates.add(idPredicate);
            }

            if (!owner_patterns.isEmpty()) {
                Predicate ownerPredicate = cb.disjunction();
                for (String s : owner_patterns) {
                    ownerPredicate = cb.or(cb.equal(logs.get(Log_.owner), s), ownerPredicate);
                }
                orPredicates.add(ownerPredicate);
            }

            if (!source_patterns.isEmpty()) {
                Predicate sourcePredicate = cb.disjunction();
                for (String s : source_patterns) {
                    sourcePredicate = cb.or(cb.equal(logs.get(Log_.source), s), sourcePredicate);
                }
                orPredicates.add(sourcePredicate);
            }

            if (!log_patterns.isEmpty()) {
                Predicate searchPredicate = cb.disjunction();
                for (String s : log_patterns) {
                    searchPredicate = cb.or(cb.like(logs.get(Log_.description), s), searchPredicate);
                    List<Long> ids = AttachmentManager.findAll(s);
                    if (!ids.isEmpty()) {
                        searchPredicate = cb.or(from.get(Entry_.id).in(ids), searchPredicate);
                    }
                }
                orPredicates.add(searchPredicate);
            }

            if (!date_matches.isEmpty()) {
                Predicate datePredicate = cb.conjunction();
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
                andPredicates.add(datePredicate);
            }

            cq.distinct(true);

            if (history) {
                Predicate statusPredicate = cb.or(cb.equal(logs.get(Log_.state), State.Active), cb.equal(logs.get(Log_.state), State.Inactive));
                orPredicates.add(statusPredicate);
            } else {
                Predicate statusPredicate = cb.equal(logs.get(Log_.state), State.Active);
                andPredicates.add(statusPredicate);
            }

            Predicate finalPredicate = cb.conjunction();

            if (!andPredicates.isEmpty()) {
                Predicate andfinalPredicate = cb.conjunction();
                for (Predicate predicate : andPredicates) {
                    andfinalPredicate = cb.and(andfinalPredicate, predicate);
                }
                finalPredicate = cb.and(finalPredicate, andfinalPredicate);
            }

            if (!orPredicates.isEmpty()) {
                Predicate orfinalPredicate = cb.disjunction();
                for (Predicate predicate : orPredicates) {
                    orfinalPredicate = cb.or(orfinalPredicate, predicate);
                }
                finalPredicate = cb.and(finalPredicate, orfinalPredicate);
            }

            cq.select(from);
            cq.where(finalPredicate);
            cq.groupBy(from);
            cq.orderBy(cb.desc(from.get(Entry_.createdDate)));
            TypedQuery<Entry> typedQuery = em.createQuery(cq);
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
                } else if (limit != null) {
                    typedQuery.setMaxResults(Integer.valueOf(limit));
                } /* else {
                 //set a hardcoded limit so the server will not run out of memory
                 typedQuery.setMaxResults(maxResults);
                 }*/

            }

            List<Log> result = new ArrayList<Log>();

            //result.setCount(JPAUtil.count(em, cq));
            //result.setCount(0L);

            if (empty) {
                em.getTransaction().commit();
                return result;
            }
            List<Entry> rs = typedQuery.getResultList();
            if (rs != null) {
                Iterator<Entry> iterator = rs.iterator();
                while (iterator.hasNext()) {
                    Entry e = iterator.next();
                    if (history) {
                        List<BitemporalLog> all = ((Entry) em.find(Entry.class, e.getId())).log().getHistory();
                        for (BitemporalLog log : all) {
                            result.add(log.getLog());
                        }
                    } else {
                        result.add(e.log().now());
                    }
                }
            }
            em.getTransaction().commit();
            return result;
        } catch (OlogException e) {
            throw new OlogException(Response.Status.INTERNAL_SERVER_ERROR, "JPA exception: " + e);
        } catch (Exception e) {
            throw new OlogException(Response.Status.BAD_REQUEST, "Bad Parameters Exception: " + e);
        } finally {
            try {
                if (em.getTransaction() != null && em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            } catch (Exception e) {
            }
            em.close();
        }
    }

    /**
     * Finds a log and edits in the database by id.
     *
     * @return Log
     * @throws OlogException wrapping an SQLException
     */
    public static Log findLog(Long id) throws OlogException {

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Entry entry = em.find(Entry.class, id);
            if (entry == null) {
                throw new OlogException(Response.Status.NOT_FOUND, "Null Entry: " + id.toString());
            }
            Log result = entry.log().now();
            em.getTransaction().commit();
            return result;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new OlogException(Response.Status.NOT_FOUND,
                    "Exception: " + e);
        } catch (OlogException e) {
            throw new OlogException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);
        } catch (NumberFormatException e) {
            throw new OlogException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);
        } finally {
            try {
                if (em.getTransaction() != null && em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            } catch (Exception e) {
            }
            em.close();
        }
    }

    /**
     * Finds a log and edits in the database by id and version.
     *
     * @return Log
     * @throws OlogException wrapping an SQLException
     */
    public static Log findLogWithVersion(Long id, String version) throws OlogException {

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Entry entry = em.find(Entry.class, id);
            Log result = entry.log().getHistory().get(Integer.getInteger(version).intValue()).getLog();
            em.getTransaction().commit();
            return result;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new OlogException(Response.Status.NOT_FOUND,
                    "Exception: " + e);
        } catch (NumberFormatException e) {
            throw new OlogException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);
        } finally {
            try {
                if (em.getTransaction() != null && em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            } catch (Exception e) {
            }
            em.close();
        }
    }

    /**
     * Creates a Log in the database.
     *
     *
     * @throws OlogException wrapping an SQLException
     */
    public static Log create(Log log) throws OlogException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            if (log.getLevel() == null) {
                throw new OlogException(Response.Status.BAD_REQUEST, "Log must have level");
            }
            if (log.getLogbooks().isEmpty()){
                throw new OlogException(Response.Status.BAD_REQUEST, "Log entry " + log.getEntry().getId() + " must be in at least one logbook.");
            }
            em.getTransaction().begin();
            Log newLog = new Log();
            newLog.setState(State.Active);
            newLog.setLevel(log.getLevel());
            newLog.setOwner(log.getOwner());
            newLog.setDescription(log.getDescription());
            //XXX: Use for psql db olog;
            //XXX: remove new line and tab character since psql do not convert them
            //newLog.setDescription(log.getDescription().replaceAll("\n", " ").replaceAll("\t", " "));
            newLog.setSource(log.getSource());
            em.persist(newLog);
            if (!log.getLogbooks().isEmpty()) {
                Iterator<Logbook> iterator = log.getLogbooks().iterator();
                Set<Logbook> logbooks = new HashSet<Logbook>();
                while (iterator.hasNext()) {
                    String logbookName = iterator.next().getName();
                    Logbook logbook = LogbookManager.findLogbook(logbookName);
                    if (logbook != null) {
                        logbook = em.merge(logbook);
                        logbook.addLog(newLog);
                        logbooks.add(logbook);
                    } else {
                        em.getTransaction().rollback();
                        throw new OlogException(Response.Status.NOT_FOUND,
                                "Log entry " + log.getEntry().getId() + " logbook:" + logbookName + " does not exists.");
                    }
                }
                newLog.setLogbooks(logbooks);
            }
            if (log.getTags() != null) {
                Iterator<Tag> iterator2 = log.getTags().iterator();
                Set<Tag> tags = new HashSet<Tag>();
                while (iterator2.hasNext()) {
                    Tag partialTag = iterator2.next();
                    if (partialTag == null) {
                        throw new OlogException(Response.Status.BAD_REQUEST, "Log tag name null");
                    }
                    String tagName = partialTag.getName();
                    Tag tag = TagManager.findTag(tagName);
                    if (tag != null) {
                        tag = em.merge(tag);
                        tag.addLog(newLog);
                        tags.add(tag);
                    } else {
                        em.getTransaction().rollback();
                        throw new OlogException(Response.Status.NOT_FOUND,
                                "Log entry " + log.getEntry().getId() + " tag:" + tagName + " does not exists.");
                    }
                }
                newLog.setTags(tags);
            }
            if (log.getEntry().getId() != null) {
                Entry entry = (Entry) em.find(Entry.class, log.getEntry().getId());
                newLog.setState(State.Active);
                newLog.setEntry(entry);
                newLog.setVersion(String.valueOf(entry.log().getEvolution().size()));
                em.merge(entry);
            } else {
                Entry entry = new Entry();
                newLog.setState(State.Active);
                entry.addLog(newLog);
                newLog.setEntry(entry);
                newLog.setVersion("1");
                em.persist(entry);
            }
            em.flush();
            em.getTransaction().commit();
            return newLog;
        } catch (OlogException e) {
            throw new OlogException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);
        } finally {
            try {
                if (em.getTransaction() != null && !em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            } catch (Exception e) {
            }
            em.close();
        }

    }

    /**
     * Remove a tag (mark as Inactive).
     *
     * @param id tag id
     */
    public static void remove(Long id) throws OlogException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Entry entry = em.find(Entry.class, id);
            entry.log().end();
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new OlogException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);

        } finally {
            try {
                if (em.getTransaction() != null && em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            } catch (Exception e) {
            }
            em.close();
        }
    }

    /**
     * Remove the logs from logbooks to speed up persistance and reduce memory
     * usage since we do not use that relation.
     *
     * @param log Log
     */
    private static Log removeLogsFromLogBooks(Log log) {
        for (Logbook logbook : log.getLogbooks()) {
            logbook.setLogs(new HashSet<Log>());
        }
        return log;
    }
}
