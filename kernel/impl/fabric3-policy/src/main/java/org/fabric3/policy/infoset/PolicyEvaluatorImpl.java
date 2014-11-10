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
package org.fabric3.policy.infoset;

import java.util.Collection;
import java.util.List;

import org.fabric3.policy.xpath.LogicalModelXPath;
import org.fabric3.spi.model.instance.Bindable;
import org.fabric3.spi.model.instance.LogicalBinding;
import org.fabric3.spi.model.instance.LogicalComponent;
import org.fabric3.spi.model.instance.LogicalOperation;
import org.fabric3.spi.model.instance.LogicalScaArtifact;
import org.jaxen.JaxenException;

/**
 *
 */
public class PolicyEvaluatorImpl implements PolicyEvaluator {

    @SuppressWarnings({"unchecked"})
    public Collection<LogicalScaArtifact<?>> evaluate(String xpathExpression, LogicalComponent<?> component) throws PolicyEvaluationException {
        try {
            LogicalModelXPath xpath = new LogicalModelXPath(xpathExpression);
            Object ret = xpath.evaluate(component);
            if (ret instanceof Collection) {
                return (Collection<LogicalScaArtifact<?>>) ret;
            }
            throw new PolicyEvaluationException("Invalid select expression: " + xpathExpression);
        } catch (JaxenException e) {
            throw new PolicyEvaluationException(e);
        }
    }

    public boolean doesApply(String appliesToXPath, LogicalScaArtifact<?> target) throws PolicyEvaluationException {
        try {
            LogicalModelXPath xpath = new LogicalModelXPath(appliesToXPath);
            Object selected = xpath.evaluate(target);
            if (selected instanceof Boolean) {
                return (Boolean) selected;
            } else if (selected instanceof List) {
                return !((List) selected).isEmpty();
            }
            return false;
        } catch (JaxenException e) {
            throw new PolicyEvaluationException(e);
        }

    }

    public boolean doesAttach(String attachesToXPath, LogicalComponent<?> target, LogicalComponent<?> context) throws PolicyEvaluationException {
        try {
            LogicalModelXPath xpath = new LogicalModelXPath(attachesToXPath);
            Object selected = xpath.evaluate(context);
            if (selected instanceof List) {
                List<?> list = (List<?>) selected;
                if (list.isEmpty()) {
                    return false;
                }
                for (Object entry : list) {
                    if (entry instanceof LogicalComponent) {
                        if (entry == target) {
                            return true;
                        }
                    } else if (entry instanceof Bindable) {
                        if (((Bindable) entry).getParent() == target) {
                            return true;
                        }
                    } else if (entry instanceof LogicalBinding) {
                        if (((LogicalBinding) entry).getParent().getParent() == target) {
                            return true;
                        }
                    } else if (entry instanceof LogicalOperation) {
                        if (((LogicalOperation) entry).getParent().getParent() == target) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (JaxenException e) {
            throw new PolicyEvaluationException(e);
        }

    }

}