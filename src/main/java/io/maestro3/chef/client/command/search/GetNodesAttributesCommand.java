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

package io.maestro3.chef.client.command.search;

import io.maestro3.chef.client.command.base.BasicChefCommand;
import io.maestro3.chef.client.http.client.RequestMethod;
import org.apache.commons.collections4.MapUtils;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gets specified attributes from all nodes registered on the Chef server.
 * See REST description for this command at <a href="http://docs.opscode.com/api_chef_server.html#id42">Chef Server API</a>
 *
 */
public class GetNodesAttributesCommand<T> extends BasicChefCommand<T> {

    private Class<T> type;
    private Map<String, List<String>> searchMap;
    private int skip;
    private int limit;

    @SuppressWarnings("unchecked")
    public GetNodesAttributesCommand(Class<T> type) {
        this(type, new HashMap<String, List<String>>(), new HashMap<String, String>());
    }

    /**
     * Construct the command with specified search mappings, that is: keys of searchMap represent real node attribute
     * names (e.g. "traffic_in") and value of search map represents under which name attributes will be returned
     * (e.g. "inputTraffic").
     *
     * @param searchMap       the map with search configuration
     * @param queryParameters the map with search query configuration
     */
    public GetNodesAttributesCommand(Class<T> type, Map<String, List<String>> searchMap, Map<String, String> queryParameters) {
        this(type, searchMap, queryParameters, 0, 1000);
    }

    /**
     * Construct the command with specified search mappings, that is: keys of searchMap represent real node attribute
     * names (e.g. "traffic_in") and value of search map represents under which name attributes will be returned
     * (e.g. "inputTraffic").
     *
     * @param searchMap       the map with search configuration
     * @param queryParameters the map with search query configuration
     */
    public GetNodesAttributesCommand(Class<T> type, Map<String, List<String>> searchMap, Map<String, String> queryParameters, int skip, int limit) {
        super(RequestMethod.POST, "search/node", buildQuery(queryParameters, skip, limit));
        this.searchMap = searchMap;
        this.type = type;
    }

    @Override
    public Type getResultType() {
        return type;
    }

    /**
     * Build query string for command, query parameters set through AND logic operation for now.
     *
     * @return query string
     */
    private static String buildQuery(Map<String, String> querySearchParameters, int skip, int limit) {
        String querySearchString = "q=*:*";
        if (MapUtils.isNotEmpty(querySearchParameters)) {
            QueryBuilder queryBuilder = new QueryBuilder();
            for (Map.Entry<String, String> entry : querySearchParameters.entrySet()) {
                queryBuilder.withQueryParameter(entry.getKey(), entry.getValue());
            }
            querySearchString = queryBuilder.getQueryString();
        }

        return querySearchString + "&sort=X_CHEF_id_CHEF_X%20asc&start=" + skip + "&rows=" + limit;
    }

    @Override
    public Object getRequestEntity() {
        return searchMap;
    }

    /**
     * Adds new search field for a command.
     *
     * @param nodeAttributeName the name of the attribute to search for, e.g. "os", can be found at Chef Server web UI
     * @param metricName        the name of the metric that attribute value will be mapped at {@linkplain NodeAttributes}, e.g. "operationSystemType"
     */
    public void append(String nodeAttributeName, String metricName) {
        searchMap.put(metricName, Arrays.asList(nodeAttributeName));
    }

    /**
     * Adds new search field for a command.
     *
     * @param nodeAttributePath the path of the attribute to search for, can be found at Chef Server web UI
     * @param metricName        the name of the metric that attribute value will be mapped at {@linkplain NodeAttributes}, e.g. "operationSystemType"
     */
    public void append(List<String> nodeAttributePath, String metricName) {
        searchMap.put(metricName, nodeAttributePath);
    }

    /**
     * Adds new search field for a command using attribute name as name of the metric.
     *
     * @param nodeAttributeName the name of the attribute to search for, e.g. "os", can be found at Chef Server web UI
     */
    public void append(String nodeAttributeName) {
        searchMap.put(nodeAttributeName, Arrays.asList(nodeAttributeName));
    }

    /**
     * Acts just like append() adding search field for a command. Uses more friendly syntax, e.g.: command.searchFor("traffic_in").as("incomeTraffic")
     *
     * @param nodeAttributeName the name of the attribute to search for, e.g. "os", can be found at Chef Server web UI
     * @return the builder for search request
     */
    public OnGoingSearchBuilder get(String nodeAttributeName) {
        return new OnGoingSearchBuilder(this, nodeAttributeName);
    }

    public static class OnGoingSearchBuilder {
        private GetNodesAttributesCommand command;
        private String nodeAttributeName;

        public OnGoingSearchBuilder(GetNodesAttributesCommand command, String nodeAttributeName) {
            this.command = command;
            this.nodeAttributeName = nodeAttributeName;
        }

        /**
         * Adds new search field for a command.
         *
         * @param metricName the name of the metric that attribute value will be mapped at {@linkplain NodeAttributes}, e.g. "operationSystemType"
         * @return the command itself for later search request build
         */
        public GetNodesAttributesCommand as(String metricName) {
            command.append(nodeAttributeName, metricName);
            return command;
        }

        /**
         * Adds new search field for a command using attribute name as name of the metric.
         *
         * @return the command itself for later search request build
         */
        public GetNodesAttributesCommand asItIs() {
            command.append(nodeAttributeName, nodeAttributeName);
            return command;
        }
    }

    public static class QueryBuilder {

        private final String HTTP_WHITESPACE = "%20";
        private final String AND_OPERATION = "AND";
        private final String KEY_VALUE_QUERY_SEPARATOR = ":";

        private StringBuilder queryStringBuilder;
        private int queryParametersCounter = 0;

        QueryBuilder() {
            queryStringBuilder = new StringBuilder("q=");
        }

        QueryBuilder withQueryParameter(String name, String value) {
            if (queryParametersCounter > 0) {
                and();
            }
            queryStringBuilder.append(name)
                    .append(KEY_VALUE_QUERY_SEPARATOR)
                    .append(value);
            queryParametersCounter++;
            return this;
        }

        private QueryBuilder and() {
            queryStringBuilder.append(HTTP_WHITESPACE)
                    .append(AND_OPERATION)
                    .append(HTTP_WHITESPACE);
            return this;
        }

        String getQueryString() {
            return queryStringBuilder.toString();
        }
    }


}
