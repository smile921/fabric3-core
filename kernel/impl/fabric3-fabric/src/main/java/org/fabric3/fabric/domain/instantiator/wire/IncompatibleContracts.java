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
package org.fabric3.fabric.domain.instantiator.wire;

import java.net.URI;
import java.util.Collections;

import org.fabric3.api.host.failure.AssemblyFailure;
import org.fabric3.spi.model.instance.LogicalReference;

/**
 * Thrown when an attempt is made to wire a reference to a service with incompatible contracts.
 */
public class IncompatibleContracts extends AssemblyFailure {
    private URI referenceUri;
    private URI serviceUri;
    private String message;

    /**
     * Constructor.
     *
     * @param reference  the reference
     * @param serviceUri the URI of the service
     * @param message    the reported contract matching error
     */
    public IncompatibleContracts(LogicalReference reference, URI serviceUri, String message) {
        super(reference.getParent().getUri(), reference.getParent().getDefinition().getContributionUri(), Collections.singletonList(reference));
        this.referenceUri = reference.getUri();
        this.serviceUri = serviceUri;
        this.message = message;
    }

    public String getMessage() {
        return "The contracts for the reference " + referenceUri + " and service " + serviceUri + " are incompatible. The following error was reported: "
               + message;
    }
}
