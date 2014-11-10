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
package org.fabric3.fabric.domain.generator.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

import org.oasisopen.sca.annotation.EagerInit;

import org.fabric3.fabric.container.command.StartContextCommand;
import org.fabric3.api.host.Names;
import org.fabric3.spi.container.command.CompensatableCommand;
import org.fabric3.spi.domain.generator.GenerationException;
import org.fabric3.spi.model.instance.LogicalComponent;
import org.fabric3.spi.model.instance.LogicalState;

/**
 * Generates commands to start deployable contexts in a zone.
 */
@EagerInit
public class StartContextCommandGeneratorImpl implements StartContextCommandGenerator {

    public Map<String, List<CompensatableCommand>> generate(List<LogicalComponent<?>> components, boolean incremental) throws GenerationException {
        Map<String, List<CompensatableCommand>> commands = new HashMap<>();
        for (LogicalComponent<?> component : components) {
            if (component.getState() == LogicalState.NEW || !incremental) {
                QName deployable = component.getDeployable();
                // only log application composite deployments
                boolean log = !component.getUri().toString().startsWith(Names.RUNTIME_NAME);
                StartContextCommand command = new StartContextCommand(deployable, log);
                String zone = component.getZone();
                List<CompensatableCommand> list = getCommands(zone, commands);
                if (!list.contains(command)) {
                    list.add(command);
                }
            }
        }
        return commands;
    }

    /**
     * Returns the list of commands by zone, creating one if necessary.
     *
     * @param zone          the zone
     * @param startCommands the list of commands mapped by zone
     * @return the list of commands for a zone
     */
    private List<CompensatableCommand> getCommands(String zone, Map<String, List<CompensatableCommand>> startCommands) {
        List<CompensatableCommand> list = startCommands.get(zone);
        if (list == null) {
            list = new ArrayList<>();
            startCommands.put(zone, list);
        }
        return list;
    }

}
