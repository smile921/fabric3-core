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
package org.fabric3.implementation.java.runtime;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.function.Supplier;

import org.fabric3.api.host.Fabric3Exception;
import org.fabric3.implementation.java.provision.JavaWireTargetDefinition;
import org.fabric3.implementation.pojo.builder.MethodUtils;
import org.fabric3.implementation.pojo.component.InvokerInterceptor;
import org.fabric3.implementation.pojo.provision.PojoWireSourceDefinition;
import org.fabric3.implementation.pojo.spi.reflection.ReflectionFactory;
import org.fabric3.implementation.pojo.spi.reflection.ServiceInvoker;
import org.fabric3.spi.container.builder.component.TargetWireAttacher;
import org.fabric3.spi.container.component.Component;
import org.fabric3.spi.container.component.ComponentManager;
import org.fabric3.spi.container.wire.InvocationChain;
import org.fabric3.spi.container.wire.Wire;
import org.fabric3.spi.model.physical.PhysicalOperationDefinition;
import org.fabric3.spi.model.physical.PhysicalWireSourceDefinition;
import org.fabric3.spi.util.UriHelper;
import org.oasisopen.sca.annotation.Reference;

/**
 * Attaches and detaches wires from Java components.
 */
public class JavaTargetWireAttacher implements TargetWireAttacher<JavaWireTargetDefinition> {

    private final ComponentManager manager;
    private ReflectionFactory reflectionFactory;

    public JavaTargetWireAttacher(@Reference ComponentManager manager, @Reference ReflectionFactory reflectionFactory) {
        this.manager = manager;
        this.reflectionFactory = reflectionFactory;
    }

    public void attach(PhysicalWireSourceDefinition sourceDefinition, JavaWireTargetDefinition targetDefinition, Wire wire) {
        URI targetName = UriHelper.getDefragmentedName(targetDefinition.getUri());
        Component component = manager.getComponent(targetName);
        if (component == null) {
            throw new Fabric3Exception("Target not found: " + targetName);
        }
        JavaComponent target = (JavaComponent) component;

        Class<?> implementationClass = target.getImplementationClass();
        ClassLoader loader = targetDefinition.getClassLoader();

        // attach the invoker interceptor to forward invocation chains
        for (InvocationChain chain : wire.getInvocationChains()) {
            PhysicalOperationDefinition operation = chain.getPhysicalOperation();
            Method method = MethodUtils.findMethod(sourceDefinition, targetDefinition, operation, implementationClass, loader);
            ServiceInvoker invoker = reflectionFactory.createServiceInvoker(method);
            InvokerInterceptor interceptor;
            if (sourceDefinition instanceof PojoWireSourceDefinition && targetDefinition.getClassLoader().equals(sourceDefinition.getClassLoader())) {
                // if the source is Java and target classloaders are equal, do not set the TCCL
                interceptor = new InvokerInterceptor(invoker, target);
            } else {
                // If the source and target classloaders are not equal, configure the interceptor to set the TCCL to the target classloader
                // when dispatching to a target instance. This guarantees when application code executes, it does so with the TCCL set to the
                // target component's classloader.
                interceptor = new InvokerInterceptor(invoker, target, loader);
            }
            chain.addInterceptor(interceptor);
        }
    }

    public Supplier<?> createSupplier(JavaWireTargetDefinition target) {
        URI targetId = UriHelper.getDefragmentedName(target.getUri());
        JavaComponent targetComponent = (JavaComponent) manager.getComponent(targetId);
        return targetComponent.createSupplier();
    }

}