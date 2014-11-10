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
package org.fabric3.spi.model.type.remote;

import java.util.List;

import org.fabric3.api.model.type.contract.ServiceContract;

/**
 * An interface of a service hosted in a remote process.
 */
public class RemoteServiceContract extends ServiceContract {
    private static final long serialVersionUID = 8902926932952586699L;

    private List<String> superTypes;

    public RemoteServiceContract(String interfaceName, List<String> superTypes) {
        this.interfaceName = interfaceName;
        this.superTypes = superTypes;
    }

    public String getQualifiedInterfaceName() {
        return interfaceName;
    }

    /**
     * Returns the super types of the contract type starting with the top type in the hierarchy.
     *
     * @return the super types of the contract type
     */
    public List<String> getSuperTypes() {
        return superTypes;
    }
}
