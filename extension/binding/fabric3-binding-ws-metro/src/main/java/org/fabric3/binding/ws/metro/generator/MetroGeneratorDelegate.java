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
package org.fabric3.binding.ws.metro.generator;

import org.fabric3.binding.ws.metro.provision.MetroWireSourceDefinition;
import org.fabric3.binding.ws.metro.provision.MetroWireTargetDefinition;
import org.fabric3.api.binding.ws.model.WsBindingDefinition;
import org.fabric3.api.model.type.contract.ServiceContract;
import org.fabric3.spi.domain.generator.policy.EffectivePolicy;
import org.fabric3.spi.domain.generator.GenerationException;
import org.fabric3.spi.model.instance.LogicalBinding;

/**
 * Generates source and target definitions for a service contract subtype.
 */
public interface MetroGeneratorDelegate<T extends ServiceContract> {

    /**
     * Generates a source definition from a logical binding.
     *
     * @param serviceBinding logical binding.
     * @param contract       the service contract
     * @param policy         the effective policy associated with the wire
     * @return Physical wire source definition.
     * @throws GenerationException if an error is raised during generation
     */
    MetroWireSourceDefinition generateSource(LogicalBinding<WsBindingDefinition> serviceBinding, T contract, EffectivePolicy policy)
            throws GenerationException;

    /**
     * Generates a target definition from a logical binding.
     *
     * @param referenceBinding logical binding.
     * @param contract         the service contract
     * @param policy           the effective policy associated with the wire
     * @return Physical wire target definition.
     * @throws GenerationException if an error is raised during generation
     */
    MetroWireTargetDefinition generateTarget(LogicalBinding<WsBindingDefinition> referenceBinding, T contract, EffectivePolicy policy)
            throws GenerationException;

    /**
     * Generates a target definition from logical reference and service bindings.
     *
     * @param serviceBinding logical service binding.
     * @param contract       the service contract
     * @param policy         the effective policy associated with the wire
     * @return Physical wire target definition.
     * @throws GenerationException if an error is raised during generation
     */
    MetroWireTargetDefinition generateServiceBindingTarget(LogicalBinding<WsBindingDefinition> serviceBinding,
                                                       T contract,
                                                       EffectivePolicy policy) throws GenerationException;

}
