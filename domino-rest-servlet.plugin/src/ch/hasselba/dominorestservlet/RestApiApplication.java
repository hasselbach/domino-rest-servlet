package ch.hasselba.dominorestservlet;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

public class RestApiApplication extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(RestApiServlet.class);
		return classes;
	}

	@Override
	public Set<Object> getSingletons() {

		ObjectMapper objMapper = new ObjectMapper();
		
		// register AnnotationProcessors: Jackson & JAXB
		AnnotationIntrospector primary = new JacksonAnnotationIntrospector();
		AnnotationIntrospector secondary = new JaxbAnnotationIntrospector(objMapper.getTypeFactory());
		AnnotationIntrospector pair = AnnotationIntrospector.pair(primary, secondary);
		
		objMapper.getDeserializationConfig().withInsertedAnnotationIntrospector(pair);
		objMapper.getSerializationConfig().withInsertedAnnotationIntrospector(pair);
				
		// globally disable empty values
		objMapper.setSerializationInclusion(Include.NON_EMPTY);

		// add mapper to JAXB
		JacksonJaxbJsonProvider jaxbProvider = new JacksonJaxbJsonProvider();
		jaxbProvider.setMapper(objMapper);
		Set<Object> s = new HashSet<Object>();
		s.add(jaxbProvider);
		return s;
	}

}