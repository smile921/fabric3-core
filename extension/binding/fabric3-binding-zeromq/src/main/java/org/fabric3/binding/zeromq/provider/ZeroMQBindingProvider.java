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
package org.fabric3.binding.zeromq.provider;

import javax.xml.namespace.QName;
import java.net.URI;

import org.fabric3.api.binding.zeromq.model.ZeroMQMetadata;
import org.fabric3.api.binding.zeromq.model.ZeroMQBindingDefinition;
import org.fabric3.api.model.type.contract.ServiceContract;
import org.fabric3.spi.domain.generator.binding.BindingMatchResult;
import org.fabric3.spi.domain.generator.binding.BindingProvider;
import org.fabric3.spi.domain.generator.binding.BindingSelectionException;
import org.fabric3.spi.model.instance.LogicalBinding;
import org.fabric3.spi.model.instance.LogicalChannel;
import org.fabric3.spi.model.instance.LogicalReference;
import org.fabric3.spi.model.instance.LogicalService;
import org.fabric3.spi.model.instance.LogicalWire;
import org.fabric3.spi.model.type.remote.RemoteServiceContract;
import org.fabric3.spi.util.UriHelper;
import org.oasisopen.sca.annotation.Property;

/**
 * A binding.sca provider that uses ZeroMQ as the underlying transport.
 */
public class ZeroMQBindingProvider implements BindingProvider {
    private static final BindingMatchResult MATCH = new BindingMatchResult(true, ZeroMQBindingDefinition.BINDING_0MQ);
    private static final BindingMatchResult NO_MATCH = new BindingMatchResult(false, ZeroMQBindingDefinition.BINDING_0MQ);

    private boolean enabled = true;
    private long highWater = -1;
    private long multicastRate = -1;
    private long multicastRecovery = -1;
    private long sendBuffer = -1;
    private long receiveBuffer = -1;

    @Property(required = false)
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Property(required = false)
    public void setHighWater(long highWater) {
        this.highWater = highWater;
    }

    @Property(required = false)
    public void setMulticastRate(long multicastRate) {
        this.multicastRate = multicastRate;
    }

    @Property(required = false)
    public void setMulticastRecovery(long multicastRecovery) {
        this.multicastRecovery = multicastRecovery;
    }

    @Property(required = false)
    public void setSendBuffer(long sendBuffer) {
        this.sendBuffer = sendBuffer;
    }

    @Property(required = false)
    public void setReceiveBuffer(long receiveBuffer) {
        this.receiveBuffer = receiveBuffer;
    }

    public QName getType() {
        return ZeroMQBindingDefinition.BINDING_0MQ;
    }

    public BindingMatchResult canBind(LogicalWire wire) {
        return !enabled ? NO_MATCH : MATCH;
    }

    public BindingMatchResult canBind(LogicalService service) {
        return !enabled ? NO_MATCH : MATCH;
    }

    public BindingMatchResult canBind(LogicalChannel channel) {
        return !enabled ? NO_MATCH : MATCH;
    }

    public void bind(LogicalService service) {
        QName deployable = service.getParent().getDeployable();

        ZeroMQMetadata metadata = createMetadata();
        ZeroMQBindingDefinition serviceDefinition = new ZeroMQBindingDefinition("binding.zeromq", metadata);
        LogicalBinding<ZeroMQBindingDefinition> serviceBinding = new LogicalBinding<>(serviceDefinition, service, deployable);
        serviceBinding.setAssigned(true);
        service.addBinding(serviceBinding);

        // check if the interface is bidirectional
        ServiceContract targetContract = service.getDefinition().getServiceContract();
        if (targetContract.getCallbackContract() != null) {
            // setup callback bindings
            ZeroMQMetadata callbackMetadata = createMetadata();

            ZeroMQBindingDefinition callbackServiceDefinition = new ZeroMQBindingDefinition("binding.zeromq.callback", callbackMetadata);
            LogicalBinding<ZeroMQBindingDefinition> callbackServiceBinding = new LogicalBinding<>(callbackServiceDefinition,
                                                                                                                         service,
                                                                                                                         deployable);
            callbackServiceBinding.setAssigned(true);
            service.addCallbackBinding(callbackServiceBinding);
        }

    }

    public void bind(LogicalWire wire) throws BindingSelectionException {
        LogicalReference source = wire.getSource().getLeafReference();
        LogicalService target = wire.getTarget().getLeafService();
        QName deployable = source.getParent().getDeployable();

        ZeroMQMetadata metadata = createMetadata();

        // setup the forward binding
        ZeroMQBindingDefinition referenceDefinition = new ZeroMQBindingDefinition("binding.zeromq", metadata);
        LogicalBinding<ZeroMQBindingDefinition> referenceBinding = new LogicalBinding<>(referenceDefinition, source, deployable);
        referenceDefinition.setTargetUri(URI.create(UriHelper.getBaseName(target.getUri())));
        referenceBinding.setAssigned(true);
        source.addBinding(referenceBinding);

        boolean bindTarget = bindTarget(target);

        if (bindTarget) {
            ZeroMQBindingDefinition serviceDefinition = new ZeroMQBindingDefinition("binding.zeromq", metadata);
            LogicalBinding<ZeroMQBindingDefinition> serviceBinding = new LogicalBinding<>(serviceDefinition, target, deployable);
            serviceBinding.setAssigned(true);
            target.addBinding(serviceBinding);
        }

        // check if the interface is bidirectional
        ServiceContract targetContract = target.getDefinition().getServiceContract();
        if (targetContract.getCallbackContract() != null) {
            // setup callback bindings
            ZeroMQMetadata callbackMetadata = createMetadata();

            ZeroMQBindingDefinition callbackReferenceDefinition = new ZeroMQBindingDefinition("binding.zeromq.callback", callbackMetadata);
            LogicalBinding<ZeroMQBindingDefinition> callbackReferenceBinding = new LogicalBinding<>(callbackReferenceDefinition,
                                                                                                                           source,
                                                                                                                           deployable);
            callbackReferenceBinding.setAssigned(true);
            source.addCallbackBinding(callbackReferenceBinding);

            if (bindTarget) {
                ZeroMQBindingDefinition callbackServiceDefinition = new ZeroMQBindingDefinition("binding.zeromq.callback", callbackMetadata);
                LogicalBinding<ZeroMQBindingDefinition> callbackServiceBinding = new LogicalBinding<>(callbackServiceDefinition,
                                                                                                                             target,
                                                                                                                             deployable);
                callbackServiceBinding.setAssigned(true);
                target.addCallbackBinding(callbackServiceBinding);
            }
        }
    }

    public void bind(LogicalChannel channel) throws BindingSelectionException {
        ZeroMQMetadata metadata = createMetadata();
        metadata.setChannelName(channel.getDefinition().getName());
        ZeroMQBindingDefinition definition = new ZeroMQBindingDefinition("binding.zeromq", metadata);
        LogicalBinding<ZeroMQBindingDefinition> binding = new LogicalBinding<>(definition, channel);
        channel.addBinding(binding);
    }

    /**
     * Determines if the target should be bound, i.e. if it has not already been bound by binding.sca or is remote (and not hosted on the current runtime).
     *
     * @param target the target
     * @return true if the target should be bound
     */
    private boolean bindTarget(LogicalService target) {
        if (target.getServiceContract() instanceof RemoteServiceContract) {
            return false;
        }
        for (LogicalBinding<?> binding : target.getBindings()) {
            if (binding.isAssigned()) {
                return false;
            }
        }
        return true;
    }

    private ZeroMQMetadata createMetadata() {
        ZeroMQMetadata metadata = new ZeroMQMetadata();
        metadata.setHighWater(highWater);
        metadata.setMulticastRate(multicastRate);
        metadata.setMulticastRecovery(multicastRecovery);
        metadata.setReceiveBuffer(receiveBuffer);
        metadata.setSendBuffer(sendBuffer);
        return metadata;
    }

}
