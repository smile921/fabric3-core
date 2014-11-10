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
package org.fabric3.monitor.spi.model.type;

import org.fabric3.api.model.type.component.ResourceDefinition;

/**
 * A monitor resource configuration.
 */
public class MonitorResourceDefinition extends ResourceDefinition {
    private static final long serialVersionUID = -1549812994321886622L;

    private String name;
    private MonitorDestinationDefinition destinationDefinition;

    public MonitorResourceDefinition(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public MonitorDestinationDefinition getDestinationDefinition() {
        return destinationDefinition;
    }

    public void setDestinationDefinition(MonitorDestinationDefinition destinationDefinition) {
        this.destinationDefinition = destinationDefinition;
        this.destinationDefinition.setParent(this);
    }

}
