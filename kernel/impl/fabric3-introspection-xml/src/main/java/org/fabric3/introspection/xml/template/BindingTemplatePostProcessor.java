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
package org.fabric3.introspection.xml.template;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;

import org.fabric3.api.annotation.model.BindingTemplate;
import org.fabric3.api.model.type.component.Binding;
import org.fabric3.api.model.type.component.ComponentType;
import org.fabric3.api.model.type.component.Reference;
import org.fabric3.api.model.type.component.Service;
import org.fabric3.api.model.type.java.InjectingComponentType;
import org.fabric3.spi.introspection.IntrospectionContext;
import org.fabric3.spi.introspection.java.AbstractBindingPostProcessor;
import org.fabric3.spi.introspection.java.InvalidAnnotation;
import org.fabric3.spi.introspection.xml.TemplateRegistry;

/**
 * Handles services and references configured with {@link BindingTemplate}.
 */
public class BindingTemplatePostProcessor extends AbstractBindingPostProcessor<BindingTemplate> {
    private TemplateRegistry registry;

    public BindingTemplatePostProcessor(@org.oasisopen.sca.annotation.Reference TemplateRegistry registry) {
        super(BindingTemplate.class);
        this.registry = registry;
    }

    protected Binding processService(BindingTemplate annotation,
                                     Service<ComponentType> service,
                                     InjectingComponentType componentType,
                                     Class<?> implClass,
                                     IntrospectionContext context) {
        return resolve(annotation, implClass, implClass, context);
    }

    protected Binding processServiceCallback(BindingTemplate annotation,
                                             Service<ComponentType> service,
                                             InjectingComponentType componentType,
                                             Class<?> implClass,
                                             IntrospectionContext context) {
        return null; // not yet supported
    }

    protected Binding processReference(BindingTemplate annotation,
                                       Reference reference,
                                       AccessibleObject object,
                                       Class<?> implClass,
                                       IntrospectionContext context) {
        return resolve(annotation, object, implClass, context);
    }

    protected Binding processReferenceCallback(BindingTemplate annotation,
                                               Reference reference,
                                               AccessibleObject object,
                                               Class<?> implClass,
                                               IntrospectionContext context) {
        return null; // not yet supported
    }

    private Binding resolve(BindingTemplate annotation, AnnotatedElement element, Class<?> implClazz, IntrospectionContext context) {
        Binding binding = registry.resolve(Binding.class, annotation.value());
        if (binding == null) {
            InvalidAnnotation error = new InvalidAnnotation("Binding template not found: " + annotation.value(), element, annotation, implClazz);
            context.addError(error);
        }
        return binding;
    }

}
