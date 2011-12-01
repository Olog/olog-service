/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.msu.nscl.olog;

/**
 *
 * @author berryman
 */

import javax.servlet.*;

public  class OlogContextListener implements ServletContextListener {
  
  private static OlogContextListener instance = new OlogContextListener();
  private static ServletContext context;
 

  public static OlogContextListener getInstance() {
      return instance;
  }
  
  public static ServletContext getContext() {
      return context;
  }
  /*This method is invoked when the Web Application has been removed
  and is no longer able to accept requests
  */

  @Override
  public void contextDestroyed(ServletContextEvent event) {
    System.out.println("Olog JCR Sessions have been removed");

  }


  //This method is invoked when the Web Application
  //is ready to service requests

  @Override
  public void contextInitialized(ServletContextEvent event) {
     context = event.getServletContext();
     if (context==null) {
        	System.out.println("Couldn't get servlet context.");
     } else {
        	System.out.println("Servlet context fetched from ServiceContext.");
     }
     System.out.println("Olog JCR has been initialized: ");

  }
  
}
