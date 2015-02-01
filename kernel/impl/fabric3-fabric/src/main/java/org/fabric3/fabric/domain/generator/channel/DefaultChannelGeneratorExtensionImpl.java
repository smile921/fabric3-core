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
package org.fabric3.fabric.domain.generator.channel;

import javax.xml.namespace.QName;
import java.net.URI;

import org.fabric3.api.host.Fabric3Exception;
import org.fabric3.api.model.type.component.Channel;
import org.fabric3.spi.domain.generator.channel.ChannelGeneratorExtension;
import org.fabric3.spi.model.instance.LogicalChannel;
import org.fabric3.spi.model.physical.ChannelConstants;
import org.fabric3.spi.model.physical.ChannelDeliveryType;
import org.fabric3.spi.model.physical.PhysicalChannelDefinition;
import org.oasisopen.sca.annotation.EagerInit;

/**
 *
 */
@EagerInit
public class DefaultChannelGeneratorExtensionImpl implements ChannelGeneratorExtension {

    public PhysicalChannelDefinition generate(LogicalChannel channel, QName deployable) throws Fabric3Exception {
        URI uri = channel.getUri();
        Channel definition = channel.getDefinition();
        String channelType = definition.getType();

        PhysicalChannelDefinition physicalDefinition = new PhysicalChannelDefinition(uri, deployable, channelType, ChannelDeliveryType.DEFAULT);
        physicalDefinition.setMetadata(definition.getMetadata().get(ChannelConstants.METADATA));

        return physicalDefinition;
    }
}
