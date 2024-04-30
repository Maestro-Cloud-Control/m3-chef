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

package io.maestro3.chef.client.factory;

import io.maestro3.chef.client.IChefClient;
import io.maestro3.chef.client.context.IChefContext;

public interface IChefClientFactory {

    IChefClient getInstance(IChefContext chefContext);

    /**
     * Create chef client with new customized http client.
     * Note: use it carefully, currently used only in admin app.
     *
     * @param attributes custom timeouts for socket/connection
     * @return IChefClient with new http client
     */
    IChefClient getAdminInstance(IChefContext chefContext, ChefClientAttributes attributes);
}
