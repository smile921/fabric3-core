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
package org.fabric3.binding.file.runtime;

import java.io.File;
import java.net.URI;

import org.fabric3.binding.file.provision.FileBindingWireSourceDefinition;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Reference;

import org.fabric3.api.annotation.monitor.Monitor;
import org.fabric3.api.binding.file.ServiceAdapter;
import org.fabric3.api.binding.file.annotation.Strategy;
import org.fabric3.binding.file.runtime.receiver.PassThroughInterceptor;
import org.fabric3.binding.file.runtime.receiver.ReceiverConfiguration;
import org.fabric3.binding.file.runtime.receiver.ReceiverManager;
import org.fabric3.binding.file.runtime.receiver.ReceiverMonitor;
import org.fabric3.api.host.runtime.HostInfo;
import org.fabric3.spi.container.builder.BuilderException;
import org.fabric3.spi.container.builder.component.SourceWireAttacher;
import org.fabric3.spi.classloader.ClassLoaderRegistry;
import org.fabric3.spi.container.component.ComponentManager;
import org.fabric3.spi.container.component.AtomicComponent;
import org.fabric3.spi.container.component.Component;
import org.fabric3.spi.model.physical.PhysicalWireTargetDefinition;
import org.fabric3.spi.container.objectfactory.ObjectFactory;
import org.fabric3.spi.container.wire.Interceptor;
import org.fabric3.spi.container.wire.InvocationChain;
import org.fabric3.spi.container.wire.Wire;

/**
 *
 */
@EagerInit
public class FileSourceWireAttacher implements SourceWireAttacher<FileBindingWireSourceDefinition> {
    private static final ServiceAdapter ADAPTER = new DefaultServiceAdapter();
    private static final ServiceAdapter JAF_ADAPTER = new DataHandlerServiceAdapter();

    private ReceiverManager receiverManager;
    private ClassLoaderRegistry registry;
    private ComponentManager manager;
    private ReceiverMonitor monitor;
    private File baseDir;

    public FileSourceWireAttacher(@Reference ReceiverManager receiverManager,
                                  @Reference ClassLoaderRegistry registry,
                                  @Reference ComponentManager manager,
                                  @Reference HostInfo hostInfo,
                                  @Monitor ReceiverMonitor monitor) {
        this.receiverManager = receiverManager;
        this.registry = registry;
        this.manager = manager;
        this.monitor = monitor;
        this.baseDir = new File(hostInfo.getDataDir(), "inbox");
    }

    public void attach(FileBindingWireSourceDefinition source, PhysicalWireTargetDefinition target, Wire wire) throws BuilderException {
        String id = source.getUri().toString();

        File location = getLocation(source);
        File errorLocation = getErrorLocation(source);
        File archiveLocation = getArchiveLocation(source);

        String pattern = source.getPattern();
        Strategy strategy = source.getStrategy();

        Interceptor interceptor = new PassThroughInterceptor();
        for (InvocationChain chain : wire.getInvocationChains()) {
            chain.addInterceptor(interceptor);
        }

        ServiceAdapter adapter = getAdaptor(source);

        long delay = source.getDelay();

        ReceiverConfiguration configuration = new ReceiverConfiguration(id,
                                                                        location,
                                                                        pattern,
                                                                        strategy,
                                                                        errorLocation,
                                                                        archiveLocation,
                                                                        interceptor,
                                                                        adapter,
                                                                        delay,
                                                                        monitor);
        receiverManager.create(configuration);
    }

    public void detach(FileBindingWireSourceDefinition source, PhysicalWireTargetDefinition target) throws BuilderException {
        String id = source.getUri().toString();
        receiverManager.remove(id);
    }

    public void attachObjectFactory(FileBindingWireSourceDefinition source, ObjectFactory<?> objectFactory, PhysicalWireTargetDefinition target) {
        throw new UnsupportedOperationException();

    }

    public void detachObjectFactory(FileBindingWireSourceDefinition source, PhysicalWireTargetDefinition target) {
        throw new UnsupportedOperationException();
    }

    private File getLocation(FileBindingWireSourceDefinition source) {
        String location = source.getLocation();
        return resolve(location);
    }

    private File getArchiveLocation(FileBindingWireSourceDefinition source) {
        File archiveLocation = null;
        String archiveLocationStr = source.getArchiveLocation();
        if (archiveLocationStr != null) {
            archiveLocation = resolve(archiveLocationStr);
        }
        return archiveLocation;
    }

    private File getErrorLocation(FileBindingWireSourceDefinition source) {
        File errorLocation = null;
        String errorLocationStr = source.getErrorLocation();
        if (errorLocationStr != null) {
            errorLocation = resolve(errorLocationStr);
        }
        return errorLocation;
    }

    /**
     * Resolve the location as an absolute address or relative to the runtime data/inbox directory.
     *
     * @param location the location
     * @return the resolved location
     */
    private File resolve(String location) {
        File file = new File(location);
        if (file.isAbsolute()) {
            return file;
        }
        return new File(baseDir, location);
    }

    /**
     * Instantiates an adaptor class or returns a component instance.
     *
     * @param source the definition
     * @return the adaptor
     * @throws BuilderException if there is an error instantiating the class or returning a component instance.
     */
    private ServiceAdapter getAdaptor(FileBindingWireSourceDefinition source) throws BuilderException {
        String adapterClass = source.getAdapterClass();
        if (adapterClass == null) {
            URI adapterUri = source.getAdapterUri();
            if (adapterUri == null) {
                return source.isDataHandler() ? JAF_ADAPTER : ADAPTER;
            }
            Component component = manager.getComponent(adapterUri);
            if (component == null) {
                throw new BuilderException("Binding adaptor component not found: " + adapterUri);
            }
            if (!(component instanceof AtomicComponent)) {
                throw new BuilderException("Adaptor component must implement " + AtomicComponent.class.getName() + ": " + adapterUri);
            }
            return new ServiceAdaptorWrapper((AtomicComponent) component);
        }
        URI uri = source.getClassLoaderId();
        ClassLoader loader = registry.getClassLoader(uri);
        if (loader == null) {
            // this should not happen
            throw new BuilderException("ClassLoader not found: " + uri);
        }
        try {
            return (ServiceAdapter) loader.loadClass(adapterClass).newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new BuilderException(e);
        }
    }

}
