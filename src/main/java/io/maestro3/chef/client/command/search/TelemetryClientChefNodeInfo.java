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

package io.maestro3.chef.client.command.search;

import com.google.gson.annotations.SerializedName;
import io.maestro3.chef.client.utils.ChefUtils;

public class TelemetryClientChefNodeInfo {

    @SerializedName("data")
    private TelemetryAttributes attributes;

    public TelemetryAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(TelemetryAttributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return ChefUtils.buildString("TelemetryClientChefNodeInfo{attributes=", attributes, "}");
    }

    public static class TelemetryAttributes {
        private String host;
        private String state;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        @Override
        public String toString() {
            return ChefUtils.buildString("TelemetryAttributes{state=", state, ", host", host, "}");
        }
    }
}
