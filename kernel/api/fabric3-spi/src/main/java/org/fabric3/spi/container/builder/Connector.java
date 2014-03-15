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
 *
 * ----------------------------------------------------
 *
 * Portions originally based on Apache Tuscany 2007
 * licensed under the Apache 2.0 license.
 *
 */
package org.fabric3.spi.container.builder;

import org.fabric3.spi.model.physical.PhysicalWireDefinition;

/**
 * Creates wires between two components and between a component and a binding. Also handles disconnecting wires.
 */
public interface Connector {

    /**
     * Creates a wire for a components and connects it to another component or binding. In the case of bindings, the wire may be connected to a
     * binding source (for bound services) or to a binding target (for bound references).
     *
     * @param definition metadata describing the wire to create
     * @throws BuildException if an error creating the wire occurs
     */
    void connect(PhysicalWireDefinition definition) throws BuildException;


    /**
     * Disconnects a wire between two components or a component and a binding.
     *
     * @param definition the metadata describing the wire to disconnect
     * @throws BuildException if an error disconnecting the wire occurs
     */
    void disconnect(PhysicalWireDefinition definition) throws BuildException;
}
