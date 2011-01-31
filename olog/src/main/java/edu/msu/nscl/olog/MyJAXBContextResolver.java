/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */

package edu.msu.nscl.olog;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ContextResolver;
import javax.xml.bind.JAXBContext;

/**
 * Gets inserted into JAXB to configure JSON marshalling.
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
@Provider
public class MyJAXBContextResolver implements ContextResolver<JAXBContext> {

    private JAXBContext context;
    private List<Class<?>> types = Arrays.asList(XmlLogs.class,
            XmlLogbooks.class, XmlTags.class);

    public MyJAXBContextResolver() throws Exception {
        this.context = new JSONJAXBContext(
                JSONConfiguration.mapped()
                .rootUnwrapping(false)
                .build(),
                types.toArray(new Class[types.size()]));
    }

    public JAXBContext getContext(Class<?> objectType) {
        return (types.contains(objectType)) ? context : null;
    }
}
