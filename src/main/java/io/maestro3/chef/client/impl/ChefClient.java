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

package io.maestro3.chef.client.impl;

import io.maestro3.chef.client.IChefClient;
import io.maestro3.chef.client.command.IChefCommand;
import io.maestro3.chef.client.context.IChefContext;
import io.maestro3.chef.client.exception.ChefClientException;
import io.maestro3.chef.client.handler.ChefRequestHandler;
import io.maestro3.chef.client.handler.ChefResponseHandler;
import io.maestro3.chef.client.http.client.Request;
import io.maestro3.chef.client.http.client.RequestBuilder;
import io.maestro3.chef.client.http.client.SimpleHttpClient;
import io.maestro3.chef.client.http.client.SimpleHttpClientImpl;
import io.maestro3.chef.client.http.client.exception.SimpleHttpClientException;
import io.maestro3.chef.client.response.IChefResponse;
import io.maestro3.chef.client.response.impl.BasicChefResponse;
import io.maestro3.chef.client.serialization.ChefRequestSerializer;
import io.maestro3.chef.client.serialization.ChefResponseDeserializer;
import io.maestro3.sdk.internal.util.Assert;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;

public class ChefClient implements IChefClient {

    private IChefContext context;
    private SimpleHttpClient client;
    private ChefResponseDeserializer deserializer;
    private ChefResponseHandler responseHandler;

    /**
     * Creates chef client. You must set chef context before using this client for command execution.
     */
    public ChefClient(HttpClient httpClient, IChefContext chefContext) {
        this.client = new SimpleHttpClientImpl(httpClient);
        this.deserializer = new ChefResponseDeserializer();
        this.responseHandler = new ChefResponseHandler();
        this.context = chefContext;
    }

    @Override
    public <R> IChefResponse<R> execute(IChefCommand<R> command) throws ChefClientException {
        Assert.notNull(context, "You must set chef context before using this client for command execution");
        try {
            BasicChefResponse<R> response = new BasicChefResponse<>();
            if (command.getReturnRaw()) {
                String rawResult = client.execute(context.getUri(), buildRequest(command));
                response.setRawResult(rawResult);
            } else {
                R result = client.execute(context.getUri(), buildRequest(command));
                response.setResult(result);
            }
            response.setCode(responseHandler.getLastResponseCode());
            response.setDescription(responseHandler.getLastResponseDescription());
            response.setErrorOccurred(responseHandler.getLastResponseError());

            response.setHash(deserializer.getLashResponseHash());
            if (!response.getErrorOccurred()) {
                response.setErrorOccurred(deserializer.getLastResponseError());
            }

            return response;
        } catch (SimpleHttpClientException exception) {
            throw new ChefClientException("Cannot execute " + command.getClass().getSimpleName() + " command.", exception);
        } catch (Exception exception) {
            throw new ChefClientException("Unexpected exception. " + exception.getMessage(), exception);
        }
    }

    public IChefContext getContext() {
        return context;
    }

    private Request buildRequest(IChefCommand command) {
        Assert.notNull(command.getMethod(), "Command must specify request method to be used");

        RequestBuilder builder = new RequestBuilder();
        builder.toUri(buildUri(command))
                .deserializer(deserializer)
                .with(new ChefRequestHandler(context, command))
                .with(responseHandler);
        if (command.getReturnRaw()) {
            builder.as(String.class);
        } else {
            builder.as(command.getResultType());
        }
        switch (command.getMethod()) {
            case GET:
                builder.get();
                break;
            case POST:
                builder.post(command).serializer(new ChefRequestSerializer());
                break;
            case PUT:
                builder.put(command).serializer(new ChefRequestSerializer());
                break;
            case DELETE:
                builder.delete();
                break;
            default:
                break;
        }
        return builder.create();
    }

    private String buildUri(IChefCommand command) {
        String query = command.getQuery();
        String chefBaseUrl = context.getUri() + context.getOrganizationResourcePrefix();
        if (StringUtils.isNotBlank(query)) {
            return chefBaseUrl + command.getResourceName() + "?" + query;
        }
        return chefBaseUrl + command.getResourceName();
    }
}
