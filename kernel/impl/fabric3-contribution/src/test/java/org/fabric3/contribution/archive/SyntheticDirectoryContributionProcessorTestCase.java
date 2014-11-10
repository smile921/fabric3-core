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
package org.fabric3.contribution.archive;

import java.net.URI;
import java.net.URL;

import junit.framework.TestCase;
import org.oasisopen.sca.annotation.EagerInit;

import org.fabric3.api.host.stream.Source;
import org.fabric3.api.host.stream.UrlSource;
import org.fabric3.spi.contribution.Contribution;
import org.fabric3.spi.introspection.DefaultIntrospectionContext;

/**
 *
 */
@EagerInit
public class SyntheticDirectoryContributionProcessorTestCase extends TestCase {
    private SyntheticDirectoryContributionProcessor processor;
    private Contribution contribution;
    private DefaultIntrospectionContext context;

    public void testCanProcess() throws Exception {
        assertTrue(processor.canProcess(contribution));
    }

    public void testProcessManifest() throws Exception {
        processor.processManifest(contribution, context);
        assertTrue(contribution.getManifest().getExtends().contains("1"));
    }

    public void testIndex() throws Exception {
        processor.index(contribution, context);
    }

    public void testProcess() throws Exception {
        processor.process(contribution, context);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        URI uri = URI.create("contribution");
        URL url = getClass().getResource("/repository/1");
        Source source = new UrlSource(url);
        contribution = new Contribution(uri, source, url, -1, "application/vnd.fabric3.synthetic", false);

        processor = new SyntheticDirectoryContributionProcessor();
        context = new DefaultIntrospectionContext();
    }
}