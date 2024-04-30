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

import java.util.Map;

/**
 * Stands for node registered on chef server. Nodes on orchestrator chef servers represent instances and contain
 * their properties.
 *
 */
public class NodeEntity {
    public static final String CHEF_TYPE = "node";
    public static final String JSON_CLASS = "Chef::Node";

    @SerializedName("name")
    private String name;

    @SerializedName("attributes")
    private Map<String, String> customAttributes;

    @SerializedName("defaults")
    private Map<String, String> defaultAttributes;

    @SerializedName("overrides")
    private Map<String, String> overriddenAttributes;

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

    public Map<String, String> getCustomAttributes() {
        return customAttributes;
    }

    public void setCustomAttributes(Map<String, String> customAttributes) {
        this.customAttributes = customAttributes;
    }

    public Map<String, String> getDefaultAttributes() {
        return defaultAttributes;
    }

    public void setDefaultAttributes(Map<String, String> defaultAttributes) {
        this.defaultAttributes = defaultAttributes;
    }

    public Map<String, String> getOverriddenAttributes() {
        return overriddenAttributes;
    }

    public void setOverriddenAttributes(Map<String, String> overriddenAttributes) {
        this.overriddenAttributes = overriddenAttributes;
    }

    @Override
    public String toString() {
        return ChefUtils.buildString("Node{", "name=", name,
                ", customAttributes=", customAttributes,
                ", defaultAttributes=", defaultAttributes,
                ", overriddenAttributes=", overriddenAttributes,
                '}');
    }
}
