/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.ws.rs.core.Response;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author berryman
 */
public class LogbookManagerTest {
    private static EntityManager em = null;

    private LogbookManagerTest() {
    }

    /**
     * Returns the list of logbooks in the database.
     *
     * @return Logbooks
     * @throws edu.msu.nscl.olog.OlogException wrapping an SQLException
     */
    public static Logbooks findAll() throws OlogException {
        em = JPAUtilTest.getEntityManagerFactory().createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Logbook> cq = cb.createQuery(Logbook.class);
        Root<Logbook> from = cq.from(Logbook.class);
        CriteriaQuery<Logbook> select = cq.select(from);
        Predicate statusPredicate = cb.equal(from.get("state"), State.Active);
        select.where(statusPredicate);
        select.orderBy(cb.asc(from.get("name")));
        TypedQuery<Logbook> typedQuery = em.createQuery(select);
        JPAUtilTest.startTransaction(em);
        try {
            Logbooks result = new Logbooks();
            List<Logbook> rs = typedQuery.getResultList();
            if (rs != null) {
                Iterator<Logbook> iterator = rs.iterator();
                while (iterator.hasNext()) {
                    result.addLogbook(iterator.next());
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

    /**
     * Finds a logbook in the database by name.
     *
     * @return Logbook
     * @throws edu.msu.nscl.olog.OlogException wrapping an SQLException
     */
    public static Logbook findLogbook(String name) throws OlogException {
        em = JPAUtilTest.getEntityManagerFactory().createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Logbook> cq = cb.createQuery(Logbook.class);
        Root<Logbook> from = cq.from(Logbook.class);
        CriteriaQuery<Logbook> select = cq.select(from);
        Predicate namePredicate = cb.equal(from.get("name"), name);
        //Predicate statusPredicate = cb.equal(from.get("state"), State.Active);
        select.where(namePredicate);
        select.orderBy(cb.asc(from.get("name")));
        TypedQuery<Logbook> typedQuery = em.createQuery(select);
        JPAUtilTest.startTransaction(em);
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
            throw new OlogException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);
        } finally {
            JPAUtilTest.finishTransacton(em);
        }
    }
    /**
     * Creates a logbook in the database.
     *
     * @param name name of logbook
     * @param owner owner of logbook
     * @throws edu.msu.nscl.olog.OlogException wrapping an SQLException
     */
    public static Logbook create(String name, String owner) throws OlogException {

        try {
            Logbook xmlLogbook = new Logbook();
            Logbook logbook = findLogbook(name);
            if (logbook != null) {
                logbook.setState(State.Active);
                logbook.setOwner(owner);
                logbook = (Logbook)JPAUtilTest.update(logbook);
                return logbook;
            } else {
                xmlLogbook.setName(name);
                xmlLogbook.setOwner(owner);
                xmlLogbook.setState(State.Active);
                JPAUtilTest.save(xmlLogbook);
                return xmlLogbook;
            }
             
        } catch (Exception e) {

            throw new OlogException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);
        }
    }
    
    /**
     * Remove a logbook (mark as Inactive).
     *
     * @param name logbook name
     */
    public static void remove(String name) throws OlogException {
        
        try {
                Logbook logbook = findLogbook(name);
                logbook.setState(State.Inactive);
                JPAUtilTest.update(logbook);
        } catch (Exception e) {
            throw new OlogException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);

        }
    }
}
