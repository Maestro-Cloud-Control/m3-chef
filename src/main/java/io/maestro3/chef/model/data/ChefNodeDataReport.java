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

package io.maestro3.chef.model.data;

import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * Contains node attributes for nodes that represent instances for specified zone and project.
 *
 */
public class ChefNodeDataReport {
    @NotNull
    private String chefUri;
    /**
     * The timestamp that report was created at. Should be generated just as simple as getting current system time.
     */
    @NotNull
    private Long timestamp;
    private Collection<ChefNodeData> data;

    @NotNull
    private Boolean success;
    private String errorMessage;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Collection<ChefNodeData> getData() {
        return data;
    }

    public void setData(Collection<ChefNodeData> data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getChefUri() {
        return chefUri;
    }

    public void setChefUri(String chefUri) {
        this.chefUri = chefUri;
    }

}
