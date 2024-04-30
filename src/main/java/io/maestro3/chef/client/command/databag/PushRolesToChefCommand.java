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
import io.maestro3.sdk.internal.util.MapUtils;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class PushRolesToChefCommand extends BasicChefCommand<Map> {
    private static final String COMMAND_RESOURCE_NAME = "nodes";

    private String name;

    private String[] roles;

    public PushRolesToChefCommand(String name, String[] roles) {
        super(RequestMethod.PUT, COMMAND_RESOURCE_NAME);
        this.name = name;
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    @Override
    public String getResourceName() {
        return COMMAND_RESOURCE_NAME + "/" + name;
    }

    @Override
    public Object getRequestEntity() {
        Assert.notNull(name, "You must set name before executing this command");
        Assert.notEmpty(roles, "You must set roles before executing this command");

        return MapUtils.builder()
                .withPair("name", name)
                .withPair("run_list", Arrays.stream(roles)
                        .map(role -> "role[" + role + "]")
                        .collect(Collectors.toList()))
                .build();
    }
}
