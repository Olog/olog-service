/*
 * Copyright (c) 2011 Michigan State University - Facility for Rare Isotope Beams
 */
package edu.msu.nscl.olog.boundry;

import edu.msu.nscl.olog.OlogException;
import edu.msu.nscl.olog.ResourceBinder;
import edu.msu.nscl.olog.control.OlogImpl;
import edu.msu.nscl.olog.UserManager;
import edu.msu.nscl.olog.control.PerformanceInterceptor;
import edu.msu.nscl.olog.entity.XmlAttachment;
import edu.msu.nscl.olog.entity.XmlAttachments;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;


/**
 * Top level Jersey HTTP methods for the .../attachments URL
 *
 * @author Eric Berryman
 */
@Path("/attachments/")
public class AttachmentResource {

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

    /** Creates a new instance of AttachmentsResource */
    public AttachmentResource() {
    }
    
    
    /**
     * GET method for retrieving attachments identified by <tt>id</tt>.
     *
     * @param logId log id
     * @return HTTP Response
     */
    @GET
    @Path("{logId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Interceptors(PerformanceInterceptor.class) 
    public Response read(@PathParam("logId") Long logId) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String user = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "";
        XmlAttachments result;
        try {
            result = cm.findAttachmentsById(logId);
            Response r = Response.ok(result).build();
            audit.info(user + "|" + uriInfo.getPath() + "|GET|OK|" + r.getStatus());

            return r;
        } catch (OlogException e) {
            log.warning(user + "|" + uriInfo.getPath() + "|GET|ERROR|"
                    + e.getResponseStatusCode() +  "|cause=" + e);
            return e.toResponse();
        }
    }
    
    /**
     * GET method for retrieving the list of attachments for a given log.
     *
     * @return
     */
    @GET
    @Path("{logId}/{fileName}")
    @Interceptors(PerformanceInterceptor.class) 
    public Response getFile(@PathParam("logId") Long logId, @PathParam("fileName") String fileName ) {
        String user = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "";
        edu.msu.nscl.olog.entity.Attachment result;
        try {
            String filePath = logId.toString();
            result = cm.getAttachment(filePath, fileName);
            Response r;
            if (result == null) {
                r = Response.status(Response.Status.NOT_FOUND).build();
            } else {
                r = Response.ok((Object)result.getContent()).type(result.getMimeType()).build();
            }
            audit.info( user+"|" + uriInfo.getPath() + "|GET|OK|" + r.getStatus());
            return r;
        } catch (OlogException e) {
            
            log.warning(user + "|" + uriInfo.getPath() + "|GET|ERROR|" + e.getResponseStatusCode() +  "|cause=" + e);
            return e.toResponse();
        }
    }
    
    /**
     * GET method for retrieving the list of attachments for a given log.
     *
     * @param logId
     * @param fileName
     * @return
     */
    @GET
    @Path("{logId}/{fileName}:thumbnail")
    @Interceptors(PerformanceInterceptor.class) 
    public Response getThumbnail(@PathParam("logId") Long logId, @PathParam("fileName") String fileName ) {
        String user = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : "";
        edu.msu.nscl.olog.entity.Attachment result;
        try {
            String filePath = "thumbnails/"+logId.toString();
            result = cm.getAttachment(filePath, fileName);
            Response r;
            if (result == null) {
                r = Response.status(Response.Status.NOT_FOUND).build();
            } else {
                r = Response.ok((Object)result.getContent()).type(result.getMimeType()).build();
            }
            audit.info( user+"|" + uriInfo.getPath() + "|GET|OK|" + r.getStatus());
            return r;
        } catch (OlogException e) {
            
            log.warning(user + "|" + uriInfo.getPath() + "|GET|ERROR|" + e.getResponseStatusCode() +  "|cause=" + e);
            return e.toResponse();
        }
    }
    
    /**
     * POST method for adding a new Attachment to a log entry.
     *
     * @param req
     * @param headers
     * @param logId the id of the log entry that the property is being added to
     * @param uploadedInputStream
     * @param disposition
     * @param body
     * @return
     */
    @POST
    @Path("{logId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Interceptors(PerformanceInterceptor.class) 
    public Response addAttachment(@Context HttpServletRequest req, 
                                  @Context HttpHeaders headers, 
                                  @PathParam("logId") Long logId,
                                  @FormDataParam("file") InputStream uploadedInputStream,
                                  @FormDataParam("file") FormDataContentDisposition disposition,
                                  @FormDataParam("file") FormDataBodyPart body) {
        UserManager um = rb.getUserManager();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        um.setHostAddress(req.getHeader("X-Forwarded-For") == null ? req.getRemoteAddr() : req.getHeader("X-Forwarded-For"));
        edu.msu.nscl.olog.entity.Attachment attachment = new edu.msu.nscl.olog.entity.Attachment();
        XmlAttachment result;
        try {
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroupOfLog(um.getUserName(), logId);
            }
            //TODO: Check PathParam fileName?
            attachment.setFileName(disposition.getFileName());
            attachment.setMimeType(body.getMediaType());
            attachment.setContent(uploadedInputStream);
            //attachment.setFileSize(incommingAttachment.getContentDisposition().getParameter(null))); 
            attachment.setEncoding(nonNull(body.getHeaders().getFirst("Content-Transfer-Encoding")));
            result = cm.createAttachment(attachment, logId);

            Response r = Response.ok(result).build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + r.getStatus());
            
            return r;
        } catch (OlogException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|POST|ERROR|"
                    + e.getResponseStatusCode() + "|cause=" + e);
            return e.toResponse();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|POST|ERROR|"
                    + Status.BAD_REQUEST + "|cause=" + e);
            return new OlogException(Status.BAD_REQUEST,e.toString()).toResponse();
        }
    }
    
    /**
     * PUT method for adding a new Attachment to a log entry.
     *
     * @param req
     * @param headers
     * @param fileName
     * @param logId
     * @param uploadedInputStream
     * @param disposition
     * @param body
     * @return
     */
    @PUT
    @Path("{logId}/{fileName}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Interceptors(PerformanceInterceptor.class) 
    public Response addReplaceAttachment(@Context HttpServletRequest req, 
                                  @Context HttpHeaders headers, 
                                  @PathParam("fileName") String fileName, 
                                  @PathParam("logId") Long logId,
                                  @FormDataParam("file") InputStream uploadedInputStream,
                                  @FormDataParam("file") FormDataContentDisposition disposition,
                                  @FormDataParam("file") FormDataBodyPart body) {
        UserManager um = rb.getUserManager();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        um.setHostAddress(req.getHeader("X-Forwarded-For") == null ? req.getRemoteAddr() : req.getHeader("X-Forwarded-For"));
        edu.msu.nscl.olog.entity.Attachment attachment = new edu.msu.nscl.olog.entity.Attachment();
        try {
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroupOfLog(um.getUserName(), logId);
            }
            //TODO: Check PathParam fileName?
            attachment.setFileName(disposition.getFileName());
            attachment.setMimeType(body.getMediaType());
            attachment.setContent(uploadedInputStream);
            //attachment.setFileSize(incommingAttachment.getContentDisposition().getParameter(null))); 
            attachment.setEncoding(nonNull(body.getHeaders().getFirst("Content-Transfer-Encoding")));
            //TODO: Should be destructive (replace)
            //cm.removeExistingAttachment(fileName,logId);
            cm.createAttachment(attachment, logId);
 
            String output = "File uploaded to : " + logId.toString()+"/"+disposition.getFileName();
            Response r = Response.status(200).entity(output).build();
            
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|OK|" + r.getStatus() + " | " + output);
            return r;
        } catch (OlogException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|ERROR|"
                    + e.getResponseStatusCode() + "|cause=" + e);
            return e.toResponse();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|POST|ERROR|"
                    + Status.BAD_REQUEST + "|cause=" + e);
            return new OlogException(Status.BAD_REQUEST,e.toString()).toResponse();
        }
    }

    /**
     * DELETE method for removing an attachment from a log entry.
     *
     * @param String attachment being deleted
     * @param Long logId the id of the log entry that the property is being added to
     * @return
     */
    @DELETE
    @Path("{logId}/{fileName}")
    @Interceptors(PerformanceInterceptor.class) 
    public Response removeAttachment(@Context HttpServletRequest req, 
                                     @Context HttpHeaders headers, 
                                     @PathParam("fileName") String fileName, 
                                     @PathParam("logId") Long logId) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        UserManager um = rb.getUserManager();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        try {
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroupOfLog(um.getUserName(), logId);
                cm.checkUserBelongsToGroup(um.getUserName(), cm.findLogById(logId));
            }
            cm.removeAttachment(fileName,logId);
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
    * Return a not null string.
    *
    * @param s String
    * @return empty string if it is null otherwise the string passed in as
    * parameter.
    */

    private static String nonNull(String s) {
        
        if (s == null) {
            return "";
        }
        return s;
    }
}
