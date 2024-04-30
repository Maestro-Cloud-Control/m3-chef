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

import java.util.Map;

public class UpdateDataBagItemCommand extends BasicChefCommand<Map> {
    private static final String COMMAND_RESOURCE_NAME = "data";

    private String name;

    private String id;

    private Map<String, Object> data;

    public UpdateDataBagItemCommand(String dataBag, String name, Map<String, Object> data) {
        super(RequestMethod.PUT, COMMAND_RESOURCE_NAME);
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
        return COMMAND_RESOURCE_NAME + "/" + name + "/" + id;
    }

    @Override
    public Object getRequestEntity() {
        Assert.notNull(name, "You must set name before executing this command");
        Assert.notNull(id, "You must set id before executing this command");
        Assert.notEmpty(data, "You must set data before executing this command");

        data.put("id", id);
        return data;
    }
}
