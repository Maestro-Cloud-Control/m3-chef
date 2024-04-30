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

package io.maestro3.chef.model;

/**
 * Shows the strategy or how the instance monitoring flow will be handled.
 *
 */
public enum ChefStrategy {

    /**
     * For nodes that are not monitored by chef servers
     */
    NOT_ACTIVE,

    /**
     * For nodes monitored by Zone chef servers
     */
    BY_ZONE,

    /**
     * For nodes monitored by EPC chef servers
     */
    BY_PROJECT,

    /**
     * For nodes monitored by any other (user) servers
     */
    CUSTOM

}
