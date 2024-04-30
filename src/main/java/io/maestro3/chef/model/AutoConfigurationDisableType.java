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

public enum AutoConfigurationDisableType {

    ALL,
    WINDOWS,
    LINUX,
    NONE;

    public boolean isAutoConfigurationDisabled() {
        return this == ALL;
    }

    public boolean isAutoConfigurationDisabled(boolean isWindows) {
        if (isWindows && this.nin(LINUX, NONE)) {
            return true;
        }
        return !isWindows && this.nin(WINDOWS, NONE);
    }

    public boolean nin(AutoConfigurationDisableType ... disableTypes) {
        for (AutoConfigurationDisableType configurationDisableType : disableTypes) {
            if (this == configurationDisableType) {
                return false;
            }
        }

        return true;
    }

    public boolean in(AutoConfigurationDisableType ... disableTypes) {
        for (AutoConfigurationDisableType configurationDisableType : disableTypes) {
            if (this == configurationDisableType) {
                return true;
            }
        }

        return false;
    }

    public static AutoConfigurationDisableType getDisableTypeByFlag(boolean isAutoConfigurationDisabled) {
        if (isAutoConfigurationDisabled) {
            return ALL;
        }

        return NONE;
    }

    public static AutoConfigurationDisableType getByName(String name) {
        for (AutoConfigurationDisableType configurationDisableType : values()) {
            if (configurationDisableType.name().equalsIgnoreCase(name)) {
                return configurationDisableType;
            }
        }

        return null;
    }
}
