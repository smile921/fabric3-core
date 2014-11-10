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
package org.fabric3.federation.deployment.command;

import org.fabric3.spi.federation.topology.ErrorResponse;

/**
 * A response returned to the controller when a runtime raises an error processing a {@link DeploymentCommand}.
 */
public class DeploymentErrorResponse implements ErrorResponse {
    private static final long serialVersionUID = 411382659017602521L;

    private String runtimeName;
    private Exception exception;

    public DeploymentErrorResponse(Exception exception) {
        this.exception = exception;
    }

    public String getRuntimeName() {
        return runtimeName;
    }

    public void setRuntimeName(String runtimeName) {
        this.runtimeName = runtimeName;
    }

    public Exception getException() {
        return exception;
    }
}