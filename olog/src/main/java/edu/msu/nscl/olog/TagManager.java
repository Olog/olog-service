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
    private static EntityManager em = null;
    
    private TagManager() {
    }
    /**
     * Returns the list of tags in the database.
     *
     * @return Tags
     * @throws CFException wrapping an SQLException
     */
    public static Tags findAll() throws CFException {
        em = JPAUtil.getEntityManagerFactory().createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tag> cq = cb.createQuery(Tag.class);
        Root<Tag> from = cq.from(Tag.class);
        CriteriaQuery<Tag> select = cq.select(from);
        Predicate statusPredicate = cb.equal(from.get("state"), State.Active);
        select.where(statusPredicate);
        select.orderBy(cb.asc(from.get("name")));
        TypedQuery<Tag> typedQuery = em.createQuery(select);
        JPAUtil.startTransaction(em);
        try {
            Tags result = new Tags();
            List<Tag> rs = typedQuery.getResultList();
            if (rs != null) {
                Iterator<Tag> iterator = rs.iterator();
                while (iterator.hasNext()) {
                    result.addXmlTag(iterator.next());
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
     * Finds a tag in the database by name.
     *
     * @return Tag
     * @throws CFException wrapping an SQLException
     */
    public static Tag findTag(String name) throws CFException {
        em = JPAUtil.getEntityManagerFactory().createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tag> cq = cb.createQuery(Tag.class);
        Root<Tag> from = cq.from(Tag.class);
        CriteriaQuery<Tag> select = cq.select(from);
        Predicate namePredicate = cb.equal(from.get("name"), name);
        //Predicate statusPredicate = cb.equal(from.get("state"), State.Active);
        select.where(namePredicate);
        select.orderBy(cb.asc(from.get("name")));
        TypedQuery<Tag> typedQuery = em.createQuery(select);
        JPAUtil.startTransaction(em);
        try {
            Tag result = null;
            List<Tag> rs = typedQuery.getResultList();
            if (rs != null) {
                Iterator<Tag> iterator = rs.iterator();
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
     * Creates a tag in the database.
     *
     * @param name name of tag
     * @param owner owner of tag
     * @throws CFException wrapping an SQLException
     */
    public static Tag create(String name) throws CFException {

        try {
            Tag xmlTag = new Tag();
            Tag tag = findTag(name);
            if (tag != null) {
                tag.setState(State.Active);
                tag = (Tag)JPAUtil.update(tag);
                return tag;
            } else {
                xmlTag.setName(name);
                xmlTag.setState(State.Active);
                JPAUtil.save(xmlTag);
                return xmlTag;
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
     public static void remove(String name) throws CFException {
        
        try {
                Tag tag = findTag(name);
                tag.setState(State.Inactive);
                JPAUtil.update(tag);
        } catch (Exception e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);

        }
    }
}
