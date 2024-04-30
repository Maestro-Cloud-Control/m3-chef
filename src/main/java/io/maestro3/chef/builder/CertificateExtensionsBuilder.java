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

import io.maestro3.chef.model.security.AlternativeName;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.util.LinkedList;
import java.util.List;

public class CertificateExtensionsBuilder {

    private final List<CertificateExtensionsBuilder.ExtensionObject> extensions = new LinkedList<>();

    private CertificateExtensionsBuilder() {
    }

    public static CertificateExtensionsBuilder start() {
        return new CertificateExtensionsBuilder();
    }

    public CertificateExtensionsBuilder basicConstraints(boolean isCritical, boolean isCa) {
        extensions.add(CertificateExtensionsBuilder.ExtensionObject.extension(Extension.basicConstraints, isCritical, new BasicConstraints(isCa)));
        return this;
    }

    /**
     * Use only those integers available in KeyUsage class.
     *
     * @see org.bouncycastle.asn1.x509.KeyUsage
     */
    public CertificateExtensionsBuilder keyUsage(boolean isCritical, int... keyUsages) {
        int usage = calculateUsage(keyUsages);
        extensions.add(CertificateExtensionsBuilder.ExtensionObject.extension(Extension.keyUsage, isCritical, new KeyUsage(usage)));
        return this;
    }

    public CertificateExtensionsBuilder extendedKeyUsage(boolean isCritical, KeyPurposeId... keyPurposeIds) {
        extensions.add(CertificateExtensionsBuilder.ExtensionObject.extension(Extension.extendedKeyUsage, isCritical, new ExtendedKeyUsage(keyPurposeIds)));
        return this;
    }

    /**
     * Specifies IP addresses for certificate issuer.
     */
    public CertificateExtensionsBuilder alternativeNames(boolean isCritical, AlternativeName... alternativeNames) {
        if (alternativeNames == null || alternativeNames.length == 0) {
            return this;
        }

        ASN1EncodableVector vector = new ASN1EncodableVector();
        for (AlternativeName alternativeName : alternativeNames) {
            GeneralName generalName = new GeneralName(alternativeName.getIdentifier(), alternativeName.getValue());
            vector.add(generalName);
        }
        ASN1Sequence sequence = new DERSequence(vector);

        GeneralNames generalNames = GeneralNames.getInstance(sequence);
        extensions.add(CertificateExtensionsBuilder.ExtensionObject.extension(Extension.subjectAlternativeName, isCritical, generalNames));
        return this;
    }

    public CertificateExtensionsBuilder authorityKeyIdentifier(boolean isCritical, PublicKey publicKey) throws InvalidKeyException {
        byte[] keyBytes = publicKey.getEncoded(); // X.509 for public key
        try {
            ASN1Primitive asn1Primitive = ASN1Sequence.fromByteArray(keyBytes);
            ASN1Sequence sequence = ASN1Sequence.getInstance(asn1Primitive);
            SubjectPublicKeyInfo subjectPublicKeyInfo = new SubjectPublicKeyInfo(sequence);
            return authorityKeyIdentifier(isCritical, subjectPublicKeyInfo);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create authorityKeyIdentifier extension.");
        }
    }

    public CertificateExtensionsBuilder authorityKeyIdentifier(boolean isCritical, SubjectPublicKeyInfo subjectPublicKeyInfo) throws InvalidKeyException {
        extensions.add(CertificateExtensionsBuilder.ExtensionObject.extension(Extension.authorityKeyIdentifier, isCritical, subjectPublicKeyInfo));
        return this;
    }

    public CertificateExtensionsBuilder subjectKeyIdentifier(boolean isCritical, PublicKey publicKey) throws InvalidKeyException {
        SubjectKeyIdentifier subjectKeyIdentifier = new SubjectKeyIdentifier(publicKey.getEncoded());
        extensions.add(CertificateExtensionsBuilder.ExtensionObject.extension(Extension.subjectKeyIdentifier, isCritical, subjectKeyIdentifier));
        return this;
    }

    public void fill(X509v3CertificateBuilder certificateBuilder) {
        for (CertificateExtensionsBuilder.ExtensionObject extension : extensions) {
            try {
                certificateBuilder.addExtension(extension.identifier, extension.critical, extension.encodable);
            } catch (CertIOException e) {
                throw new IllegalStateException("Failed to add extension " + extension.identifier);
            }
        }
        extensions.clear();
    }

    private int calculateUsage(int... keyUsages) {
        int usage = 0;
        for (int keyUsage : keyUsages) {
            usage = usage | keyUsage;
        }
        return usage;
    }

    private static class ExtensionObject {
        private ASN1ObjectIdentifier identifier;
        private boolean critical;
        private ASN1Encodable encodable;

        static CertificateExtensionsBuilder.ExtensionObject extension(ASN1ObjectIdentifier identifier, boolean critical, ASN1Encodable encodable) {
            CertificateExtensionsBuilder.ExtensionObject object = new CertificateExtensionsBuilder.ExtensionObject();
            object.identifier = identifier;
            object.critical = critical;
            object.encodable = encodable;
            return object;
        }
    }
}
