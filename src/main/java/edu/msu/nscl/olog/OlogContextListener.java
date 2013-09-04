/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

/**
 *
 * @author berryman
 */
import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.migration.SchemaVersion;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.sql.DataSource;
import javax.ws.rs.core.Response;

import org.apache.ddlutils.PlatformUtils;
import org.apache.ddlutils.platform.mysql.MySqlPlatform;
import org.apache.ddlutils.platform.postgresql.PostgreSqlPlatform;
import org.apache.jackrabbit.core.RepositoryImpl;

public class OlogContextListener implements ServletContextListener {

    private static OlogContextListener instance = new OlogContextListener();
    private static ServletContext context;
    private JCRUtil repo;

	private enum DatabaseType {
		mysql, pgsql;

		public static DatabaseType determinefromDataSource(DataSource dataSource) {
			String dbType = new PlatformUtils()
					.determineDatabaseType(dataSource);
			if (PostgreSqlPlatform.DATABASENAME.equals(dbType)) {
				return pgsql;
			}
			if (MySqlPlatform.DATABASENAME.equals(dbType)) {
				return pgsql;
			}
			return null;
		}
	}
    public static OlogContextListener getInstance() {
        return instance;
    }

    public static ServletContext getContext() {
        return context;
    }
    /*
     * This method is invoked when the Web Application has been removed and is
     * no longer able to accept requests
     */

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        JPAUtil.getEntityManagerFactory().close();
        ((RepositoryImpl) repo.getRepository()).shutdown();
        System.out.println("Olog JCR and JPA Sessions have been removed");

    }

    //This method is invoked when the Web Application
    //is ready to service requests
    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            context = event.getServletContext();
            if (context == null) {
                System.out.println("Couldn't get servlet context.");
            } else {
                System.out.println("Servlet context fetched from ServiceContext.");
            }

            Flyway flyway = new Flyway();
			DataSource dataSource = DbConnection.getInstance().getDataSource();
			if (dataSource == null) {
				throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
						"Datasource is null");
			}
			flyway.setDataSource(dataSource);

			DatabaseType dbType = DatabaseType
					.determinefromDataSource(dataSource);
			if (dbType == null) {
				throw new CFException(
						Response.Status.INTERNAL_SERVER_ERROR,
						"Unable to determine database engine "
								+ "or engine not supported (supported: Postgresql or MySQL).");
			}

			flyway.setLocations("db." + dbType.name() + ".migration");
            if (flyway.history().isEmpty()) {
				switch (dbType) {
				case mysql:
                flyway.setInitialVersion(new SchemaVersion("1.00"));
                flyway.setInitialDescription("Base version");
					break;
				case pgsql:
					flyway.setInitialVersion(new SchemaVersion("2.11"));
					flyway.setInitialDescription("Base version");
					break;
				}
                flyway.init();
            }
            flyway.migrate();
            System.out.println("Database is up to date: ");

            repo = new JCRUtil();
            System.out.println("Olog JCR has been initialized: ");
        } catch (CFException ex) {
            Logger.getLogger(OlogContextListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
