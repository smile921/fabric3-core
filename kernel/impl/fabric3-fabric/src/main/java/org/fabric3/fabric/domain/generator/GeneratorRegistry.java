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
package org.fabric3.fabric.domain.generator;

import javax.xml.namespace.QName;

import org.fabric3.api.model.type.component.BindingDefinition;
import org.fabric3.api.model.type.component.Implementation;
import org.fabric3.api.model.type.component.ResourceDefinition;
import org.fabric3.api.model.type.component.ResourceReferenceDefinition;
import org.fabric3.spi.domain.generator.wire.WireBindingGenerator;
import org.fabric3.spi.domain.generator.component.ComponentGenerator;
import org.fabric3.spi.domain.generator.channel.ConnectionBindingGenerator;
import org.fabric3.spi.domain.generator.channel.EventStreamHandlerGenerator;
import org.fabric3.spi.domain.generator.wire.InterceptorGenerator;
import org.fabric3.spi.domain.generator.resource.ResourceGenerator;
import org.fabric3.spi.domain.generator.resource.ResourceReferenceGenerator;
import org.fabric3.spi.model.instance.LogicalComponent;

/**
 * A registry of generators. Generators are responsible for producing physical model objects that are provisioned to service nodes from their logical
 * counterparts.
 */
public interface GeneratorRegistry {

    /**
     * Returns a {@link CommandGenerator} for the specified implementation.
     *
     * @param clazz the implementation type the generator handles.
     * @return a the component generator for that implementation type
     * @throws GeneratorNotFoundException if no generator is registered for the implementation type
     */
    <T extends Implementation<?>> ComponentGenerator<LogicalComponent<T>> getComponentGenerator(Class<T> clazz) throws GeneratorNotFoundException;

    /**
     * Returns a {@link WireBindingGenerator} for the specified binding class.
     *
     * @param clazz The binding type type the generator handles.
     * @return The registered binding generator.
     * @throws GeneratorNotFoundException if no generator is registered for the binding type
     */
    <T extends BindingDefinition> WireBindingGenerator<T> getBindingGenerator(Class<T> clazz) throws GeneratorNotFoundException;

    /**
     * Returns a {@link ConnectionBindingGenerator} for the specified binding class.
     *
     * @param clazz The binding type type the generator handles.
     * @return The registered binding generator.
     * @throws GeneratorNotFoundException if no generator is registered for the binding type
     */
    <T extends BindingDefinition> ConnectionBindingGenerator<?> getConnectionBindingGenerator(Class<T> clazz) throws GeneratorNotFoundException;

    /**
     * Returns the {@link ResourceReferenceGenerator} for the resource type.
     *
     * @param clazz the resource type the generator handles
     * @return the resource reference generator
     * @throws GeneratorNotFoundException if no generator is registered for the resource type
     */
    <T extends ResourceReferenceDefinition> ResourceReferenceGenerator<T> getResourceReferenceGenerator(Class<T> clazz) throws GeneratorNotFoundException;

    /**
     * Returns the {@link ResourceGenerator} for the resource type.
     *
     * @param clazz the resource type the generator handles
     * @return the resource generator
     * @throws GeneratorNotFoundException if no generator is registered for the resource type
     */
    <T extends ResourceDefinition> ResourceGenerator<T> getResourceGenerator(Class<T> clazz) throws GeneratorNotFoundException;

    /**
     * Returns the {@link InterceptorGenerator} for the qualified name.
     *
     * @param extensionName qualified name of the policy extension
     * @return interceptor generator
     * @throws GeneratorNotFoundException if no generator is registered for the policy extension type
     */
    InterceptorGenerator getInterceptorGenerator(QName extensionName) throws GeneratorNotFoundException;

    /**
     * Returns the {@link EventStreamHandlerGenerator} for the qualified name.
     *
     * @param extensionName qualified name of the generator
     * @return the generator
     * @throws GeneratorNotFoundException if no generator is registered for qualified name
     */
    EventStreamHandlerGenerator getEventStreamHandlerGenerator(QName extensionName) throws GeneratorNotFoundException;

}
