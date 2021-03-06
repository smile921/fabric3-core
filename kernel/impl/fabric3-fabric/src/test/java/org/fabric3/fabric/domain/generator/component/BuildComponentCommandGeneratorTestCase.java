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
 *
 * Portions originally based on Apache Tuscany 2007
 * licensed under the Apache 2.0 license.
 */
package org.fabric3.fabric.domain.generator.component;

import java.net.URI;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.fabric3.api.model.type.component.Component;
import org.fabric3.api.model.type.component.ComponentType;
import org.fabric3.api.model.type.component.Implementation;
import org.fabric3.fabric.container.command.BuildComponentCommand;
import org.fabric3.fabric.domain.generator.GeneratorRegistry;
import org.fabric3.spi.classloader.ClassLoaderRegistry;
import org.fabric3.spi.domain.generator.component.ComponentGenerator;
import org.fabric3.spi.model.instance.LogicalComponent;
import org.fabric3.spi.model.instance.LogicalState;
import org.fabric3.spi.model.physical.PhysicalComponentDefinition;

/**
 *
 */
public class BuildComponentCommandGeneratorTestCase extends TestCase {
    private ClassLoaderRegistry classLoaderRegistry;

    @SuppressWarnings({"unchecked"})
    public void testBuild() throws Exception {
        ComponentGenerator<LogicalComponent<MockImplementation>> componentGenerator = EasyMock.createMock(ComponentGenerator.class);
        EasyMock.expect(componentGenerator.generate(EasyMock.isA(LogicalComponent.class))).andReturn(new MockDefinition());
        GeneratorRegistry registry = EasyMock.createMock(GeneratorRegistry.class);
        EasyMock.expect(registry.getComponentGenerator(EasyMock.eq(MockImplementation.class))).andReturn(componentGenerator);
        EasyMock.replay(registry, componentGenerator);

        BuildComponentCommandGenerator generator = new BuildComponentCommandGenerator(registry, classLoaderRegistry);

        Component<MockImplementation> definition = new Component<>("component", new MockImplementation());
        LogicalComponent<MockImplementation> component = new LogicalComponent<>(URI.create("component"), definition, null);

        BuildComponentCommand command = generator.generate(component).get();
        assertNotNull(command.getDefinition());
        EasyMock.verify(registry, componentGenerator);
    }

    @SuppressWarnings({"unchecked"})
    public void testNoBuild() throws Exception {
        GeneratorRegistry registry = EasyMock.createMock(GeneratorRegistry.class);
        EasyMock.replay(registry);

        BuildComponentCommandGenerator generator = new BuildComponentCommandGenerator(registry, classLoaderRegistry);

        Component<MockImplementation> definition = new Component<>("component", new MockImplementation());
        LogicalComponent<MockImplementation> component = new LogicalComponent<>(URI.create("component"), definition, null);
        component.setState(LogicalState.PROVISIONED);

        assertFalse(generator.generate(component).isPresent());
        EasyMock.verify(registry);
    }

    private class MockImplementation extends Implementation<ComponentType> {
        public String getType() {
            return null;
        }
    }

    private class MockDefinition extends PhysicalComponentDefinition {
        private static final long serialVersionUID = 1097054400657294542L;
    }

    public void setUp() throws Exception {
        classLoaderRegistry = EasyMock.createMock(ClassLoaderRegistry.class);
        EasyMock.expect(classLoaderRegistry.getClassLoader(EasyMock.isA(URI.class))).andReturn(getClass().getClassLoader()).anyTimes();
    }

}
