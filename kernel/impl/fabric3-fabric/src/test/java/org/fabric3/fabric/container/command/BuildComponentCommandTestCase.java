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
 * Portions originally based on Apache Tuscany 2007
 * licensed under the Apache 2.0 license.
 */
package org.fabric3.fabric.container.command;

import javax.xml.namespace.QName;
import java.net.URI;

import junit.framework.TestCase;
import org.fabric3.spi.model.physical.PhysicalComponentDefinition;

public class BuildComponentCommandTestCase extends TestCase {
    private PhysicalComponentDefinition definition;
    private BuildComponentCommand command;

    public void testEquals() throws Exception {
        BuildComponentCommand command2 = new BuildComponentCommand(definition);
        assertEquals(command2, command);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        definition = new MockDefinition();
        definition.setDeployable(new QName("test", "composite"));
        definition.setContributionUri(URI.create("classloader"));
        definition.setComponentUri(URI.create("component"));
        command = new BuildComponentCommand(definition);
    }

    private class MockDefinition extends PhysicalComponentDefinition {
        private static final long serialVersionUID = 6358317322714807832L;
    }
}
