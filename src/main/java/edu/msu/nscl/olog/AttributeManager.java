/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.ws.rs.core.Response;

/**
 *
 * @author berryman
 */
public class AttributeManager {

    private AttributeManager() {
    }

    /**
     * Returns the list of set attribute in the database.
     *
     * @return Set<Attribute>
     * @throws OlogException wrapping an SQLException
     */
    public static Set<Attribute> findAll(Property property) throws OlogException {
    
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {        
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Attribute> cq = cb.createQuery(Attribute.class);
            Root<Property> from = cq.from(Property.class);
            SetJoin<Property, Attribute> attributes = from.join(Property_.attributes, JoinType.LEFT);
            CriteriaQuery<Attribute> select = cq.select(attributes);
            Predicate namePredicate = cb.equal(from.get(Property_.name), property.getName());
            Predicate pstatusPredicate = cb.equal(from.get(Property_.state), State.Active);
            Predicate astatusPredicate = cb.equal(attributes.get(Attribute_.state), State.Active);
            Predicate andPredicate = cb.and(namePredicate, pstatusPredicate, astatusPredicate);
            select.where(andPredicate);
            select.orderBy(cb.asc(attributes.get(Attribute_.name)));
            TypedQuery<Attribute> typedQuery = em.createQuery(select);
            em.getTransaction().begin();
            Set<Attribute> result = new HashSet<Attribute>();
            List<Attribute> rs = typedQuery.getResultList();
            if (rs != null) {
                Iterator<Attribute> iterator = rs.iterator();
                while (iterator.hasNext()) {
                    result.add(iterator.next());
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
    public static Attribute findAttribute(Property property, String attributeName) throws OlogException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Attribute> cq = cb.createQuery(Attribute.class);
            Root<Property> from = cq.from(Property.class);
            SetJoin<Property, Attribute> attributes = from.join(Property_.attributes, JoinType.LEFT);
            CriteriaQuery<Attribute> select = cq.select(attributes);
            Predicate pnamePredicate = cb.equal(from.get(Property_.name), property.getName());
            //Predicate pstatusPredicate = cb.equal(from.get(Property_.state), State.Active);
            //Predicate astatusPredicate = cb.equal(attributes.get(Attribute_.state), State.Active);
            Predicate anamePredicate = cb.equal(attributes.get(Attribute_.name), attributeName);
            Predicate andPredicate = cb.and(pnamePredicate, anamePredicate);
            select.where(andPredicate);
            select.orderBy(cb.asc(attributes.get(Attribute_.name)));
            TypedQuery<Attribute> typedQuery = em.createQuery(select);
            em.getTransaction().begin();
            Attribute result = new Attribute();
            List<Attribute> rs = typedQuery.getResultList();
            if (rs != null) {
                Iterator<Attribute> iterator = rs.iterator();
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
     * Creates a property in the database.
     *
     * @param property property
     * @param name attribute name
     * @throws OlogException wrapping an SQLException
     */
    public static Property create(Property property, String attributeName) throws OlogException {

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();            
            property = PropertyManager.findProperty(property.getName());
            Attribute attribute = findAttribute(property, attributeName);
            if (attribute.getId() != null) {
                attribute.setState(State.Active);
                property.addAttribute(attribute);
                property = em.merge(property);
                em.getTransaction().commit();
                return property;
            } else {
                Attribute newAttribute = new Attribute();
                newAttribute.setName(attributeName);
                newAttribute.setState(State.Active);
                newAttribute.setProperty(property);
                em.persist(newAttribute);
                property.addAttribute(newAttribute);
                property = em.merge(property);
                em.getTransaction().commit();
                return property;
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
     * Remove a attribute (mark as Inactive).
     *
     * @param name attribute name
     */
    public static void remove(Property property, String attributeName) throws OlogException {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();            
            Attribute attribute = findAttribute(property, attributeName);
            attribute.setState(State.Inactive);
            em.merge(attribute);
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
