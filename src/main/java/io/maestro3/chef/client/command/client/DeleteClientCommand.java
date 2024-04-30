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

import io.maestro3.chef.client.command.base.BasicChefCommand;
import io.maestro3.chef.client.http.client.RequestMethod;

/**
 * Deletes API client specified by name.
 * See REST description for this command at <a href="http://docs.opscode.com/api_chef_server.html#delete">Chef Server API</a>
 *
 */
public class DeleteClientCommand extends BasicChefCommand<Void> {
    private static final String COMMAND_RESOURCE_PREFIX = "clients/";

    public DeleteClientCommand(String name) {
        super(RequestMethod.DELETE, COMMAND_RESOURCE_PREFIX + name);
    }

    @Override
    public Object getRequestEntity() {
        // we won't pass anything in request body for "GET /node" request
        return null;
    }
}
