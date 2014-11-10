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
package org.fabric3.runtime.weblogic.federation;

import javax.management.JMException;
import javax.naming.Binding;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.fabric3.api.annotation.monitor.Monitor;
import org.fabric3.federation.deployment.command.DeploymentCommand;
import org.fabric3.federation.deployment.command.RuntimeUpdateCommand;
import org.fabric3.federation.deployment.command.RuntimeUpdateResponse;
import org.fabric3.runtime.weblogic.cluster.ChannelException;
import org.fabric3.runtime.weblogic.cluster.RuntimeChannel;
import org.fabric3.spi.classloader.SerializationService;
import org.fabric3.spi.container.ContainerException;
import org.fabric3.spi.container.executor.CommandExecutorRegistry;
import org.fabric3.spi.container.command.Response;
import org.fabric3.spi.container.command.ResponseCommand;
import org.fabric3.spi.federation.topology.MessageException;
import org.fabric3.spi.federation.topology.MessageReceiver;
import org.fabric3.spi.federation.topology.ParticipantTopologyService;
import org.fabric3.spi.federation.topology.TopologyListener;
import org.fabric3.spi.federation.topology.ZoneChannelException;
import org.fabric3.spi.runtime.event.EventService;
import org.fabric3.spi.runtime.event.Fabric3EventListener;
import org.fabric3.spi.runtime.event.JoinDomain;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Service;
import static org.fabric3.runtime.weblogic.federation.Constants.CONTROLLER_CONTEXT;
import static org.fabric3.runtime.weblogic.federation.Constants.DYNAMIC_CHANNEL_CONTEXT;
import static org.fabric3.runtime.weblogic.federation.Constants.PARTICIPANT_CONTEXT;

/**
 * Provides domain communication for a participant runtime using the WebLogic clustered JNDI tree.
 */
@Service(ParticipantTopologyService.class)
@EagerInit
public class WebLogicParticipantTopologyService implements ParticipantTopologyService {
    private static final String JNDI_FACTORY = "weblogic.jndi.WLInitialContextFactory";

    private ExecutorService executorService;
    private WebLogicTopologyMonitor monitor;
    private EventService eventService;
    private SerializationService serializationService;
    private CommandExecutorRegistry executorRegistry;
    private JmxHelper jmxHelper;
    private RuntimeChannel runtimeChannel;

    private RuntimeChannel controllerChannel;
    private String runtimeName;
    private String adminServerUrl = "t3://localhost:7001";
    private boolean synchronize = true;
    private String zoneName;

    private List<ChannelOpenRequest> channelRequests = new ArrayList<>();
    private Set<String> openChannels = new HashSet<>();

    public WebLogicParticipantTopologyService(@Reference EventService eventService,
                                              @Reference SerializationService serializationService,
                                              @Reference CommandExecutorRegistry executorRegistry,
                                              @Reference ExecutorService executorService,
                                              @Reference JmxHelper jmxHelper,
                                              @Monitor WebLogicTopologyMonitor monitor) {
        this.eventService = eventService;
        this.serializationService = serializationService;
        this.executorRegistry = executorRegistry;
        this.jmxHelper = jmxHelper;
        this.executorService = executorService;
        this.monitor = monitor;
    }

    @Property(required = false)
    public void setAdminServerUrl(String adminServerUrl) {
        this.adminServerUrl = adminServerUrl;
    }

    /**
     * Property to configure whether the runtime should attempt an update by controller.
     *
     * @param synchronize true if the runtime should attempt an update (the default)
     */
    @Property(required = false)
    public void setSynchronize(boolean synchronize) {
        this.synchronize = synchronize;
    }

    @Init
    public void init() throws JMException {
        runtimeName = jmxHelper.getRuntimeJmxAttribute(String.class, "ServerRuntime/Name");
        zoneName = jmxHelper.getRuntimeJmxAttribute(String.class, "DomainConfiguration/Name");
        eventService.subscribe(JoinDomain.class, new JoinDomainListener());
        runtimeChannel = new RuntimeChannelImpl(runtimeName, executorRegistry, serializationService, monitor);
    }

    public boolean isZoneLeader() {
        return false;
    }

    public void register(TopologyListener listener) {

    }

    public void deregister(TopologyListener listener) {

    }

    public boolean isControllerAvailable() {
        return true;
    }

    public String getZoneLeaderName() {
        return null;
    }

    public Response sendSynchronousToController(ResponseCommand command, long timeout) throws MessageException {
        try {
            byte[] payload = serializationService.serialize(command);
            byte[] responsePayload = controllerChannel.sendSynchronous(payload);
            return serializationService.deserialize(Response.class, responsePayload);
        } catch (ChannelException | ClassNotFoundException | IOException e) {
            throw new MessageException(e);
        }
    }

    public Response sendSynchronous(String destinationName, ResponseCommand command, long timeout) throws MessageException {
        Context rootContext = null;
        try {
            rootContext = getRootContext();
            byte[] payload = serializationService.serialize(command);
            NamingEnumeration<Binding> enumeration = rootContext.listBindings(PARTICIPANT_CONTEXT);
            while (enumeration.hasMoreElements()) {
                Binding binding = enumeration.next();
                if (RuntimeChannel.class.getName().equals(binding.getClassName())) {
                    RuntimeChannel channel = (RuntimeChannel) binding.getObject();
                    if (destinationName.equals(channel.getRuntimeName()) && channel.isActive()) {
                        byte[] responsePayload = runtimeChannel.sendSynchronous(payload);
                        return serializationService.deserialize(Response.class, responsePayload);
                    }
                }
            }
            throw new MessageException("Runtime not found: " + destinationName);
        } catch (NamingException | ClassNotFoundException | ChannelException | IOException e) {
            throw new MessageException(e);
        } finally {
            JndiHelper.close(rootContext);
        }
    }

    public boolean isChannelOpen(String name) {
        return openChannels.contains(name);
    }

    public void openChannel(String name, String configuration, MessageReceiver receiver, TopologyListener listener) throws ZoneChannelException {
        if (isChannelOpen(name)) {
            throw new ZoneChannelException("Channel already open: " + name);
        }

        Context rootContext = null;
        Context dynamicChannelContext = null;
        RuntimeChannelImpl channel = new RuntimeChannelImpl(runtimeName, executorRegistry, serializationService, receiver, monitor);
        try {
            rootContext = getRootContext();
            dynamicChannelContext = JndiHelper.getContext(DYNAMIC_CHANNEL_CONTEXT, rootContext);
            dynamicChannelContext.bind(name + ":" + runtimeName, channel);
            openChannels.add(name);
        } catch (NameAlreadyBoundException e) {
            try {
                dynamicChannelContext.rebind(name + ":" + runtimeName, channel);
            } catch (NamingException ex) {
                // controller may not be available
                monitor.errorMessage("Controller not available - queueing request for retry");
                monitor.errorDetail(ex);
                channelRequests.add(new ChannelOpenRequest(name, receiver));
            }
        } catch (NamingException e) {
            // controller may not be available
            monitor.errorMessage("Controller not available - queueing request for retry");
            monitor.errorDetail(e);
            channelRequests.add(new ChannelOpenRequest(name, receiver));
        } finally {
            JndiHelper.close(rootContext, dynamicChannelContext);
        }
    }

    public void closeChannel(String name) throws ZoneChannelException {
        Context rootContext = null;
        Context dynamicChannelContext = null;
        try {
            rootContext = getRootContext();
            dynamicChannelContext = JndiHelper.getContext(DYNAMIC_CHANNEL_CONTEXT, rootContext);
            dynamicChannelContext.unbind(name + ":" + runtimeName);
            openChannels.remove(name);
        } catch (CommunicationException e) {
            // Controller was not available. Ignore since the controller could have been shutdown before the participant
        } catch (NamingException e) {
            throw new ZoneChannelException(e);
        } finally {
            JndiHelper.close(rootContext, dynamicChannelContext);
        }
    }

    public void sendAsynchronous(String name, Serializable message) throws MessageException {
        Context rootContext = null;
        try {
            rootContext = getRootContext();
            byte[] payload = serializationService.serialize(message);
            NamingEnumeration<Binding> enumeration = rootContext.listBindings(DYNAMIC_CHANNEL_CONTEXT);
            while (enumeration.hasMoreElements()) {
                Binding binding = enumeration.next();
                if (RuntimeChannel.class.getName().equals(binding.getClassName())) {
                    RuntimeChannel channel = (RuntimeChannel) binding.getObject();
                    if (binding.getName().startsWith(name + ":")) {
                        if (channel.getRuntimeName().equals(runtimeName)) {
                            // don't send to self
                            continue;
                        }
                        channel.publish(payload);
                    }
                }
            }
        } catch (NamingException | ChannelException | IOException e) {
            throw new MessageException(e);
        } finally {
            JndiHelper.close(rootContext);
        }
    }

    public void sendAsynchronous(String destinationName, String name, Serializable message) throws MessageException {
        Context rootContext = null;
        try {
            rootContext = getRootContext();
            byte[] payload = serializationService.serialize(message);
            NamingEnumeration<Binding> enumeration = rootContext.listBindings(DYNAMIC_CHANNEL_CONTEXT);
            while (enumeration.hasMoreElements()) {
                Binding binding = enumeration.next();
                if (RuntimeChannel.class.getName().equals(binding.getClassName())) {
                    RuntimeChannel channel = (RuntimeChannel) binding.getObject();
                    if (binding.getName().equals(name + ":" + destinationName)) {
                        channel.publish(payload);
                        return;
                    }
                }
            }
            throw new MessageException("Runtime not found: " + destinationName);
        } catch (NamingException | ChannelException | IOException e) {
            throw new MessageException(e);
        } finally {
            JndiHelper.close(rootContext);
        }
    }

    /**
     * Initializes JNDI contexts used for domain communications.
     *
     * @return true if the contexts were initialized; false if there was an error
     */
    private boolean initJndiContexts() {
        monitor.connectingToAdminServer();
        Context rootContext = null;
        Context participantContext = null;
        Context controllerContext = null;
        Context dynamicChannelContext = null;
        try {
            rootContext = getRootContext();
            controllerContext = JndiHelper.getContext(CONTROLLER_CONTEXT, rootContext);
            // lookup the controller channel RMI stub
            controllerChannel = (RuntimeChannel) controllerContext.lookup(Constants.CONTROLLER_CHANNEL);
            if (!controllerChannel.isActive()) {
                controllerChannel = null;
                monitor.errorMessage("Error joining the domain, as the controller is not active. Scheduled for retry.");
                return false;
            }
            participantContext = JndiHelper.getContext(PARTICIPANT_CONTEXT, rootContext);
            try {
                participantContext.bind(runtimeName, runtimeChannel);
            } catch (NameAlreadyBoundException e) {
                participantContext.rebind(runtimeName, runtimeChannel);
            }

            dynamicChannelContext = JndiHelper.getContext(DYNAMIC_CHANNEL_CONTEXT, rootContext);

            // initialize dynamic channels
            for (ChannelOpenRequest request : channelRequests) {
                MessageReceiver receiver = request.getReceiver();
                RuntimeChannelImpl channel = new RuntimeChannelImpl(runtimeName, executorRegistry, serializationService, receiver, monitor);
                try {
                    dynamicChannelContext.bind(request.getName() + ":" + runtimeName, channel);
                } catch (NameAlreadyBoundException e) {
                    dynamicChannelContext.rebind(request.getName() + ":" + runtimeName, channel);
                }
            }

            return true;
        } catch (NamingException e) {
            monitor.errorMessage("Error joining the domain, possibly because the controller is not available. Scheduled for retry.");
            monitor.errorDetail(e);
            return false;
        } finally {
            JndiHelper.close(participantContext, controllerContext, rootContext, dynamicChannelContext);
        }
    }

    private Context getRootContext() throws NamingException {
        Context rootContext;// lookup the controller context on the admin server
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
        env.put(Context.PROVIDER_URL, adminServerUrl);
        rootContext = new InitialContext(env);
        return rootContext;
    }

    /**
     * Performs a runtime update by querying the admin server.
     *
     * @return true if the runtime was updated
     */
    private boolean update() {
        if (!synchronize) {
            return true;
        }
        monitor.updating();
        RuntimeUpdateCommand command = new RuntimeUpdateCommand(runtimeName, zoneName, null);
        Response response;
        try {
            byte[] payload = serializationService.serialize(command);
            if (controllerChannel == null || !controllerChannel.isActive()) {
                // controller not available
                return false;
            }
            byte[] responsePayload = controllerChannel.sendSynchronous(payload);
            response = serializationService.deserialize(Response.class, responsePayload);
        } catch (RemoteException e) {
            monitor.error(e);
            return false;
        } catch (ChannelException e) {
            monitor.error(e);
            return false;
        } catch (IOException e) {
            // programming error
            monitor.error(e);
            return false;
        } catch (ClassNotFoundException e) {
            // programming error
            monitor.error(e);
            return false;
        }
        RuntimeUpdateResponse updateResponse = (RuntimeUpdateResponse) response;
        if (!updateResponse.isUpdated()) {
            // not updated, wait until a controller becomes available
            return false;
        }
        try {
            DeploymentCommand deploymentCommand = updateResponse.getDeploymentCommand();
            executorRegistry.execute(deploymentCommand);
        } catch (ContainerException e) {
            monitor.error(e);
            // return true to avoid multiple attempts to update the runtime in the case of a deployment error
            return true;
        }
        monitor.updated();
        return true;
    }

    /**
     * Event listener that binds the runtime Channel to the JNDI tree when the JoinDomain event is fired.
     */
    private class JoinDomainListener implements Fabric3EventListener<JoinDomain> {

        public void onEvent(JoinDomain event) {
            if (!initJndiContexts()) {
                monitor.adminServerUnavailable();
                // admin server is not available, schedule work to retry periodically
                executorService.execute(new Work());
                return;
            }
            update();
        }
    }

    /**
     * Used to asynchronously attempt to initialize and update the runtime if the controller is not available when it boots. This polling mechanism is required
     * as WebLogic remote JNDI contexts do not implement EventContext to receive callbacks when a JNDI object changes (such as a controller channel becoming
     * available).
     */
    private class Work implements Runnable {

        public void run() {
            while (true) {
                if (initJndiContexts() && update()) {
                    return;
                }
                try {
                    Thread.sleep(10000);  // wait 10 seconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private class ChannelOpenRequest {
        private String name;
        private MessageReceiver receiver;

        private ChannelOpenRequest(String name, MessageReceiver receiver) {
            this.name = name;
            this.receiver = receiver;
        }

        public String getName() {
            return name;
        }

        public MessageReceiver getReceiver() {
            return receiver;
        }
    }

}
