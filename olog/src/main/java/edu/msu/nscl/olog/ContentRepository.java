/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.msu.nscl.olog;

import javax.jcr.Repository;
import javax.servlet.ServletContext;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;

public class ContentRepository extends OlogContextListener {


    /**
     * Create an instance of OlogManager
     */
    private ContentRepository() {
    }
    
    
    public static Repository getRepository() {
        //return instance.jcr;
        ServletContext ctx = OlogContextListener.getContext();
        return RepositoryAccessServlet.getRepository(ctx);
    }
    

}