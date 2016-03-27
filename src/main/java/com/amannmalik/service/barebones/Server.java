package com.amannmalik.service.barebones;

import com.amannmalik.service.barebones.endpoint.ExampleResourceApplication;
import com.amannmalik.service.barebones.endpoint.HealthServlet;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import org.jboss.weld.environment.servlet.EnhancedListener;
import org.jboss.weld.environment.servlet.Listener;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Created by Amann on 8/11/2015.
 */
public class Server {

    public static void main(String[] args) throws ServletException {

        DeploymentInfo servletBuilder = Servlets.deployment()
                .setClassLoader(Server.class.getClassLoader())
                .setResourceManager(new ClassPathResourceManager(Server.class.getClassLoader()))
                .setContextPath("/services")
                .setDeploymentName("services.war")
                .addListeners(
                        Servlets.listener(Listener.class)
                )
                .addServlets(
                        Servlets.servlet(org.glassfish.jersey.servlet.ServletContainer.class)
                                .setLoadOnStartup(1)
                                .addMapping("/api/*")
                                .addInitParam("javax.ws.rs.Application", ExampleResourceApplication.class.getName())
                );


        DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
        manager.deploy();
        HttpHandler servletHandler = manager.start();
        PathHandler path = Handlers.path(Handlers.redirect("/services"))
                .addPrefixPath("/services", servletHandler);

        Undertow server = Undertow.builder().addHttpListener(8080, "localhost").setHandler(path).build();

        server.start();

    }


}
