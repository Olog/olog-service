/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */

package edu.msu.nscl.olog;

import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

/**
 * Owner (group) membership management: LDAP connection and binding.
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
public class UserManager {
    private static UserManager instance = new UserManager();
    private ThreadLocal<Principal> user = new ThreadLocal<Principal>();
    private ThreadLocal<Boolean> hasAdminRole = new ThreadLocal<Boolean>();
    private ThreadLocal<Collection<String>> groups = new ThreadLocal<Collection<String>>();
    private ThreadLocal<DirContext> ctx = new ThreadLocal<DirContext>();
   //TODO: need to add this to glassfish part 1
    // private static final String ldapResourceName = "OlogGroups";

    /**
     * LDAP field name for the member UID
     */
    @Resource(name="ldapGroupMemberField") protected String memberUidField = "member";

    /**
     * LDAP field name for the group name in group entries
     */
    @Resource(name="ldapGroupTargetField") protected String groupTargetField = "sAMAccountName";

    private UserManager() {
    }

    /**
     * Returns the (singleton) instance of UserManager.
     *
     * @return instance of UserManager
     */
    public static UserManager getInstance() {
        return instance;
    }

    private void clearGroups() {
        if (this.groups.get() == null) {
            this.groups.set(new HashSet<String>());
        } else {
            this.groups.get().clear();
        }
    }

    /*
     * TODO: this should be setup on glassfish part 2
     */
    private DirContext getJndiContext() {
        DirContext dirctx = this.ctx.get();
        if (dirctx == null) {
            try {
                Hashtable env = new Hashtable();
                env.put(Context.INITIAL_CONTEXT_FACTORY,
                    "com.sun.jndi.ldap.LdapCtxFactory");
                env.put(Context.PROVIDER_URL, "ldaps://intranet.nscl.msu.edu:636/ou=NSCL%20Users,dc=intranet,dc=nscl,dc=msu,dc=edu");
                env.put(Context.SECURITY_PRINCIPAL, "cn=svc_cf-user,OU=Service Accounts,DC=intranet,DC=nscl,DC=msu,DC=edu");
                env.put(Context.SECURITY_CREDENTIALS, "U?e4cA5X");
                env.put(Context.SECURITY_PROTOCOL, "ssl");
                dirctx = new InitialDirContext(env);
                //dirctx = (DirContext) initCtx.lookup(ldapResourceName);
                this.ctx.set(dirctx);
            } catch (NamingException e ) {
                //throw new IllegalStateException("Cannot find JNDI LDAP resource '"
                //        + ldapResourceName + "'", e);
                throw new IllegalStateException("Cannot find JNDI LDAP resource ", e);
            }
        }
        return dirctx;
    }

    /**
     * Sets the (thread local) user principal to be used in further calls, initializes
     * the LDAP directory context, if necessary, then queries LDAP to find the groups
     * mapping for the given user.
     *
     * @param user principal
     * @param isAdmin flag: true = user has Admin role
     */
    public void setUser(Principal user, boolean isAdmin) {
        String Dn = null;
        this.user.set(user);
        this.hasAdminRole.set(isAdmin);
        clearGroups();
        DirContext dirctx = getJndiContext();
               try {
            SearchControls ctrls = new SearchControls();
            ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            String searchfilter = "(sAMAccountName=" + this.user.get().getName() + ")";
            NamingEnumeration<SearchResult> result = dirctx.search("", searchfilter, ctrls);


            Attributes attDn = result.next().getAttributes();
            if (attDn.get("distinguishedName") != null) {
                Dn = (String)attDn.get("distinguishedName").get();
            }

        } catch (Exception e) {
                throw new IllegalStateException("Error while retrieving dn information for user '"
                        + this.user.get().getName() + "'", e);
        }
        try {
            SearchControls ctrls = new SearchControls();
            ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            String searchfilter = "(" + memberUidField + "="+Dn+")";
            NamingEnumeration<SearchResult> result = dirctx.search("", searchfilter, ctrls);

            while (result.hasMore()) {
                Attribute att = result.next().getAttributes().get(groupTargetField);
                if (att != null) {
                    groups.get().add((String)att.get());
                }
            }
        } catch (Exception e) {
                throw new IllegalStateException("Error while retrieving group information for user '"
                        + this.user.get().getName() + "'", e);
        }
    }

    /**
     * Checks if the user is in the specified <tt>group</tt>.
     *
     * @param group name of the group to check membership
     * @return true if user is a member of <tt>group</tt>
     */
    public boolean userIsInGroup(String group) {
        return group == null ? true : groups.get().contains(group);
    }

    /**
     * Checks if the user has admin role.
     *
     * @return true if user is a member of <tt>group</tt>
     */
    public boolean userHasAdminRole() {
        return hasAdminRole.get();
    }

    /**
     * Returns the current user's name.
     *
     * @return name of current user
     */
    public String getUserName() {
        return user.get().getName();
    }
}
