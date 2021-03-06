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
package org.fabric3.fabric.container.executor;

import java.net.URI;

import org.fabric3.api.host.Fabric3Exception;
import org.fabric3.fabric.container.command.StopComponentCommand;
import org.fabric3.spi.container.component.Component;
import org.fabric3.spi.container.component.ComponentManager;
import org.fabric3.spi.container.executor.CommandExecutor;
import org.fabric3.spi.container.executor.CommandExecutorRegistry;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;

/**
 * Stops a component.
 */
@EagerInit
public class StopComponentCommandExecutor implements CommandExecutor<StopComponentCommand> {

    private final ComponentManager componentManager;
    private final CommandExecutorRegistry executorRegistry;

    public StopComponentCommandExecutor(@Reference ComponentManager componentManager, @Reference CommandExecutorRegistry executorRegistry) {
        this.componentManager = componentManager;
        this.executorRegistry = executorRegistry;
    }

    @Init
    public void init() {
        executorRegistry.register(StopComponentCommand.class, this);
    }

    public void execute(StopComponentCommand command) {
        URI uri = command.getUri();
        Component component = componentManager.getComponent(uri);
        if (component == null) {
            throw new Fabric3Exception("Component not found:" + uri);
        }
        component.stop();
    }
}

