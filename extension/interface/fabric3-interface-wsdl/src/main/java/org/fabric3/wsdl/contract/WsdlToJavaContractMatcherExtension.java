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

package org.fabric3.wsdl.contract;

import org.fabric3.spi.contract.MatchResult;
import org.fabric3.spi.model.type.java.JavaServiceContract;
import org.fabric3.wsdl.model.WsdlServiceContract;

/**
 * Determines if a WsdlServiceContract is compatible with a JavaServiceContract.
 */
public class WsdlToJavaContractMatcherExtension extends AbstractXsdContractMatcherExtension<WsdlServiceContract, JavaServiceContract> {

    public Class<WsdlServiceContract> getSource() {
        return WsdlServiceContract.class;
    }

    public Class<JavaServiceContract> getTarget() {
        return JavaServiceContract.class;
    }

    public MatchResult isAssignableFrom(WsdlServiceContract source, JavaServiceContract target, boolean reportErrors) {
        return matchContract(source, target, reportErrors);
    }

}