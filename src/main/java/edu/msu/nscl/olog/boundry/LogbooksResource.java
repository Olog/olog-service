/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */
package edu.msu.nscl.olog.boundry;

import edu.msu.nscl.olog.entity.Logbook;
import edu.msu.nscl.olog.entity.Logbooks;
import edu.msu.nscl.olog.OlogException;
import edu.msu.nscl.olog.control.OlogImpl;
import edu.msu.nscl.olog.UserManager;
import java.io.IOException;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;

/**
 * Top level Jersey HTTP methods for the .../logbooks URL
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
@Path("/logbooks/")
@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true)
public class LogbooksResource {
    @Context
    private UriInfo uriInfo;
    @Context
    private SecurityContext securityContext;

    private Logger audit = Logger.getLogger(this.getClass().getPackage().getName() + ".audit");
    private Logger log = Logger.getLogger(this.getClass().getName());

    /** Creates a new instance of LogbooksResource */
    public LogbooksResource() {
    }

    /**
     * GET method for retrieving the list of logbooks in the database.
     *
     * @return list of logs with their logbooks and tags that match
     */

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response list() {
        OlogImpl cm = OlogImpl.getInstance();
        String user = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "";
        Logbooks result = null;
        try {
            result = cm.listLogbooks();
            Response r = Response.ok(result).build();
            log.fine(user + "|" + uriInfo.getPath() + "|GET|OK|" + r.getStatus()
                    + "|returns " + result.getLogbooks().size() + " logbooks");
            return r;
        } catch (OlogException e) {
            log.warning(user + "|" + uriInfo.getPath() + "|GET|ERROR|"
                    + e.getResponseStatusCode() +  "|cause=" + e);
            return e.toResponse();
        } 
    }

    /**
     * POST method for creating multiple logbooks.
     *
     * @param data Logbooks data (from payload)
     * @return HTTP Response
     * @throws IOException when audit or log fail
     */
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response add(Logbooks data) throws IOException {
        OlogImpl cm = OlogImpl.getInstance();
        UserManager um = UserManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        Logbooks result = null;
        try {
            cm.checkValidNameAndOwner(data);
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroup(um.getUserName(), data);
            }
            result = cm.createOrReplaceLogbooks(data);
            Response r = Response.ok(result).build();
            audit.fine(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + r.getStatus()
                    + "|data=" + Logbooks.toLogger(data));
            return r;
        } catch (OlogException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|POST|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + Logbooks.toLogger(data) + "|cause=" + e);
            return e.toResponse();
        }
    }

    /**
     * GET method for retrieving the logbook with the
     * path parameter <tt>logbookName</tt> .
     *
     * @param logbook URI path parameter: logbook name to search for
     * @return list of logs with their logbooks and tags that match
     */
    @GET
    @Path("{logbookName}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response read(@PathParam("logbookName") String logbook) {
        OlogImpl cm = OlogImpl.getInstance();
        String user = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "";
        Logbook result = null;
        try {
            result = cm.findLogbookByName(logbook);
            Response r;
            if (result == null) {
                r = Response.status(Response.Status.NOT_FOUND).build();
            } else {
                r = Response.ok(result).build();
            }
            log.fine(user + "|" + uriInfo.getPath() + "|GET|OK|" + r.getStatus());
            return r;
        } catch (OlogException e) {
            log.warning(user + "|" + uriInfo.getPath() + "|GET|ERROR|"
                    + e.getResponseStatusCode() +  "|cause=" + e);
            return e.toResponse();
        }
    }

    /**
     * PUT method for creating and <b>exclusively</b> adding the logbook identified
     * by the path parameter <tt>logbookName</tt> to all logs identified by the
     * payload structure <tt>data</tt>.
     * Setting the owner attribute in the XML root element is mandatory.
     * Values for the logbooks are taken from the payload.
     *
     * @param logbook URI path parameter: logbook name
     * @param data list of logs to add the logbook <tt>name</tt> to
     * @return HTTP Response
     */
    @PUT
    @Path("{logbookName}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response create(@PathParam("logbookName") String logbookName, Logbook data) {
        OlogImpl cm = OlogImpl.getInstance();
        UserManager um = UserManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        Logbook result = null;
        try {
            cm.checkValidNameAndOwner(data);
            cm.checkNameMatchesPayload(logbookName, data);
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroup(um.getUserName(), data);
            }
            result = cm.createOrReplaceLogbook(logbookName, data);
            Response r = Response.ok(result).build();
            audit.fine(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|OK|" + r.getStatus()
                    + "|data=" + Logbook.toLogger(data));
            return r;
        } catch (OlogException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + Logbook.toLogger(data) + "|cause=" + e);
            return e.toResponse();
        }
    }

    /**
     * POST method for updating the logbook identified by the path parameter <tt>name</tt>,
     * adding it to all logs identified by the payload structure <tt>data</tt>.
     * Setting the owner attribute in the XML root element is mandatory.
     * Values for the logbooks are taken from the payload.
     *
     * @param logbook URI path parameter: logbook name
     * @param data list of logs to add the logbook <tt>name</tt> to
     * @return HTTP Response
     */
    @POST
    @Path("{logbookName}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response update(@PathParam("logbookName") String logbook, Logbook data) {
        OlogImpl cm = OlogImpl.getInstance();
        UserManager um = UserManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        Logbook result = null;
        try {
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroupOfLogbook(um.getUserName(), logbook);
                cm.checkUserBelongsToGroup(um.getUserName(), data);
            }
            result = cm.createOrReplaceLogbook(logbook, data);
            Response r = Response.ok(result).build();
            audit.fine(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + r.getStatus()
                    + "|data=" + Logbook.toLogger(data));
            return r;
        } catch (OlogException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|POST|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + Logbook.toLogger(data) + "|cause=" + e);
            return e.toResponse();
        }
    }

    /**
     * DELETE method for deleting the logbook identified by the path parameter
     * <tt>name</tt> from all logs.
     *
     * @param logbook URI path parameter: logbook name to remove
     * @return HTTP Response
     */
    @DELETE
    @Path("{logbookName}")
    public Response remove(@PathParam("logbookName") String logbook) {
        OlogImpl cm = OlogImpl.getInstance();
        UserManager um = UserManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroup(um.getUserName(), cm.findLogbookByName(logbook));
            }
            cm.removeExistingLogbook(logbook);
            Response r = Response.ok().build();
            audit.fine(um.getUserName() + "|" + uriInfo.getPath() + "|DELETE|OK|" + r.getStatus());
            return r;
        } catch (OlogException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|DELETE|ERROR|" + e.getResponseStatusCode()
                    + "|cause=" + e);
            return e.toResponse();
        }
    }

    /**
     * PUT method for adding the logbook identified by <tt>logbook</tt> to the log
     * <tt>id</tt> (both path parameters).
     *
     * @param logbook URI path parameter: logbook name
     * @param logId URI path parameter: log to addSingle <tt>logbook</tt> to
     * @param data tag data (specifying tag ownership)
     * @return HTTP Response
     */
    @PUT
    @Path("{logbookName}/{logId}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response addSingle(@PathParam("logbookName") String logbook, @PathParam("logId") Long logId) {
        OlogImpl cm = OlogImpl.getInstance();
        UserManager um = UserManager.getInstance();
        Logbook data = null;
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        Logbook result = null;
        try {
            data = LogbookManager.findLogbook(logbook);
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroup(um.getUserName(), data);
            }
            result = cm.addSingleLogbook(logbook, logId);
            Response r = Response.ok(result).build();
            audit.fine(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|OK|" + r.getStatus()
                    + "|data=" + Logbook.toLogger(data));
            return r;
        } catch (OlogException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + Logbook.toLogger(data) + "|cause=" + e);
            return e.toResponse();
        }
    }

    /**
     * DELETE method for deleting the logbook identified by <tt>logbook</tt> from the log
     * <tt>id</tt> (both path parameters).
     *
     * @param logbook URI path parameter: logbook name to remove
     * @param logId URI path parameter: log to remove <tt>logbook</tt> from
     * @return HTTP Response
     */
    @DELETE
    @Path("{logbookName}/{logId}")
    public Response removeSingle(@PathParam("logbookName") String logbook, @PathParam("logId") Long logId) {
        OlogImpl cm = OlogImpl.getInstance();
        UserManager um = UserManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroup(um.getUserName(), cm.findLogbookByName(logbook));
            }
            cm.removeSingleLogbook(logbook, logId);
            Response r = Response.ok().build();
            audit.fine(um.getUserName() + "|" + uriInfo.getPath() + "|DELETE|OK|" + r.getStatus());
            return r;
        } catch (OlogException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|DELETE|ERROR|" + e.getResponseStatusCode()
                    + "|cause=" + e);
            return e.toResponse();
        }
    }
}
