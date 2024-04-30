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

package io.maestro3.chef.service;

public interface IChefInfoService {
    String INSTALL_CHEF_CLIENT = "chefClient";
    String CHEF_CLIENT_PROFILE = "chefProfile";
    String INSTANCE_CHEF_UUID = "instanceChefUuid";
    String INSTANCE_SCRIPT = "instanceScript";
    String USER_SCRIPT_ID = "userScriptId";
    String AUTOCONFIGURATION_BUCKET_NAME = "AUTOCONFIGURATION_BUCKET_NAME";
    String CURRENT_CHEF_VERSION = "AUTOCONFIGURATION_CURRENT_CHEF_VERSION";
    String CHEF_STORAGE_CONFIG = "AUTOCONFIGURATION_CHEF_STORAGE_CONFIG_JSON";
    String CHEF_ENV = "AUTOCONFIGURATION_CHEF_ENV";
    String CHEF_NODE_NAME = "AUTOCONFIGURATION_CHEF_NODE_NAME";
    String CHEF_PREFIX = "AUTOCONFIGURATION_CHEF_PREFIX";
    String INSTANCE_UUID_SEPARATOR = ".";
    String AUTOCONFIGURATION_STATE_PATH = "AUTOCONFIGURATION_STATE_PATH";
    String AUTOCONFIGURATION_CERT_URL = "AUTOCONFIGURATION_CERT_URL";
    String AUTOCONFIGURATION_STATE_URL = "AUTOCONFIGURATION_STATE_URL";

    String getAutoconfigurationBucketName();

    String getCurrentChefVersion();

    String getChefEnv();

    String getPrefix();

    String getConfigurationUrl();

    String getChefNodeName();

    String getChefStateUrl();

    String getCertUrl();

    String getInitScriptStorageUrl();

    String getApiHost();

    boolean isChefEnabled();

    boolean isUserScriptsEnabled(String cloud);
}
