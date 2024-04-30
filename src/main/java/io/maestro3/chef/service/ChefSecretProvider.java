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

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChefSecretProvider implements IChefSecretProvider {

    private final ISecretsService secretsService;

    @Autowired
    protected ChefSecretProvider(ISecretsService secretsService) {
        this.secretsService = secretsService;
    }

    @Override
    public String get(String instanceSearchId) {
        String secretKey = getPath(instanceSearchId);
        if (secretsService.exists(secretKey)) {
            return secretsService.getSecretValue(secretKey);
        }
        String secret = RandomStringUtils.randomAlphanumeric(128);
        create(instanceSearchId, secret);
        return secret;
    }

    @Override
    public void create(String instanceSearchId, String credentials) {
        secretsService.saveSecret(getPath(instanceSearchId), credentials);
    }

    private String getPath(String instanceSearchId) {
        return "acs." + instanceSearchId;
    }

    @Override
    public void delete(String instanceSearchId) {
        secretsService.deleteSecret(getPath(instanceSearchId));
    }

}
