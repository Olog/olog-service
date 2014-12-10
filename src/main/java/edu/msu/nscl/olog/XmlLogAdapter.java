/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog;

import java.util.List;
import java.util.ListIterator;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;

/**
 *
 * @author berryman
 */
public class XmlLogAdapter extends XmlAdapter<Logs, Logs> {

    @Override
    public Logs marshal(Logs logs) throws Exception {

        return null;
    }

    @Override
    public Logs unmarshal(Logs logs) throws Exception {
        ListIterator<Log> iterator = logs.getLogs().listIterator();
        while (iterator.hasNext()) {
            Log log = iterator.next();
            log.setId(log.getEntryId());
            log.setEntry(null);
            iterator.set(log);
        }
        return logs;
    }
}
