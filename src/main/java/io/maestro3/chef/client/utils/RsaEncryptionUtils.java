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

package io.maestro3.chef.client.utils;

import io.maestro3.chef.client.exception.RsaEncryptionException;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;

public final class RsaEncryptionUtils {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private static final String RSA = "RSA";

    private RsaEncryptionUtils() {
        throw new UnsupportedOperationException("Instantiation is forbidden.");
    }

    public static String encrypt(PrivateKey rsaPrivateKey, String messageToEncrypt) throws RsaEncryptionException {
        byte[] result;
        try {
            Signature instance = Signature.getInstance(RSA);
            instance.initSign(rsaPrivateKey);
            instance.update(messageToEncrypt.getBytes(UTF_8));

            byte[] signature = instance.sign();
            result = Base64.encode(signature);
        } catch (Exception e) {
            throw new RsaEncryptionException("Failed to encrypt message.");
        }
        return new String(result);
    }

    public static String decrypt(Key rsaPrivateKey, String messageToDecrypt) throws RsaEncryptionException {
        String message = messageToDecrypt;
        String algorithm = "RSA/NONE/PKCS1Padding";

        String[] messageAndAlgorithm = messageToDecrypt.split(" ");
        if (messageAndAlgorithm.length == 2) {
            message = messageAndAlgorithm[0];
            algorithm = messageAndAlgorithm[1];
        }

        Cipher cipher = getCipher(rsaPrivateKey, algorithm);
        String decryptedMessage = decrypt(cipher, message);
        if (!StringUtils.isBlank(decryptedMessage)) {
            return decryptedMessage;
        }
        throw new RsaEncryptionException("Failed to decrypt message.");
    }

    private static Cipher getCipher(Key rsaPrivateKey, String algorithm) throws RsaEncryptionException {
        boolean useBouncyCastleSecurityProvider = !RSA.equals(algorithm);
        if (useBouncyCastleSecurityProvider) {
            ensureBounceCastleProvider();
            return getInstanceOfCipher(rsaPrivateKey, algorithm, "BC");
        }
        return getInstanceOfCipher(rsaPrivateKey, algorithm);
    }

    private static void ensureBounceCastleProvider() throws RsaEncryptionException {
        try {
            Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
            EnsureBouncyCastle.ensure();
        } catch (ClassNotFoundException e) {
            throw new RsaEncryptionException("Required security lib is missing.");
        }
    }

    private static Cipher getInstanceOfCipher(Key rsaPrivateKey, String algorithm) throws RsaEncryptionException {
        return getInstanceOfCipher(rsaPrivateKey, algorithm, null);
    }

    private static Cipher getInstanceOfCipher(Key rsaPrivateKey, String algorithm, String provider) throws RsaEncryptionException {
        try {
            Cipher cipher;
            if (StringUtils.isBlank(provider)) {
                cipher = Cipher.getInstance(algorithm);
            } else {
                cipher = Cipher.getInstance(algorithm, provider);
            }
            cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
            return cipher;
        } catch (NoSuchAlgorithmException e) {
            throw new RsaEncryptionException("Unsupported algorithm: " + algorithm);
        } catch (NoSuchProviderException e) {
            throw new RsaEncryptionException("Could not find any security provider for decryption.");
        } catch (NoSuchPaddingException e) {
            throw new RsaEncryptionException("Incorrect message padding.");
        } catch (InvalidKeyException e) {
            throw new RsaEncryptionException("Invalid key provided.");
        }
    }

    private static String decrypt(Cipher cipher, String messageToDecrypt) throws RsaEncryptionException {
        try {
            byte[] base64DecodedMessage = Base64.decode(messageToDecrypt);
            byte[] decodedMessageBytes = cipher.doFinal(base64DecodedMessage);
            return new String(decodedMessageBytes, UTF_8);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            // should never happen
            throw new RsaEncryptionException("Failed to decrypt message.");
        }
    }
}
