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

package io.maestro3.chef.builder;

import javax.security.auth.x500.X500Principal;
import java.util.LinkedHashMap;
import java.util.Map;

public class X500PrincipalBuilder {

    private static final String COMMON_NAME = "CN";
    private static final String COUNTRY_NAME = "C";
    private static final String STATE_NAME = "ST";
    private static final String LOCALITY_NAME = "L";
    private static final String ORGANIZATION_NAME = "O";
    private static final String ORGANIZATIONAL_UNIT_NAME = "OU";

    private Map<String, String> entries = new LinkedHashMap<>();

    private X500PrincipalBuilder() {
    }

    public static X500PrincipalBuilder start() {
        return new X500PrincipalBuilder();
    }

    public X500PrincipalBuilder commonName(String commonName) {
        this.addItem(COMMON_NAME, commonName);
        return this;
    }

    public X500PrincipalBuilder country(String countryName) {
        this.addItem(COUNTRY_NAME, countryName);
        return this;
    }

    public X500PrincipalBuilder state(String stateName) {
        this.addItem(STATE_NAME, stateName);
        return this;
    }

    public X500PrincipalBuilder locality(String localityName) {
        this.addItem(LOCALITY_NAME, localityName);
        return this;
    }

    public X500PrincipalBuilder organization(String organizationName) {
        this.addItem(ORGANIZATION_NAME, organizationName);
        return this;
    }

    public X500PrincipalBuilder organizationalUnit(String organizationalUnitName) {
        this.addItem(ORGANIZATIONAL_UNIT_NAME, organizationalUnitName);
        return this;
    }

    public X500Principal build() {
        return new X500Principal(mapToString(entries));
    }

    private void addItem(String key, String value) {
        if (value != null && value.trim().length() != 0) {
            entries.put(key, value);
        }
    }

    private String mapToString(Map<String, String> map) {
        StringBuilder builder = new StringBuilder();
        String delimiter = "";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.append(delimiter);
            builder.append(entry.getKey()).append("=").append(entry.getValue());
            delimiter = ",";
        }
        return builder.toString();
    }
}
