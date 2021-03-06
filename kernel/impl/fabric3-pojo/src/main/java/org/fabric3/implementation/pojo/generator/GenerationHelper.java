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
package org.fabric3.implementation.pojo.generator;

import org.fabric3.api.model.type.java.InjectingComponentType;
import org.fabric3.implementation.pojo.provision.ImplementationManagerDefinition;
import org.fabric3.implementation.pojo.provision.PojoComponentDefinition;
import org.fabric3.spi.model.instance.LogicalComponent;

/**
 * Provides common functions for Java component generation.
 */
public interface GenerationHelper {

    /**
     * Computes injectors for the component type
     *
     * @param componentType     the component type
     * @param managerDefinition the instance factor definition for creating component implementation instances
     */
    void processInjectionSites(InjectingComponentType componentType, ImplementationManagerDefinition managerDefinition);

    /**
     * Set the actual values of the physical properties.
     *
     * @param component the component corresponding to the implementation
     * @param physical  the physical component whose properties should be set
     */
    void processPropertyValues(LogicalComponent<?> component, PojoComponentDefinition physical);
}
