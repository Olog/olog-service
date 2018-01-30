package edu.msu.nscl.olog;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import edu.msu.nscl.olog.entity.Log;
import edu.msu.nscl.olog.entity.Logbook;
import edu.msu.nscl.olog.entity.Logbooks;
import edu.msu.nscl.olog.entity.Tag;
import edu.msu.nscl.olog.entity.Tags;
import edu.msu.nscl.olog.entity.XmlAttachment;
import edu.msu.nscl.olog.entity.XmlAttachments;
import edu.msu.nscl.olog.entity.XmlLog;
import edu.msu.nscl.olog.entity.XmlLogs;
import edu.msu.nscl.olog.entity.XmlProperties;
import edu.msu.nscl.olog.entity.XmlProperty;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ContextResolver;


/**
 * 
 *
 * @author Eric Berryman
 */
@Provider
public class MyJAXBContextResolver implements ContextResolver<ObjectMapper> {
    final ObjectMapper defaultObjectMapper;
    final ObjectMapper combinedObjectMapper;
    final Set<Class> classes = new HashSet<Class>();

    public MyJAXBContextResolver() {
        defaultObjectMapper = createDefaultMapper();
        combinedObjectMapper = createCombinedObjectMapper();
        classes.add(XmlLogs.class);
        classes.add(XmlLog.class);
        classes.add(XmlProperties.class);
        classes.add(XmlProperty.class);
        classes.add(Logbooks.class);
        classes.add(Logbook.class);
        classes.add(Tags.class);
        classes.add(Tag.class);
        classes.add(XmlAttachments.class);
        classes.add(XmlAttachment.class);

    }

    @Override
    public ObjectMapper getContext(final Class<?> type) {

        if (classes.contains(type)) {
            return combinedObjectMapper;
        } else {
            return defaultObjectMapper;
        }
    }

    private static ObjectMapper createCombinedObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper
                .configure(SerializationFeature.WRAP_ROOT_VALUE, false)
                .configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.enable(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME);
        objectMapper.setAnnotationIntrospector(createJaxbJacksonAnnotationIntrospector());
        return objectMapper;
    }

    private static ObjectMapper createDefaultMapper() {
        final ObjectMapper result = new ObjectMapper();
        result.enable(SerializationFeature.INDENT_OUTPUT);

        return result;
    }

    private static AnnotationIntrospector createJaxbJacksonAnnotationIntrospector() {

        final AnnotationIntrospector jaxbIntrospector = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
        final AnnotationIntrospector jacksonIntrospector = new JacksonAnnotationIntrospector();

        return AnnotationIntrospector.pair(jacksonIntrospector, jaxbIntrospector);
    }

}
