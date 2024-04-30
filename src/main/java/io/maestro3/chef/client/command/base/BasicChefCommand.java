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

import io.maestro3.chef.client.command.IChefCommand;
import io.maestro3.chef.client.http.client.RequestMethod;
import io.maestro3.chef.client.utils.ReflectionUtils;
import io.maestro3.chef.client.http.client.RequestMethod;
import io.maestro3.chef.client.command.IChefCommand;
import io.maestro3.chef.client.utils.ReflectionUtils;

import java.lang.reflect.Type;

public abstract class BasicChefCommand<R> implements IChefCommand<R> {
    private transient RequestMethod method;
    private transient String resource;
    private transient String query;
    private transient boolean returnRaw;

    protected BasicChefCommand(RequestMethod method, String resource) {
        this(method, resource, null);
    }

    protected BasicChefCommand(RequestMethod method, String resource, String query) {
        this.method = method;
        this.resource = resource;
        this.query = query;
    }

    @Override
    public Type getResultType() {
        return ReflectionUtils.getGenericParameterClass(this.getClass(), 0);
    }

    @Override
    public RequestMethod getMethod() {
        return method;
    }

    @Override
    public String getResourceName() {
        return resource;
    }

    @Override
    public boolean getReturnRaw() {
        return returnRaw;
    }

    public String getQuery() {
        return query;
    }

    public void setReturnRaw(boolean returnRaw) {
        this.returnRaw = returnRaw;
    }

}
