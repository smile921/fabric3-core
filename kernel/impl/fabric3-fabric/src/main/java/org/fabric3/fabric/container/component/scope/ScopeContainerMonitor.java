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
package org.fabric3.fabric.container.component.scope;

import javax.xml.namespace.QName;
import java.net.URI;

import org.fabric3.api.annotation.monitor.Severe;
import org.fabric3.api.host.Fabric3Exception;

/**
 * Defines monitor events for scope containers
 */
public interface ScopeContainerMonitor {

    @Severe("Error")
    void error(Exception e);

    @Severe("Error initializing component {0} ({1})")
    void initializationError(URI uri, QName deployable, Exception e);

    @Severe("Error destroying component {0} ({1})")
    void destructionError(URI uri, QName deployable, Fabric3Exception e);

    @Severe("Error electing zone leader")
    void leaderElectionError(Exception e);
}
