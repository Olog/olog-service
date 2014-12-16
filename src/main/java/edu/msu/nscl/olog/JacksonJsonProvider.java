/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */

   package edu.msu.nscl.olog;

import edu.msu.nscl.olog.entity.Log;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;


/**
 * 
 *
 * @author Eric Berryman
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JacksonJsonProvider extends JacksonJaxbJsonProvider {

    private static ObjectMapper objectMapper = null;

    public JacksonJsonProvider() throws Exception {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            //objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
           // objectMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
            objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
            objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.getTypeFactory().constructCollectionType(List.class, Log.class);
            
            objectMapper.enable(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME);

        }
        super.setMapper(objectMapper);
    }

    public ObjectMapper getContext(Class<?> objectType) {
        return objectMapper;
    }
}
