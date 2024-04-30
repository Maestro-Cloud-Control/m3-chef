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

public class TenantChefConfiguration {

    private String chefMode;
    private String customChefConfigurationId;
    private AutoConfigurationDisableType autoConfigurationDisableType = AutoConfigurationDisableType.ALL;

    public TenantChefConfiguration() {
        //json
    }

    public TenantChefConfiguration(String chefMode, String customChefConfigurationId) {
        this.chefMode = chefMode;
        this.customChefConfigurationId = customChefConfigurationId;
    }

    public String getChefMode() {
        return chefMode;
    }

    public void setChefMode(String chefMode) {
        this.chefMode = chefMode;
    }

    public String getCustomChefConfigurationId() {
        return customChefConfigurationId;
    }

    public void setCustomChefConfigurationId(String customChefConfigurationId) {
        this.customChefConfigurationId = customChefConfigurationId;
    }

    public AutoConfigurationDisableType getAutoConfigurationDisableType() {
        return autoConfigurationDisableType;
    }

    public void setAutoConfigurationDisableType(AutoConfigurationDisableType autoConfigurationDisableType) {
        this.autoConfigurationDisableType = autoConfigurationDisableType;
    }
}
