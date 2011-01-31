/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */

package edu.msu.nscl.olog;

import java.io.IOException;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;

/**
 * Top level Jersey HTTP methods for the .../logs URL
 * 
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */

@Path("/logs/")
public class LogsResource {
    @Context
    private UriInfo uriInfo;
    @Context
    private SecurityContext securityContext;

    private Logger audit = Logger.getLogger(this.getClass().getPackage().getName() + ".audit");
    private Logger log = Logger.getLogger(this.getClass().getName());
  
    /** Creates a new instance of LogsResource */
    public LogsResource() {
    }

    /**
     * GET method for retrieving a collection of Log instances,
     * based on a multi-parameter query specifying patterns for tags, logbooks,
     * and logs subjects, details to match against.
     *
     * @return HTTP Response
     */
    @GET
    @Produces({"application/xml", "application/json"})
    public Response query() {
        DbConnection db = DbConnection.getInstance();
        OLogManager cm = OLogManager.getInstance();
        String user = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "";
        try {
            db.getConnection();
            db.beginTransaction();
            XmlLogs result = cm.findLogsByMultiMatch(uriInfo.getQueryParameters());
            db.commit();
            Response r = Response.ok(result).build();
            log.fine(user + "|" + uriInfo.getPath() + "|GET|OK|" + r.getStatus()
                    + "|returns " + result.getLogs().size() + " logs");
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
     * POST method for creating multiple log instances.
     *
     * @param data XmlLogs data (from payload)
     * @return HTTP Response
     * @throws IOException when audit or log fail
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    public Response add(XmlLogs data) throws IOException {
        DbConnection db = DbConnection.getInstance();
        OLogManager cm = OLogManager.getInstance();
        UserManager um = UserManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            cm.checkValidNameAndOwner(data);
            db.getConnection();
            db.beginTransaction();
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroup(um.getUserName(), data);
            }
            cm.createOrReplaceChannels(data);
            db.commit();
            Response r = Response.noContent().build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + r.getStatus()
                    + "|data=" + XmlLogs.toLog(data));
            return r;
        } catch (CFException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|POST|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + XmlLogs.toLog(data) + "|cause=" + e);
            return e.toResponse();
        } finally {
            db.releaseConnection();
        }
    }

    /**
     * GET method for retrieving an instance of Log identified by <tt>id</tt>.
     *
     * @param id log id
     * @return HTTP Response
     */
    @GET
    @Path("{logId}")
    @Produces({"application/xml", "application/json"})
    public Response read(@PathParam("logId") String id) {
        DbConnection db = DbConnection.getInstance();
        OLogManager cm = OLogManager.getInstance();
        String user = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "";
        XmlLog result = null;
        try {
            db.getConnection();
            db.beginTransaction();
            result = cm.findLogById(id);
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
     * PUT method for editing a log instance identified by the payload.
     * The <b>complete</b> set of logbooks/tags for the log must be supplied,
     * which will replace the existing set of logbooks/tags.
     *
     * @param id id of log to edit
     * @param data new data (logbooks/tags) for log <tt>id</tt>
     * @return HTTP response
     */
    @PUT
    @Path("{logId}")
    @Consumes({"application/xml", "application/json"})
    public Response create(@PathParam("logId") String id, XmlLog data) {
        DbConnection db = DbConnection.getInstance();
        OLogManager cm = OLogManager.getInstance();
        UserManager um = UserManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            cm.checkValidNameAndOwner(data);
            cm.checkNameMatchesPayload(id, data);
            db.getConnection();
            db.beginTransaction();
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroup(um.getUserName(), data);
            }
            cm.createOrReplaceChannel(id, data);
            db.commit();
            Response r = Response.noContent().build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|OK|" + r.getStatus()
                    + "|data=" + XmlLog.toLog(data));
            return r;
        } catch (CFException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + XmlLog.toLog(data) + "|cause=" + e);
            return e.toResponse();
        } finally {
            db.releaseConnection();
        }
    }

    /**
     * POST method for merging logbooks and tags of the Log identified by the
     * payload into an existing log.
     *
     * @param id id of channel to add
     * @param data new XmlLog data (logbooks/tags) to be merged into log <tt>id</tt>
     * @return HTTP response
     */
    @POST
    @Path("{logId}")
    @Consumes({"application/xml", "application/json"})
    public Response update(@PathParam("logId") String id, XmlLog data) {
        DbConnection db = DbConnection.getInstance();
        OLogManager cm = OLogManager.getInstance();
        UserManager um = UserManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            cm.checkValidNameAndOwner(data);
            db.getConnection();
            db.beginTransaction();
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroupOfChannel(um.getUserName(), id);
                cm.checkUserBelongsToGroup(um.getUserName(), data);
            }
            cm.updateChannel(id, data);
            db.commit();
            Response r = Response.noContent().build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + r.getStatus()
                    + "|data=" + XmlLog.toLog(data));
            return r;
        } catch (CFException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|POST|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + XmlLog.toLog(data) + "|cause=" + e);
            return e.toResponse();
        } finally {
            db.releaseConnection();
        }
    }

    /**
     * DELETE method for deleting a log instance identified by
     * path parameter <tt>id</tt>.
     *
     * @param id log to remove
     * @return HTTP Response
     */
    @DELETE
    @Path("{logId}")
    public Response remove(@PathParam("logId") String id) {
        DbConnection db = DbConnection.getInstance();
        UserManager um = UserManager.getInstance();
        OLogManager cm = OLogManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            db.getConnection();
            db.beginTransaction();
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroup(um.getUserName(), cm.findChannelByName(id));
            }
            cm.removeExistingChannel(id);
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
