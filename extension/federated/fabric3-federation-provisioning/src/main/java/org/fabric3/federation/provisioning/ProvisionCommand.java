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
package org.fabric3.federation.provisioning;

import java.net.URI;

import org.fabric3.spi.container.command.Response;
import org.fabric3.spi.container.command.ResponseCommand;

/**
 * Sent to a controller or zone peer to return the provisioning URL of a contribution artifact.
 */
public class ProvisionCommand implements ResponseCommand {
    private static final long serialVersionUID = -5748556849217168270L;
    private URI contributionUri;
    private ProvisionResponse response;

    public ProvisionCommand(URI contributionUri) {
        this.contributionUri = contributionUri;
    }

    public URI getContributionUri() {
        return contributionUri;
    }

    public void setResponse(ProvisionResponse response) {
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }
}