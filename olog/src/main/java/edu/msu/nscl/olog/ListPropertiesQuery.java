/*
 * Copyright (c) 2010 Michigan State University - Facility for Rare Isotope Beams
 */
package edu.msu.nscl.olog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * JDBC query to find properties.
 * 
 * @author Robert Gaul III
 */
public class ListPropertiesQuery {

    private static SqlSessionFactory ssf = MyBatisSession.getSessionFactory();

    private ListPropertiesQuery() {
    }

    /**
     * Returns the list of properties in the database.
     *
     * @return XmlLogbooks
     * @throws CFException wrapping an SQLException
     */
    public static XmlProperties getProperties() throws CFException {
        SqlSession ss = ssf.openSession();

        try {
            XmlProperties result = new XmlProperties();
            ArrayList<XmlProperty> rs = (ArrayList<XmlProperty>) ss.selectList("mappings.PropertyMapping.allProperties");
            if (rs != null) {
                Iterator<XmlProperty> iterator = rs.iterator();
                while (iterator.hasNext()) {
                    XmlProperty prop = iterator.next();
                    result.addXmlProperty(prop);
                }
            }

            return result;
        } catch (PersistenceException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "MyBatis exception: " + e);
        } finally {
            ss.close();
        }
    }

    /**
     * Returns the list of attributes for a given property
     *
     * @return XmlLogbooks
     * @throws CFException wrapping an SQLException
     */
    static XmlProperty getAttributes(String property) throws CFException {
        SqlSession ss = ssf.openSession();

        try {
            XmlProperty result = null;
            result = (XmlProperty) ss.selectOne("mappings.PropertyMapping.getProperty", property);
            
            Map<String, String> attributes = new HashMap<String, String>();
            ArrayList<String> currentAttributes = (ArrayList<String>) ss.selectList("mappings.PropertyMapping.attributesForProperty", result.getId());
            for (String attribute : currentAttributes) {
                attributes.put(attribute, "");
            }
            
            result.setAttributes(attributes);

            return result;
        } catch (PersistenceException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "MyBatis exception: " + e);
        } finally {
            ss.close();
        }
    }
}
