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
package org.fabric3.introspection.xml.composite;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.Set;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.fabric3.introspection.xml.DefaultLoaderHelper;
import org.fabric3.introspection.xml.LoaderRegistryImpl;
import org.fabric3.introspection.xml.MockXMLFactory;
import org.fabric3.introspection.xml.common.ComponentReferenceLoader;
import org.fabric3.api.model.type.component.ComponentReference;
import org.fabric3.spi.introspection.DefaultIntrospectionContext;
import org.fabric3.spi.introspection.IntrospectionContext;
import org.fabric3.spi.introspection.xml.LoaderHelper;
import org.fabric3.spi.introspection.xml.LoaderRegistry;
import org.fabric3.spi.xml.XMLFactory;

/**
 *
 */
public class ComponentReferenceLoaderTestCase extends TestCase {
    private static final String XML =
            "<reference xmlns='http://docs.oasis-open.org/ns/opencsa/sca/200912' name='reference' autowire='false' target='target'/>";

    private ComponentReferenceLoader loader;
    private XMLStreamReader reader;
    private IntrospectionContext ctx;

    public void testLoad() throws Exception {
        ComponentReference reference = loader.load(reader, ctx);
        assertEquals("reference", reference.getName());
        assertEquals("target", reference.getTargets().get(0).getComponent());
        assertFalse(ctx.hasErrors());
    }

    public void testRoundTrip() throws Exception {
        loader.setRoundTrip(true);
        ComponentReference reference = loader.load(reader, ctx);
        assertEquals("reference", reference.getName());
        assertEquals("target", reference.getTargets().get(0).getComponent());
        Set<String> specified = reference.getSpecifiedAttributes();
        assertTrue(specified.contains("autowire"));
        assertTrue(specified.contains("target"));
        assertFalse(ctx.hasErrors());
    }

    protected void setUp() throws Exception {
        super.setUp();
        LoaderRegistry registry = new LoaderRegistryImpl(new MockXMLFactory());
        LoaderHelper helper = new DefaultLoaderHelper();

        MockImplementationLoader implLoader = new MockImplementationLoader();
        registry.registerLoader(MockImplementation.TYPE, implLoader);
        loader = new ComponentReferenceLoader(registry, helper);

        XMLFactory factory = new MockXMLFactory();
        reader = factory.newInputFactoryInstance().createXMLStreamReader(new ByteArrayInputStream(XML.getBytes()));
        reader.nextTag();
        ctx = new DefaultIntrospectionContext(URI.create("parent"), getClass().getClassLoader(), null, "foo");
    }

}