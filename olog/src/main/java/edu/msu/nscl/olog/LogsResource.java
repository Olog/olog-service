/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */

package edu.msu.nscl.olog;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.naming.NamingException;
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
import javax.ws.rs.core.HttpHeaders;
import org.apache.jackrabbit.servlet.ServletRepository;

/**
 * Top level Jersey HTTP methods for the .../logs URL
 * 
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */

@Path("/logs/")
public class LogsResource {
    //@Resource
    //private WebServiceContext wsContext
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
    public Response query() throws RepositoryException {
        OLogManager cm = OLogManager.getInstance();
        String user = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "";
        try {
            XmlLogs result = cm.findLogsByMultiMatch(uriInfo.getQueryParameters());
            Response r = Response.ok(result).build();
            log.fine(user + "|" + uriInfo.getPath() + "|GET|OK|" + r.getStatus()
                    + "|returns " + result.getLogs().size() + " logs");
            return r;
        } catch (CFException e) {
            log.warning(user + "|" + uriInfo.getPath() + "|GET|ERROR|"
                    + e.getResponseStatusCode() +  "|cause=" + e);
            return e.toResponse();
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
    @Produces({"application/xml", "application/json"})
    public Response add(@Context HttpHeaders headers,XmlLogs data) throws IOException, UnsupportedEncodingException, NoSuchAlgorithmException, NamingException, RepositoryException {
        OLogManager cm = OLogManager.getInstance();
        UserManager um = UserManager.getInstance();
        String hostAddress = "0.0.0.0";//req.getRemoteAddr();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            XmlLogs data_temp = new XmlLogs();
            for(XmlLog datum : data.getLogs()){
                datum.setOwner(um.getUserName());
                data_temp.addXmlLog(datum);
            }
            data = data_temp;
            cm.checkValidSubjectAndOwner(data);
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroup(um.getUserName(), data);
            }
            
            XmlLogs result = cm.createOrReplaceLogs(hostAddress,data);
            Response r = Response.ok(result).build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + r.getStatus()
                    + "|data=" + XmlLogs.toLog(data));
            return r;
        } catch (CFException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|POST|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + XmlLogs.toLog(data) + "|cause=" + e);
            return e.toResponse();
        }
    }

    /**
     * GET method for retrieving an instance of Log identified by <tt>id</tt>.
     *
     * @param logId log id
     * @return HTTP Response
     */
    @GET
    @Path("{logId}")
    @Produces({"application/xml", "application/json"})
    public Response read(@PathParam("logId") Long logId) {
        OLogManager cm = OLogManager.getInstance();
        String user = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "";
        XmlLog result = null;
        try {
            result = cm.findLogById(logId);
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
        }
    }

    /**
     * PUT method for editing a log instance identified by the payload.
     * The <b>complete</b> set of logbooks/tags for the log must be supplied,
     * which will replace the existing set of logbooks/tags.
     *
     * @param logId id of log to edit
     * @param data new data (logbooks/tags) for log <tt>id</tt>
     * @return HTTP response
     */
    @PUT
    @Path("{logId}")
    @Consumes({"application/xml", "application/json"})
    public Response create(@Context HttpHeaders headers, @PathParam("logId") Long logId, XmlLog data) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        OLogManager cm = OLogManager.getInstance();
        UserManager um = UserManager.getInstance();
        //MessageContext mc = wsContext.getMessageContext();
        //HttpServletRequest req = (HttpServletRequest)mc.get(MessageContext.SERVLET_REQUEST);
        String hostAddress = "0.0.0.0";//req.getRemoteAddr();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        XmlLog result = null;
        try {
            data.setOwner(um.getUserName());
            cm.checkValidSubjectAndOwner(data);
            cm.checkIdMatchesPayload(logId, data);
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroup(um.getUserName(), data);
            }
            
            result = cm.createOrReplaceLog(hostAddress, logId, data);
            Response r = Response.ok(result).build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|OK|" + r.getStatus()
                    + "|data=" + XmlLog.toLog(data));
            return r;
        } catch (CFException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + XmlLog.toLog(data) + "|cause=" + e);
            return e.toResponse();
        }
    }

    /**
     * POST method for merging logbooks and tags of the Log identified by the
     * payload into an existing log.
     *
     * @param logId id of log to add
     * @param data new XmlLog data (logbooks/tags) to be merged into log <tt>id</tt>
     * @return HTTP response
     */
    @POST
    @Path("{logId}")
    @Consumes({"application/xml", "application/json"})
    public Response update(@Context HttpHeaders headers, @PathParam("logId") Long logId, XmlLog data) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        OLogManager cm = OLogManager.getInstance();
        UserManager um = UserManager.getInstance();
        //MessageContext mc = wsContext.getMessageContext();
        //HttpServletRequest req = (HttpServletRequest)mc.get(MessageContext.SERVLET_REQUEST);
        String hostAddress = "0.0.0.0";//req.getRemoteAddr();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        XmlLog result = null;
        try {
            data.setOwner(um.getUserName());
            cm.checkValidSubjectAndOwner(data);
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroupOfLog(um.getUserName(), logId);
                cm.checkUserBelongsToGroup(um.getUserName(), data);
            }
            
            result = cm.updateLog(hostAddress, logId, data);
            Response r = Response.ok(result).build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + r.getStatus()
                    + "|data=" + XmlLog.toLog(data));
            return r;
        } catch (CFException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|POST|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + XmlLog.toLog(data) + "|cause=" + e);
            return e.toResponse();
        }
    }

    /**
     * DELETE method for deleting a log instance identified by
     * path parameter <tt>id</tt>.
     *
     * @param logId log to remove
     * @return HTTP Response
     */
    @DELETE
    @Path("{logId}")
    public Response remove(@PathParam("logId") Long logId) {
        UserManager um = UserManager.getInstance();
        OLogManager cm = OLogManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroup(um.getUserName(), cm.findLogById(logId));
            }
            cm.removeExistingLog(logId);
            Response r = Response.ok().build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|DELETE|OK|" + r.getStatus());
            return r;
        } catch (CFException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|DELETE|ERROR|" + e.getResponseStatusCode()
                    + "|cause=" + e);
            return e.toResponse();
        }
    }
}
