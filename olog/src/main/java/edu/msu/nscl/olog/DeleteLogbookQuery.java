/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */
package edu.msu.nscl.olog;

import java.util.HashMap;
import javax.ws.rs.core.Response;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * JDBC query to delete a logbook from one or all log(s).
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
public class DeleteLogbookQuery {

    private static SqlSessionFactory ssf = MyBatisSession.getSessionFactory();

    private DeleteLogbookQuery() {
    }

    /**
     * Creates a DeleteLogbookQuery to completely remove a logbook/tag (from all
     * logs and the logbook/tag itself).
     *
     * @param name logbook/tag name
     */
    public static void removeLogbook(String name) throws CFException {
        SqlSession ss = ssf.openSession();

        try {
            // Get logbook id
            Long lid = FindLogbookIdsQuery.getLogbookId(name);
            ss.update("mappings.LogbookMapping.removeLogbook", lid);

            ss.commit();
        } catch (PersistenceException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "MyBatis exception: " + e);
        } finally {
            ss.close();
        }
    }

    /**
     * Creates a DeleteLogbookQuery to completely remove a logbook/tag (from all
     * logs and the logbook/tag itself).
     *
     * @param name logbook/tag name
     */
    public static void removeExistingLogbook(String name) throws CFException {
        SqlSession ss = ssf.openSession();

        try {
            // Get logbook id
            Long lid = FindLogbookIdsQuery.getLogbookId(name);
            int rows = ss.update("mappings.LogbookMapping.removeExistingLogbook", lid);
            if (rows == 0) {
                throw new CFException(Response.Status.NOT_FOUND,
                        "Logbook/tag '" + name + "' does not exist for log '" + lid + "'");
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
     * Creates a DeleteLogbookQuery to remove all values for the specified logbook/tag
     * (without removing the logbook/tag itself).
     *
     * @param name logbook/tag name
     * @throws CFException wrapping an SQLException
     */
    public static void deleteAllValues(String name) throws CFException {
        SqlSession ss = ssf.openSession();

        try {
            // Get logbook id
            Long lid = FindLogbookIdsQuery.getLogbookId(name);
            ss.update("mappings.LogbookMapping.deleteAllValues", lid);

            ss.commit();
        } catch (PersistenceException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "MyBatis exception: " + e);
        } finally {
            ss.close();
        }
    }

    /**
     * Creates a DeleteLogbookQuery to remove one value of the specified logbook/tag
     * from the specified log.
     *
     * @param name logbook/tag name
     * @param logId log to delete <tt>name</tt> from
     */
    public static void deleteOneValue(String name, Long logId) throws CFException {
        SqlSession ss = ssf.openSession();

        try {
            // Get logbook id
            Long lid = FindLogbookIdsQuery.getLogbookId(name);

            // Fill in a hashmap to send to mybatis query
            HashMap<String, Long> hm = new HashMap<String, Long>();
            hm.put("lid", lid);
            hm.put("logid", logId);

            ss.update("mappings.LogbookMapping.deleteOneValue", hm);

            ss.commit();
        } catch (PersistenceException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "MyBatis exception: " + e);
        } finally {
            ss.close();
        }
    }
}
