package com.actian.nevernote.application;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
 
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
/**
 * @author sugan
 * 
 * This is application configuration class, that gets loaded when the application is deployed and it has all the classes that is required.
 *
 */
@ApplicationPath("/api")
public class ApplicationConfig extends Application

{
	
	    public Set<Class<?>> getClasses() {
	        
	        Set<Class<?>> resources = new java.util.HashSet<>();
	        
	        resources.add(org.glassfish.jersey.moxy.json.MoxyJsonFeature.class);
	        
	        //Adding Configuration class and resource.
	        resources.add(com.actian.nevernote.provider.JsonMoxyConfigurationContextResolver.class);
	        resources.add(com.actian.nevernote.resource.NoteBookDetails.class);
	       
	        return resources;
	    }
	    
	    public Set<Object> getSingletons() {
	        return Collections.emptySet();
	    }
	    
	    public Map<String, Object> getProperties() {
	        Map<String, Object> properties = new HashMap<>();
	        properties.put("jersey.config.server.wadl.disableWadl", true);
	        
	      
	        
	        
	        return properties;
	    }

}
