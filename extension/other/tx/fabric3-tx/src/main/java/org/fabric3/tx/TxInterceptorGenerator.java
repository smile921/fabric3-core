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
package org.fabric3.tx;

import org.oasisopen.sca.annotation.EagerInit;
import org.w3c.dom.Element;

import org.fabric3.spi.domain.generator.wire.InterceptorGenerator;
import org.fabric3.spi.domain.generator.policy.PolicyMetadata;
import org.fabric3.spi.model.instance.LogicalOperation;

/**
 * Generates metadata for creating a TxInterceptor on a wire invocation chain.
 */
@EagerInit
public class TxInterceptorGenerator implements InterceptorGenerator {

    public TxInterceptorDefinition generate(Element policy, PolicyMetadata metadata, LogicalOperation operation) {
        String action = policy.getAttribute("action");
        return new TxInterceptorDefinition(TxAction.valueOf(action));
    }
}
