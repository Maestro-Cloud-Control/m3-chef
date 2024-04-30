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

package io.maestro3.chef.client.entity;

import com.google.gson.annotations.SerializedName;
import io.maestro3.chef.client.utils.ChefUtils;

import java.util.List;
import java.util.Map;

/**
 * Stands for node registered on chef server.
 * Nodes on orchestrator chef servers represent instances and contain their properties.
 *
 */
public class GetNodeEntity {

    @SerializedName("name")
    private String name;

    @SerializedName("chef_environment")
    private String chefEnvironment;

    @SerializedName("json_class")
    private String jsonClass;

    @SerializedName("chef_type")
    private String chefType;

    @SerializedName("run_list")
    private List<String> runList;

    @SerializedName("automatic")
    private Map<String, Object> automaticAttributes;

    @SerializedName("normal")
    private Map<String, Object> normalAttributes;

    @SerializedName("default")
    private Map<String, Object> defaultAttributes;

    @SerializedName("override")
    private Map<String, Object> overriddenAttributes;

    @SerializedName("attributes")
    private Map<String, Object> customAttributes;

    public String getName() {
        return name;
    }

    /**
     * Node name must not have spaces in it. Basically it should be uri friendly (no slashes and other nasty stuff, just
     * use alphabet and numbers), since Chef uses <code>nodes/NAME</code> uri to return info about specified node.
     *
     * @param name the name of the node on server
     */
    public void setName(String name) {
        this.name = name;
    }


    public String getChefEnvironment() {
        return chefEnvironment;
    }

    public void setChefEnvironment(String chefEnvironment) {
        this.chefEnvironment = chefEnvironment;
    }

    public String getJsonClass() {
        return jsonClass;
    }

    public void setJsonClass(String jsonClass) {
        this.jsonClass = jsonClass;
    }

    public String getChefType() {
        return chefType;
    }

    public void setChefType(String chefType) {
        this.chefType = chefType;
    }

    public List<String> getRunList() {
        return runList;
    }

    public void setRunList(List<String> runList) {
        this.runList = runList;
    }

    public Map<String, Object> getAutomaticAttributes() {
        return automaticAttributes;
    }

    public void setAutomaticAttributes(Map<String, Object> automaticAttributes) {
        this.automaticAttributes = automaticAttributes;
    }

    public Map<String, Object> getNormalAttributes() {
        return normalAttributes;
    }

    public void setNormalAttributes(Map<String, Object> normalAttributes) {
        this.normalAttributes = normalAttributes;
    }

    public Map<String, Object> getDefaultAttributes() {
        return defaultAttributes;
    }

    public void setDefaultAttributes(Map<String, Object> defaultAttributes) {
        this.defaultAttributes = defaultAttributes;
    }

    public Map<String, Object> getOverriddenAttributes() {
        return overriddenAttributes;
    }

    public void setOverriddenAttributes(Map<String, Object> overriddenAttributes) {
        this.overriddenAttributes = overriddenAttributes;
    }

    public Map<String, Object> getCustomAttributes() {
        return customAttributes;
    }

    public void setCustomAttributes(Map<String, Object> customAttributes) {
        this.customAttributes = customAttributes;
    }

    @Override
    public String toString() {
        return ChefUtils.buildString("Node{", "name=", name,
                ", chefEnvironment=", chefEnvironment,
                ", jsonClass=", jsonClass,
                ", chefType=", chefType,
                ", runList=", runList,
                ", automaticAttributes=", automaticAttributes,
                ", normalAttributes=", normalAttributes,
                ", defaultAttributes=", defaultAttributes,
                ", overriddenAttributes=", overriddenAttributes,
                ", customAttributes=", customAttributes,
                '}');
    }

}
