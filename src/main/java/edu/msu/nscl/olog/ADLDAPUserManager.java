/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010-2011 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * All rights reserved. Use is subject to license terms and conditions.
 */

package edu.msu.nscl.olog;

import java.security.Principal;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Set;
import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;


/**
 * Owner (group) membership management: LDAP connection and binding.
 *
 * @author Ralph Lange <Ralph.Lange@helmholtz-berlin.de>
 */
public class ADLDAPUserManager extends UserManager {
    private ThreadLocal<DirContext> ctx = new ThreadLocal<DirContext>();
    private static final String ldapResourceName = "OlogGroup";

    /**
     * LDAP field name for the member UID
     */
    @Resource(name="ldapGroupMemberField") protected String memberUidField = "sAMAccountName";

    /**
     * LDAP field name for the group name in group entries
     */
    @Resource(name="ldapGroupTargetField") protected String groupTargetField = "memberOf";


    private DirContext getJndiContext() {
        DirContext dirctx = this.ctx.get();
        if (dirctx == null) {
            try {
                Context initCtx = new InitialContext();
                dirctx = (DirContext) initCtx.lookup(ldapResourceName);
                this.ctx.set(dirctx);
            } catch (NamingException e ) {
                throw new IllegalStateException("Cannot find JNDI LDAP resource '"
                        + ldapResourceName + "'", e);
            }
        }
        return dirctx;
    }

    @Override
    protected Set<String> getGroups(Principal user) {
        try {
            Set<String> groups = new HashSet<String>();
            DirContext dirctx = getJndiContext();
            SearchControls ctrls = new SearchControls();
            ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            String searchfilter = "(&(objectClass=user)(" + memberUidField + "=" + user.getName() + "))";
            String[] attributesToReturn = { "sAMAccountName", "memberOf", "cn" };
            ctrls.setReturningAttributes(attributesToReturn);
            NamingEnumeration<SearchResult> result = dirctx.search("", searchfilter, ctrls);

            
            while (result.hasMore()) {
                Attribute att = result.next().getAttributes().get(groupTargetField);
                if (att != null) {
                    for (int i=0; i<att.size(); i++){
                        Matcher m = Pattern.compile("CN=(.*?),[A-Z]{2}=",Pattern.CASE_INSENSITIVE).matcher((String)att.get(i));
                        if (m.find())
                            groups.add(m.group(1));
                    }  
                }
            }
            return groups;
        } catch (NamingException e) {
                throw new IllegalStateException("Error while retrieving group information for user '"
                        + user.getName() + "'", e);
        }
    }
}
