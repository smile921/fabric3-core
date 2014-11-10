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
package org.fabric3.fabric.container.builder.classloader;

import junit.framework.TestCase;

/**
 *
 */
public class ClassLoaderWireFilterTestCase extends TestCase {

    public void testNoFilterPackage() throws Exception {
        String packge = getClass().getPackage().getName();
        ClassLoaderWireFilter filter = new ClassLoaderWireFilter(getClass().getClassLoader(), packge);
        // verify class can be loaded
        filter.loadClass(getClass().getName());
    }

    public void testNoFilterWildCardPackage() throws Exception {
        Package name = getClass().getPackage();
        String packge = name.getName().substring(0, name.getName().lastIndexOf(".")) + ".*";
        ClassLoaderWireFilter filter = new ClassLoaderWireFilter(getClass().getClassLoader(), packge);
        // verify class can be loaded
        filter.loadClass(getClass().getName());
    }

    public void testFilterPackage() {
        ClassLoaderWireFilter filter = new ClassLoaderWireFilter(getClass().getClassLoader(), "foo.bar");
        // verify class is not loaded
        try {
            filter.loadClass(getClass().getName());
            fail("Class should not be visible");
        } catch (ClassNotFoundException e) {
            // expected
        }
    }

}
