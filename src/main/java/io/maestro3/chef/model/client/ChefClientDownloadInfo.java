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

public class ChefClientDownloadInfo {
    private final String key;
    private final String sha256;
    private final String uri;
    private final String version;

    public ChefClientDownloadInfo(String key, String sha256, String uri, String version) {
        this.key = key;
        this.sha256 = sha256;
        this.uri = uri;
        this.version = version;
    }

    public String getKey() {
        return key;
    }

    public String getSha256() {
        return sha256;
    }

    public String getUri() {
        return uri;
    }

    public String getVersion() {
        return version;
    }
}
