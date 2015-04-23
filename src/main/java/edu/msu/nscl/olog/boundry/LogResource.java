/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */
package edu.msu.nscl.olog.boundry;

import edu.msu.nscl.olog.entity.XmlLogs;
import edu.msu.nscl.olog.OlogException;
import edu.msu.nscl.olog.control.OlogImpl;
import edu.msu.nscl.olog.UserManager;
import edu.msu.nscl.olog.bitemporal.control.TimeUtils;
import edu.msu.nscl.olog.control.Mapper;
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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import org.joda.time.DateTime;

/**
 * Top level Jersey HTTP methods for the .../logs URL
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
@Path("/logs/")
public class LogResource {

    //@Resource
    //private WebServiceContext wsContext
    @Context
    private UriInfo uriInfo;
    @Context
    private SecurityContext securityContext;

    private Logger audit = Logger.getLogger(this.getClass().getPackage().getName() + ".audit");
    private Logger log = Logger.getLogger(this.getClass().getName());

    /**
     * Creates a new instance of LogsResource
     */
    public LogResource() {
    }

    /**
     * GET method for retrieving a collection of Log instances, based on a
     * multi-parameter query specifying patterns for tag and logbook details to
     * match against.
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
            XmlLogs xmlresult = Mapper.getXmlLogs(result);
            Response r = Response.ok(xmlresult).build();
            audit.info(user + "|" + uriInfo.getPath() + "|GET|OK|" + r.getStatus()
                    + "|returns " + result.size() + " logs");
            return r;
        } catch (OlogException e) {
            log.warning(user + "|" + uriInfo.getPath() + "|GET|ERROR|"
                    + e.getResponseStatusCode() + "|cause=" + e);
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
            XmlLogs xmlresult = Mapper.getXmlLogs(result);

            Response r = Response.ok(xmlresult.getLogs()).build();
            audit.info(user + "|" + uriInfo.getPath() + "|GET|OK|" + r.getStatus()
                    + "|returns " + result.size() + " logs");
            return r;
        } catch (OlogException e) {
            log.warning(user + "|" + uriInfo.getPath() + "|GET|ERROR|"
                    + e.getResponseStatusCode() + "|cause=" + e);
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
            List<BitemporalLog> log_data = new ArrayList<BitemporalLog>();
            for (XmlLog datum : data.getLogs()) {
                BitemporalLog result = Mapper.getBitemporalLog(datum);
                result.getLog().setOwner(um.getUserName());
                log_data.add(result);
            }

            cm.checkValid(log_data);
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroup(um.getUserName(), log_data);
            }

            List<BitemporalLog> result = cm.createOrReplaceLogs(log_data);
            XmlLogs xmlresult = Mapper.getXmlLogs(result);
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + "done adding the log"
                    + "|data=" + XmlLogs.toLogger(xmlresult.getLogs()));
            Response r = Response.ok(xmlresult).build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + r.getStatus()
                    + "|data=" + XmlLogs.toLogger(xmlresult.getLogs()));
            return r;
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
            List<BitemporalLog> log_data = new ArrayList<BitemporalLog>();
            for (XmlLog datum : data) {
                BitemporalLog result = Mapper.getBitemporalLog(datum);
                result.getLog().setOwner(um.getUserName());
                log_data.add(result);
            }

            cm.checkValid(log_data);
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroup(um.getUserName(), log_data);
            }

            List<BitemporalLog> result = cm.createOrReplaceLogs(log_data);
            XmlLogs xmlresult = Mapper.getXmlLogs(result);
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + "done adding the log"
                    + "|data=" + XmlLogs.toLogger(xmlresult.getLogs()));
            Response r = Response.ok(xmlresult).build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + r.getStatus()
                    + "|data=" + XmlLogs.toLogger(xmlresult.getLogs()));
            return r;
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
        BitemporalLog result = null;
        try {
            result = cm.findLogById(logId, uriInfo.getQueryParameters());
            Response r;
            if (result == null) {
                r = Response.status(Response.Status.NOT_FOUND).build();
            } else {
                XmlLog xmlresult = Mapper.getXmlLog(result);
                r = Response.ok(xmlresult).build();
            }
            audit.info(user + "|" + uriInfo.getPath() + "|GET|OK|" + r.getStatus());
            return r;
        } catch (OlogException e) {
            log.warning(user + "|" + uriInfo.getPath() + "|GET|ERROR|"
                    + e.getResponseStatusCode() + "|cause=" + e);
            return e.toResponse();
        }
    }

    /**
     * PUT method for editing a log instance identified by the payload. The
     * <b>complete</b> set of logbooks/tags for the log must be supplied, which
     * will replace the existing set of logbooks/tags.
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
            BitemporalLog log_data = Mapper.getBitemporalLogMergeInterval(logId, data);

            log_data.getLog().setOwner(um.getUserName());
            cm.checkValid(log_data);
            cm.checkIdMatchesPayload(logId, log_data);
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroup(um.getUserName(), log_data);
            }

            BitemporalLog result = cm.createOrReplaceLog(logId, log_data);
            XmlLog xmlresult = Mapper.getXmlLog(result);
            Response r = Response.ok(xmlresult).build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|OK|" + r.getStatus()
                    + "|data=" + XmlLog.toLogger(xmlresult));
            return r;
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
     * @param data new Log data (logbooks/tags) to be merged into log
     * <tt>id</tt>
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
        BitemporalLog result = null;
        try {
            BitemporalLog log_data = Mapper.getBitemporalLogMergeInterval(logId, data);

            log_data.getLog().setOwner(um.getUserName());
            cm.checkValid(log_data);
            cm.checkIdMatchesPayload(logId, log_data);
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroupOfLog(um.getUserName(), logId);
                cm.checkUserBelongsToGroup(um.getUserName(), log_data);
            }

            result = cm.updateLog(logId, log_data);
            XmlLog xmlresult = Mapper.getXmlLog(result);
            Response r = Response.ok(xmlresult).build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + r.getStatus()
                    + "|data=" + XmlLog.toLogger(xmlresult));
            return r;
        } catch (OlogException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|POST|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + XmlLog.toLogger(data) + "|cause=" + e);
            return e.toResponse();
        }
    }

    /**
     * DELETE method for deleting a log instance identified by path parameter
     * <tt>id</tt>.
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

    /**
     * POST method for importing multiple log instances, create time
     * and user persisted.
     *
     * @param data Logs data (from payload)
     * @return HTTP Response
     * @throws IOException when audit or log fail
     */
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("import")
    public Response importLogs(@Context HttpServletRequest req, @Context HttpHeaders headers, XmlLogs data) throws IOException, UnsupportedEncodingException, NoSuchAlgorithmException, NamingException, RepositoryException {
        OlogImpl cm = OlogImpl.getInstance();
        UserManager um = UserManager.getInstance();
        String hostAddress = req.getHeader("X-Forwarded-For") == null ? req.getRemoteAddr() : req.getHeader("X-Forwarded-For");
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        um.setHostAddress(hostAddress);
        if (!um.userHasAdminRole()) {
            return (new OlogException(Status.FORBIDDEN, "Requires Admin")).toResponse();
        }

        try {
            List<BitemporalLog> log_data = new ArrayList<BitemporalLog>();
            for (XmlLog datum : data.getLogs()) {
                TimeUtils.setReference(new DateTime(datum.getCreatedDate().getTime()));
                BitemporalLog result = null;
                if (datum.getId() != null) {
                    result = Mapper.getBitemporalLogMergeInterval(datum.getId(),datum);
                } else {
                    result = Mapper.getBitemporalLog(datum);     
                }
                log_data.add(result);
            }

            cm.checkValid(log_data);

            List<BitemporalLog> result = cm.createOrReplaceLogs(log_data);
            XmlLogs xmlresult = Mapper.getXmlLogs(result);
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + "done adding the log"
                    + "|data=" + XmlLogs.toLogger(xmlresult.getLogs()));
            Response r = Response.ok(xmlresult).build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + r.getStatus()
                    + "|data=" + XmlLogs.toLogger(xmlresult.getLogs()));
            return r;
        } catch (OlogException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|POST|ERROR|" + e.getResponseStatusCode()
                    + "|data=" + XmlLogs.toLogger(data.getLogs()) + "|cause=" + e);
            return e.toResponse();
        }
    }
}
