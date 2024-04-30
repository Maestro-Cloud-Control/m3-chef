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

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public interface SecurityConstants {

    String SIGNATURE_ALGORITHM = "SHA1withRSA";

    String X509_CERTIFICATE_TYPE = "X.509";

    String BOUNCY_CASTLE_PROVIDER_NAME = BouncyCastleProvider.PROVIDER_NAME;

    BouncyCastleProvider BOUNCY_CASTLE_PROVIDER = new BouncyCastleProvider();

    String CERTIFICATE_COUNTRY = "BY";
    String CERTIFICATE_LOCALITY = "Minsk";
    String CERTIFICATE_STATE = "MSQ";
    String CERTIFICATE_ORGANIZATION = "EPAM Systems";
}
