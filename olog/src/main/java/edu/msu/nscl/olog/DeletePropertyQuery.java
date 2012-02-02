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
class DeletePropertyQuery {

    private static SqlSessionFactory ssf = MyBatisSession.getSessionFactory();

    public DeletePropertyQuery() {
    }

    static void removeProperty(String property, XmlProperty data) throws CFException {
        SqlSession ss = ssf.openSession();

        try {
            // Remove property if no attributes are in the payload
            if (data.getAttributes() == null || data.getAttributes().keySet().isEmpty()) {
                ss.update("mappings.PropertyMapping.setAsInactive", property);
            } else { // Remove attributes in payload
                List<String> attributes = new ArrayList(data.getAttributes().keySet());
                XmlProperty prop = (XmlProperty) ss.selectOne("mappings.PropertyMapping.getProperty", property);

                HashMap<String, Object> hm = new HashMap<String, Object>();

                hm.put("pid", prop.getId());
                hm.put("attributes", attributes);

                ss.update("mappings.PropertyMapping.removeAttributes", hm);
            }

            ss.commit();
        } catch (PersistenceException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "MyBatis exception: " + e);
        } finally {
            ss.close();
        }
    }
}
