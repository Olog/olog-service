/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */
package edu.msu.nscl.olog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response;

/**
 * JDBC query to create a property/tag.
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
public class CreateLogbookQuery {

    private String name;
    private String owner;
    private boolean isTagQuery = false;

    private String getType() {
        if (isTagQuery) {
            return "tag";
        } else {
            return "logbook";
        }
    }

    private CreateLogbookQuery(String name, String owner, boolean isTagQuery) {
        this.name = name;
        this.owner = owner;
        this.isTagQuery = isTagQuery;
    }

    /**
     * Executes a JDBC based query to add Logbooks/tags.
     *
     * @param con database connection to use
     * @throws CFException wrapping an SQLException
     */
    private void executeQuery(Connection con) throws CFException {
        List<List<String>> params = new ArrayList<List<String>>();
        PreparedStatement ps;

        // Insert property
        String query = "INSERT INTO logbook (name, owner, is_tag) VALUE (?, ?, ?)";
        try {
            ps = con.prepareStatement(query);
            ps.setString(1, name);
            ps.setString(2, owner);
            ps.setBoolean(3, isTagQuery);
            ps.execute();
        } catch (SQLException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "SQL Exception while adding " + getType() + " '" + name +"'", e);
        }
    }

    /**
     * Creates a logbook in the database.
     *
     * @param name name of logbook
     * @param owner owner of logbook
     * @throws CFException wrapping an SQLException
     */
    public static void createLogbook(String name, String owner) throws CFException {
        CreateLogbookQuery q = new CreateLogbookQuery(name, owner, false);
        q.executeQuery(DbConnection.getInstance().getConnection());
    }

    /**
     * Creates a tag in the database.
     *
     * @param name name of tag
     * @param owner owner of tag
     * @throws CFException wrapping an SQLException
     */
    public static void createTag(String name) throws CFException {
        CreateLogbookQuery q = new CreateLogbookQuery(name, null, true);
        q.executeQuery(DbConnection.getInstance().getConnection());
    }
}
