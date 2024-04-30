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

package io.maestro3.chef.service;

import io.maestro3.chef.client.context.IChefContext;

import java.util.List;
import java.util.Map;

public interface IChefDataBagService {

    boolean isChefDataBagsEnabled();

    void createDataBag(IChefContext context, String name);

    boolean dataBagExists(IChefContext context, String name);

    void createItem(IChefContext context, String dataBag, String name, Map<String, String> data);

    void updateOrCreateItem(IChefContext context, String dataBag, String name, Map<String, String> data);

    boolean dataBagItemExists(IChefContext context, String dataBag, String name);

    void deleteDatabagAndCredentials(IChefContext context, String name);

    void deleteItem(IChefContext context, String dataBag, String name);

    void pushChefRole(IChefContext context, String instanceId, String[] chefRoles);

    void createNode(IChefContext context, String instanceId);

    List<String> getChefRoles(IChefContext context, String instanceId);
}
