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

package io.maestro3.chef.client.command.base;

import com.google.common.reflect.TypeToken;
import io.maestro3.chef.client.http.client.RequestMethod;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Forms a map containing resource names as keys and their uris as values. Chef server API gives this possibility
 * for most of the resources, e.g. you can perform requests: GET nodes, GET clients.
 *
 */
public abstract class BasicGetResourceUrisChefCommand extends BasicChefCommand<Map<String, String>> {

    @SuppressWarnings("unchecked")
    protected BasicGetResourceUrisChefCommand(String resourceName) {
        super(RequestMethod.GET, resourceName);
    }

    @Override
    public Object getRequestEntity() {
        // we won't pass anything in request body for "GET /resource" request
        return null;
    }

    @Override
    public Type getResultType() {
        return new TypeToken<Map<String, String>>() {
        }.getType();
    }
}
