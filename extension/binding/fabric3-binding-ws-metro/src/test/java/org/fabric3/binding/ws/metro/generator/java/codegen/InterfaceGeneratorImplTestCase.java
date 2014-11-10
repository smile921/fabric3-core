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
 */
package org.fabric3.binding.ws.metro.generator.java.codegen;

import javax.jws.WebService;

import junit.framework.TestCase;
import org.oasisopen.sca.annotation.OneWay;

import org.fabric3.binding.ws.metro.util.ClassDefinerImpl;

/**
 *
 */
public class InterfaceGeneratorImplTestCase extends TestCase {
    private InterfaceGenerator generator;

    public void testWebMethodNoGeneration() throws Exception {
        assertFalse(generator.doGeneration(WebServiceAnnotatedClass.class));
    }

    public void testGeneration() throws Exception {
        assertTrue(generator.doGeneration(NoAnnotatedClass.class));
    }

    public void testOneWayGeneration() throws Exception {
        assertTrue(generator.doGeneration(OneWayAnnotatedClass.class));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        generator = new InterfaceGeneratorImpl(new ClassDefinerImpl());

    }

    @WebService
    public static class WebServiceAnnotatedClass {

    }

    public static class OneWayAnnotatedClass {
        @OneWay
        public void oneWay() {

        }
    }

    public static class NoAnnotatedClass {

    }

}