/*
 * Copyright (c) 2011 Michigan State University - Facility for Rare Isotope Beams
 */
package edu.msu.nscl.olog;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

/**
 * Top level Jersey HTTP methods for the .../properties URL
 *
 * @author Robert Gaul III
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
     * GET method for retrieving the list of properties in the database.
     *
     * @return
     */
    @GET
    @Produces({"application/xml", "application/json"})
    public Response listProperties() {
        OlogImpl cm = OlogImpl.getInstance();
        String user = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "";
        XmlProperties result = null;
        try {
            result = cm.listProperties();
            Response r = Response.ok(result).build();
            audit.info(user + "|" + uriInfo.getPath() + "|GET|OK|" + r.getStatus()
                    + "|returns " + result.getProperties().size() + " properties");
            return r;
        } catch (CFException e) {
            log.warning(user + "|" + uriInfo.getPath() + "|GET|ERROR|"
                    + e.getResponseStatusCode() + "|cause=" + e);
            return e.toResponse();
        }
    }

    /**
     * PUT method for adding a new property. Is destructive in nature.
     *
     * @param newProperty the property being added
     * @param data the XML payload containing attributes to be added to the property
     * @return
     */
    @PUT
    @Path("{propName}")
    @Produces({"application/xml", "application/json"})
    public Response addProperty(@PathParam("propName") String newProperty, XmlProperty data) {
        OlogImpl cm = OlogImpl.getInstance();
        String user = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "";
        XmlProperty result = null;
        try {
            cm.checkPropertyName(newProperty, data);
            result = cm.addProperty(data, true);
            Response r = Response.ok(result).build();
            audit.info(user + "|" + uriInfo.getPath() + "|PUT|OK|" + r.getStatus());
            return r;
        } catch (CFException e) {
            log.warning(user + "|" + uriInfo.getPath() + "|PUT|ERROR|"
                    + e.getResponseStatusCode() + "|cause=" + e);
            return e.toResponse();
        }
    }

    /**
     * GET method for retrieving the list of attributes for a given property.
     *
     * @return
     */
    @GET
    @Path("{propName}")
    @Produces({"application/xml", "application/json"})
    public Response listAttributes(@PathParam("propName") String property) {
        OlogImpl cm = OlogImpl.getInstance();
        String user = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "";
        XmlProperty result = null;
        try {
            result = cm.listAttributes(property);
            Response r = Response.ok(result).build();
            audit.info(user + "|" + uriInfo.getPath() + "|GET|OK|" + r.getStatus());
            return r;
        } catch (CFException e) {
            log.warning(user + "|" + uriInfo.getPath() + "|GET|ERROR|"
                    + e.getResponseStatusCode() + "|cause=" + e);
            return e.toResponse();
        }
    }

    /**
     * POST method for adding a new property. Is not destructive as it appends attributes to those already there.
     *
     * @param newProperty the property being added
     * @param data the XML payload containing attributes to be added to the property
     * @return
     */
    @POST
    @Path("{propName}")
    @Produces({"application/xml", "application/json"})
    public Response appendProperty(@PathParam("propName") String newProperty, XmlProperty data) {
        OlogImpl cm = OlogImpl.getInstance();
        String user = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "";
        XmlProperty result = null;
        try {
            cm.checkPropertyName(newProperty, data);
            result = cm.addProperty(data, false);
            Response r = Response.ok(result).build();
            audit.info(user + "|" + uriInfo.getPath() + "|POST|OK|" + r.getStatus());
            return r;
        } catch (CFException e) {
            log.warning(user + "|" + uriInfo.getPath() + "|POST|ERROR|"
                    + e.getResponseStatusCode() + "|cause=" + e);
            return e.toResponse();
        }
    }

    /**
     * DELETE method for removing a property.
     *
     * @param String property property to be removed or that will contain attributes to be removed
     * @param Property data payload containing attributes to be removed
     * @return
     */
    @DELETE
    @Path("{propName}")
    @Produces({"application/xml", "application/json"})
    public Response removeProperty(@PathParam("propName") String property, XmlProperty data) {
        OlogImpl cm = OlogImpl.getInstance();
        String user = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "";
        try {
            cm.removeProperty(property);
            Response r = Response.ok().build();
            audit.info(user + "|" + uriInfo.getPath() + "|DELETE|OK|" + r.getStatus());
            return r;
        } catch (CFException e) {
            log.warning(user + "|" + uriInfo.getPath() + "|DELETE|ERROR|"
                    + e.getResponseStatusCode() + "|cause=" + e);
            return e.toResponse();
        }
    }

    /**
     * PUT method for adding a new properties attribute to a log entry.
     *
     * @param String newProperty the property being added
     * @param Long logId the id of the log entry that the property is being added to
     * @param data the XML payload containing attributes and their values to be added to be associated with the log entry
     * @return
     */
    @PUT
    @Path("{propName}/{logId}")
    @Produces({"application/xml", "application/json"})
    public Response addAttribute(@Context HttpServletRequest req, @Context HttpHeaders headers, @PathParam("propName") String property, @PathParam("logId") Long logId, XmlProperty data) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        OlogImpl cm = OlogImpl.getInstance();
        String user = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "";
        String hostAddress = req.getHeader("X-Forwarded-For") == null ? req.getRemoteAddr() : req.getHeader("X-Forwarded-For");
        Log result = null;
        try {
            cm.checkPropertyName(property, data);
            result = cm.addAttribute(logId, data);
            Response r = Response.ok(result).build();
            audit.info(user + "|" + uriInfo.getPath() + "|PUT|OK|" + r.getStatus());
            return r;
        } catch (CFException e) {
            log.warning(user + "|" + uriInfo.getPath() + "|PUT|ERROR|"
                    + e.getResponseStatusCode() + "|cause=" + e);
            return e.toResponse();
        }
    }

    /**
     * DELETE method for removing a properties attribute from a log entry.
     *
     * @param String newProperty the property being added
     * @param Long logId the id of the log entry that the property is being added to
     * @param data the XML payload containing attributes and their values to be removed from the log entry
     * @return
     */
    @DELETE
    @Path("{propName}/{logId}")
    @Produces({"application/xml", "application/json"})
    public Response removeAttribute(@Context HttpServletRequest req, @Context HttpHeaders headers, @PathParam("propName") String property, @PathParam("logId") Long logId, XmlProperty data) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        OlogImpl cm = OlogImpl.getInstance();
        String user = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "";
        String hostAddress = req.getHeader("X-Forwarded-For") == null ? req.getRemoteAddr() : req.getHeader("X-Forwarded-For");
        Log result = null;
        try {
            cm.checkPropertyName(property, data);
            result = cm.removeAttribute(logId, data);
            Response r = Response.ok(result).build();
            audit.info(user + "|" + uriInfo.getPath() + "|DELETE|OK|" + r.getStatus());
            return r;
        } catch (CFException e) {
            log.warning(user + "|" + uriInfo.getPath() + "|DELETE|ERROR|"
                    + e.getResponseStatusCode() + "|cause=" + e);
            return e.toResponse();
        }
    }
}
