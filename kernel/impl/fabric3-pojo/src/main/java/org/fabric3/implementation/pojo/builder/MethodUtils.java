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
package org.fabric3.implementation.pojo.builder;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;

import org.fabric3.api.host.Fabric3Exception;
import org.fabric3.spi.model.physical.PhysicalOperationDefinition;
import org.fabric3.spi.model.physical.PhysicalWireSourceDefinition;
import org.fabric3.spi.model.physical.PhysicalWireTargetDefinition;

/**
 *
 */
public final class MethodUtils {

    public static Method findMethod(PhysicalWireSourceDefinition source,
                                    PhysicalWireTargetDefinition target,
                                    PhysicalOperationDefinition operation,
                                    Class<?> implementationClass,
                                    ClassLoader loader) throws Fabric3Exception {
        List<Class<?>> params = operation.getTargetParameterTypes();
        Class<?>[] paramTypes = new Class<?>[params.size()];
        assert loader != null;
        for (int i = 0; i < params.size(); i++) {
            paramTypes[i] = params.get(i);
        }
        Method method = null;
        if (operation.isRemotable()) {
            // if the operation is remotable, do not match on parameter types since method names cannot be overloaded
            Method[] methods = implementationClass.getMethods();
            String name = operation.getName();
            for (Method entry : methods) {
                if (name.equals(entry.getName())) {
                    method = entry;
                    break;
                }
            }
            if (method == null) {
                URI sourceUri = source.getUri();
                URI targetUri = target.getUri();
                throw new Fabric3Exception("No matching method found when wiring " + sourceUri + " to " + targetUri);
            }
        } else {
            // operation is remote, match on operation names and parameter types
            try {
                method = implementationClass.getMethod(operation.getName(), paramTypes);
            } catch (NoSuchMethodException e) {
                URI sourceUri = source.getUri();
                URI targetUri = target.getUri();
                throw new Fabric3Exception("No matching method found when wiring " + sourceUri + " to " + targetUri, e);
            }
        }
        return method;
    }

}
