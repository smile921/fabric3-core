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
package org.fabric3.implementation.timer.introspection;

import javax.xml.stream.Location;

import org.fabric3.api.model.type.ModelObject;
import org.fabric3.spi.introspection.xml.XmlValidationFailure;

/**
 *
 */
public class InvalidTimerExpression extends XmlValidationFailure {
    private Throwable cause;

    public InvalidTimerExpression(String message, Location location, ModelObject modelObject) {
        super(message, location, modelObject);
    }

    public InvalidTimerExpression(String message, Location location, Throwable cause, ModelObject modelObject) {
        super(message, location, modelObject);
        this.cause = cause;
    }

    public String getMessage() {
        if (cause != null) {
            return super.getMessage() + ". The original error was: \n" + cause.toString();
        } else {
            return super.getMessage();
        }
    }
}

