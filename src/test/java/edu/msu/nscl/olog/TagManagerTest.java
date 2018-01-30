/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import edu.msu.nscl.olog.entity.State;
import edu.msu.nscl.olog.entity.Tag;
import edu.msu.nscl.olog.entity.Tags;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.core.Response;
import java.util.Iterator;
import java.util.List;


/**
 *
 * @author berryman
 */
public class TagManagerTest {
    private static EntityManager em = null;
    
    private TagManagerTest() {
    }
    /**
     * Returns the list of tags in the database.
     *
     * @return Tags
     * @throws edu.msu.nscl.olog.OlogException wrapping an SQLException
     */
    public static Tags findAll() throws OlogException {
        em = JPAUtilTest.getEntityManagerFactory().createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tag> cq = cb.createQuery(Tag.class);
        Root<Tag> from = cq.from(Tag.class);
        CriteriaQuery<Tag> select = cq.select(from);
        Predicate statusPredicate = cb.equal(from.get("state"), State.Active);
        select.where(statusPredicate);
        select.orderBy(cb.asc(from.get("name")));
        TypedQuery<Tag> typedQuery = em.createQuery(select);
        JPAUtilTest.startTransaction(em);
        try {
            Tags result = new Tags();
            List<Tag> rs = typedQuery.getResultList();
            if (rs != null) {
                Iterator<Tag> iterator = rs.iterator();
                while (iterator.hasNext()) {
                    result.addTag(iterator.next());
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
     * Finds a tag in the database by name.
     *
     * @return Tag
     * @throws edu.msu.nscl.olog.OlogException wrapping an SQLException
     */
    public static Tag findTag(String name) throws OlogException {
        em = JPAUtilTest.getEntityManagerFactory().createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tag> cq = cb.createQuery(Tag.class);
        Root<Tag> from = cq.from(Tag.class);
        CriteriaQuery<Tag> select = cq.select(from);
        Predicate namePredicate = cb.equal(from.get("name"), name);
        //Predicate statusPredicate = cb.equal(from.get("state"), State.Active);
        select.where(namePredicate);
        select.orderBy(cb.asc(from.get("name")));
        TypedQuery<Tag> typedQuery = em.createQuery(select);
        JPAUtilTest.startTransaction(em);
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
            throw new OlogException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);
        } finally {
          JPAUtilTest.finishTransacton(em);
        }
    }
            /**
     * Creates a tag in the database.
     *
     * @param name name of tag
     * @param owner owner of tag
     * @throws edu.msu.nscl.olog.OlogException wrapping an SQLException
     */
    public static Tag create(String name) throws OlogException {

        try {
            Tag xmlTag = new Tag();
            Tag tag = findTag(name);
            if (tag != null) {
                tag.setState(State.Active);
                tag = (Tag)JPAUtilTest.update(tag);
                return tag;
            } else {
                xmlTag.setName(name);
                xmlTag.setState(State.Active);
                JPAUtilTest.save(xmlTag);
                return xmlTag;
            }    
        } catch (Exception e) {

            throw new OlogException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);
        }
    }
    
    /**
     * Remove a tag (mark as Inactive).
     *
     * @param name tag name
     */
     public static void remove(String name) throws OlogException {
        
        try {
                Tag tag = findTag(name);
                tag.setState(State.Inactive);
                JPAUtilTest.update(tag);
        } catch (Exception e) {
            throw new OlogException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);

        }
    }
}
