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
package org.fabric3.binding.ws.generator;

import org.fabric3.api.annotation.wire.Key;
import org.fabric3.api.binding.ws.model.WsBinding;
import org.fabric3.spi.domain.generator.wire.CallbackBindingGenerator;
import org.fabric3.spi.model.instance.LogicalBinding;
import org.oasisopen.sca.annotation.EagerInit;

/**
 *
 */
@EagerInit
@Key("org.fabric3.api.binding.ws.model.WsBinding")
public class WsCallbackBindingGenerator implements CallbackBindingGenerator<WsBinding> {

    public WsBinding generateServiceCallback(LogicalBinding<WsBinding> forwardBinding) {
        return new WsBinding();
    }

    public WsBinding generateReferenceCallback(LogicalBinding<WsBinding> forwardBinding) {
        return new WsBinding();
    }
}
