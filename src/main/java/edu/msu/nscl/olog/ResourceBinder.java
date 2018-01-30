/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author berryman
 */
@Startup
@Singleton
public class ResourceBinder {
    
    private static final Logger log = Logger.getLogger(UserManager.class.getName());
    @Resource(name="olog/userManager", type = String.class)
    private  static String userManagerJNDI;
    private static final String defaultUserManager = "edu.msu.nscl.olog.JACCUserManager";
    private  static String userManagerString;
    private  static UserManager userManager;
    
    @PostConstruct
    public void invokeResource(){
        if(userManagerJNDI == null){
            userManagerString = defaultUserManager;
            log.log(Level.CONFIG, "Using default olog/userManager: {0}", userManagerString);
        } else {
            userManagerString = userManagerJNDI;
            log.log(Level.CONFIG, "Found olog/userManager: {0}", userManagerString);
        }
         try {
            userManager = (UserManager) Class.forName(userManagerString).newInstance();
        } catch (ClassNotFoundException ex) {
            log.log(Level.SEVERE, "Could not find class {0}", userManager);
        } catch (IllegalAccessException ex) {
            log.log(Level.SEVERE, "No public constructor for class {0}", userManager);
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Public constructor failed for class " + userManager, ex);
        }
    }
    
    public UserManager getUserManager(){
        return userManager;
    }
}
