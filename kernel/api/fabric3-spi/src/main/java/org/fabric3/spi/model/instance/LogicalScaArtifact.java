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
package org.fabric3.spi.model.instance;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all logical artifacts.
 */
@SuppressWarnings("NonSerializableFieldInSerializableClass")
public abstract class LogicalScaArtifact<P extends LogicalScaArtifact<?>> implements Serializable {
    private static final long serialVersionUID = 3937960041374196627L;
    private P parent;
    private Map<String, Object> metadata;

    /**
     * Constructor.
     *
     * @param parent Parent of the SCA artifact.
     */
    protected LogicalScaArtifact(P parent) {
        this.parent = parent;
    }

    /**
     * @return Parent of this SCA artifact.
     */
    public final P getParent() {
        return parent;
    }

    public void addMetadata(String key, Object data) {
        if (metadata == null) {
            metadata = new HashMap<>();
        }
        metadata.put(key, data);
    }

    public <T> T getMetadata(String key, Class<T> type) {
        if (metadata == null) {
            return null;
        }
        return type.cast(metadata.get(key));
    }

}
