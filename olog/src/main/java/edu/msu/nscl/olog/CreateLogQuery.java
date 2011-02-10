/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */
package edu.msu.nscl.olog;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
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
    private XmlLog executeQuery(Connection con) throws CFException, UnsupportedEncodingException, NoSuchAlgorithmException {
        List<List<String>> params = new ArrayList<List<String>>();
        PreparedStatement ps;
        int i;
        long id;

        // Insert log
        StringBuilder query = new StringBuilder("INSERT INTO logs "+
                "(source, owner, level_id, status_id, subject, description, md5entry, md5recent, parent_id) "+
                "VALUE (?, ?, (SELECT id from levels where name = ?), (SELECT id from statuses where name = 'Active'), ?, ?, '', '', null)");
        try {
            ps = con.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, log.getSource());
            ps.setString(2, log.getOwner());
            ps.setString(3, log.getLevel());
            ps.setString(4, log.getSubject());
            ps.setString(5, log.getDescription());
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            rs.first();
            id = rs.getLong(1);
        } catch (SQLException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "SQL Exception while adding log '" + log.getSubject() +"'", e);
        }
        query = new StringBuilder("UPDATE logs SET md5entry=?, md5recent=?, parent_id=? where id=?");
        try {
            ps = con.prepareStatement(query.toString());
            ps.setString(1, getmd5Entry(id, con));
            ps.setString(2, getmd5Recent(id, con));
            if(logIdExists(con)){
                ps.setLong(3, log.getId());
            } else {
                log.setId(id);
                ps.setNull(3, java.sql.Types.NULL);
            }
            ps.setLong(4, id);
            ps.execute();
        } catch (SQLException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "SQL Exception while adding log '" + log.getSubject() +"'", e);
        }
        // Fetch the logbook/tag ids
        Map<String, Integer> pids = FindLogbookIdsQuery.getLogbookIdMap(log);

        // Insert logbook/tags
        // Fail if there isn't at least one logbook
        if (this.log.getXmlLogbooks().getLogbooks().isEmpty()) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "Must add to at least one logbook '" + log.getSubject() +"'");
        }
        if (this.log.getXmlLogbooks().getLogbooks().size() > 0
                || this.log.getXmlTags().getTags().size() > 0) {
            params.clear();
            query.setLength(0);
            query.append("INSERT INTO logs_logbooks (log_id, logbook_id, state) VALUES ");
            for (XmlLogbook logbook : this.log.getXmlLogbooks().getLogbooks()) {
                if (pids.get(logbook.getName()) == null) {
                    throw new CFException(Response.Status.NOT_FOUND,
                    "Logbook '" + logbook.getName() + "' does not exist");
                }
                query.append("(?,?,?),");
                ArrayList<String> par = new ArrayList<String>();
                par.add(logbook.getName());
                par.add(null);
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
        return log;
    }

    /**
     * Creates a log and its logbooks/tags in the database.
     *
     * @param log XmlLog object
     * @throws CFException wrapping an SQLException
     */
    public static XmlLog createLog(XmlLog log) throws CFException, UnsupportedEncodingException, NoSuchAlgorithmException {
        CreateLogQuery q = new CreateLogQuery(log);
        return q.executeQuery(DbConnection.getInstance().getConnection());
    }

    /**
     * Check if log already exist
     *
     * @return TRUE if log exists
     */
    private boolean logIdExists(Connection con) throws CFException {
        String query = "SELECT id " +
                       "FROM logs " +
                       "WHERE id = ?";
        if(log.getId() == null)
            return false;
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setLong(1, log.getId());
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "SQL Exception while checking if log id exists", e);
        }
    }

    /**
     * Compute md5 for 10 most recent log entries from this log id
     *
     * Empty created timestamps are NOT allowed.
     *
     * @return md5Recent String of the last 10 md5Entries
     */
    private String getmd5Recent(Long logId, Connection con) throws CFException {
        String md5Recent = "";
        String query = "SELECT id, md5entry " +
                       "FROM logs " +
                       "WHERE id < ? " +
                       "ORDER BY id DESC " +
                       "LIMIT 10";

        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setLong(1, logId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                md5Recent += rs.getString(1)+" "+rs.getString(2)+"\n";
            }
        } catch (SQLException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "SQL Exception while getting recent md5 entries: "+query, e);
        }
        return md5Recent;
    }

    /**
     * Calculate the md5 for the XmlLog object
     *
     * @return md5Entry String MD5 encoded XmlLog Object
     * @todo Move this to LogEnt as a private function
     */
     private String getmd5Entry(Long logId, Connection con) throws UnsupportedEncodingException, NoSuchAlgorithmException, CFException {
        String entry;
        String explodeRecent = "";
        List<String> explodeRecentArray = new ArrayList<String>();
        explodeRecentArray = Arrays.asList(getmd5Recent(logId, con).split("\n"));

        for (String line : explodeRecentArray) {
            if ( (line == null ? "" == null : line.equals("")) || (line == null ? "\n" == null : line.equals("\n")) ) continue;
            explodeRecent += "md5_recent:" + line + "\n";
        }

        entry = "id:"           + logId                 + "\n" +
                "level:"        + log.getLevel()        + "\n" +
                "subject:"      + log.getSubject()      + "\n" +
                "description:"  + log.getDescription()  + "\n" +
                "created:"      + log.getCreatedDate()  + "\n" +
                "modified"      + log.getModifiedDate() + "\n" +
                "source:"       + log.getSource()       + "\n" +
                "owner:"        + log.getOwner()        + "\n" +
                explodeRecent;

        byte[] bytesOfMessage = entry.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5Entry = md.digest(bytesOfMessage);
        BigInteger md5Number = new BigInteger(1, md5Entry);
        String md5EntryString = md5Number.toString(16);

        return md5EntryString;
    }
}
