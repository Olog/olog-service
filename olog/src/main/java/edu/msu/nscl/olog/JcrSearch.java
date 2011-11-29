/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.QueryManager;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Repository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author gaul
 */
public class JcrSearch {

    private static Repository cr;
    private static Log logger = LogFactory.getLog(JcrSearch.class);

    public JcrSearch() {
        cr = ContentRepository.getRepository();
    }

    public static List<Long> searchForIds(String searchTerm) throws RepositoryException {
        List<Long> ids = new ArrayList<Long>();
        try {
            Session session = cr.login();
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
                //PropertyIterator pi = node.getProperties();
                //while (pi.hasNext()) {
                //    Property property = pi.nextProperty();
                //    String name = property.getName();
                //    if (name.equals("id")) {
                //        ids.add(property.getValue().getLong());
                //    }
                //}
            }
        } catch (LoginException e) {
            logger.error(JcrSearch.class.getName(),e);
        }
        catch (RepositoryException e) {
            logger.error(JcrSearch.class.getName(),e);
        }

        return ids;
    }
}
