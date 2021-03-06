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
package org.fabric3.transform.property;

import java.net.MalformedURLException;
import java.net.URL;

import org.fabric3.api.host.Fabric3Exception;
import org.fabric3.api.model.type.contract.DataType;
import org.fabric3.spi.model.type.TypeConstants;
import org.fabric3.spi.model.type.java.JavaType;
import org.fabric3.spi.transform.SingleTypeTransformer;
import org.w3c.dom.Node;

/**
 * Transforms from a Node to a URL.
 */
public class Property2URLTransformer implements SingleTypeTransformer<Node, URL> {
    private static final JavaType TARGET = new JavaType(URL.class);

    public DataType getSourceType() {
        return TypeConstants.PROPERTY_TYPE;
    }

    public DataType getTargetType() {
        return TARGET;
    }

    public URL transform(final Node node, ClassLoader loader) throws Fabric3Exception {
        final String content = node.getTextContent();
        final URL url;
        try {
            url = new URL(node.getTextContent());
        } catch (MalformedURLException me) {
            throw new Fabric3Exception("Unable to create URL :- " + content, me);
        }
        return url;
    }
}
