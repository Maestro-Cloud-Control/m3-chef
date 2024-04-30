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

package io.maestro3.chef.model.security;

import io.maestro3.chef.model.InstanceConnectionInfo;
import io.maestro3.chef.model.InstanceConnectionType;
import org.bouncycastle.asn1.x509.GeneralName;

public class AlternativeName {
    private final int identifier;
    private final String value;

    private AlternativeName(int identifier, String value) {
        this.identifier = identifier;
        this.value = value;
    }

    public static AlternativeName fromInstanceConnectionInfo(InstanceConnectionInfo connectionInfo) {
        return connectionInfo.getType() == InstanceConnectionType.DNS
            ? AlternativeName.dns(connectionInfo.getHost())
            : AlternativeName.ip(connectionInfo.getHost());
    }


    public static AlternativeName dns(String value) {
        return new AlternativeName(GeneralName.dNSName, value);
    }

    public static AlternativeName ip(String value) {
        return new AlternativeName(GeneralName.iPAddress, value);
    }

    public int getIdentifier() {
        return identifier;
    }

    public String getValue() {
        return value;
    }
}
