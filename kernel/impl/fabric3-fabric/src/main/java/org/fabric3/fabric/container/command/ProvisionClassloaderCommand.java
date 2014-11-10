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
package org.fabric3.fabric.container.command;

import org.fabric3.spi.container.command.CompensatableCommand;
import org.fabric3.spi.model.physical.PhysicalClassLoaderDefinition;

/**
 * A command to create or update a classloader on a runtime.
 */
public class ProvisionClassloaderCommand implements CompensatableCommand {
    private static final long serialVersionUID = -5993951083285578380L;

    private PhysicalClassLoaderDefinition definition;

    public ProvisionClassloaderCommand(PhysicalClassLoaderDefinition definition) {
        this.definition = definition;
    }

    public UnprovisionClassloaderCommand getCompensatingCommand() {
        return new UnprovisionClassloaderCommand(definition);
    }

    public PhysicalClassLoaderDefinition getClassLoaderDefinition() {
        return definition;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProvisionClassloaderCommand that = (ProvisionClassloaderCommand) o;
        return !(definition != null ? !definition.equals(that.definition) : that.definition != null);
    }

    public int hashCode() {
        return (definition != null ? definition.hashCode() : 0);
    }
}
