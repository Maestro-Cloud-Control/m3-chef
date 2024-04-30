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

package io.maestro3.chef.util;

import io.maestro3.chef.builder.CertificateExtensionsBuilder;
import io.maestro3.chef.client.utils.EnsureBouncyCastle;
import io.maestro3.chef.client.utils.SecurityConstants;
import io.maestro3.chef.model.security.AlternativeName;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.jce.X509KeyUsage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

public class CertificateCreator {

    private static final int CERTIFICATE_TIME_TO_LIVE_IN_YEARS = 10;

    private static final int SERIAL_NUMBER_BITS_COUNT = 32;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    static {
        EnsureBouncyCastle.ensure();
    }

    private CertificateCreator() {
    }

    public static PKCS10CertificationRequest generateCertificateRequest(KeyPair keyPair, X500Principal subject)
            throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchProviderException, SignatureException {

        return new PKCS10CertificationRequest(
                SecurityConstants.SIGNATURE_ALGORITHM, subject, keyPair.getPublic(), null/*attributes set*/, keyPair.getPrivate());
    }

    /**
     * Creates X509 certificate signing it with CA certificate and using PKCS10 certification request.
     *
     * @param certificationRequest - PKCS10 certification request (CSR) is a message sent from an applicant to a certificate authority in order to apply for a digital identity certificate.
     * @param caCertificate        - X509 certificate with extension basicConstraints: ca=true.
     * @param caPrivateKey         - private key of CA certificate.
     * @return X509 certificate
     * @throws IOException
     * @throws OperatorCreationException
     * @throws CertificateException
     * @throws InvalidKeyException
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     */
    public static X509Certificate createAndSignCertificate(PKCS10CertificationRequest certificationRequest,
                                                           X509Certificate caCertificate, PrivateKey caPrivateKey) throws IOException,
            OperatorCreationException, CertificateException, InvalidKeyException, NoSuchProviderException, NoSuchAlgorithmException {

        X500Name issuer = new X500Name(caCertificate.getSubjectX500Principal().getName());
        X500Name subject = X500Name.getInstance(certificationRequest.getCertificationRequestInfo().getSubject());
        SubjectPublicKeyInfo subjectPublicKeyInfo = certificationRequest.getCertificationRequestInfo().getSubjectPublicKeyInfo();

        CertificateExtensionsBuilder certificateExtensionsBuilder = CertificateExtensionsBuilder.start()
                .basicConstraints(false, false)
                .keyUsage(false, KeyUsage.nonRepudiation, KeyUsage.digitalSignature, KeyUsage.keyEncipherment)
                .extendedKeyUsage(false, KeyPurposeId.id_kp_clientAuth, KeyPurposeId.id_kp_serverAuth)
                .authorityKeyIdentifier(false, caCertificate.getPublicKey())
                .subjectKeyIdentifier(false, certificationRequest.getPublicKey());

        return getCertificate(caPrivateKey, issuer, subject, subjectPublicKeyInfo, certificateExtensionsBuilder);
    }

    public static X509Certificate createAndSignCertificate(X509Certificate caCertificate, PrivateKey caPrivateKey, PublicKey publicKey, X500Principal caPrincipal) throws IOException,
            OperatorCreationException, CertificateException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        return createAndSignCertificateInternal(caCertificate, caPrivateKey, publicKey, caPrincipal, null);
    }

    /**
     * Creates CA certificate. Those certificates have basicConstraints extension with value ca=true.
     *
     * @param caPrivateKey - CA private key.
     * @param caPublicKey  - CA public key.
     * @param caPrincipal  - information about certificate.
     * @return X509 CA certificate.
     * @throws CertificateException
     * @throws OperatorCreationException
     * @throws InvalidKeyException
     * @throws IOException
     */
    public static X509Certificate createCaCertificate(PrivateKey caPrivateKey,
                                                      PublicKey caPublicKey,
                                                      X500Principal caPrincipal)
            throws CertificateException, OperatorCreationException, InvalidKeyException, IOException {

        X500Name issuerAndSubject = new X500Name(caPrincipal.getName());
        SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(caPublicKey.getEncoded());

        CertificateExtensionsBuilder certificateExtensionsBuilder = CertificateExtensionsBuilder.start()
                .basicConstraints(false, true)
                .authorityKeyIdentifier(false, caPublicKey)
                .subjectKeyIdentifier(false, caPublicKey);

        return getCertificate(caPrivateKey, issuerAndSubject, issuerAndSubject, publicKeyInfo, certificateExtensionsBuilder);
    }

    /**
     * Creates X509 certificate signing it with CA certificate.
     *
     * @param caCertificate    X509 certificate with extension basicConstraints: ca=true.
     * @param caPrivateKey     private key of CA certificate.
     * @param alternativeNames IP addresses of issuer instance
     * @return X509 certificate
     * @throws IOException
     * @throws OperatorCreationException
     * @throws CertificateException
     * @throws InvalidKeyException
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     */
    private static X509Certificate createAndSignCertificateInternal(X509Certificate caCertificate,
                                                                    PrivateKey caPrivateKey,
                                                                    PublicKey publicKey,
                                                                    X500Principal caPrincipal,
                                                                    String[] alternativeNames) throws IOException,
            OperatorCreationException, InvalidKeyException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
        try (ASN1InputStream inputStream = new ASN1InputStream(caCertificate.getPublicKey().getEncoded())) {
            ASN1Sequence seq = (ASN1Sequence) inputStream.readObject();
            SubjectPublicKeyInfo parentPubKeyInfo = new SubjectPublicKeyInfo(seq);
            ContentSigner signer = new JcaContentSignerBuilder(SecurityConstants.SIGNATURE_ALGORITHM).build(caPrivateKey);

            BigInteger serialNumber = new BigInteger(SERIAL_NUMBER_BITS_COUNT, SECURE_RANDOM);

            X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(caCertificate, serialNumber, caCertificate.getNotBefore(), caCertificate.getNotAfter(), caPrincipal, publicKey);
            CertificateExtensionsBuilder extensionsBuilder = CertificateExtensionsBuilder.start()
                    .authorityKeyIdentifier(false, parentPubKeyInfo)
                    .basicConstraints(false, false)
                    .keyUsage(true, X509KeyUsage.nonRepudiation, X509KeyUsage.keyEncipherment, X509KeyUsage.dataEncipherment)
                    .extendedKeyUsage(true, KeyPurposeId.id_kp_clientAuth, KeyPurposeId.id_kp_serverAuth);

            AlternativeName[] alternativeNamesArray = getAlternativeNamesArray(alternativeNames);

            if (alternativeNames != null && alternativeNames.length != 0) {
                extensionsBuilder.alternativeNames(false, alternativeNamesArray);
            }

            extensionsBuilder.fill(certificateBuilder);

            // Build/sign the certificate.
            X509CertificateHolder certHolder = certificateBuilder.build(signer);

            X509Certificate certificate = new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider()).getCertificate(certHolder);
            certificate.verify(caCertificate.getPublicKey());

            return certificate;
        }
    }

    private static AlternativeName[] getAlternativeNamesArray(String[] alternativeNames) {
        if (alternativeNames == null || alternativeNames.length == 0) {
            return new AlternativeName[0];
        }

        AlternativeName[] names = new AlternativeName[alternativeNames.length];
        for (int i = 0; i < alternativeNames.length; i++) {
            names[i] = AlternativeName.ip(alternativeNames[i]);
        }
        return names;
    }

    /**
     * Generates X509 certificate.
     *
     * @param privateKey                   private key for certificate.
     * @param issuer                       information about certificate issuer.
     * @param subject                      information about certificate subject.
     * @param subjectPublicKeyInfo         the object that contains the public key stored in a certificate.
     * @param certificateExtensionsBuilder needed for customizing certificate configuration.
     * @return X509 certificate.
     * @throws CertificateException
     * @throws IOException
     * @throws OperatorCreationException
     */
    private static X509Certificate getCertificate(PrivateKey privateKey,
                                                  X500Name issuer,
                                                  X500Name subject,
                                                  SubjectPublicKeyInfo subjectPublicKeyInfo,
                                                  CertificateExtensionsBuilder certificateExtensionsBuilder)
            throws CertificateException, IOException, OperatorCreationException {

        AlgorithmIdentifier signAlgorithmIdentifier = getSignAlgorithmIdentifier();
        AlgorithmIdentifier digestAlgorithmIdentifier = getDigestAlgorithmIdentifier(signAlgorithmIdentifier);

        // Create a private key parameter from a PKCS8 PrivateKeyInfo encoding
        AsymmetricKeyParameter asymmetricKeyParameter = PrivateKeyFactory.createKey(privateKey.getEncoded());
        ContentSigner signer = new BcRSAContentSignerBuilder(signAlgorithmIdentifier, digestAlgorithmIdentifier).build(asymmetricKeyParameter);

        Date from = new Date();
        Date to = new DateTime(DateTimeZone.UTC).plusYears(CERTIFICATE_TIME_TO_LIVE_IN_YEARS).toDate();

        BigInteger serialNumber = new BigInteger(SERIAL_NUMBER_BITS_COUNT, SECURE_RANDOM);

        X509v3CertificateBuilder certificateBuilder = new X509v3CertificateBuilder(issuer, serialNumber, from, to, subject, subjectPublicKeyInfo);
        certificateExtensionsBuilder.fill(certificateBuilder);

        X509CertificateHolder holder = certificateBuilder.build(signer);
        return new JcaX509CertificateConverter().setProvider(SecurityConstants.BOUNCY_CASTLE_PROVIDER_NAME).getCertificate(holder);
    }

    private static AlgorithmIdentifier getDigestAlgorithmIdentifier(AlgorithmIdentifier signAlgorithmIdentifier) {
        return new DefaultDigestAlgorithmIdentifierFinder().find(signAlgorithmIdentifier);
    }

    private static AlgorithmIdentifier getSignAlgorithmIdentifier() {
        return new DefaultSignatureAlgorithmIdentifierFinder().find(SecurityConstants.SIGNATURE_ALGORITHM);
    }

}
