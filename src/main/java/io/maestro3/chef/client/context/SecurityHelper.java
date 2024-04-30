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

package io.maestro3.chef.client.context;

import io.maestro3.chef.client.utils.EnsureBouncyCastle;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.PEMWriter;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Class helper for parsing strings to certificates or keys and vise versa.
 *
 */
public final class SecurityHelper {

    private static final KeyFactory KEY_FACTORY;

    private static final String KEY_ALGORITHM = "RSA";

    private static final int MAX_KEY_SIZE = 4096;
    private static final int MIN_KEY_SIZE = 2048;

    static {
        EnsureBouncyCastle.ensure();
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance("RSA", BouncyCastleProvider.PROVIDER_NAME);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new IllegalStateException("Failed to create KeyFactory.", e);
        }
        KEY_FACTORY = keyFactory;
    }

    private SecurityHelper() {
        throw new IllegalStateException("Should never create instance of this class.");
    }

    public static X509Certificate loadCertificate(Reader reader) {
        X509CertificateObject x509 = null;
        PEMParser pemParser = new PEMParser(reader);
        try {
            X509CertificateHolder holder = (X509CertificateHolder) pemParser.readObject();
            Certificate certificate = holder.toASN1Structure();
            x509 = new X509CertificateObject(certificate);
        } catch (IOException | CertificateParsingException e) {
            throw new RuntimeException("Failed to load certificate.", e);
        } finally {
            closeQuietly(pemParser);
        }
        return x509;
    }

    public static X509Certificate loadCertificate(String certificateString) {
        return loadCertificate(new StringReader(certificateString));
    }

    public static PrivateKey loadPrivateKey(Reader reader) {
        KeyPair keyPair = loadKeyPair(reader);
        return keyPair.getPrivate();
    }

    public static PrivateKey loadPrivateKey(String privateKeyString) {
        KeyPair keyPair = loadKeyPair(new StringReader(privateKeyString));
        return keyPair.getPrivate();
    }

    public static KeyPair loadKeyPair(Reader reader) {
        PEMParser pemParser = new PEMParser(reader);
        KeyPair keyPair;
        try {
            PEMKeyPair pemKeyPair = (PEMKeyPair) pemParser.readObject();
            SubjectPublicKeyInfo publicKeyInfo = pemKeyPair.getPublicKeyInfo();
            PrivateKeyInfo privateKeyInfo = pemKeyPair.getPrivateKeyInfo();

            KeySpec spec = new PKCS8EncodedKeySpec(privateKeyInfo.getEncoded());
            PrivateKey privateKey = KEY_FACTORY.generatePrivate(spec);

            PublicKey publicKey = KEY_FACTORY.generatePublic(new X509EncodedKeySpec(publicKeyInfo.getEncoded()));
            keyPair = new KeyPair(publicKey, privateKey);
        } catch (IOException | InvalidKeySpecException e) {
            throw new RuntimeException("Failed to load private key.", e);
        } finally {
            closeQuietly(pemParser);
        }
        return keyPair;
    }

    public static KeyPair generateKeyPair(int size) {
        try {
            int normalizedSize = normalizeKeySize(size);
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM, KEY_FACTORY.getProvider());
            keyGenerator.initialize(normalizedSize);
            return keyGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate key pair.", e);
        }
    }

    public static KeyPair generateKeyPair() {
        return generateKeyPair(MIN_KEY_SIZE);
    }

    public static String toString(Object pemItem) {
        StringWriter stringWriter = new StringWriter();
        writePemItem(pemItem, stringWriter);
        return stringWriter.toString();
    }

    private static void writePemItem(Object pemItem, Writer writer) {
        PEMWriter pemWriter = new PEMWriter(writer);
        try {
            pemWriter.writeObject(pemItem);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write object to output.", e);
        } finally {
            closeQuietly(pemWriter);
        }
    }

    private static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ignore) {
        }
    }

    private static int normalizeKeySize(int size) {
        int normalizedSize = size;
        if (size > MAX_KEY_SIZE) {
            normalizedSize = MAX_KEY_SIZE;
        }
        if (size < MIN_KEY_SIZE) {
            normalizedSize = MIN_KEY_SIZE;
        }
        return normalizedSize;
    }
}
