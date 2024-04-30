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

import io.maestro3.chef.client.context.IChefContext;
import io.maestro3.chef.model.AutoConfigurationState;
import io.maestro3.chef.model.ChefConfiguration;
import io.maestro3.chef.model.ChefInstance;
import io.maestro3.chef.model.InstanceProperty;
import io.maestro3.chef.model.TenantChefConfiguration;
import io.maestro3.chef.model.UserData;
import io.maestro3.chef.model.role.ChefRole;
import io.maestro3.chef.util.ChefUtils;
import io.maestro3.sdk.internal.util.StringUtils;
import io.maestro3.sdk.v3.model.instance.SdkInstance;
import io.maestro3.sdk.v3.model.instance.SdkInstances;
import io.maestro3.sdk.v3.request.instance.RunInstanceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class ServerAutoconfigurationFacade implements IAutoconfigurationFacade {
    private final Logger LOG = LoggerFactory.getLogger(ServerAutoconfigurationFacade.class);
    private IResourceIdGenerator resourceIdGenerator;
    private IChefTenantConfigProvider chefTenantConfigProvider;
    private IChefConfigurationService chefConfigurationService;
    private IChefInstanceService chefInstanceService;
    private ICertTokenService certTokenService;
    private IChefDataBagService chefDataBagService;
    private IUserdataProvisionService userdataProvisionService;

    @Autowired
    public ServerAutoconfigurationFacade(IResourceIdGenerator resourceIdGenerator,
                                         IChefTenantConfigProvider chefTenantConfigProvider,
                                         IChefConfigurationService chefConfigurationService,
                                         IChefInstanceService chefInstanceService, ICertTokenService certTokenService,
                                         IChefDataBagService chefDataBagService, IUserdataProvisionService userdataProvisionService) {
        this.resourceIdGenerator = resourceIdGenerator;
        this.chefTenantConfigProvider = chefTenantConfigProvider;
        this.chefConfigurationService = chefConfigurationService;
        this.chefInstanceService = chefInstanceService;
        this.certTokenService = certTokenService;
        this.chefDataBagService = chefDataBagService;
        this.userdataProvisionService = userdataProvisionService;
    }

    @Override
    public void autoconfigurationPostprocessing(RunInstanceRequest request, String initiator, String regionName, String regionId,
                                                String tenantName, String tenantDisplayName, IChefContext chefContext, String cloud,
                                                SdkInstances instances) throws Exception {
        if (request.isInstallChefClient() && StringUtils.isBlank(request.getInitScript())) {
            for (SdkInstance instance : instances.getSdkInstances()) {
                LOG.info("Saving chef instance {}", instance.getInstanceId());
                ChefInstance chefInstance = saveChefInstance(tenantName, regionName, regionId, cloud,
                        request.getChefProfile(), instance, request.getAdditionalData(), request.getInsanceChefUuid());
                processDatabagCreation(chefContext, request, tenantName, tenantDisplayName, regionName, cloud, chefInstance, initiator);
            }
        }
    }

    private void processDatabagCreation(IChefContext chefContext, RunInstanceRequest request, String tenant,
                                        String tenantDisplayName, String region, String cloud,
                                        ChefInstance chefInstance, String owner) {
        if (chefInstance == null) {
            throw new IllegalArgumentException("ChefInstance can not be null");
        }
        String instanceChefUUID = chefInstance.getInstanceId();
        LOG.info("Processing databag for instance {}", instanceChefUUID);
        if (!chefDataBagService.dataBagExists(chefContext, instanceChefUUID)) {
            LOG.info("Creating databag for instance {}", instanceChefUUID);
            chefDataBagService.createDataBag(chefContext, instanceChefUUID);
        }
        Map<String, String> commonParams = chefInstanceService.getCommonDatabag(tenant, tenantDisplayName,
                region, instanceChefUUID, owner, cloud);
        String chefProfile = request.getChefProfile();
        if (StringUtils.isNotBlank(chefProfile)) {
            commonParams.put(UserData.ROLE, chefProfile);
        }
        LOG.info("Updating common databag item for instance {}", instanceChefUUID);
        chefDataBagService.updateOrCreateItem(chefContext, instanceChefUUID, ChefRole.BASE.getDataBagItemName(), commonParams);

        Map<String, String> serviceProperties = userdataProvisionService.provideUserdataProperties(tenant, region, chefInstance);

        if (StringUtils.isNotBlank(chefProfile)) {
            LOG.info("Updating chef roles, creating profile items for {}", instanceChefUUID);
            Map<String, String> dataBagMapping = chefContext.getDataBagMapping();
            String[] roles = new String[]{ChefRole.BASE.getName(), chefProfile};
            chefDataBagService.createNode(chefContext, instanceChefUUID);
            chefDataBagService.pushChefRole(chefContext, instanceChefUUID, roles);
            String dataBagName = dataBagMapping.getOrDefault(chefProfile, chefProfile);
            chefDataBagService.updateOrCreateItem(chefContext, instanceChefUUID, dataBagName, request.getAdditionalData());
            chefDataBagService.updateOrCreateItem(chefContext, instanceChefUUID, dataBagName, serviceProperties);
        }
    }

    private ChefInstance saveChefInstance(String tenant, String region, String regionId, String cloud, String chefProfile,
                                          SdkInstance sdkInstance, Map<String, String> additionalData,
                                          String instanceChefUUID) throws Exception {
        String resourceId = resourceIdGenerator.generate(tenant, region, sdkInstance);

        ChefInstance instance = new ChefInstance();
        instance.setInstanceId(instanceChefUUID);
        instance.setResourceId(resourceId);
        instance.setRegionId(regionId);
        instance.setTenantName(tenant);
        instance.setRegionName(region);
        instance.setAutoConfigurationState(AutoConfigurationState.UNKNOWN);
//        Instance describedInstance = getInstance(tenant, region, sdkInstance);
//        if (describedInstance != null) {
//            instance.setFqdn(describedInstance.getPrivateDnsName());
//            instance.setPublicIp(describedInstance.getPublicIpAddress());
//            instance.setPrivateIp(describedInstance.getPrivateIpAddress());
//        }
        instance.setCloud(cloud);
        instance.setAdditionalData(ChefUtils.encodeJson(additionalData));
        String serverId;

        TenantChefConfiguration chefConfiguration = chefTenantConfigProvider.getConfig(tenant, region);
        if (chefConfiguration != null && StringUtils.isNotBlank(chefConfiguration.getCustomChefConfigurationId())) {
            serverId = chefConfiguration.getCustomChefConfigurationId();
        } else {
            ChefConfiguration defaultConfig = chefConfigurationService.findByRegionName(region);
            if (defaultConfig == null) {
                LOG.error("Default chef configuration is not found for region {}", region);
                return null;
            }
            serverId = defaultConfig.getServerId();
        }
        instance.setChefServer(serverId);
        instance.setInitDate(System.currentTimeMillis());
        if (StringUtils.isNotBlank(chefProfile)) {
            InstanceProperty property = new InstanceProperty();
            property.setKey(UserData.EP_CHEFROLE);
            property.setValue(chefProfile);
            instance.setInstanceProperties(Collections.singletonList(property));
        }
        chefInstanceService.saveInstance(instance);

        certTokenService.provideTokenAccessToChefCertificates(tenant, instance);
        return instance;
    }
}
