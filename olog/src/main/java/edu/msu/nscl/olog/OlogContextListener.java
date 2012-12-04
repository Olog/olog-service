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
import org.apache.jackrabbit.core.RepositoryImpl;

public class OlogContextListener implements ServletContextListener {

    private static OlogContextListener instance = new OlogContextListener();
    private static ServletContext context;
    private JCRUtil repo;

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
            flyway.setDataSource(DbConnection.getInstance().getDataSource());
            if (flyway.history().isEmpty()) {
                flyway.setInitialVersion(new SchemaVersion("1.00"));
                flyway.setInitialDescription("Base version");
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
