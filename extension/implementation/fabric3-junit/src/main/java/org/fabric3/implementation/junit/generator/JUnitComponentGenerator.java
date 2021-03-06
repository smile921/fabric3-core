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
package org.fabric3.implementation.junit.generator;

import java.net.URI;

import org.fabric3.api.model.type.component.Component;
import org.fabric3.api.model.type.component.Scope;
import org.fabric3.api.model.type.contract.DataType;
import org.fabric3.api.model.type.java.Injectable;
import org.fabric3.api.model.type.java.InjectableType;
import org.fabric3.api.model.type.java.InjectingComponentType;
import org.fabric3.implementation.java.generator.JavaGenerationHelper;
import org.fabric3.implementation.java.provision.JavaComponentDefinition;
import org.fabric3.implementation.java.provision.JavaConnectionSourceDefinition;
import org.fabric3.implementation.java.provision.JavaConnectionTargetDefinition;
import org.fabric3.implementation.java.provision.JavaWireSourceDefinition;
import org.fabric3.implementation.junit.model.JUnitImplementation;
import org.fabric3.implementation.junit.provision.JUnitWireTargetDefinition;
import org.fabric3.implementation.pojo.generator.GenerationHelper;
import org.fabric3.implementation.pojo.provision.ImplementationManagerDefinition;
import org.fabric3.spi.domain.generator.component.ComponentGenerator;
import org.fabric3.spi.model.instance.LogicalComponent;
import org.fabric3.spi.model.instance.LogicalConsumer;
import org.fabric3.spi.model.instance.LogicalProducer;
import org.fabric3.spi.model.instance.LogicalReference;
import org.fabric3.spi.model.instance.LogicalResourceReference;
import org.fabric3.spi.model.instance.LogicalService;
import org.fabric3.spi.model.physical.PhysicalComponentDefinition;
import org.fabric3.spi.model.physical.PhysicalConnectionSourceDefinition;
import org.fabric3.spi.model.physical.PhysicalConnectionTargetDefinition;
import org.fabric3.spi.model.physical.PhysicalWireSourceDefinition;
import org.fabric3.spi.model.physical.PhysicalWireTargetDefinition;
import org.fabric3.spi.model.type.java.JavaServiceContract;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Reference;

/**
 *
 */
@EagerInit
public class JUnitComponentGenerator implements ComponentGenerator<LogicalComponent<JUnitImplementation>> {
    private GenerationHelper helper;
    private JavaGenerationHelper javaHelper;

    public JUnitComponentGenerator(@Reference GenerationHelper helper, @Reference JavaGenerationHelper javaHelper) {
        this.helper = helper;
        this.javaHelper = javaHelper;
    }

    public PhysicalComponentDefinition generate(LogicalComponent<JUnitImplementation> component) {

        Component<JUnitImplementation> definition = component.getDefinition();
        JUnitImplementation implementation = definition.getImplementation();
        InjectingComponentType type = implementation.getComponentType();
        Scope scope = type.getScope();

        ImplementationManagerDefinition managerDefinition = new ImplementationManagerDefinition();
        managerDefinition.setReinjectable(Scope.COMPOSITE == scope);
        managerDefinition.setConstructor(type.getConstructor());
        managerDefinition.setInitMethod(type.getInitMethod());
        managerDefinition.setDestroyMethod(type.getDestroyMethod());
        managerDefinition.setImplementationClass(implementation.getImplementationClass());
        helper.processInjectionSites(type, managerDefinition);

        JavaComponentDefinition physical = new JavaComponentDefinition();

        physical.setScope(scope);
        physical.setManagerDefinition(managerDefinition);
        helper.processPropertyValues(component, physical);
        return physical;
    }

    public PhysicalWireSourceDefinition generateSource(LogicalReference reference) {
        URI uri = reference.getUri();
        JavaServiceContract serviceContract = (JavaServiceContract) reference.getDefinition().getServiceContract();

        JavaWireSourceDefinition wireDefinition = new JavaWireSourceDefinition();
        wireDefinition.setUri(uri);
        wireDefinition.setInjectable(new Injectable(InjectableType.REFERENCE, uri.getFragment()));
        wireDefinition.setInterfaceClass(serviceContract.getInterfaceClass());

        // assume for now that any wire from a JUnit component can be optimized
        wireDefinition.setOptimizable(true);

        if (reference.getDefinition().isKeyed()) {
            wireDefinition.setKeyed(true);
            DataType type = reference.getDefinition().getKeyDataType();
            String className = type.getType().getName();
            wireDefinition.setKeyClassName(className);
        }

        return wireDefinition;
    }

    public PhysicalWireSourceDefinition generateCallbackSource(LogicalService service) {
        throw new UnsupportedOperationException();
    }

    public PhysicalConnectionSourceDefinition generateConnectionSource(LogicalProducer producer) {
        JavaConnectionSourceDefinition definition = new JavaConnectionSourceDefinition();
        javaHelper.generateConnectionSource(definition, producer);
        return definition;
    }

    public PhysicalConnectionTargetDefinition generateConnectionTarget(LogicalConsumer consumer) {
        JavaConnectionTargetDefinition definition = new JavaConnectionTargetDefinition();
        javaHelper.generateConnectionTarget(definition, consumer);
        return definition;
    }

    public PhysicalWireSourceDefinition generateResourceSource(LogicalResourceReference<?> resourceReference) {
        URI uri = resourceReference.getUri();
        JavaServiceContract serviceContract = (JavaServiceContract) resourceReference.getDefinition().getServiceContract();

        JavaWireSourceDefinition wireDefinition = new JavaWireSourceDefinition();
        wireDefinition.setUri(uri);
        wireDefinition.setInjectable(new Injectable(InjectableType.RESOURCE, uri.getFragment()));
        wireDefinition.setInterfaceClass(serviceContract.getInterfaceClass());
        return wireDefinition;
    }

    public PhysicalWireTargetDefinition generateTarget(LogicalService service) {
        JUnitWireTargetDefinition wireDefinition = new JUnitWireTargetDefinition();
        wireDefinition.setUri(service.getUri());
        return wireDefinition;
    }
}
