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

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Document(collection = "ChefInstances")
@CompoundIndex(def = "{'instanceId': 1}")
public class ChefInstance {

    private String instanceId;
    private String tenantName;
    private String regionId;
    private String regionName;
    private String privateIp;
    private String publicIp;
    private String cloud;
    private String fqdn;
    private String chefServer;
    private String chefMode;
    private String additionalData;// TODO: 21.05.2021 may contains sensitive info, need to encrypt better than base64
    private Map<String, String> scriptFiles;
    private OsType osType;
    private AutoConfigurationState autoConfigurationState;
    private Set<String> roles = new HashSet<>();
    private boolean configurationReceived;
    private int configurationReceiveAttempts;
    private boolean sshKeySet;
    private String publicKey;
    private List<InstanceProperty> instanceProperties = new ArrayList<>();
    private boolean autoConfigurationDisabled;
    private String resourceId;
    private long initDate;

    public String getCloud() {
        return cloud;
    }

    public void setCloud(String cloud) {
        this.cloud = cloud;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

    public long getInitDate() {
        return initDate;
    }

    public void setInitDate(long initDate) {
        this.initDate = initDate;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getPrivateIp() {
        return privateIp;
    }

    public void setPrivateIp(String privateIp) {
        this.privateIp = privateIp;
    }

    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getFqdn() {
        return fqdn;
    }

    public void setFqdn(String fqdn) {
        this.fqdn = fqdn;
    }

    public String getChefServer() {
        return chefServer;
    }

    public void setChefServer(String chefServer) {
        this.chefServer = chefServer;
    }

    public String getChefMode() {
        return chefMode;
    }

    public void setChefMode(String chefMode) {
        this.chefMode = chefMode;
    }

    public AutoConfigurationState getAutoConfigurationState() {
        return autoConfigurationState;
    }

    public void setAutoConfigurationState(AutoConfigurationState autoConfigurationState) {
        this.autoConfigurationState = autoConfigurationState;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public boolean isConfigurationReceived() {
        return configurationReceived;
    }

    public void setConfigurationReceived(boolean configurationReceived) {
        this.configurationReceived = configurationReceived;
    }

    public int getConfigurationReceiveAttempts() {
        return configurationReceiveAttempts;
    }

    public void setConfigurationReceiveAttempts(int configurationReceiveAttempts) {
        this.configurationReceiveAttempts = configurationReceiveAttempts;
    }

    public boolean isSshKeySet() {
        return sshKeySet;
    }

    public void setSshKeySet(boolean sshKeySet) {
        this.sshKeySet = sshKeySet;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public List<InstanceProperty> getInstanceProperties() {
        return instanceProperties;
    }

    public void setInstanceProperties(List<InstanceProperty> instanceProperties) {
        this.instanceProperties = instanceProperties;
    }

    public OsType getOsType() {
        return osType;
    }

    public void setOsType(OsType osType) {
        this.osType = osType;
    }

    public boolean isAutoConfigurationDisabled() {
        return autoConfigurationDisabled;
    }

    public void setAutoConfigurationDisabled(boolean autoConfigurationDisabled) {
        this.autoConfigurationDisabled = autoConfigurationDisabled;
    }

    public Map<String, String> getScriptFiles() {
        return scriptFiles;
    }

    public void setScriptFiles(Map<String, String> scriptFiles) {
        this.scriptFiles = scriptFiles;
    }

    @Override
    public String toString() {
        return "ChefInstance{" +
                "instanceId='" + instanceId + '\'' +
                ", tenantName='" + tenantName + '\'' +
                ", regionId='" + regionId + '\'' +
                ", regionName='" + regionName + '\'' +
                ", privateIp='" + privateIp + '\'' +
                ", publicIp='" + publicIp + '\'' +
                ", cloud=" + cloud +
                ", fqdn='" + fqdn + '\'' +
                ", chefServer='" + chefServer + '\'' +
                ", chefMode='" + chefMode + '\'' +
                ", additionalData='" + additionalData + '\'' +
                ", scriptFiles=" + scriptFiles +
                ", osType=" + osType +
                ", autoConfigurationState=" + autoConfigurationState +
                ", roles=" + roles +
                ", configurationReceived=" + configurationReceived +
                ", configurationReceiveAttempts=" + configurationReceiveAttempts +
                ", sshKeySet=" + sshKeySet +
                ", publicKey='" + publicKey + '\'' +
                ", instanceProperties=" + instanceProperties +
                ", autoConfigurationDisabled=" + autoConfigurationDisabled +
                ", resourceId='" + resourceId + '\'' +
                ", initDate=" + initDate +
                '}';
    }
}
