/*
 * Copyright (c) 2011 Michigan State University - Facility for Rare Isotope Beams
 */
package edu.msu.nscl.olog.boundry;

import edu.msu.nscl.olog.OlogException;
import edu.msu.nscl.olog.control.OlogImpl;
import edu.msu.nscl.olog.UserManager;
import edu.msu.nscl.olog.entity.XmlAttachment;
import edu.msu.nscl.olog.entity.XmlAttachments;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;


/**
 * Top level Jersey HTTP methods for the .../attachments URL
 *
 * @author Eric Berryman
 */
@Path("/attachments/")
@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true)
public class AttachmentsResource {

    @Context
    private UriInfo uriInfo;
    @Context
    private SecurityContext securityContext;
    
    private Logger audit = Logger.getLogger(this.getClass().getPackage().getName() + ".audit");
    private Logger log = Logger.getLogger(this.getClass().getName());

    /** Creates a new instance of AttachmentsResource */
    public AttachmentsResource() {
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
    public Response read(@PathParam("logId") Long logId) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        OlogImpl cm = OlogImpl.getInstance();
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
    public Response getFile(@PathParam("logId") Long logId, @PathParam("fileName") String fileName ) {
        OlogImpl cm = OlogImpl.getInstance();
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
     * @return
     */
    @GET
    @Path("{logId}/{fileName}:thumbnail")
    public Response getThumbnail(@PathParam("logId") Long logId, @PathParam("fileName") String fileName ) {
        OlogImpl cm = OlogImpl.getInstance();
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
     * @param Long logId the id of the log entry that the property is being added to
     * @param data the MULTIPART_FORM_DATA containing attachment and values to be added to be associated with the log entry
     * @return
     */
    @POST
    @Path("{logId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response addAttachment(@Context HttpServletRequest req, 
                                  @Context HttpHeaders headers, 
                                  @PathParam("logId") Long logId,
                                  @Multipart("file") org.apache.cxf.jaxrs.ext.multipart.Attachment incommingAttachment) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        OlogImpl cm = OlogImpl.getInstance();
        UserManager um = UserManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        um.setHostAddress(req.getHeader("X-Forwarded-For") == null ? req.getRemoteAddr() : req.getHeader("X-Forwarded-For"));
        edu.msu.nscl.olog.entity.Attachment attachment = new edu.msu.nscl.olog.entity.Attachment();
        XmlAttachment result;
        try {
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroupOfLog(um.getUserName(), logId);
            }
            //TODO: Check PathParam fileName?
            attachment.setFileName(incommingAttachment.getContentDisposition().getParameter("filename"));
            attachment.setMimeType(incommingAttachment.getContentType());
            attachment.setContent(incommingAttachment.getObject(InputStream.class));
            //attachment.setFileSize(incommingAttachment.getContentDisposition().getParameter(null))); 
            attachment.setEncoding(nonNull(incommingAttachment.getHeaders().getFirst("Content-Transfer-Encoding")));
            result = cm.createAttachment(attachment, logId);

            Response r = Response.ok(result).build();
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|POST|OK|" + r.getStatus());
            
            return r;
        } catch (OlogException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|POST|ERROR|"
                    + e.getResponseStatusCode() + "|cause=" + e);
            return e.toResponse();
        }
    }
    
    /**
     * PUT method for adding a new Attachment to a log entry.
     *
     * @param String fileName the fileName of the file being added
     * @param Long logId the id of the log entry that the property is being added to
     * @param data the MULTIPART_FORM_DATA containing attachment and values to be added to be associated with the log entry
     * @return
     */
    @PUT
    @Path("{logId}/{fileName}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response addReplaceAttachment(@Context HttpServletRequest req, 
                                  @Context HttpHeaders headers, 
                                  @PathParam("fileName") String fileName, 
                                  @PathParam("logId") Long logId,
                                  @Multipart("file") org.apache.cxf.jaxrs.ext.multipart.Attachment incommingAttachment) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        OlogImpl cm = OlogImpl.getInstance();
        UserManager um = UserManager.getInstance();
        um.setUser(securityContext.getUserPrincipal(), securityContext.isUserInRole("Administrator"));
        um.setHostAddress(req.getHeader("X-Forwarded-For") == null ? req.getRemoteAddr() : req.getHeader("X-Forwarded-For"));
        edu.msu.nscl.olog.entity.Attachment attachment = new edu.msu.nscl.olog.entity.Attachment();
        try {
            if (!um.userHasAdminRole()) {
                cm.checkUserBelongsToGroupOfLog(um.getUserName(), logId);
            }
            //TODO: Check PathParam fileName?
            attachment.setFileName(incommingAttachment.getContentDisposition().getParameter("filename"));
            attachment.setMimeType(incommingAttachment.getContentType());
            attachment.setContent(incommingAttachment.getObject(InputStream.class));
            //attachment.setFileSize(incommingAttachment.getContentDisposition().getParameter(null))); 
            attachment.setEncoding(nonNull(incommingAttachment.getHeaders().getFirst("Content-Transfer-Encoding")));
            //TODO: Should be destructive (replace)
            //cm.removeExistingAttachment(fileName,logId);
            cm.createAttachment(attachment, logId);
 
            String output = "File uploaded to : " + logId.toString()+"/"+incommingAttachment.getContentDisposition().getParameter("filename");
            Response r = Response.status(200).entity(output).build();
            
            audit.info(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|OK|" + r.getStatus() + " | " + output);
            return r;
        } catch (OlogException e) {
            log.warning(um.getUserName() + "|" + uriInfo.getPath() + "|PUT|ERROR|"
                    + e.getResponseStatusCode() + "|cause=" + e);
            return e.toResponse();
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
    public Response removeAttachment(@Context HttpServletRequest req, 
                                     @Context HttpHeaders headers, 
                                     @PathParam("fileName") String fileName, 
                                     @PathParam("logId") Long logId) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        UserManager um = UserManager.getInstance();
        OlogImpl cm = OlogImpl.getInstance();
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
