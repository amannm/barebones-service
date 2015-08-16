package com.amannmalik.service.barebones.adapter;

import org.jboss.resteasy.core.InjectorFactoryImpl;
import org.jboss.resteasy.core.ValueInjector;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.spi.*;
import org.jboss.resteasy.spi.metadata.Parameter;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceConstructor;
import org.jboss.resteasy.spi.metadata.ResourceLocator;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

/**
 * @author Jozef Hartinger
 */
@SuppressWarnings("rawtypes")
public class CdiInjectorFactory implements InjectorFactory {
    private static final Logger log = Logger.getLogger(CdiInjectorFactory.class);
    public static final String BEAN_MANAGER_ATTRIBUTE_PREFIX = "org.jboss.weld.environment.servlet.";
    private BeanManager manager;
    private InjectorFactory delegate = new InjectorFactoryImpl();
    private ResteasyCdiExtension extension;
    private Map<Class<?>, Type> sessionBeanInterface;

    public CdiInjectorFactory() {
        this.manager = CDI.current().getBeanManager();
        this.extension = lookupResteasyCdiExtension();
        sessionBeanInterface = extension.getSessionBeanInterface();
    }

    @Override
    public ValueInjector createParameterExtractor(Parameter parameter, ResteasyProviderFactory providerFactory) {
        return delegate.createParameterExtractor(parameter, providerFactory);
    }

    @Override
    public MethodInjector createMethodInjector(ResourceLocator method, ResteasyProviderFactory factory) {
        return delegate.createMethodInjector(method, factory);
    }

    @Override
    public PropertyInjector createPropertyInjector(ResourceClass resourceClass, ResteasyProviderFactory providerFactory) {
        return new CdiPropertyInjector(delegate.createPropertyInjector(resourceClass, providerFactory), resourceClass.getClazz(), sessionBeanInterface, manager);
    }

    @Override
    public ConstructorInjector createConstructor(ResourceConstructor constructor, ResteasyProviderFactory providerFactory) {
        Class<?> clazz = constructor.getConstructor().getDeclaringClass();

        ConstructorInjector injector = cdiConstructor(clazz);
        if (injector != null) return injector;

        log.debug("No CDI beans found for {0}. Using default ConstructorInjector.", clazz);
        return delegate.createConstructor(constructor, providerFactory);
    }

    @Override
    public ConstructorInjector createConstructor(Constructor constructor, ResteasyProviderFactory factory) {
        Class<?> clazz = constructor.getDeclaringClass();

        ConstructorInjector injector = cdiConstructor(clazz);
        if (injector != null) return injector;

        log.debug("No CDI beans found for {0}. Using default ConstructorInjector.", clazz);
        return delegate.createConstructor(constructor, factory);
    }


    protected ConstructorInjector cdiConstructor(Class<?> clazz) {
        if (!manager.getBeans(clazz).isEmpty()) {
            log.debug("Using CdiConstructorInjector for class {0}.", clazz);
            return new CdiConstructorInjector(clazz, manager);
        }

        if (sessionBeanInterface.containsKey(clazz)) {
            Type intfc = sessionBeanInterface.get(clazz);
            log.debug("Using {0} for lookup of Session Bean {1}.", intfc, clazz);
            return new CdiConstructorInjector(intfc, manager);
        }

        return null;
    }

    public PropertyInjector createPropertyInjector(Class resourceClass, ResteasyProviderFactory factory) {
        return new CdiPropertyInjector(delegate.createPropertyInjector(resourceClass, factory), resourceClass, sessionBeanInterface, manager);
    }

    public ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, Class type, Type genericType, Annotation[] annotations, ResteasyProviderFactory factory) {
        return delegate.createParameterExtractor(injectTargetClass, injectTarget, type, genericType, annotations, factory);
    }

    public ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, Class type,
                                                  Type genericType, Annotation[] annotations, boolean useDefault, ResteasyProviderFactory factory) {
        return delegate.createParameterExtractor(injectTargetClass, injectTarget, type, genericType, annotations, useDefault, factory);
    }


    /**
     * Lookup ResteasyCdiExtension instance that was instantiated during CDI bootstrap
     *
     * @return ResteasyCdiExtension instance
     */
    private ResteasyCdiExtension lookupResteasyCdiExtension() {
        Set<Bean<?>> beans = manager.getBeans(ResteasyCdiExtension.class);
        Bean<?> bean = manager.resolve(beans);
        if (bean == null) {
            throw new IllegalStateException("Unable to obtain ResteasyCdiExtension instance.");
        }
        CreationalContext<?> context = manager.createCreationalContext(bean);
        return (ResteasyCdiExtension) manager.getReference(bean, ResteasyCdiExtension.class, context);
    }
}
