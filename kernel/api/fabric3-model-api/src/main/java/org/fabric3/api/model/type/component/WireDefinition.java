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

import org.fabric3.api.model.type.ModelObject;

/**
 * A wire specified in a composite file.
 */
public class WireDefinition extends ModelObject<Composite> {
    private static final long serialVersionUID = -2310313135279527903L;

    private Target reference;
    private Target service;
    private boolean replace;

    public WireDefinition(Target reference, Target service, boolean replace) {
        this.reference = reference;
        this.service = service;
        this.replace = replace;
    }

    /**
     * Returns a <code>Target</code> identifying the source reference for the wire
     *
     * @return the target
     */
    public Target getReferenceTarget() {
        return reference;
    }

    /**
     * Returns a <code>Target</code> identifying the target service for the wire
     *
     * @return the target
     */
    public Target getServiceTarget() {
        return service;
    }

    public boolean isReplace() {
        return replace;
    }
}
