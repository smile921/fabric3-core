/*
 * Fabric3
 * Copyright (c) 2009-2012 Metaform Systems
 *
 * Fabric3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version, with the
 * following exception:
 *
 * Linking this software statically or dynamically with other
 * modules is making a combined work based on this software.
 * Thus, the terms and conditions of the GNU General Public
 * License cover the whole combination.
 *
 * As a special exception, the copyright holders of this software
 * give you permission to link this software with independent
 * modules to produce an executable, regardless of the license
 * terms of these independent modules, and to copy and distribute
 * the resulting executable under terms of your choice, provided
 * that you also meet, for each linked independent module, the
 * terms and conditions of the license of that module. An
 * independent module is a module which is not derived from or
 * based on this software. If you modify this software, you may
 * extend this exception to your version of the software, but
 * you are not obligated to do so. If you do not wish to do so,
 * delete this exception statement from your version.
 *
 * Fabric3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the
 * GNU General Public License along with Fabric3.
 * If not, see <http://www.gnu.org/licenses/>.
*/
package org.fabric3.binding.ws.metro.provision;

import javax.xml.namespace.QName;
import java.io.Serializable;

/**
 * Encapsulates endpoint information for the reference side of an invocation chain.
 */
public abstract class AbstractEndpointDefinition implements Serializable {
    private static final long serialVersionUID = -8322624061436929156L;
    private QName serviceName;
    private QName portName;
    private String wsdl;

    /**
     * Constructor.
     *
     * @param serviceName the qualified name of the target service
     * @param portName    the port name
     * @param wsdl        the serialized wsdl
     */
    public AbstractEndpointDefinition(QName serviceName, QName portName, String wsdl) {
        this.serviceName = serviceName;
        this.portName = portName;
        this.wsdl = wsdl;
    }

    /**
     * Returns the qualified service name.
     *
     * @return the qualified service name
     */
    public QName getServiceName() {
        return serviceName;
    }

    /**
     * Returns the qualified port name.
     *
     * @return the qualified port name
     */
    public QName getPortName() {
        return portName;
    }

    /**
     * Returns a serialized WSDL specified using wsdlElement or wsdlLocation, or null if one is not specified. This WSDL may be overriden by a generated one if
     * policy is specified on the reference. Otherwise, it should be used to create JAX-WS reference proxies.
     *
     * @return the serialized WSDL or null
     */
    public String getWsdl() {
        return wsdl;
    }
}