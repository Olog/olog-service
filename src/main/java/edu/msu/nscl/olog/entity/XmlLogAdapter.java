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
public class XmlLogAdapter extends XmlAdapter<HashSet<XmlLog>, HashSet<XmlLog>> {

    @Override
    public HashSet<XmlLog> marshal(HashSet<XmlLog> logs) throws Exception {

        return null;
    }

    @Override
    public HashSet<XmlLog> unmarshal(HashSet<XmlLog> logs) throws Exception {
        HashSet<XmlLog> newLogs = new HashSet<XmlLog>(logs);
        for(XmlLog log :logs){
            newLogs.remove(log);
            log.setId(log.getId());
            newLogs.add(log);
        }
        return newLogs;
    }
}
