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
package org.fabric3.implementation.system.singleton;

import java.net.URI;

import org.oasisopen.sca.annotation.EagerInit;

import org.fabric3.spi.domain.generator.component.ComponentGenerator;
import org.fabric3.spi.domain.generator.policy.EffectivePolicy;
import org.fabric3.spi.domain.generator.GenerationException;
import org.fabric3.spi.model.instance.LogicalComponent;
import org.fabric3.spi.model.instance.LogicalConsumer;
import org.fabric3.spi.model.instance.LogicalProducer;
import org.fabric3.spi.model.instance.LogicalReference;
import org.fabric3.spi.model.instance.LogicalResourceReference;
import org.fabric3.spi.model.instance.LogicalService;
import org.fabric3.spi.model.physical.PhysicalComponentDefinition;
import org.fabric3.spi.model.physical.PhysicalConnectionSourceDefinition;
import org.fabric3.spi.model.physical.PhysicalConnectionTargetDefinition;
import org.fabric3.spi.model.physical.PhysicalWireSourceDefinition;
import org.fabric3.spi.model.physical.PhysicalWireTargetDefinition;
import org.fabric3.api.model.type.java.Injectable;
import org.fabric3.api.model.type.java.InjectableType;

/**
 *
 */
@EagerInit
public class SingletonComponentGenerator implements ComponentGenerator<LogicalComponent<SingletonImplementation>> {

    public PhysicalComponentDefinition generate(LogicalComponent<SingletonImplementation> component) throws GenerationException {
        throw new UnsupportedOperationException();
    }

    public PhysicalWireSourceDefinition generateSource(LogicalReference reference, EffectivePolicy policy) throws GenerationException {
        SingletonWireSourceDefinition wireDefinition = new SingletonWireSourceDefinition();
        URI uri = reference.getUri();
        wireDefinition.setOptimizable(true);
        wireDefinition.setUri(uri);
        wireDefinition.setInjectable(new Injectable(InjectableType.REFERENCE, uri.getFragment()));

        return wireDefinition;
    }

    public PhysicalWireTargetDefinition generateTarget(LogicalService service, EffectivePolicy policy) throws GenerationException {
        SingletonWireTargetDefinition wireDefinition = new SingletonWireTargetDefinition();
        URI uri = service.getUri();
        wireDefinition.setUri(uri);
        wireDefinition.setOptimizable(true);
        return wireDefinition;
    }

    public PhysicalWireSourceDefinition generateResourceSource(LogicalResourceReference<?> resourceReference) throws GenerationException {
        SingletonWireSourceDefinition wireDefinition = new SingletonWireSourceDefinition();
        URI uri = resourceReference.getUri();
        wireDefinition.setOptimizable(true);
        wireDefinition.setUri(uri);
        wireDefinition.setInjectable(new Injectable(InjectableType.RESOURCE, uri.getFragment()));
        return wireDefinition;
    }

    public PhysicalConnectionSourceDefinition generateConnectionSource(LogicalProducer producer) {
        throw new UnsupportedOperationException();
    }

    public PhysicalConnectionTargetDefinition generateConnectionTarget(LogicalConsumer consumer) throws GenerationException {
        throw new UnsupportedOperationException();
    }

    public PhysicalWireSourceDefinition generateCallbackSource(LogicalService service, EffectivePolicy policy) throws GenerationException {
        throw new UnsupportedOperationException();
    }

}
