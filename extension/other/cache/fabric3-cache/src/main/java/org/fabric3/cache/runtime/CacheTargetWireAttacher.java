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

package org.fabric3.cache.runtime;

import java.util.function.Supplier;

import org.fabric3.api.host.Fabric3Exception;
import org.fabric3.cache.provision.CacheWireTargetDefinition;
import org.fabric3.spi.container.builder.component.TargetWireAttacher;
import org.oasisopen.sca.annotation.Reference;

/**
 *
 */
public class CacheTargetWireAttacher implements TargetWireAttacher<CacheWireTargetDefinition> {
    private CacheRegistry registry;

    public CacheTargetWireAttacher(@Reference CacheRegistry registry) {
        this.registry = registry;
    }

    public Supplier<?> createSupplier(CacheWireTargetDefinition target) throws Fabric3Exception {
        String name = target.getCacheName();
        Object cache = registry.getCache(name);
        if (cache == null) {
            throw new Fabric3Exception("Cache not found: " + name);
        }
        return () -> cache;
    }
}
