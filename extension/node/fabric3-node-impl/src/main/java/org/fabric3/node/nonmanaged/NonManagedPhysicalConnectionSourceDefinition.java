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
package org.fabric3.node.nonmanaged;

import org.fabric3.spi.model.physical.PhysicalConnectionSourceDefinition;

/**
 *
 */
public class NonManagedPhysicalConnectionSourceDefinition extends PhysicalConnectionSourceDefinition {
    private static final long serialVersionUID = 6336466438610103270L;

    private String interfaze;
    private transient Object proxy;

    public NonManagedPhysicalConnectionSourceDefinition(String interfaze) {
        this.interfaze = interfaze;
    }

    public String getInterface() {
        return interfaze;
    }

    public Object getProxy() {
        return proxy;
    }

    public void setProxy(Object proxy) {
        this.proxy = proxy;
    }
}
