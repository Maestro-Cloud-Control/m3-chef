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

import io.maestro3.chef.model.BasicResponse;
import io.maestro3.chef.model.ChefConfiguration;
import io.maestro3.chef.model.ChefInstance;
import io.maestro3.chef.service.IChefConfigurationService;
import io.maestro3.chef.service.IChefInstanceService;
import io.maestro3.chef.service.IChefSecretProvider;
import io.maestro3.chef.service.IRegionProvider;
import io.maestro3.chef.service.ISecretsService;
import io.maestro3.sdk.internal.util.Assert;
import io.maestro3.sdk.internal.util.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ChefCertificatesBundleProvider {

    private static final Logger LOG = LoggerFactory.getLogger(ChefCertificatesBundleProvider.class);

    private final ISecretsService secretsService;
    private final IChefInstanceService chefInstanceService;
    private final IRegionProvider regionService;
    private final IChefConfigurationService chefConfigurationService;
    private final IChefSecretProvider chefSecretProvider;

    @Value("${chef.configuration.receive.attempts:3}")
    private int chefConfigurationReceiveAttempts;

    @Autowired
    public ChefCertificatesBundleProvider(ISecretsService secretsService,
                                          IChefInstanceService chefInstanceService,
                                          IChefSecretProvider chefSecretProvider,
                                          IRegionProvider regionService,
                                          IChefConfigurationService chefConfigurationService) {
        this.secretsService = secretsService;
        this.chefSecretProvider = chefSecretProvider;
        this.chefInstanceService = chefInstanceService;
        this.regionService = regionService;
        this.chefConfigurationService = chefConfigurationService;
    }

    public byte[] provideFile(Map<String, String> parameters) {
        String result;
        String instanceId = parameters.get("instanceId");
        BasicResponse<ChefConfiguration> chefConfigurationResponse = getChefConfigurationForInstance(instanceId);

        ChefConfiguration chefConfiguration = chefConfigurationResponse.getResult();
        if (chefConfiguration != null) {
            String builder = secretsService.getSecretValue(StringEscapeUtils.unescapeJava(chefConfiguration.getValidationPemPlaceholder())) +
                    ";" +
                    chefSecretProvider.get(instanceId);
            result = StringEscapeUtils.unescapeJava(builder);
        } else {
            LOG.warn("Could not get chef configuration for instance {}. Reason: {}", instanceId, chefConfigurationResponse.getMessage());
            throw new RuntimeException("");
        }
        return result.getBytes();
    }

    private BasicResponse<ChefConfiguration> getChefConfigurationForInstance(String instanceId) {
        Assert.hasText(instanceId, "instanceId can't be null or empty.");

        ChefInstance instance = chefInstanceService.getInstance(instanceId);
        if (instance == null) {
            return new BasicResponse<>(HttpStatus.SC_NOT_FOUND, "Instance was not registered");
        }


        String zoneName = regionService.findById(instance.getRegionId());
        if (zoneName == null) {
            LOG.error("Zone not found for zone ID: {}", instance.getRegionId());
            return new BasicResponse<>(HttpStatus.SC_NOT_FOUND,
                    "Zone with ID=" + instance.getRegionId() + " does not exist");
        }

        if (instance.isConfigurationReceived() && instance.getConfigurationReceiveAttempts() >= chefConfigurationReceiveAttempts) {
            return new BasicResponse<>(HttpStatus.SC_CONFLICT,
                    "Configuration already received by instance " + instance.getInstanceId());
        }

        ChefConfiguration configuration = chefConfigurationService.getInstanceChefHostConfiguration(zoneName, instance);
        if (configuration == null || StringUtils.isBlank(secretsService.getSecretValue(StringEscapeUtils.unescapeJava(configuration.getValidationPemPlaceholder())))) {
            String message = "Failed to get chef configuration for instance " + instanceId;
            LOG.error(message);
            return new BasicResponse<>(HttpStatus.SC_NOT_FOUND, message);
        }

        chefInstanceService.setConfigurationReceived(instance);
        return new BasicResponse<>(configuration);
    }
}
