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

package io.maestro3.chef.client.context;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.maestro3.chef.client.context.impl.ChefContext;
import io.maestro3.chef.model.ChefConfiguration;
import io.maestro3.chef.model.ChefRoleInfo;
import io.maestro3.chef.model.TenantChefConfiguration;
import io.maestro3.chef.service.IChefConfigurationService;
import io.maestro3.chef.service.IChefTenantConfigProvider;
import io.maestro3.chef.service.IFileService;
import io.maestro3.chef.service.ISecretsService;
import io.maestro3.sdk.internal.util.Assert;
import io.maestro3.sdk.internal.util.StringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ChefContextFactory implements IChefContextFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ChefContextFactory.class);

    private static final Pattern fileLinkPattern = Pattern.compile("^/files/([a-z0-9-]*)/.*pem$");

    private static final String INSECURE_PROTOCOL = "http://";
    private static final String SECURE_PROTOCOL = "https://";
    private static final String SEPARATOR = "/";
    private static final String PROJECT_KEY_PREFIX = "project_";

    private final Cache<String, IChefContext> cache = CacheBuilder.newBuilder()
        .expireAfterAccess(2, TimeUnit.HOURS)
        .build();

    @Autowired
    private IFileService fileService;
    @Autowired
    private IChefConfigurationService chefConfigurationService;
    @Autowired
    private ISecretsService secretsService;
    @Autowired
    private IChefTenantConfigProvider tenantSettingsService;

    // default values
    private boolean secure = true;

    @Override
    public IChefContext getInstance(ChefConfiguration configuration) {
        Assert.notNull(configuration, "Chef configuration can't be null");

        String cacheKey = configuration.getServerId();
        IChefContext context = cache.getIfPresent(cacheKey);
        if (context != null) {
            cache.put(context.getServerId(), context);
            return context;
        }

        String apiUsername = StringEscapeUtils.unescapeJava(secretsService.getSecretValue(configuration.getApiUsernamePlaceholder()));
        String authentication = StringEscapeUtils.unescapeJava(secretsService.getSecretValue(configuration.getAuthenticationPlaceholder()));
        context = getChefContext(ChefContextType.ZONE, configuration.getServerId(), apiUsername,
            authentication, configuration.getChefVersion(), configuration.getChefOrganization(),
            configuration.getRoles(), configuration.getZones(), configuration.getCustomDataBagMapping());
        if (context != null) {
            cache.put(cacheKey, context);
            cache.put(context.getServerId(), context);
        }
        return context;
    }

    @Override
    public IChefContext getProjectInstance(String tenant, String region) {
        Assert.notNull(region, "region must not be null");
        Assert.notNull(tenant, "tenant must not be null");

        TenantChefConfiguration configuration = tenantSettingsService.getConfig(tenant, region);

        if (configuration == null || StringUtils.isBlank(configuration.getCustomChefConfigurationId())
                || StringUtils.isBlank(configuration.getChefMode())) {
            LOG.warn("Requested chef context for project with blank custom chef host or blank chef mode: {}, region {}",
                    tenant, region);
            return null;
        }
        if (!"EPC".equalsIgnoreCase(configuration.getChefMode())) {
            LOG.warn("Can not create Chef server context for the {} Chef mode.", configuration.getChefMode());
            return null;
        }
        // create EPC chef server context

        ChefConfiguration chefConfiguration = chefConfigurationService.findByServerId(configuration.getCustomChefConfigurationId());

        String host = chefConfiguration.getServerId();
        String cacheKey = PROJECT_KEY_PREFIX + tenant + region + host;
        IChefContext context = cache.getIfPresent(cacheKey);
        if (context != null) {
            cache.put(context.getServerId(), context);
            return context;
        }


        String apiUsername = "admin";

        String apiKeyUrl = chefConfiguration.getCustomChefApiKeyUrl();
        if (StringUtils.isBlank(apiKeyUrl)) {
            LOG.error("EPC Chef server has no API key URL for project {}", tenant);
            return null;
        }

        //read API key data
        String apiKeyString = getApiKeyData(apiKeyUrl);
        if (StringUtils.isBlank(apiKeyString)) {
            LOG.error("Blank API key string provided");
            return null;
        }

        context = getChefContext(ChefContextType.PROJECT, host, apiUsername, apiKeyString,
            chefConfiguration.getChefVersion(), chefConfiguration.getChefOrganization(),
            chefConfiguration.getRoles(), chefConfiguration.getZones(), chefConfiguration.getCustomDataBagMapping());
        if (context != null) {
            cache.put(cacheKey, context);
            cache.put(context.getServerId(), context);
        }
        return context;
    }

    @Override
    public IChefContext getInstance(String tenant, String region) {
        Assert.notNull(region, "region must not be null");
        Assert.notNull(tenant, "tenant must not be null");

        IChefContext context = getProjectInstance(tenant, region);
        if (context != null) {
            return context;
        }
        ChefConfiguration chefConfiguration = chefConfigurationService.findByRegionName(region);
        if (chefConfiguration == null) {
            throw new RuntimeException("Chef configuration is not found for region " + region);
        }
        return getInstance(chefConfiguration);
    }

    @Override
    public IChefContext getInstance(String serverId) {
        return cache.getIfPresent(serverId);
    }

    @Override
    public String convertHostToUri(String host) {
        if (StringUtils.isBlank(host)) {
            return null;
        }
        if (secure) {
            return SECURE_PROTOCOL + host.toLowerCase() + SEPARATOR;
        }
        return INSECURE_PROTOCOL + host.toLowerCase() + SEPARATOR;
    }

    public boolean getSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    private IChefContext getChefContext(ChefContextType chefContextType, String host, String apiUsername, String authentication,
                                        String chefVersion, String chefOrganization, List<ChefRoleInfo> chefRoles, List<String> regions, Map<String, String> customDataBagMapping) {
        if (StringUtils.isBlank(host) || StringUtils.isBlank(apiUsername) || StringUtils.isBlank(authentication)) {
            return null;
        }

        Key key = SecurityHelper.loadPrivateKey(authentication);
        if (key == null) {
            throw new IllegalArgumentException("Given zone orchestration settings contain invalid authentication key");
        }

        return new ChefContext()
            .setChefContextType(chefContextType)
            .setAuthenticationKey(key)
            .setRegions(regions)
            .setHost(convertHostToUri(host))
            .setUsername(apiUsername)
            .setDataBagMapping(customDataBagMapping)
            .setChefVersion(chefVersion)
            .setServerId(host)
            .setRoles(chefRoles)
            .setChefOrganization(chefOrganization);
    }

    private String getApiKeyData(String apiKeyUrl) {
        String result = null;
        //  /files/{token}/*"
        Matcher matcher = fileLinkPattern.matcher(apiKeyUrl);
        if (matcher.matches()) {
            String token = matcher.group(1);
            Object fileInfo = fileService.getFile(token);
            if (fileInfo == null) {
                LOG.error("Unable to get file by token. token: {}", token);
                return null;
            }
            try {
                InputStream is = fileService.getStream(fileInfo);
                try {
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(is, writer, StandardCharsets.UTF_8);
                    result = writer.toString();
                } finally {
                    IOUtils.closeQuietly(is);
                }
            } catch (Throwable e) {
                LOG.error("Unexpected error. token: " + token, e);
                throw new RuntimeException("File not found.");
            }
        }
        return result;
    }

    @Override
    public void invalidateCache() {
        cache.invalidateAll();
    }
}
