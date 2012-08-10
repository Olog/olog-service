/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.core.Response;

/**
 *
 * @author berryman
 */
public class LogbookManager {
    private static EntityManager em = null;
    
    private LogbookManager() {
    }

    /**
     * Returns the list of logbooks in the database.
     *
     * @return Logbooks
     * @throws CFException wrapping an SQLException
     */
    public static Logbooks findAll() throws CFException {
        em = JPAUtil.getEntityManagerFactory().createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Logbook> cq = cb.createQuery(Logbook.class);
        Root<Logbook> from = cq.from(Logbook.class);
        CriteriaQuery<Logbook> select = cq.select(from);
        Predicate statusPredicate = cb.equal(from.get("state"), State.Active);
        select.where(statusPredicate);
        select.orderBy(cb.asc(from.get("name")));
        TypedQuery<Logbook> typedQuery = em.createQuery(select);
        JPAUtil.startTransaction(em);
        try {
            Logbooks result = new Logbooks();
            List<Logbook> rs = typedQuery.getResultList();
            if (rs != null) {
                Iterator<Logbook> iterator = rs.iterator();
                while (iterator.hasNext()) {
                    result.addXmlLogbook(iterator.next());
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
     * Finds a logbook in the database by name.
     *
     * @return Logbook
     * @throws CFException wrapping an SQLException
     */
    public static Logbook findLogbook(String name) throws CFException {
        em = JPAUtil.getEntityManagerFactory().createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Logbook> cq = cb.createQuery(Logbook.class);
        Root<Logbook> from = cq.from(Logbook.class);
        CriteriaQuery<Logbook> select = cq.select(from);
        Predicate namePredicate = cb.equal(from.get("name"), name);
        Predicate statusPredicate = cb.equal(from.get("state"), State.Active);
        select.where(cb.and(namePredicate,statusPredicate));
        select.orderBy(cb.asc(from.get("name")));
        TypedQuery<Logbook> typedQuery = em.createQuery(select);
        JPAUtil.startTransaction(em);
        try {
            Logbook result = null;
            List<Logbook> rs = typedQuery.getResultList();
            if (rs != null) {
                Iterator<Logbook> iterator = rs.iterator();
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
     * Creates a logbook in the database.
     *
     * @param name name of logbook
     * @param owner owner of logbook
     * @throws CFException wrapping an SQLException
     */
    public static void create(String name, String owner) throws CFException {

        try {
            Logbook xmlLogbook = new Logbook();
            Logbook logbook = findLogbook(name);
            if (logbook != null) {
                logbook.setState(State.Active);
                logbook.setOwner(owner);
                JPAUtil.update(logbook);
            } else {
                xmlLogbook.setName(name);
                xmlLogbook.setOwner(owner);    
                JPAUtil.save(xmlLogbook);
            }    
        } catch (Exception e) {

            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);
        }
    }
    
    /**
     * Remove a logbook (mark as Inactive).
     *
     * @param name logbook name
     */
    public static void remove(String name) throws CFException {
        
        try {
                JPAUtil.remove(Logbook.class, findLogbook(name).getId());
        } catch (Exception e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);

        }
    }
}
