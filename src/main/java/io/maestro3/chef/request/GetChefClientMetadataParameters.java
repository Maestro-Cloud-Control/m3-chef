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

package io.maestro3.chef.request;

import io.maestro3.cadf.util.Assert;

public class GetChefClientMetadataParameters {

    private String instanceName;
    private String platform;
    private String platformVersion;
    private String machine;
    private String version;
    private String project; // not EPC project
    private String channel;

    private GetChefClientMetadataParameters(Builder builder) {
        this.instanceName = builder.instanceName;
        this.platform = builder.platform;
        this.platformVersion = builder.platformVersion;
        this.machine = builder.machine;
        this.version = builder.version;
        this.project = builder.project;
        this.channel = builder.channel;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getInstanceName() {
        return instanceName;
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

    public String getProject() {
        return project;
    }

    public String getChannel() {
        return channel;
    }

    public static class Builder {
        private String instanceName;
        private String platform;
        private String platformVersion;
        private String machine;
        private String version;
        private String project;
        private String channel;

        private Builder() {
        }

        public Builder instanceName(String instanceName) {
            Assert.hasText(instanceName, "instanceName cannot be null or empty.");
            this.instanceName = instanceName;
            return this;
        }

        public Builder platform(String platform) {
            Assert.hasText(platform, "platform cannot be null or empty.");
            this.platform = platform;
            return this;
        }

        public Builder platformVersion(String platformVersion) {
            Assert.hasText(platformVersion, "platformVersion cannot be null or empty.");
            this.platformVersion = platformVersion;
            return this;
        }

        public Builder machine(String machine) {
            Assert.hasText(machine, "machine cannot be null or empty.");
            this.machine = machine;
            return this;
        }

        public Builder version(String version) {
            Assert.hasText(version, "version cannot be null or empty.");
            this.version = version;
            return this;
        }

        public Builder project(String project) {
            Assert.hasText(project, "project cannot be null or empty.");
            this.project = project;
            return this;
        }

        public Builder channel(String channel) {
            Assert.hasText(channel, "channel cannot be null or empty.");
            this.channel = channel;
            return this;
        }

        public GetChefClientMetadataParameters build() {
            Assert.hasText(instanceName, "instanceName cannot be null or empty.");
            Assert.hasText(platform, "platform cannot be null or empty.");
            Assert.hasText(platformVersion, "platformVersion cannot be null or empty.");
            Assert.hasText(machine, "machine cannot be null or empty.");
            Assert.hasText(version, "version cannot be null or empty.");
            Assert.hasText(project, "project cannot be null or empty.");
            Assert.hasText(channel, "channel cannot be null or empty.");
            return new GetChefClientMetadataParameters(this);
        }
    }
}
