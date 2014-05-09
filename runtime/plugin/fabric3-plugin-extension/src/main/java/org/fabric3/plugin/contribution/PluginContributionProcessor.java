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
package org.fabric3.plugin.contribution;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fabric3.api.host.contribution.InstallException;
import org.fabric3.api.host.stream.Source;
import org.fabric3.api.host.stream.UrlSource;
import org.fabric3.api.host.util.FileHelper;
import org.fabric3.plugin.api.runtime.PluginHostInfo;
import org.fabric3.spi.contribution.ContentTypeResolutionException;
import org.fabric3.spi.contribution.ContentTypeResolver;
import org.fabric3.spi.contribution.Contribution;
import org.fabric3.spi.contribution.ContributionManifest;
import org.fabric3.spi.contribution.ContributionProcessor;
import org.fabric3.spi.contribution.JavaArtifactIntrospector;
import org.fabric3.spi.contribution.ProcessorRegistry;
import org.fabric3.spi.contribution.Resource;
import org.fabric3.spi.contribution.ResourceState;
import org.fabric3.spi.contribution.archive.ArtifactResourceCallback;
import org.fabric3.spi.introspection.DefaultIntrospectionContext;
import org.fabric3.spi.introspection.IntrospectionContext;
import org.fabric3.spi.introspection.xml.Loader;
import org.fabric3.spi.introspection.xml.LoaderException;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;

/**
 * Processes a build module or project directory as a contribution.
 */
@EagerInit
public class PluginContributionProcessor implements ContributionProcessor {
    private static final String CONTENT_TYPE = "application/vnd.fabric3.plugin-project";
    private ProcessorRegistry registry;
    private ContentTypeResolver contentTypeResolver;
    private List<JavaArtifactIntrospector> artifactIntrospectors = Collections.emptyList();
    private Loader loader;
    private PluginHostInfo info;

    public PluginContributionProcessor(@Reference ProcessorRegistry registry,
                                       @Reference ContentTypeResolver contentTypeResolver,
                                       @Reference Loader loader,
                                       @Reference PluginHostInfo info) {
        this.registry = registry;
        this.contentTypeResolver = contentTypeResolver;
        this.loader = loader;
        this.info = info;
    }

    @Reference
    public void setArtifactIntrospectors(List<JavaArtifactIntrospector> introspectors) {
        this.artifactIntrospectors = introspectors;
    }

    public boolean canProcess(Contribution contribution) {
        return CONTENT_TYPE.equals(contribution.getContentType());
    }

    @Init
    public void init() {
        registry.register(this);
    }

    public void process(Contribution contribution, IntrospectionContext context) throws InstallException {
        ClassLoader oldClassloader = Thread.currentThread().getContextClassLoader();
        ClassLoader loader = context.getClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(loader);
            List<Resource> copy = new ArrayList<>(contribution.getResources());   // copy the list since processors may add resources
            for (Resource resource : copy) {
                if (ResourceState.UNPROCESSED == resource.getState()) {
                    registry.processResource(resource, context);
                }
            }
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassloader);
        }
    }

    public void processManifest(Contribution contribution, final IntrospectionContext context) throws InstallException {
        ClassLoader cl = getClass().getClassLoader();
        URI uri = contribution.getUri();
        IntrospectionContext childContext = new DefaultIntrospectionContext(uri, cl);

        ContributionManifest manifest = null;
        try {
            URL manifestUrl = new File(info.getResourcesDir(), "META-INF" + File.separator + "sca-contribution.xml").toURI().toURL();
            manifest = loadManifest(manifestUrl, childContext);
        } catch (MalformedURLException e) {
            // ignore no manifest found
        }
        if (manifest == null) {
            // try test classes
            try {
                URL manifestUrl = new File(info.getTestResourcesDir(), "META-INF" + File.separator + "sca-contribution.xml").toURI().toURL();
                manifest = loadManifest(manifestUrl, childContext);
            } catch (MalformedURLException e) {
                // ignore no manifest found
            }
        }
        if (manifest != null) {
            contribution.setManifest(manifest);
        }
        if (childContext.hasErrors()) {
            context.addErrors(childContext.getErrors());
        }
        if (childContext.hasWarnings()) {
            context.addWarnings(childContext.getWarnings());
        }

    }

    public void index(Contribution contribution, final IntrospectionContext context) throws InstallException {
        ClassLoader oldClassloader = Thread.currentThread().getContextClassLoader();
        ClassLoader loader = context.getClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(loader);

            iterateArtifacts(contribution, context, new ArtifactResourceCallback() {
                public void onResource(Resource resource) throws InstallException {
                    registry.indexResource(resource, context);
                }
            });
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassloader);
        }
    }

    /**
     * Attempts to load a manifest, returning null if one is not found at the given location.
     *
     * @param manifestUrl  the manifest location
     * @param childContext the current context
     * @return the manifest or null if not found
     * @throws InstallException if there is an error loading the manifest
     */
    private ContributionManifest loadManifest(URL manifestUrl, IntrospectionContext childContext) throws InstallException {
        try {
            Source source = new UrlSource(manifestUrl);
            return loader.load(source, ContributionManifest.class, childContext);
        } catch (LoaderException e) {
            if (e.getCause() instanceof FileNotFoundException) {
                // ignore no manifest found
            } else {
                throw new InstallException(e);
            }
        }
        return null;
    }

    private void iterateArtifacts(Contribution contribution, final IntrospectionContext context, ArtifactResourceCallback callback) throws InstallException {
        File root = FileHelper.toFile(contribution.getLocation());
        assert root.isDirectory();
        iterateArtifactsRecursive(contribution, context, callback, root);
    }

    private void iterateArtifactsRecursive(Contribution contribution, final IntrospectionContext context, ArtifactResourceCallback callback, File dir)
            throws InstallException {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                iterateArtifactsRecursive(contribution, context, callback, file);
            } else {
                try {
                    if (file.getName().endsWith(".class")) {
                        try {
                            String name = calculateClassName(file);
                            if (name == null) {
                                continue; // ignore: not a contribution class or test class
                            }
                            URL entryUrl = file.toURI().toURL();

                            name = name.replace(File.separator, ".").substring(0, name.length() - 6);
                            Class<?> clazz = context.getClassLoader().loadClass(name);

                            Resource resource = null;
                            for (JavaArtifactIntrospector introspector : artifactIntrospectors) {
                                resource = introspector.inspect(clazz, entryUrl, contribution, context);
                                if (resource != null) {
                                    break;
                                }
                            }

                            if (resource == null) {
                                continue;
                            }
                            contribution.addResource(resource);
                            callback.onResource(resource);
                        } catch (ClassNotFoundException | NoClassDefFoundError e) {
                            // ignore since the class may reference another class not present in the contribution
                        }
                    } else {
                        String contentType = contentTypeResolver.getContentType(file.getName());
                        // skip entry if we don't recognize the content type
                        if (contentType == null) {
                            continue;
                        }
                        URL entryUrl = file.toURI().toURL();
                        UrlSource source = new UrlSource(entryUrl);
                        Resource resource = new Resource(contribution, source, contentType);
                        contribution.addResource(resource);
                        callback.onResource(resource);
                    }
                } catch (MalformedURLException | ContentTypeResolutionException e) {
                    context.addWarning(new ContributionIndexingFailure(file, e));
                }
            }
        }

    }

    private String calculateClassName(File file) {
        String name = null;
        File classesDir = info.getClassesDir();
        File testDir = info.getTestClassesDir();
        if (file.getPath().startsWith(classesDir.getPath())) {
            name = file.getPath().substring(classesDir.getPath().length() + 1);
        } else if (file.getPath().startsWith(testDir.getPath())) {
            name = file.getPath().substring(testDir.getPath().length() + 1);
        }
        return name;
    }
}
