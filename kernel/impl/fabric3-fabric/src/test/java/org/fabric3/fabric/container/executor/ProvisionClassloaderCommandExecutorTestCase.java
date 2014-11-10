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
package org.fabric3.fabric.container.executor;

import java.net.URI;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.fabric3.fabric.container.builder.classloader.ClassLoaderBuilder;
import org.fabric3.fabric.container.command.ProvisionClassloaderCommand;
import org.fabric3.spi.container.executor.CommandExecutorRegistry;
import org.fabric3.spi.model.physical.PhysicalClassLoaderDefinition;

/**
 *
 */
public class ProvisionClassloaderCommandExecutorTestCase extends TestCase {


    public void testExecute() throws Exception {
        CommandExecutorRegistry registry = EasyMock.createMock(CommandExecutorRegistry.class);
        registry.register(EasyMock.eq(ProvisionClassloaderCommand.class), EasyMock.isA(ProvisionClassloaderCommandExecutor.class));
        ClassLoaderBuilder builder = EasyMock.createMock(ClassLoaderBuilder.class);
        builder.build(EasyMock.isA(PhysicalClassLoaderDefinition.class));
        EasyMock.replay(registry, builder);


        ProvisionClassloaderCommandExecutor executor = new ProvisionClassloaderCommandExecutor(registry, builder);
        executor.init();

        PhysicalClassLoaderDefinition definition = new PhysicalClassLoaderDefinition(URI.create("classloader"), true);
        ProvisionClassloaderCommand command = new ProvisionClassloaderCommand(definition);
        executor.execute(command);

        EasyMock.verify(registry, builder);
    }

}
