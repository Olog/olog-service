package edu.msu.nscl.olog;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;



/**
 * Tomcat requires java:/comp/env/ prefix for JNDI datasource but not Glassfish.
 * So this class, try to connect to given datasource, and if it doesn't work it
 * add the prefix to jndi lookup name.
 * 

public class JPATomcatSessionCustomizer implements SessionCustomizer {

	public void customize(Session session) throws Exception {
		JNDIConnector connector = null;
		Context context = null;
		try {
			context = new InitialContext();
			connector = (JNDIConnector) session.getLogin().getConnector();

			try {
				context.lookup(connector.getName());
			} catch (NameNotFoundException e) {
				try {
					String lookupName = "java:/comp/env/" + connector.getName();
					Object ds = context.lookup(lookupName);
					if (ds != null) {
						System.out.println("Replace jndi name '"
								+ connector.getName() + "' to '" + lookupName
								+ "' (tomcat compatibilty).");
						connector.setName(lookupName);
					}
				} catch (NameNotFoundException ex) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (context != null) {
				context.close();
			}
		}
	}
}
*  
*/