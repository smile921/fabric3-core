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
package org.fabric3.binding.web.runtime.channel;

import javax.servlet.http.HttpServletRequest;

import org.fabric3.spi.container.channel.EventStream;

/**
 * Denies subscription requests for a channel.
 */
public class DenyChannelSubscriber implements ChannelSubscriber {

    public void subscribe(HttpServletRequest request) throws PublishDeniedException {
        throw new PublishDeniedException();
    }

    public EventStream getEventStream() {
        return null;
    }

    public int getSequence() {
        return 0;
    }

}
