package com.amannmalik.service.barebones.endpoint;

/**
 * Created by Amann on 8/11/2015.
 */

import io.swagger.jaxrs.config.BeanConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class ExampleResourceApplication extends Application {

    public ExampleResourceApplication() {
        BeanConfig config = new BeanConfig();

        config.setTitle("Barebones Service API");
        config.setDescription("A barebones implementation of a service");
        config.setContact("amannmalik@gmail.com");
        config.setLicense("All Rights Reserved");

        config.setVersion("1");
        config.setBasePath("/api");
        config.setResourcePackage("com.amannmalik.service.barebones.endpoint");

        config.setScan(true);
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();

        resources.add(ExampleResource.class);
        resources.add(CrossOriginResourceSharingFilter.class);
        resources.add(ExampleData.class);

        resources.add(io.swagger.jaxrs.listing.ApiListingResource.class);
        resources.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);

        return resources;
    }

}

