/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */
package edu.msu.nscl.olog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * JDBC query to find database ids for logbooks/tags.
 * 
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
public class FindLogbookIdsQuery {

    private static SqlSessionFactory ssf = MyBatisSession.getSessionFactory();

    private FindLogbookIdsQuery() {
    }

    /**
     * Find the logbook names and ids for a specified log.
     *
     * @param data the XmlLog for which to generate the map
     * @return map of logbook names and ids
     * @throws CFException wrapping an SQLException
     */
    public static Map<String, Integer> getLogbookIdMap(XmlLog data) throws CFException {
        SqlSession ss = ssf.openSession();

        try {
            Map<String, Integer> result = new HashMap<String, Integer>();
            List<String> list = logbooksAndTagsToList(data);

            ArrayList<XmlLogbook> logbooks = (ArrayList<XmlLogbook>) ss.selectList("mappings.LogbookMapping.logbookIds", list);
            if (logbooks != null) {
                Iterator<XmlLogbook> iterator = logbooks.iterator();
                while (iterator.hasNext()) {
                    XmlLogbook logbook = iterator.next();
                    result.put(logbook.getName(), logbook.getId().intValue());
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
     * Find the id of a single tag or logbook.
     *
     * @param name the name to find
     * @return id
     */
    public static Long getLogbookId(String name) throws CFException {
        SqlSession ss = ssf.openSession();

        try {
            XmlLogbook lb = (XmlLogbook) ss.selectOne("mappings.LogbookMapping.logbookId", name);
            if (lb != null) {
                return lb.getId();
            } else {
                return null;
            }
        } catch (PersistenceException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "MyBatis exception: " + e);
        } finally {
            ss.close();
        }
    }

    private static List<String> logbooksAndTagsToList(XmlLog data) {
        List<String> list = new ArrayList();

        for (XmlLogbook logbook : data.getXmlLogbooks()) {
            list.add(logbook.getName().toLowerCase());
        }
        for (XmlTag tag : data.getXmlTags()) {
            list.add(tag.getName().toLowerCase());
        }

        return list;
    }
}
