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
package org.fabric3.introspection.java.annotation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import org.fabric3.api.annotation.Source;
import org.fabric3.api.annotation.model.Component;
import org.fabric3.api.annotation.model.Namespace;
import org.fabric3.api.model.type.component.Property;
import org.fabric3.api.model.type.java.InjectingComponentType;
import org.fabric3.spi.introspection.IntrospectionContext;
import org.fabric3.spi.introspection.TypeMapping;
import org.fabric3.spi.introspection.java.IntrospectionHelper;
import org.fabric3.spi.introspection.java.InvalidAnnotation;
import org.fabric3.spi.introspection.java.MultiplicityType;
import org.fabric3.spi.introspection.java.annotation.AbstractAnnotationProcessor;
import org.fabric3.spi.model.type.java.ConstructorInjectionSite;
import org.fabric3.spi.model.type.java.FieldInjectionSite;
import org.fabric3.spi.model.type.java.MethodInjectionSite;
import org.oasisopen.sca.annotation.Reference;

/**
 *
 */
public class OASISPropertyProcessor extends AbstractAnnotationProcessor<org.oasisopen.sca.annotation.Property> {
    private final IntrospectionHelper helper;

    public OASISPropertyProcessor(@Reference IntrospectionHelper helper) {
        super(org.oasisopen.sca.annotation.Property.class);
        this.helper = helper;
    }

    public void visitField(org.oasisopen.sca.annotation.Property annotation,
                           Field field,
                           Class<?> implClass,
                           InjectingComponentType componentType,
                           IntrospectionContext context) {
        validate(annotation, field, componentType, context);
        String name = helper.getSiteName(field, annotation.name());
        Type type = field.getGenericType();
        FieldInjectionSite site = new FieldInjectionSite(field);
        TypeMapping typeMapping = context.getTypeMapping(implClass);
        boolean required = annotation.required();
        Property property = createDefinition(name, required, type, typeMapping);
        processSource(field,field.getDeclaringClass(), property, context);
        componentType.add(property, site);
    }

    public void visitMethod(org.oasisopen.sca.annotation.Property annotation,
                            Method method,
                            Class<?> implClass,
                            InjectingComponentType componentType,
                            IntrospectionContext context) {
        boolean result = validate(annotation, method, componentType, context);
        if (!result) {
            return;
        }
        String name = helper.getSiteName(method, annotation.name());
        Type type = helper.getGenericType(method);
        MethodInjectionSite site = new MethodInjectionSite(method, 0);
        TypeMapping typeMapping = context.getTypeMapping(implClass);
        boolean required = annotation.required();
        Property property = createDefinition(name, required, type, typeMapping);
        processSource(method, method.getDeclaringClass(), property, context);
        componentType.add(property, site);
    }

    public void visitConstructorParameter(org.oasisopen.sca.annotation.Property annotation,
                                          Constructor<?> constructor,
                                          int index,
                                          Class<?> implClass,
                                          InjectingComponentType componentType,
                                          IntrospectionContext context) {
        String name = helper.getSiteName(constructor, index, annotation.name());
        Type type = helper.getGenericType(constructor, index);
        ConstructorInjectionSite site = new ConstructorInjectionSite(constructor, index);
        TypeMapping typeMapping = context.getTypeMapping(implClass);
        boolean required = annotation.required();
        Property property = createDefinition(name, required, type, typeMapping);
        Class<?> paramType = constructor.getParameterTypes()[index];
        Class<?> declaringClass = constructor.getDeclaringClass();
        processSource(paramType, declaringClass, property, context);
        componentType.add(property, site);
    }

    private void validate(org.oasisopen.sca.annotation.Property annotation, Field field, InjectingComponentType componentType, IntrospectionContext context) {
        if (!Modifier.isProtected(field.getModifiers()) && !Modifier.isPublic(field.getModifiers())) {
            Class<?> clazz = field.getDeclaringClass();
            if (annotation.required()) {
                InvalidAccessor error = new InvalidAccessor("Invalid required property. The field " + field.getName() + " on " + clazz.getName()
                                                            + " is annotated with @Property but properties must be public or protected.", field, componentType);
                context.addError(error);
            } else {
                InvalidAccessor warning = new InvalidAccessor("Ignoring the field " + field.getName() + " annotated with @Property on " + clazz.getName()
                                                              + ". Properties must be public or protected.", field, componentType);
                context.addWarning(warning);
            }
        }
    }

    private boolean validate(org.oasisopen.sca.annotation.Property annotation,
                             Method method,
                             InjectingComponentType componentType,
                             IntrospectionContext context) {
        if (method.getParameterTypes().length != 1) {
            InvalidMethod error = new InvalidMethod("Setter methods for properties must have a single parameter: " + method, method, componentType);
            context.addError(error);
            return false;
        }
        if (!Modifier.isProtected(method.getModifiers()) && !Modifier.isPublic(method.getModifiers())) {
            if (annotation.required()) {
                InvalidAccessor error = new InvalidAccessor(
                        "Invalid required property. The method " + method + " is annotated with @Property and must be public or protected.",
                        method,
                        componentType);
                context.addError(error);
                return false;
            } else {
                InvalidAccessor warning = new InvalidAccessor("Ignoring " + method + " annotated with @Property. Property " + "must be public or protected.",
                                                              method,
                                                              componentType);
                context.addWarning(warning);
                return false;
            }
        }
        return true;
    }

    private Property createDefinition(String name, boolean required, Type type, TypeMapping typeMapping) {
        Property property = new Property(name);
        property.setRequired(required);
        MultiplicityType multiplicityType = helper.introspectMultiplicity(type, typeMapping);
        property.setMany(MultiplicityType.COLLECTION == multiplicityType || MultiplicityType.DICTIONARY == multiplicityType);
        return property;
    }

    private void processSource(AccessibleObject accessible, Class<?> clazz, Property property, IntrospectionContext context) {
        Source source = accessible.getAnnotation(Source.class);
        if (source != null) {
            if (!source.value().startsWith("$")) {
                InvalidAnnotation error = new InvalidAnnotation("Source attribute must specify an expression starting with '$' on:" + accessible,
                                                                accessible,
                                                                source,
                                                                clazz);
                context.addError(error);
            } else {
                property.setSource(source.value());

                // handle defined namespaces
                Component componentAnnotation = clazz.getAnnotation(Component.class);
                if (componentAnnotation != null) {
                    Namespace[] namespaces = componentAnnotation.namespaces();
                    for (Namespace namespace : namespaces) {
                        String prefix = namespace.prefix();
                        String uri = namespace.uri();
                        property.addNamespace(prefix, uri);
                    }
                }
            }
        }
    }

    private void processSource(Class<?> type, Class<?> clazz, Property property, IntrospectionContext context) {
        Source source = type.getAnnotation(Source.class);
        if (source != null) {
            if (!source.value().startsWith("$")) {
                InvalidAnnotation error = new InvalidAnnotation("Source attribute must specify an expression starting with '$' on:" + type,
                                                                type,
                                                                source,
                                                                clazz);
                context.addError(error);
            } else {
                property.setSource(source.value());
            }
        }
    }

}