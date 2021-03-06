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
package org.fabric3.fabric.domain.generator;

import java.util.Optional;

import org.fabric3.api.host.Fabric3Exception;
import org.fabric3.spi.container.command.Command;
import org.fabric3.spi.model.instance.LogicalComponent;

/**
 * Generates a Command that must be applied to a zone based on changes to a logical component.
 */
public interface CommandGenerator<T extends Command> {

    int PREPARE = 1;

    int BUILD_COMPONENTS = 2;

    int ATTACH = 3;

    int START_COMPONENTS = 4;

    int DISPOSE_COMPONENTS = 5;

    int DISPOSE_RESOURCES = 6;

    /**
     * Gets the phase the command generator should be called in.
     *
     * @return an ascending value where 0 is first
     */
    int getOrder();

    /**
     * Generates a command based on the contents of a logical component
     *
     * @param logicalComponent the logical component to generate the command from
     * @return the generated command
     * @throws Fabric3Exception if an error occurs during generation
     */
    Optional<T> generate(LogicalComponent<?> logicalComponent) throws Fabric3Exception;

}
