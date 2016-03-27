package com.amannmalik.service.barebones;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
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
import io.undertow.servlet.api.ServletContainer;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;


/**
 * Created by Amann on 8/11/2015.
 */
public class Server {


    public static void main(String[] args) throws ServletException {

        ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.DEBUG);

        Undertow server = Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler(getHandler())
                .build();
        server.start();

    }

    private static PathHandler getHandler() throws ServletException {
        ClassLoader contextClassLoader = Server.class.getClassLoader();

        ClassPathResourceManager publicResources = new ClassPathResourceManager(contextClassLoader, "public");

        //ResteasyDeployment resteasyDeployment = new ResteasyDeployment();
        //resteasyDeployment.setApplicationClass(ExampleResourceApplication.class.getName());
        //resteasyDeployment.setInjectorFactoryClass(CdiInjectorFactory.class.getName());

       DeploymentInfo deployment = Servlets.deployment()
                .setClassLoader(contextClassLoader)
               .setContextPath("/")
                .setDeploymentName("service.war")
                .addListener(Servlets.listener(org.jboss.weld.environment.servlet.Listener.class))

               .addServlets(
                       Servlets.servlet(HealthServlet.class)
                               .setLoadOnStartup(1)
                               .addMapping("/health")
               )

//                       Servlets.servlet(org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher.class)
//                               .setLoadOnStartup(1)
//                               .setAsyncSupported(true)
//                               .addInitParam("resteasy.servlet.mapping.prefix", "/api")
//                               .addMapping("/api/*")

               //.addServletContextAttribute(ResteasyDeployment.class.getName(), resteasyDeployment);
;
        ServletContainer container = Servlets.defaultContainer();
        DeploymentManager deploymentManager = container.addDeployment(deployment);
        deploymentManager.deploy();
        HttpHandler serviceHandler = deploymentManager.start();
        return Handlers.path(Handlers.resource(publicResources)).addPrefixPath("/health", serviceHandler);
    }



}
