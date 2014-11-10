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
package org.fabric3.binding.web.runtime.service;

import java.io.IOException;
import java.util.UUID;

import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResource.TRANSPORT;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.handler.AbstractReflectorAtmosphereHandler;

import org.fabric3.binding.web.runtime.common.BroadcasterManager;

/**
 *
 */
public class ServiceWebSocketHandler extends AbstractReflectorAtmosphereHandler {

    private ServiceManager serviceManager;
    private BroadcasterManager broadcasterManager;
    private ServiceMonitor monitor;


    public ServiceWebSocketHandler(ServiceManager serviceManager, BroadcasterManager broadcasterManager, ServiceMonitor monitor) {
        this.serviceManager = serviceManager;
        this.broadcasterManager = broadcasterManager;
        this.monitor = monitor;
    }


    public void onRequest(AtmosphereResource resource) throws IOException {
        AtmosphereRequest req = resource.getRequest();
        AtmosphereResponse res = resource.getResponse();
        String method = req.getMethod();

        // Suspend the response.
        if ("GET".equalsIgnoreCase(method)) {
            UUID uuid = UUID.randomUUID();
            // Log all events on the console, including WebSocket events.
            WebSocketServiceListener listener = new WebSocketServiceListener(uuid, monitor, serviceManager);
            resource.addEventListener(listener);

            res.setContentType("text/html;charset=ISO-8859-1");

            Broadcaster b = broadcasterManager.getServiceBroadcaster(uuid.toString(), resource.getAtmosphereConfig());
            resource.setBroadcaster(b);

            if (resource.transport() == TRANSPORT.LONG_POLLING) {
                req.setAttribute(ApplicationConfig.RESUME_ON_BROADCAST, Boolean.TRUE);
                resource.suspend(-1, false);
            } else {
                resource.suspend(-1);
            }
        } else if ("POST".equalsIgnoreCase(method)) {
            res.setStatus(500);
            monitor.error(new UnsupportedOperationException("No inbound messages allowed."));
        }
    }

    public void destroy() {
    }


}
