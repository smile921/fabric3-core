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
package org.fabric3.implementation.pojo.provision;

import org.fabric3.api.model.type.java.Injectable;
import org.fabric3.spi.model.physical.PhysicalWireSourceDefinition;

/**
 *
 */
public class PojoWireSourceDefinition extends PhysicalWireSourceDefinition {
    private static final long serialVersionUID = -7594088400247150995L;
    private Class<?> interfaceClass;
    private Injectable injectable;
    private boolean keyed;
    private String keyClassName;

    /**
     * Returns the Java interface for the service contract.
     *
     * @return the Java interface for the service contract
     */
    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    /**
     * Sets the Java interface for the service contract.
     *
     * @param interfaceClass the Java interface for the service contract
     */
    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    /**
     * Returns the Injectable this wire applies to.
     *
     * @return the the injectable this wire applies to
     */
    public Injectable getInjectable() {
        return injectable;
    }

    /**
     * Sets the Injectable for this wire applies to.
     *
     * @param injectable the injectable this wire applies to
     */
    public void setInjectable(Injectable injectable) {
        this.injectable = injectable;
    }

    /**
     * Returns true if the reference is a keyed reference, i.e. is a map-style multiplicity.
     *
     * @return true if the reference is a keyed reference
     */
    public boolean isKeyed() {
        return keyed;
    }

    /**
     * Sets if if the reference is a keyed reference.
     *
     * @param keyed true if the reference is a keyed reference
     */
    public void setKeyed(boolean keyed) {
        this.keyed = keyed;
    }

    /**
     * Returns the reference key class name.
     *
     * @return the reference key class name.
     */
    public String getKeyClassName() {
        return keyClassName;
    }

    /**
     * Sets the reference key class name.
     *
     * @param name the reference key class name.
     */
    public void setKeyClassName(String name) {
        this.keyClassName = name;
    }
}
