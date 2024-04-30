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

import io.maestro3.chef.exception.PermissionException;
import io.maestro3.chef.model.AutoConfigurationState;
import io.maestro3.chef.model.BasicResponse;
import io.maestro3.chef.model.ChefInstance;
import io.maestro3.chef.model.OperationResult;
import io.maestro3.chef.model.TenantChefConfiguration;
import io.maestro3.chef.util.IpUtils;
import io.maestro3.sdk.internal.util.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * <p><b>Author:</b> Dmytro_Kurkin </p>
 * <p><b>Date:</b> 26.12.12 </p>
 */
@Service
class UserdataCollectingService implements IUserdataCollectingService {
    private static final Logger LOG = LoggerFactory.getLogger(UserdataCollectingService.class);

    private final IChefInstanceService chefInstanceService;
    private final IRegionProvider regionProvider;
    private final IChefTenantConfigProvider tenantSettingsService;


    @Autowired
    public UserdataCollectingService(IChefInstanceService chefInstanceService, IRegionProvider regionProvider,
                                     IChefTenantConfigProvider tenantSettingsService) {
        this.chefInstanceService = chefInstanceService;
        this.regionProvider = regionProvider;
        this.tenantSettingsService = tenantSettingsService;
    }

    @Override
    public BasicResponse<OperationResult> updateAutoConfigurationState(String instanceId, String requestIp, AutoConfigurationState receivedAcState, String chefServer, Set<String> receivedChefRoles) throws PermissionException {
        ChefInstance instance = chefInstanceService.getInstance(instanceId);

        if (instance == null) {
            return new BasicResponse<>(HttpStatus.SC_NOT_FOUND, "Instance was not registered");
        }

        IpUtils.assertInstanceIp(instance);

        if (receivedAcState == null || receivedAcState.in(AutoConfigurationState.UNKNOWN)) {
            String message = String.format("Wrong auto configuration state received for instance %s", instanceId);
            LOG.warn(message);
            return new BasicResponse<>(HttpStatus.SC_BAD_REQUEST, message);
        }

        if (isStateChanged(instance, receivedAcState, receivedChefRoles)) {
            chefInstanceService.setAutoConfigurationState(instance, receivedAcState);
        }

        if (StringUtils.isNotBlank(chefServer) && !chefServer.equals(instance.getChefServer())) {
            String chefMode = instance.getChefMode();
            String regionName = regionProvider.findById(instance.getRegionId());
            TenantChefConfiguration chefConfiguration = tenantSettingsService.getConfig(instance.getTenantName(), regionName);
            if (StringUtils.isBlank(chefMode)) {
                chefMode = chefConfiguration.getChefMode();
            } else {
                String projectChefMode = chefConfiguration.getChefMode();
                if (!chefMode.equals(projectChefMode)) {
                    chefMode = projectChefMode;
                }
            }

            // if chefServer has changed
            chefInstanceService.setChefServer(instance, chefServer, chefMode);
        }

        if (AutoConfigurationState.STARTED.equals(receivedAcState)) {
            chefInstanceService.setRoles(instance, receivedChefRoles);
        }

        return new BasicResponse<>(OperationResult.SUCCESS);
    }

    boolean isStateChanged(ChefInstance instance, AutoConfigurationState receivedAcState, Set<String> receivedChefRoles) {
        AutoConfigurationState currentAcState = instance.getAutoConfigurationState();
        Set<String> currentNodeChefRoles = instance.getRoles();

        if (AutoConfigurationState.UNKNOWN.equals(currentAcState)) {
            return true;
        }
        if (AutoConfigurationState.STARTED.equals(receivedAcState)) {
            return !currentNodeChefRoles.containsAll(receivedChefRoles);
        }
        return !currentAcState.equals(receivedAcState);
    }
}
