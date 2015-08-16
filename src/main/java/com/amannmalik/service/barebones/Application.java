package com.amannmalik.service.barebones;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.amannmalik.service.barebones.adapter.ResteasyCdiExtension;
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
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;


/**
 * Created by Amann on 8/11/2015.
 */
public class Application {


    public static void main(String[] args) throws ServletException {

        ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.DEBUG);

        WeldContainer container = new Weld()
                .disableDiscovery()
                .addExtension(new ResteasyCdiExtension())
                .addPackage(true, HealthServlet.class).initialize();


        PathHandler path = Handlers.path(getContentProvider());
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler(path)
                .build();

        server.start();
        path.addPrefixPath("/service", getServiceProvider());

    }

    public static HttpHandler getContentProvider() {
        ClassPathResourceManager publicResources = new ClassPathResourceManager(Thread.currentThread().getContextClassLoader(), "public");
        HttpHandler handler = Handlers.resource(publicResources);
        return handler;
    }

    public static HttpHandler getServiceProvider() throws ServletException {

        DeploymentInfo servletDeployment = new DeploymentInfo()
                .setClassLoader(Thread.currentThread().getContextClassLoader())
                .setDeploymentName("Services")
                .setContextPath("/")
//                .addListeners(
//                        Servlets.listener(org.jboss.weld.environment.servlet.Listener.class)
//                )
                ;

        addHealthServlet(servletDeployment);
        addApiServlet(servletDeployment);

        ServletContainer container = Servlets.defaultContainer();

        DeploymentManager deploymentManager = container.addDeployment(servletDeployment);
        deploymentManager.deploy();
        HttpHandler handler = deploymentManager.start();
        return handler;
    }

    public static void addHealthServlet(DeploymentInfo deploymentInfo) {
        deploymentInfo.addServlets(
                Servlets.servlet(HealthServlet.class)
                        .setLoadOnStartup(1)
                        .addMapping("/health")
        );
    }

    public static void addApiServlet(DeploymentInfo deploymentInfo) {
        ResteasyDeployment resteasyDeployment = new ResteasyDeployment();
        resteasyDeployment.setInjectorFactoryClass("com.amannmalik.service.barebones.adapter.CdiInjectorFactory");
        resteasyDeployment.setApplicationClass(ExampleResourceApplication.class.getName());
        deploymentInfo.addServlet(
                Servlets.servlet(HttpServlet30Dispatcher.class)
                        .setLoadOnStartup(1)
                        .addMapping("/api/*")
                        .setAsyncSupported(true)
                        .addInitParam("resteasy.servlet.mapping.prefix", "/service/api")
        );
        deploymentInfo.addServletContextAttribute("org.jboss.resteasy.spi.ResteasyDeployment", resteasyDeployment);
    }


}
