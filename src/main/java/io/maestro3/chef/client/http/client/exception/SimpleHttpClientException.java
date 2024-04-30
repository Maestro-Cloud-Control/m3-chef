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

package io.maestro3.chef.client.http.client.exception;

/**
 * User: Sergii Ovcharenko
 * Date: 7/2/12
 */
public class SimpleHttpClientException extends Exception {

    public SimpleHttpClientException() {
    }

    public SimpleHttpClientException(String message) {
        super(message);
    }

    public SimpleHttpClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public SimpleHttpClientException(Throwable cause) {
        super(cause);
    }

}
