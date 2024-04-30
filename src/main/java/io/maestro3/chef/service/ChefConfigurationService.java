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

import io.maestro3.chef.dao.IChefConfigurationDao;
import io.maestro3.chef.model.ChefConfiguration;
import io.maestro3.chef.model.ChefInstance;
import io.maestro3.chef.model.TenantChefConfiguration;
import io.maestro3.chef.model.client.ChefClientDownloadInfo;
import io.maestro3.chef.model.client.ChefClientKey;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class ChefConfigurationService implements IChefConfigurationService {

    private static final String VALIDATION_PEM_PLACEHOLDER = "m3.chef.validation.pem.%s";
    private static final String AUTH_PLACEHOLDER = "m3.chef.auth.%s";
    private static final String API_USERNAME_PLACEHOLDER = "m3.chef.api.username.%s";

    private final IChefConfigurationDao chefConfigurationDao;
    private final ISecretsService secretsService;
    private final IChefTenantConfigProvider tenantSettingsService;

    @Autowired
    public ChefConfigurationService(IChefConfigurationDao chefConfigurationDao,
                                    ISecretsService secretsService,
                                    IChefTenantConfigProvider tenantSettingsService) {
        this.chefConfigurationDao = chefConfigurationDao;
        this.secretsService = secretsService;
        this.tenantSettingsService = tenantSettingsService;
    }

    @Override
    public ChefConfiguration getInstanceChefHostConfiguration(String regionName, ChefInstance instance) {
        Assert.notNull(instance, "instance can not be null");

        TenantChefConfiguration configuration = tenantSettingsService.getConfig(instance.getTenantName(), regionName);
        if (configuration != null) {
            return chefConfigurationDao.find(configuration.getCustomChefConfigurationId());
        } else {
            return chefConfigurationDao.findByRegionName(regionName);
        }
    }

    @Override
    public ChefConfiguration findByRegionName(String regionName) {
        Assert.hasText(regionName, "regionName can not be null or empty");

        return chefConfigurationDao.findByRegionName(regionName);
    }

    @Override
    public void setupChefServerConfigurationKey(String chefServerId, String validationPem, String certificate, String chefVersion, String serviceId) {
        Assert.hasText(chefServerId, "chefServerId can not be null or empty");
        Assert.hasText(validationPem, "validationPem can not be null or empty");

        // TODO: 9/25/2019 upload validationPem to SSM
        String serverId = StringUtils.lowerCase(chefServerId.trim());

        String validationPemPlaceholder = String.format(VALIDATION_PEM_PLACEHOLDER, serverId);
        secretsService.saveSecret(validationPemPlaceholder, validationPem);
        ChefConfiguration chefConfiguration = chefConfigurationDao.find(serverId);
        if (chefConfiguration == null) {
            chefConfiguration = new ChefConfiguration(serverId, validationPemPlaceholder, certificate, chefVersion);
            if (StringUtils.isNotBlank(serviceId)) {
                chefConfiguration.setServiceId(serviceId);
            }
            chefConfigurationDao.save(chefConfiguration);
            return;
        }

        if (StringUtils.isNotBlank(chefVersion)) {
            chefConfiguration.setChefVersion(chefVersion);
        }
        if (StringUtils.isNotBlank(serviceId)) {
            chefConfiguration.setServiceId(serviceId);
        }

        chefConfiguration.setValidationPemPlaceholder(validationPemPlaceholder);
        chefConfiguration.setCertificatePlaceholder(certificate);

        chefConfigurationDao.update(chefConfiguration);
    }

    @Override
    public void setupChefServerConfigurationKey(ChefConfiguration chefConfiguration, String validationPem) {
        Assert.notNull(chefConfiguration, "chefConfiguration can not be null or empty");
        String serverId = chefConfiguration.getServerId();
        String validationPemPlaceholder = String.format(VALIDATION_PEM_PLACEHOLDER, serverId);
        secretsService.saveSecret(validationPemPlaceholder, validationPem);
        chefConfiguration.setValidationPemPlaceholder(validationPemPlaceholder);
        chefConfigurationDao.update(chefConfiguration);
    }

    @Override
    public void setupChefServerAdminUserCredentials(ChefConfiguration chefConfiguration, String auth, String apiUsername) {
        Assert.notNull(chefConfiguration, "chefConfiguration can not be null");
        String serverId = chefConfiguration.getServerId();
        if (StringUtils.isNotBlank(auth)) {
            String authPlaceholder = String.format(AUTH_PLACEHOLDER, serverId);
            secretsService.saveSecret(authPlaceholder, auth);
            chefConfiguration.setAuthenticationPlaceholder(authPlaceholder);
        }
        if (StringUtils.isNotBlank(apiUsername)) {
            String apiUsernamePlaceholder = String.format(API_USERNAME_PLACEHOLDER, serverId);
            chefConfiguration.setApiUsernamePlaceholder(apiUsernamePlaceholder);
            secretsService.saveSecret(apiUsernamePlaceholder, apiUsername);
        }
        chefConfigurationDao.update(chefConfiguration);
    }

    @Override
    public ChefClientDownloadInfo getChefClientDownloadInfo(ChefClientKey clientKey) {
        return null;
    }

    @Override
    public ChefConfiguration findProjectChef(String tenantInRegionId) {
        Assert.hasText(tenantInRegionId, "tenantInRegionId cannot be null or empty.");
        return chefConfigurationDao.findByTenantInRegionId(tenantInRegionId);
    }

    @Override
    public ChefConfiguration findByServerId(String serverId) {
        Assert.hasText(serverId, "serverId cannot be null or empty.");
        return chefConfigurationDao.find(serverId);
    }

    @Override
    public void update(ChefConfiguration chefConfiguration) {
        Assert.notNull(chefConfiguration, "chefConfiguration can't be null.");

        chefConfigurationDao.update(chefConfiguration);
    }

    @Override
    public void save(ChefConfiguration chefConfiguration) {
        Assert.notNull(chefConfiguration, "chefConfiguration can't be null.");
        chefConfigurationDao.save(chefConfiguration);
    }
}
