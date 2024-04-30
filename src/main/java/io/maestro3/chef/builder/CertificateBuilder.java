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

package io.maestro3.chef.builder;

import io.maestro3.chef.client.context.SecurityHelper;
import io.maestro3.chef.client.utils.SecurityConstants;
import io.maestro3.chef.model.security.AlternativeName;
import io.maestro3.chef.model.security.ServerKeyPair;
import io.maestro3.sdk.internal.util.Assert;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import javax.security.auth.x500.X500Principal;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

public class CertificateBuilder {

    private static final int SERIAL_NUMBER_BITS_COUNT = 32;

    private PrivateKey caPrivateKey;
    private X509Certificate caCertificate;
    private X500Principal subject;
    private CertificateExtensionsBuilder extensionsBuilder;

    public static CertificateBuilder create() {
        return new CertificateBuilder();
    }

    private CertificateBuilder() {
        extensionsBuilder = CertificateExtensionsBuilder.start();
    }

    public CertificateBuilder withSubject(X500Principal subject) {
        this.subject = subject;
        return this;
    }

    public CertificateBuilder altNames(AlternativeName... names) {
        if (names == null || names.length == 0) {
            return this;
        }
        extensionsBuilder.alternativeNames(false, names);
        return this;
    }

    public CertificateBuilder keyUsage(int... keyUsages) {
        if (keyUsages == null || keyUsages.length == 0) {
            return this;
        }
        extensionsBuilder.keyUsage(false, keyUsages);
        return this;
    }

    public CertificateBuilder extendedKeyUsage(boolean isCritical, KeyPurposeId... keyPurposeIds) {
        if (keyPurposeIds == null || keyPurposeIds.length == 0) {
            return this;
        }
        extensionsBuilder.extendedKeyUsage(isCritical, keyPurposeIds);
        return this;
    }

    public CertificateBuilder signWithCa(PrivateKey caPrivateKey, X509Certificate caCertificate) {
        if ((caPrivateKey == null && caCertificate != null) || (caPrivateKey != null && caCertificate == null)) {
            throw new IllegalArgumentException("both or none of caCertificate and caPrivateKey should be null.");
        }
        this.caPrivateKey = caPrivateKey;
        this.caCertificate = caCertificate;
        return this;
    }

    public ServerKeyPair build() {
        Assert.notNull(subject, "subject");
        try {
            KeyPair keyPair = SecurityHelper.generateKeyPair();
            X509Certificate certificate;

            if (caCertificate != null) {
                try (ASN1InputStream inputStream = new ASN1InputStream(caCertificate.getPublicKey().getEncoded())) {
                    ASN1Sequence seq = (ASN1Sequence) inputStream.readObject();
                    SubjectPublicKeyInfo parentPubKeyInfo = new SubjectPublicKeyInfo(seq);
                    extensionsBuilder.authorityKeyIdentifier(false, parentPubKeyInfo);
                }
            }

            BigInteger serialNumber = new BigInteger(SERIAL_NUMBER_BITS_COUNT, new SecureRandom());

            X509v3CertificateBuilder certificateBuilder;
            if (caCertificate != null) {
                certificateBuilder = new JcaX509v3CertificateBuilder(caCertificate,
                        serialNumber,
                        caCertificate.getNotBefore(),
                        caCertificate.getNotAfter(),
                        subject,
                        keyPair.getPublic()
                );
            } else {
                Calendar calendar = Calendar.getInstance();
                Date notBefore = calendar.getTime();
                calendar.add(Calendar.YEAR, 10);
                Date notAfter = calendar.getTime();
                certificateBuilder = new JcaX509v3CertificateBuilder(subject, serialNumber, notBefore, notAfter, subject, keyPair.getPublic());
            }
            extensionsBuilder.fill(certificateBuilder);

            // Build/sign the certificate.
            ContentSigner signer;
            if (caPrivateKey != null) {
                signer = new JcaContentSignerBuilder(SecurityConstants.SIGNATURE_ALGORITHM).build(caPrivateKey);
            } else {
                signer = new JcaContentSignerBuilder(SecurityConstants.SIGNATURE_ALGORITHM).build(keyPair.getPrivate());
            }
            X509CertificateHolder certHolder = certificateBuilder.build(signer);

            certificate = new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider()).getCertificate(certHolder);
            if (caCertificate != null) {
                certificate.verify(caCertificate.getPublicKey());
            }

            String privateKeyString = SecurityHelper.toString(keyPair.getPrivate());
            String certificateString = SecurityHelper.toString(certificate);
            return new ServerKeyPair(privateKeyString, certificateString);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate and sign X509 certificate. Reason: " + e.getMessage());
        }
    }
}
