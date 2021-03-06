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
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.function.Supplier;

import org.fabric3.spi.container.injection.Injector;

/**
 * Factory responsible for creating instantiators, injectors, and invokers.
 */
public interface ReflectionFactory {

    /**
     * Creates a Supplier that is used to instantiate instances.
     *
     * @param constructor the constructor to instantiate with
     * @param suppliers   object factories which return constructor parameters
     * @return the Supplier
     */
    Supplier<?> createInstantiator(Constructor<?> constructor, Supplier<?>[] suppliers);

    /**
     * Creates an injector for a field or method.
     *
     * @param member   the field or method
     * @param supplier the factory that returns an instance to be injected
     * @return the injector
     */
    Injector<?> createInjector(Member member, Supplier<?> supplier);

    /**
     * Creates a lifecycle invoker that is used to issue a method callback on an implementation instance.
     *
     * @param method the callback method
     * @return the invoker
     */
    LifecycleInvoker createLifecycleInvoker(Method method);

    /**
     * Creates a service invoker for the given method.
     *
     * @param method the method
     * @return the invoker
     */
    ServiceInvoker createServiceInvoker(Method method);

    /**
     * Creates a consumer invoker for the given method.
     *
     * @param method the method
     * @return the invoker
     */
    ConsumerInvoker createConsumerInvoker(Method method);

}
