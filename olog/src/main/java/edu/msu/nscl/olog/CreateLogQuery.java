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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response;

/**
 * JDBC query to create one channel.
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
public class CreateLogQuery {

    private XmlLog log;

    private CreateLogQuery(XmlLog log) {
        this.log = log;
    }

    /**
     * Executes a JDBC based query to add a log and its logbooks/tags.
     *
     * @param con database connection to use
     * @throws CFException wrapping an SQLException
     */
    private void executeQuery(Connection con) throws CFException {
        List<List<String>> params = new ArrayList<List<String>>();
        PreparedStatement ps;
        int i;
        long id;

        // Insert log
        StringBuilder query = new StringBuilder("INSERT INTO log (name, owner) VALUE (?, ?)");
        try {
            ps = con.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, log.getSubject());
            ps.setString(2, log.getOwner());
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            rs.first();
            id = rs.getLong(1);
        } catch (SQLException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "SQL Exception while adding log '" + log.getSubject() +"'", e);
        }

        // Fetch the logbook/tag ids
        Map<String, Integer> pids = FindLogbookIdsQuery.getLogbookIdMap(log);

        // Insert logbook/tags
        if (this.log.getXmlLogbooks().getLogbooks().size() > 0
                || this.log.getXmlTags().getTags().size() > 0) {
            params.clear();
            query.setLength(0);
            query.append("INSERT INTO value (log_id, logbook_id, value) VALUES ");
            for (XmlLogbook logbook : this.log.getXmlLogbooks().getLogbooks()) {
                if (pids.get(logbook.getName()) == null) {
                    throw new CFException(Response.Status.NOT_FOUND,
                    "Logbook '" + logbook.getName() + "' does not exist");
                }
                query.append("(?,?,?),");
                ArrayList<String> par = new ArrayList<String>();
                par.add(logbook.getName());
                params.add(par);
            }
            for (XmlTag tag : this.log.getXmlTags().getTags()) {
                if (pids.get(tag.getName()) == null) {
                    throw new CFException(Response.Status.NOT_FOUND,
                    "Tag '" + tag.getName() + "' does not exist");
                }
                query.append("(?,?,?),");
                ArrayList<String> par = new ArrayList<String>();
                par.add(tag.getName());
                par.add(null);
                params.add(par);
            }
            try {
                ps = con.prepareStatement(query.substring(0, query.length() - 1));
                i = 1;
                for (List<String> par : params) {
                    ps.setLong(i++, id);
                    ps.setLong(i++, pids.get(par.get(0)));
                    if (par.get(1) == null) {
                        ps.setNull(i++, java.sql.Types.NULL);
                    } else {
                        ps.setString(i++, par.get(1));
                    }
                }
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                        "SQL Exception while adding logbooks/tags for log '" + log.getSubject() + "'", e);
            }
        }
    }

    /**
     * Creates a log and its logbooks/tags in the database.
     *
     * @param log XmlLog object
     * @throws CFException wrapping an SQLException
     */
    public static void createLog(XmlLog log) throws CFException {
        CreateLogQuery q = new CreateLogQuery(log);
        q.executeQuery(DbConnection.getInstance().getConnection());
    }
}
