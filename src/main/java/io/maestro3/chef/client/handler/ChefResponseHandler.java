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

package io.maestro3.chef.client.handler;

import io.maestro3.chef.client.http.client.exception.SimpleHttpClientException;
import io.maestro3.chef.client.http.client.handler.ResponseHandler;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

public class ChefResponseHandler implements ResponseHandler {
    private Integer lastResponseCode;
    private String lastResponseDescription;
    private Boolean lastResponseError;

    public Integer getLastResponseCode() {
        return lastResponseCode;
    }

    public String getLastResponseDescription() {
        return lastResponseDescription;
    }

    public Boolean getLastResponseError() {
        return lastResponseError;
    }

    public void process(HttpResponse response) throws SimpleHttpClientException {
        int code = response.getStatusLine().getStatusCode();
        lastResponseCode = code;
        switch (code) {
            case HttpStatus.SC_OK:
                // fall through
            case HttpStatus.SC_CREATED:
                // SC_CREATED is for PUT requests
                updateResponseState("Ok. The request was successful.", false);
                break;
            case HttpStatus.SC_BAD_REQUEST:
                updateResponseState("Bad request. The contents of the request are not formatted correctly.", true);
                return;
            case HttpStatus.SC_UNAUTHORIZED:
                updateResponseState("Unauthorized. The user which made the request is not authorized to perform the action.", true);
                return;
            case HttpStatus.SC_FORBIDDEN:
                updateResponseState("Forbidden. The user which made the request is not authorized to perform the action.", true);
                return;
            case HttpStatus.SC_NOT_FOUND:
                updateResponseState("Not found. The requested object does not exist.", true);
                return;
            case HttpStatus.SC_CONFLICT:
                updateResponseState("Conflict. The object already exists.", true);
                return;
            default:
                updateResponseState("Unexpected response status code. Code = " + code, true);
                return;
        }

        Header contentTypeHeader = response.getFirstHeader("Content-Type");
        if (contentTypeHeader == null || !ChefRequestHandler.CONTENT_TYPE.equalsIgnoreCase(contentTypeHeader.getValue())) {
            updateResponseState("Unexpected response content type. Header = " + contentTypeHeader, true);
        }
    }

    private void updateResponseState(String description, boolean errorOccurred) {
        this.lastResponseDescription = description;
        this.lastResponseError = errorOccurred;
    }
}
