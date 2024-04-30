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

import io.maestro3.chef.model.AutoConfigurationState;
import io.maestro3.chef.model.ChefInstance;
import io.maestro3.chef.model.InstanceProperty;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IChefInstanceService {
    ChefInstance getInstance(String instanceId);

    ChefInstance getInstanceByResourceId(String resourceId);

    List<ChefInstance> findAllInRegion(String tenant, String regionId);

    ChefInstance getInstanceByIp(String instanceIp);

    void setAutoConfigurationState(ChefInstance instance, AutoConfigurationState receivedAcState);

    void updateNetworkConfig(ChefInstance instance);

    void setChefServer(ChefInstance instance, String chefServer, String chefMode);

    void setRoles(ChefInstance instance, Set<String> receivedChefRoles);

    void setConfigurationReceived(ChefInstance instance);

    void setSshKeySet(ChefInstance instance);

    String getInstancePublicKey(String instanceId);

    void updateInstanceProperties(ChefInstance instance, List<InstanceProperty> instanceProperties, boolean append);

    void setInstanceProperties(String instanceId, List<InstanceProperty> instanceProperties);

    void saveInstance(ChefInstance instance);

    void removeInstanceByResourceId(String resourceId);

    void setScriptFiles(ChefInstance instance, Map<String, String> scriptFiles);

    ChefInstance findInstance(String tenant, String regionId, String instanceId);

    Map<String, String> getCommonDatabag(String tenant, String tenantDisplayName, String regionName, String instanceId,
                                         String owner, String cloud);

}
