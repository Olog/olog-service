/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.msu.nscl.olog;

/**
 *
 * @author berryman
 */

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.*;
import javax.jcr.Repository;
import org.apache.jackrabbit.servlet.ServletRepository;

public  class OlogContextListener implements ServletContextListener {
  private ServletContext context = null;
  private ContentRepository jcr;

  /*This method is invoked when the Web Application has been removed
  and is no longer able to accept requests
  */

  public void contextDestroyed(ServletContextEvent event) {
    //Output a simple message to the server's console
    System.out.println("Olog JCR Sessions have been removed");
    this.context = null;
   // if(this.jcr.getSession()!=null)
   //     this.jcr.cleanJcrSessions();
    this.jcr = null;
  }


  //This method is invoked when the Web Application
  //is ready to service requests

  public void contextInitialized(ServletContextEvent event) {
      this.context = event.getServletContext();
      //this.jcr = ContentRepository.getInstance();



    //Output a simple message to the server's console
    System.out.println("Olog JCR has been initialized");

  }

}
