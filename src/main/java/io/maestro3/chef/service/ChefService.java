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

import com.google.common.collect.Lists;
import io.maestro3.cadf.util.Assert;
import io.maestro3.chef.client.IChefClient;
import io.maestro3.chef.client.command.IChefCommand;
import io.maestro3.chef.client.command.client.DeleteClientCommand;
import io.maestro3.chef.client.command.node.DeleteNodeCommand;
import io.maestro3.chef.client.command.role.GetRoleUrisCommand;
import io.maestro3.chef.client.command.search.GetNodesAttributesCommand;
import io.maestro3.chef.client.command.search.NodeDescriptor;
import io.maestro3.chef.client.command.search.attributes.GetNodesStatisticAttributesResult;
import io.maestro3.chef.client.command.search.attributes.IGetNodesAttributesResult;
import io.maestro3.chef.client.context.IChefContext;
import io.maestro3.chef.client.context.IChefContextFactory;
import io.maestro3.chef.client.entity.NodeEntity;
import io.maestro3.chef.client.exception.ChefClientException;
import io.maestro3.chef.client.factory.IChefClientFactory;
import io.maestro3.chef.client.response.IChefResponse;
import io.maestro3.chef.exception.ChefServerUnexpectedErrorException;
import io.maestro3.chef.model.ChefConfiguration;
import io.maestro3.chef.model.ChefStrategy;
import io.maestro3.sdk.internal.util.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ChefService implements IChefService {

    private static final Logger LOG = LoggerFactory.getLogger(ChefService.class);

    private static final int NODE_ATTRIBUTE_INFO_LIMIT = 1000;

    private final IChefClientFactory chefClientFactory;
    private final IChefContextFactory contextFactory;
    private final IChefConfigurationService chefConfigurationService;

    @Autowired
    public ChefService(IChefClientFactory chefClientFactory, IChefContextFactory contextFactory,
                       IChefConfigurationService chefConfigurationService) {
        this.chefClientFactory = chefClientFactory;
        this.contextFactory = contextFactory;
        this.chefConfigurationService = chefConfigurationService;
    }


    @Override
    public List<NodeDescriptor> getDefaultChefNodeAttributes(String tenant, String region) throws ChefClientException {
        Assert.notNull(tenant, "tenant can't be null.");
        Assert.notNull(region, "zone can't be null.");

        IChefContext chefContext = contextFactory.getInstance(tenant, region);
        IChefClient client = chefClientFactory.getInstance(chefContext);

        if (client != null && chefContext != null) {
            Map<String, List<String>> searchMap = getSearchNodeMap();
            IChefCommand<GetNodesStatisticAttributesResult> command = new GetNodesAttributesCommand<>(GetNodesStatisticAttributesResult.class, searchMap, null);

            IChefResponse<GetNodesStatisticAttributesResult> response = client.execute(command);
            if (!response.getErrorOccurred()) {
                GetNodesStatisticAttributesResult result = response.getResult();
                return result.getResults();
            } else {
                LOG.error("{}, chef server URI: {}", response.getDescription(), chefContext.getUri());
            }
        }

        return Collections.emptyList();
    }

    private Map<String, List<String>> getSearchNodeMap() {
        Map<String, List<String>> searchMap = new HashMap<>();
        searchMap.put("nodeName", Lists.newArrayList("name"));
        searchMap.put("inputOutputOperationsPerSecond", Lists.newArrayList("iops"));
        searchMap.put("incomeTraffic", Lists.newArrayList("traffic_in"));
        searchMap.put("outcomeTraffic", Lists.newArrayList("traffic_out"));
        searchMap.put("operationSystemType", Lists.newArrayList("os"));
        searchMap.put("timestamp", Lists.newArrayList("ohai_time"));
        return searchMap;
    }

    @Override
    public <R extends IGetNodesAttributesResult, T> List<T> getCustomNodeAttributes(Class<R> responseType, String region, String tenant, Map<String, List<String>> searchFiendsMap, Map<String, String> searchQueryParameters)
        throws ChefClientException {
        Assert.notNull(responseType, "responseType can't be null.");
        IChefContext chefContext = contextFactory.getInstance(tenant, region);
        IChefClient client = chefClientFactory.getInstance(chefContext);

        return getNodeChefAttributes(responseType, searchFiendsMap, searchQueryParameters, chefContext, client);
    }

    @Override
    public <R extends IGetNodesAttributesResult, T> List<T> getCustomNodeAttributes(Class<R> responseType, ChefConfiguration chefConfiguration, Map<String, List<String>> searchFiendsMap, Map<String, String> searchQueryParameters)
        throws ChefClientException {
        Assert.notNull(responseType, "responseType can't be null.");
        IChefContext chefContext = contextFactory.getInstance(chefConfiguration);
        IChefClient client = chefClientFactory.getInstance(chefContext);

        return getNodeChefAttributes(responseType, searchFiendsMap, searchQueryParameters, chefContext, client);
    }

    private <R extends IGetNodesAttributesResult, T> List<T> getNodeChefAttributes(Class<R> responseType, Map<String, List<String>> searchFiendsMap, Map<String, String> searchQueryParameters, IChefContext chefContext, IChefClient client) throws ChefClientException {
        List<T> results = Lists.newArrayList();

        if (client != null && chefContext != null) {
            int retrieved = 0;
            Integer total = 0;

            int retryCount = 5;
            while (retrieved <= total) {
                GetNodesAttributesCommand<R> command = new GetNodesAttributesCommand<>(responseType, searchFiendsMap, searchQueryParameters, retrieved, NODE_ATTRIBUTE_INFO_LIMIT);

                IChefResponse<R> response = client.execute(command);

                if (!response.getErrorOccurred()) {
                    IGetNodesAttributesResult<T> responseGeneralResult = response.getResult();
                    if (responseGeneralResult != null) {
                        results.addAll(responseGeneralResult.getResults());
                        retrieved = results.size();
                    }
                } else {
                    // if retry counter exceeded - return empty response
                    if (retryCount <= 0) {
                        throw new ChefClientException(response.getDescription());
                    }
                    retryCount--;
                    LOG.error(response.getDescription());
                }

                if (total == 0) {
                    total = response.getResult().getTotal();
                    if (total == null) {
                        // if chef does not return "total", then forget about pagination and return what we got
                        break;
                    }
                }

                retrieved += NODE_ATTRIBUTE_INFO_LIMIT;
            }
        }
        return results;
    }

    @Override
    public Set<String> describeDefaultChefRoles(String zoneName) throws ChefServerUnexpectedErrorException {
        Assert.hasText(zoneName, "zoneName cannot be null or empty.");

        Set<String> chefRoles = new HashSet<>();
        ChefConfiguration chefConfiguration = chefConfigurationService.findByRegionName(zoneName);

        try {
            IChefContext chefContext = contextFactory.getInstance(chefConfiguration);
            IChefClient chefClient = chefClientFactory.getInstance(chefContext);
            fillRoles(chefRoles, chefClient);
        } catch (ChefClientException e) {
            LOG.error("Failed to execute request to obtain chef roles from project chef. Region [" + zoneName + "]", e);
            throw new ChefServerUnexpectedErrorException(e.getMessage());
        }
        return chefRoles;
    }

    private void fillRoles(Set<String> chefRoles, IChefClient chefClient) throws ChefClientException, ChefServerUnexpectedErrorException {
        GetRoleUrisCommand command = new GetRoleUrisCommand();
        IChefResponse<Map<String, String>> chefResponse = getAndValidateChefResponse(chefClient, command);
        Set<String> chefRolesRetrieved = getChefRoles(chefResponse);
        if (CollectionUtils.isNotEmpty(chefRolesRetrieved)) {
            chefRoles.addAll(chefRolesRetrieved);
        }
    }

    @Override
    public Set<String> describeChefRolesForServer(String serverId) throws ChefServerUnexpectedErrorException {
        Assert.hasText(serverId, "zoneName cannot be null or empty.");

        Set<String> chefRoles = new HashSet<>();
        ChefConfiguration chefConfiguration = chefConfigurationService.findByServerId(serverId);
        try {
            IChefContext chefContext = contextFactory.getInstance(chefConfiguration);
            IChefClient chefClient = chefClientFactory.getInstance(chefContext);
            fillRoles(chefRoles, chefClient);
        } catch (ChefClientException e) {
            LOG.error("Failed to execute request to obtain chef roles from server chef. Server id [" + serverId + "]", e);
            throw new ChefServerUnexpectedErrorException(e.getMessage());
        }
        return chefRoles;
    }

    @Override
    public Set<String> describeTenantInRegionChefRoles(String tenant, String region) throws ChefServerUnexpectedErrorException {
        Assert.notNull(tenant, "tenant cannot be null.");
        Assert.notNull(region, "zone cannot be null.");

        Set<String> chefRoles = new HashSet<>();

        IChefContext chefContext = contextFactory.getProjectInstance(tenant, region);
        try {
            IChefClient chefClient = chefClientFactory.getInstance(chefContext);
            fillRoles(chefRoles, chefClient);
        } catch (ChefClientException e) {
            LOG.error("Failed to execute request to obtain chef roles from project chef. Tenant [" + tenant + "], region [" + region + "]", e);
            throw new ChefServerUnexpectedErrorException(e.getMessage());
        }
        return chefRoles;
    }

    private IChefResponse<Map<String, String>> getAndValidateChefResponse(IChefClient chefClient, GetRoleUrisCommand command) throws ChefClientException, ChefServerUnexpectedErrorException {
        IChefResponse<Map<String, String>> chefResponse = chefClient.execute(command);
        if (chefResponse.getCode() >= HttpStatus.SC_BAD_REQUEST) {
            throw new ChefServerUnexpectedErrorException("Chef server returned " + chefResponse.getCode() + " status code.");
        }
        return chefResponse;
    }

    private Set<String> getChefRoles(IChefResponse<Map<String, String>> chefResponse) {
        if (chefResponse != null) {
            Map<String, String> chefRolesMap = chefResponse.getResult();
            if (MapUtils.isNotEmpty(chefRolesMap)) {
                return chefRolesMap.keySet();
            }
        }
        return null;
    }


    @Override
    public boolean deleteNode(String tenant, String region, String node, ChefStrategy strategy) {
        switch (strategy) {
            case NOT_ACTIVE:
                LOG.warn("Will try to delete node {} from zone {}.", node, region);
                deleteNodeFromZone(region, node);
                return true;
            case BY_ZONE:
                return deleteNodeFromZone(region, node);
            case BY_PROJECT:
                return deleteNodeFromProject(tenant, region, node);
            case CUSTOM:
                return true;
        }
        return false;
    }

    private boolean deleteNodeFromZone(String region, String node) {
        ChefConfiguration chefConfiguration = chefConfigurationService.findByRegionName(region);
        IChefContext context = contextFactory.getInstance(chefConfiguration);
        if (context != null) {
            IChefClient client = chefClientFactory.getInstance(context);
            return deleteNode(client, node);
        }
        return true;
    }

    private boolean deleteNodeFromProject(String tenant, String region, String node) {
        IChefContext context = contextFactory.getProjectInstance(tenant, region);
        if (context == null) {
            return false;
        }
        String chefUri = contextFactory.convertHostToUri(context.getUri());
        if (!chefUri.equals(context.getUri())) {
            return false;
        }
        IChefClient client = chefClientFactory.getInstance(context);
        return deleteNode(client, node);
    }

    private boolean deleteNode(IChefClient client, String node) {
        if (StringUtils.isNotBlank(node)) {
            try {
                IChefCommand<NodeEntity> nodeCommand = new DeleteNodeCommand(node);
                IChefResponse<NodeEntity> nodeResponse = client.execute(nodeCommand);
                if (nodeResponse.getErrorOccurred()) {
                    LOG.error("Can not delete node from Chef server. Node: {}, Reason: {}, Code: {}", node, nodeResponse.getDescription(), nodeResponse.getCode());
                }
                if (nodeResponse.getCode() != HttpStatus.SC_OK) {
                    LOG.error("Unexpected status code while deleting Chef node {}: {}.", node, nodeResponse.getCode());
                }
                IChefCommand<Void> clientCommand = new DeleteClientCommand(node);
                IChefResponse<Void> clientResponse = client.execute(clientCommand);
                if (clientResponse.getErrorOccurred()) {
                    LOG.error("Can not delete client from Chef server. Client: {}, Reason: {}, Code: {}", node, clientResponse.getDescription(), clientResponse.getCode());
                }
                if (clientResponse.getCode() != HttpStatus.SC_OK) {
                    LOG.error("Unexpected status code while deleting Chef client {}: {}.", node, clientResponse.getCode());
                }
                return successfullyDeleted(nodeResponse, clientResponse);
            } catch (Exception e) {
                LOG.error("Can not delete node and client from Chef server. node: " + node, e);
                return false;
            }
        }
        return false;
    }

    private boolean successfullyDeleted(IChefResponse<NodeEntity> nodeResponse, IChefResponse<Void> clientResponse) {
        boolean nodeDeleted = !nodeResponse.getErrorOccurred() && nodeResponse.getCode() == HttpStatus.SC_OK;
        boolean clientDeleted = !clientResponse.getErrorOccurred() && clientResponse.getCode() == HttpStatus.SC_OK;
        return nodeDeleted && clientDeleted;
    }
}
