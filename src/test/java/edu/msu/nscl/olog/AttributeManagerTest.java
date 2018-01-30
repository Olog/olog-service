/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import edu.msu.nscl.olog.entity.State;
import edu.msu.nscl.olog.boundry.PropertyManager;
import edu.msu.nscl.olog.entity.Attribute;
import edu.msu.nscl.olog.entity.Property;
import edu.msu.nscl.olog.entity.Property_;
import edu.msu.nscl.olog.entity.Attribute_;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author berryman
 */
public class AttributeManagerTest {

    private static EntityManager em = null;

    private AttributeManagerTest() {
    }

    /**
     * Returns the list of set attribute in the database.
     *
     * @return Set<Attribute>
     * @throws edu.msu.nscl.olog.OlogException wrapping an SQLException
     */
    public static Set<Attribute> findAll(Property property) throws OlogException {
        em = JPAUtilTest.getEntityManagerFactory().createEntityManager();
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
        JPAUtilTest.startTransaction(em);
        try {
            Set<Attribute> result = new HashSet<Attribute>();
            List<Attribute> rs = typedQuery.getResultList();
            if (rs != null) {
                Iterator<Attribute> iterator = rs.iterator();
                while (iterator.hasNext()) {
                    result.add(iterator.next());
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
    public static Attribute findAttribute(Property property, String attributeName) throws OlogException {
        em = JPAUtilTest.getEntityManagerFactory().createEntityManager();
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
        JPAUtilTest.startTransaction(em);
        try {
            Attribute result = new Attribute();
            List<Attribute> rs = typedQuery.getResultList();
            if (rs != null) {
                Iterator<Attribute> iterator = rs.iterator();
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
     * Creates a property in the database.
     *
     * @param property property
     * @throws edu.msu.nscl.olog.OlogException wrapping an SQLException
     */
    public static Property create(Property property, String attributeName) throws OlogException {

        try {
            property = PropertyManager.findProperty(property.getName());
            Attribute attribute = findAttribute(property, attributeName);
            if (attribute.getId() != null) {
                attribute.setState(State.Active);
                property.addAttribute(attribute);
                property = (Property) JPAUtilTest.update(property);
                return property;
            } else {
                Attribute newAttribute = new Attribute();
                newAttribute.setName(attributeName);
                newAttribute.setState(State.Active);
                newAttribute.setProperty(property);
                JPAUtilTest.save(newAttribute);
                newAttribute = findAttribute(property, newAttribute.getName());
                property.addAttribute(newAttribute);
                property = (Property) JPAUtilTest.update(property);
                return property;
            }
        } catch (Exception e) {

            throw new OlogException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);
        }
    }

    /**
     * Remove a attribute (mark as Inactive).
     *
     */
    public static void remove(Property property, String attributeName) throws OlogException {

        try {
            Attribute attribute = findAttribute(property, attributeName);
            attribute.setState(State.Inactive);
            JPAUtilTest.update(attribute);
        } catch (Exception e) {
            throw new OlogException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);

        }
    }
}
