/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import edu.msu.nscl.olog.entity.State;
import edu.msu.nscl.olog.entity.Attribute;
import edu.msu.nscl.olog.boundry.AttributeManager;
import edu.msu.nscl.olog.entity.Property;
import edu.msu.nscl.olog.entity.Property_;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author berryman
 */
public class PropertyManagerTest {

    private static EntityManager em = null;

    private PropertyManagerTest() {
    }

    /**
     * Returns the list of tags in the database.
     *
     * @return Tags
     * @throws edu.msu.nscl.olog.OlogException wrapping an SQLException
     */
    public static Set<Property> findAll() throws OlogException {
        em = JPAUtilTest.getEntityManagerFactory().createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Property> cq = cb.createQuery(Property.class);
        Root<Property> from = cq.from(Property.class);
        CriteriaQuery<Property> select = cq.select(from);
        Predicate statusPredicate = cb.equal(from.get(Property_.state), State.Active);
        select.where(statusPredicate);
        select.orderBy(cb.asc(from.get(Property_.name)));
        TypedQuery<Property> typedQuery = em.createQuery(select);
        JPAUtilTest.startTransaction(em);
        try {
            Set<Property> result = new HashSet<Property>();
            List<Property> rs = typedQuery.getResultList();
            if (rs != null) {
                Iterator<Property> iterator = rs.iterator();
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
    public static Property findProperty(String propertyName) throws OlogException {
        em = JPAUtilTest.getEntityManagerFactory().createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Property> cq = cb.createQuery(Property.class);
        Root<Property> from = cq.from(Property.class);
        CriteriaQuery<Property> select = cq.select(from);
        Predicate namePredicate = cb.equal(from.get(Property_.name), propertyName);
        //Predicate statusPredicate = cb.equal(from.get("state"), State.Active);
        select.where(namePredicate);
        select.orderBy(cb.asc(from.get(Property_.name)));
        TypedQuery<Property> typedQuery = em.createQuery(select);
        JPAUtilTest.startTransaction(em);
        try {
            Property result = null;
            List<Property> rs = typedQuery.getResultList();
            if (rs != null) {
                Iterator<Property> iterator = rs.iterator();
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
     * @param name name of property
     * @param owner owner of property
     * @throws edu.msu.nscl.olog.OlogException wrapping an SQLException
     */
    public static Property create(String propertyName) throws OlogException {

        try {
            Property newProperty = new Property();
            Property property = findProperty(propertyName);
            if (property != null) {
                property.setState(State.Active);
                property = (Property) JPAUtilTest.update(property);
                return property;
            } else {
                newProperty.setName(propertyName);
                newProperty.setState(State.Active);
                JPAUtilTest.save(newProperty);
                return newProperty;
            }
        } catch (Exception e) {

            throw new OlogException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);
        }
    }

    /**
     * Creates a property in the database.
     *
     * @param name name of property
     * @param attributes attributes of property
     * @throws edu.msu.nscl.olog.OlogException wrapping an SQLException
     */
    public static Property create(Property property) throws OlogException {
        if (property.getAttributes() != null) {
            Iterator<Attribute> iterator = property.getAttributes().iterator();
            while (iterator.hasNext()) {
                Attribute att = AttributeManager.findAttribute(property, iterator.next().getName());
                if (att.getId() != null) {
                    property.addAttribute(att);
                }
            }

        }
        try {
            Property Inactiveproperty = findProperty(property.getName());
            if (Inactiveproperty != null) {
                Inactiveproperty.setState(State.Active);
                Inactiveproperty = (Property) JPAUtilTest.update(Inactiveproperty);
                return Inactiveproperty;
            } else {
                Property newProperty = new Property();
                newProperty.setName(property.getName());
                newProperty.setState(State.Active);
                JPAUtilTest.save(newProperty);
                return newProperty;
            }
        } catch (Exception e) {

            throw new OlogException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);
        }
    }

    /**
     * Remove a property (mark as Inactive).
     *
     * @param name property name
     */
    public static void remove(String propertyName) throws OlogException {

        try {
            Property property = findProperty(propertyName);
            property.setState(State.Inactive);
            if (property.getAttributes() != null) {
                Set<Attribute> attributes = property.getAttributes();
                Iterator<Attribute> iterator = attributes.iterator();
                while (iterator.hasNext()) {
                    Attribute attribute = iterator.next();
                    attribute.setState(State.Inactive);
                    JPAUtilTest.update(attribute);
                }
            }
            JPAUtilTest.update(property);
        } catch (Exception e) {
            throw new OlogException(Response.Status.INTERNAL_SERVER_ERROR,
                    "JPA exception: " + e);

        }
    }
}
