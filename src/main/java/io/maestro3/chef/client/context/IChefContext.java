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

package io.maestro3.chef.client.context;

import io.maestro3.chef.model.ChefRoleInfo;

import java.security.Key;
import java.util.List;
import java.util.Map;

/**
 * Basically represents one-server-info essential to run any command on that server.
 *
 */
public interface IChefContext {

    /**
     * @return uri with protocol and slash at the end, e.g. "https://example.epam.com/"
     */
    String getUri();

    /**
     * @return chef organization name
     */
    String getChefOrganization();

    /**
     * @return private key decoded from "username.pem" file for specified server
     */
    Key getAuthenticationKey();

    /**
     * @return user identifier, e.g. "username"
     */
    String getUsername();

    /**
     * @return chef server version, e.g. "0.10.4"
     */
    String getChefVersion();

    /**
     * @return the id of the server used as cache key
     */
    String getServerId();

    /**
     * return either ZONE or PROJECT type.
     */
    ChefContextType getChefContextType();

    /**
     * Organization resource prefix. Valid started from 12.x Chef version. Otherwise empty.
     */
    String getOrganizationResourcePrefix();

    /**
     * @return the list of available chef role names
     */
    List<ChefRoleInfo> getRoles();

    /**
     * @return the list of available region names
     */
    List<String> getRegions();

    /**
     * @return the map of custom mapping role on databag name
     */
    Map<String, String> getDataBagMapping();

}
