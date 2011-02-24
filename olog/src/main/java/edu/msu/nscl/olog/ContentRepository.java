/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.msu.nscl.olog;

import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jcr.LoginException;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import java.security.AccessController;
import javax.naming.InitialContext;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ContentRepository {
	private static ThreadLocal<Map<Integer, Session>> sessionMap = new ThreadLocal<Map<Integer,Session>>();
	private static Log logger = LogFactory.getLog(ContentRepository.class);
        private static final String jcrResourceName = "OlogRepository";
        private static final String jcrWorkspace = "olog";
        private static final int maxConcurrentSessions = 10;
        private javax.jcr.Repository jcrRepo;

        private static ThreadLocal<ContentRepository> instance = new ThreadLocal<ContentRepository>() {
            @Override
            protected ContentRepository initialValue() {
                return new ContentRepository();
            }
        };     

	protected int size(){
		return ((sessionMap == null)?0:sessionMap.get().size());
	}

	public ContentRepository(){
		reinitializeSessionMapResources(maxConcurrentSessions);
                try {
                    javax.naming.Context initCtx = new InitialContext();
                    jcrRepo = (javax.jcr.Repository) initCtx.lookup(jcrResourceName);
                } catch (NamingException ex) {
                    logger.error(ContentRepository.class.getName(),ex);
                }
	}

       /**
        * Returns the ContentRepository instance.
        *
        * @return ContentRepository instance
        */
        public static ContentRepository getInstance() {
            return instance.get();
        }

	public void cleanJcrSessions(){
		logger.debug("Starting the JCR session cleanup through ContextListener");
		synchronized (sessionMap) {
			for (Integer aKey : sessionMap.get().keySet()) {
				try {
					sessionMap.get().get(aKey).logout();
					logger.debug("Released a session...");
				} catch (Exception e) {
					logger.error(e, e);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void reinitializeSessionMapResources(int size){
	    logger.debug("Initializing Session Map");
		sessionMap.set((Map<Integer,Session>)(Collections.synchronizedMap(new LRUMap(size))));
		logger.debug("Succesfully initialized Session Map");
	}
        public javax.jcr.Repository getRepository(){
            return this.jcrRepo;
        }

        public Session getSession() {

            long threadId = Thread.currentThread().getId();
            final int sessionKeyId = new SessionKey(jcrWorkspace,threadId).hashCode();
            logger.debug("Session map is managing " + ((sessionMap == null)?0:sessionMap.get().size()) + " sessions");
            synchronized (sessionMap) {
		if (sessionMap.get().containsKey(sessionKeyId)) {
			Session session = sessionMap.get().get(threadId);
			if (session.isLive()) {
				String[] locks = session.getLockTokens();
				for (String aLock : locks){
					session.removeLockToken(aLock);
				}
				return session;
			}
		}
		try {
                    Subject subject = Subject.getSubject(AccessController.getContext());
                    Session session = (Session) Subject.doAs(subject, new PrivilegedAction() {
                    @Override
                        public Object run() {
                        try {
                            return jcrRepo.login();
                        } catch (LoginException ex) {
                            // TODO: throw auth error
                            Logger.getLogger(ContentRepository.class.getName()).log(Level.SEVERE, null, ex);
                            return null;
                        } catch (RepositoryException ex) {
                            // TODO: throw repo error
                            Logger.getLogger(ContentRepository.class.getName()).log(Level.SEVERE, null, ex);
                            return null;
                        }
                        }
                    });
                        sessionMap.get().put(sessionKeyId, session);
                        return session;
		} catch (Exception e) {
			logger.debug(e.getMessage());
			throw new RuntimeException(e);
		}
            }
        }

        class SessionKey{
            int hashCode = -1;
            SessionKey(String workspace, Long thread){
                workspace = (workspace == null)? "default" : workspace;
                thread = (thread == null)? UUID.randomUUID().clockSequence() : thread;
                hashCode =
                    17 * workspace.hashCode() +
                    19 * thread.hashCode();
            }
            @Override
            public int hashCode() {
                return hashCode;
            }
        }
}