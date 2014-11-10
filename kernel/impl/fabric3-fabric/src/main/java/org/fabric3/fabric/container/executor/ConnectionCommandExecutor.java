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

import org.fabric3.fabric.container.command.AttachWireCommand;
import org.fabric3.fabric.container.command.ConnectionCommand;
import org.fabric3.fabric.container.command.DetachWireCommand;
import org.fabric3.spi.container.ContainerException;
import org.fabric3.spi.container.executor.CommandExecutor;
import org.fabric3.spi.container.executor.CommandExecutorRegistry;
import org.fabric3.spi.container.executor.ExecutionException;
import org.fabric3.spi.container.component.Component;
import org.fabric3.spi.container.component.ComponentManager;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;

/**
 * Connects and disconnects wires from references of a component to target services.
 */
@EagerInit
public class ConnectionCommandExecutor implements CommandExecutor<ConnectionCommand> {
    private ComponentManager componentManager;
    private CommandExecutorRegistry commandExecutorRegistry;

    public ConnectionCommandExecutor(@Reference ComponentManager componentManager, @Reference CommandExecutorRegistry commandExecutorRegistry) {
        this.componentManager = componentManager;
        this.commandExecutorRegistry = commandExecutorRegistry;
    }

    @Init
    public void init() {
        commandExecutorRegistry.register(ConnectionCommand.class, this);
    }

    public void execute(ConnectionCommand command) throws ContainerException {
        URI uri = command.getComponentUri();
        Component component = componentManager.getComponent(uri);
        if (component == null) {
            throw new ExecutionException("Component not found: " + uri);
        }
        component.startUpdate();
        // detach must be executed first so wire attachers can drop connection prior to adding new ones
        for (DetachWireCommand detachWireCommand : command.getDetachCommands()) {
            commandExecutorRegistry.execute(detachWireCommand);
        }
        for (AttachWireCommand attachWireCommand : command.getAttachCommands()) {
            commandExecutorRegistry.execute(attachWireCommand);
        }
        component.endUpdate();
    }
}