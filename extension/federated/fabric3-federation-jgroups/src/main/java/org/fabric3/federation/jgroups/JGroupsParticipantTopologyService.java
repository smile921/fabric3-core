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
package org.fabric3.federation.jgroups;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import org.fabric3.api.annotation.management.Management;
import org.fabric3.api.annotation.management.ManagementOperation;
import org.fabric3.api.annotation.monitor.Monitor;
import org.fabric3.api.host.runtime.HostInfo;
import org.fabric3.federation.deployment.command.ControllerAvailableCommand;
import org.fabric3.federation.deployment.command.DeploymentCommand;
import org.fabric3.federation.deployment.command.RuntimeUpdateCommand;
import org.fabric3.federation.deployment.command.RuntimeUpdateResponse;
import org.fabric3.spi.container.ContainerException;
import org.fabric3.spi.container.executor.CommandExecutor;
import org.fabric3.spi.container.executor.CommandExecutorRegistry;
import org.fabric3.spi.container.command.Command;
import org.fabric3.spi.container.command.Response;
import org.fabric3.spi.container.command.ResponseCommand;
import org.fabric3.spi.federation.topology.ControllerNotFoundException;
import org.fabric3.spi.federation.topology.MessageException;
import org.fabric3.spi.federation.topology.MessageReceiver;
import org.fabric3.spi.federation.topology.ParticipantTopologyService;
import org.fabric3.spi.federation.topology.RemoteSystemException;
import org.fabric3.spi.federation.topology.TopologyListener;
import org.fabric3.spi.federation.topology.ZoneChannelException;
import org.fabric3.spi.runtime.event.EventService;
import org.fabric3.spi.runtime.event.Fabric3EventListener;
import org.fabric3.spi.runtime.event.JoinDomain;
import org.fabric3.spi.runtime.event.RuntimeStop;
import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.View;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.util.UUID;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;
import org.w3c.dom.Element;

/**
 *
 */
@EagerInit
@Management(name = "ParticipantTopologyService", path = "/runtime/federation/zone/view")
public class JGroupsParticipantTopologyService extends AbstractTopologyService implements ParticipantTopologyService {
    private static final int NOT_UPDATED = -1;
    private static final int UPDATED = 1;

    private String zoneName;
    private Element channelConfig;
    private JChannel domainChannel;
    private Fabric3EventListener<JoinDomain> joinListener;
    private Fabric3EventListener<RuntimeStop> stopListener;
    private MessageDispatcher domainDispatcher;
    private boolean synchronize = true;
    private final Object viewLock;
    private TopologyListenerMultiplexer multiplexer;

    private Map<String, Channel> channels = new ConcurrentHashMap<>();

    private int state = NOT_UPDATED;

    public JGroupsParticipantTopologyService(@Reference HostInfo info,
                                             @Reference CommandExecutorRegistry executorRegistry,
                                             @Reference EventService eventService,
                                             @Reference Executor executor,
                                             @Reference JGroupsHelper helper,
                                             @Monitor TopologyServiceMonitor monitor) {
        super(info, executorRegistry, eventService, executor, helper, monitor);
        zoneName = info.getZoneName();
        viewLock = new Object();
        multiplexer = new TopologyListenerMultiplexer(helper, viewLock);
    }

    @Property(required = false)
    public void setChannelConfig(Element config) {
        this.channelConfig = (Element) config.getElementsByTagName("config").item(0);
    }

    @Reference(required = false)
    public void setTopologyListeners(List<TopologyListener> listeners) {
        this.multiplexer.addAll(listeners);
    }

    /**
     * Property to configure whether the runtime should attempt an update by querying a zone leader or the controller. In some topologies, the runtime may pull
     * deployment information from a persistent store, which eliminates the need to update via a peer or the controller.
     *
     * @param synchronize true if the runtime should attempt an update (the default)
     */
    @Property(required = false)
    public void setSynchronize(boolean synchronize) {
        this.synchronize = synchronize;
    }

    @Init
    public void init() throws Exception {
        super.init();
        if (!synchronize) {
            state = UPDATED;
        }
        ControllerAvailableCommandExecutor executor = new ControllerAvailableCommandExecutor();
        executorRegistry.register(ControllerAvailableCommand.class, executor);

        if (channelConfig != null) {
            domainChannel = new JChannel(channelConfig);
        } else {
            domainChannel = new JChannel();
        }
        domainChannel.setName(info.getRuntimeName());
        initializeChannel(domainChannel);

        Fabric3MessageListener messageListener = new Fabric3MessageListener();
        Fabric3RequestHandler requestHandler = new Fabric3RequestHandler();
        domainDispatcher = new MessageDispatcher(domainChannel, messageListener, multiplexer, requestHandler);
    }

    public void register(TopologyListener listener) {
        multiplexer.add(listener);
    }

    public void deregister(TopologyListener listener) {
        multiplexer.remove(listener);
    }

    @ManagementOperation(description = "True if the runtime is the zone leader")
    public boolean isZoneLeader() {
        View view = domainChannel.getView();
        Address address = domainChannel.getAddress();
        return view != null && address != null && address.equals(helper.getZoneLeader(zoneName, view));
    }

    @ManagementOperation(description = "True if the controller is reachable")
    public boolean isControllerAvailable() {
        View view = domainChannel.getView();
        return view != null && helper.getController(view) != null;
    }

    @ManagementOperation(description = "The name of the zone leader")
    public String getZoneLeaderName() {
        View view = domainChannel.getView();
        if (view == null) {
            return null;
        }
        Address address = helper.getZoneLeader(zoneName, view);
        if (address == null) {
            return null;
        }
        return UUID.get(address);
    }

    public Response sendSynchronous(String runtimeName, ResponseCommand command, long timeout) throws MessageException {
        View view = domainChannel.getView();
        if (view == null) {
            throw new MessageException("Federation channel closed or not connected when sending message to: " + runtimeName);
        }
        Address address = helper.getRuntimeAddress(runtimeName, view);
        if (address == null) {
            throw new MessageException("Runtime not found: " + runtimeName);
        }
        return send(address, command, timeout);
    }

    public Response sendSynchronousToController(ResponseCommand command, long timeout) throws MessageException {
        Address controller = helper.getController(domainChannel.getView());
        if (controller == null) {
            throw new ControllerNotFoundException("Controller could not be located");
        }
        return send(controller, command, timeout);
    }

    public boolean isChannelOpen(String name) {
        return channels.containsKey(name);
    }

    public void openChannel(String name, String configuration, MessageReceiver receiver, TopologyListener listener) throws ZoneChannelException {
        if (channels.containsKey(name)) {
            throw new ZoneChannelException("Channel already open:" + name);
        }
        try {

            Channel channel;
            if (configuration != null) {
                channel = new JChannel(configuration);
            } else if (channelConfig != null) {
                channel = new JChannel(channelConfig);
            } else {
                channel = new JChannel();
            }
            channel.setName(runtimeName);
            initializeChannel(channel);
            channels.put(name, channel);

            Object viewLock = new Object();
            List<TopologyListener> listeners = Collections.singletonList(listener);
            TopologyListenerMultiplexer multiplexer = (listener != null) ? new TopologyListenerMultiplexer(helper, viewLock, listeners) : null;

            DelegatingReceiver delegatingReceiver = new DelegatingReceiver(channel, receiver, helper, multiplexer, monitor);
            channel.setReceiver(delegatingReceiver);
            channel.connect(info.getDomain().getAuthority() + ":" + name);
        } catch (Exception e) {
            throw new ZoneChannelException(e);
        }
    }

    public void closeChannel(String name) throws ZoneChannelException {
        Channel channel = channels.remove(name);
        if (channel == null) {
            throw new ZoneChannelException("Channel not found: " + name);
        }
        channel.close();
    }

    public void sendAsynchronous(String name, Serializable message) throws MessageException {
        Channel channel = channels.get(name);
        if (channel == null) {
            throw new MessageException("Channel not found: " + name);
        }
        try {
            byte[] payload = helper.serialize(message);
            Message jMessage = new Message(null, null, payload);
            channel.send(jMessage);
        } catch (Exception e) {
            throw new MessageException(e);
        }
    }

    public void sendAsynchronous(String runtimeName, String name, Serializable message) throws MessageException {
        Channel channel = channels.get(name);
        if (channel == null) {
            throw new MessageException("Channel not found: " + name);
        }
        try {
            View view = channel.getView();
            if (view == null) {
                throw new MessageException("Federation channel closed or not connected when sending message to: " + runtimeName);
            }
            Address address = helper.getRuntimeAddress(runtimeName, view);
            byte[] payload = helper.serialize(message);
            Message jMessage = new Message(address, null, payload);
            channel.send(jMessage);
        } catch (Exception e) {
            throw new MessageException(e);
        }
    }

    Fabric3EventListener<JoinDomain> getJoinListener() {
        if (joinListener == null) {
            joinListener = new JoinEventListener();
        }
        return joinListener;
    }

    Fabric3EventListener<RuntimeStop> getStopListener() {
        if (stopListener == null) {
            stopListener = new RuntimeStopEventListener();
        }
        return stopListener;
    }

    @Override
    JChannel getDomainChannel() {
        return domainChannel;
    }

    private Response send(Address address, Command command, long timeout) throws MessageException {
        try {
            Address sourceAddress = domainChannel.getAddress();
            byte[] payload = helper.serialize(command);
            Message message = new Message(address, sourceAddress, payload);
            RequestOptions options = new RequestOptions(ResponseMode.GET_ALL, timeout);
            Object val = domainDispatcher.sendMessage(message, options);
            assert val instanceof byte[] : " expected byte[] for response";
            return (Response) helper.deserialize(((byte[]) val));
        } catch (Exception e) {
            throw new MessageException("Error sending message to: " + runtimeName, e);
        }
    }

    /**
     * Attempts to update the runtime with the current set of deployments for the zone. The zone leader (i.e. oldest runtime in the zone) is queried for the
     * deployment commands. If the zone leader is unavailable or has not been updated, the controller is queried.
     *
     * @throws MessageException if an error is encountered during update
     */
    private void update() throws MessageException {
        // send the sync request
        View view = domainChannel.getView();
        Address address = helper.getZoneLeader(zoneName, view);
        if (address != null && !domainChannel.getAddress().equals(address)) {
            // check if current runtime is the zone leader - if not, attempt to retrieve cached deployment from it
            try {
                if (update(address)) {
                    return;
                }
            } catch (MessageException e) {
                monitor.error("Error retrieving deployment from zone leader: " + zoneName, e);
            }
        }
        // check the controller
        address = helper.getController(view);
        if (address == null) {
            // controller is not present
            monitor.updateDeferred();
            return;
        }
        update(address);
    }

    /**
     * Performs the actual runtime update by querying the given runtime address.
     *
     * @param address the runtime address
     * @return true if the runtime was updated
     * @throws MessageException if an error is encountered during update
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private boolean update(Address address) throws MessageException {
        String name = UUID.get(address);
        monitor.updating(name);
        RuntimeUpdateCommand command = new RuntimeUpdateCommand(runtimeName, zoneName, null);
        Response response = send(address, command, defaultTimeout);
        if (response instanceof RemoteSystemException) {
            RemoteSystemException exception = (RemoteSystemException) response;
            throw new MessageException("Remote system exception from " + exception.getRuntimeName() + ": " + exception.getException().getMessage());
        } else if (!(response instanceof RuntimeUpdateResponse)) {
            throw new MessageException("Unknown response type: " + response.getClass());
        }
        RuntimeUpdateResponse updateResponse = (RuntimeUpdateResponse) response;
        if (!updateResponse.isUpdated()) {
            // not updated, wait until a controller becomes available
            return false;
        }
        // mark synchronized here to avoid multiple retries in case a deployment error is encountered
        state = UPDATED;
        try {
            DeploymentCommand deploymentCommand = updateResponse.getDeploymentCommand();
            executorRegistry.execute(deploymentCommand);
        } catch (ContainerException e) {
            throw new MessageException(e);
        }
        monitor.updated();
        return true;
    }

    class JoinEventListener implements Fabric3EventListener<JoinDomain> {

        public void onEvent(JoinDomain event) {
            try {
                domainChannel.connect(domainName);
                domainDispatcher.start();
                monitor.joinedDomain(runtimeName);
                if (synchronize) {
                    while (domainChannel.getView() == null) {
                        try {
                            // Wait until the first view is available. Notification will happen when the ZoneMemberListener is called on a
                            // different thread.
                            viewLock.wait(defaultTimeout);
                        } catch (InterruptedException e) {
                            monitor.error("Timeout attempting to join the domain", e);
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    update();
                }
            } catch (Exception e) {
                monitor.error("Error joining the domain", e);
            }
        }
    }

    class RuntimeStopEventListener implements Fabric3EventListener<RuntimeStop> {

        public void onEvent(RuntimeStop event) {
            if (domainDispatcher != null) {
                domainDispatcher.stop();
            }
            if (domainChannel != null && domainChannel.isConnected()) {
                domainChannel.disconnect();
                domainChannel.close();
            }
            for (Channel channel : channels.values()) {
                if (channel.isConnected()) {
                    channel.disconnect();
                    channel.close();
                }
            }
        }
    }

    private class ControllerAvailableCommandExecutor implements CommandExecutor<ControllerAvailableCommand> {

        public void execute(ControllerAvailableCommand command) throws ContainerException {
            if (UPDATED == state) {
                return;
            }
            try {
                // A controller is now available and this runtime has not been synchronized. This can happen when the first member in a zone becomes
                // available before a controller.
                View view = domainChannel.getView();
                Address controller = helper.getController(view);
                update(controller);
            } catch (MessageException e) {
                monitor.error("Error updating the runtime", e);
            }
        }
    }
}