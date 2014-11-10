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
package org.fabric3.binding.jms.spi.runtime.manager;

import org.fabric3.spi.container.ContainerException;

/**
 * Denotes an error registering a connection factory.
 */
public class FactoryRegistrationException extends ContainerException {
    private static final long serialVersionUID = -9001751422278517341L;

    public FactoryRegistrationException(String message) {
        super(message);
    }

    public FactoryRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
