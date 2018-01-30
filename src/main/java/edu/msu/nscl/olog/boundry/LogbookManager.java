/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog.boundry;

import edu.msu.nscl.olog.entity.Logbooks;
import edu.msu.nscl.olog.entity.Logbook_;
import edu.msu.nscl.olog.entity.Logbook;
import com.google.common.collect.Iterables;
import edu.msu.nscl.olog.JPAUtil;
import edu.msu.nscl.olog.OlogException;
import edu.msu.nscl.olog.entity.State;

import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.ws.rs.core.Response;

/**
 *
 * @author berryman
 */
public class LogbookManager {

    private LogbookManager() {
    }

    /**
     * Returns the list of logbooks in the database.
     *
     * @return Logbooks
     * @throws OlogException wrapping an SQLException
     */
    public static Logbooks findAll() throws OlogException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Logbook> cq = cb.createQuery(Logbook.class);
            Root<Logbook> from = cq.from(Logbook.class);
            CriteriaQuery<Logbook> select = cq.select(from);
            Predicate statusPredicate = cb.equal(from.get(Logbook_.state), State.Active);
            select.where(statusPredicate);
            select.orderBy(cb.asc(from.get(Logbook_.name)));
            TypedQuery<Logbook> typedQuery = em.createQuery(select);
            Logbooks result = new Logbooks();
            List<Logbook> rs = typedQuery.getResultList();
            if (rs != null) {
                result.setLogbooks(rs);
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

    /**
     * Finds a logbook in the database by name.
     *
     * @return Logbook
     * @throws OlogException wrapping an SQLException
     */
    @Deprecated
    public static Logbook findLogbookOld(String name) throws OlogException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Logbook> cq = cb.createQuery(Logbook.class);
            Root<Logbook> from = cq.from(Logbook.class);
            CriteriaQuery<Logbook> select = cq.select(from);
            Predicate namePredicate = cb.equal(from.get("name"), name);
            //Predicate statusPredicate = cb.equal(from.get("state"), State.Active);
            select.where(namePredicate);
            select.orderBy(cb.asc(from.get("name")));
            TypedQuery<Logbook> typedQuery = em.createQuery(select);
            Logbook result = null;
            List<Logbook> rs = typedQuery.getResultList();
            if (rs != null) {
                Iterator<Logbook> iterator = rs.iterator();
                while (iterator.hasNext()) {
                    result = iterator.next();
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

    /**
     * XXX: The biggest bottom neck here is that if the logbook contains more
     * than 1000 logs, the merge times goes up exponentially, to prevent this,
     * lets get a logbook without logs, removing the botton neck. this works
     * without needing to remove the Logbook cache, since we never marshall the
     * Log objects for the Logbook if this change, this approach will not work
     * and the cache should be remove at the end of the log creation by
     * em.getEntityManagerFactory().getCache().evict(Logbook.class);\ Finds a
     * logbook in the database by name.
     *
     * @return Logbook
     * @throws OlogException wrapping an SQLException
     */
    public static Logbook findLogbook(String name) throws OlogException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Logbook> cq = cb.createQuery(Logbook.class);
            Root<Logbook> from = cq.from(Logbook.class);
            Path<Long> idPath = from.get(Logbook_.id);
            Path<String> namePath = from.get(Logbook_.name);
            Path<String> ownerPath = from.get(Logbook_.owner);
            CriteriaQuery<Logbook> select = cq.select(cb.construct(Logbook.class, idPath, namePath, ownerPath, from.get(Logbook_.state)));
            Predicate namePredicate = cb.equal(from.get(Logbook_.name), name);
            //Predicate statusPredicate = cb.equal(from.get("state"), State.Active);
            select.where(namePredicate);
            select.orderBy(cb.asc(from.get(Logbook_.name)));
            TypedQuery<Logbook> typedQuery = em.createQuery(select);
            Logbook result = null;
            List<Logbook> rs = typedQuery.getResultList();
            if (rs != null && !rs.isEmpty()) {
                result = Iterables.getLast(rs);
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

    /**
     * Creates a logbook in the database.
     *
     * @param name name of logbook
     * @param owner owner of logbook
     * @throws OlogException wrapping an SQLException
     */
    public static Logbook create(String name, String owner) throws OlogException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Logbook xmlLogbook = new Logbook();
            Logbook logbook = findLogbook(name);
            if (logbook != null) {
                logbook.setState(State.Active);
                logbook.setOwner(owner);
                logbook = em.merge(logbook);
                em.getTransaction().commit();
                return logbook;
            } else {
                xmlLogbook.setName(name);
                xmlLogbook.setOwner(owner);
                xmlLogbook.setState(State.Active);
                em.persist(xmlLogbook);
                em.getTransaction().commit();
                return xmlLogbook;
            }
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
     * Remove a logbook (mark as Inactive).
     *
     * @param name logbook name
     */
    public static void remove(String name) throws OlogException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Logbook logbook = findLogbook(name);
            logbook.setState(State.Inactive);
            em.merge(logbook);
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
}
