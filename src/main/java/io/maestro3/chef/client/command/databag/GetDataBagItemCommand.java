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

package io.maestro3.chef.client.command.databag;

import io.maestro3.chef.client.command.base.BasicChefCommand;
import io.maestro3.chef.client.http.client.RequestMethod;

import java.util.Map;

public class GetDataBagItemCommand extends BasicChefCommand<Map> {
    private static final String COMMAND_RESOURCE_NAME = "data";

    private String name;

    private String id;

    public GetDataBagItemCommand(String name, String id) {
        super(RequestMethod.GET, COMMAND_RESOURCE_NAME);
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getResourceName() {
        return COMMAND_RESOURCE_NAME + "/" + name + "/" + id;
    }

    @Override
    public Object getRequestEntity() {
        // we won't pass anything in request body for "GET /node" request
        return null;
    }

}
