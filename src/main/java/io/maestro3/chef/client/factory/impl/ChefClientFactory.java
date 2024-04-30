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

package io.maestro3.chef.client.factory.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.maestro3.chef.client.IChefClient;
import io.maestro3.chef.client.context.ChefContextType;
import io.maestro3.chef.client.context.IChefContext;
import io.maestro3.chef.client.factory.ChefClientAttributes;
import io.maestro3.chef.client.factory.IChefClientFactory;
import io.maestro3.chef.client.factory.SyncedLazyInitializers;
import io.maestro3.chef.client.http.client.HttpClientsCounter;
import io.maestro3.chef.client.http.client.ICloseableHttpClientFactory;
import io.maestro3.chef.client.impl.ChefClient;
import io.maestro3.sdk.internal.util.Assert;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class ChefClientFactory implements IChefClientFactory {

    private static final int CLIENT_CACHE_SIZE = 100;

    private Cache<String, IChefClient> zoneChefClients = CacheBuilder.newBuilder()
            .maximumSize(CLIENT_CACHE_SIZE)
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build();

    private Cache<String, IChefClient> projectChefClients = CacheBuilder.newBuilder()
            .maximumSize(CLIENT_CACHE_SIZE)
            .expireAfterAccess(3, TimeUnit.HOURS)
            .build();

    @Autowired
    private ICloseableHttpClientFactory clientFactory;

    private HttpClient zoneChefHttpClient;
    private HttpClient projectChefHttpClient;

    private Lock lock = new ReentrantLock();

    @PostConstruct
    public void init() {
        zoneChefHttpClient = clientFactory.getCountedHttpClient("default-chef");
    }

    @Override
    public IChefClient getInstance(IChefContext chefContext) {
        switch (chefContext.getChefContextType()) {
            case PROJECT:
                return getFromCacheOrCreateNew(chefContext, new ProjectCloseableHttpClientProvider());
            case ZONE:
            default:
                return getFromCacheOrCreateNew(chefContext, new CloseableHttpClientProvider());
        }

    }

    @Override
    public IChefClient getAdminInstance(IChefContext chefContext, ChefClientAttributes attributes) {
        return getFromCacheOrCreateNew(chefContext, new HttpClientWithAttributesProvider(attributes));
    }

    private synchronized IChefClient getFromCacheOrCreateNew(IChefContext chefContext, HttpClientProvider httpClientProvider) {
        Cache<String, IChefClient> chefClients = resolveByType(chefContext.getChefContextType());
        String cacheKey = chefContext.getServerId();
        IChefClient chefClient = chefClients.getIfPresent(cacheKey);
        if (chefClient != null) {
            return chefClient;
        }

        chefClient = new ChefClient(httpClientProvider.getHttpClient(), chefContext);
        chefClients.put(cacheKey, chefClient);
        return chefClient;
    }

    private Cache<String, IChefClient> resolveByType(ChefContextType contextType) {
        Assert.notNull(contextType, "contextType cannot be null.");
        if (contextType == ChefContextType.PROJECT) {
            return projectChefClients;
        } else {
            return zoneChefClients;
        }
    }

    @PreDestroy
    private void destroy() throws IOException {
        if (zoneChefClients instanceof CloseableHttpClient) {
            ((CloseableHttpClient) zoneChefHttpClient).close();

        }
        if (Objects.nonNull(projectChefHttpClient)) {
            if (projectChefHttpClient instanceof CloseableHttpClient) {
                ((CloseableHttpClient) projectChefHttpClient).close();

            }
        }
    }

    private interface HttpClientProvider {

        HttpClient getHttpClient();
    }

    private class CloseableHttpClientProvider implements HttpClientProvider {
        @Override
        public HttpClient getHttpClient() {
            return zoneChefHttpClient;
        }
    }

    private class ProjectCloseableHttpClientProvider implements HttpClientProvider {
        @Override
        public HttpClient getHttpClient() {
            return SyncedLazyInitializers.getLazyInitializedObject(
                    () -> lock,
                    () -> projectChefHttpClient,
                    () -> {
                        projectChefHttpClient = clientFactory.getCountedHttpClient("project-paas-chef");
                        return projectChefHttpClient;
                    }
            );
        }
    }

    private class HttpClientWithAttributesProvider implements HttpClientProvider {

        private final ChefClientAttributes attributes;

        private HttpClientWithAttributesProvider(ChefClientAttributes attributes) {
            this.attributes = attributes;
        }

        @Override
        public HttpClient getHttpClient() {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(attributes.getConnectionTimeout())
                    .setSocketTimeout(attributes.getSocketTimeout())
                    .build();
            HttpClientsCounter.incrementCounter("admin-custom-chef-client");
            return clientFactory.getHttpClient(requestConfig);
        }
    }
}
