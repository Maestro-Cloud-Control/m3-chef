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

package io.maestro3.chef.provider;

import io.maestro3.chef.model.ChefConfiguration;
import io.maestro3.chef.model.OsType;
import io.maestro3.chef.model.TenantChefConfiguration;
import io.maestro3.chef.model.script.InitScript;
import io.maestro3.chef.service.IChefConfigurationService;
import io.maestro3.chef.service.IChefInfoService;
import io.maestro3.chef.service.IChefTenantConfigProvider;
import io.maestro3.chef.service.IFileAccessService;
import io.maestro3.chef.service.IFileService;
import io.maestro3.sdk.internal.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class InitScriptsProvider implements IInitScriptsProvider {

    private static final Logger LOG = LoggerFactory.getLogger(InitScriptsProvider.class);

    private static final String EP_ORCH_IP = "@VAR_EP_ORCH_IP";
    private static final String PROJECT_CHEF = "@VAR_PROJECT_CHEF";
    private static final String NODENAME = "@VAR_NODENAME";
    private static final String CHEF_CERTS_URL = "@VAR_CHEF_CERTS_URL";
    private static final String CHEF_SERVER = "@VAR_CHEF_SERVER";
    private static final String CHEF_ENV = "@VAR_CHEF_ENV";
    private static final String EP_CHEF_ORGANIZATION = "@VAR_CHEF_ORG_NAME";
    private static final String CONFIG_URL = "@VAR_CONFIG_URL";
    private static final String VIRT_TYPE = "@VAR_VIRT_TYPE";
    private static final String ACS_ENABLE = "@VAR_ACS_ENABLE";
    private static final String USER_SCRIPT = "@VAR_USER_SCRIPT";
    private static final String DOWNLOAD_URL = "@VAR_DOWNLOAD_URL";
    private static final String STORAGE_URL = "@VAR_STORAGE_URL";
    private static final String NOTIF_URL = "@VAR_NOTIF_URL";

    private static final int USER_SCRIPT_EXPIRATION_HOURS = 1;

    private final IChefInfoService autoconfigurationSettingsManager;
    private final IChefConfigurationService chefConfigurationService;
    private final IFileAccessService fileAccessTokenService;
    private final IFileService fileService;
    private final IChefTenantConfigProvider tenantSettingsService;

    @Autowired
    public InitScriptsProvider(IChefInfoService autoconfigurationSettingsManager,
                               IChefConfigurationService chefConfigurationService,
                               IFileAccessService fileAccessTokenService,
                               IFileService fileService,
                               IChefTenantConfigProvider tenantSettingsService) {
        this.autoconfigurationSettingsManager = autoconfigurationSettingsManager;
        this.chefConfigurationService = chefConfigurationService;
        this.fileAccessTokenService = fileAccessTokenService;
        this.fileService = fileService;
        this.tenantSettingsService = tenantSettingsService;
    }

    @Override
    public String replaceInstanceParameters(InitScript script, Map<String, String> instanceParameters, String nodeName) {
        String content = script.getContent();
        if (StringUtils.isNotBlank(nodeName)) {
            content = content.replace(NODENAME, nodeName);
        }
        if (StringUtils.isNotBlank(script.getUserScriptId())) {
            String scriptToken = fileAccessTokenService.prepareUserScriptToken(script.getUserScriptId(), script.getUserScriptName());
            content = content.replace(USER_SCRIPT, scriptToken);
        }
        return content;
    }

    @Override
    public InitScript provideInitScript(String tenant, String region, String cloud, Map<String, String> instanceParams, OsType osType) {
        InitScript initScript;
        try {
            initScript = new InitScript(provideScriptTemplate(cloud, osType));
        } catch (Exception e) {
            LOG.warn("Cannot load init script for {} tenant", tenant);
            return new InitScript("");
        }

        boolean chefEnabled = autoconfigurationSettingsManager.isChefEnabled();
        boolean userScriptsEnabled = autoconfigurationSettingsManager.isUserScriptsEnabled(cloud);
        Map<String, String> scriptParams = new HashMap<>();
        if (chefEnabled || userScriptsEnabled) {
            setupCommonParameters(scriptParams);
            if (chefEnabled) {
                setupAutoconfiguration(scriptParams, instanceParams, tenant, region, cloud);
            }
            if (userScriptsEnabled) {
                String userScriptId = instanceParams.get(IChefInfoService.USER_SCRIPT_ID);
                if (StringUtils.isNotBlank(userScriptId)) {
                    initScript.setUserScriptId(userScriptId);
                    initScript.setUserScriptName(fileService.getFileById(userScriptId));
                }
            }
        }
        return substituteScriptParameters(initScript, scriptParams);
    }

    private void setupCommonParameters(Map<String, String> scriptParams) {
        String configurationUrl = autoconfigurationSettingsManager.getConfigurationUrl();
        String certUrl = autoconfigurationSettingsManager.getCertUrl();
        String notifUrl = autoconfigurationSettingsManager.getChefStateUrl();
        scriptParams.put(CHEF_CERTS_URL, certUrl);
        scriptParams.put(NOTIF_URL, notifUrl);
        scriptParams.put(EP_ORCH_IP, configurationUrl);
        scriptParams.put(CONFIG_URL, configurationUrl);
        scriptParams.put(DOWNLOAD_URL, getDownloadUrl());
        scriptParams.put(STORAGE_URL, autoconfigurationSettingsManager.getInitScriptStorageUrl());
    }

    private String provideScriptTemplate(String cloud, OsType osType) {
        String filename = osType.getInitScriptDefaultName();
        byte[] script = fileService.getFileByPath(autoconfigurationSettingsManager.getAutoconfigurationBucketName(),
                autoconfigurationSettingsManager.getCurrentChefVersion() + "/" + cloud, filename);
        return script == null ? null : new String(script, StandardCharsets.UTF_8);
    }

    private void setupAutoconfiguration(Map<String, String> scriptParams,
                                        Map<String, String> instanceParams,
                                        String tenant, String region, String cloud) {
        String installChefParam = instanceParams.get(IChefInfoService.INSTALL_CHEF_CLIENT);
        if (Boolean.parseBoolean(installChefParam)) {
            prepareAutoconfigurationParameters(scriptParams, tenant, region, cloud);
        }
    }

    private void prepareAutoconfigurationParameters(Map<String, String> scriptParams, String tenant, String region, String cloud) {
        TenantChefConfiguration tenantChefConfiguration = tenantSettingsService.getConfig(tenant, region);
        ChefConfiguration configuration = getChefConfiguration(region, tenantChefConfiguration);
        boolean isProjectChef = tenantChefConfiguration != null && StringUtils.isNotBlank(tenantChefConfiguration.getCustomChefConfigurationId());

        scriptParams.put(PROJECT_CHEF, String.valueOf(isProjectChef));
        scriptParams.put(CHEF_SERVER, configuration.getServerId());
        scriptParams.put(EP_CHEF_ORGANIZATION, configuration.getChefOrganization());
        scriptParams.put(CHEF_ENV, autoconfigurationSettingsManager.getChefEnv());
        scriptParams.put(VIRT_TYPE, cloud);
        scriptParams.put(ACS_ENABLE, "true");
    }

//    private String getConfigurationUrl() {
//        ServerConfig serverConfig = serverConfigProvider.getServerConfig();
//        String prefix = autoconfigurationSettingsManager.getPrefix();
//        prefix = StringUtils.isNotBlank(prefix)
//            ? prefix
//            : "http://";
//        String chefNodeName = autoconfigurationSettingsManager.getChefNodeName();
//        chefNodeName = StringUtils.isNotBlank(chefNodeName)
//            ? chefNodeName
//            : serverConfig.getDnsName();
//        return prefix + chefNodeName + serverConfig.getApiEndpoint();
//    }

    private ChefConfiguration getChefConfiguration(String region, TenantChefConfiguration tenantChefConfiguration) {
        ChefConfiguration configuration;
        if (tenantChefConfiguration == null || StringUtils.isBlank(tenantChefConfiguration.getCustomChefConfigurationId())) {
            configuration = chefConfigurationService.findByRegionName(region);
        } else {
            configuration = chefConfigurationService.findByServerId(tenantChefConfiguration.getCustomChefConfigurationId());
        }
        if (Objects.isNull(configuration)) {
            throw new RuntimeException(String.format("Chef server is not found for region '%s'", region));
        }
        return configuration;
    }

    private String getDownloadUrl() {
        return autoconfigurationSettingsManager.getApiHost() + "/download/";
    }

    private InitScript substituteScriptParameters(InitScript initScript, Map<String, String> scriptParams) {
        for (Map.Entry<String, String> entry : scriptParams.entrySet()) {
            initScript.replaceParameter(entry.getKey(), entry.getValue());
        }
        return initScript;
    }
}
