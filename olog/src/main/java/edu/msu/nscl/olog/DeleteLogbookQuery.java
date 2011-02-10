/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */
package edu.msu.nscl.olog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.ws.rs.core.Response;

/**
 * JDBC query to delete a logbook from one or all log(s).
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
public class DeleteLogbookQuery {

    private String name;
    private Long logId;
    private boolean removeLogbook = false;

    private DeleteLogbookQuery(String name, boolean removeLogbook) {
        this.name = name;
        this.removeLogbook = removeLogbook;
    }

    private DeleteLogbookQuery(String name,Long logId) {
        this.name = name;
        this.logId = logId;
    }

    /**
     * Creates and executes the JDBC based query.
     *
     * @param con connection to use
     * @param ignoreNoExist flag: true = do not generate an error if property/tag does not exist
     * @throws CFException wrapping an SQLException
     */
    private void executeQuery(Connection con, boolean ignoreNoExist) throws CFException {
        Long cid = null;
        PreparedStatement ps;
        String query;

        // Get logbook id
        Long lid = FindLogbookIdsQuery.getLogbookId(name);

        if (lid == null) {
            if (ignoreNoExist) {
                return;
            } else {
                throw new CFException(Response.Status.NOT_FOUND,
                        "Logbook/tag '" + name + "' does not exist");
            }
        }

        if (logId != null) {
            // Delete values for log
            try {
                query = "UPDATE logs_logbooks ll, statuses s, logs l "+
                        "SET ll.status_id = s.id "+
                        "WHERE s.name = 'Inactive' "+
                        "AND ll.logbook_id = ? "+
                        "AND ll.log_id = l.id "+
                        "AND (l.id = ? OR l.parent_id = ?)";
                ps = con.prepareStatement(query);
                ps.setLong(1, lid);
                ps.setLong(2, logId);
                ps.setLong(3, logId);
                int rows = ps.executeUpdate();
                if (rows == 0 && !ignoreNoExist) {
                    throw new CFException(Response.Status.NOT_FOUND,
                            "Logbook/tag '" + name + "' does not exist for log '"
                            + logId + "'");
                }
            } catch (SQLException e) {
                throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                        "SQL Exception while deleting logbook/tag '" + name +
                        "' from log '" + logId + "'", e);
            }

        } else {

            if (removeLogbook) {
                try {
                    query = "UPDATE logbooks l, statuses s "+
                            "SET l.status_id = s.id "+
                            "WHERE s.name = 'Inactive' "+
                            "AND l.id = ?";
                    ps = con.prepareStatement(query);
                    ps.setLong(1, lid);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                            "SQL Exception while deleting logbook/tag '" + name + "'", e);
                }
            } else {
                try {
                    query = "UPDATE logs_logbooks ll, statuses s "+
                            "SET ll.status_id = s.id "+
                            "WHERE s.name='Inactive' "+
                            "AND logbook_id = ?";
                    ps = con.prepareStatement(query);
                    ps.setLong(1, lid);
                    int rows = ps.executeUpdate();
                    if (rows == 0 && !ignoreNoExist) {
                        throw new CFException(Response.Status.NOT_FOUND,
                                "Logbook/tag '" + name + "' does not exist");
                    }
                } catch (SQLException e) {
                    throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                            "SQL Exception while deleting logbook/tag '" + name + "'", e);
                }
            }
        }
    }

    /**
     * Creates a DeleteLogbookQuery to completely remove a logbook/tag (from all
     * logs and the logbook/tag itself).
     *
     * @param name logbook/tag name
     */
    public static void removeLogbook(String name) throws CFException {
        DeleteLogbookQuery q = new DeleteLogbookQuery(name, true);
        q.executeQuery(DbConnection.getInstance().getConnection(), true);
    }

    /**
     * Creates a DeleteLogbookQuery to completely remove a logbook/tag (from all
     * logs and the logbook/tag itself).
     *
     * @param name logbook/tag name
     */
    public static void removeExistingLogbook(String name) throws CFException {
        DeleteLogbookQuery q = new DeleteLogbookQuery(name, true);
        q.executeQuery(DbConnection.getInstance().getConnection(), false);
    }

    /**
     * Creates a DeleteLogbookQuery to remove all values for the specified logbook/tag
     * (without removing the logbook/tag itself).
     *
     * @param name logbook/tag name
     * @throws CFException wrapping an SQLException
     */
    public static void deleteAllValues(String name) throws CFException {
        DeleteLogbookQuery q = new DeleteLogbookQuery(name, false);
        q.executeQuery(DbConnection.getInstance().getConnection(), true);
    }

    /**
     * Creates a DeleteLogbookQuery to remove one value of the specified logbook/tag
     * from the specified log.
     *
     * @param name logbook/tag name
     * @param logId log to delete <tt>name</tt> from
     */
    public static void deleteOneValue(String name,Long logId) throws CFException {
        DeleteLogbookQuery q = new DeleteLogbookQuery(name, logId);
        q.executeQuery(DbConnection.getInstance().getConnection(), false);
    }
}
