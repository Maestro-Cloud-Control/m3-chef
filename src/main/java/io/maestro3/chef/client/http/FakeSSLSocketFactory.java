/*
 * Copyright 2023 Maestro Cloud Control LLC
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
 */

package io.maestro3.chef.client.http;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by ANTON_SULZHENKO
 * Date: 7/4/12
 * Time: 11:55 PM
 */
public class FakeSSLSocketFactory {

    private FakeSSLSocketFactory() {

    }

    public static SSLSocketFactory getInstance() throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        return new SSLSocketFactory(new TrustStrategy() {
            public boolean isTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
                return true;
            }

        }, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
    }
}
