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

import io.maestro3.sdk.internal.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p><b>Author:</b> Dmytro_Kurkin </p>
 * <p><b>Date:</b> 26.12.12 </p>
 */
public class UserData {

    public static final String USERNAME_PARAM = "ep_admin";
    public static final String USERKEY_PARAM = "ep_adminkey";
    public static final String PROJECT_PARAM = "project_id";
    public static final String REGION_PARAM = "region";
    public static final String PROXYSERVER_PARAM = "ep_proxyserver";
    public static final String CHEFSERVER_PARAM = "ep_chefserver";
    public static final String CONFIGURL_PARAM = "ep_orch_ip";
    public static final String CHEFPASSWORD_PARAM = "ep_config_pwd";
    public static final String CHEFUSERNAME_PARAM = "ep_config_usr";
    public static final String CHEF_PEM_FILE_NAME_PARAM = "ep_chefpemfilename";
    public static final String CUSTOM_CHEFSERVER_PARAM = "ep_customchef";
    public static final String CHEFSERVER_VALIDATION_KEY_PARAM = "ep_chefvalidationkey";
    public static final String CUSTOM_SCRIPTS_PARAM = "ep_scripts";
    public static final String OPERATIONAL_INSTANCE_ID = "ep_uuid";
    public static final String EP_CHEFROLE = "role";
    public static final String EP_ADMIN_PASSWORD_PLACEHOLDER = "ep_adminpw";
    public static final String EP_LEVEL15_KEY = "ep_level15key";
    public static final String EP_OWNER_NAME = "owner_name";
    public static final String INSTANCE_DESCRIPTION = "description";

    public static final String INSTANCE_FQDN = "fqdn";
    public static final String INSTANCE_PUBLIC_IP = "ep_public_ip";

    public static final String EO_NODE_NAME = "ep_eo_name";
    public static final String REGION_TYPE = "region_type";
    public static final String CHEF_CALLBACK_URL = "orch_api";
    public static final String STORAGE_URL = "storage_url";
    public static final String ROLE = "role";
    public static final String LAST_RUN = "last_run";

    private List<InstanceProperty> instanceProperties;
    private Map<String, String> orchestratorProperties = new HashMap<>();

    public List<InstanceProperty> getInstanceProperties() {
        return instanceProperties;
    }

    public void setInstanceProperties(List<InstanceProperty> instanceProperties) {
        this.instanceProperties = instanceProperties;
    }

    public Map<String, String> getOrchestratorProperties() {
        return orchestratorProperties;
    }

    public void setOrchestratorProperties(Map<String, String> orchestratorProperties) {
        this.orchestratorProperties = orchestratorProperties;
    }

    public void addOrchestratorProperty(String propertyName, String propertyValue) {
        this.orchestratorProperties.put(propertyName, propertyValue);
    }

    public boolean containsOrchestratorProperty(String propertyName) {
        return this.orchestratorProperties.containsKey(propertyName);
    }

    public String toKeyValuePairs() {
        StringBuilder sb = new StringBuilder(512);
        addFromMap(sb, orchestratorProperties);
        addFromPropertyList(sb, instanceProperties);
        return sb.toString();
    }

    private void addFromMap(StringBuilder sb, Map<String, String> map) {
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (StringUtils.isNotEmpty(entry.getKey()) && StringUtils.isNotEmpty(entry.getValue())) {
                    sb.append(entry.getKey());
                    sb.append('=');
                    sb.append(entry.getValue());
                    sb.append(';');
                }
            }
        }
    }

    private void addFromPropertyList(StringBuilder sb, Collection<InstanceProperty> properties) {
        if (CollectionUtils.isNotEmpty(properties)) {
            for (InstanceProperty oneProperty : properties) {
                if (StringUtils.isNotEmpty(oneProperty.getKey()) && StringUtils.isNotEmpty(oneProperty.getValue())) {
                    sb.append(oneProperty.getKey());
                    sb.append('=');
                    sb.append(oneProperty.getValue());
                    sb.append(';');
                }
            }
        }
    }
}
