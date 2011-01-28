/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */
package edu.msu.nscl.olog;

import java.io.IOException;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

/**
 * Top level Jersey HTTP methods for the .../properties URL
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
@Path("/logbooks/")
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
     * @param name URI path parameter: tag name to search for
     * @return list of logs with their logbooks and tags that match
     */

    @GET
    @Produces({"application/xml", "application/json"})
    public Response list() {
        DbConnection db = DbConnection.getInstance();
        LogManager cm = LogManager.getInstance();
        String user = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "";
        XmlLogbooks result = null;
        try {
            db.getConnection();
            db.beginTransaction();
            result = cm.listLogbooks();
            db.commit();
            Response r = Response.ok(result).build();
            log.fine(user + "|" + uriInfo.getPath() + "|GET|OK|" + r.getStatus()
                    + "|returns " + result.getLogbooks().size() + " properties");
            return r;
        } catch (CFException e) {
            log.warning(user + "|" + uriInfo.getPath() + "|GET|ERROR|"
                    + e.getResponseStatusCode() +  "|cause=" + e);
            return e.toResponse();
        } finally {
            db.releaseConnection();
        }
    }

    /**
     * POST method for creating multiple logbooks.
     *
     * @param data XmlLogbooks data (from payload)
     * @return HTTP Response
     * @throws IOException when audit or log fail
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    public Response add(XmlLogbooks data) throws IOException {
        DbConnection db = DbConnection.getInstance();
        LogManager cm = LogManager.getInstance();
        UserManager um = UserManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            cm.checkValidNameAndOwner(data);
            db.getConnection();
            db.beginTransaction();
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroup(um.getUserName(), data);
            }
            cm.createOrReplaceLogbooks(data);
            db.commit();
            Response r = Response.noContent().build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + r.getStatus()
                    + "|data=" + XmlLogbooks.toLog(data));
            return r;
        } catch (CFException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|POST|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + XmlLogbooks.toLog(data) + "|cause=" + e);
            return e.toResponse();
        } finally {
            db.releaseConnection();
        }
    }

    /**
     * GET method for retrieving the logbook with the
     * path parameter <tt>logbookName</tt> and its logs.
     *
     * @param prop URI path parameter: logbook name to search for
     * @return list of logs with their logbooks and tags that match
     */
    @GET
    @Path("{logbookName}")
    @Produces({"application/xml", "application/json"})
    public Response read(@PathParam("logbookName") String logbook) {
        DbConnection db = DbConnection.getInstance();
        LogManager cm = LogManager.getInstance();
        String user = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "";
        XmlLogbook result = null;
        try {
            db.getConnection();
            db.beginTransaction();
            result = cm.findLogbookByName(logbook);
            db.commit();
            Response r;
            if (result == null) {
                r = Response.status(Response.Status.NOT_FOUND).build();
            } else {
                r = Response.ok(result).build();
            }
            log.fine(user + "|" + uriInfo.getPath() + "|GET|OK|" + r.getStatus());
            return r;
        } catch (CFException e) {
            log.warning(user + "|" + uriInfo.getPath() + "|GET|ERROR|"
                    + e.getResponseStatusCode() +  "|cause=" + e);
            return e.toResponse();
        } finally {
            db.releaseConnection();
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
    @Consumes({"application/xml", "application/json"})
    public Response create(@PathParam("logbookName") String logbook, XmlLogbook data) {
        DbConnection db = DbConnection.getInstance();
        LogManager cm = LogManager.getInstance();
        UserManager um = UserManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            cm.checkValidNameAndOwner(data);
            cm.checkNameMatchesPayload(logbook, data);
            db.getConnection();
            db.beginTransaction();
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroup(um.getUserName(), data);
            }
            cm.createOrReplaceLogbook(logbook, data);
            db.commit();
            Response r = Response.noContent().build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|OK|" + r.getStatus()
                    + "|data=" + XmlLogbook.toLog(data));
            return r;
        } catch (CFException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + XmlLogbook.toLog(data) + "|cause=" + e);
            return e.toResponse();
        } finally {
            db.releaseConnection();
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
    @Consumes({"application/xml", "application/json"})
    public Response update(@PathParam("logbookName") String logbook, XmlLogbook data) {
        DbConnection db = DbConnection.getInstance();
        LogManager cm = LogManager.getInstance();
        UserManager um = UserManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            db.getConnection();
            db.beginTransaction();
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroupOfLogbook(um.getUserName(), logbook);
                cm.checkUserBelongsToGroup(um.getUserName(), data);
            }
            cm.updateLogbook(logbook, data);
            db.commit();
            Response r = Response.noContent().build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + r.getStatus()
                    + "|data=" + XmlLogbook.toLog(data));
            return r;
        } catch (CFException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|POST|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + XmlLogbook.toLog(data) + "|cause=" + e);
            return e.toResponse();
        } finally {
            db.releaseConnection();
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
        DbConnection db = DbConnection.getInstance();
        LogManager cm = LogManager.getInstance();
        UserManager um = UserManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            db.getConnection();
            db.beginTransaction();
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroup(um.getUserName(), cm.findPropertyByName(prop));
            }
            cm.removeExistingLogbook(logbook);
            db.commit();
            Response r = Response.ok().build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|DELETE|OK|" + r.getStatus());
            return r;
        } catch (CFException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|DELETE|ERROR|" + e.getResponseStatusCode()
                    + "|cause=" + e);
            return e.toResponse();
        } finally {
            db.releaseConnection();
        }
    }

    /**
     * PUT method for adding the logbook identified by <tt>logbook</tt> to the log
     * <tt>id</tt> (both path parameters).
     *
     * @param logbook URI path parameter: logbook name
     * @param id URI path parameter: log to addSingle <tt>logbook</tt> to
     * @param data tag data (specifying tag ownership)
     * @return HTTP Response
     */
    @PUT
    @Path("{tagName}/{logId}")
    @Consumes({"application/xml", "application/json"})
    public Response addSingle(@PathParam("tagName") String logbook, @PathParam("logId") String id, XmlProperty data) {
        DbConnection db = DbConnection.getInstance();
        LogManager cm = LogManager.getInstance();
        UserManager um = UserManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            cm.checkNameMatchesPayload(logbook, data);
            db.getConnection();
            db.beginTransaction();
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroup(um.getUserName(), data);
            }
            cm.addSingleLogbook(logbook, id, data);
            db.commit();
            Response r = Response.noContent().build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|OK|" + r.getStatus()
                    + "|data=" + XmlLogbook.toLog(data));
            return r;
        } catch (CFException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + XmlLogbook.toLog(data) + "|cause=" + e);
            return e.toResponse();
        } finally {
            db.releaseConnection();
        }
    }

    /**
     * DELETE method for deleting the logbook identified by <tt>logbook</tt> from the log
     * <tt>id</tt> (both path parameters).
     *
     * @param logbook URI path parameter: logbook name to remove
     * @param id URI path parameter: log to remove <tt>logbook</tt> from
     * @return HTTP Response
     */
    @DELETE
    @Path("{logbookName}/{logId}")
    public Response removeSingle(@PathParam("logbookName") String logbook, @PathParam("logId") String id) {
        DbConnection db = DbConnection.getInstance();
        LogManager cm = LogManager.getInstance();
        UserManager um = UserManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            db.getConnection();
            db.beginTransaction();
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroup(um.getUserName(), cm.findLogbookByName(logbook));
            }
            cm.removeSingleLogbook(logbook, id);
            db.commit();
            Response r = Response.ok().build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|DELETE|OK|" + r.getStatus());
            return r;
        } catch (CFException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|DELETE|ERROR|" + e.getResponseStatusCode()
                    + "|cause=" + e);
            return e.toResponse();
        } finally {
            db.releaseConnection();
        }
    }
}
