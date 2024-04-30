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

package io.maestro3.chef.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = ChefConfiguration.TABLE_NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChefConfiguration {

    private static final String DEFAULT_CHEF_ORG_NAME = "default";
    public static final String TABLE_NAME = "ChefConfiguration";

    public static final String FIELD_SERVER_ID = "serverId";
    public static final String FIELD_TENANT_IN_REGION_ID = "tenantInRegionId";

    @Id
    private String id;

    @NotNull
    @Field(FIELD_SERVER_ID)
    private String serverId;

    private String validationPemPlaceholder;

    private String certificatePlaceholder;

    @Field(FIELD_TENANT_IN_REGION_ID)
    private String tenantInRegionId;

    /**
     * Pem file name is the name of the file used to contact chef server first time instance runs to retrieve it's personal pem.
     */
    private String pemFileName;
    /**
     * Api username is used to request data from chef server using <a href="http://docs.opscode.com/api_chef_server.html">Chef Server API</a>.
     */
    private String apiUsernamePlaceholder;
    /**
     * Authentication is the content of the pem file used to access data on chef server using <a href="http://docs.opscode.com/api_chef_server.html">Chef Server API</a>.
     * The file itself can be retrieved from server using SSH connection.
     */
    private String authenticationPlaceholder;

    private double alertThreshold;

    private List<ChefRoleInfo> roles = new ArrayList<>();

    private List<String> zones;

    private String customChefValidationKeyUrl;

    private String customChefApiKeyUrl;

    private String chefVersion;

    private boolean defaultChef;

    // for EPC chef configuration
    private String serviceId;

    /**
     * Orch username and password are used by new instances to retrieve their own pem file from server when they register on server.
     * Required for default chef on CSA region
     */
    private String chefOrchUsernamePlaceholder;
    private String chefOrchPasswordPlaceholder;

    private String chefOrganization;
    private Map<String, String> customDataBagMapping = new HashMap<>();


    public ChefConfiguration() {
    }

    public ChefConfiguration(String serverId, String validationPemPlaceholder, String certificatePlaceholder, String chefVersion) {
        if (StringUtils.isBlank(serverId)) {
            throw new IllegalArgumentException("serverId can not be null or blank");
        }
        this.serverId = StringUtils.lowerCase(serverId);
        this.validationPemPlaceholder = validationPemPlaceholder;
        this.certificatePlaceholder = certificatePlaceholder;
        this.chefVersion = chefVersion;
        this.setDefaultChefOrganization();
    }

    public ChefConfiguration(String serverId, String customChefValidationKeyUrl, String chefVersion) {
        if (StringUtils.isBlank(serverId)) {
            throw new IllegalArgumentException("serverId can not be null or blank");
        }
        this.serverId = StringUtils.lowerCase(serverId);
        this.customChefValidationKeyUrl = customChefValidationKeyUrl;
        this.chefVersion = chefVersion;
        this.setDefaultChefOrganization();

    }

    public String getId() {
        return id;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        if (StringUtils.isBlank(serverId)) {
            throw new IllegalArgumentException("serverId can not be null or blank");
        }
        this.serverId = StringUtils.lowerCase(serverId);
    }

    public String getValidationPemPlaceholder() {
        return validationPemPlaceholder;
    }

    public void setValidationPemPlaceholder(String validationPemPlaceholder) {
        this.validationPemPlaceholder = validationPemPlaceholder;
    }

    public String getCertificatePlaceholder() {
        return certificatePlaceholder;
    }

    public void setCertificatePlaceholder(String certificatePlaceholder) {
        this.certificatePlaceholder = certificatePlaceholder;
    }

    public String getPemFileName() {
        return pemFileName;
    }

    public void setPemFileName(String pemFileName) {
        this.pemFileName = pemFileName;
    }

    public String getApiUsernamePlaceholder() {
        return apiUsernamePlaceholder;
    }

    public void setApiUsernamePlaceholder(String apiUsernamePlaceholder) {
        this.apiUsernamePlaceholder = apiUsernamePlaceholder;
    }

    public String getAuthenticationPlaceholder() {
        return authenticationPlaceholder;
    }

    public void setAuthenticationPlaceholder(String authenticationPlaceholder) {
        this.authenticationPlaceholder = authenticationPlaceholder;
    }

    public double getAlertThreshold() {
        return alertThreshold;
    }

    public void setAlertThreshold(double alertThreshold) {
        this.alertThreshold = alertThreshold;
    }

    public List<ChefRoleInfo> getRoles() {
        return roles;
    }

    public void setRoles(List<ChefRoleInfo> roles) {
        this.roles = roles;
    }

    public List<String> getZones() {
        return zones;
    }

    public void setZones(List<String> zones) {
        this.zones = zones;
    }

    public String getCustomChefValidationKeyUrl() {
        return customChefValidationKeyUrl;
    }

    public void setCustomChefValidationKeyUrl(String customChefValidationKeyUrl) {
        this.customChefValidationKeyUrl = customChefValidationKeyUrl;
    }

    public String getCustomChefApiKeyUrl() {
        return customChefApiKeyUrl;
    }

    public void setCustomChefApiKeyUrl(String customChefApiKeyUrl) {
        this.customChefApiKeyUrl = customChefApiKeyUrl;
    }

    public String getChefVersion() {
        return chefVersion;
    }

    public void setChefVersion(String chefVersion) {
        this.chefVersion = chefVersion;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public boolean isDefaultChef() {
        return defaultChef;
    }

    public void setDefaultChef(boolean defaultChef) {
        this.defaultChef = defaultChef;
    }

    public String getChefOrchUsernamePlaceholder() {
        return chefOrchUsernamePlaceholder;
    }

    public String getChefOrchPasswordPlaceholder() {
        return chefOrchPasswordPlaceholder;
    }

    public void setChefOrchUsernamePlaceholder(String chefOrchUsernamePlaceholder) {
        this.chefOrchUsernamePlaceholder = chefOrchUsernamePlaceholder;
    }

    public void setChefOrchPasswordPlaceholder(String chefOrchPasswordPlaceholder) {
        this.chefOrchPasswordPlaceholder = chefOrchPasswordPlaceholder;
    }

    public String getChefOrganization() {
        return chefOrganization;
    }

    public void setChefOrganization(String chefOrganization) {
        this.chefOrganization = chefOrganization;
    }

    public void setDefaultChefOrganization() {
        this.chefOrganization = DEFAULT_CHEF_ORG_NAME;
    }

    public String getTenantInRegionId() {
        return tenantInRegionId;
    }

    public void setTenantInRegionId(String tenantInRegionId) {
        this.tenantInRegionId = tenantInRegionId;
    }

    public Map<String, String> getCustomDataBagMapping() {
        return customDataBagMapping;
    }

    public void setCustomDataBagMapping(Map<String, String> customDataBagMapping) {
        this.customDataBagMapping = customDataBagMapping;
    }
}
