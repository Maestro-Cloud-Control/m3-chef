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
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class CreateDataBagItemCommand extends BasicChefCommand<CreateDataBagItemCommand.Result> {
    private static final String COMMAND_RESOURCE_NAME = "data";

    private String name;

    private String id;

    private Map<String, Object> data;

    public CreateDataBagItemCommand(String dataBag, String name, Map<String, Object> data) {
        super(RequestMethod.POST, COMMAND_RESOURCE_NAME);
        this.name = dataBag;
        this.id = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getResourceName() {
        return COMMAND_RESOURCE_NAME + "/" + name;
    }

    @Override
    public Object getRequestEntity() {
        Assert.notNull(name, "You must set name before executing this command");
        Assert.notNull(id, "You must set id before executing this command");
        Assert.notEmpty(data, "You must set data before executing this command");
        data.put("id", id);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "data_bag_item_" + name + "_" + id);
        requestBody.put("json_class", "Chef::DataBagItem");
        requestBody.put("chef_type", "data_bag_item");
        requestBody.put("data_bag", name);
        requestBody.put("raw_data", data);
        return requestBody;
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
            return "CreateDataBagItemCommand {uri=" + uri + '}';
        }
    }
}
