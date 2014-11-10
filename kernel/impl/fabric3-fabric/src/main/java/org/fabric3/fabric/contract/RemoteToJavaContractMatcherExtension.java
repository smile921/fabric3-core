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

package org.fabric3.fabric.contract;

import org.fabric3.spi.contract.ContractMatcherExtension;
import org.fabric3.spi.contract.MatchResult;
import org.fabric3.spi.model.type.java.JavaServiceContract;
import org.fabric3.spi.model.type.remote.RemoteServiceContract;

/**
 * Compares a {@link RemoteServiceContract} and {@link JavaServiceContract} for compatibility.
 */
public class RemoteToJavaContractMatcherExtension implements ContractMatcherExtension<RemoteServiceContract, JavaServiceContract> {

    public Class<RemoteServiceContract> getSource() {
        return RemoteServiceContract.class;
    }

    public Class<JavaServiceContract> getTarget() {
        return JavaServiceContract.class;
    }

    public MatchResult isAssignableFrom(RemoteServiceContract source, JavaServiceContract target, boolean reportErrors) {
        String sourceName = source.getQualifiedInterfaceName();
        if (sourceName.equals(target.getQualifiedInterfaceName())) {
            return MatchResult.MATCH;
        }
        for (String interfaze : target.getInterfaces()) {
            if (sourceName.equals(interfaze)) {
                return MatchResult.MATCH;
            }
        }
        return MatchResult.NO_MATCH;
    }

}
