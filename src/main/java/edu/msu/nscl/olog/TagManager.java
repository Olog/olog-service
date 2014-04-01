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
public class TagManager {

    private TagManager() {
    }
    /**
     * Returns the list of tags in the database.
     *
     * @return Tags
     * @throws OlogException wrapping an SQLException
     */
    public static Tags findAll() throws OlogException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Tag> cq = cb.createQuery(Tag.class);
            Root<Tag> from = cq.from(Tag.class);
            CriteriaQuery<Tag> select = cq.select(from);
            Predicate statusPredicate = cb.equal(from.get("state"), State.Active);
            select.where(statusPredicate);
            select.orderBy(cb.asc(from.get("name")));
            TypedQuery<Tag> typedQuery = em.createQuery(select);
            Tags result = new Tags();
            List<Tag> rs = typedQuery.getResultList();
            if (rs != null) {
                Iterator<Tag> iterator = rs.iterator();
                while (iterator.hasNext()) {
                    result.addTag(iterator.next());
                }
            }
            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
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
     * Finds a tag in the database by name.
     *
     * @return Tag
     * @throws OlogException wrapping an SQLException
     */
    public static Tag findTag(String name) throws OlogException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Tag> cq = cb.createQuery(Tag.class);
            Root<Tag> from = cq.from(Tag.class);
            CriteriaQuery<Tag> select = cq.select(from);
            Predicate namePredicate = cb.equal(from.get("name"), name);
            //Predicate statusPredicate = cb.equal(from.get("state"), State.Active);
            select.where(namePredicate);
            select.orderBy(cb.asc(from.get("name")));
            TypedQuery<Tag> typedQuery = em.createQuery(select);
            Tag result = null;
            List<Tag> rs = typedQuery.getResultList();
            if (rs != null) {
                Iterator<Tag> iterator = rs.iterator();
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
                if (em.getTransaction() != null && !em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            } catch (Exception e) {
            }
            em.close();
        }
    }
    /**
     * Creates a tag in the database.
     *
     * @param name name of tag
     * @param owner owner of tag
     * @throws OlogException wrapping an SQLException
     */
    public static Tag create(String name) throws OlogException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();
            Tag xmlTag = new Tag();
            Tag tag = findTag(name);
            if (tag != null) {
                tag.setState(State.Active);
                tag = em.merge(tag);
                em.getTransaction().commit();
                return tag;
            } else {
                xmlTag.setName(name);
                xmlTag.setState(State.Active);
                em.persist(xmlTag);
                em.getTransaction().commit();
                return xmlTag;
            }
        } catch (Exception e) {

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
     * @param name tag name
     */
    public static void remove(String name) throws OlogException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();
            Tag tag = findTag(name);
            tag.setState(State.Inactive);
            em.merge(tag);
            em.getTransaction().commit();
        } catch (Exception e) {
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
}
