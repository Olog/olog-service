/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import edu.msu.nscl.olog.entity.XmlAttachments;
import edu.msu.nscl.olog.entity.Attachment;
import edu.msu.nscl.olog.entity.XmlAttachment;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.JcrConstants;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author berryman
 */
public class AttachmentManagerTest {

    private AttachmentManagerTest() {
    }

    public static List<Long> findAll(String searchTerm) throws OlogException {
        return new ArrayList<Long>();
    }

    public static XmlAttachments findAll(Long logId) throws OlogException {
        return new XmlAttachments();
    }

    public static Attachment findAttachment(String filePath, String fileName) throws OlogException {
        return new Attachment();
    }

    public static XmlAttachment create(Attachment attachment, Long logId) throws OlogException {
        return new XmlAttachment();
    }



    public static void remove(String fileName, Long logId) throws OlogException {

    }
}
