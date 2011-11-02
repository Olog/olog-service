/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */
package edu.msu.nscl.olog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * JDBC query to create a logbook/tag.
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
public class CreateLogbookQuery {

    private static SqlSessionFactory ssf = MyBatisSession.getSessionFactory();

    private CreateLogbookQuery() {
    }

    /**
     * Creates a logbook in the database.
     *
     * @param name name of logbook
     * @param owner owner of logbook
     * @throws CFException wrapping an SQLException
     */
    public static void createLogbook(String name, String owner) throws CFException {
        SqlSession ss = ssf.openSession();

        try {
            HashMap<String, Object> hm = new HashMap<String, Object>();
            hm.put("name", name);
            hm.put("owner", owner);

            XmlLogbook log = (XmlLogbook) ss.selectOne("mappings.LogbookMapping.logbookId", name);
            if (log != null) {
                ss.update("mappings.LogbookMapping.returnToActive", hm);
            } else {
                ss.insert("mappings.LogbookMapping.createLogbook", hm);
            }

            ss.commit();
        } catch (PersistenceException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "MyBatis exception: " + e);
        } finally {
            ss.close();
        }
    }

    /**
     * Creates a tag in the database.
     *
     * @param name name of tag
     * @throws CFException wrapping an SQLException
     */
    public static void createTag(String name) throws CFException {
        SqlSession ss = ssf.openSession();

        try {
            XmlTag tag = (XmlTag) ss.selectOne("mappings.TagMapping.tagByNameAnyStatus", name);
            if (tag != null) {
                ss.update("mappings.TagMapping.returnToActive", name);
            } else {
                ss.insert("mappings.TagMapping.createTag", name);
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
