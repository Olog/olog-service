/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */
package edu.msu.nscl.olog.boundry;

import edu.msu.nscl.olog.OlogException;
import edu.msu.nscl.olog.ResourceBinder;
import edu.msu.nscl.olog.control.OlogImpl;
import edu.msu.nscl.olog.entity.Tag;
import edu.msu.nscl.olog.entity.Tags;
import edu.msu.nscl.olog.UserManager;
import edu.msu.nscl.olog.control.PerformanceInterceptor;
import java.io.IOException;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

/**
 * Top level Jersey HTTP methods for the .../tags URL
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
@Path("/tags/")
public class TagResource {
    @Context
    private UriInfo uriInfo;
    @Context
    private SecurityContext securityContext;
    @Inject
    ResourceBinder rb;
    @Inject
    OlogImpl cm;

    private Logger audit = Logger.getLogger(this.getClass().getPackage().getName() + ".audit");
    private Logger log = Logger.getLogger(this.getClass().getName());

    /** Creates a new instance of TagsResource */
    public TagResource() {
    }

    /**
     * GET method for retrieving the list of tags in the database.
     *
     * @return list of logs with their logbooks and tags that match
     */

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Interceptors(PerformanceInterceptor.class) 
    public Response list() {
        String user = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "";
        Tags result = null;
        try {
            result = cm.listTags();
            Response r = Response.ok(result).build();
            audit.info(user + "|" + uriInfo.getPath() + "|GET|OK|" + r.getStatus()
                    + "|returns " + result.getTags().size() + " tags");
            return r;
        } catch (OlogException e) {
            log.warning(user + "|" + uriInfo.getPath() + "|GET|ERROR|"
                    + e.getResponseStatusCode() +  "|cause=" + e);
            return e.toResponse();
        }
    }

    /**
     * POST method for creating multiple tags.
     *
     * @param data Tags data (from payload)
     * @return HTTP Response
     * @throws IOException when audit or log fail
     */
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Interceptors(PerformanceInterceptor.class) 
    public Response add(Tags data) throws IOException {
        UserManager um = rb.getUserManager();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        Tags result = null;
        try {
            cm.checkValidNameAndOwner(data);
            result = cm.createOrReplaceTags(data);
            Response r = Response.ok(result).build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + r.getStatus()
                    + "|data=" + Tags.toLogger(data));
            return r;
        } catch (OlogException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|POST|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + Tags.toLogger(data) + "|cause=" + e);
            return e.toResponse();
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
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Interceptors(PerformanceInterceptor.class) 
    public Response read(@PathParam("tagName") String tag) {
        String user = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "";
        Tag result = null;
        try {
            result = cm.findTagByName(tag);
            Response r;
            if (result == null) {
                r = Response.status(Response.Status.NOT_FOUND).build();
            } else {
                r = Response.ok(result).build();
            }
            audit.info(user + "|" + uriInfo.getPath() + "|GET|OK|" + r.getStatus());
            return r;
        } catch (OlogException e) {
            log.warning(user + "|" + uriInfo.getPath() + "|GET|ERROR|"
                    + e.getResponseStatusCode() +  "|cause=" + e);
            return e.toResponse();
        }
    }

    /**
     * PUT method to create and <b>exclusively</b> update the tag identified by the
     * path parameter <tt>name</tt> to all logs identified in the payload
     * structure <tt>data</tt>.
     * Setting the owner attribute in the XML root element is mandatory.
     *
     * @param tag URI path parameter: tag name
     * @param data Tag structure containing the list of logs to be tagged
     * @return HTTP Response
     */
    @PUT
    @Path("{tagName}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Interceptors(PerformanceInterceptor.class) 
    public Response create(@PathParam("tagName") String tag, Tag data) {
        UserManager um = rb.getUserManager();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        Tag result = null;
        try {
            cm.checkValidNameAndOwner(data);
            cm.checkNameMatchesPayload(tag, data);
            result = cm.createOrReplaceTag(tag, data);
            Response r = Response.ok(result).build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|OK|" + r.getStatus()
                    + "|data=" + Tag.toLogger(data));
            return r;
        } catch (OlogException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + Tag.toLogger(data) + "|cause=" + e);
            return e.toResponse();
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
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Interceptors(PerformanceInterceptor.class) 
    public Response update(@PathParam("tagName") String tag, Tag data) {
        UserManager um = rb.getUserManager();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        Tag result = null;
        try {
            result = cm.updateTag(tag, data);
            Response r = Response.ok(result).build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + r.getStatus()
                    + "|data=" + Tag.toLogger(data));
            return r;
        } catch (OlogException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|POST|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + Tag.toLogger(data) + "|cause=" + e);
            return e.toResponse();
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
    @Interceptors(PerformanceInterceptor.class) 
    public Response remove(@PathParam("tagName") String tag) {
        UserManager um = rb.getUserManager();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            cm.removeExistingTag(tag);
            Response r = Response.ok().build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|DELETE|OK|" + r.getStatus());
            return r;
        } catch (OlogException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|DELETE|ERROR|" + e.getResponseStatusCode()
                    + "|cause=" + e);
            return e.toResponse();
        }
    }

    /**
     * PUT method for adding the tag identified by <tt>tag</tt> to the single log
     * <tt>id</tt> (both path parameters).
     *
     * @param tag URI path parameter: tag name
     * @param logId URI path parameter: log to update <tt>tag</tt> to
     * @param data tag data (ignored)
     * @return HTTP Response
     */
    @PUT
    @Path("{tagName}/{logId}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Interceptors(PerformanceInterceptor.class) 
    public Response addSingle(@PathParam("tagName") String tag, @PathParam("logId")Long logId) {
        UserManager um = rb.getUserManager();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        Tag result = null;
        try {
            result = cm.addSingleTag(tag, logId);
            Response r = Response.ok(result).build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|OK|" + r.getStatus());
            return r;
        } catch (OlogException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|ERROR|" + e.getResponseStatusCode()
                    + "|cause=" + e);
            return e.toResponse();
        }
    }

    /**
     * DELETE method for deleting the tag identified by <tt>tag</tt> from the log
     * <tt>id</tt> (both path parameters).
     *
     * @param tag URI path parameter: tag name to remove
     * @param logId URI path parameter: log to remove <tt>tag</tt> from
     * @return HTTP Response
     */
    @DELETE
    @Path("{tagName}/{logId}")
    @Interceptors(PerformanceInterceptor.class) 
    public Response removeSingle(@PathParam("tagName") String tag, @PathParam("logId")Long logId) {
        UserManager um = rb.getUserManager();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            cm.removeSingleTag(tag, logId);
            Response r = Response.ok().build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|DELETE|OK|" + r.getStatus());
            return r;
        } catch (OlogException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|DELETE|ERROR|" + e.getResponseStatusCode()
                    + "|cause=" + e);
            return e.toResponse();
        }
    }
}
