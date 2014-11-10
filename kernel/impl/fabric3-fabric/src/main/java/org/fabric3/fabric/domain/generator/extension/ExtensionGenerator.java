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
package org.fabric3.fabric.domain.generator.extension;

import java.util.List;
import java.util.Map;

import org.fabric3.fabric.domain.generator.GenerationType;
import org.fabric3.spi.container.command.CompensatableCommand;
import org.fabric3.spi.contribution.Contribution;
import org.fabric3.spi.domain.generator.GenerationException;
import org.fabric3.spi.model.instance.LogicalComponent;

/**
 * Generates commands to un/provision extensions for a deployment or undeployment.
 */
public interface ExtensionGenerator {

    /**
     * Generates the un/provision commands.
     *
     * @param contributions      the contributions being deployed or undeployed
     * @param components         the components being deployed or undeployed
     * @param deploymentCommands the current deployment commands
     * @param type               the generation type being performed (full, incremental, undeploy)
     * @return the extension deployment commands keyed by zone
     * @throws GenerationException if an error occurs generating the commands
     */
    Map<String, CompensatableCommand> generate(Map<String, List<Contribution>> contributions,
                                               List<LogicalComponent<?>> components,
                                               Map<String, List<CompensatableCommand>> deploymentCommands,
                                               GenerationType type) throws GenerationException;
}
