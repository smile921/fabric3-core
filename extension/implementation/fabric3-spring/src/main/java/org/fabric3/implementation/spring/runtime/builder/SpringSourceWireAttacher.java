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
package org.fabric3.implementation.spring.runtime.builder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.fabric3.api.host.Fabric3Exception;
import org.fabric3.implementation.pojo.spi.proxy.WireProxyService;
import org.fabric3.implementation.spring.provision.SpringWireSourceDefinition;
import org.fabric3.implementation.spring.runtime.component.SpringComponent;
import org.fabric3.spi.container.builder.component.SourceWireAttacher;
import org.fabric3.spi.container.component.ComponentManager;
import org.fabric3.spi.container.wire.Wire;
import org.fabric3.spi.model.physical.PhysicalWireTargetDefinition;
import org.fabric3.spring.spi.WireListener;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Reference;

/**
 * Attaches the source side of a wire to a Spring component.
 */
@EagerInit
public class SpringSourceWireAttacher implements SourceWireAttacher<SpringWireSourceDefinition> {
    private ComponentManager manager;
    private WireProxyService proxyService;

    private List<WireListener> listeners = Collections.emptyList();

    public SpringSourceWireAttacher(@Reference ComponentManager manager, @Reference WireProxyService proxyService) {
        this.manager = manager;
        this.proxyService = proxyService;
    }

    @Reference(required = false)
    public void setListeners(List<WireListener> listeners) {
        this.listeners = listeners;
    }

    public void attach(SpringWireSourceDefinition source, PhysicalWireTargetDefinition target, Wire wire) {
        SpringComponent component = getComponent(source);
        String referenceName = source.getReferenceName();
        ClassLoader loader = source.getClassLoader();
        Class<?> interfaze;
        try {
            interfaze = loader.loadClass(source.getInterface());
            // note callbacks not supported for spring beans
            Supplier<?> supplier = proxyService.createSupplier(interfaze, wire, null);
            component.attach(referenceName, interfaze, supplier);
            for (WireListener listener : listeners) {
                listener.onAttach(wire);
            }
        } catch (ClassNotFoundException e) {
            throw new Fabric3Exception(e);
        }
    }

    public void attachSupplier(SpringWireSourceDefinition source, Supplier<?> supplier, PhysicalWireTargetDefinition target) {
        SpringComponent component = getComponent(source);
        String referenceName = source.getReferenceName();
        ClassLoader loader = source.getClassLoader();
        Class<?> interfaze;
        try {
            interfaze = loader.loadClass(source.getInterface());
            component.attach(referenceName, interfaze, supplier);
        } catch (ClassNotFoundException e) {
            throw new Fabric3Exception(e);
        }
    }

    public void detach(SpringWireSourceDefinition source, PhysicalWireTargetDefinition target) {
        SpringComponent component = getComponent(source);
        String referenceName = source.getReferenceName();
        component.detach(referenceName);
    }

    public void detachSupplier(SpringWireSourceDefinition source, PhysicalWireTargetDefinition target) {
        detach(source, target);
    }

    private SpringComponent getComponent(SpringWireSourceDefinition definition) {
        URI uri = definition.getUri();
        SpringComponent component = (SpringComponent) manager.getComponent(uri);
        if (component == null) {
            throw new Fabric3Exception("Source not found: " + uri);
        }
        return component;
    }

}
