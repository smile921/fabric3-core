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
package org.fabric3.spi.federation.topology;

import java.io.Serializable;

import org.fabric3.api.host.Fabric3Exception;
import org.fabric3.spi.container.command.Command;

/**
 * Responsible for communications across a federated (distributed) domain.
 */
public interface NodeTopologyService {

    /**
     * Returns true if the current runtime is the zone leader.
     *
     * @return true if the current runtime is the zone leader
     */
    boolean isZoneLeader();

    /**
     * Registers a transient {@link TopologyListener}.
     *
     * @param listener the listener
     */
    void register(TopologyListener listener);

    /**
     * De-registers a transient {@link TopologyListener}.
     *
     * @param listener the listener
     */
    void deregister(TopologyListener listener);

    /**
     * Sends a command asynchronously to all runtimes in the domain.
     *
     * @param command the command
     * @throws Fabric3Exception if there is an error sending the message
     */
    void broadcast(Command command) throws Fabric3Exception;

    /**
     * Asynchronously sends a message over the given channel to the specified runtime.
     *
     * @param runtimeName the runtime
     * @param name        the channel name
     * @param message     the message
     * @throws Fabric3Exception if there is an error sending the message
     */
    void sendAsynchronous(String runtimeName, String name, Serializable message) throws Fabric3Exception;

    /**
     * Asynchronously sends a message over the given channel.
     *
     * @param channelName    the channel name
     * @param message the message
     * @throws Fabric3Exception if there is an error sending the message
     */
    void sendAsynchronous(String channelName, Serializable message) throws Fabric3Exception;

    /**
     * Opens a channel.
     *
     * @param name          the channel name
     * @param configuration the channel configuration or null to use the default configuration
     * @param receiver      the receiver to callback when a message is received
     * @param listener      an optional topology listener. May be null.
     * @throws Fabric3Exception if an error occurs opening the channel
     */
    void openChannel(String name, String configuration, MessageReceiver receiver, TopologyListener listener) throws Fabric3Exception;

    /**
     * Closes a channel.
     *
     * @param name the channel name
     * @throws Fabric3Exception if an error occurs closing the channel
     */
    void closeChannel(String name) throws Fabric3Exception;

}
