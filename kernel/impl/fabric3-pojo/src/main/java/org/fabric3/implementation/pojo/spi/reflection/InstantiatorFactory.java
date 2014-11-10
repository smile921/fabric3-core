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
package org.fabric3.implementation.pojo.spi.reflection;

import java.lang.reflect.Constructor;

import org.fabric3.spi.container.objectfactory.ObjectFactory;

/**
 * Creates {@link ObjectFactory}s for instantiating component implementation instances.
 */
public interface InstantiatorFactory {

    /**
     * Returns true if this is the default factory.
     *
     * @return true if this is the default factory
     */
    boolean isDefault();

    /**
     * Creates an object factory that is used to instantiate instances.
     *
     * @param constructor        the constructor to instantiate with
     * @param parameterFactories object factories which return constructor parameters
     * @return the object factory
     */
    <T> ObjectFactory<T> createInstantiator(Constructor<T> constructor, ObjectFactory<?>[] parameterFactories);

}
