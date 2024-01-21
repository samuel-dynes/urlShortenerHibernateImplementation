package org.shortener.configurations;


import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Configures the JAX-RS application and sets the base path for the REST API.
 */
@ApplicationPath("/api")
public class ApplicationConfig extends Application {

    /**
     * Retrieves the set of resource classes to be registered in the JAX-RS application.
     *
     * @return Set of resource classes.
     */
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(org.shortener.resources.UrlResource.class);
        return classes;
    }
}
