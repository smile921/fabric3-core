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
package org.fabric3.spi.domain.generator;

import org.fabric3.spi.model.instance.LogicalCompositeComponent;

/**
 * Generates a {@link Deployment} from the current logical state of the domain.
 */
public interface Generator {

    /**
     * Performs the generation.
     *
     * @param domain      the logical domain composite
     * @param incremental true if generation should be incremental, i.e. commands are generated only for new components, channels, wires, and event
     *                    streams as opposed to all components (new and existing ones)
     * @return the deployment
     * @throws GenerationException If unable to generate the deployment
     */
    Deployment generate(LogicalCompositeComponent domain, boolean incremental) throws GenerationException;

}
