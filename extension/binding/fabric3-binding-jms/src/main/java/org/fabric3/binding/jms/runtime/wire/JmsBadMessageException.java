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
package org.fabric3.binding.jms.runtime.wire;

import org.fabric3.api.host.Fabric3Exception;

/**
 * Denotes a message the JMS binding cannot process and therefore should not be redelivered.
 */
public class JmsBadMessageException extends Fabric3Exception {
    private static final long serialVersionUID = 2460924186536830633L;

    public JmsBadMessageException(String message) {
        super(message);
    }

    public JmsBadMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
