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
package org.fabric3.binding.jms.generator;

import java.net.URI;
import java.util.List;

import org.fabric3.api.annotation.wire.Key;
import org.fabric3.api.binding.jms.model.DestinationType;
import org.fabric3.api.binding.jms.model.JmsBinding;
import org.fabric3.api.binding.jms.model.JmsBindingMetadata;
import org.fabric3.api.host.Fabric3Exception;
import org.fabric3.api.model.type.component.Consumer;
import org.fabric3.api.model.type.contract.DataType;
import org.fabric3.binding.jms.spi.generator.JmsResourceProvisioner;
import org.fabric3.binding.jms.spi.provision.JmsChannelBindingDefinition;
import org.fabric3.binding.jms.spi.provision.JmsConnectionSource;
import org.fabric3.binding.jms.spi.provision.JmsConnectionTarget;
import org.fabric3.binding.jms.spi.provision.SessionType;
import org.fabric3.spi.domain.generator.channel.ConnectionBindingGenerator;
import org.fabric3.spi.model.instance.LogicalBinding;
import org.fabric3.spi.model.instance.LogicalConsumer;
import org.fabric3.spi.model.instance.LogicalProducer;
import org.fabric3.spi.model.physical.ChannelDeliveryType;
import org.fabric3.spi.model.physical.PhysicalChannelBindingDefinition;
import org.fabric3.spi.model.physical.PhysicalConnectionSourceDefinition;
import org.fabric3.spi.model.physical.PhysicalConnectionTargetDefinition;
import org.fabric3.spi.model.physical.PhysicalDataTypes;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Reference;

/**
 * Connection binding generator that creates source and target definitions for bound channels, producers, and consumers.
 */
@EagerInit
@Key("org.fabric3.api.binding.jms.model.JmsBinding")
public class JmsConnectionBindingGenerator implements ConnectionBindingGenerator<JmsBinding> {
    private static final String JAXB = "JAXB";

    // optional provisioner for host runtimes to receive callbacks
    private JmsResourceProvisioner provisioner;

    @Reference(required = false)
    public void setProvisioner(JmsResourceProvisioner provisioner) {
        this.provisioner = provisioner;
    }

    public PhysicalConnectionSourceDefinition generateConnectionSource(LogicalConsumer consumer,
                                                                       LogicalBinding<JmsBinding> binding,
                                                                       ChannelDeliveryType deliveryType) throws Fabric3Exception {
        JmsBindingMetadata metadata = binding.getDefinition().getJmsMetadata().snapshot();

        SessionType sessionType = getSessionType(binding);

        JmsGeneratorHelper.generateDefaultFactoryConfiguration(metadata.getConnectionFactory(), sessionType);
        URI uri = consumer.getUri();

        // set the client id specifier
        if (metadata.getSubscriptionId() == null && metadata.isDurable()) {
            metadata.setSubscriptionId(JmsGeneratorHelper.getSubscriptionId(uri));
        }
        String specifier = metadata.getSubscriptionId();
        metadata.setSubscriptionId(specifier);

        metadata.getDestination().setType(DestinationType.TOPIC);  // only use topics for channels
        Consumer<?> consumerDefinition = consumer.getDefinition();
        DataType dataType = isJAXB(consumerDefinition.getTypes()) ? PhysicalDataTypes.JAXB : PhysicalDataTypes.JAVA_TYPE;
        JmsConnectionSource connectionSource = new JmsConnectionSource(uri, metadata, dataType, sessionType);
        if (provisioner != null) {
            provisioner.generateConnectionSource(connectionSource);
        }
        return connectionSource;
    }

    private SessionType getSessionType(LogicalBinding<JmsBinding> binding) {
        return binding.getDefinition().getJmsMetadata().isClientAcknowledge() ? SessionType.CLIENT_ACKNOWLEDGE : SessionType.AUTO_ACKNOWLEDGE;
    }

    public PhysicalConnectionTargetDefinition generateConnectionTarget(LogicalProducer producer,
                                                                       LogicalBinding<JmsBinding> binding,
                                                                       ChannelDeliveryType deliveryType) throws Fabric3Exception {
        URI uri = binding.getDefinition().getTargetUri();
        JmsBindingMetadata metadata = binding.getDefinition().getJmsMetadata().snapshot();

        JmsGeneratorHelper.generateDefaultFactoryConfiguration(metadata.getConnectionFactory(), SessionType.AUTO_ACKNOWLEDGE);

        DataType type = isJAXB(producer.getStreamOperation().getDefinition().getInputTypes()) ? PhysicalDataTypes.JAXB : PhysicalDataTypes.JAVA_TYPE;

        JmsConnectionTarget connectionTarget = new JmsConnectionTarget(uri, metadata, type);
        if (provisioner != null) {
            provisioner.generateConnectionTarget(connectionTarget);
        }
        return connectionTarget;
    }

    public PhysicalChannelBindingDefinition generateChannelBinding(LogicalBinding<JmsBinding> binding, ChannelDeliveryType deliveryType) {
        // a binding definition needs to be created even though it is not used so the channel is treated as bound (e.g. its implementation will be sync)
        return new JmsChannelBindingDefinition();
    }

    private boolean isJAXB(List<DataType> eventTypes) {
        for (DataType eventType : eventTypes) {
            if (JAXB.equals(eventType.getDatabinding())) {
                return true;
            }
        }
        return false;
    }

}