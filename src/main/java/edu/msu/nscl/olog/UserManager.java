/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010-2011 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * All rights reserved. Use is subject to license terms and conditions.
 */
package edu.msu.nscl.olog;

import java.security.Principal;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * Owner (group) membership management.
 *
 * @author Ralph Lange <Ralph.Lange@helmholtz-berlin.de>
 * @author Gabriele Carcassi <carcassi@bnl.gov>
 */
public abstract class UserManager {

    private static final Logger log = Logger.getLogger(UserManager.class.getName());

    private ThreadLocal<Principal> user = new ThreadLocal<Principal>();
    private ThreadLocal<Boolean> hasAdminRole = new ThreadLocal<Boolean>();
    private ThreadLocal<Collection<String>> groups = new ThreadLocal<Collection<String>>();
    private ThreadLocal<String> hostName = new ThreadLocal<String>();

    /**
     * Retrieves the group membership for the given principal.
     *
     * @param user a user
     * @return the group names
     */
    protected abstract Set<String> getGroups(Principal user);

    /**
     * Sets the (thread local) user principal to be used in further calls and
     * retrieves the group information.
     *
     * @param user principal
     * @param isAdmin flag: true = user has Admin role
     */
    public void setUser(Principal user, boolean isAdmin) {
        this.user.set(user);
        this.hasAdminRole.set(isAdmin);
        this.groups.set(getGroups(user));
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

    /**
     * Sets the (thread local) user hostName to be used in further calls.
     *
     * @param haostName String of host name
     */
    public void setHostAddress(String hostName) {
        this.hostName.set(hostName);
    }

    /**
     * Returns the current user's hostName.
     *
     * @return hostName of current user
     */
    public String getHostAddress() {
        return hostName.get();
    }
}
