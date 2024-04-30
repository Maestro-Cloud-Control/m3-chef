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

package io.maestro3.chef.scheduler;

import io.maestro3.chef.client.context.IChefContextFactory;
import io.maestro3.chef.model.ChefConfiguration;
import io.maestro3.chef.model.ChefRoleInfo;
import io.maestro3.chef.service.IChefConfigurationManager;
import io.maestro3.chef.service.IChefConfigurationService;
import io.maestro3.chef.service.IChefInfoService;
import io.maestro3.chef.service.IRegionProvider;
import io.maestro3.sdk.internal.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
public class ChefRoleUpdater implements IChefRoleUpdater {

    private static final Logger LOG = LoggerFactory.getLogger(ChefRoleUpdater.class);

    private IChefInfoService managementSettingsManger;
    private IChefConfigurationService chefConfigurationService;
    private IChefConfigurationManager chefConfigurationManager;
    private IChefContextFactory chefContextFactory;
    private IRegionProvider zoneService;

    @Autowired
    public ChefRoleUpdater(IChefInfoService managementSettingsManger, IChefConfigurationService chefConfigurationService,
                           IChefConfigurationManager chefConfigurationManager, IRegionProvider zoneService,
                           IChefContextFactory chefContextFactory) {
        this.chefContextFactory = chefContextFactory;
        this.managementSettingsManger = managementSettingsManger;
        this.chefConfigurationService = chefConfigurationService;
        this.chefConfigurationManager = chefConfigurationManager;
        this.zoneService = zoneService;
    }

    @Scheduled(cron = "${cron.chef.roles.update: 0 45 4 ? * *}")
    public void updateChefRolesInZones() {
        try {
            if (!managementSettingsManger.isChefEnabled()) {
                return;
            }
            for (String zone : zoneService.findAll()) {
                try {
                    LOG.info("Updating chef roles for {}", zone);
                    ChefConfiguration chefConfiguration = chefConfigurationService.findByRegionName(zone);
                    if (chefConfiguration == null) {
                        LOG.warn("Chef does not configured for zone {}", zone);
                        continue;
                    }
                    updateChefRolesInConfiguration(chefConfiguration);
                } catch (Exception e) {
                    LOG.error("Failed to update chef configuration for region " + zone, e);
                }
            }
        } catch (Throwable e) {
            LOG.error("Internal error on running Chef roles update", e);
        }
    }

    @Override
    public void updateChefRolesInConfiguration(ChefConfiguration chefConfiguration) {
        Set<String> roles = chefConfigurationManager.describeChefRolesForServer(chefConfiguration.getServerId());
        if (CollectionUtils.isNotEmpty(roles)) {
            List<ChefRoleInfo> roleInfos = new ArrayList<>();
            roles.forEach(roleInfo -> roleInfos.add(new ChefRoleInfo(roleInfo, 0, 0, Collections.emptyList())));
            chefConfiguration.setRoles(roleInfos);
            chefConfigurationService.update(chefConfiguration);
            LOG.info("Chef roles successfully updated for {}", chefConfiguration.getServerId());
        } else {
            LOG.info("Chef roles not found for {}", chefConfiguration.getServerId());
        }
        chefContextFactory.invalidateCache();
    }
}
