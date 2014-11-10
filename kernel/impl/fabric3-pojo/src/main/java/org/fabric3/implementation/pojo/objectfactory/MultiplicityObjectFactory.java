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
package org.fabric3.implementation.pojo.objectfactory;

import org.fabric3.spi.container.objectfactory.InjectionAttributes;
import org.fabric3.spi.container.objectfactory.ObjectFactory;

/**
 * Implementations use a backing collection of {@link ObjectFactory} instances that create a collection of instances for injection on a component,
 * e.g. a multiplicity reference.
 * <p/>
 * Implementations should implement {@link ObjectFactory#getInstance()} in a lock-free manner. The semantics of this contract require that update
 * operations are synchronized. That is, access to {@link #clear()}, {@link #startUpdate()} ()}, and {@link #endUpdate()} can only be made from a
 * single thread from the time {@link #startUpdate()} is called to when {@link #endUpdate()} is invoked. This means that implementations can cache
 * changes made via {@link #addObjectFactory(ObjectFactory, InjectionAttributes)} and  apply them atomically when {@link #endUpdate()} is called.
 * During an update sequence, {@link #getInstance()} can continue to return instances using the non-updated backing collection in a lock-free manner.
 *
 * @param <T> the instance type
 */
public interface MultiplicityObjectFactory<T> extends ObjectFactory<T> {

    /**
     * Adds a constituent object factory.
     *
     * @param objectFactory Constituent object factory
     * @param attributes    the injection attributes
     */
    void addObjectFactory(ObjectFactory<?> objectFactory, InjectionAttributes attributes);

    /**
     * Clears the contents of the object factory
     */
    void clear();

    /**
     * Used to put the factory in the update state.
     */
    void startUpdate();

    /**
     * Used to signal when an update is complete. Note that updates may not have taken place.
     */
    void endUpdate();

}
