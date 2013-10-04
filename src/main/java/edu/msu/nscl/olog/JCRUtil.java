/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.msu.nscl.olog;

import java.io.File;
import java.lang.Boolean;
import java.lang.String;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.ConfigurationException;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;

public class JCRUtil extends OlogContextListener {
    private static Repository repository;
    private static Session session;
    private static final String WEBINF = "WEB-INF";

    /**
     * Create an instance of JCRUtil
     */
    public JCRUtil() {
        try {
            URL url = JCRUtil.class.getResource("JCRUtil.class");
            String className = url.getFile();
            String filePath = className.substring(0,className.indexOf(WEBINF) + WEBINF.length());
            String xml;
            Boolean jcrInDb = null;
            try {
                Context initCtx = new InitialContext();
                jcrInDb = (Boolean) initCtx.lookup("java:/comp/env/JCR_IN_DB");
            } catch (NamingException e ) {
            }
            System.out.println(jcrInDb);
            if (Boolean.TRUE.equals(jcrInDb)) {
                xml = filePath + "/repository_db.xml";
            } else {
                xml = filePath + "/repository.xml";
            }
            String dir = null;
            try {
                Context initCtx = new InitialContext();
                dir = (String) initCtx.lookup("java:/comp/env/JCR_REPO_PATH");
            } catch (NamingException e ) {
            }
            if (dir==null) {
                dir = "jackrabbit";
            }
            RepositoryConfig config = RepositoryConfig.create(xml, dir);
            repository = RepositoryImpl.create(config);
            
            SimpleCredentials adminCred = new 
            SimpleCredentials("admin", new char[0]); 
            session = repository.login(adminCred);
            
        } catch (RepositoryException ex) {
            Logger.getLogger(JCRUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static Repository getRepository() {

        return repository;
    }
    
    public static Session getSession() {

        return session;
    }
    

}
