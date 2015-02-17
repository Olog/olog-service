/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

/**
 *
 * @author berryman
 */
import edu.msu.nscl.olog.control.OlogImpl;
import edu.msu.nscl.olog.entity.Logbooks;
import edu.msu.nscl.olog.entity.XmlProperties;
import edu.msu.nscl.olog.entity.XmlProperty;
import edu.msu.nscl.olog.entity.Logbook;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.ddlutils.PlatformUtils;
import org.apache.ddlutils.platform.mysql.MySqlPlatform;
import org.apache.ddlutils.platform.postgresql.PostgreSqlPlatform;
import org.apache.jackrabbit.core.RepositoryImpl;

import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.api.MigrationVersion;
import edu.msu.nscl.olog.entity.BitemporalLog;
import edu.msu.nscl.olog.entity.XmlLog;
import java.sql.SQLException;
import org.apache.cxf.jaxrs.impl.MetadataMap;
import org.dozer.DozerBeanMapperSingletonWrapper;

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
				return mysql;
			}
			return null;
		}
	}
    
    private static final String MIGRATION_PATH = "MIGRATION_PATH";    
    
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
			DataSource dataSource = DbConnection.getInstance().getDataSource();
			if (dataSource == null) {
				throw new OlogException(Response.Status.INTERNAL_SERVER_ERROR,
						"Datasource is null");
			}
			DatabaseType dbType = DatabaseType
					.determinefromDataSource(dataSource);
			if (dbType == null) {
				throw new OlogException(
						Response.Status.INTERNAL_SERVER_ERROR,
						"Unable to determine database engine "
								+ "or engine not supported (supported: Postgresql or MySQL).");
			}
            
            // We could have different migration paths, as defined by directory names inside src/java/resources/db directory
            // Migration path is configured via edu.msu.nscl.olog.OlogContextListener.MIGRATION parameter in web.xml
            String migrationPath = context.getInitParameter(this.getClass().getName()+"."+MIGRATION_PATH);
			if (migrationPath == null || "migration".equals(migrationPath))
				migrationPath = dbType.name() + File.separator + "migration";
            
            Flyway flyway = new Flyway();
            flyway.setLocations("db" + File.separator + migrationPath);
			flyway.setDataSource(dataSource);
			
            if (flyway.info().applied().length==0) {
				switch (dbType) {
				case mysql:
	                flyway.setInitVersion(new MigrationVersion("1.00"));
					break;
				case pgsql:
					flyway.setInitVersion(new MigrationVersion("2.11"));
					break;
				}
				flyway.setInitDescription("Base version");
                flyway.init();
            }
            
            flyway.migrate();
            System.out.println("Database is up to date: ");
            DbConnection.getInstance().close();

            repo = new JCRUtil();
            try {
                // start Dozer singleton
                BitemporalLog result = DozerBeanMapperSingletonWrapper.getInstance().map(new XmlLog(), BitemporalLog.class);
                preCache();
            } catch (Exception e) {
                Logger.getLogger(OlogContextListener.class.getName()).log(Level.SEVERE, null, e);

            }
            System.out.println("Olog JCR has been initialized: ");
        } catch (OlogException ex) {
            Logger.getLogger(OlogContextListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void preCache() throws Exception {
        OlogImpl cm = OlogImpl.getInstance();
        Logbooks logbooks  = cm.listLogbooks();
        for (Logbook logbook : logbooks.getLogbooks()) {
            MultivaluedMap<String,String> map = new MetadataMap();
            map.add("logbook", logbook.getName());
            map.add("limit", "10");
            cm.findLogsByMultiMatch(map);
        }
        cm.listLogbooks();
        cm.listTags();
        XmlProperties properties = cm.listProperties();
        for(XmlProperty property : properties.getProperties()) {
            cm.listAttributes(property.getName());
        }
    }
}
