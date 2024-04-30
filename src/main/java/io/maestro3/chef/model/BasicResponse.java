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

import org.apache.http.HttpStatus;

public class BasicResponse<T> {

    private final T result;
    private int statusCode = HttpStatus.SC_OK;
    private String message;
    private String redirectUrl;

    public BasicResponse(T result) {
        this.result = result;
    }

    public BasicResponse(int statusCode, String message) {
        this.result = null;
        this.statusCode = statusCode;
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
