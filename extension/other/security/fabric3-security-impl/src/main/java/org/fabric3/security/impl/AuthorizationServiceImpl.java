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
package org.fabric3.security.impl;

import java.util.Collection;

import org.fabric3.api.SecuritySubject;
import org.fabric3.spi.security.AuthorizationException;
import org.fabric3.spi.security.AuthorizationService;
import org.fabric3.spi.security.BasicSecuritySubject;
import org.fabric3.spi.security.NotAuthorizedException;

/**
 * Basic implementation of the AuthorizationService.
 */
public class AuthorizationServiceImpl implements AuthorizationService {
    public void checkRole(SecuritySubject subject, String role) throws AuthorizationException {
        BasicSecuritySubject basicSubject = subject.getDelegate(BasicSecuritySubject.class);
        if (!basicSubject.hasRole(role)) {
            throw new NotAuthorizedException("Subject not authorized for role: " + role);
        }
    }

    public void checkRoles(SecuritySubject subject, Collection<String> roles) throws AuthorizationException {
        BasicSecuritySubject basicSubject = subject.getDelegate(BasicSecuritySubject.class);
        for (String role : roles) {
            if (!basicSubject.hasRole(role)) {
                throw new NotAuthorizedException("Subject not authorized for role");
            }
        }
    }

    public void checkPermission(SecuritySubject subject, String role) throws AuthorizationException {
        throw new UnsupportedOperationException();
    }

    public void checkPermissions(SecuritySubject subject, Collection<String> roles) throws AuthorizationException {
        throw new UnsupportedOperationException();
    }
}
