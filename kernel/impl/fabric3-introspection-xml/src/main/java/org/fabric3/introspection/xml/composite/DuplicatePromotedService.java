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
package org.fabric3.introspection.xml.composite;

import javax.xml.stream.Location;

import org.fabric3.api.model.type.component.Composite;
import org.fabric3.spi.introspection.xml.XmlValidationFailure;

/**
 *
 */
public class DuplicatePromotedService extends XmlValidationFailure {

    public DuplicatePromotedService(String serviceName, Location location, Composite type) {
        super("More than one promoted service with the name " + serviceName + " was found in the composite ", location, type);
    }
}
