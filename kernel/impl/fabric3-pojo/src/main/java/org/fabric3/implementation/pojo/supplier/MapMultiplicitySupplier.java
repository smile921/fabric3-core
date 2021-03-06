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
package org.fabric3.implementation.pojo.supplier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.fabric3.api.host.Fabric3Exception;
import org.fabric3.spi.container.injection.InjectionAttributes;

/**
 * Returns a <code>Map</code> containing object instances.
 */
public class MapMultiplicitySupplier implements MultiplicitySupplier<Map<?, ?>> {
    private volatile Map<Object, Supplier<?>> suppliers = new HashMap<>();
    private Map<Object, Supplier<?>> temporarySuppliers;

    private FactoryState state = FactoryState.UPDATED;

    public Map<Object, Object> get() throws Fabric3Exception {
        Map<Object, Object> map = new HashMap<>();
        for (Map.Entry<Object, Supplier<?>> entry : suppliers.entrySet()) {
            map.put(entry.getKey(), entry.getValue().get());
        }
        return map;
    }

    public void addSupplier(Supplier<?> supplier, InjectionAttributes attributes) {
        if (attributes == null) {
            throw new IllegalArgumentException("Attributes was null");
        }
        if (attributes.getKey() == null) {
            // programming error as null keys are checked during wire resolution
            throw new IllegalArgumentException("Attributes was null");
        }
        if (state != FactoryState.UPDATING) {
            throw new IllegalStateException("Factory not in updating state. The method startUpdate() must be called first.");
        }
        temporarySuppliers.put(attributes.getKey(), supplier);
    }

    public void clear() {
        suppliers.clear();
    }

    public void startUpdate() {
        state = FactoryState.UPDATING;
        temporarySuppliers = new HashMap<>();
    }

    public void endUpdate() {
        if (temporarySuppliers != null && !temporarySuppliers.isEmpty()) {
            // The isEmpty() check ensures only updates are applied since startUpdate()/endUpdate() can be called if there are no changes present.
            // Otherwise, if no updates are made, existing factories will be overwritten by the empty collection.
            suppliers = temporarySuppliers;
            temporarySuppliers = null;
        }
        state = FactoryState.UPDATED;
    }


}
