/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
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

    private static EntityManager em = null;

    private LogManager() {
    }

    /**
     * Returns the list of logs in the database.
     *
     * @return Logs
     * @throws CFException wrapping an SQLException
     */
    public static Logs findAll() throws CFException {
        em = JPAUtil.getEntityManagerFactory().createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Log> cq = cb.createQuery(Log.class);
        Root<Log> from = cq.from(Log.class);
        CriteriaQuery<Log> select = cq.select(from);
        Predicate statusPredicate = cb.equal(from.get(Log_.state), State.Active);
        select.where(statusPredicate);
        select.orderBy(cb.desc(from.get(Log_.createdDate)));
        TypedQuery<Log> typedQuery = em.createQuery(select);
        JPAUtil.startTransaction(em);
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
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);
        } finally {
            JPAUtil.finishTransacton(em);
        }
    }

    public static Logs findLog(MultivaluedMap<String, String> matches) throws CFException {


        List<String> log_patterns = new ArrayList();
        List<String> logbook_matches = new ArrayList();
        List<String> logbook_patterns = new ArrayList();
        List<String> tag_matches = new ArrayList();
        List<String> tag_patterns = new ArrayList();
        List<String> property_matches = new ArrayList();
        List<String> property_patterns = new ArrayList();
        Multimap<String, String> date_matches = ArrayListMultimap.create();
        Multimap<String, String> paginate_matches = ArrayListMultimap.create();

        em = JPAUtil.getEntityManagerFactory().createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Log> cq = cb.createQuery(Log.class);
        Root<Log> from = cq.from(Log.class);
        //Root<Log> fromParent = cq.from(Log.class);
        //from.fetch(Log_.tags, JoinType.LEFT);
        Join<Log, Log> parent = from.join(Log_.parent, JoinType.LEFT);
        SetJoin<Log, Tag> tags = from.join(Log_.tags, JoinType.LEFT);
        SetJoin<Log, Logbook> logbooks = from.join(Log_.logbooks, JoinType.LEFT);

        for (Map.Entry<String, List<String>> match : matches.entrySet()) {
            String key = match.getKey().toLowerCase();
            Collection<String> matchesValues = match.getValue();
            if (key.equals("search")) {
                log_patterns.addAll(match.getValue());
            } else if (key.equals("tag")) {
                for (String m : matchesValues) {
                    if (m.contains("?") || m.contains("*")) {
                        m = m.replace("*", "%");
                        m = m.replace("?", "_");
                        tag_patterns.add(m);
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
                        m = m.replace("*", "%");
                        m = m.replace("?", "_");
                        logbook_patterns.add(m);
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
                        m = m.replace("*", "%");
                        m = m.replace("?", "_");
                        property_patterns.add(m);
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
            }
        }
        //cb.or() causes an error in eclipselink with p1 as first argument
        Predicate p1 = cb.disjunction();
        if (!tag_matches.isEmpty()) {
            p1 = cb.or(tags.get(Tag_.name).in(tag_matches), p1);
        }
        for (String s : tag_patterns) {
            p1 = cb.or(cb.like(tags.get(Tag_.name), s), p1);
        }

        Predicate p2 = cb.disjunction();
        if (!logbook_matches.isEmpty()) {
            p2 = cb.and(p2, logbooks.get(Logbook_.name).in(logbook_matches));
        }
        for (String s : logbook_patterns) {
            p2 = cb.and(p2, cb.like(logbooks.get(Logbook_.name), s));
        }

        Predicate p3 = cb.disjunction();
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
                p3 = cb.between(from.get(Log_.createdDate),
                        jStart,
                        jEndNow);
            } else if (start == null && end != null) {
                Date jStart1970 = new java.util.Date(0);
                Date jEnd = new java.util.Date(Long.valueOf(end) * 1000);
                p3 = cb.between(from.get(Log_.createdDate),
                        jStart1970,
                        jEnd);
            } else {
                Date jStart = new java.util.Date(Long.valueOf(start) * 1000);
                Date jEnd = new java.util.Date(Long.valueOf(end) * 1000);
                p3 = cb.between(from.get(Log_.createdDate),
                        jStart,
                        jEnd);
            }
        }

        cq.distinct(true);

        Predicate statusPredicate = cb.equal(from.get(Log_.state), State.Active);
        Predicate finalPredicate = cb.and(statusPredicate, p2, p3, p1);
        cq.where(finalPredicate);
        //https://bugs.eclipse.org/bugs/show_bug.cgi?id=381250
        cq.orderBy(cb.desc(cb.selectCase().when(cb.isNotNull(from.get(Log_.parent)),parent.get(Log_.createdDate)).otherwise(from.get(Log_.createdDate))));
        TypedQuery<Log> typedQuery = em.createQuery(cq);

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


        JPAUtil.startTransaction(em);

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
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);
        } finally {
            JPAUtil.finishTransacton(em);
        }
    }

    /**
     * Finds a log and edits in the database by id.
     *
     * @return Log
     * @throws CFException wrapping an SQLException
     */
    public static Log findLog(Long id) throws CFException {
        em = JPAUtil.getEntityManagerFactory().createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Log> cq = cb.createQuery(Log.class);
        Root<Log> from = cq.from(Log.class);

        from.fetch("children", JoinType.LEFT);
        CriteriaQuery<Log> select = cq.select(from);
        Predicate idPredicate = cb.equal(from.get(Log_.id), id);

        select.where(idPredicate);

        select.orderBy(cb.desc(from.get(Log_.createdDate)));
        TypedQuery<Log> typedQuery = em.createQuery(select);

        JPAUtil.startTransaction(em);


        try {
            Log result = null;
            List<Log> rs = typedQuery.getResultList();
            if (rs != null) {
                Iterator<Log> iterator = rs.iterator();
                while (iterator.hasNext()) {
                    result = iterator.next();
                    if (result.getChildren().size() > 0) {
                        Collection<Log> siblings = result.getChildren();
                        result = Collections.max(siblings);
                    }
                }
            }

            return result;
        } catch (Exception e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);
        } finally {
            JPAUtil.finishTransacton(em);
        }
    }

    /**
     * Creates a Log in the database.
     *
     * @param name name of tag
     * @param owner owner of tag
     * @throws CFException wrapping an SQLException
     */
    public static Log create(Log log) throws CFException {
        Log parentLog = null;
        if (log.getLogbooks() != null) {
            Iterator<Logbook> iterator = log.getLogbooks().iterator();
            Set<Logbook> logbooks = new HashSet<Logbook>();
            while (iterator.hasNext()) {
                logbooks.add(LogbookManager.findLogbook(iterator.next().getName()));
            }
            log.setLogbooks(logbooks);
        }
        if (log.getTags() != null) {
            Iterator<Tag> iterator2 = log.getTags().iterator();
            Set<Tag> tags = new HashSet<Tag>();
            while (iterator2.hasNext()) {
                tags.add(TagManager.findTag(iterator2.next().getName()));
            }
            log.setTags(tags);
        }
        try {
            if (log.getId() != null) {
                parentLog = (Log) JPAUtil.findByID(Log.class, log.getId());
            }
            if (parentLog != null) {
                if (parentLog.getChildren() != null) {
                    Collection<Log> siblings = parentLog.getChildren();
                    Iterator<Log> iteratorChild = siblings.iterator();
                    while (iteratorChild.hasNext()) {
                        Log sibling = iteratorChild.next();
                        sibling.setState(State.Inactive);
                        JPAUtil.update(sibling);
                    }
                    siblings.add(log);
                    parentLog.setChildren(siblings);
                }
                log.setState(State.Active);
                log.setId(null);
                log.setParent(parentLog);
                parentLog.setState(State.Inactive);
                JPAUtil.update(parentLog);
                return findLog(log.getId());
            } else {
                log.setState(State.Active);
                JPAUtil.update(log);
                return findLog(log.getId());
            }
        } catch (Exception e) {

            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);
        }

    }

    /**
     * Remove a tag (mark as Inactive).
     *
     * @param name tag name
     */
    public static void remove(Long id) throws CFException {
        try {
            Log log = (Log) JPAUtil.findByID(Log.class, id);
            if (log
                    != null) {
                if (log.getChildren() != null) {
                    Collection<Log> siblings = log.getChildren();
                    Iterator<Log> iteratorChild = siblings.iterator();
                    while (iteratorChild.hasNext()) {
                        Log sibling = iteratorChild.next();
                        sibling.setState(State.Inactive);
                        JPAUtil.update(sibling);
                    }
                    log.setChildren(siblings);
                }
                log.setState(State.Inactive);
                JPAUtil.update(log);
            }
        } catch (Exception e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);

        }
    }
}
