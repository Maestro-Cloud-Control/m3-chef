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

import org.bouncycastle.util.encoders.Base64;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

public final class ChefUtils {
    private static final int HEADER_LENGTH = 60;
    private static final String HASHING_ALGORITHM = "SHA-1";

    private ChefUtils() {
        throw new UnsupportedOperationException("Instantiation is forbidden.");
    }

    /**
     * Concatenates given strings into one using StringBuilder.
     *
     * @param parts the list of string to concatenate
     * @return concatenated string
     */
    public static String buildString(Object... parts) {
        StringBuilder builder = new StringBuilder();
        for (Object part : parts) {
            builder.append(part);
        }
        return builder.toString();
    }

    /**
     * Hashes given string.
     *
     * @param input the string to hash
     * @return hash of the string
     * @throws Exception when hashing algorithm can't be found
     */
    public static String getHash(String input) throws Exception {
        MessageDigest messageDigest;
        byte[] result;
        try {
            messageDigest = MessageDigest.getInstance(HASHING_ALGORITHM);
            byte[] digest = messageDigest.digest(input.getBytes());
            result = Base64.encode(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("Can't hash up string", e);
        }
        return new String(result);
    }

    /**
     * Signs up given string using given key. Key should be private so other side can read data using public key.
     *
     * @param stringToSign the string to be signed up with the key
     * @param key          the private key to be used for signing the string
     * @return signed string
     * @throws Exception when signing fails or signing algorithm is not found
     */
    public static String signAuthenticationString(String stringToSign, Key key) throws Exception {
        return RsaEncryptionUtils.encrypt((PrivateKey) key, stringToSign);
    }

    /**
     * Splits input string into several string of the same size.
     *
     * @param input the string to split
     * @return the array of string parts
     */
    public static String[] splitHeaders(String input) {
        int length = input.length();
        int count = length / HEADER_LENGTH;
        boolean hasRemainder = length % HEADER_LENGTH != 0;
        String[] result = new String[hasRemainder ? count + 1 : count];

        for (int index = 0; index < count; index++) {
            int start = index * HEADER_LENGTH;
            result[index] = input.substring(start, start + HEADER_LENGTH);
        }

        if (hasRemainder) {
            result[count] = input.substring(count * HEADER_LENGTH);
        }

        return result;
    }
}
