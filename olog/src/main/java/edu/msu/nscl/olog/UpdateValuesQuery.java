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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * JDBC query to add a logbook to log(s).
 * 
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
public class UpdateValuesQuery {

    private XmlLogs logs;
    private boolean isTagQuery = false;
    private String oldname;
    private String name;
    private String owner;
    private String dbname;
    private String dbowner;
    private Logger audit = Logger.getLogger(this.getClass().getPackage().getName() + ".audit");

    private String getType() {
        if (isTagQuery) {
            return "tag";
        } else {
            return "logbook";
        }
    }

    /**
     * Creates a new instance of UpdateValuesQuery.
     *
     * @param data logbook data (containing logs to add logbook to)
     */
    private UpdateValuesQuery(String name, XmlLogbook data) {
        this.oldname = name;
        this.name = data.getName();
        this.owner = data.getOwner();
        this.logs = data.getXmlLogs();
    }

    /**
     * Creates a new instance of UpdateValuesQuery.
     *
     * @param data logbook data (containing logs to add logbook to)
     */
    private UpdateValuesQuery(String name, XmlTag data) {
        this.oldname = name;
        this.name = data.getName();
        this.logs = data.getXmlLogs();
        this.isTagQuery = true;
    }

    /**
     * Creates a new instance of UpdateValuesQuery for a single tag on a single log
     *
     * @param name name of tag to add
     * @param owner owner for tag to add
     * @param logId id to add tag to
     */
    private UpdateValuesQuery(String name,Long logId) {
        this.name = name;
        logs = new XmlLogs(new XmlLog(logId));
        isTagQuery = true;
    }

    /**
     * Creates and executes a JDBC based query to add a logbook to the listed logs
     *
     * @param con  connection to use
     * @throws CFException wrapping an SQLException
     */
    public void executeQuery(Connection con) throws CFException {
        List<String> params = new ArrayList<String>();
        Map<Long, String> ids = new HashMap<Long, String>();
        Map<Long, String> newestVersionIds = new HashMap<Long, String>();
        Map<String, String> values = new HashMap<String, String>();
        PreparedStatement ps;
        int i;

        // Get logbook id
        Long pid = FindLogbookIdsQuery.getLogbookId(name);

        if (pid == null) {
            throw new CFException(Response.Status.NOT_FOUND,
                    "A " + getType() + " named '" + name + "' does not exist");
        }

        // Update name and owner if necessary
        if (isTagQuery) {
            XmlTag t = ListLogbooksQuery.findTag(name);
            dbname = t.getName();
        } else {
            XmlLogbook p = ListLogbooksQuery.findLogbook(name);
            dbname = p.getName();
            dbowner = p.getOwner();
        }
        if ((oldname != null && !oldname.equals(name)) || (owner != null && !dbowner.equals(owner))) {
            String q = "UPDATE logbooks SET name = ?, owner = ? WHERE id = ?";
            try {
                ps = con.prepareStatement(q.toString());
                ps.setString(1, name);
                ps.setString(2, owner);
                ps.setLong(3, pid);

                ps.executeUpdate();
            } catch (SQLException e) {
                throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                        "SQL Exception while updating "
                        + getType() + " '" + name + "'", e);
            }
        }

        if (logs==null) return;
        if (logs.getLogs().isEmpty()) return;
        if (logs.getLogs().iterator().next().getSubject()==null) return;

        // Get Log ids
        StringBuilder query = new StringBuilder("SELECT log.id FROM `logs` as log "+
                                                "LEFT JOIN `logs` as parent ON log.id = parent.parent_id "+
                                                "WHERE (parent.parent_id IS NULL and log.parent_id IS NULL "+
                                                "OR log.id IN (SELECT MAX(logs.id) FROM logs WHERE logs.parent_id=log.parent_id)) "+
                                                "AND log.parent_id IN (");

        for (XmlLog log : logs.getLogs()) {
            if(log.getVersion() > 0 ){
                newestVersionIds.put(log.getId(), null);
                query.append("?, ");
            } else {
                ids.put(log.getId(), null);
            }
        }
        query.replace(query.length() - 2, query.length(), ")");

        if(!newestVersionIds.isEmpty()) {

            try {
                ps = con.prepareStatement(query.toString());
                i = 1;
                for (Long id : newestVersionIds.keySet()) {
                    ps.setLong(i++, id);
                }

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    ids.put(rs.getLong(1), null);
                }
            } catch (SQLException e) {
                throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                        "SQL Exception while retrieving log ids for insertion of "
                           + getType() + " '" + name + "' "+newestVersionIds.toString()+query.toString(), e);
            }
        }
        if (ids.isEmpty()) {
            throw new CFException(Response.Status.NOT_FOUND,
                    "Logs specified in " + getType() + " update do not exist");
        }

        // Get values from payload
//        for (XmlLog log : logs.getLogs()) {
//            for (XmlLogbook logbook : log.getXmlLogbooks().getLogbooks()) {
//                if (name.equals(logbook.getName())) {
//                    values.put(log.getName(), logbook.getValue());
//                }
//            }
//        }

        // Remove existing values for the specified logs
        query.setLength(0);
        params.clear();
        query.append("UPDATE logs_logbooks ll, statuses s "+
                     "SET ll.status_id=s.id "+
                     "WHERE s.name='Inactive' AND ll.logbook_id = ? AND ll.log_id NOT IN (");
        for (Long id : ids.keySet()) {
            query.append("?, ");
        }
        query.replace(query.length() - 2, query.length(), ")");

        try {
            ps = con.prepareStatement(query.toString());
            ps.setLong(1, pid);
            i = 2;
            for (Long id : ids.keySet()) {
                ps.setLong(i++, id);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "SQL Exception while deleting values for " + getType() + " '"
                    + name + "' (before reinsert) "+query.toString(), e);
        }
        // Add new values
        query.setLength(0);
        params.clear();
        query.append("INSERT INTO logs_logbooks (log_id, logbook_id, state) VALUES ");
        for (Long id : ids.keySet()) {
            query.append("(?,?,?),");
        }
        try {
            ps = con.prepareStatement(query.substring(0, query.length() - 1));
            i = 1;
            for (Long log : ids.keySet()) {
                ps.setLong(i++, log);
                ps.setLong(i++, pid);
                if (ids.get(log) == null) {
                    ps.setNull(i++, java.sql.Types.NULL);
                } else {
                    ps.setString(i++, ids.get(log));
                }
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "SQL Exception while inserting values for " + getType()
                    + " '" + name + "' ", e);
        }
    }

    /**
     * Updates a logbook in the database.
     *
     * @param logbook XmlLogbook
     * @throws CFException wrapping an SQLException
     */
    public static void updateLogbook(String name, XmlLogbook logbook) throws CFException {
        UpdateValuesQuery q = new UpdateValuesQuery(name, logbook);
        q.executeQuery(DbConnection.getInstance().getConnection());
    }

    /**
     * Updates a tag in the database, adding it to all logs in <tt>tag</tt>.
     *
     * @param tag XmlTag
     * @throws CFException wrapping an SQLException
     */
    public static void updateTag(String name, XmlTag tag) throws CFException {
        UpdateValuesQuery q = new UpdateValuesQuery(name, tag);
        q.executeQuery(DbConnection.getInstance().getConnection());
    }

    /**
     * Updates the <tt>tag</tt> in the database, adding it to the single log <tt>chan</tt>.
     *
     * @param tag name of tag to add
     * @param logId id of log to add tag to
     * @throws CFException wrapping an SQLException
     */
    public static void updateTag(String tag,Long logId) throws CFException {
        UpdateValuesQuery q = new UpdateValuesQuery(tag, logId);
        q.executeQuery(DbConnection.getInstance().getConnection());
    }
}
