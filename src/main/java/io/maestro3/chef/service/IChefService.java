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

package io.maestro3.chef.service;

import io.maestro3.chef.client.command.search.NodeDescriptor;
import io.maestro3.chef.client.command.search.attributes.IGetNodesAttributesResult;
import io.maestro3.chef.client.exception.ChefClientException;
import io.maestro3.chef.exception.ChefServerUnexpectedErrorException;
import io.maestro3.chef.model.ChefConfiguration;
import io.maestro3.chef.model.ChefStrategy;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IChefService {

//    GetNodeEntity getNodeDetails(IZone zone, ITenantInRegion project, String instanceId) throws ChefClientException;

    List<NodeDescriptor> getDefaultChefNodeAttributes(String tenant, String region) throws ChefClientException;

    boolean deleteNode(String tenant, String region, String instanceId, ChefStrategy strategy);

    <R extends IGetNodesAttributesResult, T> List<T> getCustomNodeAttributes(
            Class<R> responseType,
            String zone,
            String tenant,
            Map<String, List<String>> searchFiendsMap,
            Map<String, String> searchQueryParameters) throws ChefClientException;

    <R extends IGetNodesAttributesResult, T> List<T> getCustomNodeAttributes(
            Class<R> responseType,
            ChefConfiguration chefConfiguration,
            Map<String, List<String>> searchFiendsMap,
            Map<String, String> searchQueryParameters) throws ChefClientException;

    Set<String> describeDefaultChefRoles(String zoneName) throws ChefServerUnexpectedErrorException;

    Set<String> describeChefRolesForServer(String serverId) throws ChefServerUnexpectedErrorException;

    Set<String> describeTenantInRegionChefRoles(String tenant, String region) throws ChefServerUnexpectedErrorException;

}
