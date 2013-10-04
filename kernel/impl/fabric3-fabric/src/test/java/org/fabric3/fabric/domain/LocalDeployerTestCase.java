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
package org.fabric3.fabric.domain;

import junit.framework.TestCase;
import org.easymock.EasyMock;

import org.fabric3.host.domain.DeploymentException;
import org.fabric3.model.type.component.Scope;
import org.fabric3.spi.command.CompensatableCommand;
import org.fabric3.spi.container.component.ScopeContainer;
import org.fabric3.spi.container.component.ScopeRegistry;
import org.fabric3.spi.domain.DeployerMonitor;
import org.fabric3.spi.domain.DeploymentPackage;
import org.fabric3.spi.command.CommandExecutorRegistry;
import org.fabric3.spi.command.ExecutionException;
import org.fabric3.spi.deployment.generator.Deployment;

import static org.fabric3.host.Names.LOCAL_ZONE;

/**
 *
 */
public class LocalDeployerTestCase extends TestCase {

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    public void testRollback() throws Exception {
        ScopeContainer scopeContainer = EasyMock.createMock(ScopeContainer.class);
        scopeContainer.reinject();

        ScopeRegistry scopeRegistry = EasyMock.createMock(ScopeRegistry.class);
        EasyMock.expect(scopeRegistry.getScopeContainer(Scope.COMPOSITE)).andReturn(scopeContainer);

        DeployerMonitor monitor = EasyMock.createMock(DeployerMonitor.class);
        monitor.rollback("local");

        CompensatableCommand rollbackCommand = EasyMock.createMock(CompensatableCommand.class);
        CompensatableCommand command1 = EasyMock.createMock(CompensatableCommand.class);
        EasyMock.expect(command1.getCompensatingCommand()).andReturn(rollbackCommand);

        CompensatableCommand command2 = EasyMock.createMock(CompensatableCommand.class);


        CommandExecutorRegistry executorRegistry = EasyMock.createMock(CommandExecutorRegistry.class);
        executorRegistry.execute(command1);
        executorRegistry.execute(command2);
        EasyMock.expectLastCall().andThrow(new ExecutionException("test error"));
        executorRegistry.execute(rollbackCommand);

        EasyMock.replay(executorRegistry, scopeRegistry, scopeContainer, monitor, command1, command2, rollbackCommand);

        LocalDeployer deployer = new LocalDeployer(executorRegistry, scopeRegistry, monitor);

        Deployment currentDeployment = new Deployment("current");
        currentDeployment.addCommand(LOCAL_ZONE, command1);
        currentDeployment.addCommand(LOCAL_ZONE, command2);

        Deployment fullDeployment = new Deployment("full");
        DeploymentPackage deploymentPackage = new DeploymentPackage(currentDeployment, fullDeployment);

        try {
            deployer.deploy(deploymentPackage);
            fail();
        } catch (DeploymentException e) {
            // expected 
            assertTrue(e.getCause() instanceof ExecutionException);
        }

        EasyMock.verify(executorRegistry, scopeContainer, scopeRegistry, monitor, command1, command2, rollbackCommand);
    }
}
