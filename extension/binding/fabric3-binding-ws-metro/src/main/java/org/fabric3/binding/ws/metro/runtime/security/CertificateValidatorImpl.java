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
 *
 * Originally based on code from the Sun Metro Project XWS-Security extension:
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
*/
package org.fabric3.binding.ws.metro.runtime.security;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import com.sun.xml.wss.impl.XWSSecurityRuntimeException;

/**
 * Default CertificateValidator extension.
 */
public class CertificateValidatorImpl implements CertificateValidator {

    public boolean validate(X509Certificate certificate, KeyStore trustStore) throws XWSSecurityRuntimeException {
        try {
            certificate.checkValidity();
        } catch (CertificateExpiredException | CertificateNotYetValidException e) {
            throw new XWSSecurityRuntimeException(e);
        }

        // for self-signed certificate
        if (certificate.getIssuerX500Principal().equals(certificate.getSubjectX500Principal())) {
            if (isTrustedSelfSigned(certificate, trustStore)) {
                return true;
            } else {
                throw new XWSSecurityRuntimeException("Validation of self signed certificate failed");
            }
        }

        X509CertSelector certSelector = new X509CertSelector();
        certSelector.setCertificate(certificate);

        PKIXBuilderParameters parameters;
        CertPathValidator certValidator;
        CertPath certPath;
        List<Certificate> certChainList = new ArrayList<>();
        boolean caFound = false;
        Principal certChainIssuer = null;
        int noOfEntriesInTrustStore = 0;
        boolean isIssuerCertMatched = false;

        try {
            parameters = new PKIXBuilderParameters(trustStore, certSelector);
            parameters.setRevocationEnabled(false);   // TODO should ths be hardcoded?
            //create a CertStore on the fly with CollectionCertStoreParameters since some JDK's
            //cannot build chains to certs only contained in a TrustStore
            CertStore cs = CertStore.getInstance("Collection", new CollectionCertStoreParameters(Collections.singleton(certificate)));
            parameters.addCertStore(cs);

            Certificate[] certChain = null;
            String certAlias = trustStore.getCertificateAlias(certificate);
            if (certAlias != null) {
                certChain = trustStore.getCertificateChain(certAlias);
            }
            if (certChain == null) {
                certChainList.add(certificate);
                certChainIssuer = certificate.getIssuerX500Principal();
                noOfEntriesInTrustStore = trustStore.size();
            } else {
                certChainList = Arrays.asList(certChain);
            }
            while (!caFound && noOfEntriesInTrustStore-- != 0 && certChain == null) {
                Enumeration aliases = trustStore.aliases();
                while (aliases.hasMoreElements()) {
                    String alias = (String) aliases.nextElement();
                    Certificate cert = trustStore.getCertificate(alias);
                    if (cert == null || !"X.509".equals(cert.getType()) || certChainList.contains(cert)) {
                        continue;
                    }
                    X509Certificate x509Cert = (X509Certificate) cert;
                    if (certChainIssuer.equals(x509Cert.getSubjectX500Principal())) {
                        certChainList.add(cert);
                        if (x509Cert.getSubjectX500Principal().equals(x509Cert.getIssuerX500Principal())) {
                            caFound = true;
                            break;
                        } else {
                            certChainIssuer = x509Cert.getIssuerDN();
                            if (!isIssuerCertMatched) {
                                isIssuerCertMatched = true;
                            }
                        }
                    }
                }
                if (!caFound) {
                    if (!isIssuerCertMatched) {
                        break;
                    } else {
                        isIssuerCertMatched = false;
                    }
                }
            }
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            certPath = cf.generateCertPath(certChainList);
            certValidator = CertPathValidator.getInstance("PKIX");
            certValidator.validate(certPath, parameters);
            return true;
        } catch (InvalidAlgorithmParameterException | KeyStoreException | CertificateException | NoSuchAlgorithmException | CertPathValidatorException e) {
            throw new XWSSecurityRuntimeException(e);
        }
    }

    private static boolean isTrustedSelfSigned(X509Certificate cert, KeyStore trustStore) throws XWSSecurityRuntimeException {
        try {
            Enumeration aliases = trustStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = (String) aliases.nextElement();
                Certificate certificate = trustStore.getCertificate(alias);
                if (certificate == null || !"X.509".equals(certificate.getType())) {
                    continue;
                }
                X509Certificate x509Cert = (X509Certificate) certificate;
                if (x509Cert.equals(cert)) {
                    return true;
                }
            }
            return false;
        } catch (KeyStoreException e) {
            throw new XWSSecurityRuntimeException(e);
        }
    }

}
