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
 */
package f3;

import javax.xml.namespace.QName;

import org.fabric3.api.Namespaces;
import org.fabric3.api.annotation.model.Provides;
import org.fabric3.api.model.type.builder.CompositeBuilder;
import org.fabric3.api.model.type.component.Composite;
import org.fabric3.implementation.system.generator.SystemComponentGenerator;
import org.fabric3.implementation.system.introspection.SystemConstructorHeuristic;
import org.fabric3.implementation.system.introspection.SystemHeuristic;
import org.fabric3.implementation.system.introspection.SystemImplementationIntrospectorImpl;
import org.fabric3.implementation.system.introspection.SystemImplementationLoader;
import org.fabric3.implementation.system.introspection.SystemServiceHeuristic;
import org.fabric3.implementation.system.introspection.SystemUnannotatedHeuristic;
import org.fabric3.implementation.system.provision.SystemComponentDefinition;
import org.fabric3.implementation.system.provision.SystemConnectionSourceDefinition;
import org.fabric3.implementation.system.provision.SystemConnectionTargetDefinition;
import org.fabric3.implementation.system.provision.SystemWireSourceDefinition;
import org.fabric3.implementation.system.provision.SystemWireTargetDefinition;
import org.fabric3.implementation.system.runtime.SystemComponentBuilder;
import org.fabric3.implementation.system.runtime.SystemSourceConnectionAttacher;
import org.fabric3.implementation.system.runtime.SystemSourceWireAttacher;
import org.fabric3.implementation.system.runtime.SystemTargetConnectionAttacher;
import org.fabric3.implementation.system.runtime.SystemTargetWireAttacher;
import org.fabric3.implementation.system.singleton.SingletonComponentGenerator;
import org.fabric3.implementation.system.singleton.SingletonImplementation;
import org.fabric3.implementation.system.singleton.SingletonWireSourceDefinition;
import org.fabric3.implementation.system.singleton.SingletonSourceWireAttacher;
import org.fabric3.implementation.system.singleton.SingletonWireTargetDefinition;
import org.fabric3.implementation.system.singleton.SingletonTargetWireAttacher;
import org.fabric3.spi.model.type.system.SystemComponentDefinitionBuilder;
import org.fabric3.spi.model.type.system.SystemImplementation;
import static org.fabric3.spi.model.type.system.SystemComponentDefinitionBuilder.newBuilder;

/**
 * Provides components for managing system and singleton components.
 */
public class SystemImplementationProvider {

    private static final QName QNAME = new QName(Namespaces.F3, "SystemImplementationComposite");

    @Provides
    public static Composite getComposite() {
        CompositeBuilder compositeBuilder = CompositeBuilder.newBuilder(QNAME);

        addSystemImplementation(compositeBuilder);
        addSingletonImplementation(compositeBuilder);

        return compositeBuilder.build();
    }

    private static void addSingletonImplementation(CompositeBuilder compositeBuilder) {
        compositeBuilder.component(newBuilder(SingletonComponentGenerator.class).key(SingletonImplementation.class.getName()).build());
        compositeBuilder.component(newBuilder(SingletonSourceWireAttacher.class).key(SingletonWireSourceDefinition.class.getName()).build());
        compositeBuilder.component(newBuilder(SingletonTargetWireAttacher.class).key(SingletonWireTargetDefinition.class.getName()).build());
    }

    private static void addSystemImplementation(CompositeBuilder compositeBuilder) {
        compositeBuilder.component(newBuilder(SystemImplementationLoader.class).key(Namespaces.F3_PREFIX + "implementation.system").build());
        compositeBuilder.component(newBuilder(SystemComponentBuilder.class).key(SystemComponentDefinition.class.getName()).build());
        compositeBuilder.component(newBuilder(SystemSourceWireAttacher.class).key(SystemWireSourceDefinition.class.getName()).build());
        compositeBuilder.component(newBuilder(SystemTargetWireAttacher.class).key(SystemWireTargetDefinition.class.getName()).build());
        compositeBuilder.component(newBuilder(SystemSourceConnectionAttacher.class).key(SystemConnectionSourceDefinition.class.getName()).build());
        compositeBuilder.component(newBuilder(SystemTargetConnectionAttacher.class).key(SystemConnectionTargetDefinition.class.getName()).build());

        SystemComponentDefinitionBuilder componentBuilder = newBuilder("SystemImplementationIntrospectorImpl", SystemImplementationIntrospectorImpl.class);
        componentBuilder.key(Namespaces.F3_PREFIX + "implementation.system");
        componentBuilder.reference("heuristic", "SystemHeuristic");
        compositeBuilder.component(componentBuilder.build());

        componentBuilder = newBuilder(SystemHeuristic.class);
        componentBuilder.reference("service", "SystemServiceHeuristic");
        componentBuilder.reference("constructor", "SystemConstructorHeuristic");
        componentBuilder.reference("injection", "SystemUnannotatedHeuristic");
        compositeBuilder.component(componentBuilder.build());

        compositeBuilder.component(newBuilder(SystemServiceHeuristic.class).build());
        compositeBuilder.component(newBuilder(SystemConstructorHeuristic.class).build());
        compositeBuilder.component(newBuilder(SystemUnannotatedHeuristic.class).build());

        compositeBuilder.component(newBuilder(SystemComponentGenerator.class).key(SystemImplementation.class.getName()).build());
    }

}
