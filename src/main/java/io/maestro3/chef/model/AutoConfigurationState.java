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

public enum AutoConfigurationState {

    UNKNOWN, STARTED, SUCCESS, FAILED;

    public static AutoConfigurationState fromStateString(String state) {
        if (state == null) {
            return UNKNOWN;
        }
        for (AutoConfigurationState instanceState : AutoConfigurationState.values()) {
            if (instanceState.name().equalsIgnoreCase(state)) {
                return instanceState;
            }
        }
        return UNKNOWN;
    }

    public boolean in(AutoConfigurationState... states) {
        if (states == null || states.length == 0) {
            return false;
        }
        for (AutoConfigurationState state : states) {
            if (state.equals(this)) {
                return true;
            }
        }
        return false;
    }

    public boolean nin(AutoConfigurationState... states) {
        return !in(states);
    }
}
