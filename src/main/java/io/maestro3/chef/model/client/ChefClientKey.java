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

package io.maestro3.chef.model.client;

import io.maestro3.sdk.internal.util.Assert;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ChefClientKey {
    private final String platform;
    private final String platformVersion;
    private final String machine;
    private final String version;

    public ChefClientKey(String platform, String platformVersion, String machine, String version) {
        Assert.hasText(platform, "platform cannot be null or empty.");
        Assert.hasText(platformVersion, "platformVersion cannot be null or empty.");
        Assert.hasText(machine, "machine cannot be null or empty.");
        Assert.hasText(version, "version cannot be null or empty.");
        this.platform = platform;
        this.platformVersion = platformVersion;
        this.machine = machine;
        this.version = version;
    }

    public String getPlatform() {
        return platform;
    }

    public String getPlatformVersion() {
        return platformVersion;
    }

    public String getMachine() {
        return machine;
    }

    public String getVersion() {
        return version;
    }

    public String encode() {
        return "p:" + platform + ":pv:" + platformVersion + ":m:" + machine + ":v:" + version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ChefClientKey clientKey = (ChefClientKey) o;

        return new EqualsBuilder()
                .append(platform, clientKey.platform)
                .append(platformVersion, clientKey.platformVersion)
                .append(machine, clientKey.machine)
                .append(version, clientKey.version)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(platform)
                .append(platformVersion)
                .append(machine)
                .append(version)
                .toHashCode();
    }
}
