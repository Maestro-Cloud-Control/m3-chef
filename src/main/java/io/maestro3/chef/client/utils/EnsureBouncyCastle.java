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

import io.maestro3.chef.client.exception.EnsureBouncyCastleException;

import java.security.Security;

public final class EnsureBouncyCastle {

    private EnsureBouncyCastle() {
        throw new IllegalStateException("Class is not designed for instantiation.");
    }

    public static void ensure() {
        try {
            Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
            EnsureBouncyCastleInternal.ensure();
        } catch (ClassNotFoundException e) {
            throw new EnsureBouncyCastleException("Required security lib is missing.");
        }
    }

    private static class EnsureBouncyCastleInternal {
        public static void ensure() {
            if (Security.getProvider(SecurityConstants.BOUNCY_CASTLE_PROVIDER_NAME) == null) {
                Security.insertProviderAt(SecurityConstants.BOUNCY_CASTLE_PROVIDER, 1);
            }
        }
    }
}
