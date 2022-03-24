/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog.boundry;

//import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import edu.msu.nscl.olog.JCRUtil;
import edu.msu.nscl.olog.OlogException;
import edu.msu.nscl.olog.entity.Attachment;
import edu.msu.nscl.olog.entity.XmlAttachment;
import edu.msu.nscl.olog.entity.XmlAttachments;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.JcrConstants;

/**
 *
 * @author berryman
 */
public class AttachmentManager {

    private static final Logger log = Logger.getLogger(AttachmentManager.class.getName());

    private AttachmentManager() {
    }

    public static List<Long> findAll(String searchTerm) throws OlogException {
        List<Long> ids = new ArrayList<Long>();
        Session session = JCRUtil.getSession();
        try {
            Workspace workspace = session.getWorkspace();
            QueryManager qm = workspace.getQueryManager();
            Query query = qm.createQuery("//element(*, nt:file)[jcr:contains(jcr:content, '" + searchTerm + "')]", Query.XPATH);
            QueryResult qr = query.execute();
            NodeIterator ni = qr.getNodes();
            while (ni.hasNext()) {
                Node node = ni.nextNode();
                Node parent = node.getParent();
                String name = parent.getName();
                ids.add(Long.valueOf(name));
            }
        } catch (LoginException e) {
            throw new OlogException(Response.Status.BAD_REQUEST,
                    "Search: " + searchTerm + " could not login to repository. " + e);
        } catch (RepositoryException e) {
            throw new OlogException(Response.Status.CONFLICT,
                    "Search: " + searchTerm + " could not put item in repository. " + e);
        } finally {
        	session.logout();
        }

        return ids;
    }

    public static XmlAttachments findAll(Long logId) throws OlogException {
        XmlAttachments xmlAttachments = new XmlAttachments();
        Session session = JCRUtil.getSession();
        try {
            Node rn = session.getRootNode();
            Node folderNode = rn.getNode(logId.toString());
            NodeIterator nodes = folderNode.getNodes();
            while (nodes.hasNext()) {
                Node contentNode = nodes.nextNode();
                String tfileName = contentNode.getName();
                XmlAttachment xmlAttachment = new XmlAttachment();
                xmlAttachment.setFileName(contentNode.getName());
                xmlAttachment.setContentType(contentNode.getNode(JcrConstants.JCR_CONTENT).getProperty(JcrConstants.JCR_MIMETYPE).getString());
                xmlAttachment.setFileSize(contentNode.getNode(JcrConstants.JCR_CONTENT).getProperty(JcrConstants.JCR_DATA).getLength());
                if (rn.hasNode("thumbnails/" + logId.toString() + "/" + tfileName)) {
                    xmlAttachment.setThumbnail(true);
                }
                xmlAttachments.addXmlAttachment(xmlAttachment);
            }
            return xmlAttachments;

        } catch (LoginException ex) {
            throw new OlogException(Response.Status.BAD_REQUEST,
                    "Log entry " + logId.toString() + " could not login to repository. " + ex);
        } catch (RepositoryException ex) {
            // TODO: Return Empty set only for javax.jcr.PathNotFoundException
            return xmlAttachments;
            //
            //throw new CFException(Response.Status.NOT_FOUND,
            //        "Log entry " + logId.toString() + " could not find item in repository. " + ex);
        } finally {
        	session.logout();
        }
    }

    public static Attachment findAttachment(String filePath, String fileName) throws OlogException {
        InputStream content = null;
        String mimeType = null;
        String[] arrayMimeType = null;
        Session session = JCRUtil.getSession();
        try {
            Node rn = session.getRootNode();
            Node folderNode = rn.getNode(filePath);
            Node contentNode = folderNode.getNode(fileName).getNode(JcrConstants.JCR_CONTENT);
            javax.jcr.Property dataProperty = contentNode.getProperty(JcrConstants.JCR_DATA);
            javax.jcr.Property mimeProperty = contentNode.getProperty(JcrConstants.JCR_MIMETYPE);

            mimeType = mimeProperty.getString();
            arrayMimeType = mimeType.split("/");

            Binary bin = dataProperty.getBinary();
            content = bin.getStream();
            bin.dispose();

        } catch (LoginException ex) {
            throw new OlogException(Response.Status.BAD_REQUEST,
                    filePath + ", could not login to repository. " + ex);
        } catch (RepositoryException ex) {
            throw new OlogException(Response.Status.NOT_FOUND,
                    filePath + ", could not find item in repository. " + ex);
        } finally {
        	session.logout();
        }
        Attachment attachment = new Attachment();
        attachment.setContent(content);
        if (arrayMimeType.length == 2) {
            attachment.setMimeType(new MediaType(arrayMimeType[0], arrayMimeType[1]));
        } else {
            attachment.setMimeType(new MediaType("application", "octet-stream"));
        }

        return attachment;
    }

    public static XmlAttachment create(Attachment attachment, Long logId) throws OlogException {
        XmlAttachment result = new XmlAttachment();
        Session session = JCRUtil.getSession();
        try {
            ValueFactory valueFactory = session.getValueFactory();
            Node rn = session.getRootNode();
            MediaType mimeType = attachment.getMimeType();
            String fileName = attachment.getFileName();
            Long fileSize = attachment.getFileSize();

            InputStream stream;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(attachment.getContent(), baos);
            byte[] bytes = baos.toByteArray();
            String string = new String(bytes);

            stream = new ByteArrayInputStream(bytes);

            if (mimeType == null) {
                mimeType = new MediaType("application", "octet-stream");
            }

            Node folderNode;

            if (rn.hasNode(logId.toString())) {
                folderNode = rn.getNode(logId.toString());
                if (!folderNode.isNodeType(JcrConstants.NT_FOLDER)) {
                    folderNode = rn.addNode(logId.toString(), JcrConstants.NT_FOLDER);
                }
            } else {
                folderNode = rn.addNode(logId.toString(), JcrConstants.NT_FOLDER);
            }
            Node fileNode = folderNode.addNode(fileName, JcrConstants.NT_FILE);
            Node resNode = fileNode.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);
            resNode.setProperty(JcrConstants.JCR_MIMETYPE, mimeType.toString());
            resNode.setProperty(JcrConstants.JCR_ENCODING, "");

            Binary binFile = valueFactory.createBinary(stream);
            resNode.setProperty(JcrConstants.JCR_DATA, binFile);

            // Add thumbnail
            if ((mimeType.getSubtype().equals("jpeg") || mimeType.getSubtype().equals("jpg")
                    || mimeType.getSubtype().equals("gif")
                    || mimeType.getSubtype().equals("png"))) {
                Node tfolderNode;
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Thumbnails.of(binFile.getStream()).size(80, 80).outputFormat(mimeType.getSubtype()).toOutputStream(outputStream);
                InputStream fis = new ByteArrayInputStream(outputStream.toByteArray());
                Binary binThumbnail = valueFactory.createBinary(fis);

                if (!rn.hasNode("thumbnails")) {
                    rn.addNode("thumbnails", JcrConstants.NT_FOLDER);
                }

                if (rn.hasNode("thumbnails/" + logId.toString())) {
                    tfolderNode = rn.getNode("thumbnails/" + logId.toString());
                    if (!tfolderNode.isNodeType(JcrConstants.NT_FOLDER)) {
                        tfolderNode = rn.addNode("thumbnails/" + logId.toString(), JcrConstants.NT_FOLDER);
                    }
                } else {
                    tfolderNode = rn.addNode("thumbnails/" + logId.toString(), JcrConstants.NT_FOLDER);
                }
                Node tfileNode = tfolderNode.addNode(fileName, JcrConstants.NT_FILE);
                Node tresNode = tfileNode.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);
                tresNode.setProperty(JcrConstants.JCR_MIMETYPE, mimeType.toString());
                tresNode.setProperty(JcrConstants.JCR_ENCODING, "");
                tresNode.setProperty(JcrConstants.JCR_DATA, binThumbnail);
                binThumbnail.dispose();
                result.setThumbnail(true);
            }
            binFile.dispose();

            session.save();
            result.setContentType(mimeType.toString());
            result.setFileName(fileName);
            result.setFileSize(fileSize);

            return result;

        } catch (IOException ex) {
            throw new OlogException(Response.Status.INTERNAL_SERVER_ERROR,
                    "Log entry " + logId.toString() + " could not create thumbnail. " + ex);
        } catch (LoginException ex) {
            throw new OlogException(Response.Status.BAD_REQUEST,
                    "Log entry " + logId.toString() + " could not login to repository. " + ex);
        } catch (RepositoryException ex) {
            throw new OlogException(Response.Status.CONFLICT,
                    "Log entry " + logId.toString() + " could not put item in repository. " + ex);
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new OlogException(Response.Status.INTERNAL_SERVER_ERROR,
                    "Log entry " + logId.toString() + " could not convert base64 object. " + ex);
        } finally {
        	session.logout();
        }
    }

    public static void remove(String fileName, Long logId) throws OlogException {
    	Session session = JCRUtil.getSession();
    	try {
            Node rn = session.getRootNode();
            Node folderNode = rn.getNode(logId.toString());
            Node contentNode = folderNode.getNode(fileName);
            contentNode.remove();
            if (rn.hasNode("thumbnails/" + logId.toString() + "/" + fileName)) {
                Node tfolderNode = rn.getNode("thumbnails/" + logId.toString());
                Node tcontentNode = tfolderNode.getNode(fileName);
                tcontentNode.remove();
            }
            session.save();

        } catch (LoginException ex) {
            throw new OlogException(Response.Status.BAD_REQUEST,
                    "Log entry " + logId.toString() + " could not login to repository. " + ex);
        } catch (RepositoryException ex) {
            throw new OlogException(Response.Status.NOT_FOUND,
                    "Log entry " + logId.toString() + " could not find item in repository. " + ex);
        } finally {
        	session.logout();
        }
    }
    
    private static Boolean isBase64Encoded(String str) {
        try {
            byte[] data = DatatypeConverter.parseBase64Binary(str);
            return (str.replace(" ", "").length() % 4 == 0);
        } catch (Exception ex) {
            return false;
        }
    }
}
