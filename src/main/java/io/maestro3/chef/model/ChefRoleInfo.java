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

import java.util.List;

public class ChefRoleInfo {
    private String roleName;
    private int minCpu;
    private long minMemoryMb;
    private List<String> requiredParameters;

    public ChefRoleInfo() {
    }

    public ChefRoleInfo(String roleName, int minCpu, long minMemoryMb, List<String> requiredParameters) {
        this.roleName = roleName;
        this.minCpu = minCpu;
        this.minMemoryMb = minMemoryMb;
        this.requiredParameters = requiredParameters;
    }

    public List<String> getRequiredParameters() {
        return requiredParameters;
    }

    public void setRequiredParameters(List<String> requiredParameters) {
        this.requiredParameters = requiredParameters;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getMinCpu() {
        return minCpu;
    }

    public void setMinCpu(int minCpu) {
        this.minCpu = minCpu;
    }

    public long getMinMemoryMb() {
        return minMemoryMb;
    }

    public void setMinMemoryMb(long minMemoryMb) {
        this.minMemoryMb = minMemoryMb;
    }
}
