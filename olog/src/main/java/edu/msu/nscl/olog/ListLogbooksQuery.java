/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */
package edu.msu.nscl.olog;

import java.util.ArrayList;
import java.util.Iterator;
import javax.ws.rs.core.Response;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * JDBC query to find logbooks/tags.
 * 
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
public class ListLogbooksQuery {

    private static SqlSessionFactory ssf = MyBatisSession.getSessionFactory();

    private ListLogbooksQuery() {
    }

    /**
     * Returns the list of logbooks in the database.
     *
     * @return XmlLogbooks
     * @throws CFException wrapping an SQLException
     */
    public static XmlLogbooks getLogbooks() throws CFException {
        SqlSession ss = ssf.openSession();

        try {
            XmlLogbooks result = new XmlLogbooks();
            ArrayList<XmlLogbook> rs = (ArrayList<XmlLogbook>) ss.selectList("mappings.LogbookMapping.allLogbooks");
            if (rs != null) {
                Iterator<XmlLogbook> iterator = rs.iterator();
                while (iterator.hasNext()) {
                    result.addXmlLogbook(iterator.next());
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
     * Finds a logbook in the database by name.
     *
     * @return XmlLogbook
     * @throws CFException wrapping an SQLException
     */
    public static XmlLogbook findLogbook(String name) throws CFException {
        SqlSession ss = ssf.openSession();

        try {
            XmlLogbook result = null;
            ArrayList<XmlLogbook> rs = (ArrayList<XmlLogbook>) ss.selectList("mappings.LogbookMapping.logbookByName", name);
            if (rs != null) {
                Iterator<XmlLogbook> iterator = rs.iterator();
                while (iterator.hasNext()) {
                    result = iterator.next();
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
     * Returns the list of tags in the database.
     *
     * @return XmlTags
     * @throws CFException wrapping an SQLException
     */
    public static XmlTags getTags() throws CFException {
        SqlSession ss = ssf.openSession();

        try {
            XmlTags result = new XmlTags();
            ArrayList<XmlTag> rs = (ArrayList<XmlTag>) ss.selectList("mappings.TagMapping.allTags");
            if (rs != null) {
                Iterator<XmlTag> iterator = rs.iterator();
                while (iterator.hasNext()) {
                    result.addXmlTag(iterator.next());
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
     * Finds a tag in the database by name.
     *
     * @return XmlTag
     * @throws CFException wrapping an SQLException
     */
    public static XmlTag findTag(String name) throws CFException {
        SqlSession ss = ssf.openSession();

        try {
            XmlTag result = null;
            ArrayList<XmlTag> rs = (ArrayList<XmlTag>) ss.selectList("mappings.TagMapping.tagByName", name);
            if (rs != null) {
                Iterator<XmlTag> iterator = rs.iterator();
                while (iterator.hasNext()) {
                    result = iterator.next();
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
}
