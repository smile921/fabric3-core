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
import java.net.URI;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.fabric3.fabric.container.command.DisposeChannelCommand;
import org.fabric3.spi.container.builder.channel.ChannelBuilderRegistry;
import org.fabric3.spi.container.channel.Channel;
import org.fabric3.spi.model.physical.PhysicalChannelDefinition;

/**
 *
 */
public class DisposeChannelCommandExecutorTestCase extends TestCase {

    public void testDisposeChannel() throws Exception {
        PhysicalChannelDefinition definition = new PhysicalChannelDefinition(URI.create("test"), new QName("foo", "bar"));

        Channel channel = EasyMock.createMock(Channel.class);

        ChannelBuilderRegistry channelBuilderRegistry = EasyMock.createMock(ChannelBuilderRegistry.class);
        channelBuilderRegistry.dispose(EasyMock.isA(PhysicalChannelDefinition.class));

        EasyMock.replay(channelBuilderRegistry, channel);

        DisposeChannelCommandExecutor executor = new DisposeChannelCommandExecutor(channelBuilderRegistry, null);

        DisposeChannelCommand command = new DisposeChannelCommand(definition);
        executor.execute(command);

        EasyMock.verify(channelBuilderRegistry, channel);
    }

}
