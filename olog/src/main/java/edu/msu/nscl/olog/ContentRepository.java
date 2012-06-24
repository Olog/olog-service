/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.msu.nscl.olog;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.servlet.ServletContext;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.ConfigurationException;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;

public class ContentRepository extends OlogContextListener {
    private static Repository repository;
    
    private static Session session;

    /**
     * Create an instance of ContentRepository
     */
    public ContentRepository() {
        try {
            String xml = "repository/repository.xml";
            String dir = "repository";
            RepositoryConfig config = RepositoryConfig.create(xml, dir);
            repository = RepositoryImpl.create(config);
            
            SimpleCredentials adminCred = new 
            SimpleCredentials("admin", new char[0]); 
            session = repository.login(adminCred);
            
        } catch (RepositoryException ex) {
            Logger.getLogger(ContentRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static Repository getRepository() {

        return repository;
    }
    
    public static Session getSession() {

        return session;
    }
    

}