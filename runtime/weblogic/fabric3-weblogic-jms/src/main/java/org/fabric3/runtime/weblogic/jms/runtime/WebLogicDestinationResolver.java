/*
 * Fabric3
 * Copyright (c) 2009 Metaform Systems
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
package org.fabric3.runtime.weblogic.jms.runtime;

import java.util.Hashtable;
import javax.jms.Destination;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

import org.fabric3.binding.jms.spi.common.DestinationDefinition;
import org.fabric3.binding.jms.spi.runtime.JmsResolutionException;
import org.fabric3.binding.jms.spi.runtime.ProviderDestinationResolver;
import org.fabric3.host.RuntimeMode;
import org.fabric3.host.runtime.HostInfo;

/**
 * Attempts to resolve a destination against the cluster JNDI tree if the runtime is a participant in a cluster or against the local runtime JNDI tree
 * if the server is in single-VM or controller mode.
 *
 * @version $Rev$ $Date$
 */
public class WebLogicDestinationResolver implements ProviderDestinationResolver {
    private HostInfo info;

    private String adminServerUrl = "t3://localhost:7001";
    private Hashtable<String, String> environment;

    public WebLogicDestinationResolver(@Reference HostInfo info) {
        this.info = info;
    }

    @Property(required = false)
    public void setAdminServerUrl(String adminServerUrl) {
        this.adminServerUrl = adminServerUrl;
    }

    @Init
    public void init() {
        environment = new Hashtable<String, String>();
        if (RuntimeMode.PARTICIPANT == info.getRuntimeMode()) {
            environment.put(Context.PROVIDER_URL, adminServerUrl);
        }
    }

    public Destination resolve(DestinationDefinition definition) throws JmsResolutionException {
        InitialContext context = null;
        try {
            context = new InitialContext(environment);
            return (Destination) context.lookup(definition.getName());
        } catch (NameNotFoundException e) {
            return null;
        } catch (NamingException e) {
            throw new JmsResolutionException("Error resolving destination:" + definition.getName(), e);
        } finally {
            if (context != null) {
                try {
                    context.close();
                } catch (NamingException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}