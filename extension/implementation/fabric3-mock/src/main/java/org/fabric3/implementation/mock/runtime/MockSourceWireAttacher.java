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
package org.fabric3.implementation.mock.runtime;

import org.fabric3.implementation.mock.provision.MockWireSourceDefinition;
import org.fabric3.spi.container.ContainerException;
import org.fabric3.spi.container.builder.component.SourceWireAttacher;
import org.fabric3.spi.model.physical.PhysicalWireTargetDefinition;
import org.fabric3.spi.container.objectfactory.ObjectFactory;
import org.fabric3.spi.container.wire.Wire;

/**
 *
 */
public class MockSourceWireAttacher implements SourceWireAttacher<MockWireSourceDefinition> {

    public void attachObjectFactory(MockWireSourceDefinition source, ObjectFactory<?> factor, PhysicalWireTargetDefinition target) throws ContainerException {
        // Empty implementation; we don't want to attach anything to the mock
    }

    public void attach(MockWireSourceDefinition source, PhysicalWireTargetDefinition target, Wire wire) throws ContainerException {
        // Empty implementation; we don't want to attach anything to the mock
    }

    public void detach(MockWireSourceDefinition source, PhysicalWireTargetDefinition target) throws ContainerException {
    }


    public void detachObjectFactory(MockWireSourceDefinition source, PhysicalWireTargetDefinition target) throws ContainerException {
    }

}
