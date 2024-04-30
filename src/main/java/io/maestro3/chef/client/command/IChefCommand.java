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

package io.maestro3.chef.client.command;

import io.maestro3.chef.client.http.client.RequestMethod;

import java.lang.reflect.Type;

public interface IChefCommand<R> {

    /**
     * @return the entity for POST and PUT requests bodies, e.g. can return Map of String-Object that will be serialized into json
     */
    Object getRequestEntity();

    /**
     * @return the class of result that is expected from command execution, e.g. MyCommandResult.class
     */
    Type getResultType();

    /**
     * @return the method that is used to perform chef command, e.g. RequestMethod.GET
     */
    RequestMethod getMethod();

    /**
     * @return the name of resource that command deals with, e.g. "nodes" for "GET host/nodes" commands
     */
    String getResourceName();

    /**
     * @return the query string for GET requests
     */
    String getQuery();

    /**
     * @return <code>true</code> if command result should not be converted
     */
    boolean getReturnRaw();

}
