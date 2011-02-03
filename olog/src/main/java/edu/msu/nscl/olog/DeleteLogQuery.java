/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */
package edu.msu.nscl.olog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ws.rs.core.Response;

/**
 * JDBC query to delete one channel.
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
public class DeleteLogQuery {

    private Long logId;

    private DeleteLogQuery(Long logId) {
        this.logId = logId;
    }

    /**
     * Creates and executes a JDBC based query for deleting one log.
     *
     * @param con db connection to use
     * @param ignoreNoExist flag: true = do not generate an error if channel does not exist
     * @throws CFException wrapping an SQLException
     */
    private void executeQuery(Connection con, boolean ignoreNoExist) throws CFException {
        String query;
        PreparedStatement ps;
        try {
            query = "UPDATE logs, statuses SET logs.status_id = statuses.id WHERE (logs.id = ? OR parent_id = ?) AND statuses.name = 'Inactive';";
            ps = con.prepareStatement(query);
            ps.setLong(1, logId);
            int rows = ps.executeUpdate();
            if (rows == 0 && !ignoreNoExist) {
                throw new CFException(Response.Status.NOT_FOUND,
                        "Log '" + logId + "' does not exist");
            }
        } catch (SQLException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "SQL Exception while deleting log '" + logId + "'", e);
        }
    }

    /**
     * Deletes a log and its logbooks/tags from the database, failing if the
     * log does not exist.
     *
     * @param logId Log id
     * @throws CFException on fail or wrapping an SQLException
     */
    public static void deleteLogFailNoexist(Long logId) throws CFException {
        DeleteLogQuery q = new DeleteLogQuery(logId);
        q.executeQuery(DbConnection.getInstance().getConnection(), false);
    }

    /**
     * Deletes a log and its logbooks/tags from the database.
     *
     * @param logId Log id
     * @throws CFException wrapping an SQLException
     */
    public static void deleteLogIgnoreNoexist(Long logId) throws CFException {
        DeleteLogQuery q = new DeleteLogQuery(logId);
        q.executeQuery(DbConnection.getInstance().getConnection(), true);
    }
}
