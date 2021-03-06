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
package org.fabric3.spi.contribution;

import org.fabric3.api.host.Fabric3Exception;
import org.fabric3.spi.introspection.IntrospectionContext;

/**
 * Processes a contribution resource.
 */
public interface ResourceProcessor {

    /**
     * Returns the content type the processor handles.
     *
     * @return the content type the processor handles
     */
    String getContentType();

    /**
     * Indexes the resource.
     *
     * @param resource the resource to index
     * @param context  the context to which validation errors and warnings are reported
     * @throws Fabric3Exception if an error occurs during indexing
     */
    void index(Resource resource, IntrospectionContext context) throws Fabric3Exception;

    /**
     * Loads the the Resource.
     *
     * @param resource the resource to process
     * @param context  the context to which validation errors and warnings are reported
     * @throws Fabric3Exception if an error processing the contribution occurs
     */
    void process(Resource resource, IntrospectionContext context) throws Fabric3Exception;

}
