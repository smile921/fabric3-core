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

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.net.URI;

import junit.framework.TestCase;
import org.fabric3.api.model.type.component.Component;
import org.fabric3.api.model.type.component.Consumer;
import org.fabric3.api.model.type.component.Property;
import org.fabric3.introspection.xml.LoaderRegistryImpl;
import org.fabric3.introspection.xml.common.ComponentConsumerLoader;
import org.fabric3.spi.introspection.DefaultIntrospectionContext;
import org.fabric3.spi.introspection.IntrospectionContext;
import org.fabric3.spi.introspection.xml.LoaderRegistry;

/**
 *
 */
public class ComponentConsumerLoaderTestCase extends TestCase {
    private static final String XML = "<consumer xmlns='http://docs.oasis-open.org/ns/opencsa/sca/200912' name='consumer' source='source'/>";

    private ComponentConsumerLoader loader;
    private XMLStreamReader reader;
    private IntrospectionContext ctx;

    public void testLoad() throws Exception {
        Consumer<Component> consumer = loader.load(reader, ctx);
        assertEquals("consumer", consumer.getName());
        assertEquals("source", consumer.getSources().get(0).toString());
        assertFalse(ctx.hasErrors());
    }

    protected void setUp() throws Exception {
        super.setUp();
        LoaderRegistry registry = new LoaderRegistryImpl();

        MockImplementationLoader implLoader = new MockImplementationLoader();
        implLoader.setProperties(new Property("prop"));
        registry.registerLoader(MockImplementation.TYPE, implLoader);
        loader = new ComponentConsumerLoader(registry);

        reader = XMLInputFactory.newFactory().createXMLStreamReader(new ByteArrayInputStream(XML.getBytes()));
        reader.nextTag();
        ctx = new DefaultIntrospectionContext(URI.create("parent"), getClass().getClassLoader(), null, "foo");
    }

}