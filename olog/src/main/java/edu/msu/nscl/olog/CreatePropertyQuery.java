/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.persistence.PersistenceException;
import javax.ws.rs.core.Response;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 *
 * @author gaul
 */
class CreatePropertyQuery {

    private static SqlSessionFactory ssf = MyBatisSession.getSessionFactory();

    private CreatePropertyQuery() {
    }

    public static XmlProperty addProperty(String newProperty, XmlProperty data, boolean destructive) throws CFException {
        SqlSession ss = ssf.openSession();

        try {

            XmlProperty property = (XmlProperty) ss.selectOne("mappings.PropertyMapping.getProperty", newProperty);
            int pid;

            // Add new property or return it to active
            if (property != null) {
                ss.update("mappings.PropertyMapping.returnToActive", newProperty);
                pid = property.getId();
            } else {
                ss.insert("mappings.PropertyMapping.addProperty", newProperty);
                pid = (Integer) ss.selectOne("mappings.PropertyMapping.lastId");
            }

            // Are we going to remove all of this properties attributes?
            if (destructive) {
                ss.update("mappings.PropertyMapping.removeAllAttributes", pid);
            }

            HashMap<String, Object> hm = new HashMap<String, Object>();

            // Add all the new attributes from the payload
            if (data.getAttributes() != null) {
                List<String> attributes = new ArrayList(data.getAttributes().keySet());

                // Get all current attributes and remove them from the list coming in -  only if not destructive
                ArrayList<String> currentAttributes = (ArrayList<String>) ss.selectList("mappings.PropertyMapping.attributesForProperty", pid);

                // If any of the "new" attributes coming in are attributes that have been added before just return them to active
                currentAttributes.retainAll(attributes);
                if (currentAttributes.size() > 0) {
                    hm.put("pid", pid);
                    hm.put("attributes", currentAttributes);
                    ss.update("mappings.PropertyMapping.returnAttributeToActive", hm);
                }

                // Do not duplicate attributes that already exist
                attributes.removeAll(currentAttributes);

                // If there are any attributes left over then they are new ones to be added
                if (attributes.size() > 0) {
                    hm.clear();
                    hm.put("pid", pid);
                    hm.put("attributes", attributes);

                    ss.insert("mappings.PropertyMapping.addAttributes", hm);
                }
            }

            ss.commit();

            return (property != null) ? property : new XmlProperty(newProperty);
        } catch (PersistenceException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "MyBatis exception: " + e);
        } finally {
            ss.close();
        }
    }
}
