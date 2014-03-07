package org.fabric3.binding.ws.model;

import java.net.URI;

import org.fabric3.model.type.ModelObject;

/**
 * A WSA Address.
 */
public class EndpointReference extends ModelObject {
    private URI address;

    public EndpointReference(URI address) {
        this.address = address;
    }

    public URI getAddress() {
        return address;
    }
}