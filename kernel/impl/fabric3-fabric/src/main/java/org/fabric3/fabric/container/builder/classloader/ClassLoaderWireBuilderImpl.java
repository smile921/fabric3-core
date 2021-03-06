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
package org.fabric3.fabric.container.builder.classloader;

import java.net.URI;

import org.fabric3.spi.classloader.ClassLoaderRegistry;
import org.fabric3.spi.classloader.MultiParentClassLoader;
import org.fabric3.spi.container.builder.classloader.ClassLoaderWireBuilder;
import org.fabric3.spi.model.physical.PhysicalClassLoaderWireDefinition;
import org.oasisopen.sca.annotation.Reference;

/**
 *
 */
public class ClassLoaderWireBuilderImpl implements ClassLoaderWireBuilder {
    private ClassLoaderRegistry registry;

    public ClassLoaderWireBuilderImpl(@Reference ClassLoaderRegistry registry) {
        this.registry = registry;
    }

    public void build(MultiParentClassLoader source, PhysicalClassLoaderWireDefinition wireDefinition) {
        URI uri = wireDefinition.getTargetClassLoader();
        ClassLoader target = registry.getClassLoader(uri);
        if (target == null) {
            throw new AssertionError("Target classloader not found: " + uri);
        }
        String packageName = wireDefinition.getPackageName();
        if (packageName != null) {
            ClassLoader filter = new ClassLoaderWireFilter(target, packageName);
            source.addParent(filter);
        } else {
            source.addParent(target);
        }
    }
}
