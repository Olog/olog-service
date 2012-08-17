/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import javax.persistence.*;
import org.apache.log4j.Logger;

/**
 *
 * @author berryman
 */
public class JPAUtil {

    private static final EntityManagerFactory factory;
    private static final Logger logger = Logger.getLogger(edu.msu.nscl.olog.JPAUtil.class);

    static {
        try {
            factory = Persistence.createEntityManagerFactory("olog_prod");

        } catch (Throwable ex) {
            logger.error("Initial SessionFactory creation failed", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return factory;
    }

    public static void startTransaction(EntityManager em) {
        em.getTransaction().begin();
    }

    public static void finishTransacton(EntityManager em) {
        if (em.isOpen()) {
            EntityTransaction tx = em.getTransaction();
            if (tx.isActive()) {
                em.getTransaction().commit();
            }
            em.close();
        }
    }

    public static void transactionFailed(EntityManager em) {
        if (em.isOpen()) {
            EntityTransaction tx = em.getTransaction();

            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
        }
    }

    public static void save(Object o) {
        EntityManager em = null;

        try {
            em = JPAUtil.getEntityManagerFactory().createEntityManager();
            JPAUtil.startTransaction(em);
            em.persist(o);
            JPAUtil.finishTransacton(em);

        } catch (PersistenceException e) {
            JPAUtil.transactionFailed(em);
            throw e;
        }
    }

    public static void update(Object o) {
        EntityManager em = null;

        try {
            em = JPAUtil.getEntityManagerFactory().createEntityManager();
            JPAUtil.startTransaction(em);
            em.merge(o);
            JPAUtil.finishTransacton(em);

        } catch (PersistenceException e) {
            JPAUtil.transactionFailed(em);
            throw e;
        }
    }

    public static void refresh(Object o) {
        EntityManager em = null;

        try {
            em = JPAUtil.getEntityManagerFactory().createEntityManager();
            JPAUtil.startTransaction(em);
            em.refresh(o);
            JPAUtil.finishTransacton(em);

        } catch (PersistenceException e) {
            JPAUtil.transactionFailed(em);
            throw e;
        }
    }

    public static void flush() {
        EntityManager em = null;

        try {
            em = JPAUtil.getEntityManagerFactory().createEntityManager();
            JPAUtil.startTransaction(em);
            em.flush();
            JPAUtil.finishTransacton(em);

        } catch (PersistenceException e) {
            JPAUtil.transactionFailed(em);
            throw e;
        }
    }

    public static void remove(Class type, long id) {
        EntityManager em = null;

        try {
            em = JPAUtil.getEntityManagerFactory().createEntityManager();
            JPAUtil.startTransaction(em);

            Query query = em.createQuery("UPDATE " + type.getName() + " c  SET c.status='" + State.Inactive + "' WHERE c.id = ?");
            query.setParameter(1, id);
            query.executeUpdate();

            JPAUtil.finishTransacton(em);

        } catch (PersistenceException e) {
            JPAUtil.transactionFailed(em);
            throw e;
        }
    }

    public static Object findByID(Class type, long id) {
        EntityManager em = null;

        try {
            em = JPAUtil.getEntityManagerFactory().createEntityManager();
            JPAUtil.startTransaction(em);
            Object o = em.find(type, id);
            JPAUtil.finishTransacton(em);

            return o;

        } catch (PersistenceException e) {
            JPAUtil.transactionFailed(em);
            throw e;
        }
    }
}
