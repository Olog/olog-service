/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
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
        Predicate statusPredicate = cb.equal(from.get("status"), Status.Active);
        select.where(statusPredicate);
        select.orderBy(cb.desc(from.get("createdDate")));
        TypedQuery<Log> typedQuery = em.createQuery(select);
        JPAUtil.startTransaction(em);
        try {
            Logs result = new Logs();
            List<Log> rs = typedQuery.getResultList();

            if (rs != null) {
                Iterator<Log> iterator = rs.iterator();
                while (iterator.hasNext()) {
                    result.addXmlLog(iterator.next());
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
        List<String> date_matches = new ArrayList();

        em = JPAUtil.getEntityManagerFactory().createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Log> cq = cb.createQuery(Log.class);
        Root<Log> from = cq.from(Log.class);
        SetJoin<Log, Tag> tags = from.join(Log_.tags);
        SetJoin<Log, Logbook> logbooks = from.join(Log_.logbooks);

        for (Map.Entry<String, List<String>> match : matches.entrySet()) {
            String key = match.getKey().toLowerCase();
            Collection<String> matchesValues = match.getValue();
            if (key.equals("search")) {
                log_patterns.addAll(match.getValue());
            } else if (key.equals("tag")) {
                for (String m : matchesValues) {
                    if (m.contains("?") || m.contains("*")) {
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
                        property_patterns.add(m);
                    } else {
                        property_matches.add(m);
                    }
                }
                //} else if (key.equals("page")) {
                //    logPaginate_matches.putAll(key, match.getValue());
                //} else if (key.equals("limit")) {
                //    logPaginate_matches.putAll(key, match.getValue());
                //} else if (key.equals("start")) {
                //    date_matches.putAll(key, match.getValue());
                //} else if (key.equals("end")) {
                //    date_matches.putAll(key, match.getValue());
                //} else {
                //    value_matches.putAll(key, match.getValue());
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
        }
        //Predicate p3 = cb.between(r.get("fetchDate").as(Date.class),
        //    start.getTime(), end.getTime());

        cq.distinct(true);

        Predicate statusPredicate = cb.equal(from.get("status"), Status.Active);

        cq.where(cb.and(statusPredicate, p1, p2));
        cq.orderBy(cb.desc(from.get("createdDate")));
        TypedQuery<Log> typedQuery = em.createQuery(cq);

        JPAUtil.startTransaction(em);

        try {
            Logs result = new Logs();
            List<Log> rs = typedQuery.getResultList();

            if (rs != null) {
                Iterator<Log> iterator = rs.iterator();
                while (iterator.hasNext()) {
                    result.addXmlLog(iterator.next());
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

        from.fetch(
                "children");
        CriteriaQuery<Log> select = cq.select(from);
        Predicate idPredicate = cb.equal(from.get("id"), id);

        select.where(idPredicate);

        select.orderBy(cb.desc(from.get("createdDate")));
        TypedQuery<Log> typedQuery = em.createQuery(select);

        JPAUtil.startTransaction(em);


        try {
            Log result = null;
            List<Log> rs = typedQuery.getResultList();
            if (rs != null) {
                Iterator<Log> iterator = rs.iterator();
                while (iterator.hasNext()) {
                    result = iterator.next();
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

        try {
            Log parentLog = (Log) JPAUtil.findByID(Log.class, log.getId());
            if (parentLog
                    != null) {
                log.setParent(parentLog);
                parentLog.setStatus(Status.Inactive);
                JPAUtil.update(parentLog);
                JPAUtil.save(log);
                return findLog(log.getId());
            } else {
                JPAUtil.save(log);
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
            Log fullLog = (Log) JPAUtil.findByID(Log.class, id);
            JPAUtil.remove(Log.class, fullLog.getInternalId());
        } catch (Exception e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);

        }
    }
}
