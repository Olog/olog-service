/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */

package edu.msu.nscl.olog.boundry;

import edu.msu.nscl.olog.entity.Log;
import edu.msu.nscl.olog.entity.XmlLogs;
import edu.msu.nscl.olog.OlogException;
import edu.msu.nscl.olog.control.OlogImpl;
import edu.msu.nscl.olog.UserManager;
import edu.msu.nscl.olog.entity.BitemporalLog;
import edu.msu.nscl.olog.entity.XmlLog;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.jcr.RepositoryException;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.dozer.DozerBeanMapperSingletonWrapper;
import org.dozer.MappingException;

/**
 * Top level Jersey HTTP methods for the .../logs URL
 * 
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */

@Path("/logs/")
@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true)
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
     * based on a multi-parameter query specifying patterns for tag and logbook details to match against.
     *
     * @return HTTP Response
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response query() throws RepositoryException, UnsupportedEncodingException, NoSuchAlgorithmException {
        OlogImpl cm = OlogImpl.getInstance();
        String user = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "";
        try {
            List<BitemporalLog> result = cm.findLogsByMultiMatch(uriInfo.getQueryParameters());
            XmlLogs xmlresult = DozerBeanMapperSingletonWrapper.getInstance().map(result, XmlLogs.class);
            Response r = Response.ok(xmlresult).build();
            audit.info(user + "|" + uriInfo.getPath() + "|GET|OK|" + r.getStatus()
                    + "|returns " + result.size() + " logs");
            return r;
        } catch (MappingException e){
            log.warning(user + "|" + uriInfo.getPath() + "|GET|ERROR|"
                    + Response.Status.NOT_FOUND +  "|cause=" + e.getMessage());
            return new OlogException(Response.Status.NOT_FOUND,e.getMessage()).toResponse();
        } catch (OlogException e) {
            log.warning(user + "|" + uriInfo.getPath() + "|GET|ERROR|"
                    + e.getResponseStatusCode() +  "|cause=" + e);
            return e.toResponse();
        } 
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryjson() throws RepositoryException, UnsupportedEncodingException, NoSuchAlgorithmException {
        OlogImpl cm = OlogImpl.getInstance();
        String user = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "";
        try {
            List<BitemporalLog> result = cm.findLogsByMultiMatch(uriInfo.getQueryParameters());
            List<XmlLog> xmlresult = new ArrayList<XmlLog>();
            // should be a faster map
            for(BitemporalLog l:result){
                xmlresult.add(DozerBeanMapperSingletonWrapper.getInstance().map(l, XmlLog.class));
            }
            Response r = Response.ok(xmlresult).build();
            audit.info(user + "|" + uriInfo.getPath() + "|GET|OK|" + r.getStatus()
                    + "|returns " + result.size() + " logs");
            return r;
        } catch (MappingException e){
            log.warning(user + "|" + uriInfo.getPath() + "|GET|ERROR|"
                    + Response.Status.NOT_FOUND +  "|cause=" + e.getMessage());
            return new OlogException(Response.Status.NOT_FOUND,e.getMessage()).toResponse();
        } catch (OlogException e) {
            log.warning(user + "|" + uriInfo.getPath() + "|GET|ERROR|"
                    + e.getResponseStatusCode() +  "|cause=" + e);
            return e.toResponse();
        } 
    }

    /**
     * POST method for creating multiple log instances.
     *
     * @param data Logs data (from payload)
     * @return HTTP Response
     * @throws IOException when audit or log fail
     */
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response add(@Context HttpServletRequest req, @Context HttpHeaders headers, XmlLogs data) throws IOException, UnsupportedEncodingException, NoSuchAlgorithmException, NamingException, RepositoryException {
        OlogImpl cm = OlogImpl.getInstance();
        UserManager um = UserManager.getInstance();
        String hostAddress = req.getHeader("X-Forwarded-For") == null ? req.getRemoteAddr() : req.getHeader("X-Forwarded-For");
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        um.setHostAddress(hostAddress);

        try {
            List<Log> log_data = new ArrayList<Log>();
            for(XmlLog datum : data.getLogs()){
                Log result = DozerBeanMapperSingletonWrapper.getInstance().map(datum, Log.class);
                result.setOwner(um.getUserName());
                log_data.add(result);
            }
            
            cm.checkValid(log_data);
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroup(um.getUserName(), log_data);
            }
            
            List<Log> result = cm.createOrReplaceLogs(log_data);
            XmlLogs xmlresult = DozerBeanMapperSingletonWrapper.getInstance().map(result, XmlLogs.class);
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + "done adding the log"
                    + "|data=" + XmlLogs.toLogger(xmlresult.getLogs()));
            Response r = Response.ok(xmlresult).build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + r.getStatus()
                    + "|data=" + XmlLogs.toLogger(xmlresult.getLogs()));
            return r;
        } catch (MappingException e){
            log.warning(um.getUserName()  + "|" + uriInfo.getPath() + "|POST|ERROR|"
                    + Response.Status.NOT_FOUND + "|data=" + XmlLogs.toLogger(data.getLogs()) +  "|cause=" + e.getMessage());
            return new OlogException(Response.Status.NOT_FOUND,e.getMessage()).toResponse();
        } catch (OlogException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|POST|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + XmlLogs.toLogger(data.getLogs()) + "|cause=" + e);
            return e.toResponse();
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addjson(@Context HttpServletRequest req, @Context HttpHeaders headers, List<XmlLog> data) throws IOException, UnsupportedEncodingException, NoSuchAlgorithmException, NamingException, RepositoryException {
        OlogImpl cm = OlogImpl.getInstance();
        UserManager um = UserManager.getInstance();
        String hostAddress = req.getHeader("X-Forwarded-For") == null ? req.getRemoteAddr() : req.getHeader("X-Forwarded-For");
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        um.setHostAddress(hostAddress);
        try {
            List<Log> log_data = new ArrayList<Log>();
            for(XmlLog datum : data){
                Log result = DozerBeanMapperSingletonWrapper.getInstance().map(datum, Log.class);
                result.setOwner(um.getUserName());
                log_data.add(result);
            }
            
            cm.checkValid(log_data);
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroup(um.getUserName(), log_data);
            }
            
            List<Log> result = cm.createOrReplaceLogs(log_data);
            XmlLogs xmlresult = DozerBeanMapperSingletonWrapper.getInstance().map(result, XmlLogs.class);
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + "done adding the log"
                    + "|data=" + XmlLogs.toLogger(xmlresult.getLogs()));
            Response r = Response.ok(xmlresult).build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + r.getStatus()
                    + "|data=" + XmlLogs.toLogger(xmlresult.getLogs()));
            return r;
        } catch (MappingException e){
            log.warning(um.getUserName()  + "|" + uriInfo.getPath() + "|POST|ERROR|"
                    + Response.Status.NOT_FOUND + "|data=" + XmlLogs.toLogger(data) +  "|cause=" + e.getMessage());
            return new OlogException(Response.Status.NOT_FOUND,e.getMessage()).toResponse();
        } catch (OlogException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|POST|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + XmlLogs.toLogger(data) + "|cause=" + e);
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
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response read(@PathParam("logId") Long logId) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        OlogImpl cm = OlogImpl.getInstance();
        String user = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "";
        Log result = null;
        try {
            result = cm.findLogById(logId, uriInfo.getQueryParameters());
            Response r;
            if (result == null) {
                r = Response.status(Response.Status.NOT_FOUND).build();
            } else {
                XmlLog xmlresult = DozerBeanMapperSingletonWrapper.getInstance().map(result, XmlLog.class);
                r = Response.ok(xmlresult).build();
            }
            audit.info(user + "|" + uriInfo.getPath() + "|GET|OK|" + r.getStatus());
            return r;
        } catch (MappingException e){
            log.warning(user + "|" + uriInfo.getPath() + "|GET|ERROR|"
                    + Response.Status.NOT_FOUND +  "|cause=" + e.getMessage());
            return new OlogException(Response.Status.NOT_FOUND,e.getMessage()).toResponse();
        } catch (OlogException e) {
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
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response create(@Context HttpServletRequest req, @Context HttpHeaders headers, @PathParam("logId") Long logId, XmlLog data) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        OlogImpl cm = OlogImpl.getInstance();
        UserManager um = UserManager.getInstance();
        String hostAddress = req.getHeader("X-Forwarded-For") == null ? req.getRemoteAddr() : req.getHeader("X-Forwarded-For");
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        um.setHostAddress(hostAddress);
        try {
            Log log_data = DozerBeanMapperSingletonWrapper.getInstance().map(data, Log.class);
            log_data.setOwner(um.getUserName());
            cm.checkValid(log_data);
            cm.checkIdMatchesPayload(logId, log_data);
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroup(um.getUserName(), log_data);
            }
            
            Log result = cm.createOrReplaceLog(logId, log_data);
            XmlLog xmlresult = DozerBeanMapperSingletonWrapper.getInstance().map(result, XmlLog.class);
            Response r = Response.ok(xmlresult).build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|OK|" + r.getStatus()
                    + "|data=" + XmlLog.toLogger(xmlresult));
            return r;
        } catch (MappingException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|ERROR|" + Response.Status.NOT_FOUND
                    + "|data=" + XmlLog.toLogger(data) + "|cause=" + e.getMessage());
            return new OlogException(Response.Status.NOT_FOUND,e.getMessage()).toResponse();
        } catch (OlogException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + XmlLog.toLogger(data) + "|cause=" + e);
            return e.toResponse();
        }
    }

    /**
     * POST method for merging logbooks and tags of the Log identified by the
     * payload into an existing log.
     *
     * @param logId id of log to add
     * @param data new Log data (logbooks/tags) to be merged into log <tt>id</tt>
     * @return HTTP response
     */
    @POST
    @Path("{logId}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response update(@Context HttpServletRequest req, @Context HttpHeaders headers, @PathParam("logId") Long logId, XmlLog data) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        OlogImpl cm = OlogImpl.getInstance();
        UserManager um = UserManager.getInstance();
        String hostAddress = req.getHeader("X-Forwarded-For") == null ? req.getRemoteAddr() : req.getHeader("X-Forwarded-For");
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        um.setHostAddress(hostAddress);
        Log result = null;
        try {
           Log log_data = DozerBeanMapperSingletonWrapper.getInstance().map(data, Log.class);
            log_data.setOwner(um.getUserName());
            cm.checkValid(log_data);
            cm.checkIdMatchesPayload(logId, log_data);
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroupOfLog(um.getUserName(), logId);
                cm.checkUserBelongsToGroup(um.getUserName(), log_data);
            }
            
            result = cm.updateLog(logId, log_data);
            XmlLog xmlresult = DozerBeanMapperSingletonWrapper.getInstance().map(result, XmlLog.class);
            Response r = Response.ok(xmlresult).build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + r.getStatus()
                    + "|data=" + XmlLog.toLogger(xmlresult));
            return r;
        } catch (MappingException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|POST|ERROR|" + Response.Status.NOT_FOUND
                    + "|data=" + XmlLog.toLogger(data) + "|cause=" + e.getMessage());
            return new OlogException(Response.Status.NOT_FOUND,e.getMessage()).toResponse();
        } catch (OlogException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|POST|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + XmlLog.toLogger(data) + "|cause=" + e);
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
    public Response remove(@PathParam("logId") Long logId) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        UserManager um = UserManager.getInstance();
        OlogImpl cm = OlogImpl.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroup(um.getUserName(), cm.findLogById(logId));
            }
            cm.removeLog(logId);
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
