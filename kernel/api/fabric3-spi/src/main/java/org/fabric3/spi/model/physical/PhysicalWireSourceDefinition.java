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
package org.fabric3.spi.model.physical;

import org.fabric3.api.model.type.contract.DataType;

/**
 * Represents the source side of a wire.
 */
public abstract class PhysicalWireSourceDefinition extends PhysicalAttachPointDefinition {
    private static final long serialVersionUID = 2560576437284123839L;

    private boolean optimizable;
    private String key;
    private int order = Integer.MIN_VALUE;

    public PhysicalWireSourceDefinition() {
    }

    public PhysicalWireSourceDefinition(DataType... types) {
        super(types);
    }

    /**
     * Returns whether the source side of the wire is optimizable.
     *
     * @return true if the source side of the wire is optimizable
     */
    public boolean isOptimizable() {
        return optimizable;
    }

    /**
     * Sets whether the source side of the wire is optimizable.
     *
     * @param optimizable whether the source side of the wire is optimizable
     */
    public void setOptimizable(boolean optimizable) {
        this.optimizable = optimizable;
    }

    /**
     * Returns the key to be used when this wire is part of a Map-style reference.
     *
     * @return the key to be used when this wire is part of a Map-style reference
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key to be used when this wire is part of a Map-style reference.
     *
     * @param key the key to be used when this wire is part of a Map-style reference
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Returns the order for collection- and array-based wires or {@link Integer#MIN_VALUE} if not specified.
     *
     * @return the order for collection- and array-based wires or {@link Integer#MIN_VALUE} if not specified
     */
    public int getOrder() {
        return order;
    }

    /**
     * Returns true if the wire is ordered.
     *
     * @return true if the wire is ordered
     */
    public boolean isOrdered() {
        return order != Integer.MIN_VALUE;
    }

    /**
     * Sets the order for collection- and array-based wires.
     *
     * @param order the order
     */
    public void setOrder(int order) {
        this.order = order;
    }
}