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
package org.fabric3.binding.rs.runtime.bytecode;

/**
 * Generates a concrete delegating provider class from the given base class and the delegating class.
 *
 * This service is used to satisfy the Jersey requirement that provider classes be unique since Fabric3 component providers are proxied by a single class.
 */
public interface ProviderGenerator {

    /**
     * Generates the concrete subclass.
     *
     * @param baseClass        the superclass
     * @param delegateClass    the delegate class
     * @param genericSignature the generic signature; may be null for non-generic types
     * @return the subclass
     */
    <T> Class<? extends T> generate(Class<T> baseClass, Class<?> delegateClass, String genericSignature);

}
