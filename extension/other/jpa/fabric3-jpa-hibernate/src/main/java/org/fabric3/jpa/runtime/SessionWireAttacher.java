/*
 * Fabric3
 * Copyright (c) 2009-2015 Metaform Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Portions originally based on Apache Tuscany 2007
 * licensed under the Apache 2.0 license.
 */
package org.fabric3.jpa.runtime;

import java.net.URI;
import javax.transaction.TransactionManager;

import org.fabric3.spi.container.ContainerException;
import org.oasisopen.sca.annotation.Reference;

import org.fabric3.jpa.api.EntityManagerFactoryResolver;
import org.fabric3.jpa.api.JpaResolutionException;
import org.fabric3.jpa.api.PersistenceOverrides;
import org.fabric3.jpa.provision.SessionWireTargetDefinition;
import org.fabric3.jpa.runtime.proxy.EntityManagerService;
import org.fabric3.jpa.runtime.proxy.MultiThreadedSessionProxyFactory;
import org.fabric3.jpa.runtime.proxy.StatefulSessionProxyFactory;
import org.fabric3.spi.container.builder.component.TargetWireAttacher;
import org.fabric3.spi.classloader.ClassLoaderRegistry;
import org.fabric3.spi.model.physical.PhysicalWireSourceDefinition;
import org.fabric3.spi.container.objectfactory.ObjectFactory;
import org.fabric3.spi.container.wire.Wire;

/**
 *
 */
public class SessionWireAttacher implements TargetWireAttacher<SessionWireTargetDefinition> {
    private EntityManagerFactoryResolver emfResolver;
    private ClassLoaderRegistry registry;
    private TransactionManager tm;
    private EntityManagerService emService;

    /**
     * Constructor.
     *
     * @param emService   the service for creating EntityManagers
     * @param tm          the transaction manager
     * @param emfResolver the EMF builder
     * @param registry    the classloader registry
     */
    public SessionWireAttacher(@Reference EntityManagerService emService,
                               @Reference TransactionManager tm,
                               @Reference EntityManagerFactoryResolver emfResolver,
                               @Reference ClassLoaderRegistry registry) {
        this.emfResolver = emfResolver;
        this.registry = registry;
        this.emService = emService;
        this.tm = tm;
    }

    public ObjectFactory<?> createObjectFactory(SessionWireTargetDefinition definition) throws ContainerException {
        String unitName = definition.getUnitName();
        URI classLoaderId = definition.getClassLoaderId();
        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        try {
            // get the classloader for the entity manager factory
            ClassLoader classLoader = registry.getClassLoader(classLoaderId);
            Thread.currentThread().setContextClassLoader(classLoader);
            // eagerly build the the EntityManagerFactory
            PersistenceOverrides overrides = definition.getOverrides();
            emfResolver.resolve(unitName, overrides, classLoader);
            if (definition.isMultiThreaded()) {
                return new MultiThreadedSessionProxyFactory(unitName, emService, tm);
            } else {
                return new StatefulSessionProxyFactory(unitName, emService, tm);
            }
        } catch (JpaResolutionException e) {
            throw new ContainerException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
        }
    }

    public void attach(PhysicalWireSourceDefinition source, SessionWireTargetDefinition target, Wire wire) throws ContainerException {
        throw new UnsupportedOperationException();
    }

    public void detach(PhysicalWireSourceDefinition source, SessionWireTargetDefinition target) throws ContainerException {
        throw new UnsupportedOperationException();
    }

}