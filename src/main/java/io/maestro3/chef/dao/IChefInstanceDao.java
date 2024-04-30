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

package io.maestro3.chef.dao;

import io.maestro3.chef.model.AutoConfigurationState;
import io.maestro3.chef.model.ChefInstance;
import io.maestro3.chef.model.InstanceProperty;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IChefInstanceDao {
    ChefInstance find(String instanceId);

    List<ChefInstance> findAllInRegion(String tenantName, String regionId);

    ChefInstance findByResourceId(String resourceId);

    ChefInstance findByTenantInRegionAndInstanceId(String tenantName, String regionId, String instanceId);

    void removeByResourceId(String resourceId);

    ChefInstance findByIp(String ip);

    void setAutoConfigurationState(ChefInstance instance, AutoConfigurationState state);

    void updateInstanceNetworkSettings(String resourceId, String publicIp, String privateIp, String publicDns);

    void setChefServer(ChefInstance instance, String chefServer, String chefMode);

    void setRoles(ChefInstance instance, Set<String> roles);

    void setConfigurationReceived(ChefInstance instance);

    void setSshKeySet(ChefInstance instance);

    String getInstancePublicKey(String instanceId);

    void setProperties(ChefInstance instance, List<InstanceProperty> instanceProperties);

    void setProperties(String instanceId, List<InstanceProperty> instanceProperties);

    void saveInstance(ChefInstance chefInstance);

    void setScriptFiles(ChefInstance instance, Map<String, String> scriptFiles);
}
