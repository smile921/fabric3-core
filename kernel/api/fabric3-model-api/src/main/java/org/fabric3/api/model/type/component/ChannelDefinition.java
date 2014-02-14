/*
 * Fabric3
 * Copyright (c) 2009-2013 Metaform Systems
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
package org.fabric3.api.model.type.component;

import java.net.URI;
import java.util.ArrayList;

/**
 * A channel configuration in a composite.
 */
public class ChannelDefinition extends BindableDefinition<Composite> {
    public static final String DEFAULT_TYPE = "default";

    private static final long serialVersionUID = 8735705202863105855L;

    private String name;
    private URI contributionUri;
    private String type = DEFAULT_TYPE;
    private boolean local;

    public ChannelDefinition(String name) {
        this.name = name;
    }

    public ChannelDefinition(String name, URI contributionUri) {
        this.name = name;
        this.contributionUri = contributionUri;
        bindings = new ArrayList<>();
    }

    public ChannelDefinition(String name, URI contributionUri, String type, boolean local) {
        this.name = name;
        this.contributionUri = contributionUri;
        this.type = type;
        this.local = local;
        bindings = new ArrayList<>();
    }

    /**
     * Returns the channel name.
     *
     * @return the channel name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the URI of the contribution this channel is defined in.
     *
     * @return the URI of the contribution this channel is defined in
     */
    public URI getContributionUri() {
        return contributionUri;
    }

    /**
     * Sets the contribution URI.
     *
     * @param uri the contribution URI
     */
    public void setContributionUri(URI uri) {
        this.contributionUri = uri;
    }

    /**
     * Returns the channel type.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the channel type.
     *
     * @param type the channel type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * True if the channel is local.
     *
     * @return true if the channel is local
     */
    public boolean isLocal() {
        return local;
    }

    /**
     * Sets if the channel is local.
     *
     * @param local true if the channel is local
     */
    public void setLocal(boolean local) {
        this.local = local;
    }
}