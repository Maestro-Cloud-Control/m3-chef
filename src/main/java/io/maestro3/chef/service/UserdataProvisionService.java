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

import io.maestro3.cadf.util.Assert;
import io.maestro3.chef.model.ChefInstance;
import io.maestro3.chef.model.ChefRole;
import io.maestro3.chef.model.InstanceProperty;
import io.maestro3.chef.model.UserData;
import io.maestro3.chef.provider.IChefRoleUserdataProvider;
import io.maestro3.chef.util.PropertyUtils;
import io.maestro3.sdk.internal.util.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserdataProvisionService implements IUserdataProvisionService {

    private static final Logger LOG = LoggerFactory.getLogger(UserdataProvisionService.class);

    private Map<String, IChefRoleUserdataProvider> chefRoleUserdataProviders = new HashMap<>();

    @Autowired
    public UserdataProvisionService(Optional<List<IChefRoleUserdataProvider>> userdataProviders) {
        if (userdataProviders.isPresent() && CollectionUtils.isNotEmpty(userdataProviders.get())) {
            userdataProviders.get().forEach(dataProvider -> chefRoleUserdataProviders.put(dataProvider.getChefRole(), dataProvider));
        }
        LOG.info("Chef role userdata providers count: {}", chefRoleUserdataProviders.size());
    }

    @Override
    public Map<String, String> provideUserdataProperties(String tenant, String region, ChefInstance instance) {
        Assert.notNull(instance, "instance cannot be null.");
        Assert.notNull(tenant, "tenant cannot be null.");
        Assert.notNull(region, "zone cannot be null.");

        Map<String, String> userdataProperties = null;
        try {
            userdataProperties = provideForChefRole(instance);
        } catch (Exception e) {
            LOG.error("Error while providing userdata for instance " + instance.getInstanceId() + ": " + e.getMessage(), e);
        }
        return userdataProperties == null ? Collections.emptyMap() : userdataProperties;
    }

    private Map<String, String> provideForChefRole(ChefInstance instance) {
        Map<String, String> userdata = new HashMap<>();

        InstanceProperty property = PropertyUtils.getProperty(instance, UserData.EP_CHEFROLE);
        if (property == null) {
            return null;
        }

        String chefRolesAsString = property.getValue();
        String[] chefRolesArray = StringUtils.split(chefRolesAsString, ",");
        if (chefRolesArray != null && chefRolesArray.length > 0) {
            for (String chefRoleString : chefRolesArray) {
                ChefRole chefRole = ChefRole.fromName(chefRoleString);
                if (chefRole == null) {
                    continue;
                }

                IChefRoleUserdataProvider provider = chefRoleUserdataProviders.get(chefRole);
                if (provider == null) {
                    continue;
                }

                Map<String, String> chefRoleCustomUserdata = provider.provide(instance);
                if (MapUtils.isNotEmpty(chefRoleCustomUserdata)) {
                    userdata.putAll(chefRoleCustomUserdata);
                }
            }
        }
        return userdata;
    }

}
