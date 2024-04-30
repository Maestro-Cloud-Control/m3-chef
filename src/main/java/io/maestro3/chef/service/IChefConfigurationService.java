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

import io.maestro3.chef.model.ChefConfiguration;
import io.maestro3.chef.model.ChefInstance;
import io.maestro3.chef.model.client.ChefClientDownloadInfo;
import io.maestro3.chef.model.client.ChefClientKey;

public interface IChefConfigurationService {
    ChefConfiguration getInstanceChefHostConfiguration(String regionName, ChefInstance instance);

    ChefConfiguration findByRegionName(String regionName);

    void setupChefServerConfigurationKey(String serverId, String validationPem, String certificate, String chefVersion, String serviceId);

    void setupChefServerConfigurationKey(ChefConfiguration chefConfiguration, String validationPem);

    void setupChefServerAdminUserCredentials(ChefConfiguration chefConfiguration, String auth, String apiUsername);

    ChefClientDownloadInfo getChefClientDownloadInfo(ChefClientKey clientKey);

    ChefConfiguration findProjectChef(String tenantInRegionId);

    ChefConfiguration findByServerId(String serverId);

    void update(ChefConfiguration chefConfiguration);

    void save(ChefConfiguration chefConfiguration);
}
