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
import org.fabric3.fabric.container.command.AttachWireCommand;
import org.fabric3.fabric.container.command.ConnectionCommand;
import org.fabric3.fabric.container.command.DetachWireCommand;
import org.fabric3.spi.container.executor.CommandExecutorRegistry;
import org.fabric3.spi.container.component.Component;
import org.fabric3.spi.container.component.ComponentManager;

/**
 *
 */
public class ConnectionCommandExecutorTestCase extends TestCase {

    @SuppressWarnings({"unchecked"})
    public void testExecute() throws Exception {
        URI uri = URI.create("component");

        CommandExecutorRegistry registry = EasyMock.createStrictMock(CommandExecutorRegistry.class);
        registry.execute(EasyMock.isA(DetachWireCommand.class));
        registry.execute(EasyMock.isA(AttachWireCommand.class));

        Component component = EasyMock.createStrictMock(Component.class);
        component.startUpdate();
        component.endUpdate();
        ComponentManager manager = EasyMock.createMock(ComponentManager.class);
        EasyMock.expect(manager.getComponent(uri)).andReturn(component);

        EasyMock.replay(registry, manager, component);

        ConnectionCommandExecutor executor = new ConnectionCommandExecutor(manager, registry);
        ConnectionCommand command = new ConnectionCommand(uri);
        command.add(new AttachWireCommand());
        command.add(new DetachWireCommand());
        executor.execute(command);
        EasyMock.verify(registry, manager, component);
    }

}
