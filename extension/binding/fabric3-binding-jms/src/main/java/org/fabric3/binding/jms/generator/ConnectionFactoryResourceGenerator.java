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
package org.fabric3.binding.jms.generator;

import org.fabric3.api.annotation.wire.Key;
import org.fabric3.api.binding.jms.resource.ConnectionFactoryResource;
import org.fabric3.binding.jms.spi.provision.PhysicalConnectionFactoryResource;
import org.fabric3.spi.domain.generator.resource.ResourceGenerator;
import org.fabric3.spi.model.instance.LogicalResource;
import org.oasisopen.sca.annotation.EagerInit;

/**
 * Generates connection factory definitions.
 */
@EagerInit
@Key("org.fabric3.api.binding.jms.resource.ConnectionFactoryResource")
public class ConnectionFactoryResourceGenerator implements ResourceGenerator<ConnectionFactoryResource> {

    public PhysicalConnectionFactoryResource generateResource(LogicalResource<ConnectionFactoryResource> resource) {
        return new PhysicalConnectionFactoryResource(resource.getDefinition().getConfiguration());
    }
}