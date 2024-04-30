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

package io.maestro3.chef.model.azure;

public class AzureAutoConfig {

    private final String chefCertificateUrl;
    private final String chefServer;
    private final String nodeName;
    private final String chefOrganization;
    private final boolean projectChef;

    public AzureAutoConfig(String chefCertificateUrl, String chefServer, String nodeName, String chefOrganization, boolean projectChef) {
        this.chefCertificateUrl = chefCertificateUrl;
        this.chefServer = chefServer;
        this.nodeName = nodeName;
        this.chefOrganization = chefOrganization;
        this.projectChef = projectChef;
    }

    public String getChefCertificateUrl() {
        return chefCertificateUrl;
    }

    public String getChefServer() {
        return chefServer;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getChefOrganization() {
        return chefOrganization;
    }

    public boolean isProjectChef() {
        return projectChef;
    }
}
