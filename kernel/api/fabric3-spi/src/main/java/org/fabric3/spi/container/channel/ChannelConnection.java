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
package org.fabric3.spi.container.channel;

/**
 * Contains one or more event streams for transmitting events to or from a channel. Channel connections may exist between:
 *
 * - A component producer and a channel
 *
 * - A component producer and a channel binding
 *
 * - A channel binding and a channel
 *
 * - A channel and a component consumer
 */
public interface ChannelConnection {

    /**
     * Returns the sequence this connection should receive events from a channel.
     *
     * @return the sequence
     */
    public int getSequence();

    /**
     * Returns the connection event stream.
     *
     * @return the connection event stream
     */
    EventStream getEventStream();

}