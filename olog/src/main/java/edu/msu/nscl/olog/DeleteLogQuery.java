/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */
package edu.msu.nscl.olog;

import javax.ws.rs.core.Response;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * JDBC query to delete one log.
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
public class DeleteLogQuery {

    private static SqlSessionFactory ssf = MyBatisSession.getSessionFactory();

    private DeleteLogQuery(Long logId) {
    }

    /**
     * Deletes a log and its logbooks/tags from the database, failing if the
     * log does not exist.
     *
     * @param logId Log id
     * @throws CFException on fail or wrapping an SQLException
     */
    public static void deleteLogFailNoexist(Long logId) throws CFException {
        SqlSession ss = ssf.openSession();

        try {
            int rows = ss.update("mappings.LogMapping.deleteLog", logId);
            if (rows == 0) {
                throw new CFException(Response.Status.NOT_FOUND,
                        "Log '" + logId + "' does not exist");
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
     * Deletes a log and its logbooks/tags from the database.
     *
     * @param logId Log id
     * @throws CFException wrapping an SQLException
     */
    public static void deleteLogIgnoreNoexist(Long logId) throws CFException {
        SqlSession ss = ssf.openSession();

        try {
            ss.update("mappings.LogMapping.deleteLog", logId);
            ss.commit();
        } catch (PersistenceException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "MyBatis exception: " + e);
        } finally {
            ss.close();
        }
    }
}
