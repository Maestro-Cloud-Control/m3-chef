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

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public enum ChefVersion {
    CHEF_VERSION_11("11"),
    CHEF_VERSION_12("12"),
    CHEF_VERSION_13("13");

    private static final Collection<ChefVersion> SUPPORT_ORGANIZATION = Collections.unmodifiableList(
        Arrays.asList(CHEF_VERSION_12, CHEF_VERSION_13));

    private String versionParam;

    public String getVersionParam() {
        return versionParam;
    }

    ChefVersion(String versionParam) {
        this.versionParam = versionParam;
    }

    public static ChefVersion forVersion(String version) {
        if (StringUtils.isBlank(version)) {
            return null;
        }
        for (ChefVersion chefVersion : values()) {
            if (chefVersion.getVersionParam().equalsIgnoreCase(version)) {
                return chefVersion;
            }
        }
        return null;
    }

    public static List<String> getAvailableVersions(ChefMode chefMode) {
        if (ChefMode.EPC.equals(chefMode)) {
            return Collections.singletonList(CHEF_VERSION_13.getVersionParam());
        }
        List<String> availableVersions = new ArrayList<>();
        for (ChefVersion chefVersion : values()) {
            availableVersions.add(chefVersion.getVersionParam());
        }

        return availableVersions;
    }

    public static boolean supportOrganization(ChefVersion version) {
        if (version == null) {
            return false;
        }

        return SUPPORT_ORGANIZATION.contains(version);
    }

    public static ChefVersion getDefaultVersion() {
        return CHEF_VERSION_13;
    }
}
