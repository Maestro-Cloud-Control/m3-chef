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

package io.maestro3.chef.client.command.client;

import com.google.gson.annotations.SerializedName;
import io.maestro3.sdk.internal.util.Assert;
import io.maestro3.chef.client.http.client.RequestMethod;
import io.maestro3.chef.client.command.base.BasicChefCommand;
import io.maestro3.chef.client.utils.ChefUtils;

/**
 * Creates new api client. Returns client uri and it's private key as a result.
 * See REST description for this command at <a href="http://docs.opscode.com/api_chef_server.html#post">Chef Server API</a>
 *
 */
public class CreateClientCommand extends BasicChefCommand<CreateClientCommand.Result> {
    private static final String COMMAND_RESOURCE_NAME = "clients";

    private CreateClientCommandInput clientData;

    public CreateClientCommand() {
        super(RequestMethod.POST, COMMAND_RESOURCE_NAME);
    }

    public CreateClientCommandInput getClientData() {
        return clientData;
    }

    public void setClientData(CreateClientCommandInput clientData) {
        this.clientData = clientData;
    }

    @Override
    public Object getRequestEntity() {
        Assert.notNull(clientData, "You must set clientData before executing this command");

        return clientData;
    }

    /**
     * Represents the result of creating new client on chef server, namely, the uri to make requests for this client and the
     * generated private key to be used to sign requests.
     *
     */
    public static class Result {

        @SerializedName("uri")
        private String uri;

        @SerializedName("private_key")
        private String authenticationKey;

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getAuthenticationKey() {
            return authenticationKey;
        }

        public void setAuthenticationKey(String authenticationKey) {
            this.authenticationKey = authenticationKey;
        }

        @Override
        public String toString() {
            return ChefUtils.buildString("CreateClientCommandResult{uri=", uri,
                    ", authenticationKey=", authenticationKey,
                    '}');
        }
    }
}
