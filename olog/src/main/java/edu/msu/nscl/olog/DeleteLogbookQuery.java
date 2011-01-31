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
 * JDBC query to delete a logbook from one or all log(s).
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
public class DeleteLogbookQuery {

    private String name;
    private int logId;
    private boolean removeLogbook = false;

    private DeleteLogbookQuery(String name, boolean removeProperty) {
        this.name = name;
        this.removeLogbook = removeProperty;
    }

    private DeleteLogbookQuery(String name, int logId) {
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

        // Get property id
        Long pid = FindLogbookIdsQuery.getLogbookId(name);

        if (pid == null) {
            if (ignoreNoExist) {
                return;
            } else {
                throw new CFException(Response.Status.NOT_FOUND,
                        "Property/tag '" + name + "' does not exist");
            }
        }
// TODO: Don't need this
        if (logId != 0) {
            // Get log id
            try {
                query = "SELECT id FROM log WHERE name = ?";
                ps = con.prepareStatement(query);
                ps.setInt(1, logId);

                ResultSet rs = ps.executeQuery();
                if (rs.first()) {
                    cid = rs.getLong(1);
                } else {
                    throw new CFException(Response.Status.NOT_FOUND,
                            "Log '" + logId + "' does not exist");
                }
            } catch (SQLException e) {
                throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                        "SQL Exception while preparing deletion of property/tag '" + name
                        + "' from log '" + logId + "'", e);
            }
            // Delete values for log
            try {
                query = "DELETE FROM value WHERE logbook_id = ? AND log_id = ?";
                ps = con.prepareStatement(query);
                ps.setLong(1, pid);
                ps.setLong(2, cid);
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
                    query = "DELETE FROM logbook WHERE id = ?";
                    ps = con.prepareStatement(query);
                    ps.setLong(1, pid);
                    int rows = ps.executeUpdate();
                } catch (SQLException e) {
                    throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                            "SQL Exception while deleting logbook/tag '" + name + "'", e);
                }
            } else {
                try {
                    query = "DELETE FROM value WHERE logbook_id = ?";
                    ps = con.prepareStatement(query);
                    ps.setLong(1, pid);
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
     * @return new FindLogsQuery instance
     */
    public static void deleteOneValue(String name, int logId) throws CFException {
        DeleteLogbookQuery q = new DeleteLogbookQuery(name, logId);
        q.executeQuery(DbConnection.getInstance().getConnection(), false);
    }
}
