/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */
package edu.msu.nscl.olog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * JDBC query to add a logbook to log(s).
 * 
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
public class UpdateValuesQuery {

    private Logger audit = Logger.getLogger(this.getClass().getPackage().getName() + ".audit");
    private static SqlSessionFactory ssf = MyBatisSession.getSessionFactory();

    /**
     * Creates a new instance of UpdateValuesQuery.
     *
     * @param data logbook data (containing logs to add logbook to)
     */
    private UpdateValuesQuery() {
    }
    
    /**
     * Updates a logbook in the database with an incoming log
     *
     * @param name String the name of the logbook
     * @param logId Long the log to add to logbook
     * @throws CFException wrapping an SQLException
     */
    public static Logbook updateLogbookWithLog(String name, Long logId) throws CFException {
        SqlSession ss = ssf.openSession();

        try {
            List<Long> ids = new ArrayList<Long>();

            // Get logbook id
            Long pid = LogbookManager.findLogbook(name).getId();

            if (pid == null) {
                throw new CFException(Response.Status.NOT_FOUND, "Log entry + " + logId 
                        + " could not be added to logbook '" + name 
                        + "': Logbook does not exist");
            }
            
            // Add incoming id
            ids.add(logId);

            if (ids.isEmpty()) {
                throw new CFException(Response.Status.BAD_REQUEST,
                        "Log entry could not be added to logbook '" + name 
                        + "': No log specified");
            }

            HashMap<String, Object> hm = new HashMap<String, Object>();
            hm.put("logidsList", ids);
            hm.put("logbookid", pid);
            hm.put("state", null);

            ss.insert("mappings.LogMapping.logsLogbooksEntryFromList", hm);

            ss.commit();
            
            // Return the logbook now that the new log has been added
            return LogbookManager.findLogbook(name);
        } catch (PersistenceException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "MyBatis exception: " + e);
        } finally {
            ss.close();
        }
    }

    /**
     * Updates a logbook in the database.
     *
     * @param logbook Logbook
     * @throws CFException wrapping an SQLException
     */
    public static Logbook updateLogbook(String name, Logbook logbook) throws CFException {
        SqlSession ss = ssf.openSession();

        try {
            List<Long> newestVersionIds = new ArrayList<Long>();
            List<Long> ids = new ArrayList<Long>();

            // Get logbook id
            Long pid = LogbookManager.findLogbook(logbook.getName()).getId();

            if (pid == null) {
                throw new CFException(Response.Status.NOT_FOUND, 
                        "Logbook '" + logbook.getName() + "' could not be updated: Logbook does not exist");
            }

            Logbook p = LogbookManager.findLogbook(logbook.getName());
            String logbookOwner = p.getOwner();

            if ((name != null && !name.equals(logbook.getName())) || (logbook.getOwner() != null && !logbookOwner.equals(logbook.getOwner()))) {
                HashMap<String, Object> hm = new HashMap<String, Object>();
                hm.put("name", logbook.getName());
                hm.put("owner", logbook.getOwner());
                hm.put("id", pid);
                ss.update("mappings.LogbookMapper.updateLogbook", hm);
            }
            
            if (logbook.getLogs() == null) {
                return LogbookManager.findLogbook(logbook.getName());
            }

            for (Log log : logbook.getLogs().getLogs()) {
                if (log.getVersion() > 0) {
                    newestVersionIds.add(log.getId());
                } else {
                    ids.add(log.getId());
                }
            }

            if (!newestVersionIds.isEmpty()) {
                ArrayList<Log> logs = (ArrayList<Log>) ss.selectList("mappings.LogMapping.getChildrenIds", newestVersionIds);
                if (logs.isEmpty()) {
                    throw new CFException(Response.Status.NOT_FOUND,
                            "Logbook '" + logbook.getName() + "' could not be updated: "
                            + "No parent logs could be found");
                }

                for (Log log : logs) {
                    ids.add(log.getId());
                }
            }

            if (ids.isEmpty()) {
                throw new CFException(Response.Status.NOT_FOUND,
                        "Logbook '" + logbook.getName() + "' could not be updated: "
                        + "Logs specified do not exist");
            }

            HashMap<String, Object> hm = new HashMap<String, Object>();
            hm.put("pid", pid);
            hm.put("list", ids);

            ss.update("mappings.LogMapping.updateAsInactive", hm);

            hm.clear();

            hm.put("logidsList", ids);
            hm.put("logbookid", pid);
            hm.put("state", null);

            ss.insert("mappings.LogMapping.logsLogbooksEntryFromList", hm);

            ss.commit();
            
            // Return the logbook now that all the changes have been made
            return LogbookManager.findLogbook(logbook.getName());
        } catch (PersistenceException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "MyBatis exception: " + e);
        } finally {
            ss.close();
        }
    }

    /**
     * Updates a tag in the database, adding it to all logs in <tt>tag</tt>.
     *
     * @param tag Tag
     * @throws CFException wrapping an SQLException
     */
    public static Tag updateTag(String name, Tag tag) throws CFException {
        SqlSession ss = ssf.openSession();

        try {
            List<Long> newestVersionIds = new ArrayList<Long>();
            List<Long> ids = new ArrayList<Long>();

            // Get logbook id
            Long pid = TagManager.findTag(tag.getName()).getId();
            
            if (pid == null) {
                throw new CFException(Response.Status.NOT_FOUND, 
                        "Tag '" + tag.getName() + "' could not be updated: Does not exist");
            }

            if (name != null && !name.equals(tag.getName())) {
                HashMap<String, Object> hm = new HashMap<String, Object>();
                hm.put("name", tag.getName());
                hm.put("id", pid);
                ss.update("mappings.TagMapper.updateTag", hm);
            }
            
            Tag t = TagManager.findTag(tag.getName());
            if(t == null){
                return null;
            }
            
            if (tag.getLogs() == null) {
                return t;
            }
            if (tag.getLogs().getLogs().isEmpty()) {
                return t;
            }
            
            for (Log log : tag.getLogs().getLogs()) {
                if (log.getVersion() > 0) {
                    newestVersionIds.add(log.getId());
                } else {
                    ids.add(log.getId());
                }
            }

            if (!newestVersionIds.isEmpty()) {
                ArrayList<Log> logs = (ArrayList<Log>) ss.selectList("mappings.LogMapping.getChildrenIds", newestVersionIds);
                if (logs.isEmpty()) {
                    throw new CFException(Response.Status.NOT_FOUND,
                            "Tag '" + tag.getName() + "' could not be updated: "
                            + "No parent logs could be found");
                }

                for (Log log : logs) {
                    ids.add(log.getId());
                }
            }

            if (ids.isEmpty()) {
                throw new CFException(Response.Status.NOT_FOUND,
                        "Tag '" + tag.getName() + "' could not be updated: "
                        + "Logs specified do not exist");
            }

            HashMap<String, Object> hm = new HashMap<String, Object>();
            hm.put("pid", pid);
            hm.put("list", ids);

            ss.update("mappings.LogMapping.updateAsInactive", hm);

            hm.clear();

            hm.put("logidsList", ids);
            hm.put("logbookid", pid);
            hm.put("state", null);

            ss.insert("mappings.LogMapping.logsLogbooksEntryFromList", hm);

            ss.commit();
            
            // Return new tag now that the logs have been added
            return TagManager.findTag(tag.getName());
        } catch (PersistenceException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "MyBatis exception: " + e);
        } finally {
            ss.close();
        }
    }

    /**
     * Updates the <tt>tag</tt> in the database, adding it to the single log <tt>logId</tt>.
     *
     * @param tag name of tag to add
     * @param logId id of log to add tag to
     * @throws CFException wrapping an SQLException
     */
    public static Tag updateTag(String tag, Long logId) throws CFException {
        SqlSession ss = ssf.openSession();

        try {
            List<Long> newestVersionIds = new ArrayList<Long>();
            List<Long> ids = new ArrayList<Long>();
            
            Logs logs = new Logs(new Log(logId));

            // Get log id
            Long pid = TagManager.findTag(tag).getId();
            if (pid == null) {
                throw new CFException(Response.Status.NOT_FOUND, 
                        "Tag '" + tag + "' could not be updated: Does not exist");
            }
             
            if (logs == null) {
                return null;
            }
            if (logs.getLogs().isEmpty()) {
                return null;
            }
            
            for (Log log : logs.getLogs()) {
                if (log.getVersion() > 0) {
                    newestVersionIds.add(log.getId());
                } else {
                    ids.add(log.getId());
                }
            }

            if (!newestVersionIds.isEmpty()) {
                ArrayList<Log> logs_returned = (ArrayList<Log>) ss.selectList("mappings.LogMapping.getChildrenIds", newestVersionIds);
                if (logs_returned.isEmpty()) {
                    throw new CFException(Response.Status.NOT_FOUND,
                            "Tag '" + tag + "' could not be updated: "
                            + "No parent logs could be found");
                }

                for (Log log : logs_returned) {
                    ids.add(log.getId());
                }
            }

            if (ids.isEmpty()) {
                throw new CFException(Response.Status.NOT_FOUND,
                        "Tag '" + tag + "' could not be updated: "
                        + "Logs specified do not exist");
            }

            HashMap<String, Object> hm = new HashMap<String, Object>();

            hm.put("logidsList", ids);
            hm.put("logbookid", pid);
            hm.put("state", null);

            ss.insert("mappings.LogMapping.logsLogbooksEntryFromList", hm);

            ss.commit();
            
            // Return new tag now that the new log have been added
            return TagManager.findTag(tag);
        } catch (PersistenceException e) {
            throw new CFException(Response.Status.INTERNAL_SERVER_ERROR,
                    "MyBatis exception: " + e);
        } finally {
            ss.close();
        }
    }
}
