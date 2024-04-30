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

package io.maestro3.chef.model.admin;

public class ChefAddServerParameters {

    private String chefServerId;
    private String validationPem;
    private String certificate;
    private String auth;
    private String apiUsername;
    private Integer threshold;
    private String version;
    private String organization;
    private String chefOrchUsername;
    private String chefOrchPassword;

    public String getChefServerId() {
        return chefServerId;
    }

    public void setChefServerId(String chefServerId) {
        this.chefServerId = chefServerId;
    }

    public String getValidationPem() {
        return validationPem;
    }

    public void setValidationPem(String validationPem) {
        this.validationPem = validationPem;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getApiUsername() {
        return apiUsername;
    }

    public void setApiUsername(String apiUsername) {
        this.apiUsername = apiUsername;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getChefOrchUsername() {
        return chefOrchUsername;
    }

    public void setChefOrchUsername(String chefOrchUsername) {
        this.chefOrchUsername = chefOrchUsername;
    }

    public String getChefOrchPassword() {
        return chefOrchPassword;
    }

    public void setChefOrchPassword(String chefOrchPassword) {
        this.chefOrchPassword = chefOrchPassword;
    }
}
