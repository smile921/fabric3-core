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
package org.fabric3.spi.container.builder.component;

import org.fabric3.api.host.Fabric3Exception;
import org.fabric3.spi.container.channel.Channel;
import org.fabric3.spi.model.physical.PhysicalChannelBindingDefinition;

/**
 * Builds and disposes channel binding infrastructure.
 */
public interface ChannelBindingBuilder<B extends PhysicalChannelBindingDefinition> {

    /**
     * Build (provision) the infrastructure.
     *
     * @param definition the binding definition
     * @param channel    the channel
     * @throws Fabric3Exception if there is an error during the build process
     */
    void build(B definition, Channel channel) throws Fabric3Exception;

    /**
     * Disposes the infrastructure.
     *
     * @param definition the binding definition
     * @param channel    the channel
     * @throws Fabric3Exception if there is an error during the dispose process
     */
    void dispose(B definition, Channel channel) throws Fabric3Exception;
}
