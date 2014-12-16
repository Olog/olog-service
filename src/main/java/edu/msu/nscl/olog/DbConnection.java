/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author berryman
 */
public class DbConnection {
    private static ThreadLocal<DbConnection> instance = new ThreadLocal<DbConnection>() {

        @Override
        protected DbConnection initialValue() {
            return new DbConnection();
        }
    };
    private static final String dbResourceName = "jdbc/olog";
    private DataSource ds;

    private DbConnection() {
        try {
            InitialContext ic = new InitialContext();
            ds = (DataSource) ic.lookup("java:comp/env/" + dbResourceName);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot find JDBC DataSource '"
                    + dbResourceName + "'", e);
        }
    }

    /**
     * Returns the DbConnection instance.
     *
     * @return DbConnection instance
     */
    public static DbConnection getInstance() {
        return instance.get();
    }

    /**
     * Returns the DataSource Connection, requesting a new one (thread local) if needed.
     *
     * @return Connection to the JDBC DataSource
     * @throws OlogException wrapping an SQLException
     */
    public DataSource getDataSource() throws OlogException {
        return ds;
    }
    
    public void close() {
        try {
            if (ds.getConnection()!=null){
                ds.getConnection().close();   
            }
            instance.remove();
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void finalize() {
        close();
    }
}
