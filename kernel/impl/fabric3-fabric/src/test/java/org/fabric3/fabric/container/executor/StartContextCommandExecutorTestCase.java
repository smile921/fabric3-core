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

import javax.xml.namespace.QName;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.fabric3.api.model.type.component.Scope;
import org.fabric3.fabric.container.command.StartContextCommand;
import org.fabric3.spi.container.channel.ChannelManager;
import org.fabric3.spi.container.component.ScopeContainer;
import org.fabric3.spi.container.component.ScopeRegistry;
import org.fabric3.spi.container.executor.CommandExecutorRegistry;

/**
 *
 */
public class StartContextCommandExecutorTestCase extends TestCase {

    public void testExecute() throws Exception {
        CommandExecutorRegistry executorRegistry = EasyMock.createMock(CommandExecutorRegistry.class);
        executorRegistry.register(EasyMock.eq(StartContextCommand.class), EasyMock.isA(StartContextCommandExecutor.class));

        ScopeContainer compositeContainer = EasyMock.createMock(ScopeContainer.class);
        compositeContainer.startContext(EasyMock.isA(QName.class));
        ScopeContainer domainContainer = EasyMock.createMock(ScopeContainer.class);
        domainContainer.startContext(EasyMock.isA(QName.class));
        ScopeRegistry scopeRegistry = EasyMock.createMock(ScopeRegistry.class);
        EasyMock.expect(scopeRegistry.getScopeContainer(Scope.COMPOSITE)).andReturn(compositeContainer);
        EasyMock.expect(scopeRegistry.getScopeContainer(Scope.DOMAIN)).andReturn(domainContainer);

        ChannelManager channelManager = EasyMock.createMock(ChannelManager.class);
        channelManager.startContext(EasyMock.isA(QName.class));

        ContextMonitor monitor = EasyMock.createNiceMock(ContextMonitor.class);

        EasyMock.replay(executorRegistry, scopeRegistry, compositeContainer, domainContainer, channelManager, monitor);

        StartContextCommandExecutor executor = new StartContextCommandExecutor(executorRegistry, scopeRegistry, channelManager, monitor);
        executor.init();
        StartContextCommand command = new StartContextCommand(new QName("test", "component"), true);
        executor.execute(command);

        EasyMock.verify(executorRegistry, scopeRegistry, compositeContainer, domainContainer, channelManager, monitor);

    }

}
