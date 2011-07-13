/*
 * Fabric3 Copyright (c) 2009-2011 Metaform Systems
 * 
 * Fabric3 is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version, with the following exception:
 * 
 * Linking this software statically or dynamically with other modules is making
 * a combined work based on this software. Thus, the terms and conditions of the
 * GNU General Public License cover the whole combination.
 * 
 * As a special exception, the copyright holders of this software give you
 * permission to link this software with independent modules to produce an
 * executable, regardless of the license terms of these independent modules, and
 * to copy and distribute the resulting executable under terms of your choice,
 * provided that you also meet, for each linked independent module, the terms
 * and conditions of the license of that module. An independent module is a
 * module which is not derived from or based on this software. If you modify
 * this software, you may extend this exception to your version of the software,
 * but you are not obligated to do so. If you do not wish to do so, delete this
 * exception statement from your version.
 * 
 * Fabric3 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Fabric3. If not, see <http://www.gnu.org/licenses/>.
 */
package org.fabric3.binding.zeromq.runtime.management;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

import org.fabric3.api.annotation.management.Management;
import org.fabric3.api.annotation.management.ManagementOperation;
import org.fabric3.binding.zeromq.runtime.message.Subscriber;
import org.fabric3.spi.management.ManagementException;
import org.fabric3.spi.management.ManagementService;

/**
 * @version $Revision: 10212 $ $Date: 2011-03-15 18:20:58 +0100 (Tue, 15 Mar 2011) $
 */
@EagerInit
@Management(path = "/runtime/transports/zeromq", description = "Manages ZeroMQ infrastructure")
public class ZeroMQManagementServiceImpl implements ZeroMQManagementService {
    private static final String CHANNELS_PATH = "transports/zeromq/channels/";

    private List<String> channels = new ArrayList<String>();
    private ManagementService managementService;

    public ZeroMQManagementServiceImpl(@Reference ManagementService managementService) {
        this.managementService = managementService;
    }

    public void register(String channelName, URI subscriberId, Subscriber subscriber) {
        try {
            channels.add(channelName);
            managementService.export(CHANNELS_PATH + channelName, "", "", subscriber);
        } catch (ManagementException e) {
            e.printStackTrace();
        }
    }

    public void unregister(String channelName, URI subscriberId) {
        try {
            channels.remove(channelName);
            managementService.remove(CHANNELS_PATH + channelName, "");
        } catch (ManagementException e) {
            e.printStackTrace();
        }
    }

    @ManagementOperation(path = "channels")
    public Collection<String> getChannels() {
        return channels;
    }

}