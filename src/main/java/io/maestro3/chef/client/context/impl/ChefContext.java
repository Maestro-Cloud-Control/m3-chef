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

package io.maestro3.chef.client.context.impl;

import io.maestro3.chef.client.context.ChefContextType;
import io.maestro3.chef.client.context.IChefContext;
import io.maestro3.chef.model.ChefRoleInfo;
import io.maestro3.chef.model.ChefVersion;
import io.maestro3.sdk.internal.util.Assert;
import org.apache.commons.lang3.StringUtils;

import java.security.Key;
import java.util.List;
import java.util.Map;

public class ChefContext implements IChefContext {
    private static final String ORGANIZATION_PREFIX_PATTERN = "organizations/%s/";

    private String host;
    private Key authenticationKey;
    private String username;
    private String chefVersion;
    private String serverId;
    private String chefOrganization;
    private ChefContextType chefContextType;
    private List<ChefRoleInfo> roles;
    private List<String> regions;
    private Map<String, String> dataBagMapping;

    @Override
    public List<String> getRegions() {
        return regions;
    }

    @Override
    public String getUri() {
        return host;
    }

    @Override
    public Key getAuthenticationKey() {
        return authenticationKey;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getChefVersion() {
        return chefVersion;
    }

    @Override
    public String getServerId() {
        return serverId;
    }

    public ChefContextType getChefContextType() {
        return chefContextType;
    }

    @Override
    public String getOrganizationResourcePrefix() {
        if (ChefVersion.forVersion(chefVersion) == ChefVersion.CHEF_VERSION_11) {
            return StringUtils.EMPTY;
        }

        return String.format(ORGANIZATION_PREFIX_PATTERN, chefOrganization);
    }

    @Override
    public String getChefOrganization() {
        return chefOrganization;
    }

    public ChefContext setRegions(List<String> regions) {
        this.regions = regions;
        return this;
    }

    public ChefContext setHost(String host) {
        this.host = host;
        return this;
    }

    public ChefContext setChefContextType(ChefContextType chefContextType) {
        Assert.notNull(chefContextType, "chefContextType cannot be null.");

        this.chefContextType = chefContextType;
        return this;
    }

    public ChefContext setUsername(String username) {
        this.username = username;
        return this;
    }

    public ChefContext setChefVersion(String chefVersion) {
        this.chefVersion = chefVersion;
        return this;
    }

    public ChefContext setServerId(String serverId) {
        this.serverId = serverId;
        return this;
    }

    public ChefContext setAuthenticationKey(Key authenticationKey) {
        this.authenticationKey = authenticationKey;
        return this;
    }

    public ChefContext setChefOrganization(String chefOrganization) {
        this.chefOrganization = chefOrganization;
        return this;
    }

    public List<ChefRoleInfo> getRoles() {
        return roles;
    }

    public ChefContext setRoles(List<ChefRoleInfo> roles) {
        this.roles = roles;
        return this;
    }

    public Map<String, String> getDataBagMapping() {
        return dataBagMapping;
    }

    public ChefContext setDataBagMapping(Map<String, String> dataBagMapping) {
        this.dataBagMapping = dataBagMapping;
        return this;
    }

    @Override
    public String toString() {
        return "ChefContext{" +
                "host='" + host + '\'' +
                '}';
    }
}
