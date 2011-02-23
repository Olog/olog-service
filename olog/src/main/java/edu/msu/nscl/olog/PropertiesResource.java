/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010-2011 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * All rights reserved. Use is subject to license terms and conditions.
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
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@helmholtz-berlin.de>
 */
@Path("/properties/")
public class PropertiesResource {
    @Context
    private UriInfo uriInfo;
    @Context
    private SecurityContext securityContext;

    private Logger audit = Logger.getLogger(this.getClass().getPackage().getName() + ".audit");
    private Logger log = Logger.getLogger(this.getClass().getName());

    /** Creates a new instance of PropertiesResource */
    public PropertiesResource() {
    }

    /**
     * GET method for retrieving the properties with the
     * path parameter <tt>logId</tt> for a log
     *
     * @param prop URI path parameter: property name to search for
     * @return list of channels with their properties and tags that match
     */
    @GET
    @Path("{logId}")
    @Produces({"application/xml", "application/json"})
    public Response read(@PathParam("logId") Long logId) {
        DbConnection db = DbConnection.getInstance();
        OLogManager cm = OLogManager.getInstance();
        String user = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "";
        XmlProperty result = null;
        try {
            db.getConnection();
            db.beginTransaction();
            result = cm.findPropertyByLogId(logId);
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
     * PUT method for creating and <b>exclusively</b> adding the property identified
     * by the path parameter <tt>logId</tt> to log
     *
     * Setting the owner attribute in the XML root element is mandatory.
     * Values for the properties are taken from the payload.
     *
     * @param prop URI path parameter: property name
     * @param data list of channels to add the property <tt>name</tt> to
     * @return HTTP Response
     */
    @PUT
    @Path("{logId}/{propName}")
    @Consumes({"application/xml", "application/json"})
    public Response create(@PathParam("logId") Long logId, @PathParam("propName") String prop, XmlProperty data) {
        DbConnection db = DbConnection.getInstance();
        OLogManager cm = OLogManager.getInstance();
        UserManager um = UserManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            cm.checkValidNameAndOwner(data);
            cm.checkNameMatchesPayload(prop, data);
            db.getConnection();
            db.beginTransaction();
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroup(um.getUserName(), data);
            }
            cm.createOrReplaceProperty(logId, data);
            db.commit();
            Response r = Response.noContent().build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|OK|" + r.getStatus()
                    + "|data=" + XmlProperty.toLog(data));
            return r;
        } catch (CFException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + XmlProperty.toLog(data) + "|cause=" + e);
            return e.toResponse();
        } finally {
            db.releaseConnection();
        }
    }
    /**
     * PUT method for creating and <b>exclusively</b> adding the property identified
     * by the path parameter <tt>logId</tt> to log
     *
     * Setting the owner attribute in the XML root element is mandatory.
     * Values for the properties are taken from the payload.
     *
     * @param prop URI path parameter: property name
     * @param data list of channels to add the property <tt>name</tt> to
     * @return HTTP Response
     */
    @PUT
    @Path("{logId}")
    @Consumes({"application/xml", "application/json"})
    public Response create(@PathParam("logId") Long logId, XmlProperty data) {
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
            cm.createOrReplaceProperty(logId, data);
            db.commit();
            Response r = Response.noContent().build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|OK|" + r.getStatus()
                    + "|data=" + XmlProperty.toLog(data));
            return r;
        } catch (CFException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + XmlProperty.toLog(data) + "|cause=" + e);
            return e.toResponse();
        } finally {
            db.releaseConnection();
        }
    }

    /**
     * POST method for updating the property identified by the path parameter <tt>name</tt>,
     * adding it to all channels identified by the payload structure <tt>data</tt>.
     * Setting the owner attribute in the XML root element is mandatory.
     * Values for the properties are taken from the payload.
     *
     * @param prop URI path parameter: property name
     * @param data list of channels to add the property <tt>name</tt> to
     * @return HTTP Response
     */
    @POST
    @Path("{logId}")
    @Consumes({"application/xml", "application/json"})
    public Response update(@PathParam("logId") Long logId, XmlProperty data) {
        DbConnection db = DbConnection.getInstance();
        OLogManager cm = OLogManager.getInstance();
        UserManager um = UserManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            db.getConnection();
            db.beginTransaction();
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroupOfLogId(um.getUserName(), logId);
                cm.checkUserBelongsToGroup(um.getUserName(), data);
            }
            cm.updateProperty(logId, data);
            db.commit();
            Response r = Response.noContent().build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + r.getStatus()
                    + "|data=" + XmlProperty.toLog(data));
            return r;
        } catch (CFException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|POST|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + XmlProperty.toLog(data) + "|cause=" + e);
            return e.toResponse();
        } finally {
            db.releaseConnection();
        }
    }

    /**
     * DELETE method for deleting the properties identified by the path parameter
     * <tt>logId</tt> from the log.
     *
     * @param prop URI path parameter: tag name to remove
     * @return HTTP Response
     */
    @DELETE
    @Path("{logId}")
    public Response remove(@PathParam("logId") Long logId) {
        DbConnection db = DbConnection.getInstance();
        OLogManager cm = OLogManager.getInstance();
        UserManager um = UserManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            db.getConnection();
            db.beginTransaction();
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroup(um.getUserName(), cm.findPropertyByName(prop));
            }
            cm.removeExistingProperties(logId);
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
     * DELETE method for deleting the property identified by the path parameter
     * <tt>name</tt> from all logs.
     *
     * @param prop URI path parameter: tag name to remove
     * @return HTTP Response
     */
    @DELETE
    @Path("{logId}/{propName}")
    public Response remove(@PathParam("propName") String prop, @PathParam("logId") Long logId) {
        DbConnection db = DbConnection.getInstance();
        OLogManager cm = OLogManager.getInstance();
        UserManager um = UserManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            db.getConnection();
            db.beginTransaction();
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroup(um.getUserName(), cm.findPropertyByName(prop));
            }
            cm.removeExistingProperty(logId,prop);
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
