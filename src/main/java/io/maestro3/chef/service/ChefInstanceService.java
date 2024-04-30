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

import io.maestro3.chef.dao.IChefInstanceDao;
import io.maestro3.chef.model.AutoConfigurationState;
import io.maestro3.chef.model.ChefInstance;
import io.maestro3.chef.model.InstanceProperty;
import io.maestro3.chef.model.UserData;
import io.maestro3.chef.util.PropertyUtils;
import io.maestro3.sdk.internal.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ChefInstanceService implements IChefInstanceService {

    private static final Logger LOG = LoggerFactory.getLogger(ChefInstanceService.class);

    private IChefInstanceDao chefInstanceDao;
    private IChefInfoService autoconfigurationSettingsManager;
    private IGpgService gpgService;

    @Autowired
    public ChefInstanceService(IChefInstanceDao chefInstanceDao,
                               IGpgService gpgService,
                               IChefInfoService autoconfigurationSettingsManager) {
        this.gpgService = gpgService;
        this.autoconfigurationSettingsManager = autoconfigurationSettingsManager;
        this.chefInstanceDao = chefInstanceDao;
    }

    @Override
    public ChefInstance getInstanceByResourceId(String resourceId) {
        Assert.hasText(resourceId, "resourceId can not be null or empty");
        return chefInstanceDao.findByResourceId(resourceId);
    }

    @Override
    public void removeInstanceByResourceId(String resourceId) {
        Assert.hasText(resourceId, "resourceId can not be null or empty");
        chefInstanceDao.removeByResourceId(resourceId);
    }

    @Override
    public ChefInstance getInstance(String instanceId) {
        return chefInstanceDao.find(instanceId);
    }

    @Override
    public List<ChefInstance> findAllInRegion(String tenant, String regionId) {
        return chefInstanceDao.findAllInRegion(tenant, regionId);
    }

    @Override
    public ChefInstance getInstanceByIp(String instanceIp) {
        Assert.hasText(instanceIp, "instance ip can not be null or empty");
        return chefInstanceDao.findByIp(instanceIp);
    }

    @Override
    public void setAutoConfigurationState(ChefInstance instance, AutoConfigurationState receivedAcState) {
        Assert.notNull(instance, "instance can not be null");
        Assert.notNull(instance.getInstanceId(), "instance id can not be null");
        Assert.notNull(receivedAcState, "acState can not be null or empty");
        chefInstanceDao.setAutoConfigurationState(instance, receivedAcState);
    }

    @Override
    public void updateNetworkConfig(ChefInstance instance) {
        Assert.notNull(instance, "instance can not be null");
        Assert.notNull(instance.getResourceId(), "resourceId can not be null");
        Assert.notNull(instance.getPublicIp(), "public ip can not be null");
        Assert.notNull(instance.getPrivateIp(), "private ip can not be null");
        Assert.notNull(instance.getFqdn(), "fqdn can not be null");
        chefInstanceDao.updateInstanceNetworkSettings(instance.getResourceId(), instance.getPublicIp(),
                instance.getPrivateIp(), instance.getFqdn());
    }

    @Override
    public void setChefServer(ChefInstance instance, String chefServer, String chefMode) {
        Assert.notNull(instance, "instance can not be null");
        Assert.notNull(instance.getInstanceId(), "instance id can not be null");
        Assert.notNull(chefServer, "chefServer can not be null or empty");
        Assert.notNull(chefMode, "chefMode can not be null or empty");
        chefInstanceDao.setChefServer(instance, chefServer, chefMode);
    }

    @Override
    public void setRoles(ChefInstance instance, Set<String> receivedChefRoles) {
        Assert.notNull(instance, "instance can not be null");
        Assert.notNull(instance.getInstanceId(), "instance id can not be null");
        Assert.notNull(receivedChefRoles, "receivedChefRoles can not be null or empty");
        chefInstanceDao.setRoles(instance, receivedChefRoles);
    }

    @Override
    public void setConfigurationReceived(ChefInstance instance) {
        Assert.notNull(instance, "instance can't be null.");
        Assert.hasText(instance.getInstanceId(), "instance.id can't be null or empty.");
        chefInstanceDao.setConfigurationReceived(instance);
    }

    @Override
    public void setSshKeySet(ChefInstance instance) {
        Assert.notNull(instance, "instance can't be null.");
        Assert.hasText(instance.getInstanceId(), "instance.id can't be null or empty.");
        chefInstanceDao.setSshKeySet(instance);
    }

    @Override
    public String getInstancePublicKey(String instanceId) {
        Assert.hasText(instanceId, "instance id can not be null or empty");
        return chefInstanceDao.getInstancePublicKey(instanceId);
    }

    @Override
    public void updateInstanceProperties(ChefInstance instance, List<InstanceProperty> instanceProperties, boolean append) {
        Assert.notNull(instance, "instance can't be null.");
        chefInstanceDao.setProperties(instance, PropertyUtils.mergeResourceProperties(instance.getInstanceProperties(), instanceProperties, append));
    }

    @Override
    public void setInstanceProperties(String instanceId, List<InstanceProperty> instanceProperties) {
        Assert.notNull(instanceId, "instance can't be null.");
        chefInstanceDao.setProperties(instanceId, instanceProperties);
    }

    @Override
    public void saveInstance(ChefInstance instance) {
        Assert.notNull(instance, "instance can't be null.");
        chefInstanceDao.saveInstance(instance);
    }

    @Override
    public void setScriptFiles(ChefInstance instance, Map<String, String> scriptFiles) {
        Assert.notNull(instance, "instanceId can't be null or empty.");
        Assert.hasText(instance.getInstanceId(), "instance id can not be null");
        Assert.notNull(scriptFiles, "scriptFiles can't be null or empty.");
        chefInstanceDao.setScriptFiles(instance, scriptFiles);
    }

    @Override
    public ChefInstance findInstance(String tenant, String regionId, String instanceId) {
        Assert.notNull(tenant, "tenant can't be null.");
        Assert.notNull(regionId, "zone can't be null.");
        Assert.notNull(instanceId, "instanceId can't be null.");
        return chefInstanceDao.findByTenantInRegionAndInstanceId(tenant, regionId, instanceId);
    }

    @Override
    public Map<String, String> getCommonDatabag(String tenantName, String tenantDisplayName, String regionName,
                                                String instanceId, String owner, String cloud) {
        ChefInstance instance = getInstance(instanceId);
        List<InstanceProperty> instanceProperties = instance.getInstanceProperties();
        Map<String, String> commonProperties = new HashMap<>();
        commonProperties.put(UserData.PROJECT_PARAM, StringUtils.upperCase(tenantDisplayName));
        commonProperties.put(UserData.REGION_PARAM, StringUtils.upperCase(regionName));
        String instanceFqdn = instance.getFqdn();
        if (StringUtils.isBlank(instanceFqdn)) {
            LOG.info("Got blank FQDN. Instance ID: {}", instance.getInstanceId());
            instanceFqdn = "temp.com";
        } else {
            instanceFqdn = StringUtils.lowerCase(instanceFqdn);
        }
        commonProperties.put(UserData.INSTANCE_FQDN, instanceFqdn);
        commonProperties.put(UserData.REGION_TYPE, cloud);
        commonProperties.put(UserData.CHEF_CALLBACK_URL, autoconfigurationSettingsManager.getApiHost());
        commonProperties.put(UserData.STORAGE_URL, autoconfigurationSettingsManager.getInitScriptStorageUrl());
        commonProperties.put(UserData.LAST_RUN, autoconfigurationSettingsManager.getChefStateUrl());
        if (StringUtils.isNotBlank(owner)) {
            commonProperties.put(UserData.EP_OWNER_NAME, StringUtils.lowerCase(owner));
        }
        if (CollectionUtils.isNotEmpty(instanceProperties)) {
            for (InstanceProperty property : instanceProperties) {
                String propertyValue = StringUtils.lowerCase(property.getValue());
                if (StringUtils.isBlank(propertyValue)) {
                    continue;
                }
                if (UserData.EP_CHEFROLE.equalsIgnoreCase(property.getKey())) {
                    commonProperties.put(UserData.EP_CHEFROLE, propertyValue);
                }
                if ("ep_hostname".equalsIgnoreCase(property.getKey())) {
                    commonProperties.put("ep_hostname", propertyValue);
                }
                if ("ep_chefrecipe".equalsIgnoreCase(property.getKey())) {
                    commonProperties.put("ep_chefrecipe", propertyValue);
                }
            }

        }
        if (instance.getPublicKey() != null) {
            commonProperties.put(UserData.USERKEY_PARAM, instance.getPublicKey());
        } else {
            if (CollectionUtils.isNotEmpty(instanceProperties)) {
                // encrypt admin password if we the user specified one
                for (InstanceProperty property : instanceProperties) {
                    if (UserData.EP_ADMIN_PASSWORD_PLACEHOLDER.equalsIgnoreCase(property.getKey()) && StringUtils.isNotBlank(property.getValue())) {
                        property.setKey(UserData.EP_ADMIN_PASSWORD_PLACEHOLDER);
                        property.setValue(gpgService.encrypt(property.getValue()));
                        break;
                    }
                }
            }
        }
        return commonProperties;
    }
}
