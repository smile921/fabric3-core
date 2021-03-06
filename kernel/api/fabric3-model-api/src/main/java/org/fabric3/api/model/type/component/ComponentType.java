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
package org.fabric3.api.model.type.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fabric3.api.model.type.ModelObject;
import org.fabric3.api.model.type.PolicyAware;

/**
 * A base component type.
 */
public class ComponentType extends ModelObject<Implementation> implements PolicyAware {
    private String key;
    private int order = Integer.MIN_VALUE;

    private Map<String, Service<ComponentType>> services = new HashMap<>();
    private Map<String, Consumer<ComponentType>> consumers = new HashMap<>();
    private Map<String, Reference<ComponentType>> references = new HashMap<>();
    private Map<String, Producer<ComponentType>> producers = new HashMap<>();
    private Map<String, Property> properties = new HashMap<>();
    private Map<String, ResourceReference> resourceReferences = new HashMap<>();

    private List<String> policies;

    /**
     * Returns the key value for map-based wires or null.
     *
     * @return the key value for map-based wires or null
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key value for map-based wires.
     *
     * @param key the key value
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Returns the order for collection- and array-based wires or {@link Integer#MIN_VALUE} if not specified.
     *
     * @return the order for collection- and array-based wires or {@link Integer#MIN_VALUE} if not specified
     */
    public int getOrder() {
        return order;
    }

    /**
     * Sets the order for collection- and array-based wires.
     *
     * @param order the order
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Returns the services provided by the implementation keyed by name.
     *
     * @return services provided by the implementation
     */
    public Map<String, Service<ComponentType>> getServices() {
        return services;
    }

    /**
     * Adds a service provided by the implementation.
     *
     * @param service the service to add
     */
    public void add(Service<ComponentType> service) {
        service.setParent(this);
        services.put(service.getName(), service);
    }

    /**
     * Returns the consumers provided by the implementation.
     *
     * @return the consumers provided by the implementation
     */
    public Map<String, Consumer<ComponentType>> getConsumers() {
        return consumers;
    }

    /**
     * Adds a consumer provided by the implementation.
     *
     * @param consumer the consumer to add
     */
    public void add(Consumer<ComponentType> consumer) {
        consumer.setParent(this);
        consumers.put(consumer.getName(), consumer);
    }

    /**
     * Returns references defined by the implementation keyed by name.
     *
     * @return references defined by the implementation
     */
    public Map<String, Reference<ComponentType>> getReferences() {
        return references;
    }

    /**
     * Adds a reference defined by the implementation.
     *
     * @param reference the reference to add
     */
    public void add(Reference<ComponentType> reference) {
        reference.setParent(this);
        references.put(reference.getName(), reference);
    }

    /**
     * Returns producers defined by implementation keyed by name.
     *
     * @return producers defined by implementation
     */
    public Map<String, Producer<ComponentType>> getProducers() {
        return producers;
    }

    /**
     * Adds a producer to the implementation.
     *
     * @param producer the producer to add
     */
    public void add(Producer<ComponentType> producer) {
        producer.setParent(this);
        producers.put(producer.getName(), producer);
    }

    /**
     * Returns properties defined by the implementation keyed by name.
     *
     * @return properties defined by the implementation
     */
    public Map<String, Property> getProperties() {
        return properties;
    }

    /**
     * Add a property defined by the implementation.
     *
     * @param property the property to add
     */
    public void add(Property property) {
        property.setParent(this);
        properties.put(property.getName(), property);
    }

    /**
     * Returns resource references defined by the implementation keyed by name.
     *
     * @return resource references defined by the implementation
     */
    public Map<String, ResourceReference> getResourceReferences() {
        return resourceReferences;
    }

    /**
     * Adds a resource reference defined by the implementation keyed by name.
     *
     * @param definition the resource reference to add
     */
    public void add(ResourceReference definition) {
        definition.setParent(this);
        resourceReferences.put(definition.getName(), definition);
    }

    public void addPolicy(String policy) {
        if (policies == null) {
            policies = new ArrayList<>();
        }
        policies.add(policy);
    }

    public List<String> getPolicies() {
        return policies == null ? Collections.emptyList() : policies;
    }

}
