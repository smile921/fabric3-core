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

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.fabric3.fabric.container.command.AttachChannelConnectionCommand;
import org.fabric3.fabric.container.command.ChannelConnectionCommand;
import org.fabric3.fabric.container.command.DetachChannelConnectionCommand;
import org.fabric3.spi.container.executor.CommandExecutorRegistry;
import org.fabric3.spi.model.physical.PhysicalChannelConnectionDefinition;

/**
 *
 */
public class ChannelConnectionCommandExecutorTestCase extends TestCase {


    @SuppressWarnings({"unchecked"})
    public void testDetachBeforeAttach() throws Exception {
        PhysicalChannelConnectionDefinition definition = new MockDefinition();

        CommandExecutorRegistry registry = EasyMock.createStrictMock(CommandExecutorRegistry.class);
        registry.execute(EasyMock.isA(DetachChannelConnectionCommand.class));
        registry.execute(EasyMock.isA(AttachChannelConnectionCommand.class));
        EasyMock.replay(registry);

        ChannelConnectionCommandExecutor executor = new ChannelConnectionCommandExecutor(registry);

        ChannelConnectionCommand command = new ChannelConnectionCommand();
        command.add(new AttachChannelConnectionCommand(definition));
        command.add(new DetachChannelConnectionCommand(definition));

        executor.execute(command);

        EasyMock.verify(registry);
    }

    private class MockDefinition extends PhysicalChannelConnectionDefinition {
        private static final long serialVersionUID = -809769047230911419L;

        private MockDefinition() {
            super(null, null, null);
        }
    }

}
