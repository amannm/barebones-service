package com.amannmalik.service.barebones;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.amannmalik.service.barebones.endpoint.ExampleResourceApplication;
import com.amannmalik.service.barebones.endpoint.HealthServlet;
import com.amannmalik.service.barebones.endpoint.WebsocketEndpoint;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;


/**
 * Created by Amann on 8/11/2015.
 */
public class Server {


    public static void main(String[] args) throws ServletException {

        ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.DEBUG);

        WeldContainer container = new Weld()
                .addExtension(new org.jboss.resteasy.cdi.ResteasyCdiExtension())
                .addPackage(true, Server.class)
                .initialize();

        PathHandler path = Handlers.path(getContentProvider())
                .addPrefixPath("/service", getServiceProvider())
                .addPrefixPath("/socket", getSocketProvider());

        Undertow server = Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler(path)
                .build();

        server.start();

    }

    private static HttpHandler getContentProvider() {

        ClassPathResourceManager publicResources = new ClassPathResourceManager(Thread.currentThread().getContextClassLoader(), "public");
        HttpHandler handler = Handlers.resource(publicResources);
        return handler;

    }

    private static HttpHandler getServiceProvider() throws ServletException {

        DeploymentInfo deployment = new DeploymentInfo()
                .setClassLoader(Thread.currentThread().getContextClassLoader())
                .setDeploymentName("service")
                .setContextPath("/");

        addHealthServlet(deployment);
        addApiServlet(deployment);

        ServletContainer container = Servlets.defaultContainer();

        DeploymentManager deploymentManager = container.addDeployment(deployment);
        deploymentManager.deploy();
        HttpHandler handler = deploymentManager.start();
        return handler;
    }

    private static void addHealthServlet(DeploymentInfo deploymentInfo) {

        deploymentInfo.addServlet(
                Servlets.servlet(HealthServlet.class)
                        .setLoadOnStartup(1)
                        .addMapping("/health")
        );

    }

    private static void addApiServlet(DeploymentInfo deploymentInfo) {

        ResteasyDeployment resteasyDeployment = new ResteasyDeployment();
        resteasyDeployment.setApplicationClass(ExampleResourceApplication.class.getName());
        resteasyDeployment.setInjectorFactoryClass("org.jboss.resteasy.cdi.CdiInjectorFactory");

        deploymentInfo.addServletContextAttribute("org.jboss.resteasy.spi.ResteasyDeployment", resteasyDeployment);
        deploymentInfo.addServlet(
                Servlets.servlet(HttpServlet30Dispatcher.class)
                        .setLoadOnStartup(1)
                        .addMapping("/api/*")
                        .setAsyncSupported(true)
                        .addInitParam("resteasy.servlet.mapping.prefix", "/services/api")
        );

    }

    private static HttpHandler getSocketProvider() throws ServletException {

        DeploymentInfo deployment = new DeploymentInfo()
                .setClassLoader(Thread.currentThread().getContextClassLoader())
                .setDeploymentName("socket")
                .setContextPath("/");

        WebSocketDeploymentInfo deploymentInfo = new WebSocketDeploymentInfo();
        deploymentInfo.addEndpoint(WebsocketEndpoint.class);

        deployment.addServletContextAttribute(WebSocketDeploymentInfo.ATTRIBUTE_NAME, deploymentInfo);

        ServletContainer container = Servlets.defaultContainer();

        DeploymentManager deploymentManager = container.addDeployment(deployment);
        deploymentManager.deploy();
        HttpHandler handler = deploymentManager.start();
        return handler;

    }

}
