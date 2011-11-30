/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.msu.nscl.olog;

import javax.jcr.Repository;
import javax.servlet.ServletContext;
import org.apache.jackrabbit.j2ee.RepositoryAccessServlet;

public class ContentRepository extends OlogContextListener {
    private static ServletContext ctx;

    /**
     * Create an instance of ContentRepository
     */
    private ContentRepository() {
        //TODO: Should create session with login.
        
    }
    
    //TODO: Add getSession()
    public static Repository getRepository() {
        //return instance.jcr;
        ctx = OlogContextListener.getContext();
        return RepositoryAccessServlet.getRepository(ctx);
    }
    

}