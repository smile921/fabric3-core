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
package org.fabric3.binding.zeromq.generator;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.fabric3.api.annotation.wire.Key;
import org.fabric3.api.binding.zeromq.model.ZeroMQBinding;
import org.fabric3.api.binding.zeromq.model.ZeroMQMetadata;
import org.fabric3.api.host.Fabric3Exception;
import org.fabric3.api.model.type.contract.Operation;
import org.fabric3.api.model.type.contract.ServiceContract;
import org.fabric3.binding.zeromq.provision.ZeroMQWireSourceDefinition;
import org.fabric3.binding.zeromq.provision.ZeroMQWireTargetDefinition;
import org.fabric3.spi.domain.generator.wire.WireBindingGenerator;
import org.fabric3.spi.model.instance.LogicalBinding;
import org.fabric3.spi.model.instance.LogicalComponent;
import org.fabric3.spi.model.instance.LogicalCompositeComponent;
import org.fabric3.spi.model.instance.LogicalOperation;
import org.fabric3.spi.model.instance.LogicalService;
import org.fabric3.spi.model.instance.LogicalState;
import org.fabric3.spi.util.UriHelper;
import org.oasisopen.sca.annotation.EagerInit;

/**
 *
 */
@EagerInit
@Key("org.fabric3.api.binding.zeromq.model.ZeroMQBinding")
public class ZeroMQWireBindingGenerator implements WireBindingGenerator<ZeroMQBinding> {
    private static final String TARGET_URI = "targetUri";

    public ZeroMQWireSourceDefinition generateSource(LogicalBinding<ZeroMQBinding> binding, ServiceContract contract, List<LogicalOperation> operations) {
        ZeroMQMetadata metadata = binding.getDefinition().getZeroMQMetadata();
        if (binding.isCallback()) {
            URI uri = URI.create("zmq://" + contract.getInterfaceName());
            return new ZeroMQWireSourceDefinition(uri, metadata);
        } else {
            return new ZeroMQWireSourceDefinition(metadata);
        }
    }

    public ZeroMQWireTargetDefinition generateTarget(LogicalBinding<ZeroMQBinding> binding, ServiceContract contract, List<LogicalOperation> operations) {
        validateServiceContract(contract);
        ZeroMQMetadata metadata = binding.getDefinition().getZeroMQMetadata();

        if (binding.isCallback()) {
            URI targetUri = URI.create("zmq://" + contract.getInterfaceName());
            return new ZeroMQWireTargetDefinition(targetUri, metadata);
        }
        URI targetUri;
        // If this is an undeployment, use the previously calculated target URI. This must be done since the target component may no longer
        // be in the domain if it has been undeployed from another zone.
        if (LogicalState.MARKED == binding.getState()) {
            targetUri = binding.getMetadata(TARGET_URI, URI.class);
        } else {
            targetUri = parseTargetUri(binding);
            if (targetUri != null) {
                binding.addMetadata(TARGET_URI, targetUri);
            }
        }
        return generateTarget(contract, targetUri, metadata);
    }

    public ZeroMQWireTargetDefinition generateServiceBindingTarget(LogicalBinding<ZeroMQBinding> binding,
                                                                   ServiceContract contract,
                                                                   List<LogicalOperation> operations) {
        URI targetUri = binding.getParent().getUri();
        ZeroMQMetadata metadata = binding.getDefinition().getZeroMQMetadata();
        return generateTarget(contract, targetUri, metadata);
    }

    private ZeroMQWireTargetDefinition generateTarget(ServiceContract contract, URI targetUri, ZeroMQMetadata metadata) {
        boolean hasCallback = contract.getCallbackContract() != null;
        if (hasCallback) {
            URI callbackUri = URI.create("zmq://" + contract.getCallbackContract().getInterfaceName());
            return new ZeroMQWireTargetDefinition(targetUri, callbackUri, metadata);
        }
        return new ZeroMQWireTargetDefinition(targetUri, metadata);
    }

    /**
     * Parses the target URI. May return null if the target is not set and addresses are explicitly configured.
     *
     * @param binding the binding
     * @return the URI or null
     * @ if there is a parsing error
     */
    private URI parseTargetUri(LogicalBinding<ZeroMQBinding> binding) {
        URI bindingTargetUri = binding.getDefinition().getTargetUri();
        if (bindingTargetUri == null) {
            // create a synthetic name
            return URI.create("f3synthetic://" + binding.getParent().getUri() + "/" + binding.getDefinition().getName());
        }
        LogicalCompositeComponent composite = binding.getParent().getParent().getParent();
        URI parent = composite.getUri();

        String bindingTarget = bindingTargetUri.toString();

        URI targetUri;
        if (bindingTarget.contains("/")) {
            String[] tokens = bindingTarget.split("/");
            if (tokens.length != 2) {
                throw new Fabric3Exception("Invalid target specified on binding: " + bindingTarget);
            }
            targetUri = URI.create(parent.toString() + "/" + tokens[0]);
            LogicalComponent<?> component = composite.getComponent(targetUri);
            if (component == null) {
                throw new Fabric3Exception("Target component not found: " + targetUri);
            }
            LogicalService service = component.getService(tokens[1]);
            if (service == null) {
                throw new Fabric3Exception("Target service not found on component " + targetUri + ": " + tokens[1]);
            }
            // get the leaf service as the target may be a promotion
            targetUri = service.getUri();
        } else {
            targetUri = URI.create(parent.toString() + "/" + bindingTarget);
            if (targetUri.getFragment() == null) {
                LogicalComponent<?> component = composite.getComponent(targetUri);
                if (component == null) {
                    throw new Fabric3Exception("Target component not found: " + targetUri);
                }
                if (component.getServices().size() != 1) {
                    throw new Fabric3Exception("Target component must have exactly one service if the service is not specified in the target URI");
                }
                Collection<LogicalService> services = component.getServices();
                LogicalService service = services.iterator().next();
                // get the leaf service as the target may be a promotion
                targetUri = service.getUri();
            } else {
                URI defragmented = UriHelper.getDefragmentedName(targetUri);
                LogicalComponent component = composite.getComponent(defragmented);
                if (component == null) {
                    throw new Fabric3Exception("Target component not found: " + targetUri);
                }

            }
        }
        return targetUri;
    }

    private void validateServiceContract(ServiceContract contract) {
        boolean oneway = false;
        boolean first = true;
        for (Operation operation : contract.getOperations()) {
            if (first) {
                oneway = operation.isOneWay();
                first = false;
            } else {
                if ((!oneway && operation.isOneWay()) || (oneway && !operation.isOneWay())) {
                    String name = contract.getInterfaceName();
                    throw new Fabric3Exception("The ZeroMQ binding does not support mixing one-way and request-response operations: " + name);
                }
            }
        }
    }

}
