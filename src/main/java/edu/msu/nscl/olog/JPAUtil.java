/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import org.apache.log4j.Logger;

/**
 *
 * @author berryman
 */
public class JPAUtil {

    private static final EntityManagerFactory factory;
    private static volatile long aliasCount = 0;
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

    public static Object update(Object o) {
        EntityManager em = null;

        try {
            em = JPAUtil.getEntityManagerFactory().createEntityManager();
            JPAUtil.startTransaction(em);
            o = em.merge(o);
            JPAUtil.finishTransacton(em);
            return o;

        } catch (PersistenceException e) {
            JPAUtil.transactionFailed(em);
            throw e;
        }
    }

    public static void remove(Class type, Long id) {
        EntityManager em = null;

        try {
            em = JPAUtil.getEntityManagerFactory().createEntityManager();
            JPAUtil.startTransaction(em);

            Query query = em.createQuery("UPDATE " + type.getName() + " c  SET c.state= edu.msu.nscl.olog.State.Inactive  WHERE c.id = " + id.toString());
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

    /**
     * Result count from a CriteriaQuery
     *
     * @param em Entity Manager
     * @param criteria Criteria Query to count results
     * @return row count
     */
    public static <T> Long count(EntityManager em, CriteriaQuery<T> criteria) {
            return em.createQuery(countCriteria(em, criteria)).getSingleResult();
    }

    /**
     * Create a row count CriteriaQuery from a CriteriaQuery
     *
     * @param em entity manager
     * @param criteria source criteria
     * @return row count CriteriaQuery
     */
    public static <T> CriteriaQuery<Long> countCriteria(EntityManager em, CriteriaQuery<T> criteria) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
        copyCriteriaNoSelection(criteria, countCriteria);
        countCriteria.select(builder.count(findRoot(countCriteria,
                criteria.getResultType())));

        return countCriteria;
    }

    /**
     * Copy Criteria without Selection
     *
     * @param from source Criteria
     * @param to destination Criteria
     */
    public static void copyCriteriaNoSelection(CriteriaQuery<?> from, CriteriaQuery<?> to) {

        //for (Root<?> root : from.getRoots()) {
        //    Root<?> dest = to.from(root.getJavaType());
        //    dest.alias(getOrCreateAlias(root));
        //    copyJoins(root, dest);
        //}

        to.groupBy(from.getGroupList());
        to.distinct(from.isDistinct());
        //to.having(from.getGroupRestriction());
        to.where(from.getRestriction());
        to.orderBy(from.getOrderList());
    }

    /**
     * Gets The result alias, if none set a default one and return it
     *
     * @param selection
     * @return root alias or generated one
     */
    public static synchronized <T> String getOrCreateAlias(Selection<T> selection) {
        // reset alias count
        if (aliasCount > 1000) {
            aliasCount = 0;
        }

        String alias = selection.getAlias();
        if (alias == null) {
            alias = "generatedAlias" + aliasCount++;
            selection.alias(alias);
        }
        return alias;

    }

    /**
     * Find Root of result type
     *
     * @param query criteria query
     * @return the root of result type or null if none
     */
    public static <T> Root<T> findRoot(CriteriaQuery<T> query) {
        return findRoot(query, query.getResultType());
    }

    /**
     * Find the Root with type class on CriteriaQuery Root Set
     *
     * @param <T> root type
     * @param query criteria query
     * @param clazz root type
     * @return Root<T> of null if none
     */
    public static <T> Root<T> findRoot(CriteriaQuery<?> query, Class<T> clazz) {

        for (Root<?> r : query.getRoots()) {
            if (clazz.equals(r.getJavaType())) {
                return (Root<T>) r.as(clazz);
            }
        }
        return (Root<T>) query.getRoots().iterator().next();
    }

    /**
     * Copy Joins
     *
     * @param from source Join
     * @param to destination Join
     */
    public static void copyJoins(From<?, ?> from, From<?, ?> to) {
        for (Join<?, ?> j : from.getJoins()) {
            Join<?, ?> toJoin = to.join(j.getAttribute().getName(), j.getJoinType());
            toJoin.alias(getOrCreateAlias(j));

            copyJoins(j, toJoin);
        }

        for (Fetch<?, ?> f : from.getFetches()) {
            Fetch<?, ?> toFetch = to.fetch(f.getAttribute().getName());
            copyFetches(f, toFetch);

        }
    }

    /**
     * Copy Fetches
     *
     * @param from source Fetch
     * @param to dest Fetch
     */
    public static void copyFetches(Fetch<?, ?> from, Fetch<?, ?> to) {
        for (Fetch<?, ?> f : from.getFetches()) {
            Fetch<?, ?> toFetch = to.fetch(f.getAttribute().getName());
            // recursively copy fetches
            copyFetches(f, toFetch);
        }
    }
}
