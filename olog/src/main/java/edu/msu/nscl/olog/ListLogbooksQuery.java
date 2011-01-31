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
import java.util.List;
import javax.ws.rs.core.Response;

/**
 * JDBC query to find logbooks/tags.
 * 
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
public class ListLogbooksQuery {
    private String name;

    private ListLogbooksQuery() {
    }

    private ListLogbooksQuery(String name) {
        this.name = name;
    }

    /**
     * Creates and executes a JDBC based query returning logbooks or tags.
     *
     * @param con connection to use
     * @return result set with columns named <tt>id</tt>, <tt>name</tt> or null if no result
     * @throws CFException wrapping an SQLException
     */
    private ResultSet executeQuery(Connection con, boolean isTagQuery) throws CFException {
        PreparedStatement ps;
        List<String> name_params = new ArrayList<String>();

        StringBuilder query = new StringBuilder("SELECT id, name, owner FROM logbook ");

        if (isTagQuery) {
            query.append("WHERE is_tag = TRUE");
        } else {
            query.append("WHERE is_tag = FALSE");
        }
        if (name != null) {
            query.append(" AND name = ?");
        }
        try {
            ps = con.prepareStatement(query.toString());
            if (name != null) {
                ps.setString(1, name);
            }
            return ps.executeQuery();
        } catch (SQLException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "SQL Exception during logbook/tag list query", e);
        }
    }

    /**
     * Returns the list of logbooks in the database.
     *
     * @return XmlLogbooks
     * @throws CFException wrapping an SQLException
     */
    public static XmlLogbooks getLogbooks() throws CFException {
        XmlLogbooks result = new XmlLogbooks();
        ListLogbooksQuery q = new ListLogbooksQuery();
        try {
            ResultSet rs = q.executeQuery(DbConnection.getInstance().getConnection(), false);
            if (rs != null) {
                while (rs.next()) {
                    result.addXmlLogbook(new XmlLogbook(rs.getString("name"), rs.getString("owner")));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "SQL Exception scanning result of logbook list request", e);
        }
    }

    /**
     * Finds a logbook in the database by name.
     *
     * @return XmlLogbook
     * @throws CFException wrapping an SQLException
     */
    public static XmlLogbook findLogbook(String name) throws CFException {
        XmlLogbook result = null;
        ListLogbooksQuery q = new ListLogbooksQuery(name);
        try {
            ResultSet rs = q.executeQuery(DbConnection.getInstance().getConnection(), false);
            if (rs != null) {
                while (rs.next()) {
                    result = new XmlLogbook(rs.getString("name"), rs.getString("owner"));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "SQL Exception scanning result of find logbook request", e);
        }
    }

    /**
     * Returns the list of tags in the database.
     *
     * @return XmlTags
     * @throws CFException wrapping an SQLException
     */
    public static XmlTags getTags() throws CFException {
        XmlTags result = new XmlTags();
        ListLogbooksQuery q = new ListLogbooksQuery();
        try {
            ResultSet rs = q.executeQuery(DbConnection.getInstance().getConnection(), true);
            if (rs != null) {
                while (rs.next()) {
                    result.addXmlTag(new XmlTag(rs.getString("name")));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "SQL Exception scanning result of tag list request", e);
        }
    }

    /**
     * Finds a tag in the database by name.
     *
     * @return XmlTag
     * @throws CFException wrapping an SQLException
     */
    public static XmlTag findTag(String name) throws CFException {
        XmlTag result = null;
        ListLogbooksQuery q = new ListLogbooksQuery(name);
        try {
            ResultSet rs = q.executeQuery(DbConnection.getInstance().getConnection(), true);
            if (rs != null) {
                while (rs.next()) {
                    result = new XmlTag(rs.getString("name"));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "SQL Exception scanning result of find tag request", e);
        }
    }
}
