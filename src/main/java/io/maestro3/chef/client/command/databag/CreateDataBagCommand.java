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
import io.maestro3.sdk.internal.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class CreateDataBagCommand extends BasicChefCommand<CreateDataBagCommand.Result> {
    private static final String COMMAND_RESOURCE_NAME = "data";

    private String name;

    public CreateDataBagCommand(String name) {
        super(RequestMethod.POST, COMMAND_RESOURCE_NAME);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object getRequestEntity() {
        Assert.notNull(name, "You must set name before executing this command");
        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        return body;
    }

    public static class Result {
        private String uri;

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        @Override
        public String toString() {
            return "CreateNodeCommandResult{uri=" + uri + '}';
        }
    }
}
