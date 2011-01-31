/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */
package edu.msu.nscl.olog;

import java.io.IOException;
import java.util.logging.Logger;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

/**
 * Top level Jersey HTTP methods for the .../tags URL
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
@Path("/tags/")
public class TagsResource {
    @Context
    private UriInfo uriInfo;
    @Context
    private SecurityContext securityContext;

    private Logger audit = Logger.getLogger(this.getClass().getPackage().getName() + ".audit");
    private Logger log = Logger.getLogger(this.getClass().getName());

    /** Creates a new instance of TagsResource */
    public TagsResource() {
    }

    /**
     * GET method for retrieving the list of tags in the database.
     *
     * @param name URI path parameter: tag name to search for
     * @return list of logs with their logbooks and tags that match
     */

    @GET
    @Produces({"application/xml", "application/json"})
    public Response list() {
        DbConnection db = DbConnection.getInstance();
        OLogManager cm = OLogManager.getInstance();
        String user = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "";
        XmlTags result = null;
        try {
            db.getConnection();
            db.beginTransaction();
            result = cm.listTags();
            db.commit();
            Response r = Response.ok(result).build();
            log.fine(user + "|" + uriInfo.getPath() + "|GET|OK|" + r.getStatus()
                    + "|returns " + result.getTags().size() + " tags");
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
     * POST method for creating multiple tags.
     *
     * @param data XmlTags data (from payload)
     * @return HTTP Response
     * @throws IOException when audit or log fail
     */
    @POST
    @Consumes({"application/xml", "application/json"})
    public Response add(XmlTags data) throws IOException {
        DbConnection db = DbConnection.getInstance();
        OLogManager cm = OLogManager.getInstance();
        UserManager um = UserManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            cm.checkValidNameAndOwner(data);
            db.getConnection();
            db.beginTransaction();
            cm.createOrReplaceTags(data);
            db.commit();
            Response r = Response.noContent().build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + r.getStatus()
                    + "|data=" + XmlTags.toLog(data));
            return r;
        } catch (CFException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|POST|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + XmlTags.toLog(data) + "|cause=" + e);
            return e.toResponse();
        } finally {
            db.releaseConnection();
        }
    }

    /**
     * GET method for retrieving the tag with the
     * path parameter <tt>tagName</tt> and its logs.
     *
     * @param tag URI path parameter: tag name to search for
     * @return list of logs with their logbooks and tags that match
     */
    @GET
    @Path("{tagName}")
    @Produces({"application/xml", "application/json"})
    public Response read(@PathParam("tagName") String tag) {
        DbConnection db = DbConnection.getInstance();
        OLogManager cm = OLogManager.getInstance();
        String user = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "";
        XmlTag result = null;
        try {
            db.getConnection();
            db.beginTransaction();
            result = cm.findTagByName(tag);
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
     * PUT method to create and <b>exclusively</b> update the tag identified by the
     * path parameter <tt>name</tt> to all logs identified in the payload
     * structure <tt>data</tt>.
     * Setting the owner attribute in the XML root element is mandatory.
     *
     * @param tag URI path parameter: tag name
     * @param data XmlTag structure containing the list of channels to be tagged
     * @return HTTP Response
     */
    @PUT
    @Path("{tagName}")
    @Consumes({"application/xml", "application/json"})
    public Response create(@PathParam("tagName") String tag, XmlTag data) {
        DbConnection db = DbConnection.getInstance();
        OLogManager cm = OLogManager.getInstance();
        UserManager um = UserManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            cm.checkValidNameAndOwner(data);
            cm.checkNameMatchesPayload(tag, data);
            db.getConnection();
            db.beginTransaction();
            cm.createOrReplaceTag(tag, data);
            db.commit();
            Response r = Response.noContent().build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|OK|" + r.getStatus()
                    + "|data=" + XmlTag.toLog(data));
            return r;
        } catch (CFException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + XmlTag.toLog(data) + "|cause=" + e);
            return e.toResponse();
        } finally {
            db.releaseConnection();
        }
    }

    /**
     * POST method to update the the tag identified by the path parameter <tt>name</tt>,
     * adding it to all logs identified by the logs inside the payload
     * structure <tt>data</tt>.
     * Setting the owner attribute in the XML root element is mandatory.
     *
     * @param tag URI path parameter: tag name
     * @param data list of logs to addSingle the tag <tt>name</tt> to
     * @return HTTP Response
     */
    @POST
    @Path("{tagName}")
    @Consumes({"application/xml", "application/json"})
    public Response update(@PathParam("tagName") String tag, XmlTag data) {
        DbConnection db = DbConnection.getInstance();
        OLogManager cm = OLogManager.getInstance();
        UserManager um = UserManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            db.getConnection();
            db.beginTransaction();
            cm.updateTag(tag, data);
            db.commit();
            Response r = Response.noContent().build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + r.getStatus()
                    + "|data=" + XmlTag.toLog(data));
            return r;
        } catch (CFException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|POST|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + XmlTag.toLog(data) + "|cause=" + e);
            return e.toResponse();
        } finally {
            db.releaseConnection();
        }
    }

    /**
     * DELETE method for deleting the tag identified by the path parameter <tt>name</tt>
     * from all logs.
     *
     * @param tag URI path parameter: tag name to remove
     * @return HTTP Response
     */
    @DELETE
    @Path("{tagName}")
    public Response remove(@PathParam("tagName") String tag) {
        DbConnection db = DbConnection.getInstance();
        OLogManager cm = OLogManager.getInstance();
        UserManager um = UserManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            db.getConnection();
            db.beginTransaction();
            cm.removeExistingProperty(tag);
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
     * PUT method for adding the tag identified by <tt>tag</tt> to the single log
     * <tt>id</tt> (both path parameters).
     *
     * @param tag URI path parameter: tag name
     * @param id URI path parameter: log to update <tt>tag</tt> to
     * @param data tag data (ignored)
     * @return HTTP Response
     */
    @PUT
    @Path("{tagName}/{logId}")
    @Consumes({"application/xml", "application/json"})
    public Response addSingle(@PathParam("tagName") String tag, @PathParam("logId") String id, XmlTag data) {
        DbConnection db = DbConnection.getInstance();
        OLogManager cm = OLogManager.getInstance();
        UserManager um = UserManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            cm.checkNameMatchesPayload(tag, data);
            db.getConnection();
            db.beginTransaction();
            cm.addSingleTag(tag, id);
            db.commit();
            Response r = Response.noContent().build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|OK|" + r.getStatus()
                    + "|data=" + XmlTag.toLog(data));
            return r;
        } catch (CFException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + XmlTag.toLog(data) + "|cause=" + e);
            return e.toResponse();
        } finally {
            db.releaseConnection();
        }
    }

    /**
     * DELETE method for deleting the tag identified by <tt>tag</tt> from the log
     * <tt>id</tt> (both path parameters).
     *
     * @param tag URI path parameter: tag name to remove
     * @param id URI path parameter: log to remove <tt>tag</tt> from
     * @return HTTP Response
     */
    @DELETE
    @Path("{tagName}/{logId}")
    public Response removeSingle(@PathParam("tagName") String tag, @PathParam("logId") String id) {
        DbConnection db = DbConnection.getInstance();
        OLogManager cm = OLogManager.getInstance();
        UserManager um = UserManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            db.getConnection();
            db.beginTransaction();
            cm.removeSingleTag(tag, id);
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
