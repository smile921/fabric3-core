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
package org.fabric3.spi.contribution;

import java.io.InputStream;
import java.net.URI;

import org.fabric3.api.host.Fabric3Exception;

/**
 * Implementations resolve contribution artifacts in a domain.
 */
public interface ContributionResolverExtension {

    /**
     * Resolves the contribution artifact associated with the URI, returning an InputStream.
     *
     * @param contributionUri the contribution URI
     * @return an input stream for the artifact
     * @throws Fabric3Exception if an error occurs resolving the artifact
     */
    InputStream resolve(URI contributionUri) throws Fabric3Exception;

}