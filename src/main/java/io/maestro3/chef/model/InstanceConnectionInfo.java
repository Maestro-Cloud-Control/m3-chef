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

package io.maestro3.chef.model;

public class InstanceConnectionInfo {

    private final String host;
    private final InstanceConnectionType type;

    private InstanceConnectionInfo(String host, InstanceConnectionType type) {
        this.host = host;
        this.type = type;
    }

    public static InstanceConnectionInfo dns(String dnsName) {
        return new InstanceConnectionInfo(dnsName, InstanceConnectionType.DNS);
    }

    public static InstanceConnectionInfo ip(String ipAddress) {
        return new InstanceConnectionInfo(ipAddress, InstanceConnectionType.IP);
    }

    public String getHost() {
        return host;
    }

    public InstanceConnectionType getType() {
        return type;
    }
}
