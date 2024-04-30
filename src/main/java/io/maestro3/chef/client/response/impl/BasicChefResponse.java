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

package io.maestro3.chef.client.response.impl;

import io.maestro3.chef.client.response.IChefResponse;

public class BasicChefResponse<R> implements IChefResponse<R> {
    private int hash;
    private int code;
    private String description;
    private boolean errorOccurred;
    private R result;
    private String rawResult;

    @Override
    public int getHash() {
        return hash;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean getErrorOccurred() {
        return errorOccurred;
    }

    @Override
    public R getResult() {
        return result;
    }

    @Override
    public String getRawResult() {
        return rawResult;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

    public void setResult(R result) {
        this.result = result;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setErrorOccurred(boolean errorOccurred) {
        this.errorOccurred = errorOccurred;
    }

    public void setRawResult(String rawResult) {
        this.rawResult = rawResult;
    }

}
