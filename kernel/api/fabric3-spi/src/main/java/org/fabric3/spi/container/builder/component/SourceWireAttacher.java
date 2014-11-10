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
package org.fabric3.spi.container.builder.component;

import org.fabric3.spi.container.ContainerException;
import org.fabric3.spi.model.physical.PhysicalWireSourceDefinition;
import org.fabric3.spi.model.physical.PhysicalWireTargetDefinition;
import org.fabric3.spi.container.objectfactory.ObjectFactory;
import org.fabric3.spi.container.wire.Wire;

/**
 * Attaches (and detaches) a wire from a source component or transport binding.
 */
public interface SourceWireAttacher<PSD extends PhysicalWireSourceDefinition> {
    /**
     * Attaches a wire to a source component or an incoming binding.
     *
     * @param source metadata for the source side of the wire
     * @param target metadata for the target side of the wire
     * @param wire   the wire
     * @throws ContainerException if an exception occurs during the attach operation
     */
    void attach(PSD source, PhysicalWireTargetDefinition target, Wire wire) throws ContainerException;

    /**
     * Attaches an ObjectFactory that returns a direct target instance to a source component.
     *
     * @param source        the definition of the component reference to attach to
     * @param objectFactory an ObjectFactory that can produce values compatible with the reference
     * @param target        the target definition for the wire
     * @throws ContainerException if an exception occurs during the attach operation
     */
    void attachObjectFactory(PSD source, ObjectFactory<?> objectFactory, PhysicalWireTargetDefinition target) throws ContainerException;

    /**
     * Detaches a wire from a source component or transport binding.
     *
     * @param source metadata for the source side of the wire
     * @param target metadata for the target side of the wire
     * @throws ContainerException if an exception occurs during the attach operation
     */
    void detach(PSD source, PhysicalWireTargetDefinition target) throws ContainerException;

    /**
     * detaches an ObjectFactory from a source component.
     *
     * @param source the definition of the component reference to detach
     * @param target the target definition for the wire
     * @throws ContainerException if an exception occurs during the deattach operation
     */
    void detachObjectFactory(PSD source, PhysicalWireTargetDefinition target) throws ContainerException;

}
