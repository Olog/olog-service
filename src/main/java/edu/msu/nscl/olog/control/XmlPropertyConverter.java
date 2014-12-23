/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.msu.nscl.olog.control;

import edu.msu.nscl.olog.OlogException;
import edu.msu.nscl.olog.boundry.AttributeManager;
import edu.msu.nscl.olog.boundry.PropertyManager;
import edu.msu.nscl.olog.entity.Attribute;
import edu.msu.nscl.olog.entity.LogAttribute;
import edu.msu.nscl.olog.entity.Property;
import edu.msu.nscl.olog.entity.XmlProperty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import org.dozer.DozerConverter;
import org.dozer.Mapper;
import org.dozer.MapperAware;

/**
 *
 * @author berryman
 */
public class XmlPropertyConverter extends DozerConverter<Collection, Set> implements MapperAware {

    private Mapper mapper;

    public XmlPropertyConverter() {
        super(Collection.class, Set.class);
    }

    @Override
    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Set convertTo(Collection a, Set b) {
        Set<LogAttribute> logattrs = new HashSet<LogAttribute>();
        Long i = 0L;
        Iterator<XmlProperty> iter = a.iterator();
        while (iter.hasNext()) {
            try {
                XmlProperty p = iter.next();
                Property prop = PropertyManager.findProperty(p.getName());
                if (prop != null) {
                    for (Map.Entry<String, String> att : p.getAttributes().entrySet()) {
                        Attribute newAtt = AttributeManager.findAttribute(prop, att.getKey());
                        if (newAtt != null) {
                            LogAttribute logattr = new LogAttribute();
                            logattr.setAttribute(newAtt);
                            logattr.setAttributeId(newAtt.getId());
                            logattr.setValue(att.getValue());
                            logattr.setGroupingNum(i);
                            logattrs.add(logattr);
                        } else {
                            throw new OlogException(Response.Status.NOT_FOUND,
                                    "Log entry  property attribute:" + prop.getName() + newAtt.getName() + " does not exists.");
                        }
                    }
                    i++;
                } else {
                    throw new OlogException(Response.Status.NOT_FOUND,
                            "Log entry prop:" + prop.getName() + " does not exists.");
                }
            } catch (OlogException ex) {
                Logger.getLogger(XmlPropertyConverter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return logattrs;
    }

    @Override
    public Collection convertFrom(Set b, Collection a) {
        Iterator<LogAttribute> iter = b.iterator();
        Collection<XmlProperty> xmlProperties = new ArrayList<XmlProperty>();
        while (iter.hasNext()) {
            XmlProperty xmlProperty = new XmlProperty();
            Map<String, String> map = new HashMap<String, String>();
            LogAttribute logattr = iter.next();
            Attribute attr = logattr.getAttribute();
            xmlProperty.setName(attr.getProperty().getName());
            xmlProperty.setId(attr.getProperty().getId());
            for (XmlProperty prevXmlProperty : xmlProperties) {
                if (prevXmlProperty.getId().equals(xmlProperty.getId())) {
                    map = prevXmlProperty.getAttributes();
                }
            }
            map.put(attr.getName(), logattr.getValue());
            xmlProperty.setAttributes(map);
            xmlProperties.add(xmlProperty);
        }
        return xmlProperties;
    }

}
