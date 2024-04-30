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

package io.maestro3.chef.client.command.search;

import io.maestro3.chef.client.utils.ChefUtils;

/**
 * Represents bean with node attributes (all values are strings).
 *
 */
public class NodeAttributes {
    private String nodeName;
    private String operationSystemType;
    private String timestamp;
    private String backtrace;
    private String state;

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getOperationSystemType() {
        return operationSystemType;
    }

    public void setOperationSystemType(String operationSystemType) {
        this.operationSystemType = operationSystemType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getBacktrace() {
        return backtrace;
    }

    public void setBacktrace(String backtrace) {
        this.backtrace = backtrace;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return ChefUtils.buildString("NodeAttributes{nodeName=", nodeName,
                ", operationSystemType=", operationSystemType,
                ", timestamp=", timestamp,
                "}");
    }
}
