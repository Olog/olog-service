/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog.entity;

import java.util.HashSet;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author berryman
 */
public class XmlLogAdapter extends XmlAdapter<HashSet<Log>, HashSet<Log>> {

    @Override
    public HashSet<Log> marshal(HashSet<Log> logs) throws Exception {

        return null;
    }

    @Override
    public HashSet<Log> unmarshal(HashSet<Log> logs) throws Exception {
        HashSet<Log> newLogs = new HashSet<Log>(logs);
        for(Log log :logs){
            newLogs.remove(log);
            log.setId(log.getEntryId());
            log.setEntry(null);
            newLogs.add(log);
        }
        return newLogs;
    }
}
