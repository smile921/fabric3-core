/*
 * Fabric3
 * Copyright (c) 2009-2011 Metaform Systems
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
package org.fabric3.binding.file.model;

import javax.xml.namespace.QName;

import org.fabric3.binding.file.common.Strategy;
import org.fabric3.host.Namespaces;
import org.fabric3.model.type.component.BindingDefinition;

/**
 * A file binding configuration set on a reference.
 *
 * @version $Rev$ $Date$
 */
public class FileBindingDefinition extends BindingDefinition {
    private static final long serialVersionUID = -8904535030035183877L;
    public static final QName BINDING_FILE = new QName(Namespaces.F3, "binding.file");

    private String pattern;
    private String location;
    private Strategy strategy = Strategy.DELETE;
    private String archiveLocation;
    private String errorLocation;
    private String adapterClass;
    private long delay;

    public FileBindingDefinition(String name, String location, String errorLocation) {
        super(name, null, BINDING_FILE);
        this.location = location;
        this.errorLocation = errorLocation;
    }

    public FileBindingDefinition(String name,
                                 String pattern,
                                 String location,
                                 Strategy strategy,
                                 String archiveLocation,
                                 String errorLocation,
                                 String adapterClass,
                                 long delay) {
        super(name, null, BINDING_FILE);
        this.pattern = pattern;
        this.location = location;
        this.strategy = strategy;
        this.archiveLocation = archiveLocation;
        this.errorLocation = errorLocation;
        this.adapterClass = adapterClass;
        this.delay = delay;
    }

    public String getPattern() {
        return pattern;
    }

    public String getLocation() {
        return location;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public String getArchiveLocation() {
        return archiveLocation;
    }

    public String getErrorLocation() {
        return errorLocation;
    }

    public String getAdapterClass() {
        return adapterClass;
    }

    public long getDelay() {
        return delay;
    }
}
