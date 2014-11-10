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
 *
 * Portions originally based on Apache Tuscany 2007
 * licensed under the Apache 2.0 license.
 */
package org.fabric3.contribution;

import javax.xml.namespace.QName;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.fabric3.api.host.contribution.ContributionLockedException;
import org.fabric3.api.host.contribution.ContributionOrder;
import org.fabric3.api.host.contribution.ContributionSource;
import org.fabric3.api.host.contribution.Deployable;
import org.fabric3.api.host.contribution.FileContributionSource;
import org.fabric3.api.host.contribution.RemoveException;
import org.fabric3.api.host.contribution.UninstallException;
import org.fabric3.api.host.repository.Repository;
import org.fabric3.api.model.type.component.Composite;
import org.fabric3.spi.contribution.Capability;
import org.fabric3.spi.contribution.ContentTypeResolver;
import org.fabric3.spi.contribution.Contribution;
import org.fabric3.spi.contribution.ContributionState;
import org.fabric3.spi.contribution.DependencyResolver;
import org.fabric3.spi.contribution.MetaDataStore;
import org.fabric3.spi.contribution.ProcessorRegistry;
import org.fabric3.spi.contribution.Resource;
import org.fabric3.spi.contribution.ResourceElement;
import org.fabric3.spi.contribution.manifest.QNameSymbol;
import org.fabric3.spi.introspection.IntrospectionContext;

/**
 *
 */
public class ContributionServiceImplTestCase extends TestCase {
    private ProcessorRegistry processorRegistry;
    private MetaDataStore store;
    private ContributionLoader loader;
    private ContentTypeResolver resolver;
    private DependencyResolver dependencyResolver;

    private URI contributionUri;
    private QName deployableName;
    private Deployable deployable;
    private Contribution contribution;

    private ContributionServiceImpl service;
    private URI profileUri;

    public void testExists() throws Exception {
        EasyMock.expect(store.find(contributionUri)).andReturn(contribution);
        EasyMock.replay(processorRegistry, store, loader, resolver, dependencyResolver);

        assertTrue(service.exists(contributionUri));

        EasyMock.verify(processorRegistry, store, loader, resolver, dependencyResolver);
    }

    public void testGetContributions() throws Exception {
        EasyMock.expect(store.getContributions()).andReturn(Collections.singleton(contribution));
        EasyMock.replay(processorRegistry, store, loader, resolver, dependencyResolver);

        assertTrue(service.getContributions().contains(contributionUri));

        EasyMock.verify(processorRegistry, store, loader, resolver, dependencyResolver);
    }

    public void testGetTimeStamp() throws Exception {
        EasyMock.expect(store.find(contributionUri)).andReturn(contribution);
        EasyMock.replay(processorRegistry, store, loader, resolver, dependencyResolver);

        assertEquals(1, service.getContributionTimestamp(contributionUri));

        EasyMock.verify(processorRegistry, store, loader, resolver, dependencyResolver);
    }

    public void testGetDeployables() throws Exception {
        EasyMock.expect(store.find(contributionUri)).andReturn(contribution);
        EasyMock.replay(processorRegistry, store, loader, resolver, dependencyResolver);

        assertTrue(service.getDeployables(contributionUri).contains(deployable));

        EasyMock.verify(processorRegistry, store, loader, resolver, dependencyResolver);
    }

    public void testGetDeployedComposites() throws Exception {
        contribution.acquireLock(deployable.getName());
        EasyMock.expect(store.find(contributionUri)).andReturn(contribution);
        EasyMock.replay(processorRegistry, store, loader, resolver, dependencyResolver);

        assertTrue(service.getDeployedComposites(contributionUri).contains(deployable.getName()));

        EasyMock.verify(processorRegistry, store, loader, resolver, dependencyResolver);
    }

    public void testStore() throws Exception {
        EasyMock.expect(store.find(contributionUri)).andReturn(null);
        store.store(contribution);
        EasyMock.replay(processorRegistry, store, loader, resolver, dependencyResolver);

        URL location = new URL("file://location");
        ContributionSource source = new FileContributionSource(contributionUri, location, -1, "application/xml", false);
        assertEquals(contributionUri, service.store(source));

        EasyMock.verify(processorRegistry, store, loader, resolver, dependencyResolver);
    }

    public void testStoreMultiple() throws Exception {
        EasyMock.expect(store.find(contributionUri)).andReturn(null);
        store.store(contribution);
        EasyMock.replay(processorRegistry, store, loader, resolver, dependencyResolver);

        URL location = new URL("file://location");
        ContributionSource source = new FileContributionSource(contributionUri, location, -1, "application/xml", false);
        assertTrue(service.store(Collections.singletonList(source)).contains(contributionUri));

        EasyMock.verify(processorRegistry, store, loader, resolver, dependencyResolver);
    }

    @SuppressWarnings({"unchecked"})
    public void testInstall() throws Exception {
        createResourceWithComposite();

        EasyMock.expect(store.find(contributionUri)).andReturn(contribution);
        processorRegistry.processManifest(EasyMock.eq(contribution), EasyMock.isA(IntrospectionContext.class));
        EasyMock.expect(dependencyResolver.resolve(EasyMock.isA(List.class))).andReturn(Collections.singletonList(contribution));
        EasyMock.expect(loader.load(contribution)).andReturn(getClass().getClassLoader());
        processorRegistry.indexContribution(EasyMock.eq(contribution), EasyMock.isA(IntrospectionContext.class));
        store.store(contribution);
        processorRegistry.processContribution(EasyMock.eq(contribution), EasyMock.isA(IntrospectionContext.class));

        EasyMock.replay(processorRegistry, store, loader, resolver, dependencyResolver);

        service.install(contributionUri);

        EasyMock.verify(processorRegistry, store, loader, resolver, dependencyResolver);
    }

    public void testUnInstall() throws Exception {
        contribution.setState(ContributionState.INSTALLED);
        EasyMock.expect(store.find(contributionUri)).andReturn(contribution);
        loader.unload(contribution);
        EasyMock.replay(processorRegistry, store, loader, resolver, dependencyResolver);

        service.uninstall(contributionUri);

        EasyMock.verify(processorRegistry, store, loader, resolver, dependencyResolver);
    }

    @SuppressWarnings({"unchecked"})
    public void testUnInstallMultiple() throws Exception {
        contribution.setState(ContributionState.INSTALLED);
        EasyMock.expect(store.find(contributionUri)).andReturn(contribution);
        EasyMock.expect(dependencyResolver.orderForUninstall(EasyMock.isA(List.class))).andReturn(Collections.singletonList(contribution));
        loader.unload(contribution);

        EasyMock.replay(processorRegistry, store, loader, resolver, dependencyResolver);

        service.uninstall(Collections.singletonList(contributionUri));

        EasyMock.verify(processorRegistry, store, loader, resolver, dependencyResolver);
    }

    public void testUnInstallContributionLocked() throws Exception {
        contribution.setState(ContributionState.INSTALLED);
        contribution.acquireLock(deployableName);
        EasyMock.expect(store.find(contributionUri)).andReturn(contribution);
        EasyMock.replay(processorRegistry, store, loader, resolver, dependencyResolver);
        try {
            service.uninstall(contributionUri);
            fail();
        } catch (ContributionLockedException e) {
            // expected
        }
        EasyMock.verify(processorRegistry, store, loader, resolver, dependencyResolver);
    }

    public void testUnInstallContributionNotInstalled() throws Exception {
        contribution.acquireLock(deployableName);
        EasyMock.expect(store.find(contributionUri)).andReturn(contribution);
        EasyMock.replay(processorRegistry, store, loader, resolver, dependencyResolver);
        try {
            service.uninstall(contributionUri);
            fail();
        } catch (UninstallException e) {
            // expected
        }
        EasyMock.verify(processorRegistry, store, loader, resolver, dependencyResolver);
    }

    public void testRemoveContribution() throws Exception {
        EasyMock.expect(store.find(contributionUri)).andReturn(contribution);
        store.remove(contributionUri);
        Repository repository = EasyMock.createMock(Repository.class);
        repository.remove(contributionUri);
        service.setRepository(repository);

        EasyMock.replay(processorRegistry, store, loader, resolver, dependencyResolver, repository);
        service.remove(contributionUri);
        EasyMock.verify(processorRegistry, store, loader, resolver, dependencyResolver, repository);
    }

    public void testRemoveContributionsMultiple() throws Exception {
        EasyMock.expect(store.find(contributionUri)).andReturn(contribution);
        store.remove(contributionUri);
        Repository repository = EasyMock.createMock(Repository.class);
        repository.remove(contributionUri);
        service.setRepository(repository);

        EasyMock.replay(processorRegistry, store, loader, resolver, dependencyResolver, repository);
        service.remove(Collections.singletonList(contributionUri));
        EasyMock.verify(processorRegistry, store, loader, resolver, dependencyResolver, repository);
    }

    public void testProfileExists() throws Exception {
        EasyMock.expect(store.getContributions()).andReturn(Collections.<Contribution>singleton(contribution));

        EasyMock.replay(processorRegistry, store, loader, resolver, dependencyResolver);

        assertTrue(service.profileExists(profileUri));
        EasyMock.verify(processorRegistry, store, loader, resolver, dependencyResolver);
    }

    public void testGetContributionsInProfile() throws Exception {
        EasyMock.expect(store.getContributions()).andReturn(Collections.<Contribution>singleton(contribution));

        EasyMock.replay(processorRegistry, store, loader, resolver, dependencyResolver);

        assertTrue(service.getContributionsInProfile(profileUri).contains(contribution.getUri()));
        EasyMock.verify(processorRegistry, store, loader, resolver, dependencyResolver);
    }

    @SuppressWarnings({"unchecked"})
    public void testGetSortedContributionsInProfile() throws Exception {
        EasyMock.expect(store.getContributions()).andReturn(Collections.<Contribution>singleton(contribution));
        List<Contribution> list = Collections.singletonList(contribution);
        EasyMock.expect(dependencyResolver.orderForUninstall(EasyMock.isA(List.class))).andReturn(list);
        EasyMock.replay(processorRegistry, store, loader, resolver, dependencyResolver);

        assertTrue(service.getSortedContributionsInProfile(profileUri).contains(contribution.getUri()));
        EasyMock.verify(processorRegistry, store, loader, resolver, dependencyResolver);
    }

    @SuppressWarnings({"unchecked"})
    public void testRegisterProfiles() throws Exception {
        EasyMock.expect(store.getContributions()).andReturn(Collections.<Contribution>singleton(contribution));
        EasyMock.expect(store.find(contributionUri)).andReturn(contribution);
        EasyMock.replay(processorRegistry, store, loader, resolver, dependencyResolver);

        URI newProfile = URI.create("newProfile");
        service.registerProfile(newProfile, Collections.<URI>singletonList(contributionUri));
        assertTrue(contribution.getProfiles().contains(newProfile));
        EasyMock.verify(processorRegistry, store, loader, resolver, dependencyResolver);
    }

    @SuppressWarnings({"unchecked"})
    public void testInstallProfile() throws Exception {
        createResourceWithComposite();
        EasyMock.expect(store.getContributions()).andReturn(Collections.<Contribution>singleton(contribution));
        List<Contribution> list = Collections.singletonList(contribution);
        EasyMock.expect(dependencyResolver.resolve(EasyMock.isA(List.class))).andReturn(list);
        processorRegistry.processManifest(EasyMock.eq(contribution), EasyMock.isA(IntrospectionContext.class));
        processorRegistry.indexContribution(EasyMock.eq(contribution), EasyMock.isA(IntrospectionContext.class));
        processorRegistry.processContribution(EasyMock.eq(contribution), EasyMock.isA(IntrospectionContext.class));
        EasyMock.expect(loader.load(contribution)).andReturn(getClass().getClassLoader());
        store.store(contribution);
        EasyMock.replay(processorRegistry, store, loader, resolver, dependencyResolver);

        service.installProfile(profileUri);
        assertTrue(ContributionState.INSTALLED == contribution.getState());
        EasyMock.verify(processorRegistry, store, loader, resolver, dependencyResolver);
    }

    @SuppressWarnings({"unchecked"})
    public void testUnInstallProfile() throws Exception {
        contribution.setState(ContributionState.INSTALLED);
        EasyMock.expect(store.find(contributionUri)).andReturn(contribution);
        EasyMock.expect(store.getContributions()).andReturn(Collections.<Contribution>singleton(contribution));
        List<Contribution> list = Collections.singletonList(contribution);
        EasyMock.expect(dependencyResolver.orderForUninstall(EasyMock.isA(List.class))).andReturn(list);
        loader.unload(contribution);
        EasyMock.replay(processorRegistry, store, loader, resolver, dependencyResolver);

        service.uninstallProfile(profileUri);
        assertTrue(ContributionState.STORED == contribution.getState());
        EasyMock.verify(processorRegistry, store, loader, resolver, dependencyResolver);
    }

    public void testRemoveProfile() throws Exception {
        contribution.setState(ContributionState.STORED);
        EasyMock.expect(store.find(contributionUri)).andReturn(contribution);
        EasyMock.expect(store.getContributions()).andReturn(Collections.<Contribution>singleton(contribution));
        store.remove(contributionUri);
        Repository repository = EasyMock.createMock(Repository.class);
        repository.remove(contributionUri);
        service.setRepository(repository);

        EasyMock.replay(processorRegistry, store, loader, resolver, dependencyResolver, repository);

        service.removeProfile(profileUri);
        assertTrue(ContributionState.STORED == contribution.getState());
        EasyMock.verify(processorRegistry, store, loader, resolver, dependencyResolver, repository);
    }

    public void testErrorRemoveProfileInInstalledState() throws Exception {
        contribution.setState(ContributionState.INSTALLED);
        EasyMock.expect(store.getContributions()).andReturn(Collections.<Contribution>singleton(contribution));

        EasyMock.replay(processorRegistry, store, loader, resolver, dependencyResolver);

        try {
            service.removeProfile(profileUri);
            fail();
        } catch (RemoveException e) {
            // expected
        }
        EasyMock.verify(processorRegistry, store, loader, resolver, dependencyResolver);
    }

    @SuppressWarnings({"unchecked"})
    public void testProcessManifest() throws Exception {
        createResourceWithComposite();
        URI otherContributionUri = URI.create("otherContribution");
        Contribution otherContribution = new Contribution(otherContributionUri);
        otherContribution.getManifest().addRequiredCapability(new Capability("capability", true));
        List<ContributionSource> sources = new ArrayList<>();
        URL location = new URL("file://location");
        URL otherLocation = new URL("file://otherLocation");

        sources.add(new FileContributionSource(contributionUri, location, -1, "application/xml", false));
        sources.add(new FileContributionSource(otherContributionUri, otherLocation, -1, "application/xml", false));
        List<Contribution> contributions = new ArrayList<>();
        contributions.add(contribution);
        contributions.add(otherContribution);

        EasyMock.expect(store.find(contributionUri)).andReturn(null);
        EasyMock.expect(store.find(otherContributionUri)).andReturn(null);
        store.store(contribution);
        store.store(otherContribution);
        processorRegistry.processManifest(EasyMock.eq(contribution), EasyMock.isA(IntrospectionContext.class));
        processorRegistry.processManifest(EasyMock.eq(otherContribution), EasyMock.isA(IntrospectionContext.class));
        EasyMock.expect(dependencyResolver.resolve(EasyMock.isA(List.class))).andReturn(contributions);
        
        EasyMock.replay(processorRegistry, store, loader, resolver, dependencyResolver);


        ContributionOrder order = service.processManifests(sources);
        assertTrue(order.getBaseContributions().contains(contributionUri));
        assertTrue(order.getIsolatedContributions().contains(otherContributionUri));
        EasyMock.verify(processorRegistry, store, loader, resolver, dependencyResolver);
    }

    @SuppressWarnings({"unchecked"})
    public void testProcessContents() throws Exception {
        createResourceWithComposite();

        EasyMock.expect(store.find(contributionUri)).andReturn(contribution);
        store.store(contribution);
        EasyMock.expect(loader.load(contribution)).andReturn(getClass().getClassLoader());
        processorRegistry.indexContribution(EasyMock.eq(contribution), EasyMock.isA(IntrospectionContext.class));
        processorRegistry.processContribution(EasyMock.eq(contribution), EasyMock.isA(IntrospectionContext.class));

        EasyMock.replay(processorRegistry, store, loader, resolver, dependencyResolver);

        service.processContents(contributionUri);

        EasyMock.verify(processorRegistry, store, loader, resolver, dependencyResolver);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        processorRegistry = EasyMock.createMock(ProcessorRegistry.class);
        store = EasyMock.createMock(MetaDataStore.class);
        loader = EasyMock.createMock(ContributionLoader.class);
        resolver = EasyMock.createMock(ContentTypeResolver.class);
        dependencyResolver = EasyMock.createMock(DependencyResolver.class);

        ContributionServiceMonitor monitor = EasyMock.createNiceMock(ContributionServiceMonitor.class);
        EasyMock.replay(monitor);

        contributionUri = URI.create("contribution");
        URL locationUrl = new URL("file://test");
        contribution = new Contribution(contributionUri, null, locationUrl, 1, "application/xml", true);
        deployableName = new QName("test", "composite");
        deployable = new Deployable(deployableName);
        contribution.getManifest().addDeployable(deployable);
        profileUri = URI.create("profile");
        contribution.addProfile(profileUri);
        service = new ContributionServiceImpl(processorRegistry, store, loader, resolver, dependencyResolver, monitor);
    }

    private void createResourceWithComposite() {
        Resource resource = new Resource(contribution, null, "application/xml");
        QNameSymbol symbol = new QNameSymbol(deployableName);
        Composite composite = new Composite(deployableName);
        ResourceElement<QNameSymbol, Composite> element = new ResourceElement<>(symbol, composite);
        resource.addResourceElement(element);
        contribution.addResource(resource);
    }

}
